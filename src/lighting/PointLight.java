package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

public class PointLight extends Light implements LightSource{

    private final Point position;
    private double kC = 1;
    private double kL = 0;
    private double kQ = 0;

    public PointLight(Color intensity, Point position) {
        super(intensity);
        this.position = position;
    }

    public PointLight setKC(double kC) {
        this.kC = kC;
        return this;
    }

    public PointLight setKL(double kL) {
        this.kL = kL;
        return this;
    }

    public PointLight setKQ(double kQ) {
        this.kQ = kQ;
        return this;
    }

    @Override
    public Vector getL(Point p) {
        return p.subtract(position).normalize();
    }

    @Override
    public Color getIntensity(Point p) {
        return intensity.scale(1/(kC + kL * p.distance(position) + kQ * p.distanceSquared(position)));
    }
}
