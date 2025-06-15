package renderer;

import primitives.*;
import primitives.Color;
import primitives.Point;
import scene.Scene;
import renderer.PixelManager.Pixel;

import java.util.LinkedList;
import java.util.MissingResourceException;
import java.util.stream.IntStream;

import static primitives.Util.*;

/**
 * The {@code Camera} class represents a virtual camera for a 3D rendering engine.
 * It allows you to define the camera's position, orientation, view plane size, and distance from the view plane.
 * A {@link Camera} is built using the Builder pattern via the {@link Camera.Builder} class.
 * The camera can be used to construct rays through view plane pixels and render images using a {@link RayTracerBase}.
 * It also provides utility methods for rendering, displaying grids, and saving the rendered image.
 * The class is {@code Cloneable} to allow safe duplication.
 */
public class Camera implements Cloneable {

    private Point p0;              // Camera location
    private Vector vTo;            // "Forward" direction vector
    private Vector vUp;            // "Up" direction vector
    private Vector vRight;         // "Right" direction vector (orthogonal to vTo and vUp)
    private double viewPlaneDistance = 0.0; // Distance to view plane
    private double viewPlaneWidth = 0.0;    // Width of the view plane
    private double viewPlaneHeight = 0.0;   // Height of the view plane
    private Point viewPlaneCenter;         // Center point of the view plane
    private ImageWriter imageWriter;       // Image writer to handle pixel output
    private RayTracerBase rayTracer;       // Ray tracer for color calculations
    private int nX = 1;                    // Number of horizontal pixels
    private int nY = 1;                    // Number of vertical pixels
    private int threadsCount = 0; // -2 auto, -1 range/stream, 0 no threads, 1+ number of threads
    private static final int SPARE_THREADS = 2; // Spare threads if trying to use all the cores
    private double printInterval = 0; // printing progress percentage interval (0 – no printing)
    private PixelManager pixelManager; // pixel manager object


    /**
     * Private constructor for Camera. Use {@link Builder} to construct an instance.
     */
    private Camera() {
    }

    /**
     * Returns a new {@link Builder} for configuring and constructing a Camera.
     *
     * @return A new Builder instance.
     */
    public static Builder getBuilder() {
        return new Builder();
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
        if (!isZero(xj))
            p = p.add(vRight.scale(xj));
        if (!isZero(yi))
            p = p.add(vUp.scale(yi));
        Vector v = p.subtract(p0);
        return new Ray(p0, v);
    }

    /**
     * Renders the image by casting rays through each pixel and coloring it based on the ray trace result.
     *
     * @return This Camera instance (for chaining).
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
     * Draws a grid on the image with the given interval and color.
     *
     * @param interval Interval between grid lines (in pixels).
     * @param color    Color of the grid lines.
     * @return This Camera instance (for chaining).
     */
    public Camera printGrid(int interval, Color color) {
        for (int i = 0; i < imageWriter.nX(); i++)
            for (int j = 0; j < imageWriter.nY(); j++)
                if (i % interval == 0 || j % interval == 0)
                    imageWriter.writePixel(i, j, color);
        return this;
    }

    /**
     * Saves the rendered image to a file with the given name.
     *
     * @param imageName The name of the image file to write.
     * @return This Camera instance (for chaining).
     */
    public Camera writeToImage(String imageName) {
        this.imageWriter.writeToImage(imageName);
        return this;
    }

    /**
     * Casts a ray through the given pixel and writes the resulting color to the image.
     *
     * @param j Pixel column index.
     * @param i Pixel row index.
     */
    private void castRay(int j, int i) {
        Ray ray = constructRay(nX, nY, j, i);
        Color intensity = rayTracer.traceRay(ray);
        imageWriter.writePixel(j, i, intensity);
        pixelManager.pixelDone();
    }

    /**
     * Clones the camera instance.
     *
     * @return A cloned copy of this camera.
     */
    @Override
    public Camera clone() {
        try {
            return (Camera) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning Camera failed", e);
        }
    }

    /**
     * Render image using multi-threading by creating and running raw threads* @return the camera object itself
     */
    private Camera renderImageRawThreads() {
        var threads = new LinkedList<Thread>();
        while (threadsCount-- > 0)
            threads.add(new Thread(() -> {
                Pixel pixel;
                while ((pixel = pixelManager.nextPixel()) != null)
                    castRay(pixel.col(), pixel.row());
            }));
        for (var thread : threads) thread.start();
        try {
            for (var thread : threads) thread.join();
        } catch (InterruptedException ignore) {
        }
        return this;
    }

