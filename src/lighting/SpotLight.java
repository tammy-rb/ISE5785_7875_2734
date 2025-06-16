package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.pow;
import static primitives.Util.alignZero;

/**
 * The {@code SpotLight} class represents a light source that behaves like a spotlight.
 * It emits light in a specific direction from a given position, and the intensity
 * falls off according to the angle between the direction to the point and the spotlight's direction.
 * <p>
 * This class extends {@link PointLight}, adding the concept of directionality and narrow beam control.
 * The beam narrowing is controlled via a power exponent that affects the dot product-based intensity falloff.
 * <p>
 * Supports optional soft shadow simulation using a blackboard-based sampling approach when a radius is provided.
 */
public class SpotLight extends PointLight{
    private final Vector direction;
    private double narrowBeam = 1; // default: no narrowing

    public SpotLight(Color intensity, Point position, Vector direction) {
        super(intensity, position);
        this.direction = direction.normalize();
    }

    public SpotLight(Color intensity, Point position, Vector direction, double radius) {
        super(intensity, position, radius);
        this.direction = direction.normalize();
    }

    public SpotLight setNarrowBeam(double narrowBeam) {
        this.narrowBeam = narrowBeam;
        return this;
    }

    @Override
    public SpotLight setKC(double kC) {
        return (SpotLight)super.setKC(kC);
    }

    @Override
    public SpotLight setKL(double kL) {
        return (SpotLight)super.setKL(kL);
    }

    @Override
    public SpotLight setKQ(double kQ) {
        return (SpotLight)super.setKQ(kQ);
    }

    @Override
    public Color getIntensity(Point p) {
        return super.getIntensity(p).scale(
                max(0, pow(alignZero(direction.dotProduct(getL(p))), narrowBeam))
        );
    }
    @Override
    public List<Ray> generateRays(Point p0) {
        return super.generateRays(p0);
    }

    @Override
    protected void setBlackboardOrientation(Point p0) {
        Vector vTo = direction.normalize();

        // Step 1: Choose arbitrary non-parallel vector
        Vector arbitrary = Math.abs(vTo.dotProduct(new Vector(0, 1, 0))) < 0.99
                ? new Vector(0, 1, 0)
                : new Vector(1, 0, 0);

        // Step 2: Compute orthogonal basis
        Vector vRight = vTo.crossProduct(arbitrary).normalize();
        Vector vUp = vRight.crossProduct(vTo).normalize(); // guaranteed orthogonal to vTo and vRight

        blackboard.setOrientation(vTo, vRight); // assuming setOrientation(vTo, vRight) calculates vUp internally
    }
}
