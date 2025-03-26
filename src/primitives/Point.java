package primitives;

/**
 * Represents a point in 3D space using the {@link Double3} class.
 */
public class Point {

    /**
     * The coordinates of the point.
     */
    final protected Double3 xyz;

    /**
     * A constant representing the origin point (0,0,0).
     */
    public static final Point ZERO = new Point(Double3.ZERO);

    /**
     * Constructs a point using individual x, y, and z coordinates.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @param z The z-coordinate.
     */
    public Point(double x, double y, double z) {
        xyz = new Double3(x, y, z);
    }

    /**
     * Constructs a point using a {@link Double3} object representing coordinates.
     *
     * @param xyz A {@link Double3} object containing x, y, and z coordinates.
     */
    public Point(Double3 xyz) {
        this.xyz = xyz;
    }

    /**
     * Subtracts another point from this point to create a vector.
     *
     * @param p The point to subtract.
     * @return A vector representing the difference between the two points.
     */
    public Vector subtract(Point p) {
        return new Vector(xyz.subtract(p.xyz));
    }

    /**
     * Adds a vector to this point to create a new point.
     *
     * @param v1 The vector to add.
     * @return A new point representing the sum of the current point and the vector.
     */
    public Point add(Vector v1) {
        return new Point(xyz.add(v1.xyz));
    }

    /**
     * Calculates the squared distance between this point and another point.
     *
     * @param p The point to calculate the distance to.
     * @return The squared distance between the two points.
     */
    public double distanceSquared(Point p) {
        double x = this.xyz.d1() - p.xyz.d1();
        double y = this.xyz.d2() - p.xyz.d2();
        double z = this.xyz.d3() - p.xyz.d3();
        return (x * x) + (y * y) + (z * z);
    }

    /**
     * Calculates the distance between this point and another point.
     *
     * @param p The point to calculate the distance to.
     * @return The distance between the two points.
     */
    public double distance(Point p) {
        return Math.sqrt(distanceSquared(p));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return (obj instanceof Point other)
                && this.xyz.equals(other.xyz);
    }

    @Override
    public String toString() {
        return "" + xyz;
    }
}
