package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AABBTests {

    @Test
    void testIntersects() {
        AABB aabb = new AABB(-1, 1, 0, 1, 2, 2);

        // ========== EP (Equivalence Partition) ==========

        // TC01: Ray does not intersect the box
        Ray ray01 = new Ray(new Point(5, 0, 0), new Vector(4, 1, 0));
        assertFalse(aabb.intersects(ray01), "TC01: Ray should not intersect AABB");

        // TC02: Ray intersects the box
        Ray ray02 = new Ray(new Point(0, 3, 0), new Vector(0, -3, 1));
        assertTrue(aabb.intersects(ray02), "TC02: Ray should intersect AABB");

        // ========== Boundary Value Tests ==========

        // TC11: Ray intersects with the base edge
        Ray ray11 = new Ray(new Point(1, 0, 0), new Vector(0, 1, 0));
        assertTrue(aabb.intersects(ray11), "TC11: Ray should intersect base edge");

        // TC12: Ray intersects with the edge
        Ray ray12 = new Ray(new Point(1, 0, 1), new Vector(0, 1, 0));
        assertTrue(aabb.intersects(ray12), "TC12: Ray should intersect the edge");

        // TC13: Ray intersects with the base face
        Ray ray13 = new Ray(new Point(0, 1, -1), new Vector(0, 0, 1));
        assertTrue(aabb.intersects(ray13), "TC13: Ray should intersect base face");

        // TC14: Ray starts on base and goes inward
        Ray ray14 = new Ray(new Point(0, 1, 0), new Vector(0, 0, 1));
        assertTrue(aabb.intersects(ray14), "TC14: Ray should intersect from base");

        // TC15: Ray starts inside and intersects edge
        Ray ray15 = new Ray(new Point(0.5, 1.5, 1), new Vector(-0.5, -0.5, 1));
        assertTrue(aabb.intersects(ray15), "TC15: Ray should intersect from inside");

        // TC16: Ray starts inside and exits the box
        Ray ray16 = new Ray(new Point(0.5, 1.5, 1), new Vector(0.5, -0.5, 2));
        assertTrue(aabb.intersects(ray16), "TC16: Ray should intersect from inside");

        // TC17: Ray starts at vertex and goes inside
        Ray ray17 = new Ray(new Point(1, 1, 0), new Vector(0, 1, 3));
        assertTrue(aabb.intersects(ray17), "TC17: Ray should intersect from vertex");

        // TC18: Ray starts at edge and goes away - consider as an intersection
        Ray ray18 = new Ray(new Point(1, 1.5, 1), new Vector(4, -6.5, 2));
        assertTrue(aabb.intersects(ray18), "TC18: Ray should not intersect");

        // TC19: Ray starts at vertex and goes in wrong direction - consider as an intersection
        Ray ray19 = new Ray(new Point(1, 1, 0), new Vector(-2, -6, 3));
        assertTrue(aabb.intersects(ray19), "TC19: Ray should not intersect");

        // TC20: Ray intersects exactly at a vertex
        Ray ray20 = new Ray(new Point(0, 0, 0), new Vector(1, 1, 0));
        assertTrue(aabb.intersects(ray20), "TC20: Ray should intersect exactly at vertex");
    }

    @Test
    void testSurround() {
        // Base AABB
        AABB aabb1 = new AABB(-1, -1, -1, 1, 1, 1);

        // Case 1: Same AABB
        AABB result1 = aabb1.surround(new AABB(-1, -1, -1, 1, 1, 1));
        assertEquals(aabb1, result1, "Case 1: Surround with same AABB should return identical AABB");

        // Case 2: AABB where only x_max is greater
        AABB aabb2 = new AABB(-1, -1, -1, 2, 1, 1);
        AABB expected2 = new AABB(-1, -1, -1, 2, 1, 1);
        AABB result2 = aabb1.surround(aabb2);
        assertEquals(expected2, result2, "Case 2: Surround with one extended axis");

        // Case 3: Completely different AABB
        AABB aabb3 = new AABB(5, 6, 7, 9, 10, 11);
        AABB expected3 = new AABB(-1, -1, -1, 9, 10, 11);
        AABB result3 = aabb1.surround(aabb3);
        assertEquals(expected3, result3, "Case 3: Surround with disjoint AABB");
    }
}