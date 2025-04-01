package geometries;

/**
 * Abstract class representing a radial geometry, which has a radius.
 */
public abstract class RadialGeometry extends Geometry {
    protected final double radius;

    /**
     * Constructor for RadialGeometry.
     *
     * @param radius The radius of the radial geometry.
     */
    public RadialGeometry(double radius) {
        this.radius = radius;
    }
}
