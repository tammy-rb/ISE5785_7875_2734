package renderer;

import primitives.*;
import primitives.Color;
import primitives.Point;
import scene.Scene;
import renderer.PixelManager.Pixel;
import renderer.blackboard.Blackboard;
import renderer.blackboard.RectangleBlackboard;

import java.util.LinkedList;
import java.util.MissingResourceException;
import java.util.List;
import java.util.stream.IntStream;

import static primitives.Util.*;

/**
 * The {@code Camera} class represents a virtual camera in a 3D scene used for ray tracing.
 * It is responsible for generating rays through each pixel of the view plane,
 * tracing their color using a ray tracer, and writing the resulting image.
 * <p>
 * The camera supports multithreading, super sampling (via blackboards), and configurable resolution and orientation.
 */
public class Camera implements Cloneable {

    private Point p0;
    private Vector vTo;
    private Vector vUp;
    private Vector vRight;
    private double viewPlaneDistance = 0.0;
    private double viewPlaneWidth = 0.0;
    private double viewPlaneHeight = 0.0;
    private Point viewPlaneCenter;
    private ImageWriter imageWriter;
    private RayTracerBase rayTracer;
    private int nX = 1;
    private int nY = 1;
    private int threadsCount = 0;
    private static final int SPARE_THREADS = 2;
    private double printInterval = 0;
    private PixelManager pixelManager;
    private int numRays = 1;

