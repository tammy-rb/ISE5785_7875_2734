package geometries;

import primitives.Point;
import primitives.Vector;

public class Plane extends Geometry {
    private final Point p;
    private final Vector normal;

    public Plane(Point p1, Point p2, Point p3) {
        this.p = p1;
        this.normal = null;
    }

    public Plane(Point p, Vector normal) {
        this.p = p;
        this.normal = normal.normalize();
    }

    @Override
    public Vector getNormal(Point p) {
        return normal;
    }

    public Vector getNormal() {
        return normal;
    }
}
