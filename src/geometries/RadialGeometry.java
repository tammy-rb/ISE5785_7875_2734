package geometries;

public abstract class RadialGeometry extends Geometry {
    protected final double radius;
    public RadialGeometry(double radius) {
        this.radius = radius;
    }
}
