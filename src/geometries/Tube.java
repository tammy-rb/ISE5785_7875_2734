package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Represents a tube, a radial geometry that extends along a ray.
 */
public class Tube extends RadialGeometry {
    protected final Ray axis;

    /**
     * Constructor for creating a tube.
     * @param radius The radius of the tube.
     * @param axis The axis along which the tube extends.
     */
    public Tube(double radius, Ray axis) {
        super(radius);
        this.axis = axis;
    }

    @Override
    public Vector getNormal(Point p) {
        return null;
    }
}
