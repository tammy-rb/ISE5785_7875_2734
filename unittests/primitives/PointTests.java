package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointTests {

    @Test
    void testSubtract() {
        // ============ Equivalence Partitions Tests ==============
        Point p1 = new Point(1, 2, 3);
        // =============== Boundary Values Tests ==================
    }

    @Test
    void testAdd() {
    }

    @Test
    void testDistanceSquared() {
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(2, 4, 5);
        assertEquals(9, p1.distanceSquared(p2), 0.00001,
                "ERROR: squared distance between points is wrong");
        assertEquals(0, p1.distanceSquared(p1), 0.00001,
                "ERROR: point squared distance to itself is not zero");
    }

    @Test
    void testDistance() {
    }
}