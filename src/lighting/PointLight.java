package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;
import renderer.blackboard.Blackboard;
import renderer.blackboard.CircleBlackboard;
import primitives.Ray;

import java.util.LinkedList;
import java.util.List;

public class PointLight extends Light implements LightSource {

    private static final double DEFAULT_RADIUS = 0.0;

    private final Point position;
    private double radius;
    private double kC = 1;
    private double kL = 0;
    private double kQ = 0;
    protected Blackboard blackboard;

    // Constructor WITH radius
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

    // Constructor WITHOUT radius – uses default
    public PointLight(Color intensity, Point position) {
        this(intensity, position, DEFAULT_RADIUS);
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
        double distance = p.distance(position);
        return intensity.scale(1 / (kC + kL * distance + kQ * distance * distance));
    }

    @Override
    public double getDistance(Point point) {
        return point.distance(position);
    }

    /**
     * Generates a list of shadow rays toward this light source.
     * The number of rays is automatically estimated based on light radius and distance.
     * If the light has no area (point light), returns a single ray.
     *
     * @param p0 the point in the scene
     * @return list of rays
     */
    public List<Ray> generateRays(Point p0) {
        List<Ray> rays = new LinkedList<>();

        // No soft shadows if radius is zero
        if (blackboard == null || radius == 0.0) {
            rays.add(new Ray(p0, position.subtract(p0)));
            return rays;
        }

        // Estimate number of rays based on light angular size
        double distance = p0.distance(position);
        double angle = Math.atan2(radius, distance); // in radians
        int samplesPerAxis = Math.max(1, (int)(angle * 60)); // heuristic scale
        int numRays = samplesPerAxis * samplesPerAxis;

        this.setBlackboardOrientation(p0);

        blackboard
                .setCenter(position)
                .setNumRays(numRays);

        rays = blackboard.constructRays(p0);
        return rays;
    }

    protected void setBlackboardOrientation(Point p0) {
        Vector vTo = position.subtract(p0).normalize();
        // Any arbitrary vector not parallel to vTo (e.g. (0, 1, 0))
        Vector up = Math.abs(vTo.dotProduct(new Vector(0, 1, 0))) < 0.99 ? new Vector(0, 1, 0) : new Vector(0, 0, 1);
        Vector vRight = vTo.crossProduct(up).normalize();
        blackboard.setOrientation(vTo, vRight);
    }
}
