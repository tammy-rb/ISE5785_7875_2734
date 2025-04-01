package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

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
        Point p1 = p0.add(v.scale(height)); // top center base
        // points are the centers of the bases
        if (p.equals(p0))
            return v.scale(-1);
        if (p.equals(p1))
            return v;
        // Project point onto the cylinder's axis
        Vector u = p.subtract(p0);
        double t = v.dotProduct(u);

        // point is on a base or on its edge
        if (t == 0) return v.scale(-1); // Bottom base center, normal is -v
        if (t == height) return v; // Top base center, normal is v

        Point o = p0.add(v.scale(t)); // Projection onto axis
        return p.subtract(o).normalize(); // Normal to surface
    }
}