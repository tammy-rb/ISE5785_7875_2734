package renderer.blackboard;

import primitives.Point;
import primitives.Vector;
import primitives.Ray;

import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;

import static primitives.Util.isZero;
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