package geometries.factory;

import geometries.*;
import primitives.*;
import util.parsers.PrimitiveParsers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static util.parsers.PrimitiveParsers.*;

public class MapGeometryFactory implements GeometryFactory<Map<String, String>> {

    @Override
    public Geometry createGeometry(Map<String, String> data) {
        String type = data.get("type");

        if (type == null) {
            throw new IllegalArgumentException("Missing 'type' in geometry data.");
        }

        Geometry geometry;

        switch (type.toLowerCase()) {
            case "sphere":
                geometry = new Sphere(
                        parsePoint(data.get("center")),
                        parseDouble(data.get("radius"), 0)
                );
                break;

            case "triangle":
                geometry = new Triangle(
                        parsePoint(data.get("p0")),
                        parsePoint(data.get("p1")),
                        parsePoint(data.get("p2"))
                );
                break;

            case "tube":
                geometry = new Tube(
                        parseDouble(data.get("radius"), 0),
                        new Ray(
                                parsePoint(data.get("p0")),
                                parseVector(data.get("direction"))
                        )
                );
                break;

            case "cylinder":
                geometry = new Cylinder(
                        parseDouble(data.get("radius"), 0),
                        new Ray(
                                parsePoint(data.get("p0")),
                                parseVector(data.get("direction"))
                        ),
                        parseDouble(data.get("height"), 0)
                );
                break;

            case "plane":
                geometry = data.containsKey("normal")
                        ? new Plane(
                        parsePoint(data.get("p0")),
                        parseVector(data.get("normal")))
                        : new Plane(
                        parsePoint(data.get("p0")),
                        parsePoint(data.get("p1")),
                        parsePoint(data.get("p2")));
                break;

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
                if (pointList.size() < 3) {
                    throw new IllegalArgumentException("Polygon needs at least 3 points");
                }
                geometry = new Polygon(pointList.toArray(new Point[0]));
                break;

            default:
                throw new IllegalArgumentException("Unknown geometry type: " + type);
        }

        // Set emission if present
        if (data.containsKey("emission")) {
            geometry.setEmission(parseColor(data.get("emission")));
        }

        // Set material properties
        Material material = new Material()
                .setKD(parseDouble(data.get("kD"), 0))
                .setKS(parseDouble(data.get("kS"), 0))
                .setKT(parseDouble(data.get("kT"), 0))
                .setKR(parseDouble(data.get("kR"), 0))
                .setShininess((int) parseDouble(data.get("shininess"), 0));

        geometry.setMaterial(material);

        return geometry;
    }
}
