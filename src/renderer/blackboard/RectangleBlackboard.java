package renderer.blackboard;

import primitives.Point;
import primitives.Vector;
import primitives.Ray;

import java.util.LinkedList;
import java.util.List;


public class RectangleBlackboard extends Blackboard {
    @Override
    public List<Ray> constructRays(Point cameraPosition) {
        List<Point> points = constructPoints();
        List<Ray> rays = new LinkedList<>();
        for (Point p : points) {
            rays.add(new Ray(cameraPosition, p.subtract(cameraPosition)));
        }
        return rays;
    }

    @Override
    public RectangleBlackboard setOrientation(Vector vTo, Vector vRight) {
        super.setOrientation(vTo, vRight);
        return this;
    }

    @Override
    public RectangleBlackboard setCenter(Point center) {
        super.setCenter(center);
        return this;
    }

    @Override
    public RectangleBlackboard setWidthHeight(double width, double height) {
        super.setWidthHeight(width, height);
        return this;
    }

    @Override
    public RectangleBlackboard setNumRays(int numRays) {
        super.setNumRays(numRays);
        return this;
    }
}
