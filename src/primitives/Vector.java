package primitives;

import static primitives.Util.alignZero;

/**
 * Represents a vector in 3D space, extending the {@link Point} class.
 * A vector is defined by its x, y, and z components as their distance
 * from the origin
 */
public class Vector extends Point {

    /** X-axis unit vector (1, 0, 0) */
    public static final Vector AXIS_X = new Vector(1, 0, 0);

    /** Y-axis unit vector (0, 1, 0) */
    public static final Vector AXIS_Y = new Vector(0, 1, 0);

    /** Z-axis unit vector (0, 0, 1) */
    public static final Vector AXIS_Z = new Vector(0, 0, 1);

    /**
     * Constructs a vector using individual x, y, and z coordinates.
     * Throws an exception if the vector is a zero vector.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @param z The z-coordinate.
     * @throws IllegalArgumentException if the vector is a zero vector.
     */
    public Vector(double x, double y, double z) {
        super(x, y, z);
        if (this.xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("Vector is zero");
        }
    }

    /**
     * Constructs a vector using a {@link Double3} object representing coordinates.
     * Throws an exception if the vector is a zero vector.
     *
     * @param xyz A {@link Double3} object containing x, y, and z coordinates.
     * @throws IllegalArgumentException if the vector is a zero vector.
     */
    public Vector(Double3 xyz) {
        super(xyz);
        if (this.xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("Vector is zero");
        }
    }

    /**
     * Adds another vector to this vector.
     *
     * @param v The vector to add.
     * @return A new vector representing the sum of both vectors.
     */
    public Vector add(Vector v) {
        return new Vector(this.xyz.add(v.xyz));
    }

    /**
     * Scales this vector by a scalar value.
     *
     * @param scalar The scalar value to multiply the vector by.
     * @return A new vector representing the scaled vector.
     */
    public Vector scale(double scalar) {
        return new Vector(this.xyz.scale(scalar));
    }

    /**
     * Computes the dot product of this vector with another vector.
     *
     * @param v The vector to compute the dot product with.
     * @return The dot product as a double.
     */
    public Double dotProduct(Vector v) {
        return this.xyz.d1() * v.xyz.d1() + this.xyz.d2() * v.xyz.d2() + this.xyz.d3() * v.xyz.d3();
    }

    /**
     * Computes the cross product of this vector with another vector.
     *
     * @param v The vector to compute the cross product with.
     * @return A new vector representing the cross product result.
     */
    public Vector crossProduct(Vector v) {
        double x = this.xyz.d2() * v.xyz.d3() - this.xyz.d3() * v.xyz.d2();
        double y = this.xyz.d3() * v.xyz.d1() - this.xyz.d1() * v.xyz.d3();
        double z = this.xyz.d1() * v.xyz.d2() - this.xyz.d2() * v.xyz.d1();
        return new Vector(x, y, z);
    }

    /**
     * Calculates the squared length (magnitude) of the vector.
     *
     * @return The squared length of the vector.
     */
    public double lengthSquared() {
        return alignZero(this.dotProduct(this));
    }

    /**
     * Calculates the length (magnitude) of the vector.
     *
     * @return The length of the vector.
     */
    public double length() {
        return Math.sqrt(this.lengthSquared());
    }

    /**
     * Normalizes this vector to have a magnitude of 1.
     *
     * @return A new vector representing the normalized vector.
     */
    public Vector normalize() {
        return this.scale(1 / this.length());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return (obj instanceof Vector other) && super.equals(other);
    }

    @Override
    public String toString() {
        return "->" + super.toString();
    }
}
