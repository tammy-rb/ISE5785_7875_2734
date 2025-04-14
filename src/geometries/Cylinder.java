package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.isZero;

/**
 * Represents a cylinder, which is a special case of a tube with height.
 */
public class Cylinder extends Tube {
    private final double height;

    /**
     * Constructor for creating a cylinder.
     *
     * @param radius The radius of the cylinder.
     * @param axis   The axis along which the cylinder extends.
     * @param height The height of the cylinder.
     */
    public Cylinder(double radius, Ray axis, double height) {
        super(radius, axis);
        this.height = height;
    }

    @Override
    public Vector getNormal(Point p) {
        Point p0 = axis.getHead(); // Base center
        Vector v = axis.getDirection(); // Axis direction
        Point p1 = axis.getPoint(height);
        // points are the centers of the bases
        if (p.equals(p0))
            return v.scale(-1);
        if (p.equals(p1))
            return v;

        // Project point onto the cylinder's axis
        Vector u = p.subtract(p0);
        double t = v.dotProduct(u);

        // point is on a base or on its edge
        if (isZero(t)) return v.scale(-1); // Bottom base center, normal is -v
        if (isZero(t - height)) return v; // Top base center, normal is v

        Point o = axis.getPoint(t); // Projection onto axis
        return p.subtract(o).normalize(); // Normal to surface
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        return super.findIntersections(ray);
    }
}