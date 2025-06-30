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
     * Splits the list in-place into two child nodes and replaces this node's geometries with them.
     * Limited to a maximum recursion depth of 4.
     */
    public void createBVH() {
        createBVH(0);
    }

    /**
     * Recursively builds a BVH by splitting geometries along the longest axis.
     * Splits the list in-place into two child nodes and replaces this node's geometries with them.
     *
     * @param depth Current recursion depth
     */
    private void createBVH(int depth) {
        // Base case: don't subdivide small lists or if we've reached max depth
        if (geometries.size() <= 2 || depth >= 4) {
            return;
        }

        // Flatten any nested Geometries to avoid infinite recursion
        List<Intersectable> flattenedGeometries = new ArrayList<>();
        flattenGeometries(geometries, flattenedGeometries);

        // If after flattening we still have too few geometries, don't build BVH
        if (flattenedGeometries.size() <= 2) {
            geometries.clear();
            geometries.addAll(flattenedGeometries);
            return;
        }

        // Ensure all geometries have bounding boxes
        List<Intersectable> validGeometries = new ArrayList<>();
        for (Intersectable geometry : flattenedGeometries) {
            if (geometry.getBoundingBox() == null) {
                // Only call createCBR on non-Geometries objects to avoid recursion
                if (!(geometry instanceof Geometries)) {
                    geometry.createCBR();
                }
            }
            if (geometry.getBoundingBox() != null) {
                validGeometries.add(geometry);
            }
        }

        if (validGeometries.size() <= 2) {
            geometries.clear();
            geometries.addAll(validGeometries);
            return;
        }

        // Find the best splitting axis and position using SAH (Surface Area Heuristic)
        int bestAxis = -1;
        int bestSplitIndex = -1;
        double bestCost = Double.POSITIVE_INFINITY;

        for (int axis = 0; axis < 3; axis++) {
            // Sort geometries by their center along this axis
            List<Intersectable> sorted = new ArrayList<>(validGeometries);
            final int currentAxis = axis;
            sorted.sort(Comparator.comparingDouble(g -> g.getBoundingBox().center(currentAxis)));

            // Try different split positions
            for (int splitIndex = 1; splitIndex < sorted.size(); splitIndex++) {
                // Calculate bounding boxes for left and right partitions
                CBR leftBox = null;
                for (int i = 0; i < splitIndex; i++) {
                    CBR box = sorted.get(i).getBoundingBox();
                    leftBox = (leftBox == null) ? box : leftBox.surround(box);
                }

                CBR rightBox = null;
                for (int i = splitIndex; i < sorted.size(); i++) {
                    CBR box = sorted.get(i).getBoundingBox();
                    rightBox = (rightBox == null) ? box : rightBox.surround(box);
                }

                if (leftBox != null && rightBox != null) {
                    // Calculate cost using surface area heuristic
                    double leftSA = leftBox.surfaceArea();
                    double rightSA = rightBox.surfaceArea();
                    int leftCount = splitIndex;
                    int rightCount = sorted.size() - splitIndex;
                    double cost = leftSA * leftCount + rightSA * rightCount;

                    if (cost < bestCost) {
                        bestCost = cost;
                        bestAxis = currentAxis;
                        bestSplitIndex = splitIndex;
                    }
                }
            }
        }

        // If we couldn't find a good split, don't subdivide
        if (bestAxis == -1 || bestSplitIndex == -1) {
            geometries.clear();
            geometries.addAll(validGeometries);
            return;
        }

        // Perform the best split
        List<Intersectable> sorted = new ArrayList<>(validGeometries);
        final int splitAxis = bestAxis;
        sorted.sort(Comparator.comparingDouble(g -> g.getBoundingBox().center(splitAxis)));

        // Create child nodes with flattened geometries
        Geometries left = new Geometries();
        Geometries right = new Geometries();

        for (int i = 0; i < bestSplitIndex; i++) {
            left.geometries.add(sorted.get(i));
        }
        for (int i = bestSplitIndex; i < sorted.size(); i++) {
            right.geometries.add(sorted.get(i));
        }

        // Recursively build BVH for children with incremented depth
        left.createBVH(depth + 1);
        right.createBVH(depth + 1);

        // Replace this node's geometries with the two child nodes
        geometries.clear();
        geometries.add(left);
        geometries.add(right);

        // Update this node's bounding box
        CBR leftBox = left.getBoundingBox();
        CBR rightBox = right.getBoundingBox();

        if (leftBox == null) leftBox = left.createCBR();
        if (rightBox == null) rightBox = right.createCBR();

        if (leftBox != null && rightBox != null) {
            setBoundingBox(leftBox.surround(rightBox));
        } else if (leftBox != null) {
            setBoundingBox(leftBox);
        } else if (rightBox != null) {
            setBoundingBox(rightBox);
        }
        System.out.println("geometries");
    }

    /**
     * Flattens nested Geometries objects to avoid infinite recursion in BVH creation.
     * This method recursively extracts all primitive geometries from nested Geometries objects.
     *
     * @param source The list of geometries to flatten
     * @param target The list to add flattened geometries to
     */
    private void flattenGeometries(List<Intersectable> source, List<Intersectable> target) {
        for (Intersectable geometry : source) {
            if (geometry instanceof Geometries) {
                // Recursively flatten nested Geometries
                Geometries nestedGeometries = (Geometries) geometry;
                flattenGeometries(nestedGeometries.geometries, target);
            } else {
                // Add primitive geometry directly
                target.add(geometry);
            }
        }
    }
}