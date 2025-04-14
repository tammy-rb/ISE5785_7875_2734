package geometries;

import primitives.Point;
import primitives.Ray;

import java.util.List;

/**
 * The {@code Intersectable} interface represents geometric objects
 * that can be intersected by a {@link Ray}.
 * Implementing classes provide the logic to find intersection points
 * between the ray and the geometric shape.
 */
public interface Intersectable {

    /**
     * Finds all intersection points between a given {@link Ray}
     * and the implementing geometric object.
     *
     * @param ray the ray to intersect with the geometry
     * @return a list of {@link Point} objects where the ray intersects
     * the geometry, or {@code null} if there are no intersections
     */
    List<Point> findIntersections(Ray ray);
}
