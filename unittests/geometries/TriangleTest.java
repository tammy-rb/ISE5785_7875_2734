package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

class TriangleTest {

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

}