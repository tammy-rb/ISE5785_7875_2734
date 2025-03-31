package geometries;

import primitives.Point;
import primitives.Vector;

/**
 * Represents a sphere in 3D space, defined by its center and radius.
 */
public class Sphere extends RadialGeometry {
    private final Point center;

    /**
     * Constructor for creating a sphere.
     * @param radius The radius of the sphere.
     * @param center The center of the sphere.
     */
    public Sphere(double radius, Point center) {
        super(radius);
        this.center = center;
    }

    @Override
    public Vector getNormal(Point p) {
        Vector v = p.subtract(center);
        return v.normalize();
    }
}