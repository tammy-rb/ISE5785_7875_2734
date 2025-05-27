package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

import static java.lang.Math.max;
import static primitives.Util.alignZero;

public class SpotLight extends PointLight{
    private final Vector direction;

    public SpotLight(Color intensity, Point position, Vector direction) {
        super(intensity, position);
        this.direction = direction.normalize();
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
        return super.getIntensity(p).scale(max(0, alignZero(direction.dotProduct(getL(p)))));
    }

}
