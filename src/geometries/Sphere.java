package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

/**
 * Represents a sphere in 3D space, defined by its center and radius.
 */
public class Sphere extends RadialGeometry {
    private final Point center;

    /**
     * Constructor for creating a sphere.
     *
     * @param radius The radius of the sphere.
     * @param center The center of the sphere.
     */
    public Sphere(double radius, Point center) {
        super(radius);
        this.center = center;
    }

    @Override
    public Vector getNormal(Point p) {
        return p.subtract(center).normalize();
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        return null;
    }
}