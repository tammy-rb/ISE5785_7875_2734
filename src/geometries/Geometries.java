package geometries;

import primitives.Point;
import primitives.Ray;

import java.util.*;

/**
 * Represents a collection of intersectable geometries.
 * Implements Intersectable to allow ray intersection queries on all contained geometries.
 */
public class Geometries implements Intersectable {

    private final List<Intersectable> geometries = new LinkedList<>();

    /**
     * Returns the length of the geometries' list.
     */
    public int getGeometriesSize() {
        return geometries.size();
    }

    /**
     * Default constructor for an empty geometries collection.
     */
    public Geometries() {
    }

    /**
     * Constructs a Geometries object with one or more geometries.
     *
     * @param geometries Intersectable geometries to add
     */
    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    /**
     * Adds geometries to the internal list.
     *
     * @param geometries geometries to add
     */
    public void add(Intersectable... geometries) {
        Collections.addAll(this.geometries, geometries);
    }

    /**
     * Finds intersection points between the ray and all geometries.
     * Removes duplicate points.
     *
     * @param ray The ray to test for intersections
     * @return A list of unique intersection points, or null if none found
     */
    @Override
    public List<Point> findIntersections(Ray ray) {
        List<Point> intersections = new ArrayList<>();

        for (Intersectable geometry : geometries) {
            List<Point> geoIntersections = geometry.findIntersections(ray);
            System.out.println(geoIntersections + " :: " + geometry);
            if (geoIntersections != null) {
                intersections.addAll(geoIntersections); // Keep all, even duplicates
            }
        }

        return intersections.isEmpty() ? null : intersections;
    }
}
