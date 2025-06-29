package geometries;

import primitives.CBR;
import primitives.Ray;

import java.util.*;

/**
 * Represents a collection of intersectable geometries.
 * Implements Intersectable to allow ray intersection queries on all contained geometries.
 */
public class Geometries extends Intersectable {

    private final List<Intersectable> geometries = new LinkedList<>();

    /**
     * Returns the length of the geometries' list.
     *
     * @return number of geometries in the list
     */
    public int getGeometriesSize() {
        return geometries.size();
    }

    /**
     * Default constructor for an empty geometries collection with CBR disabled.
     */
    public Geometries() {
    }

    /**
     * Constructs a Geometries object with one or more geometries, with CBR disabled.
     *
     * @param geometries Intersectable geometries to add
     */
    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    /**
     * Adds one or more geometries to the collection.
     * <p>
     * For each added geometry:
     * <ul>
     *     <li>If it does not yet have a bounding box, one is created by calling {@code createBoundingBox()}.</li>
     *     <li>If the geometry has a valid bounding box, it is used to update the overall bounding box
     *         of this {@code Geometries} object using the {@code surround()} method.</li>
     *     <li>The geometry is then added to the internal list.</li>
     * </ul>
     * <p>
     * This method ensures that the {@code Geometries} object's bounding box is always up-to-date
     * with respect to its contents.
     *
     * @param geometries One or more {@link Intersectable} geometries to add
     */
    public void add(Intersectable... geometries) {
        Collections.addAll(this.geometries, geometries);
    }


    /**
     * Finds intersection points between the ray and all geometries.
     * Removes duplicate points.
     *
     * @param ray         The ray to test for intersections
     * @param maxDistance The maximum distance from the ray origin to search for intersections
     * @return A list of unique intersection points, or null if none found
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray, double maxDistance) {
        List<Intersection> intersections = new ArrayList<>();

        for (Intersectable geometry : geometries) {
            List<Intersection> geoIntersections = geometry.calculateIntersections(ray, maxDistance);
            if (geoIntersections != null) {
                intersections.addAll(geoIntersections);
            }
        }

        return intersections.isEmpty() ? null : intersections;
    }

    /**
     * Creates a bounding box that surrounds all geometries in the collection.
     *
     * @return AABB that bounds all internal geometries, or null if empty
     */
    @Override
    protected CBR createBoundingBoxHelper() {
        CBR result = null;

        for (Intersectable geometry : geometries) {
            // Ensure the geometry has a bounding box and store it
            CBR box = (geometry.getBoundingBox() != null)
                    ? geometry.getBoundingBox()
                    : geometry.createCBR();

            // Combine into the global bounding box
            if (box != null) {
                result = (result == null) ? box : result.surround(box);
            }
        }

        if (result != null) {
            setBoundingBox(result);
        }
        return result;
    }

    /**
     * Recursively builds a BVH by splitting geometries along the longest axis.
     * Splits the list in-place into two child nodes and replaces this node’s geometries with them.
     */
    public void createBVH() {
        int size = geometries.size();
        if (size <= 2) {
            // Base case: nothing to subdivide
            return;
        }

        // Calculate this node's bounding box
        CBR rootBox = createCBR();
        int axis = rootBox.longestAxis();

        // Sort geometries in-place by center on longest axis
        geometries.sort(Comparator.comparingDouble(g -> {
            CBR cbr = g.getBoundingBox();
            return cbr != null ? cbr.center(axis) : 0.0;
        }));

        // Split current list into two halves (in-place)
        Geometries left = new Geometries();
        Geometries right = new Geometries();

        // Reuse the same internal list, no extra copies
        for (int i = 0; i < size; i++) {
            if (i < size / 2) {
                left.geometries.add(geometries.get(i));
            } else {
                right.geometries.add(geometries.get(i));
            }
        }

        // Recursively apply BVH to child nodes
        left.createBVH();
        right.createBVH();

        // Replace current list with two BVH children
        geometries.clear();
        geometries.add(left);
        geometries.add(right);
        // Update this node's bounding box
        this.setBoundingBox(left.getBoundingBox().surround(right.getBoundingBox()));
    }
}
