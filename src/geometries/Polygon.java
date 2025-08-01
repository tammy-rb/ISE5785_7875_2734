package geometries;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static primitives.Util.*;

import primitives.*;

/**
 * Polygon class represents two-dimensional polygon in 3D Cartesian coordinate
 * system
 *
 * @author Dan
 */
public class Polygon extends Geometry {
    /**
     * List of polygon's vertices
     */
    protected final List<Point> vertices;
    /**
     * Associated plane in which the polygon lays
     */
    protected final Plane plane;
    /**
     * The size of the polygon - the amount of the vertices in the polygon
     */
    private final int size;

    /**
     * Polygon constructor based on vertices list. The list must be ordered by edge
     * path. The polygon must be convex.
     *
     * @param vertices list of vertices according to their order by
     *                 edge path
     * @throws IllegalArgumentException in any case of illegal combination of
     *                                  vertices:
     *                                  <ul>
     *                                  <li>Less than 3 vertices</li>
     *                                  <li>Consequent vertices are in the same
     *                                  point
     *                                  <li>The vertices are not in the same
     *                                  plane</li>
     *                                  <li>The order of vertices is not according
     *                                  to edge path</li>
     *                                  <li>Three consequent vertices lay in the
     *                                  same line (180&#176; angle between two
     *                                  consequent edges)
     *                                  <li>The polygon is concave (not convex)</li>
     *                                  </ul>
     */
    public Polygon(Point... vertices) {
        if (vertices.length < 3)
            throw new IllegalArgumentException("A polygon can't have less than 3 vertices");
        this.vertices = List.of(vertices);
        size = vertices.length;

        // Generate the plane according to the first three vertices and associate the
        // polygon with this plane.
        // The plane holds the invariant normal (orthogonal unit) vector to the polygon
        plane = new Plane(vertices[0], vertices[1], vertices[2]);
        if (size == 3) return; // no need for more tests for a Triangle

        Vector n = plane.getNormal(vertices[0]);
        // Subtracting any subsequent points will throw an IllegalArgumentException
        // because of Zero Vector if they are in the same point
        Vector edge1 = vertices[size - 1].subtract(vertices[size - 2]);
        Vector edge2 = vertices[0].subtract(vertices[size - 1]);

        // Cross Product of any subsequent edges will throw an IllegalArgumentException
        // because of Zero Vector if they connect three vertices that lay in the same
        // line.
        // Generate the direction of the polygon according to the angle between last and
        // first edge being less than 180deg. It is hold by the sign of its dot product
        // with the normal. If all the rest consequent edges will generate the same sign
        // - the polygon is convex ("kamur" in Hebrew).
        boolean positive = edge1.crossProduct(edge2).dotProduct(n) > 0;
        for (var i = 1; i < size; ++i) {
            // Test that the point is in the same plane as calculated originally
            if (!isZero(vertices[i].subtract(vertices[0]).dotProduct(n)))
                throw new IllegalArgumentException("All vertices of a polygon must lay in the same plane");
            // Test the consequent edges have
            edge1 = edge2;
            edge2 = vertices[i].subtract(vertices[i - 1]);
            if (positive != (edge1.crossProduct(edge2).dotProduct(n) > 0))
                throw new IllegalArgumentException("All vertices must be ordered and the polygon must be convex");
        }
    }

    @Override
    public Vector getNormal(Point point) {
        return plane.getNormal(point);
    }

    @Override
    protected AABB createBoundingBoxHelper() {
        Point first = vertices.getFirst();
        double minX = first.get_xyz().d1(), maxX = first.get_xyz().d1();
        double minY = first.get_xyz().d2(), maxY = first.get_xyz().d2();
        double minZ = first.get_xyz().d3(), maxZ = first.get_xyz().d3();

        for (int i = 1; i < vertices.size(); i++) {
            Point p = vertices.get(i);
            double x = p.get_xyz().d1(), y = p.get_xyz().d2(), z = p.get_xyz().d3();

            if (x < minX) minX = x;
            else if (x > maxX) maxX = x;

            if (y < minY) minY = y;
            else if (y > maxY) maxY = y;

            if (z < minZ) minZ = z;
            else if (z > maxZ) maxZ = z;
        }

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }


    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray, double maxDistance) {
        var intersections = this.plane.findIntersections(ray);
        if (intersections == null)
            return null;
        Point p = intersections.getFirst();
        // Check if the intersection point lies on any of the polygon's vertices
        for (Point vertex : vertices)
            if (p.equals(vertex))
                return null;

        Vector[] normals = new Vector[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            Vector v1 = null;
            if (i == vertices.size() - 1)
                v1 = vertices.getFirst().subtract(vertices.get(i));
            else
                v1 = vertices.get(i + 1).subtract(vertices.get(i));
            Vector v2 = vertices.get(i).subtract(p);
            try {
                normals[i] = v1.crossProduct(v2);
            } catch (IllegalArgumentException e) {
                return null; // p is on edge or edge continueus
            }
        }
        // check the point is inside the polygon
        double initialDot = normals[0].dotProduct(normals[1]);
        for (int i = 1; i < normals.length - 1; i++)
            if (initialDot * normals[i].dotProduct(normals[i + 1]) <= 0)
                return null; // not all in the same direction

        List<Intersection> intersectionList = intersections.stream()
                .filter(i -> alignZero(i.distance(ray.getHead()) - maxDistance) <= 0)
                .map(i -> new Intersection(this, i)).collect(Collectors.toList());
        if (!intersectionList.isEmpty()) {
            return intersectionList;
        }
        return null;
    }
}