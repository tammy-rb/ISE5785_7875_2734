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

    /**
     * Test method for {@link geometries.Triangle#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        Triangle triangle = new Triangle(
                new Point(-1, -2, 0),
                new Point(-1, -5, 0),
                new Point(-4, -2, 0)
        );

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray intersects inside the triangle (1 point)
        final Ray ray01 = new Ray(new Point(0, 0, -2), new Vector(-2, -3, 2));
        final var expected01 = List.of(new Point(-2, -3, 0));
        final var result01 = triangle.findIntersections(ray01);
        assertNotNull(result01, "Ray intersects inside the triangle — result should not be null");
        assertEquals(1, result01.size(), "Wrong number of intersection points");
        assertEquals(expected01, result01, "Intersection point inside triangle is incorrect");

        // TC02: Ray intersects outside the triangle against an edge (0 points)
        final Ray ray02 = new Ray(new Point(0, 1, -2), new Vector(-2, -1, 2));
        final var result02 = triangle.findIntersections(ray02);
        assertNull(result02, "Ray intersects outside triangle against edge — should return null");

        // TC03: Ray intersects outside the triangle against a vertex (0 points)
        final Ray ray03 = new Ray(new Point(0, 1, 3), new Vector(0, -2, -3));
        final var result03 = triangle.findIntersections(ray03);
        assertNull(result03, "Ray intersects outside triangle against vertex — should return null");

        // =============== Boundary Values Tests ==================

        // TC11: Ray intersects exactly on an edge (0 points per Moller implementation)
        final Ray ray11 = new Ray(new Point(4, 1, -1), new Vector(-7, -3, 1));
        final var result11 = triangle.findIntersections(ray11);
        assertNull(result11, "Ray intersects on an edge — should return null");

        // TC12: Ray intersects exactly on a vertex
        final Ray ray12 = new Ray(new Point(0, 1, 5), new Vector(-1, -3, -5));
        final var result12 = triangle.findIntersections(ray12);
        assertNull(result12, "Ray intersects exactly on a vertex — should return null");

        // TC13: Ray intersects exactly on an edge continuation (0 points)
        final Ray ray13 = new Ray(new Point(-2, 3, -4), new Vector(3, -5, 4));
        final var result13 = triangle.findIntersections(ray13);
        assertNull(result13, "Ray intersects on edge continuation — should return null");
    }
}