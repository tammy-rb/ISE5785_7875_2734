package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.isZero;

/**
 * Triangle class represents a triangle in 3D Cartesian coordinate system.
 * It inherits from Polygon since a triangle is a polygon with 3 vertices.
 */
public class Triangle extends Polygon {
    /**
     * Constructor for Triangle using three points.
     *
     * @param p1 First vertex of the triangle
     * @param p2 Second vertex of the triangle
     * @param p3 Third vertex of the triangle
     */
    public Triangle(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        Point p0 = ray.getHead();
        Vector dir = ray.getDirection();

        Point v0 = vertices.get(0);
        Point v1 = vertices.get(1);
        Point v2 = vertices.get(2);

        if (p0.equals(v0) || p0.equals(v1) || p0.equals(v2)) {
            return null;
        }

        Vector edge1 = v1.subtract(v0);
        Vector edge2 = v2.subtract(v0);
        try{
            dir.crossProduct(edge2);
        }
        catch (IllegalArgumentException e){
            return null;
        }
        Vector h = dir.crossProduct(edge2);
        double a = edge1.dotProduct(h);

        if (isZero(a)) return null; // Ray is parallel to triangle

        double f = 1.0 / a;
        Vector s = p0.subtract(v0);
        double u = f * s.dotProduct(h);
        if (u <= 0 || u >= 1) return null; // Exclude edges/vertices

        try{
            s.crossProduct(edge1);
        }
        catch (IllegalArgumentException e){
            return null;
        }
        Vector q = s.crossProduct(edge1);
        double v = f * dir.dotProduct(q);
        if (v <= 0 || v >= 1 || u + v >= 1) return null; // Exclude edges/vertices

        double t = f * edge2.dotProduct(q);
        if (t <= 0) return null; // Exclude intersections at or behind ray origin

        Point intersection = p0.add(dir.scale(t));
        return List.of(intersection);
    }
}
