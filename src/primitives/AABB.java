package primitives;

import static primitives.Util.isZero;

public class AABB {
    private double x_min = 0;
    private double y_min = 0;
    private double z_min = 0;
    private double x_max = 0;
    private double y_max = 0;
    private double z_max = 0;

    public AABB() {}

    public AABB(double x_min, double y_min, double z_min, double x_max, double y_max, double z_max) {
        this.x_min = x_min;
        this.y_min = y_min;
        this.z_min = z_min;
        this.x_max = x_max;
        this.y_max = y_max;
        this.z_max = z_max;
    }

    public double getX_min() {
        return x_min;
    }

    public void setX_min(double x_min) {
        this.x_min = x_min;
    }

    public double getY_min() {
        return y_min;
    }

    public void setY_min(double y_min) {
        this.y_min = y_min;
    }

    public double getZ_min() {
        return z_min;
    }

    public void setZ_min(double z_min) {
        this.z_min = z_min;
    }

    public double getX_max() {
        return x_max;
    }

    public void setX_max(double x_max) {
        this.x_max = x_max;
    }

    public double getY_max() {
        return y_max;
    }

    public void setY_max(double y_max) {
        this.y_max = y_max;
    }

    public double getZ_max() {
        return z_max;
    }

    public void setZ_max(double z_max) {
        this.z_max = z_max;
    }

    /**
     * Creates a new AABB that surrounds both this AABB and another given AABB.
     * Useful for combining bounding boxes when constructing a bounding volume hierarchy.
     *
     * @param other The other axis-aligned bounding box to include.
     * @return A new AABB that completely encloses both this and the given AABB.
     */
    public AABB surround(AABB other) {
        if (other == null) return this;

        return new AABB(
                Math.min(this.x_min, other.x_min),
                Math.min(this.y_min, other.y_min),
                Math.min(this.z_min, other.z_min),
                Math.max(this.x_max, other.x_max),
                Math.max(this.y_max, other.y_max),
                Math.max(this.z_max, other.z_max)
        );
    }

    /**
     * Checks whether the given ray intersects this AABB.
     *
     * @param ray The ray to check for intersection.
     * @return true if the ray intersects the bounding box, false otherwise.
     */
    public boolean intersects(Ray ray) {
        Double3 originXYZ = ray.getHead().get_xyz();
        Double3 dirXYZ = ray.getDirection().get_xyz();

        double tMin = Double.NEGATIVE_INFINITY;
        double tMax = Double.POSITIVE_INFINITY;

        double[] originArr = {originXYZ.d1(), originXYZ.d2(), originXYZ.d3()};
        double[] dirArr = {dirXYZ.d1(), dirXYZ.d2(), dirXYZ.d3()};
        double[] minArr = {x_min, y_min, z_min};
        double[] maxArr = {x_max, y_max, z_max};

        for (int i = 0; i < 3; i++) {
            if (isZero(dirArr[i])) {
                // Ray is parallel to slab. No hit if origin not within slab
                if (originArr[i] < minArr[i] || originArr[i] > maxArr[i]) {
                    return false;
                }
            } else {
                double t1 = (minArr[i] - originArr[i]) / dirArr[i];
                double t2 = (maxArr[i] - originArr[i]) / dirArr[i];

                double tEntry = Math.min(t1, t2);
                double tExit = Math.max(t1, t2);

                tMin = Math.max(tMin, tEntry);
                tMax = Math.min(tMax, tExit);

                if (tMin > tMax) {
                    return false;
                }
            }
        }

        return true;
    }

}
