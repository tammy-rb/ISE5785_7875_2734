package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link geometries.Sphere} class.
 */
class SphereTests {

    /**
     * Delta value for accuracy when comparing the numbers of type 'double' in
     * assertEquals
     */
    private static final double DELTA = 0.000001;

    @Test
    void testGetNormal() {
        Point center = new Point(0, 0, 0);

        // ============ Equivalence Partitions Tests ==============
        // TC01: Basic sphere
        Sphere sphere = new Sphere(1, center);

        // ensure there are no exceptions and getNormal work correctly
        assertDoesNotThrow(() -> sphere.getNormal(new Point(0, 0, 1)),
                "ERROR: getNormal() should not throw an exception");

        // generate the test result
        Vector normal = sphere.getNormal(new Point(0, 0, 1));

        // ensure |normal| = 1
        assertEquals(1, normal.length(), DELTA, "Sphere's normal is not a unit vector");

        // ensure the normal is correct
        assertEquals(new Vector(0, 0, 1), normal,
                "Sphere's normal is not orthogonal to the sphere");
    }

    @Test
    void testFindIntersections() {
    }
}