package primitives;

import static primitives.Util.isZero;

/**
 * Axis-Aligned Bounding Box (AABB) class.
 * Represents a 3D rectangular box aligned with the coordinate axes,
 * used in spatial partitioning and collision detection.
 */
public class AABB extends CBR {
    private double x_min = 0;
    private double y_min = 0;
    private double z_min = 0;
    private double x_max = 0;
    private double y_max = 0;
    private double z_max = 0;

    /**
     * Constructs an AABB using minimum and maximum bounds for each axis.
     *
     * @param x_min The minimum x-coordinate.
     * @param y_min The minimum y-coordinate.
     * @param z_min The minimum z-coordinate.
     * @param x_max The maximum x-coordinate.
     * @param y_max The maximum y-coordinate.
     * @param z_max The maximum z-coordinate.
     */
    public AABB(double x_min, double y_min, double z_min, double x_max, double y_max, double z_max) {
        this.x_min = x_min;
        this.y_min = y_min;
        this.z_min = z_min;
        this.x_max = x_max;
        this.y_max = y_max;
        this.z_max = z_max;
    }

    /**
     * @return The minimum x-coordinate of the bounding box.
     */
    public double getX_min() {
        return x_min;
    }

    /**
     * Sets the minimum x-coordinate of the bounding box.
     *
     * @param x_min The new minimum x-coordinate.
     */
    public void setX_min(double x_min) {
        this.x_min = x_min;
    }

    /**
     * @return The minimum y-coordinate of the bounding box.
     */
    public double getY_min() {
        return y_min;
    }

    /**
     * Sets the minimum y-coordinate of the bounding box.
     *
     * @param y_min The new minimum y-coordinate.
     */
    public void setY_min(double y_min) {
        this.y_min = y_min;
    }

    /**
     * @return The minimum z-coordinate of the bounding box.
     */
    public double getZ_min() {
        return z_min;
    }

    /**
     * Sets the minimum z-coordinate of the bounding box.
     *
     * @param z_min The new minimum z-coordinate.
     */
    public void setZ_min(double z_min) {
        this.z_min = z_min;
    }

    /**
     * @return The maximum x-coordinate of the bounding box.
     */
    public double getX_max() {
        return x_max;
    }

    /**
     * Sets the maximum x-coordinate of the bounding box.
     *
     * @param x_max The new maximum x-coordinate.
     */
    public void setX_max(double x_max) {
        this.x_max = x_max;
    }

    /**
     * @return The maximum y-coordinate of the bounding box.
     */
    public double getY_max() {
        return y_max;
    }

    /**
     * Sets the maximum y-coordinate of the bounding box.
     *
     * @param y_max The new maximum y-coordinate.
     */
    public void setY_max(double y_max) {
        this.y_max = y_max;
    }

    /**
     * @return The maximum z-coordinate of the bounding box.
     */
    public double getZ_max() {
        return z_max;
    }

    /**
     * Sets the maximum z-coordinate of the bounding box.
     *
     * @param z_max The new maximum z-coordinate.
     */
    public void setZ_max(double z_max) {
        this.z_max = z_max;
    }

    /**
     * Creates a new AABB that surrounds both this AABB and another given AABB.
     * Useful for combining bounding boxes when constructing a bounding volume hierarchy.
     *
     * @param other The other axis-aligned bounding box to include.
     * @return A new AABB that completely encloses both this and the given AABB.
     * @throws IllegalArgumentException if the provided CBR is not an instance of AABB.
     */
    @Override
    public CBR surround(CBR other) {
        if (!(other instanceof AABB aabb)) {
            throw new IllegalArgumentException("surround: Expected AABB instance");
        }

        return new AABB(
                Math.min(this.x_min, aabb.x_min),
                Math.min(this.y_min, aabb.y_min),
                Math.min(this.z_min, aabb.z_min),
                Math.max(this.x_max, aabb.x_max),
                Math.max(this.y_max, aabb.y_max),
                Math.max(this.z_max, aabb.z_max)
        );
    }

    /**
     * Checks whether the given ray intersects this AABB.
     *
     * @param ray The ray to check for intersection.
     * @return {@code true} if the ray intersects the bounding box; {@code false} otherwise.
     */
    @Override
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
                // Ray is parallel to slab. No hit if origin not within slab.
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

    /**
     * Determines which axis (x, y, or z) has the longest length in this bounding box.
     *
     * @return 0 for x-axis, 1 for y-axis, 2 for z-axis.
     */
    @Override
    public int longestAxis() {
        double xLength = x_max - x_min;
        double yLength = y_max - y_min;
        double zLength = z_max - z_min;

        if (xLength >= yLength && xLength >= zLength) {
            return 0;
        }
        if (yLength >= xLength && yLength >= zLength) {
            return 1;
        }
        return 2;
    }

    /**
     * Calculates the center coordinate of the bounding box along the specified axis.
     *
     * @param axis The axis (0 = x, 1 = y, 2 = z).
     * @return The center coordinate along the specified axis.
     * @throws IllegalArgumentException If an invalid axis is provided.
     */
    @Override
    public double center(int axis) {
        switch (axis) {
            case 0:
                return (x_min + x_max) / 2.0;
            case 1:
                return (y_min + y_max) / 2.0;
            case 2:
                return (z_min + z_max) / 2.0;
            default:
                throw new IllegalArgumentException("Invalid axis: " + axis);
        }
    }

    /**
     * Compares this AABB with another object for equality.
     *
     * @param obj The object to compare with.
     * @return {@code true} if the other object is an AABB and all min/max coordinates are equal (within tolerance); {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AABB other)) return false;

        return isZero(this.x_min - other.x_min)
                && isZero(this.y_min - other.y_min)
                && isZero(this.z_min - other.z_min)
                && isZero(this.x_max - other.x_max)
                && isZero(this.y_max - other.y_max)
                && isZero(this.z_max - other.z_max);
    }
}
