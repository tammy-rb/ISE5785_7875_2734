package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link primitives.Point} class.
 */
class PointTests {
    /**
     * Delta value for accuracy when comparing the numbers of type 'double' in
     * assertEquals
     */
    private static final double DELTA = 0.000001;

    /**
     * Test method for {@link primitives.Point#subtract(primitives.Point)}.
     */
    @Test
    void testSubtract() {
        // ============ Equivalence Partitions Tests ==============
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(2, 3, 4);

        // TC01: Test subtracting two points results in the correct vector
        assertEquals(new Vector(-1, -1, -1), p1.subtract(p2),
                "ERROR: (point2 - point1) does not work correctly");

        // =============== Boundary Values Tests ==================
        // TC11: Test subtracting a point from itself throws an exception zero vector
        assertThrows(IllegalArgumentException.class, () -> p1.subtract(p1),
                "ERROR: (point - itself) does not throw an exception");
    }

    /**
     * Test method for {@link primitives.Point#add(primitives.Vector)}.
     */
    @Test
    void testAdd() {
        // ============ Equivalence Partitions Tests ==============
        Point p = new Point(1, 2, 3);
        Vector v = new Vector(1, 2, 3);
        Vector vOpposite = new Vector(-1, -2, -3);

        // TC01: Test adding a vector to a point results in the correct new point
        assertEquals(new Point(2, 4, 6), p.add(v),
                "ERROR: (point + vector) = other point does not work correctly");

        // TC02: Test adding an opposite vector results in the zero point
        assertEquals(Point.ZERO, p.add(vOpposite),
                "ERROR: (point + vector) = center of coordinates does not work correctly");
    }

    /**
     * Test method for {@link primitives.Point#distanceSquared(primitives.Point)}.
     */
    @Test
    void testDistanceSquared() {
        // ============ Equivalence Partitions Tests ==============
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(2, 4, 5);

        // TC01: Test squared distance between two distinct points
        assertEquals(9, p1.distanceSquared(p2), DELTA,
                "ERROR: squared distance between points is wrong");

        // TC02: Test squared distance is symmetric (p1 to p2 equals p2 to p1)
        assertEquals(9, p2.distanceSquared(p1), DELTA,
                "ERROR: squared distance between points is wrong");

        // =============== Boundary Values Tests ==================
        // TC11: Test squared distance from a point to itself is zero
        assertEquals(0, p1.distanceSquared(p1), DELTA,
                "ERROR: point squared distance to itself is not zero");
    }

    /**
     * Test method for {@link primitives.Point#distance(primitives.Point)}.
     */
    @Test
    void testDistance() {
        // ============ Equivalence Partitions Tests ==============
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(2, 4, 5);

        // TC01: Test distance between two distinct points
        assertEquals(3, p1.distance(p2), DELTA,
                "ERROR: distance between points is wrong");

        // TC02: Test distance is symmetric (p1 to p2 equals p2 to p1)
        assertEquals(3, p2.distance(p1), DELTA,
                "ERROR: distance between points is wrong");

        // =============== Boundary Values Tests ==================
        // TC11: Test distance from a point to itself is zero
        assertEquals(0, p1.distance(p1), DELTA,
                "ERROR: point distance to itself is not zero");
    }
}