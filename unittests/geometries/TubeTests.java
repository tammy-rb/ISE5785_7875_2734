package geometries;

import org.junit.jupiter.api.Test;
import primitives.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link geometries.Tube} class.
 */
class TubeTests {

    /**
     * Delta value for accuracy when comparing the numbers of type 'double' in
     * assertEquals
     */
    private static final double DELTA = 0.000001;

    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============
        // Define the tube's axis
        Point p0 = new Point(0, 0, 0); // Base point of the axis
        Vector direction = new Vector(0, 0, 1); // Tube axis along Z
        Ray ray = new Ray(p0, direction);

        // Create the tube with radius 1
        Tube tube = new Tube(1, ray);

        // Select a point on the tube surface
        Point p_surface = new Point(1, 0, 2); // 1 unit from the Z-axis, at height 2

        // Ensure getNormal does not throw an exception
        assertDoesNotThrow(() -> tube.getNormal(p_surface),
                "ERROR: getNormal() should not throw an exception");

        // Compute the normal
        Vector normal = tube.getNormal(p_surface);

        // Expected normal: from the tube's axis (0,0,2) to (1,0,2) → (1,0,0)
        assertEquals(new Vector(1, 0, 0), normal,
                "ERROR: Incorrect normal computed");

        // Ensure the normal is a unit vector
        assertEquals(1, normal.length(), DELTA,
                "ERROR: Normal is not a unit vector");

        // =============== Boundary Values Tests ==================
        // TC11: Test for boundary case where point is orthogonal to the ray vector
        // Select a point which is p-p0 is orthogonal to the ray direction
        Point p_bva = new Point(0, 1, 0);

        // Ensure getNormal does not throw an exception for BVA
        assertDoesNotThrow(() -> tube.getNormal(p_bva),
                "ERROR: getNormal() should not throw an exception for orthogonal point");

        // Compute the normal for the BVA point
        Vector normalBVA = tube.getNormal(p_bva);

        Vector expectedNormalBVA = p_bva.subtract(p0);

        // Ensure the normal is correct for the boundary value
        assertEquals(expectedNormalBVA, normalBVA,
                "ERROR: Incorrect normal computed for orthogonal point");

        // Ensure the normal is a unit vector
        assertEquals(1, normalBVA.length(), DELTA,
                "ERROR: Normal for BVA point is not a unit vector");
    }

    @Test
    void testFindIntersections() {
    }
}