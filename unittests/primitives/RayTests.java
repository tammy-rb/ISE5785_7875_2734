package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link primitives.Ray} class.
 */
class RayTests {

    /**
     * Test method for {@link Ray#getPoint(double)}.
     * Verifies the correctness of computing a point along the ray at a given distance from the head.
     */
    @Test
    void testGetPoint() {
        Ray ray = new Ray(new Point(1, 0, -1), new Vector(0, 0, 1));

        // ============ Equivalence Partitions Tests ==============
        // TC01: Positive distance
        assertDoesNotThrow(() -> ray.getPoint(2),
                "ERROR: getPoint() should not throw an exception for positive distance");

        assertEquals(new Point(1, 0, 1), ray.getPoint(2),
                "ERROR: getPoint() wrong result for positive distance");

        // TC02: Negative distance
        assertDoesNotThrow(() -> ray.getPoint(-1),
                "ERROR: getPoint() should not throw an exception for negative distance");

        assertEquals(new Point(1, 0, -2), ray.getPoint(-1),
                "ERROR: getPoint() wrong result for negative distance");

        // =============== Boundary Values Tests ==================
        // TC11: Zero distance
        // getPoint(0) should return the head of the ray
        assertDoesNotThrow(() -> ray.getPoint(0),
                "ERROR: getPoint() should not throw an exception for zero distance");

        assertEquals(ray.getHead(), ray.getPoint(0),
                "ERROR: getPoint() with zero distance should return the ray's head");
    }
}
