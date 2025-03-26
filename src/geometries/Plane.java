package geometries;

import primitives.Point;
import primitives.Vector;

/**
 * Represents a plane in 3D space defined by a point and a normal vector.
 */
public class Plane extends Geometry {
    private final Point p;
    private final Vector normal;

    /**
     * Constructor for creating a plane using three points.
     *
     * @param p1 The first point.
     * @param p2 The second point.
     * @param p3 The third point.
     */
    public Plane(Point p1, Point p2, Point p3) {
        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);

        this.p = p1;
        this.normal = v1.crossProduct(v2).normalize();
    }

    /**
     * Constructor for creating a plane using a point and a normal vector.
     *
     * @param p      The point on the plane.
     * @param normal The normal vector to the plane.
     */
    public Plane(Point p, Vector normal) {
        this.p = p;
        this.normal = normal.normalize();
    }

    @Override
    public Vector getNormal(Point p) {
        return normal;
    }

    /**
     * Gets the normal vector of the plane.
     *
     * @return The normal vector.
     */
    public Vector getNormal() {
        return normal;
    }
}