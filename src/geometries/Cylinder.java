package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static primitives.Util.*;

/**
 * Represents a finite cylinder in 3D space.
 * A cylinder is defined by a central axis (a ray), a radius, and a height.
 * It is a special case of a {@link Tube}, but unlike an infinite tube,
 * the cylinder is bounded by two circular caps (top and bottom).
 */
public class Cylinder extends Tube {
    private final double height;

    /**
     * Constructs a finite cylinder.
     *
     * @param radius The radius of the cylinder.
     * @param axis   The central axis of the cylinder (a ray).
     * @param height The finite height of the cylinder from base to top.
     */
    public Cylinder(double radius, Ray axis, double height) {
        super(radius, axis);
        this.height = height;
    }

    @Override
    public Vector getNormal(Point p) {
        Point p0 = axis.getHead();           // Base center of the cylinder
        Vector v = axis.getDirection();      // Direction of the cylinder's axis
        Point p1 = axis.getPoint(height);    // Top center of the cylinder

        // Check if point lies exactly at the base or top center
        if (p.equals(p0)) return v.scale(-1);
        if (p.equals(p1)) return v;

        // Project point onto the axis to determine location along height
        Vector u = p.subtract(p0);
        double t = v.dotProduct(u);

        // If point lies exactly on one of the caps
        if (isZero(t)) return v.scale(-1);         // Bottom cap
        if (isZero(t - height)) return v;          // Top cap

        // Otherwise, the point lies on the curved surface
        Point o = axis.getPoint(t);                // Projection onto axis
        return p.subtract(o).normalize();          // Normal to curved surface
    }

    /**
     * Helper method to find the intersection between a ray and one circular cap of the cylinder.
     *
     * @param ray    The ray to intersect with the cap.
     * @param center The center of the circular cap (bottom or top).
     * @param normal The normal vector of the cap (aligned with cylinder axis).
     * @return The intersection point if it exists and lies within the cap’s radius, otherwise {@code null}.
     */
    private Point intersectBase(Ray ray, Point center, Vector normal, double maxDistance) {
        // Represent the cap as a plane
        Plane basePlane = new Plane(center, normal);

        // Find the intersection point with the plane
        List<Point> intersections = basePlane.findIntersections(ray);

        // If no intersection or the point is outside the cap's radius, return null
        if (intersections == null)
            return null;

        Point intersection = intersections.getFirst();
        return alignZero(intersection.distance(center) - radius) <= 0 &&
                alignZero(ray.getHead().distance(intersection) - maxDistance) <= 0
                ? intersection : null;
    }

    /**
     * Helper method to check if a list contains a point within a small tolerance.
     */
    private boolean containsPoint(List<Point> points, Point p) {
        final double EPS = 1e-10;
        for (Point point : points) {
            if (point.distance(p) < EPS) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Intersection> calculateIntersectionsHelper(Ray ray, double maxDistance) {
        List<Point> intersections = new LinkedList<>();

        // Intersections with the infinite tube surface
        List<Intersection> tube_intersections = super.calculateIntersectionsHelper(ray, maxDistance);
        List<Point> tubeIntersections = null;
        if (tube_intersections != null)
            tubeIntersections = tube_intersections.stream().map(intersection -> intersection.point).toList();

        if (tubeIntersections != null) {
            Vector axisDir = axis.getDirection();
            Point baseCenter = axis.getHead();

            for (Point pt : tubeIntersections) {
                Vector baseToPoint = pt.subtract(baseCenter);
                double projection = alignZero(axisDir.dotProduct(baseToPoint));

                // Keep only points within the cylinder's finite height (excluding caps)
                if (projection >= 0 && projection <= height)
                    intersections.add(pt);
            }
        }

        // Intersections with the bottom cap
        Point bottomCenter = axis.getHead();
        Vector axisDir = axis.getDirection();
        Point bottomIntersection = intersectBase(ray, bottomCenter, axisDir.scale(-1), maxDistance); // bottom cap normal points opposite to axis

        if (bottomIntersection != null && !containsPoint(intersections, bottomIntersection))
            intersections.add(bottomIntersection);

        // Intersections with the top cap
        Point topCenter = axis.getPoint(height);
        Point topIntersection = intersectBase(ray, topCenter, axisDir, maxDistance);

        if (topIntersection != null && !containsPoint(intersections, topIntersection))
            intersections.add(topIntersection);

        // Return null if no intersections found
        if (intersections.isEmpty())
            return null;

        // Ensure correct order: closest point to ray origin first
        if (intersections.size() == 2) {
            Point p0 = ray.getHead();
            if (intersections.get(0).distance(p0) > intersections.get(1).distance(p0)) {
                Point temp = intersections.get(0);
                intersections.set(0, intersections.get(1));
                intersections.set(1, temp);
            }
        }
        return intersections.stream().map(i -> new Intersection(this, i)).collect(Collectors.toList());
    }
}