    /**
     * Render image using multi-threading by creating and running raw threads* @return the camera object itself
     */
    public Camera renderImageStream() {
        IntStream.range(0, nY).parallel() //
                .forEach(i -> IntStream.range(0, nX).parallel() //
                        .forEach(j -> castRay(j, i)));
        return this;
    }

    /**
     * ...
     */
    public Camera renderImage() {
        pixelManager = new PixelManager(nY, nX, printInterval);
        return switch (threadsCount) {
            case 0 -> renderImageNoThreads();
            case -1 -> renderImageStream();
            default -> renderImageRawThreads();
        };
    }

    /**
     * Builder class for constructing {@link Camera} instances.
     */
    public static class Builder {
        private final Camera camera = new Camera();
        private static final String ERROR_MESSAGE = "Missing rendering data";
        private static final String CLASS_NAME = "Camera";

        /**
         * Sets the camera position.
         *
         * @param location Camera position as a {@link Point}.
         * @return This Builder instance (for chaining).
         */
        public Builder setLocation(Point location) {
            if (location == null)
                throw new IllegalArgumentException("Location cannot be null");
            camera.p0 = location;
            return this;
        }

        /**
         * Sets the camera's forward and up vectors.
         *
         * @param vTo Forward direction vector.
         * @param vUp Upward direction vector.
         * @return This Builder instance (for chaining).
         */
        public Builder setDirection(Vector vTo, Vector vUp) {
            if (vTo == null || vUp == null)
                throw new IllegalArgumentException("Direction vectors cannot be null");
            if (!isZero(vTo.dotProduct(vUp)))
                throw new IllegalArgumentException("vTo and vUp must be orthogonal");
            camera.vTo = vTo.normalize();
            camera.vUp = vUp.normalize();
            return this;
        }

        /**
         * Sets the camera's direction using a target point and an up vector.
         *
         * @param pTarget Target point to look at.
         * @param vUp     Upward direction vector.
         * @return This Builder instance (for chaining).
         */
        public Builder setDirection(Point pTarget, Vector vUp) {
            if (pTarget == null || vUp == null)
                throw new IllegalArgumentException("Target point and up vector cannot be null");
            camera.vTo = pTarget.subtract(camera.p0).normalize();
            camera.vRight = camera.vTo.crossProduct(vUp).normalize();
            camera.vUp = camera.vRight.crossProduct(camera.vTo).normalize();
            return this;
        }

        /**
         * Sets the camera's direction using a target point and assumes a default up vector of (0,1,0).
         *
         * @param pTarget Target point to look at.
         * @return This Builder instance (for chaining).
         */
        public Builder setDirection(Point pTarget) {
            if (pTarget == null)
                throw new IllegalArgumentException("Target point cannot be null");
            camera.vUp = Vector.AXIS_Y;
            return setDirection(pTarget, camera.vUp);
        }

        /**
         * Sets the view plane size.
         *
         * @param width  Width of the view plane.
         * @param height Height of the view plane.
         * @return This Builder instance (for chaining).
         */
        public Builder setViewPlaneSize(double width, double height) {
            if (alignZero(width) <= 0 || alignZero(height) <= 0)
                throw new IllegalArgumentException("ViewPlane size must be positive");
            camera.viewPlaneWidth = width;
            camera.viewPlaneHeight = height;
            return this;
        }

        /**
         * Sets the distance from the camera to the view plane.
         *
         * @param distance Distance value.
         * @return This Builder instance (for chaining).
         */
        public Builder setViewPlaneDistance(double distance) {
            if (alignZero(distance) <= 0)
                throw new IllegalArgumentException("ViewPlane distance must be positive");
            camera.viewPlaneDistance = distance;
            return this;
        }

        /**
         * Sets the resolution (number of pixels) of the view plane.
         *
         * @param nX Number of columns.
         * @param nY Number of rows.
         * @return This Builder instance (for chaining).
         */
        public Builder setResolution(int nX, int nY) {
            camera.nX = nX;
            camera.nY = nY;
            return this;
        }

        /**
         * Sets the ray tracer based on the selected {@link RayTracerType}.
         *
         * @param scene         The scene to trace rays in.
         * @param rayTracerType Type of ray tracer.
         * @return This Builder instance (for chaining).
         */
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
            else { // == -2
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

        /**
         * Validates and builds the configured {@link Camera} instance.
         *
         * @return A fully configured and initialized {@link Camera}.
         * @throws MissingResourceException If required fields are not set.
         * @throws IllegalArgumentException If dimensions or distance are invalid.
         */
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
