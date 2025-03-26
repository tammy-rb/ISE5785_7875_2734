package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointTest {

    @org.junit.jupiter.api.Test
    void testSubtract() {
    }

    @org.junit.jupiter.api.Test
    void testAdd() {
    }

    @org.junit.jupiter.api.Test
    void testDistanceSquared() {
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(2, 4, 6);
        assertEquals(9, p1.distanceSquared(p2), 0.00001,
                "ERROR: squared distance between points is wrong");
    }

    @org.junit.jupiter.api.Test
    void testDistance() {
    }
}