package geometries;

import primitives.Point;

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
}
