package renderer.blackboard;

import primitives.Point;
import primitives.Vector;
import primitives.Ray;

import java.util.LinkedList;
import java.util.List;

import static primitives.Util.isZero;

/**
 * The {@code Blackboard} abstract class provides a template for sampling points
 * on a 2D surface (e.g., rectangle or circle) to generate multiple rays from a camera
 * toward a pixel area, enabling effects like soft shadows, depth of field, and anti-aliasing.
 * <p>
 * It defines the orientation, center, size, and number of rays, and includes common logic
 * for generating jittered sample points. Subclasses must implement {@link #constructRays(Point)}
 * to define how rays are generated from a given origin point {@code p0}.
 */
public abstract class Blackboard {
    protected Vector vTo;
    protected Vector vRight;
    protected Vector vUp;
    protected Point center;
    protected int numRays = 1;
    protected double width;
    protected double height;

    public Blackboard setOrientation(Vector vTo, Vector vRight) {
        if (!isZero(vTo.dotProduct(vRight)))
            throw new IllegalArgumentException("vTo must be orthogonal to vRight");
        this.vTo = vTo.normalize();
        this.vRight = vRight.normalize();
        this.vUp = vRight.crossProduct(vTo).normalize();
        return this;
    }

    public Blackboard setCenter(Point center) {
        this.center = center;
        return this;
    }

    public Blackboard setWidthHeight(double width, double height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public Blackboard setNumRays(int numRays) {
        this.numRays = numRays;
        return this;
    }

    /**
     * Constructs jittered sampling points inside a rectangular area.
     * @return list of points in the rectangle area
     */
    protected List<Point> constructPoints() {
        List<Point> points = new LinkedList<>();

        int sqrt = (int) Math.sqrt(numRays);
        for (int i = 0; i < sqrt; i++) {
            for (int j = 0; j < sqrt; j++) {
                double dx = (j + Math.random()) * width / sqrt - width / 2;
                double dy = (i + Math.random()) * height / sqrt - height / 2;
                Point p = center.add(vRight.scale(dx)).add(vUp.scale(dy));
                points.add(p);
            }
        }
        return points;
    }

    public abstract List<Ray> constructRays(Point p0);
}