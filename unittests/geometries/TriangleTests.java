package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link geometries.Triangle} class.
 */
class TriangleTests {

    /**
     * Delta value for accuracy when comparing the numbers of type 'double' in
     * assertEquals
     */
    private static final double DELTA = 0.000001;

    /**
     * Test method for {@link geometries.Triangle#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: There is a simple single test here - using a triangle
        Point[] pts = {
                new Point(0, 0, 0),
                new Point(0, 1, 0),
                new Point(1, 0, 0)
        };
        Polygon triangle = new Polygon(pts);

        // ensure there are no exceptions
        assertDoesNotThrow(() -> triangle.getNormal(new Point(0.25, 0.25, 0)),
                "ERROR: getNormal() should not throw an exception");

        // generate the test result
        Vector normal = triangle.getNormal(new Point(0.25, 0.25, 0));

        // ensure |normal| = 1
        assertEquals(1, normal.length(), DELTA, "Triangle's normal is not a unit vector");

        // ensure the result is orthogonal to all the edges
        for (int i = 0; i < 3; ++i) {
            assertEquals(0d, normal.dotProduct(pts[i].subtract(pts[i == 0 ? 2 : i - 1])), DELTA,
                    "Triangle's normal is not orthogonal to one of the edges");
        }
    }

    @Test
    void testFindIntersections() {
        final Polygon polygon = new Polygon(
                new Point(0, 1, 0),
                new Point(4, 1, 0),
                new Point(4, 5, 0),
                new Point(0, 5, 0)
        );

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray intersects inside the polygon (1 point)
        final Ray ray01 = new Ray(new Point(0, 0, -3), new Vector(2, 3, 3));
        final List<Point> expected01 = List.of(new Point(2, 3, 0));
        final var result01 = polygon.findIntersections(ray01);
        assertNotNull(result01, "Ray intersects inside the polygon — result should not be null");
        assertEquals(1, result01.size(), "Wrong number of intersection points");
        assertEquals(expected01, result01, "Intersection point inside polygon is incorrect");

        // TC02: Ray intersects outside the polygon against an edge (0 points)
        final Ray ray02 = new Ray(new Point(-2, 0, -3), new Vector(5, 0, 3));
        final var result02 = polygon.findIntersections(ray02);
        assertNull(result02, "Ray intersects outside polygon against edge — should return null");

        // TC03: Ray intersects outside the polygon against a vertex (0 points)
        final Ray ray03 = new Ray(new Point(3, 0, -3), new Vector(-4, 0, 3));
        final var result03 = polygon.findIntersections(ray03);
        assertNull(result03, "Ray intersects outside polygon against vertex — should return null");

        // =============== Boundary Values Tests ==================

        // TC11: Ray intersects exactly on an edge continues (0 points)
        final Ray ray11 = new Ray(new Point(0, 0, -3), new Vector(4, 0, 3));
        final var result11 = polygon.findIntersections(ray11);
        assertNull(result11, "Ray intersects on an edge continues — should return null");

        // TC12: Ray intersects exactly on a vertex (0 points)
        final Ray ray12 = new Ray(new Point(-1, 0, -3), new Vector(5, 5, 3));
        final var result12 = polygon.findIntersections(ray12);
        assertNull(result12, "Ray intersects exactly on a vertex — should return null");

        // TC13: Ray intersects exactly on an edge (0 points)
        final Ray ray13 = new Ray(new Point(2, 0, -3), new Vector(-2, 3, 3));
        final var result13 = polygon.findIntersections(ray13);
        assertNull(result13, "Ray intersects exactly on edge — should return null");
    }

}