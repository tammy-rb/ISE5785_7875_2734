package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.alignZero;

/**
 * Triangle class represents a triangle in 3D Cartesian coordinate system.
 * It inherits from Polygon since a triangle is a polygon with 3 vertices.
 */
public class Triangle extends Polygon {

    /**
     * Constructs a Triangle with three given vertices.
     *
     * @param p1 First vertex of the triangle
     * @param p2 Second vertex of the triangle
     * @param p3 Third vertex of the triangle
     */
    public Triangle(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }

    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray, double maxDistance) {
        // First find intersection with the plane
        var intersections = plane.findIntersections(ray);
        if (intersections == null) return null;

        Point p = intersections.getFirst();

        // Check distance constraint
        if (alignZero(p.distance(ray.getHead()) - maxDistance) > 0) {
            return null;
        }

        // Use barycentric coordinates for robust inside/outside test
        Point p0 = vertices.get(0);
        Point p1 = vertices.get(1);
        Point p2 = vertices.get(2);
        if (p.equals(p0) || p.equals(p1) || p.equals(p2))
            return null;
        // Calculate triangle edges
        Vector edge1 = p1.subtract(p0);
        Vector edge2 = p2.subtract(p0);
        Vector pointVec = p.subtract(p0);

        try {
            // Calculate barycentric coordinates
            double dot00 = edge2.dotProduct(edge2);
            double dot01 = edge2.dotProduct(edge1);
            double dot02 = edge2.dotProduct(pointVec);
            double dot11 = edge1.dotProduct(edge1);
            double dot12 = edge1.dotProduct(pointVec);

            double invDenom = 1.0 / (dot00 * dot11 - dot01 * dot01);
            double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
            double v = (dot00 * dot12 - dot01 * dot02) * invDenom;

            // Check if point is inside triangle
            // Use small epsilon to exclude edge points and avoid artifacts
            final double EPSILON = 1e-10;
            if (u > EPSILON && v > EPSILON && (u + v) < (1.0 - EPSILON)) {
                return List.of(new Intersection(this, p));
            }

        } catch (Exception e) {
            // Handle degenerate cases
            return null;
        }
        return null;
    }
}
