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
        final Point gp1 = new Point(0, 1, 0);
        final Point gp2 = new Point(4, 1, 0);
        final Point gp3 = new Point(4, 5, 0);
        final Point gp4 = new Point(0, 5, 0);
        final Polygon polygon = new Polygon(new Point[]{gp1, gp2, gp3, gp4});

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray intersects inside the polygon (1 point)
        final Point p01 = new Point(-1, -1, -4);
        final Vector v01 = new Vector(4, 2, 2);
        final Ray ray01 = new Ray(p01, v01);
        final Point intersectionPoint01 = new Point(3, 1, 0);
        final var expected01 = List.of(intersectionPoint01);
        final var result01 = polygon.findIntersections(ray01);
        assertNotNull(result01, "Ray intersects inside the polygon — result should not be null");
        assertEquals(1, result01.size(), "Wrong number of intersection points");
        assertEquals(expected01, result01, "Intersection point inside polygon is incorrect");

        // TC02: Ray intersects outside the polygon against an edge (0 points)
        final Point p02 = new Point(-3, -3, -6);
        final Vector v02 = new Vector(4, 6, 6);
        final Ray ray02 = new Ray(p02, v02);
        final var result02 = polygon.findIntersections(ray02);
        assertNull(result02, "Ray intersects outside polygon against edge — should return null");

        // TC03: Ray intersects outside the polygon against a vertex (0 points)
        final Point p03 = new Point(-2, -1, -4);
        final Vector v03 = new Vector(3, 0, 4);
        final Ray ray03 = new Ray(p03, v03);
        final var result03 = polygon.findIntersections(ray03);
        assertNull(result03, "Ray intersects outside polygon against vertex — should return null");

        // =============== Boundary Values Tests ==================

        // TC11: Ray intersects exactly on an edge continues (0 points)
        final Point p11 = new Point(1, 0, -3);
        final Vector v11 = new Vector(1, 0, 3);
        final Ray ray11 = new Ray(p11, v11);
        final var result11 = polygon.findIntersections(ray11);
        assertNull(result11, "Ray intersects on an edge continues — should return null");

        // TC12: Ray intersects exactly on a vertex (0 points)
        final Point p12 = new Point(-1, -3, -1);
        final Vector v12 = new Vector(3, 3, 1);
        final Ray ray12 = new Ray(p12, v12);
        final var result12 = polygon.findIntersections(ray12);
        assertNull(result12, "Ray intersects exactly on a vertex — should return null");

        // TC13: Ray intersects exactly on an edge (0 points)
        final Point p13 = new Point(-4, -3, -2);
        final Vector v13 = new Vector(6, 6, 2);
        final Ray ray13 = new Ray(p13, v13);
        final var result13 = polygon.findIntersections(ray13);
        assertNull(result13, "Ray intersects exactly on edge — should return null");
    }
}