package geometries;

import org.junit.jupiter.api.Test;
import primitives.*;

import java.util.ArrayList;
import java.util.List;

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
        Tube tube = new Tube(1, new Ray(new Point(2, 0, 0), new Vector(0, 0, 1)));
        Tube tube2 = new Tube(2, new Ray(new Point(5, 2, 1), new Vector(1, 1, 1)));

        // ============ Equivalence Partitions Tests ==============
        // TC01: Ray starts outside and crosses the tube (2 intersections)
        Ray ray1 = new Ray(new Point(0, 2, 1), new Vector(1, -1, 0));
        final var result01 = tube.findIntersections(ray1);
        assertEquals(2, result01.size(), "TC01: expected 2 intersections");
        assertEquals(List.of(
                        new Point(2 - Math.sqrt(2) / 2, Math.sqrt(2) / 2, 1),
                        new Point(2 + Math.sqrt(2) / 2, -Math.sqrt(2) / 2, 1)),
                result01, "TC01: intersections points are not correct");

        // TC02: Ray is completely outside and does not intersect (2 rays tested)
        Ray ray2_1 = new Ray(new Point(0, -1, 0), new Vector(-1, -1, -1));
        assertNull(tube.findIntersections(ray2_1), "TC02: Expected null - ray does not intersect");

        Ray ray2_2 = new Ray(new Point(3, -1, 2), new Vector(0, 1, 0));
        assertNull(tube2.findIntersections(ray2_2), "TC02: Expected null - ray does not intersect");

        // TC03: Ray starts inside the tube and intersects the surface (2 rays tested)
        Ray ray3_1 = new Ray(new Point(2.5, 0, 4), new Vector(1, 0, 10));
        final var result03_1 = tube.findIntersections(ray3_1);
        assertEquals(1, result03_1.size(), "TC03: expected 1 intersection");
        assertEquals(List.of(new Point(3, 0, 9)), result03_1, "TC03: intersection point is not correct");

        Ray ray3_2 = new Ray(new Point(5, 2, 1), new Vector(2, -2, 0));
        final var result03_2 = tube2.findIntersections(ray3_2);
        assertEquals(1, result03_2.size(), "TC03: expected 1 intersection");
        assertEquals(List.of(
                        new Point(5 + Math.sqrt(2), 2 - Math.sqrt(2), 1)),
                result03_2, "TC03: intersection point is not correct");

        // =============== Boundary Values Tests ==================
        // TC10: Ray starts inside the tube and orthogonal to the axis.
        Ray ray10 = new Ray(new Point(1.5, 0, 2), new Vector(-1, 0, 0));
        final var result10 = tube.findIntersections(ray10);
        assertEquals(1, result10.size(), "TC10: expected 1 intersection");
        assertEquals(List.of(new Point(1, 0, 2)), result10, "TC10: intersection point is not correct");

        // TC11: Ray starts inside the tube and orthogonal to the axis. starts at the axis
        Ray ray11 = new Ray(new Point(2, 0, 2), new Vector(-1, 0, 0));
        final var result11 = tube.findIntersections(ray11);
        assertEquals(1, result11.size(), "TC11: expected 1 intersection");
        assertEquals(List.of(new Point(1, 0, 2)), result11, "TC11: intersection point is not correct");

        // TC12: Ray starts inside the tube and orthogonal to the axis. intersect the axis
        Ray ray12 = new Ray(new Point(1.5, 0, 1), new Vector(1, 0, 0));
        final var result12 = tube.findIntersections(ray12);
        assertEquals(1, result12.size(), "TC12: expected 1 intersection");
        assertEquals(List.of(new Point(3, 0, 1)), result12, "TC12: intersection point is not correct");

        // TC13: Ray starts at surface and goes inside (orthogonal to axis). p-p0 is orthogonal to the axis
        Ray ray13 = new Ray(new Point(3, 0, 0), new Vector(-1, 0, 0));
        final var result13 = tube.findIntersections(ray13);
        assertEquals(1, result13.size(), "TC13: expected 1 intersection");
        assertEquals(List.of(new Point(1, 0, 0)), result13, "TC13: intersection point is not correct");

        // TC14: Ray starts at surface and goes inside (orthogonal to axis). intersect axis.
        Ray ray14 = new Ray(new Point(3, 0, 1), new Vector(-1, 0, 0));
        final var result14 = tube.findIntersections(ray14);
        assertEquals(1, result14.size(), "TC14: expected 1 intersection");
        assertEquals(List.of(new Point(1, 0, 1)), result14, "TC14: intersection point is not correct");

        // TC15: Ray starts at surface and goes inside (orthogonal to axis).
        Ray ray15 = new Ray(new Point(3, 0, 1), new Vector(-1, 1, 0));
        final var result15 = tube.findIntersections(ray15);
        assertEquals(1, result15.size(), "TC15: expected 1 intersection");
        assertEquals(List.of(new Point(2, 1, 1)), result15, "TC15: intersection point is not correct");

        // TC16: Ray starts on tube axis, orthogonal to axis. starts at p0 of the axis
        Ray ray16 = new Ray(new Point(2, 0, 0), new Vector(1, 1, 0));
        final var result16 = tube.findIntersections(ray16);
        assertEquals(1, result16.size(), "TC16: expected 1 intersection");
        assertEquals(List.of(new Point(2 + Math.sqrt(2) / 2, Math.sqrt(2) / 2, 0)), result16, "TC16: intersection point is not correct");

        // TC17: Ray starts on tube axis (not orthogonal or parallel to axis)
        Ray ray17 = new Ray(new Point(2, 0, 1), new Vector(1, 1, 0));
        final var result17 = tube.findIntersections(ray17);
        assertEquals(1, result17.size(), "TC17: expected 1 intersection");
        assertEquals(List.of(new Point(2 + Math.sqrt(2) / 2, Math.sqrt(2) / 2, 1)), result17, "TC17: intersection point is not correct");

        // TC18: Ray orthogonal to axis, starts outside, intersects p0 of the tube
        Ray ray18_1 = new Ray(new Point(5, 0, 0), new Vector(-1, 0, 0));
        final var result18_1 = tube.findIntersections(ray18_1);
        assertEquals(2, result18_1.size(), "TC18_1: expected 2 intersections");
        assertEquals(List.of(new Point(3, 0, 0), new Point(1, 0, 0)), result18_1, "TC18_1: intersection points are not correct");

        Ray ray18_2 = new Ray(new Point(3, 4, 1), new Vector(1, -1, 0));
        final var result18_2 = tube2.findIntersections(ray18_2);
        assertEquals(2, result18_2.size(), "TC18: expected 2 intersections");
        assertEquals(List.of(new Point(5 - Math.sqrt(2), 2 + Math.sqrt(2), 1), new Point(5 + Math.sqrt(2), 2 - Math.sqrt(2), 1)), result18_2, "TC18: intersection points are not correct");

        Ray ray19 = new Ray(new Point(3, 4, 1), new Vector(1, -1, 0));
        final var result19 = tube2.findIntersections(ray19);
        assertEquals(2, result19.size(), "TC19: expected 2 intersections");
        assertEquals(List.of(new Point(5 - Math.sqrt(2), 2 + Math.sqrt(2), 1), new Point(5 + Math.sqrt(2), 2 - Math.sqrt(2), 1)), result19, "TC19: intersection points are not correct");

        // TC20: Ray orthogonal to axis, starts outside, intersects axis
        Ray ray20 = new Ray(new Point(6, 0, 1), new Vector(-1, 0, 0));
        final var result20 = tube.findIntersections(ray20);
        assertEquals(2, result20.size(), "TC20: expected 2 intersections");
        assertEquals(List.of(new Point(3, 0, 1), new Point(1, 0, 1)), result20, "TC20: intersection points are not correct");

        // TC21: Ray orthogonal to axis, starts outside, p-p0 orthogonal to axis
        Ray ray21 = new Ray(new Point(4, 0, 0), new Vector(-14, 1, 0));
        final var result21 = tube.findIntersections(ray21);
        assertEquals(2, result21.size(), "TC21: expected 2 intersections");
        assertEquals(List.of(
                        new Point((792 + 14 * Math.sqrt(772)) / 394, (56 - Math.sqrt(772)) / 394, 0),
                        new Point((792 - 14 * Math.sqrt(772)) / 394, (56 + Math.sqrt(772)) / 394, 0)),
                result21, "TC21: intersection points are not correct");

        // TC22: Ray orthogonal to axis, starts outside.
        Ray ray22 = new Ray(new Point(7, 0, 3), new Vector(-14, 1, 0));
        final var result22 = tube.findIntersections(ray22);
        assertEquals(2, result22.size(), "TC22: expected 2 intersections");
        assertEquals(List.of(
                        new Point((399 + 28 * Math.sqrt(43)) / 197, (70 - 2 * Math.sqrt(43)) / 197, 3),
                        new Point((399 - 28 * Math.sqrt(43)) / 197, (70 + 2 * Math.sqrt(43)) / 197, 3)),
                result22, "TC22: intersection points are not correct");

        // TC23: Ray coincides with the axis, starts at p0
        Ray ray23 = new Ray(new Point(2, 0, 0), new Vector(0, 0, 1));
        assertNull(tube.findIntersections(ray23), "TC23: Expected null - ray inside tube axis");

        // TC24: Ray coincides with the axis, starts at p0, direction is opposite to the axis direction
        Ray ray24 = new Ray(new Point(2, 0, 0), new Vector(0, 0, -1));
        assertNull(tube.findIntersections(ray24), "TC24: Expected null - ray inside tube axis");

        // TC25: Ray coincides with the axis
        Ray ray25 = new Ray(new Point(2, 0, 1), new Vector(0, 0, 1));
        assertNull(tube.findIntersections(ray25), "TC25: Expected null - ray inside tube axis");

        // TC26: Ray coincides with axis in opposite direction
        Ray ray26 = new Ray(new Point(2, 0, 2), new Vector(0, 0, -1));
        assertNull(tube.findIntersections(ray26), "TC26: Expected null - ray inside tube axis");

        // TC27: Ray starts on axis's p0, not orthogonal or parallel to axis
        Ray ray27 = new Ray(new Point(2, 0, 0), new Vector(1, 0, 1));
        final var result27 = tube.findIntersections(ray27);
        assertEquals(1, result27.size(), "TC27: expected 1 intersection");
        assertEquals(List.of(new Point(3, 0, 1)), result27, "TC27: intersection point is not correct");

        // TC28: Ray tangent to the tube surface
        Ray ray28 = new Ray(new Point(1, -1, 1), new Vector(0, 1, 0));
        final var result28 = tube.findIntersections(ray28);
        assertEquals(1, result28.size(), "TC28: expected 1 intersection");
        assertEquals(List.of(new Point(1, 0, 1)), result28, "TC28: intersection point is not correct");

        // TC29: Ray tangent to tube, p-p0 orthogonal to axis
        Ray ray29 = new Ray(new Point(1, -1, 0), new Vector(0, 1, 0));
        final var result29 = tube.findIntersections(ray29);
        assertEquals(1, result29.size(), "TC29: expected 1 intersection");
        assertEquals(List.of(new Point(1, 0, 0)), result29, "TC29: intersection point is not correct");

        // TC30: Ray tangent to tube, starts at surface, p-p0 orthogonal to axis
        Ray ray30 = new Ray(new Point(3, 0, 0), new Vector(0, 1, 0));
        assertNull(tube.findIntersections(ray30), "TC30: Expected null - tangent ray from surface");

        // TC31: Ray tangent to tube, starts at surface
        Ray ray31 = new Ray(new Point(3, 0, 2), new Vector(0, 1, 0));
        assertNull(tube.findIntersections(ray31), "TC31: Expected null - tangent ray from surface");

        // TC32: Ray starts from surface and goes outside
        Ray ray32 = new Ray(new Point(3, 0, 5), new Vector(1, 1, 1));
        assertNull(tube.findIntersections(ray32), "TC32: Expected null - ray going away from tube");

        // TC33: Ray starts from surface and goes inside
        Ray ray33 = new Ray(new Point(3, 0, 5), new Vector(-1, 1, 1));
        final var result33 = tube.findIntersections(ray33);
        assertEquals(1, result33.size(), "TC33: expected 1 intersection");
        assertEquals(List.of(new Point(2, 1, 6)), result33, "TC33: intersection point is not correct");

        // TC34: Ray starts from surface, p-p0 orthogonal to axis, goes outside
        Ray ray34 = new Ray(new Point(3, 0, 0), new Vector(1, 1, 1));
        assertNull(tube.findIntersections(ray34), "TC34: Expected null - ray going away from tube");

        // TC35: Ray starts inside the tube and intersects p0 (orthogonal to axis)
        Ray ray35 = new Ray(new Point(2.5, 0, 0), new Vector(-1, 0, 0));
        final var result35 = tube.findIntersections(ray35);
        assertEquals(1, result35.size(), "TC35: expected 1 intersection");
        assertEquals(List.of(new Point(1, 0, 0)), result35, "TC35: intersection point is not correct");

        // TC36: Ray starts inside and intersects p0 (not orthogonal to axis)
        Ray ray36 = new Ray(new Point(2.5, 0, -1), new Vector(-1, 0, 2));
        final var result36 = tube.findIntersections(ray36);
        assertEquals(1, result36.size(), "TC36: expected 1 intersection");
        assertEquals(List.of(new Point(1, 0, 2)), result36, "TC36: intersection point is not correct");

        // TC37: Ray inside tube, parallel to axis (no intersections)
        Ray ray37 = new Ray(new Point(2.5, 0, -3), new Vector(0, 0, 1));
        assertNull(tube.findIntersections(ray37), "TC37: Expected null - inside and parallel");

        // TC38: Ray outside the tube and parallel to axis
        Ray ray38 = new Ray(new Point(1, 1, -1), new Vector(0, 0, 1));
        assertNull(tube.findIntersections(ray38), "TC38: Expected null - outside and parallel");

        // TC39: Ray on the tube surface and parallel to axis
        Ray ray39 = new Ray(new Point(3, 0, 1), new Vector(0, 0, 1));
        assertNull(tube.findIntersections(ray39), "TC39: Expected null - on surface and parallel");

        // TC40: Ray on the tube surface and parallel to axis, p-p0 orthogonal to axis
        Ray ray40 = new Ray(new Point(1, 0, 0), new Vector(0, 0, 1));
        assertNull(tube.findIntersections(ray40), "TC40: Expected null - on surface and parallel");

        // TC41: Ray inside tube, parallel to axis (no intersections). p-p0 orthogonal to axis
        Ray ray41 = new Ray(new Point(2.5, 0, 0), new Vector(0, 0, 1));
        assertNull(tube.findIntersections(ray41), "TC41: Expected null - inside and parallel");

        // TC42: Ray outside the tube and parallel to axis. p-p0 orthogonal to axis
        Ray ray42 = new Ray(new Point(1, 1, 0), new Vector(0, 0, 1));
        assertNull(tube.findIntersections(ray42), "TC42: Expected null - outside and parallel");

        // TC43: Ray starts outside the tube and not orthogonal nor parallel to the axis. p-p0 orthogonal to the axis. No intersections.
        Ray ray43 = new Ray(new Point(0, 0, 0), new Vector(-1, 0, 1));
        assertNull(tube.findIntersections(ray43), "TC43: Expected null - outside and non-intersecting");

        // TC44: Ray starts outside the tube and not orthogonal nor parallel to the axis. p-p0 orthogonal to the axis. 2 intersections.
        Ray ray44 = new Ray(new Point(0, 0, 0), new Vector(1, 0, 1));
        final var result44 = tube.findIntersections(ray44);
        assertEquals(2, result44.size(), "TC44: expected 2 intersections");
        assertEquals(List.of(
                new Point(1, 0, 1),
                new Point(3, 0, 3)
        ), result44, "TC44: intersection points are not correct");

        // TC45: Ray starts inside the tube and not orthogonal nor parallel to the axis. p-p0 orthogonal to the axis. 1 intersection.
        Ray ray45 = new Ray(new Point(1.5, 0, 0), new Vector(1, 0, 1));
        final var result45 = tube.findIntersections(ray45);
        assertEquals(1, result45.size(), "TC45: expected 1 intersection");
        assertEquals(List.of(new Point(3, 0, 1.5)), result45, "TC45: intersection point is not correct");

        // TC46: Direction of the ray is orthogonal to the axis and to vector p-p0. No intersections.
        Ray ray46 = new Ray(new Point(4, 0, 0), new Vector(0, 1, 0));
        assertNull(tube.findIntersections(ray46), "TC46: Expected null");

        // TC47: Direction of the ray is orthogonal to the axis and to vector p-p0 and also tangent to the cylinder
        Ray ray47 = new Ray(new Point(3, -1, 2), new Vector(0, 1, 0));
        final var result47 = tube.findIntersections(ray47);
        assertEquals(1, result47.size(), "TC47: expected 1 intersection");
        assertEquals(List.of(new Point(3, 0, 2)), result47, "TC47: intersection point is not correct");
    }
}