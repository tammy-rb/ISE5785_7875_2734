package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.isZero;

/**
 * Represents a tube, a radial geometry that extends along a ray.
 */
public class Tube extends RadialGeometry {
    protected final Ray axis;

    /**
     * Constructor for creating a tube.
     *
     * @param radius The radius of the tube.
     * @param axis   The axis along which the tube extends.
     */
    public Tube(double radius, Ray axis) {
        super(radius);
        this.axis = axis;
    }

    @Override
    public Vector getNormal(Point p) {
        Vector u = p.subtract(axis.getHead());
        Vector v = axis.getDirection();
        double t = v.dotProduct(u);
        if (isZero(t)) //p-p0 orthogonal to ray
            return u.normalize();
        Point o = axis.getHead().add(v.scale(t));
        return p.subtract(o).normalize();
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        return null;
    }
}