    private Camera() {
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    private Point constructPixelCenter(int nX, int nY, int j, int i) {
        double rY = viewPlaneHeight / nY;
        double rX = viewPlaneWidth / nX;
        double yi = -(i - (double) (nY - 1) / 2) * rY;
        double xj = (j - (double) (nX - 1) / 2) * rX;
        Point p = viewPlaneCenter;
        if (!isZero(xj)) p = p.add(vRight.scale(xj));
        if (!isZero(yi)) p = p.add(vUp.scale(yi));
        return p;
    }

    /**
     * Constructs a ray through a specific pixel on the view plane.
     *
     * @param nX Number of columns (pixels in width).
     * @param nY Number of rows (pixels in height).
     * @param j  Pixel column index (0-based).
     * @param i  Pixel row index (0-based).
     * @return A {@link Ray} from the camera through the specified pixel.
     */
    public Ray constructRay(int nX, int nY, int j, int i) {
        double rY = viewPlaneHeight / nY;
        double rX = viewPlaneWidth / nX;
        double yi = -(i - (double) (nY - 1) / 2) * rY;
        double xj = (j - (double) (nX - 1) / 2) * rX;
        Point p = viewPlaneCenter;
        if (!isZero(xj)) p = p.add(vRight.scale(xj));
        if (!isZero(yi)) p = p.add(vUp.scale(yi));
        Vector v = p.subtract(p0);
        return new Ray(p0, v);
    }

    /**
     * Renders the image without using multithreading.
     *
     * @return This camera instance.
     */
    public Camera renderImageNoThreads() {
        for (int j = 0; j < nX; j++) {
            for (int i = 0; i < nY; i++)
                castRay(j, i);
            System.out.println(j);
        }
        return this;
    }

    /**
     * Draws a grid on the rendered image with the specified interval and color.
     *
     * @param interval Distance between grid lines in pixels.
     * @param color    Color of the grid lines.
     * @return This camera instance.
     */
    public Camera printGrid(int interval, Color color) {
        for (int i = 0; i < imageWriter.nX(); i++)
            for (int j = 0; j < imageWriter.nY(); j++)
                if (i % interval == 0 || j % interval == 0)
                    imageWriter.writePixel(i, j, color);
        return this;
    }

    /**
     * Writes the final rendered image to disk with the specified name.
     *
     * @param imageName Name of the output file.
     * @return This camera instance.
     */
    public Camera writeToImage(String imageName) {
        this.imageWriter.writeToImage(imageName);
        return this;
    }

    private void castRay(int j, int i) {
        Point pixelCenter = constructPixelCenter(nX, nY, j, i);

        if (numRays == 1 ) {
            Ray ray = new Ray(p0, pixelCenter.subtract(p0));
            Color intensity = rayTracer.traceRay(ray);
            imageWriter.writePixel(j, i, intensity);
        } else {
            // Create a fresh blackboard instance for thread safety
            Blackboard localBoard = new RectangleBlackboard()
                    .setCenter(pixelCenter)
                    .setOrientation(vTo, vRight)
                    .setWidthHeight(viewPlaneWidth / nX, viewPlaneHeight / nY)
                    .setNumRays(numRays);

            List<Ray> rays = localBoard.constructRays(p0);
            Color color = Color.BLACK;
            for (Ray ray : rays) {
                color = color.add(rayTracer.traceRay(ray));
            }
            imageWriter.writePixel(j, i, color.reduce(rays.size()));
        }

        if (pixelManager != null) pixelManager.pixelDone();
    }


    @Override
    public Camera clone() {
        try {
            return (Camera) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning Camera failed", e);
        }
    }


    private Camera renderImageRawThreads() {
        var threads = new LinkedList<Thread>();
        while (threadsCount-- > 0)
            threads.add(new Thread(() -> {
                Pixel pixel;
                while ((pixel = pixelManager.nextPixel()) != null)
                    castRay(pixel.col(), pixel.row());
            }));
        for (var thread : threads)
            thread.start();
        try {
            for (var thread : threads) thread.join();
        } catch (InterruptedException ignore) {
        }
        return this;
    }

    /**
     * Renders the image using a parallel stream-based approach.
     *
     * @return This camera instance.
     */
    public Camera renderImageStream() {
        IntStream.range(0, nY).parallel()
                .forEach(i -> IntStream.range(0, nX).parallel()
                        .forEach(j -> castRay(j, i)));
        return this;
    }

    /**
     * Renders the image based on the selected multithreading configuration.
     * <ul>
     *     <li>threadsCount = 0 → single-threaded</li>
     *     <li>threadsCount = -1 → parallel streams</li>
     *     <li>threadsCount > 0 → manual multithreading</li>
     * </ul>
     *
     * @return This camera instance.
     */
    public Camera renderImage() {
        pixelManager = new PixelManager(nY, nX, printInterval);
        return switch (threadsCount) {
            case 0 -> renderImageNoThreads();
            case -1 -> renderImageStream();
            default -> renderImageRawThreads();
        };
    }

    public static class Builder {
        private final Camera camera = new Camera();
        private static final String ERROR_MESSAGE = "Missing rendering data";
        private static final String CLASS_NAME = "Camera";

        public Builder setLocation(Point location) {
            if (location == null)
                throw new IllegalArgumentException("Location cannot be null");
            camera.p0 = location;
            return this;
        }

        public Builder setDirection(Vector vTo, Vector vUp) {
            if (vTo == null || vUp == null)
                throw new IllegalArgumentException("Direction vectors cannot be null");
            if (!isZero(vTo.dotProduct(vUp)))
                throw new IllegalArgumentException("vTo and vUp must be orthogonal");
            camera.vTo = vTo.normalize();
            camera.vUp = vUp.normalize();
            return this;
        }

        public Builder setDirection(Point pTarget, Vector vUp) {
            if (pTarget == null || vUp == null)
                throw new IllegalArgumentException("Target point and up vector cannot be null");
            camera.vTo = pTarget.subtract(camera.p0).normalize();
            camera.vRight = camera.vTo.crossProduct(vUp).normalize();
            camera.vUp = camera.vRight.crossProduct(camera.vTo).normalize();
            return this;
        }

        public Builder setDirection(Point pTarget) {
            if (pTarget == null)
                throw new IllegalArgumentException("Target point cannot be null");
            camera.vUp = Vector.AXIS_Y;
            return setDirection(pTarget, camera.vUp);
        }

        public Builder setViewPlaneSize(double width, double height) {
            if (alignZero(width) <= 0 || alignZero(height) <= 0)
                throw new IllegalArgumentException("ViewPlane size must be positive");
            camera.viewPlaneWidth = width;
            camera.viewPlaneHeight = height;
            return this;
        }

        public Builder setViewPlaneDistance(double distance) {
            if (alignZero(distance) <= 0)
                throw new IllegalArgumentException("ViewPlane distance must be positive");
            camera.viewPlaneDistance = distance;
            return this;
        }

        public Builder setResolution(int nX, int nY) {
            camera.nX = nX;
            camera.nY = nY;
            return this;
        }

        public Builder setRayTracer(Scene scene, RayTracerType rayTracerType) {
            if (rayTracerType == RayTracerType.SIMPLE)
                camera.rayTracer = new SimpleRayTracer(scene);
            else
                camera.rayTracer = null;
            return this;
        }

        public Builder setMultithreading(int threads) {
            if (threads < -2) throw new IllegalArgumentException("Multithreading must be -2 or higher");
            if (threads >= -1) camera.threadsCount = threads;
            else {
                int cores = Runtime.getRuntime().availableProcessors() - SPARE_THREADS;
                camera.threadsCount = cores <= 2 ? 1 : cores;
            }
            return this;
        }

        public Builder setDebugPrint(double interval) {
            if (interval < 0)
                throw new IllegalArgumentException("Interval value must be non-negative");
            camera.printInterval = interval;
            return this;
        }

        public Builder setNumRays(int numRays) {
            camera.numRays = numRays;
            return this;
        }

        public Camera build() {
            if (camera.p0 == null)
                throw new MissingResourceException(ERROR_MESSAGE, CLASS_NAME, "p0");
            if (camera.vUp == null)
                throw new MissingResourceException(ERROR_MESSAGE, CLASS_NAME, "vUp");
            if (camera.vTo == null)
                throw new MissingResourceException(ERROR_MESSAGE, CLASS_NAME, "vTo");
            if (alignZero(camera.viewPlaneWidth) <= 0)
                throw new IllegalArgumentException("ViewPlane width must be positive");
            if (alignZero(camera.viewPlaneHeight) <= 0)
                throw new IllegalArgumentException("ViewPlane height must be positive");
            if (alignZero(camera.viewPlaneDistance) <= 0)
                throw new IllegalArgumentException("ViewPlane distance must be positive");
            if (alignZero(camera.nX) <= 0 || alignZero(camera.nY) <= 0)
                throw new IllegalArgumentException("NX and NY must be positive");

            if (camera.vRight == null)
                camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();
            if (camera.rayTracer == null)
                camera.rayTracer = new SimpleRayTracer(null);

            camera.viewPlaneCenter = camera.p0.add(camera.vTo.scale(camera.viewPlaneDistance));
            camera.imageWriter = new ImageWriter(camera.nX, camera.nY);

            return (Camera) camera.clone();
        }
    }
}
