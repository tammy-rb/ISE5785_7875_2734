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
        Vector vTo = direction.normalize();        // Any arbitrary vector not parallel to vTo (e.g. (0, 1, 0))
        Vector up = Math.abs(vTo.dotProduct(new Vector(0, 1, 0))) < 0.99 ? new Vector(0, 1, 0) : new Vector(0, 0, 1);
        Vector vRight = vTo.crossProduct(up).normalize();
        blackboard.setOrientation(vTo, vRight);
    }
}
