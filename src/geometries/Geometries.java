package geometries;

import primitives.Point;
import primitives.Ray;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a collection of intersectable geometries.
 * Implements Intersectable to allow ray intersection queries on all contained geometries.
 */
public class Geometries implements Intersectable {

    private final List<Intersectable> geometries = new LinkedList<>();

    /**
     * Returns the list of geometries.
     */
    public List<Intersectable> getGeometries() {
        return geometries;
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
        for (Intersectable geometry : geometries) {
            this.geometries.add(geometry);
        }
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
        Set<Point> intersections = new HashSet<>();

        for (Intersectable geometry : geometries) {
            List<Point> geoIntersections = geometry.findIntersections(ray);
            System.out.println(geoIntersections+":: "+geometry);
            if (geoIntersections != null) {
                intersections.addAll(geoIntersections);
            }
        }

        return intersections.isEmpty() ? null : new ArrayList<>(intersections);
    }
}
