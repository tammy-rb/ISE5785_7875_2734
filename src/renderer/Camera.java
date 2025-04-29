package renderer;

import primitives.*;

import java.util.MissingResourceException;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Camera class represents a virtual camera in a 3D scene, using the Builder design pattern.
 * It defines the camera's location and orientation and provides methods to configure the camera.
 * <p>
 * Implements Cloneable to allow creating copies of the camera.
 */
public class Camera implements Cloneable {

    private Point p0; // Camera location
    private Vector vTo; // "Forward" vector
    private Vector vUp; // "Up" vector
    private Vector vRight; // "Right" vector
    private double viewPlaneDistance = 0.0;
    private double viewPlaneWidth = 0.0;
    private double viewPlaneHeight = 0.0;

    /**
     * Private default constructor for Camera.
     * Only Builder can create instances.
     */
    private Camera() {
    }

    /**
     * Returns a new instance of the Builder class for constructing a Camera.
     *
     * @return Builder instance
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Constructs a ray through a specific pixel in the view plane.
     * Currently not implemented.
     *
     * @param nX Number of pixels in the X axis (columns)
     * @param nY Number of pixels in the Y axis (rows)
     * @param j  Column index of the pixel
     * @param i  Row index of the pixel
     * @return Ray through the pixel (currently returns null)
     */
    public Ray constructRay(int nX, int nY, int j, int i) {
        Point pCenter = p0.add(vTo.scale(viewPlaneDistance));
        double rY = viewPlaneHeight / nY;
        double rX = viewPlaneWidth / nX;
        double yi = -(i - (double) (nY - 1) / 2) * rY;
        double xj = (j - (double) (nX - 1) / 2) * rX;
        Point p = pCenter;
        if (!isZero(xj))
            p = p.add(vRight.scale(xj));
        if (!isZero(yi))
            p = p.add(vUp.scale(yi));
        Vector v = p.subtract(p0);
        return new Ray(p0, v);
    }

    @Override
    public Camera clone() {
        try {
            return (Camera) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning Camera failed", e);
        }
    }

    /**
     * Builder class to construct Camera instances using the Builder design pattern.
     */
    public static class Builder {
        private final Camera camera = new Camera();

        /**
         * Constant for error message when data is missing
         */
        private static final String ERROR_MESSAGE = "Missing rendering data";

        /**
         * Constant for the Camera class name
         */
        private static final String CLASS_NAME = "Camera";

        /**
         * Default constructor for Builder.
         */
        public Builder() {
        }

        /**
         * Sets the location of the camera.
         *
         * @param location Point representing the camera's location
         * @return Builder instance
         */
        public Builder setLocation(Point location) {
            if (location == null)
                throw new IllegalArgumentException("Location cannot be null");
            camera.p0 = location;
            return this;
        }

        /**
         * Sets the camera direction using a forward vector and an up vector.
         *
         * @param vTo Vector pointing forward
         * @param vUp Vector pointing upward
         * @return Builder instance
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
         * @param pTarget Target point the camera should look at
         * @param vUp     Up vector
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
         * Assumes the default up vector is (0,1,0).
         *
         * @param pTarget Target point the camera should look at
         * @return Builder instance
         */
        public Builder setDirection(Point pTarget) {
            if (pTarget == null)
                throw new IllegalArgumentException("Target point cannot be null");
            camera.vUp = new Vector(0, 1, 0);
            camera.vTo = pTarget.subtract(camera.p0).normalize();
            Vector cross = camera.vTo.crossProduct(camera.vUp);
            if (cross.lengthSquared() == 0)
                throw new IllegalArgumentException("Camera looking exactly along up vector; undefined right vector");
            camera.vRight = cross.normalize();
            camera.vUp = camera.vRight.crossProduct(camera.vTo).normalize();
            return this;
        }

        /**
         * Sets the size of the View Plane.
         *
         * @param width  Width of the view plane
         * @param height Height of the view plane
         * @return Builder instance
         */
        public Builder setViewPlaneSize(double width, double height) {
            if (alignZero(width) <= 0 || alignZero(height) <= 0)
                throw new IllegalArgumentException("ViewPlane size must be positive");
            camera.viewPlaneWidth = width;
            camera.viewPlaneHeight = height;
            return this;
        }

        /**
         * Sets the distance from the camera to the View Plane.
         *
         * @param distance Distance to the view plane
         * @return Builder instance
         */
        public Builder setViewPlaneDistance(double distance) {
            if (alignZero(distance) <= 0)
                throw new IllegalArgumentException("ViewPlane distance must be positive");
            camera.viewPlaneDistance = distance;
            return this;
        }

        /**
         * Sets the resolution of the View Plane (currently does nothing).
         *
         * @param nX Number of columns
         * @param nY Number of rows
         * @return Builder instance
         */
        public Builder setResolution(int nX, int nY) {
            return this;
        }

        /**
         * Builds and returns a copy of the Camera.
         * Validates that all necessary parameters are set correctly.
         *
         * @return A constructed and validated Camera
         * @throws MissingResourceException if any critical field is missing
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

            if (camera.vRight == null) {
                camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();
            }
            return (Camera) camera.clone();
        }
    }
}
