package geometries.factory;

import geometries.*;
import primitives.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapGeometryFactory implements GeometryFactory<Map<String, String>> {

    @Override
    public Geometry createGeometry(Map<String, String> data) {
        String type = data.get("type");

        if (type == null) {
            throw new IllegalArgumentException("Missing 'type' in geometry data.");
        }

        switch (type.toLowerCase()) {
            case "sphere":
                return new Sphere(parsePoint(data.get("center")), Double.parseDouble(data.get("radius")));

            case "triangle":
                return new Triangle(parsePoint(data.get("p0")), parsePoint(data.get("p1")), parsePoint(data.get("p2")));

            case "tube":
                return new Tube(Double.parseDouble(data.get("radius")),
                        new Ray(parsePoint(data.get("p0")), parseVector(data.get("direction"))));

            case "cylinder":
                return new Cylinder(Double.parseDouble(data.get("radius")),
                        new Ray(parsePoint(data.get("p0")), parseVector(data.get("direction"))),
                        Double.parseDouble(data.get("height")));

            case "plane":
                return data.containsKey("normal")
                        ? new Plane(parsePoint(data.get("p0")), parseVector(data.get("normal")))
                        : new Plane(parsePoint(data.get("p0")), parsePoint(data.get("p1")), parsePoint(data.get("p2")));

            case "polygon":
                List<Point> pointList = new ArrayList<>();
                int index = 0;
                while (true) {
                    String key = "p" + index;
                    String val = data.get(key);
                    if (val == null) break;
                    pointList.add(parsePoint(val.trim()));
                    index++;
                }
                if (pointList.size() < 3) throw new IllegalArgumentException("Polygon needs at least 3 points");
                return new Polygon(pointList.toArray(new Point[0]));
            default:
                throw new IllegalArgumentException("Unknown geometry type: " + type);
        }
    }

    private Point parsePoint(String s) {
        String[] coords = s.trim().split("\\s+");
        return new Point(
                Double.parseDouble(coords[0]),
                Double.parseDouble(coords[1]),
                Double.parseDouble(coords[2])
        );
    }

    private Vector parseVector(String s) {
        String[] coords = s.trim().split("\\s+");
        return new Vector(
                Double.parseDouble(coords[0]),
                Double.parseDouble(coords[1]),
                Double.parseDouble(coords[2])
        );
    }
}
