package util.parsers;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Utility class for parsing strings into primitives used in the scene (Color, Point, Vector, etc.)
 */
public class PrimitiveParsers {

    public static Color parseColor(String s) {
        if (s == null) {
            throw new IllegalArgumentException("Color string is null");
        }
        String[] rgb = s.trim().split("\\s+");
        if (rgb.length != 3) {
            throw new IllegalArgumentException("Color must have 3 components: " + s);
        }
        return new Color(
                Integer.parseInt(rgb[0]),
                Integer.parseInt(rgb[1]),
                Integer.parseInt(rgb[2])
        );
    }

    public static Point parsePoint(String s) {
        if (s == null) {
            throw new IllegalArgumentException("Point string is null");
        }
        String[] coords = s.trim().split("\\s+");
        if (coords.length != 3) {
            throw new IllegalArgumentException("Point must have 3 components: " + s);
        }
        return new Point(
                Double.parseDouble(coords[0]),
                Double.parseDouble(coords[1]),
                Double.parseDouble(coords[2])
        );
    }

    public static Vector parseVector(String s) {
        if (s == null) {
            throw new IllegalArgumentException("Vector string is null");
        }
        String[] coords = s.trim().split("\\s+");
        if (coords.length != 3) {
            throw new IllegalArgumentException("Vector must have 3 components: " + s);
        }
        return new Vector(
                Double.parseDouble(coords[0]),
                Double.parseDouble(coords[1]),
                Double.parseDouble(coords[2])
        );
    }

    public static double parseDouble(String s, double defaultValue) {
        return s == null ? defaultValue : Double.parseDouble(s);
    }
}
