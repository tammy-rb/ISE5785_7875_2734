package primitives;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Test
    void testFindClosestPoint() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Point in the middle of the list
        Ray ray_01 = new Ray(new Point(1, 0, -1), new Vector(0, 0, 1));
        List<Point> points_01 = List.of(
                new Point(1, 0, 2),
                new Point(1, 0, 0),
                new Point(1, 0, 4)
        );
        assertEquals(ray_01.findClosestPoint(points_01), new Point(1, 0, 0),
                "ERROR: findClosestPoint() should return other point");

        //tc11:
        Ray ray_11 = new Ray(new Point(1, 0, 5), new Vector(1, 2, 1));
        assertNull(ray_11.findClosestPoint(List.of()),
                "ERROR: findClosestPoint() should return null");

        //tc12:
        Ray ray_12 = new Ray(new Point(2, 2, 0), new Vector(1, 1, 1));
        List<Point> points_12 = List.of(
                new Point(3, 3, 1),
                new Point(4, 4, 2),
                new Point(6, 6, 4)
        );
        assertEquals(ray_12.findClosestPoint(points_12), new Point(3, 3, 1),
                "ERROR: findClosestPoint() should return other point");

        //tc13:
        Ray ray_13 = new Ray(new Point(3, 0, 0), new Vector(-2, 0, 0));
        List<Point> points_13 = List.of(
                new Point(-3, 0, 0),
                new Point(-1, 0, 0),
                new Point(1, 0, 0)
        );
        assertEquals(ray_13.findClosestPoint(points_13), new Point(1, 0, 0),
                "ERROR: findClosestPoint() should return other point");
    }
}
