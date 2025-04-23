package renderer;

import primitives.*;

import static primitives.Util.*;

public class Camera implements Cloneable {
    private Point p0;
    private Vector vTo;
    private Vector vUp;
    private Vector vRight;
    private double distance = 0.0;
    private double width = 0.0;
    private double height = 0.0;

    private Camera() {
    }

//    public Camera(Point p0, Vector vTo, Vector vUp) {
//        this.p0 = p0;
//        this.vTo = vTo.normalize();
//        this.vUp = vUp.normalize();
//
//        vRight = vTo.crossProduct(vUp).normalize();
//    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public Camera setDistance(double distance) {
        this.distance = distance;
        return this;
    }

    public Camera setViewPlaneSize(double width, double height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public Ray constractRay(int nX, int nY, int j, int i) {
        return null;
    }

    @Override
    public Camera clone() {
        try {
            Camera clone = (Camera) super.clone();
            clone.p0 = new Point(p0.getX(), p0.getY(), p0.getZ());
            clone.vTo = new Vector(vTo.getHead());
            clone.vUp = new Vector(vUp.getHead());
            clone.vRight = new Vector(vRight.getHead());
            clone.viewPlanePC = viewPlanePC != null ? new Point(viewPlanePC.getX(), viewPlanePC.getY(), viewPlanePC.getZ()) : null;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public static class Builder {
        private final Camera camera = new Camera();

        public Builder setLocation(Point location) {
            camera.p0 = location;
            return this;
        }

        public Builder setDirection(Vector vTo, Vector vUp) {
            if (!isZero(camera.vTo.dotProduct(camera.vUp)))
                throw new IllegalArgumentException("Vector vTo must be orthogonal to vector vUp");
            camera.vTo = vTo.normalize();
            camera.vUp = vUp.normalize();
            return this;
        }

        public Builder setDirection(Point pTarget, Vector vUp) {
            camera.vTo = pTarget.subtract(camera.p0).normalize();
            camera.vRight = camera.vTo.crossProduct(vUp).normalize();
            camera.vUp = camera.vRight.crossProduct(vUp).normalize();
            return this;
        }

        public Builder setDirection(Point pTarget) {
            camera.vUp = new Vector(0, 1, 0);
            camera.vTo = pTarget.subtract(camera.p0).normalize();
            camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();
            return this;
        }

        public Builder setViewPlaneSize(double width, double height) {
            camera.width = width;
            camera.height = height;
            return this;
        }

        public Builder setViewPlaneDistance(double distance) {
            camera.distance = distance;
            return this;
        }

        public Builder setResolution(int nX, int nY) {
            return this;
        }

        public Camera build() {
            if (camera.imageWriter == null)
                throw new MissingResourceException(ERROR_TITLE, CLASS_NAME, "imageWriter");
            if (camera.rayTracer == null)
                throw new MissingResourceException(ERROR_TITLE, CLASS_NAME, "rayTracer");
            if (camera.p0 == null)
                throw new MissingResourceException(ERROR_TITLE, CLASS_NAME, "p0");
            if (camera.vUp == null)
                throw new MissingResourceException(ERROR_TITLE, CLASS_NAME, "vUp");
            if (camera.vTo == null)
                throw new MissingResourceException(ERROR_TITLE, CLASS_NAME, "vTo");

            if (alignZero(camera.viewPlaneWidth) <= 0)
                throw new IllegalArgumentException("viewPlaneWidth must be positive");
            if (alignZero(camera.viewPlaneHeight) <= 0)
                throw new IllegalArgumentException("viewPlaneHeight must be positive");
            if (alignZero(camera.viewPlaneDistance) <= 0)
                throw new IllegalArgumentException("viewPlaneDistance must be positive");

            if (!isZero(camera.vTo.dotProduct(camera.vUp)))
                throw new IllegalArgumentException("vTo and vUp must be orthogonal");

            camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();
            camera.viewPlanePC = camera.p0.add(camera.vTo.scale(camera.viewPlaneDistance));

            return camera.clone();
        }
    }
}
