package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;
import java.util.stream.Collectors;

import static primitives.Util.isZero;

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
    protected List<Intersection> calculateIntersectionsHelper(Ray ray) {
        var intersections = plane.findIntersections(ray);
        if (intersections == null) return null;

        Point p = intersections.getFirst();
        Point p0 = vertices.get(0);
        Point p1 = vertices.get(1);
        Point p2 = vertices.get(2);
        if (p.equals(p0) || p.equals(p1) || p.equals(p2))
            return null;

        Vector n1, n2, n3;
        try {
            n1 = p1.subtract(p0).crossProduct(p0.subtract(p));
            n2 = p2.subtract(p1).crossProduct(p1.subtract(p));
            n3 = p0.subtract(p2).crossProduct(p2.subtract(p));
        } catch (IllegalArgumentException e) {
            return null;
        }

        double d1 = n1.dotProduct(n2);
        double d2 = n1.dotProduct(n3);
        if (d1 > 0 && d2 > 0)
            return intersections.stream().map(i -> new Intersection(this, i)).collect(Collectors.toList());
        return null;
    }
}
