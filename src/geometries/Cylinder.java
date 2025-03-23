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
     * @param radius The radius of the cylinder.
     * @param axis The axis along which the cylinder extends.
     * @param height The height of the cylinder.
     */
    public Cylinder(double radius, Ray axis, double height) {
        super(radius, axis);
        this.height = height;
    }

    @Override
    public Vector getNormal(Point p) {
        return null;  // Placeholder, normal calculation not implemented
    }
}