package geometries;

import lighting.LightSource;
import primitives.*;

import java.util.List;
import java.util.Objects;

/**
 * The {@code Intersectable} interface represents geometric objects
 * that can be intersected by a {@link Ray}.
 * Implementing classes provide the logic to find intersection points
 * between the ray and the geometric shape.
 */
public abstract class Intersectable {

    private AABB boundingBox;

    protected final void createBoundingBox() {
        if (boundingBox == null) {
            boundingBox = createBoundingBoxHelper();
        }
    }

    public AABB getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(AABB boundingBox) {
        this.boundingBox = boundingBox;
    }

    protected abstract AABB createBoundingBoxHelper();

    public static class Intersection {
        public final Geometry geometry;
        public final Point point;
        public final Material material;
        public Vector v;
        public Vector normal;
        public double vNormal;
        public LightSource light;
        public Vector l;
        public double lNormal;

        public Intersection(Geometry geometry, Point point) {
            this.geometry = geometry;
            this.point = point;
            this.material = geometry != null ? geometry.getMaterial() : null;
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
        if (boundingBox != null && isBoundingBoxIntersected(ray)) {
            var list = calculateIntersections(ray);
            return list == null ? null : list.stream().map(intersection -> intersection.point).toList();
        }
        return null;
    }

    /**
     * Returns a list of {@link Intersection} records between the given ray and this geometry,
     * without limiting the distance (i.e., up to infinity).
     *
     * @param ray the ray to intersect
     * @return a list of {@link Intersection} objects, or {@code null} if no intersections were found
     */
    public final List<Intersection> calculateIntersections(Ray ray) {
        return calculateIntersections(ray, Double.POSITIVE_INFINITY);
    }

    /**
     * Returns a list of {@link Intersection} records between the given ray and this geometry,
     * limited to a maximum distance from the ray origin.
     *
     * @param ray         the ray to intersect
     * @param maxDistance the maximum distance from the ray origin to consider
     * @return a list of {@link Intersection} objects within the specified distance,
     * or {@code null} if no intersections were found
     */
    public final List<Intersection> calculateIntersections(Ray ray, double maxDistance) {
        return calculateIntersectionsHelper(ray, maxDistance);
    }

    /**
     * Internal abstract method to be implemented by subclasses that computes
     * the list of intersections between a ray and the geometry, up to a given distance.
     *
     * @param ray         the ray to intersect
     * @param maxDistance the maximum distance from the ray origin to consider
     * @return a list of {@link Intersection} objects, or {@code null} if none are found
     */
    protected abstract List<Intersection> calculateIntersectionsHelper(Ray ray, double maxDistance);

    // check if a ray intersect the AABB box.
    protected boolean isBoundingBoxIntersected(Ray ray){
        return true;
    };
}