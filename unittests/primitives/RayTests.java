package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RayTests {

    @Test
    void testGetPoint() {
        Ray ray = new Ray(new Point(1, 0, -1), new Vector(0, 0, 1));
        // ============ Equivalence Partitions Tests ==============
        // TC01: Positive distance
        assertDoesNotThrow(() -> ray.getPoint(2),
                "ERROR: getNormal() should not throw an exception");

        assertEquals(new Point(1, 0, 1), ray.getPoint(2),
                "ERROR: getPoint() is wrong");

        // TC02: Negative distance
        assertDoesNotThrow(() -> ray.getPoint(-1),
                "ERROR: getNormal() should not throw an exception");

        assertEquals(new Point(1, 0, -2), ray.getPoint(-1),
                "ERROR: getPoint() is wrong");


        // =============== Boundary Values Tests ==================
        // TC11: Zero distance
        assertDoesNotThrow(() -> ray.getPoint(0),
                "ERROR: getNormal() should not throw an exception");

        assertEquals(ray.getHead(), ray.getPoint(0),
                "ERROR: getPoint() should be null");
    }
}