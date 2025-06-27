package geometries;

import primitives.AABB;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

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

    /**
     * Finds the intersection point between a ray and the plane.
     * <p>
     * Note: Intersections at the ray's starting point are not counted,
     * and rays parallel to the plane do not intersect.
     *
     * @param ray the ray to intersect with the plane
     * @return a list containing the intersection point, or {@code null} if there is no intersection
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray, double maxDistance) {
        if (p.equals(ray.getHead()))
            return null;

        double nv = alignZero(normal.dotProduct(ray.getDirection()));
        if (isZero(nv))
            return null;

        double t = alignZero(normal.dotProduct(p.subtract(ray.getHead()))) / nv;

        if (t <= 0 || alignZero(t - maxDistance) > 0)
            return null;

        return List.of(new Intersection(this, ray.getPoint(t)));
    }

}