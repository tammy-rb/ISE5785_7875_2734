package renderer;

import primitives.*;

import java.util.MissingResourceException;

import static primitives.Util.*;

/**
 * The {@code Camera} class represents a virtual camera in a 3D scene.
 * It uses the Builder design pattern to configure various camera parameters,
 * such as position, direction, view plane size, and distance.
 * <p>
 * The class supports creating rays through pixels for rendering and
 * is {@code Cloneable} to allow safe duplication.
 */
public class Camera implements Cloneable {

    private Point p0; // Camera location
    private Vector vTo; // "Forward" vector
    private Vector vUp; // "Up" vector
    private Vector vRight; // "Right" vector
    private double viewPlaneDistance = 0.0;
    private double viewPlaneWidth = 0.0;
    private double viewPlaneHeight = 0.0;
    private Point viewPlaneCenter;

    /**
     * Private constructor. Use the {@link Builder} to create instances.
     */
    private Camera() {
    }

    /**
     * Returns a new {@link Builder} for constructing a {@code Camera}.
     *
     * @return Builder instance
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Constructs a ray through a specific pixel on the view plane.
     *
     * @param nX Number of columns (horizontal resolution)
     * @param nY Number of rows (vertical resolution)
     * @param j  Pixel column index (0-based)
     * @param i  Pixel row index (0-based)
     * @return Ray from camera through the specified pixel
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
     * Creates a clone of the camera.
     *
     * @return Cloned {@code Camera} object
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
     * Builder class for constructing {@link Camera} instances.
     */
    public static class Builder {
        private final Camera camera = new Camera();

        private static final String ERROR_MESSAGE = "Missing rendering data";
        private static final String CLASS_NAME = "Camera";

        /**
         * Default constructor for Builder.
         */
        public Builder() {
        }

        /**
         * Sets the camera location.
         *
         * @param location {@link Point} representing camera position
         * @return Builder instance
         * @throws IllegalArgumentException if location is {@code null}
         */
        public Builder setLocation(Point location) {
            if (location == null)
                throw new IllegalArgumentException("Location cannot be null");
            camera.p0 = location;
            return this;
        }

        /**
         * Sets the camera's direction using forward and up vectors.
         *
         * @param vTo "Forward" vector
         * @param vUp "Up" vector
         * @return Builder instance
         * @throws IllegalArgumentException if vectors are {@code null} or not orthogonal
         */
        public Builder setDirection(Vector vTo, Vector vUp) {
            if (vTo == null || vUp == null)
                throw new IllegalArgumentException("Direction vectors cannot be null");
            if (!isZero(vTo.dotProduct(vUp)))
                throw new IllegalArgumentException("vTo and vUp must be orthogonal (perpendicular)");
            camera.vTo = vTo.normalize();
            camera.vUp = vUp.normalize();
            return this;
        }

        /**
         * Sets the camera direction using a target point and an up vector.
         *
         * @param pTarget Point the camera looks at
         * @param vUp     "Up" vector
         * @return Builder instance
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
         * Sets the camera direction using only a target point.
         * Assumes a default "up" vector of (0,1,0).
         *
         * @param pTarget Point to look at
         * @return Builder instance
         */
        public Builder setDirection(Point pTarget) {
            if (pTarget == null)
                throw new IllegalArgumentException("Target point cannot be null");
            camera.vUp = Vector.AXIS_Y;
            return setDirection(pTarget, camera.vUp);
        }

        /**
         * Sets the size of the view plane.
         *
         * @param width  Width of the view plane
         * @param height Height of the view plane
         * @return Builder instance
         * @throws IllegalArgumentException if dimensions are not positive
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
         * @param distance Distance value
         * @return Builder instance
         * @throws IllegalArgumentException if the distance is not positive
         */
        public Builder setViewPlaneDistance(double distance) {
            if (alignZero(distance) <= 0)
                throw new IllegalArgumentException("ViewPlane distance must be positive");
            camera.viewPlaneDistance = distance;
            return this;
        }

        /**
         * Placeholder for setting the view plane resolution.
         * Currently does not affect camera behavior.
         *
         * @param nX Number of columns
         * @param nY Number of rows
         * @return Builder instance
         */
        public Builder setResolution(int nX, int nY) {
            return this;
        }

        /**
         * Builds and returns a validated {@link Camera} instance.
         *
         * @return A fully-initialized {@code Camera}
         * @throws MissingResourceException if required fields are not initialized
         * @throws IllegalArgumentException if any parameter has an invalid value
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

            if (camera.vRight == null)
                camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();

            camera.viewPlaneCenter = camera.p0.add(camera.vTo.scale(camera.viewPlaneDistance));

            return (Camera) camera.clone();
        }
    }
}
