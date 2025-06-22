package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;
import renderer.blackboard.Blackboard;
import renderer.blackboard.CircleBlackboard;
import primitives.Ray;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a point light source with optional radius for area lighting.
 * Supports attenuation based on distance and soft shadows via multiple rays.
 */
public class PointLight extends Light implements LightSource {

    private static final double DEFAULT_RADIUS = 0.0;

    private final Point position;
    private double radius;
    private double kC = 1;
    private double kL = 0;
    private double kQ = 0;
    protected Blackboard blackboard;

    /**
     * Constructs a PointLight with a given intensity, position, and radius.
     * If radius > 0, enables area lighting and soft shadows.
     *
     * @param intensity the color intensity of the light
     * @param position  the position of the light source
     * @param radius    the radius of the light's emitting area
     */
    public PointLight(Color intensity, Point position, double radius) {
        super(intensity);
        this.position = position;
        this.radius = radius;
        if (this.radius > 0) {
            this.blackboard = new CircleBlackboard()
                    .setCenter(position)
                    .setRadius(this.radius);
        }
    }

    /**
     * Constructs a pure point light (no soft shadows).
     *
     * @param intensity the color intensity of the light
     * @param position  the position of the light source
     */
    public PointLight(Color intensity, Point position) {
        this(intensity, position, DEFAULT_RADIUS);
    }

    /**
     * Sets the constant attenuation factor.
     *
     * @param kC the constant coefficient
     * @return this light instance (for chaining)
     */
    public PointLight setKC(double kC) {
        this.kC = kC;
        return this;
    }

    /**
     * Sets the linear attenuation factor.
     *
     * @param kL the linear coefficient
     * @return this light instance (for chaining)
     */
    public PointLight setKL(double kL) {
        this.kL = kL;
        return this;
    }

    /**
     * Sets the quadratic attenuation factor.
     *
     * @param kQ the quadratic coefficient
     * @return this light instance (for chaining)
     */
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
        double distance = p.distance(position);
        return intensity.scale(1 / (kC + kL * distance + kQ * distance * distance));
    }

    @Override
    public double getDistance(Point point) {
        return point.distance(position);
    }

    /**
     * Generates a list of rays from the given point toward this light source.
     * If radius > 0, returns multiple rays for soft shadows. Otherwise, returns a single ray.
     *
     * @param p0 the origin point for the rays
     * @return list of shadow rays toward the light
     */
    public List<Ray> generateRays(Point p0) {
        List<Ray> rays = new LinkedList<>();

        if (blackboard == null || radius == 0.0) {
            rays.add(new Ray(p0, position.subtract(p0)));
            return rays;
        }

        double distance = p0.distance(position);
        double angle = Math.atan2(radius, distance);
        int samplesPerAxis = Math.max(4, (int) (angle * 60)); // heuristic
        int numRays = samplesPerAxis * samplesPerAxis;

        this.setBlackboardOrientation(p0);

        blackboard
                .setCenter(position)
                .setNumRays(numRays);

        return blackboard.constructRays(p0);
    }

    /**
     * Sets the orientation of the blackboard used for area light sampling.
     * Calculates orthogonal vectors based on the view direction from the point to the light.
     *
     * @param p0 the point from which the light is viewed
     */
    protected void setBlackboardOrientation(Point p0) {
        Vector vTo = position.subtract(p0).normalize();
        Vector arbitrary = Math.abs(vTo.dotProduct(new Vector(0, 1, 0))) < 0.99
                ? new Vector(0, 1, 0)
                : new Vector(1, 0, 0);

        Vector vRight = vTo.crossProduct(arbitrary).normalize();

        blackboard.setOrientation(vTo, vRight);
    }
}
