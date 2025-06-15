package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

public interface LightSource {
    Color getIntensity(Point p);

    Vector getL(Point p);

    double getDistance(Point point);

    List<Ray> generateRays(Point p0);
}
