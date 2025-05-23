package geometries;

import primitives.Point;
import primitives.Ray;

import java.util.List;
import java.util.Objects;

/**
 * The {@code Intersectable} interface represents geometric objects
 * that can be intersected by a {@link Ray}.
 * Implementing classes provide the logic to find intersection points
 * between the ray and the geometric shape.
 */
public abstract class Intersectable {

    public static class Intersection {
        public final Geometry geometry;
        public final Point point;

        public Intersection(Geometry geometry, Point point) {
            this.geometry = geometry;
            this.point = point;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Intersection)) {
                return false;
            }
            return this.geometry == ((Intersection) obj).geometry && this.point.equals(((Intersection) obj).point);
        }

        @Override
        public int hashCode() {
            return Objects.hash(geometry, point);
        }

        @Override
        public String toString() {
            return "Intersection{" +
                    "geometry=" + geometry +
                    ", point=" + point +
                    '}';
        }

    }

    /**
     * Finds all intersection points between a given {@link Ray}
     * and the implementing geometric object.
     *
     * @param ray the ray to intersect with the geometry
     * @return a list of {@link Point} objects where the ray intersects
     * the geometry, or {@code null} if there are no intersections
     */
    public final List<Point> findIntersections(Ray ray) {
        var list = calculateIntersections(ray);
        return list == null ? null : list.stream().map(intersection -> intersection.point).toList();
    }

    protected List<Intersection> calculateIntersectionHelper(Ray ray) {
        return null;
    }

    public final List<Intersection> calculateIntersections(Ray ray) {
        return calculateIntersectionHelper(ray);
    }
}
