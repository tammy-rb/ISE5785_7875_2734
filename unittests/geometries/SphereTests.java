package geometries;

import org.junit.jupiter.api.Test;
import primitives.*;

import java.util.List;

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

    /**
     * Test method for {@link geometries.Sphere#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        Sphere sphere = new Sphere(1, new Point(2, 0, 0));

        // ============ Equivalence Partitions Tests ==============
        // TC01: Ray starts before the sphere and intersects it twice
        Ray ray01 = new Ray(new Point(0, 0.5, 0.5), new Vector(1, 0, 0));
        final var expected01 = List.of(
                new Point(2 - Math.sqrt(2) / 2, 0.5, 0.5),
                new Point(2 + Math.sqrt(2) / 2, 0.5, 0.5));
        final var result01 = sphere.findIntersections(ray01);
        assertNotNull(result01,
                "Error: Ray intersects the sphere — result should not be null");
        assertEquals(2, result01.size(),
                "Error: Wrong number of intersection points");
        assertEquals(expected01, result01,
                "Error: Intersection points with the sphere are incorrect");

        // TC02: Ray starts inside the sphere and intersects it once
        Ray ray02 = new Ray(new Point(2.9, 0, 0), new Vector(1, 0, 0));
        final var expected02 = List.of(new Point(3, 0, 0));
        final var result02 = sphere.findIntersections(ray02);
        assertNotNull(result02,
                "Error: Ray intersects the sphere — result should not be null");
        assertEquals(1, result02.size(),
                "Error: Wrong number of intersection points");
        assertEquals(expected02, result02,
                "Error: Intersection point with the sphere is incorrect");

        // TC03: Ray starts outside the sphere and turns away from it
        Ray ray03 = new Ray(new Point(4, 1.5, 0), new Vector(1, 0.5, 0.5));
        assertNull(sphere.findIntersections(ray03),
                "Error: Ray doesn't intersect — should return null");

        // TC04: Ray starts outside the sphere and misses it
        Ray ray04 = new Ray(new Point(4, 2, 2), new Vector(-0.5, 1, 0));
        assertNull(sphere.findIntersections(ray04),
                "Error: Ray doesn't intersect — should return null");

        // =============== Boundary Values Tests ==================
        // TC101: Ray starts on the sphere and turns away from it
        Ray ray101 = new Ray(new Point(3, 0, 0), new Vector(1, 0.1, 0.1));
        assertNull(sphere.findIntersections(ray101),
                "Error: Ray doesn't intersect — should return null");

        // TC102: Ray starts on the sphere and intersects it once
        Ray ray102 = new Ray(new Point(2, 1, 0), new Vector(0, -1, 0.1));
        final var expected102 = List.of(new Point(2, 1 - 2 / 1.01, 0.1 * 2 / 1.01));
        final var result102 = sphere.findIntersections(ray102);
        assertNotNull(result102,
                "Error: Ray intersects the sphere — result should not be null");
        assertEquals(1, result102.size(),
                "Error: Wrong number of intersection points");
        assertEquals(expected102, result102,
                "Error: Intersection point with the sphere is incorrect");

        // TC103: Ray through center, starts on surface, turns away
        Ray ray103 = new Ray(new Point(2, 0, 1), new Vector(0, 0, 1));
        assertNull(sphere.findIntersections(ray103),
                "Error: Ray doesn't intersect — should return null");

        // TC104: Ray through center, starts on surface, turns toward sphere
        Ray ray104 = new Ray(new Point(2, -1, 0), new Vector(0, 1, 0));
        final var expected104 = List.of(new Point(2, 1, 0));
        final var result104 = sphere.findIntersections(ray104);
        assertNotNull(result104,
                "Error: Ray intersects the sphere — result should not be null");
        assertEquals(1, result104.size(),
                "Error: Wrong number of intersection points");
        assertEquals(expected104, result104,
                "Error: Intersection point with the sphere is incorrect");

        // TC105: Ray through center, starts at center of sphere
        Ray ray105 = new Ray(new Point(2, 0, 0), new Vector(1, 0.5, 0));
        final var expected105 = List.of(new Point(2 + 2 * Math.sqrt(5) / 5, Math.sqrt(5) / 5, 0));
        final var result105 = sphere.findIntersections(ray105);
        assertNotNull(result105,
                "Error: Ray intersects the sphere — result should not be null");
        assertEquals(1, result105.size(),
                "Error: Wrong number of intersection points");
        assertEquals(expected105, result105,
                "Error: Intersection point with the sphere is incorrect");

        // TC106: Ray through center, starts outside and goes away
        Ray ray106 = new Ray(new Point(4, 0, 0), new Vector(1, 0, 0));
        assertNull(sphere.findIntersections(ray106),
                "Error: Ray doesn't intersect — should return null");

        // TC107: Ray through center, starts outside and intersects twice
        Ray ray107 = new Ray(new Point(4, 0, 0), new Vector(-1, 0, 0));
        final var expected107 = List.of(new Point(3, 0, 0), new Point(1, 0, 0));
        final var result107 = sphere.findIntersections(ray107);
        assertNotNull(result107,
                "Error: Ray intersects the sphere — result should not be null");
        assertEquals(2, result107.size(),
                "Error: Wrong number of intersection points");
        assertEquals(expected107, result107,
                "Error: Intersection points with the sphere are incorrect");

        // TC108: Ray through center, starts inside and intersects once
        Ray ray108 = new Ray(new Point(2.5, 0, 0), new Vector(1, 0, 0));
        final var expected108 = List.of(new Point(3, 0, 0));
        final var result108 = sphere.findIntersections(ray108);
        assertNotNull(result108,
                "Error: Ray intersects the sphere — result should not be null");
        assertEquals(1, result108.size(),
                "Error: Wrong number of intersection points");
        assertEquals(expected108, result108,
                "Error: Intersection point with the sphere is incorrect");

        // TC109: Ray is tangent, starts before the launch point
        Ray ray109 = new Ray(new Point(4, 1, 0), new Vector(-1, 0, 0));
        assertNull(sphere.findIntersections(ray109),
                "Error: Ray doesn't intersect — should return null");

        // TC110: Ray is tangent, starts at the launch point
        Ray ray110 = new Ray(new Point(3, 0, 0), new Vector(0, 1, 0));
        assertNull(sphere.findIntersections(ray110),
                "Error: Ray doesn't intersect — should return null");

        // TC111: Ray is tangent, starts after the launch point
        Ray ray111 = new Ray(new Point(3, 1, 0), new Vector(0, 1, 0));
        assertNull(sphere.findIntersections(ray111),
                "Error: Ray doesn't intersect — should return null");

        // TC112: Ray parallel to tangent, starts inside sphere
        Ray ray112 = new Ray(new Point(2, 0.5, 0.8), new Vector(1, 0, 0));
        final var expected112 = List.of(new Point(2 + Math.sqrt(0.11), 0.5, 0.8));
        final var result112 = sphere.findIntersections(ray112);
        assertNotNull(result112,
                "Error: Ray intersects the sphere — result should not be null");
        assertEquals(1, result112.size(),
                "Error: Wrong number of intersection points");
        assertEquals(expected112, result112,
                "Error: Intersection point with the sphere is incorrect");

        // TC113: Ray parallel to tangent, starts outside sphere
        Ray ray113 = new Ray(new Point(4, 1.1, 0), new Vector(1, 0, 0));
        assertNull(sphere.findIntersections(ray113),
                "Error: Ray doesn't intersect — should return null");
    }
}