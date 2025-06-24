package geometries;

import primitives.AABB;
import primitives.Ray;

import java.util.*;

/**
 * Represents a collection of intersectable geometries.
 * Implements Intersectable to allow ray intersection queries on all contained geometries.
 */
public class Geometries extends Intersectable {

    private final List<Intersectable> geometries = new LinkedList<>();
    private final boolean enableCBR;

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
        this(false);
    }

    /**
     * Constructor for an empty geometries collection with configurable CBR.
     *
     * @param enableCBR Whether to enable coarse bounding rectangle optimization
     */
    public Geometries(boolean enableCBR) {
        this.enableCBR = enableCBR;
    }

    /**
     * Constructs a Geometries object with one or more geometries, with CBR disabled.
     *
     * @param geometries Intersectable geometries to add
     */
    public Geometries(Intersectable... geometries) {
        this(false);
        add(geometries);
    }

    /**
     * Constructs a Geometries object with one or more geometries and CBR flag.
     *
     * @param enableCBR  Whether to enable coarse bounding rectangle optimization
     * @param geometries Intersectable geometries to add
     */
    public Geometries(boolean enableCBR, Intersectable... geometries) {
        this.enableCBR = enableCBR;
        add(geometries);
    }

    /**
     * Returns whether CBR optimization is enabled.
     *
     * @return true if CBR is enabled, false otherwise
     */
    public boolean isEnableCBR() {
        return enableCBR;
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
        if (enableCBR) {
            for (Intersectable geometry : geometries) {
                if (geometry.getBoundingBox() == null) {
                    geometry.createBoundingBox();
                }

                if (geometry.getBoundingBox() != null) {
                    if (getBoundingBox() == null) {
                        setBoundingBox(geometry.getBoundingBox());
                    } else {
                        setBoundingBox(getBoundingBox().surround(geometry.getBoundingBox()));
                    }
                }

                this.geometries.add(geometry);
            }
        }
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
    protected AABB createBoundingBoxHelper() {
        if (geometries.isEmpty()) return null;

        AABB result = null;
        for (Intersectable geometry : geometries) {
            AABB box = geometry.getBoundingBox();

            // Only compute bounding box if not already set
            if (box == null) {
                geometry.createBoundingBox();
                box = geometry.getBoundingBox();
            }

            if (box != null) {
                result = (result == null) ? box : result.surround(box);
            }
        }
        return result;
    }
}
