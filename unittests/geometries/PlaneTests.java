package geometries;

import org.junit.jupiter.api.Test;
import primitives.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link geometries.Plane} class.
 */
class PlaneTests {

    /**
     * Delta value for accuracy when comparing the numbers of type 'double' in
     * assertEquals
     */
    private static final double DELTA = 0.000001;

    /**
     * Test method for
     * {@link geometries.Plane#Plane(primitives.Point, primitives.Point, primitives.Point)}.
     */
    @Test
    void testPlaneConstructorThreePoints() {

        Point p1 = new Point(0, 0, 0);
        Point p2 = new Point(0, 0, 1);
        Point p3 = new Point(0, 3, -4);

        // ============ Equivalence Partitions Tests ==============
        // TC01: Test that the normal is orthogonal to two vectors in the plane
        // check the constructor can make a valid plane
        assertDoesNotThrow(() -> new Plane(p1, p2, p3),
                "Error: Failed constructing a correct plane");
        Plane plane = new Plane(p1, p2, p3);

        // Vectors in the plane
        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);
        Vector normal = plane.getNormal();

        // Check if the normal is orthogonal to both vectors in the plane
        assertEquals(0, normal.dotProduct(v1), DELTA,
                "ERROR: normal of plane is not orthogonal to v1");
        assertEquals(0, normal.dotProduct(v2), DELTA,
                "ERROR: normal of plane is not orthogonal to v2");

        // Check if the normal is normalized (length = 1)
        assertEquals(1, normal.length(), DELTA,
                "ERROR: normal vector is not normalized");

        // =============== Boundary Values Tests ==================
        // TC11: Test with two identical points (p1 == p2)
        assertThrows(IllegalArgumentException.class, () -> new Plane(p1, p1, p3),
                "ERROR: constructed a plane with 2 identical points");

        // TC12: Test with two identical points (p1 == p3)
        assertThrows(IllegalArgumentException.class, () -> new Plane(p1, p2, p1),
                "ERROR: constructed a plane with 2 identical points");

        // TC13: Test with two identical points (p2 == p3)
        assertThrows(IllegalArgumentException.class, () -> new Plane(p1, p3, p3),
                "ERROR: constructed a plane with 2 identical points");

        // TC14: Test with three identical points
        assertThrows(IllegalArgumentException.class, () -> new Plane(p2, p2, p2),
                "ERROR: constructed a plane with 3 identical points");

        // TC15: Test with points on the same line (collinear points)
        Point p4 = new Point(1, 1, 1);
        Point p5 = new Point(2, 2, 2);
        Point p6 = new Point(3, 3, 3);
        assertThrows(IllegalArgumentException.class, () -> new Plane(p4, p5, p6),
                "ERROR: constructed a plane with 3 points on the same line");
    }

    /**
     * Test method for {@link geometries.Plane#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Checking normal calculation with three non-collinear points
        Point[] pts = {
                new Point(0, 0, 0),
                new Point(0, 0, 1),
                new Point(0, 3, -4)
        };
        Plane plane = new Plane(pts[0], pts[1], pts[2]);

        // Ensuring that no exceptions are thrown
        assertDoesNotThrow(() -> plane.getNormal(pts[1]),
                "ERROR: getting normal of plane throws an unnecessary exception");

        // Generating the test result
        Vector normal = plane.getNormal(pts[1]);

        // Ensuring that the normal is a unit vector
        assertEquals(1, normal.length(), DELTA,
                "Error: Plane's normal is not a unit vector");

        // Ensuring that the normal is orthogonal to the plane's edges
        for (int i = 0; i < 3; ++i) {
            assertEquals(0d, normal.dotProduct(pts[i].subtract(pts[i == 0 ? 2 : i - 1])), DELTA,
                    "Error: Plane's normal is not orthogonal to one of the vectors");
        }
    }

    /**
     * Test method for {@link geometries.Plane#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        // Plane XZ: defined by q=(1,0,1), normal=(0,1,0)
        Plane plane = new Plane(new Point(1, 0, 1), new Vector(0, 1, 0));

        // ============ Equivalence Partitions Tests ==============
        // TC01: Ray isn't orthogonal nor parallel to the plane and intersects it
        final Ray ray01 = new Ray(new Point(1, 2, 1), new Vector(0, -1, 0));
        final var expected01 = List.of(new Point(1, 0, 1));
        final var result01 = plane.findIntersections(ray01);
        assertNotNull(result01,
                "Error: Ray intersects the plane — result should not be null");
        assertEquals(1, result01.size(),
                "Error: Wrong number of intersection points");
        assertEquals(expected01, result01,
                "Error: Intersection point with the plane is incorrect");

        // TC02: Ray isn't orthogonal nor parallel to the plane and doesn't intersect it
        final Ray ray02 = new Ray(new Point(1, 2, 2), new Vector(0, 1, 0));
        final var result02 = plane.findIntersections(ray02);
        assertNull(result02,
                "Error: Ray doesn't intersect the plane — should return null");

        // =============== Boundary Values Tests ==================
        // TC11: Ray is parallel to the plane and isn't included in the plane
        final Ray ray11 = new Ray(new Point(1, 2, 3), new Vector(1, 0, -1));
        final var result11 = plane.findIntersections(ray11);
        assertNull(result11,
                "Error: Ray is parallel and not in the plane — should return null");

        // TC12: Ray is parallel to the plane and included in the plane
        final Ray ray12 = new Ray(new Point(2, 0, 2), new Vector(1, 0, -1));
        final var result12 = plane.findIntersections(ray12);
        assertNull(result12,
                "Error: Ray lies in the plane — should return null");

        // TC13: Ray is orthogonal to the plane and starts before the plane
        final Ray ray13 = new Ray(new Point(1, -2, 1), new Vector(0, 1, 0));
        final var expected13 = List.of(new Point(1, 0, 1));
        final var result13 = plane.findIntersections(ray13);
        assertNotNull(result13,
                "Error: Ray intersects the plane — result should not be null");
        assertEquals(1, result13.size(),
                "Error: Wrong number of intersection points");
        assertEquals(expected13, result13,
                "Error: Intersection point with the plane is incorrect");

        // TC14: Ray is orthogonal to the plane and starts in the plane
        final Ray ray14 = new Ray(new Point(1, 0, 2), new Vector(0, 1, 0));
        final var result14 = plane.findIntersections(ray14);
        assertNull(result14,
                "Error: Ray starts in the plane and goes out — should return null");

        // TC15: Ray is orthogonal to the plane and starts after the plane
        final Ray ray15 = new Ray(new Point(2, 4, 2), new Vector(0, 1, 0));
        final var result15 = plane.findIntersections(ray15);
        assertNull(result15,
                "Error: Ray starts after the plane — should return null");

        // TC16: Ray isn't orthogonal nor parallel and starts in the plane
        final Ray ray16 = new Ray(new Point(2, 0, 3), new Vector(1, 1, 0));
        final var result16 = plane.findIntersections(ray16);
        assertNull(result16,
                "Error: Ray starts in the plane — should return null");

        // TC17: Ray isn't orthogonal nor parallel and starts at q (defining point of plane)
        final Ray ray17 = new Ray(new Point(1, 0, 1), new Vector(1, 1, 1));
        final var result17 = plane.findIntersections(ray17);
        assertNull(result17,
                "Error: Ray starts at q — should return null");
    }
}