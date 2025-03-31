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
        // ============ Equivalence Partitions Tests ==============

        // TC01: Test that the normal is orthogonal to two vectors in the plane
        Point p1 = new Point(0, 0, 0);
        Point p2 = new Point(0, 0, 1);
        Point p3 = new Point(0, 3, -4);

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
        // check that the normal is normalized (length = 1)
        assertEquals(0, normal.length() - 1, 0.00001,
                "ERROR: normal vector is not normalized");
    }


    @Test
    void testGetNormal() {
    }

    @Test
    void testTestGetNormal() {
    }
}