package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

class PlaneTests {

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
                "Failed constructing a correct plane");
        Plane plane = new Plane(p1, p2, p3);

        // Vectors in the plane
        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);
        Vector normal = plane.getNormal();

        // Check if the normal is orthogonal to both vectors in the plane
        assertEquals(0, normal.dotProduct(v1), 0.00001,
                "ERROR: normal of plane is not orthogonal to v1");
        assertEquals(0, normal.dotProduct(v2), 0.00001,
                "ERROR: normal of plane is not orthogonal to v2");

        // Check that the normal is normalized (length = 1)
        assertEquals(0, normal.length() - 1, 0.00001,
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
        // TC01: Check normal calculation with three non-collinear points
        Point[] pts = {
                new Point(0, 0, 0),
                new Point(0, 0, 1),
                new Point(0, 3, -4)
        };
        Plane plane = new Plane(pts[0], pts[1], pts[2]);

        // Ensure no exceptions are thrown
        assertDoesNotThrow(() -> plane.getNormal(pts[1]),
                "ERROR: getting normal of plane throws an unnecessary exception");

        // Generate the test result
        Vector normal = plane.getNormal(pts[1]);

        // Ensure the normal is a unit vector
        assertEquals(1, normal.length(), 0.00001,
                "Plane's normal is not a unit vector");

        // Ensure the normal is orthogonal to the plane's edges
        for (int i = 0; i < 3; ++i) {
            assertEquals(0d, normal.dotProduct(pts[i].subtract(pts[i == 0 ? 2 : i - 1])), 0.0001,
                    "Plane's normal is not orthogonal to one of the vectors");
        }
    }
}