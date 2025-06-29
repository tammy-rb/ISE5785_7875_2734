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
        if (geometries.size() <= 2) return;

        // Ensure all geometries have bounding boxes
        List<Intersectable> validGeometries = geometries.stream()
                .peek(g -> {
                    if (g.getBoundingBox() == null)
                        g.createCBR();
                })
                .filter(g -> g.getBoundingBox() != null)
                .toList();

        if (validGeometries.size() <= 2) return;

        int bestAxis = -1;
        int bestSplitIndex = -1;
        double bestCost = Double.POSITIVE_INFINITY;

        for (int j = 0; j < 3; j++) {
            int axis = j;
            List<Intersectable> sorted = new ArrayList<>(validGeometries);
            sorted.sort(Comparator.comparingDouble(g -> g.getBoundingBox().center(axis)));

            List<CBR> leftBounds = new ArrayList<>();
            List<CBR> rightBounds = new ArrayList<>(Collections.nCopies(sorted.size(), null));

            // Compute cumulative left bounds
            CBR leftBox = null;
            for (int i = 0; i < sorted.size(); i++) {
                CBR box = sorted.get(i).getBoundingBox();
                leftBox = (leftBox == null) ? box : leftBox.surround(box);
                leftBounds.add(leftBox);
            }

            // Compute cumulative right bounds
            CBR rightBox = null;
            for (int i = sorted.size() - 1; i >= 0; i--) {
                CBR box = sorted.get(i).getBoundingBox();
                rightBox = (rightBox == null) ? box : rightBox.surround(box);
                rightBounds.set(i, rightBox);
            }

            // Evaluate best split
            for (int i = 1; i < sorted.size(); i++) {
                double leftSA = leftBounds.get(i - 1).surfaceArea();
                double rightSA = rightBounds.get(i).surfaceArea();
                int leftCount = i;
                int rightCount = sorted.size() - i;
                double cost = leftSA * leftCount + rightSA * rightCount;

                if (cost < bestCost) {
                    bestCost = cost;
                    bestAxis = axis;
                    bestSplitIndex = i;
                }
            }
        }

        if (bestAxis == -1 || bestSplitIndex == -1) return;

        // Final split on best axis
        List<Intersectable> sorted = new ArrayList<>(validGeometries);
        int axisToSplit = bestAxis;
        sorted.sort(Comparator.comparingDouble(g -> g.getBoundingBox().center(axisToSplit)));

        Geometries left = new Geometries();
        Geometries right = new Geometries();
        left.geometries.addAll(sorted.subList(0, bestSplitIndex));
        right.geometries.addAll(sorted.subList(bestSplitIndex, sorted.size()));

        left.createBVH();
        right.createBVH();

        // Replace this node with BVH children
        geometries.clear();
        geometries.add(left);
        geometries.add(right);

        CBR leftBox = (left.getBoundingBox() != null) ? left.getBoundingBox() : left.createCBR();
        CBR rightBox = (right.getBoundingBox() != null) ? right.getBoundingBox() : right.createCBR();
        if (leftBox != null && rightBox != null) {
            setBoundingBox(leftBox.surround(rightBox));
        }
    }



}
