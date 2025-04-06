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

    /**
     * Finds intersection points between the sphere and a given ray.
     *
     * The method calculates the points where a ray intersects the sphere.
     * Points that lie exactly at the ray's origin are not counted as intersections.
     * also launch points are not counted as intersections
     *
     * @param ray The ray to test for intersection with the sphere.
     * @return A list of intersection points, or {@code null} if there are none.
     */
    @Override
    public List<Point> findIntersections(Ray ray) {
        // Special case: the ray starts at the center of the sphere
        if (center.equals(ray.getHead())) {
            return List.of(ray.getHead().add(ray.getDirection().scale(radius)));
        }

        Vector u = center.subtract(ray.getHead());
        double tm = ray.getDirection().dotProduct(u);
        double dSquared = u.lengthSquared() - tm * tm;
        double radiusSquared = radius * radius;

        // No intersection if the perpendicular distance from the center to the ray is greater than the radius
        if (dSquared >= radiusSquared) {
            return null;
        }

        double th = Math.sqrt(radiusSquared - dSquared);
        double t1 = tm - th;
        double t2 = tm + th;

        // Calculate potential intersection points (if they are in front of the ray)
        Point p1 = t1 > 0 ? ray.getHead().add(ray.getDirection().scale(t1)) : null;
        Point p2 = t2 > 0 ? ray.getHead().add(ray.getDirection().scale(t2)) : null;

        // Return the valid intersections (ignoring those behind the ray's origin or at its head)
        if (p1 != null && p2 != null) {
            return t1 < t2 ? List.of(p1, p2) : List.of(p2, p1);
        }
        if (p1 != null) {
            return List.of(p1);
        }
        if (p2 != null) {
            return List.of(p2);
        }

        return null;
    }
}