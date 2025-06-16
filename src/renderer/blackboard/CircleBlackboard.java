package renderer.blackboard;

import primitives.Point;
import primitives.Vector;
import primitives.Ray;

import java.util.LinkedList;
import java.util.List;

public class CircleBlackboard extends Blackboard {
    private double radius;

    public CircleBlackboard setRadius(double radius) {
        this.radius = radius;
        super.setWidthHeight(radius*2, radius*2);
        return this;
    }

    @Override
    public CircleBlackboard setOrientation(Vector vTo, Vector vRight) {
        super.setOrientation(vTo, vRight);
        return this;
    }

    @Override
    public CircleBlackboard setCenter(Point center) {
        super.setCenter(center);
        return this;
    }

    @Override
    public CircleBlackboard setNumRays(int numRays) {
        super.setNumRays(numRays);
        return this;
    }

    @Override
    public List<Ray> constructRays(Point p0) {
        if (numRays == 1) {
            List<Ray> rays = new LinkedList<>();
            rays.add(new Ray(p0, center.subtract(p0)));
            return rays;
        }
        int originalNumRays = numRays;
        setNumRays((int)(numRays * 1.3));
        List<Point> points = constructPoints();
        setNumRays(originalNumRays);
        List<Ray> rays = new LinkedList<>();
        for (Point p : points) {
            double dx = p.subtract(center).dotProduct(vRight);
            double dy = p.subtract(center).dotProduct(vUp);
            if (dx * dx + dy * dy <= radius * radius) {
                rays.add(new Ray(p0, p.subtract(p0)));
            }
        }
        return rays;
    }
}
