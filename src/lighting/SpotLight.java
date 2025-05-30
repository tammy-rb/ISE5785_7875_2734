package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

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

}
