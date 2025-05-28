package geometries;

import org.junit.jupiter.api.Test;
import primitives.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link geometries.Cylinder} class.
 */
class CylinderTests {

    /**
     * Test method for {@link geometries.Cylinder#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        // Define the cylinder axis
        Point baseCenter = new Point(0, 0, 0);
        Vector axisDirection = new Vector(0, 0, 1);
        Ray axis = new Ray(baseCenter, axisDirection);

        // Create cylinder with radius 1 and height 5
        Cylinder cylinder = new Cylinder(1, axis, 5);

        // ============ Equivalence Partitions Tests ==============
        // TC01: Point on the round surface (side of the cylinder)
        Point p_surface = new Point(1, 0, 2); // 1 unit away from axis, at height 2
        assertEquals(new Vector(1, 0, 0), cylinder.getNormal(p_surface),
                "ERROR: Incorrect normal for cylinder surface");

        // TC02: Point on the upper base
        Point p_top = new Point(0.5, 0.5, 5);
        assertEquals(new Vector(0, 0, 1), cylinder.getNormal(p_top),
                "ERROR: Incorrect normal for cylinder top base");

        // TC03: Point on the lower base
        Point p_bottom = new Point(-0.5, -0.5, 0);
        assertEquals(new Vector(0, 0, -1), cylinder.getNormal(p_bottom),
                "ERROR: Incorrect normal for cylinder bottom base");

        // =============== Boundary Values Tests ==================
        // TC11: Center of the bottom base
        Point p_centerBottom = new Point(0, 0, 0);
        assertEquals(axisDirection.scale(-1), cylinder.getNormal(p_centerBottom),
                "ERROR: Incorrect normal at bottom base center");

        // TC12: Center of the top base
        Point p_centerTop = new Point(0, 0, 5);
        assertEquals(axisDirection, cylinder.getNormal(p_centerTop),
                "ERROR: Incorrect normal at top base center");

        // TC13: Edge of the bottom base
        Point p_edgeBottom = new Point(1, 0, 0);
        assertEquals(axisDirection.scale(-1), cylinder.getNormal(p_edgeBottom),
                "ERROR: Incorrect normal at bottom base edge");

        // TC14: Edge of the top base
        Point p_edgeTop = new Point(1, 0, 5);
        assertEquals(axisDirection, cylinder.getNormal(p_edgeTop),
                "ERROR: Incorrect normal at top base edge");
    }

    @Test
    public void testFindIntersections() {
        // Cylinder definition for all tests:
        // Radius=1, Axis along z-axis starting at (2,0,0), Height=2.
        // This means caps are at z=0 and z=2. Lateral surface is (x-2)^2 + y^2 = 1.
        Cylinder cylinder = new Cylinder(1,
                new Ray(new Point(2, 0, 0), new Vector(0, 0, 1)),
                2);

        // ============ Equivalence Partitions Tests ==============
        // TC01: Ray starts outside, crosses lateral surface twice
        var result01 = cylinder.findIntersections(new Ray(
                new Point(0, 0, 1),
                new Vector(1, 0, 0)));
        assertEquals(2, result01.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(1, 0, 1),
                        new Point(3, 0, 1)),
                result01,
                "ERROR: Intersection points incorrect");

        // TC02: Ray starts outside, misses cylinder (no intersection)
        assertNull(cylinder.findIntersections(new Ray(
                        new Point(0, 2, 1),
                        new Vector(1, 0, 0))),
                "ERROR: Expected null - ray misses");

        // TC03: Ray starts inside, hits lateral surface once
        var result03 = cylinder.findIntersections(new Ray(
                new Point(2.5, 0, 1),
                new Vector(1, 0, 0)));
        assertEquals(1, result03.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(3, 0, 1)),
                result03,
                "ERROR: Intersection point incorrect");

        // TC04: Ray starts outside, enters top cap, exits bottom cap (2 cap intersections)
        var result04 = cylinder.findIntersections(new Ray(
                new Point(2, 0, 3),
                new Vector(0, 0, -1)));
        assertEquals(2, result04.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(2, 0, 2),
                        new Point(2, 0, 0)),
                result04,
                "ERROR: Intersection points incorrect");

        // TC05: Ray starts outside, enters bottom cap, exits top cap (2 cap intersections)
        var result05 = cylinder.findIntersections(new Ray(
                new Point(2, 0, -1),
                new Vector(0, 0, 1)));
        assertEquals(2, result05.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(2, 0, 0),
                        new Point(2, 0, 2)),
                result05,
                "ERROR: Intersection points incorrect");

        // TC06: Ray starts inside, hits top cap once
        var result06 = cylinder.findIntersections(new Ray(
                new Point(2, 0, 1),
                new Vector(0, 0, 1)));
        assertEquals(1, result06.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(2, 0, 2)),
                result06,
                "ERROR: Intersection point incorrect");

        // TC07: Ray starts inside, hits bottom cap once
        var result07 = cylinder.findIntersections(new Ray(
                new Point(2, 0, 1),
                new Vector(0, 0, -1)));
        assertEquals(1, result07.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(2, 0, 0)),
                result07,
                "ERROR: Intersection point incorrect");

        // TC08: Ray starts outside, enters bottom cap, exits top cap (offset from axis)
        var result08 = cylinder.findIntersections(new Ray(
                new Point(2.5, 0, -1),
                new Vector(0, 0, 1)));
        assertEquals(2, result08.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(2.5, 0, 0),
                        new Point(2.5, 0, 2)),
                result08,
                "ERROR: Intersection points incorrect");

        // TC09: Ray starts outside, enters lateral surface, exits top cap
        var result09 = cylinder.findIntersections(new Ray(
                new Point(0, 0, 1),
                new Vector(1, 0, 0.5)));
        assertEquals(2, result09.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(1, 0, 1.5),
                        new Point(2, 0, 2)),
                result09,
                "ERROR: Intersection points incorrect");

        // TC10: Ray starts outside, enters bottom cap, exits lateral surface
        var result10 = cylinder.findIntersections(new Ray(
                new Point(1, 0, -1),
                new Vector(1, 0, 1)));
        assertEquals(2, result10.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(2, 0, 0),
                        new Point(3, 0, 1)),
                result10,
                "ERROR: Intersection points incorrect");

        // =============== Boundary Values Tests ==================
        // TC11: Ray tangent to lateral surface, starts outside (single intersection)
        var result11 = cylinder.findIntersections(new Ray(
                new Point(3, 1, 1),
                new Vector(-1, 0, 0)));
        assertEquals(1, result11.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(2, 1, 1)),
                result11,
                "ERROR: Intersection point incorrect");

        // TC12: Ray starts on lateral surface, tangent (no further intersection)
        assertNull(cylinder.findIntersections(new Ray(
                        new Point(3, 0, 1),
                        new Vector(0, 1, 0))),
                "ERROR: Expected null - tangent from surface");

        // TC13: Ray starts on lateral surface, goes inside (1 intersection)
        var result13 = cylinder.findIntersections(new Ray(
                new Point(3, 0, 1),
                new Vector(-1, 0, 0)));
        assertEquals(1, result13.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(1, 0, 1)),
                result13,
                "ERROR: Intersection point incorrect");

        // TC14: Ray starts on lateral surface, goes outside (no further intersection)
        assertNull(cylinder.findIntersections(new Ray(
                        new Point(3, 0, 1),
                        new Vector(1, 0, 0))),
                "ERROR: Expected null - goes outside");

        // TC15: Ray starts on top cap, goes outside (no further intersection)
        assertNull(cylinder.findIntersections(new Ray(
                        new Point(2, 0, 2),
                        new Vector(0, 0, 1))),
                "ERROR: Expected null - goes outside");

        // TC16: Ray starts on top cap, goes inside (hits bottom cap)
        var result16 = cylinder.findIntersections(new Ray(
                new Point(2, 0, 2),
                new Vector(0, 0, -1)));
        assertEquals(1, result16.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(2, 0, 0)),
                result16,
                "ERROR: Intersection point incorrect");

        // TC17: Ray starts on bottom cap, goes outside (no further intersection)
        assertNull(cylinder.findIntersections(new Ray(
                        new Point(2, 0, 0),
                        new Vector(0, 0, -1))),
                "ERROR: Expected null - goes outside");

        // TC18: Ray starts on bottom cap, goes inside (hits top cap)
        var result18 = cylinder.findIntersections(new Ray(
                new Point(2, 0, 0),
                new Vector(0, 0, 1)));
        assertEquals(1, result18.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(2, 0, 2)),
                result18,
                "ERROR: Intersection point incorrect");

        // TC19: Ray starts on bottom rim, goes to top rim (straight across diameter).
        var result19 = cylinder.findIntersections(new Ray(
                new Point(1, 0, 0),
                new Vector(1, 0, 1)));
        assertEquals(1, result19.size(),
                "ERROR: Wrong number of intersection points (rim to rim)");
        assertEquals(List.of(
                        new Point(3, 0, 2)),
                result19,
                "ERROR: Intersection point incorrect (rim to rim)");

        // TC20: Ray on axis, starts mid-height, hits bottom cap
        var result20 = cylinder.findIntersections(new Ray(
                new Point(2, 0, 1),
                new Vector(0, 0, -1)));
        assertEquals(1, result20.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(2, 0, 0)),
                result20,
                "ERROR: Intersection point incorrect");

        // TC21: Ray parallel to axis, inside cylinder, hits top cap
        var result21 = cylinder.findIntersections(new Ray(
                new Point(2.5, 0, 1),
                new Vector(0, 0, 1)));
        assertEquals(1, result21.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(2.5, 0, 2)),
                result21,
                "ERROR: Intersection point incorrect");

        // TC22: Ray parallel to axis, outside cylinder, no intersection
        assertNull(cylinder.findIntersections(new Ray(
                        new Point(4, 0, 1),
                        new Vector(0, 0, 1))),
                "ERROR: Expected null - outside and parallel");

        // TC23: Ray parallel to axis, on lateral surface, hits top cap rim
        var result23 = cylinder.findIntersections(new Ray(
                new Point(3, 0, 1),
                new Vector(0, 0, 1)));
        assertEquals(1, result23.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(3, 0, 2)),
                result23,
                "ERROR: Intersection point incorrect");

        // TC24: Ray starts on top cap rim, goes inside, hits bottom cap
        var result24 = cylinder.findIntersections(new Ray(
                new Point(3, 0, 2),
                new Vector(-1, 0, -1)));
        assertEquals(1, result24.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(1, 0, 0)),
                result24,
                "ERROR: Intersection point incorrect");

        // TC25: Ray starts on bottom cap rim, goes inside, hits top cap
        var result25 = cylinder.findIntersections(new Ray(
                new Point(3, 0, 0),
                new Vector(-1, 0, 1)));
        assertEquals(1, result25.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(1, 0, 2)),
                result25,
                "ERROR: Intersection point incorrect");

        // TC26: Ray orthogonal to axis, starts outside, intersects axis (2 intersections)
        var result26 = cylinder.findIntersections(new Ray(
                new Point(4, 0, 1),
                new Vector(-1, 0, 0)));
        assertEquals(2, result26.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(3, 0, 1),
                        new Point(1, 0, 1)),
                result26,
                "ERROR: Intersection points incorrect");

        // TC27: Ray orthogonal to axis, starts inside, intersects axis (1 intersection)
        var result27 = cylinder.findIntersections(new Ray(
                new Point(2.5, 0, 1),
                new Vector(-1, 0, 0)));
        assertEquals(1, result27.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(1, 0, 1)),
                result27,
                "ERROR: Intersection point incorrect");

        // TC28: Ray starts at p0 (bottom cap center), not parallel nor orthogonal (hits lateral surface)
        var result28 = cylinder.findIntersections(new Ray(
                new Point(2, 0, 0),
                new Vector(1, 0, 1)));
        assertEquals(1, result28.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(3, 0, 1)),
                result28,
                "ERROR: Intersection point incorrect");

        // TC29: Ray starts outside, hits top cap rim (tangent-like hit) -> corrected to 2 intersections
        var result29 = cylinder.findIntersections(new Ray(
                new Point(3, 0, 3),
                new Vector(0, 0, -1)));
        assertEquals(2, result29.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(3, 0, 2),
                        new Point(3, 0, 0)),
                result29,
                "ERROR: Intersection points incorrect");

        // TC30: Ray starts outside, hits bottom cap rim (tangent-like hit) - Corrected to 2 intersections
        var result30 = cylinder.findIntersections(new Ray(
                new Point(3, 0, -1),
                new Vector(0, 0, 1)));
        assertEquals(2, result30.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(3, 0, 0),
                        new Point(3, 0, 2)),
                result30,
                "ERROR: Intersection points incorrect");

        // TC31: Ray starts on top cap center, goes outside (oblique, no further intersection)
        assertNull(cylinder.findIntersections(new Ray(
                        new Point(2, 0, 2),
                        new Vector(0, 1, 1))),
                "ERROR: Expected null - goes outside");

        // TC32: Ray starts on bottom cap center, goes outside (oblique, no further intersection)
        assertNull(cylinder.findIntersections(new Ray(
                        new Point(2, 0, 0),
                        new Vector(0, 1, -1))),
                "ERROR: Expected null - goes outside");

        // TC33: Ray starts outside, enters lateral surface, exits lateral surface (oblique, different Z)
        var result33 = cylinder.findIntersections(new Ray(
                new Point(0, 0, 3),
                new Vector(1, 0, -1)));
        assertEquals(2, result33.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(1, 0, 2),
                        new Point(3, 0, 0)),
                result33,
                "ERROR: Intersection points incorrect");

        // TC34: Ray starts inside, exits lateral surface (oblique, hits rim)
        var result34 = cylinder.findIntersections(new Ray(
                new Point(2.5, 0, 1.5),
                new Vector(1, 0, -1)));
        assertEquals(1, result34.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(3, 0, 1)),
                result34,
                "ERROR: Intersection point incorrect");

        // TC35: Ray starts outside, enters bottom cap, exits top cap (parallel to axis, offset)
        var result35 = cylinder.findIntersections(new Ray(
                new Point(2.5, 0, -1),
                new Vector(0, 0, 2)));
        assertEquals(2, result35.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(2.5, 0, 0),
                        new Point(2.5, 0, 2)),
                result35,
                "ERROR: Intersection points incorrect");

        // TC36: Ray starts outside, enters lateral surface, exits bottom cap (oblique)
        var result36 = cylinder.findIntersections(new Ray(
                new Point(0, 0, 1),
                new Vector(1, 0, -0.5)));
        assertEquals(2, result36.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(1, 0, 0.5),
                        new Point(2, 0, 0)),
                result36,
                "ERROR: Intersection points incorrect");

        // TC37: Ray starts below the cylinder, on its axis, and passes through both the bottom and top caps.
        var result37 = cylinder.findIntersections(new Ray(
                new Point(2, 0, -1),
                new Vector(0, 0, 1)));
        assertEquals(2, result37.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(2, 0, 0),
                        new Point(2, 0, 2)),
                result37,
                "ERROR:Intersection points incorrect");

        // TC38: Ray starts on lateral surface at z=2 (rim), goes inside (hits bottom cap rim)
        var result38 = cylinder.findIntersections(new Ray(
                new Point(1, 0, 2),
                new Vector(1, 0, -1)));
        assertEquals(1, result38.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(3, 0, 0)),
                result38,
                "ERROR: Intersection point incorrect");

        // TC39: Ray starts outside, orthogonal to axis, no intersections (outside Z range)
        assertNull(cylinder.findIntersections(new Ray(
                        new Point(4, 0, 3),
                        new Vector(-1, 0, 0))),
                "ERROR: Expected null - misses cylinder");

        // TC40: Ray starts on top cap rim, tangent to cap (no further intersection)
        assertNull(cylinder.findIntersections(new Ray(
                        new Point(3, 0, 2),
                        new Vector(0, 1, 0))),
                "ERROR: Expected null - tangent to cap");

        // TC41: Ray starts on bottom cap rim, tangent to cap (no further intersection)
        assertNull(cylinder.findIntersections(new Ray(
                        new Point(3, 0, 0),
                        new Vector(0, 1, 0))),
                "ERROR: Expected null - tangent to cap");

        // TC42 (NEW): Ray starts on top rim, goes to bottom rim (straight across diameter).
        var result42 = cylinder.findIntersections(new Ray(
                new Point(1, 0, 2),
                new Vector(1, 0, -1)));
        assertEquals(1, result42.size(),
                "ERROR: Wrong number of intersection points (rim to rim)");
        assertEquals(List.of(
                        new Point(3, 0, 0)),
                result42,
                "ERROR: Intersection point incorrect (rim to rim)");

        // TC43: Ray starts outside, passes through bottom rim, then hits lateral surface.
        var result43 = cylinder.findIntersections(new Ray(
                new Point(0, 0, 0),
                new Vector(1, 0, 0.1)));
        assertEquals(2, result43.size(),
                "ERROR: Wrong number of intersection points (outside through rim to lateral)");
        assertEquals(List.of(
                        new Point(1, 0, 0.1),
                        new Point(3, 0, 0.3)),
                result43,
                "ERROR: Intersection points incorrect (outside through rim to lateral)");

        // TC44: Ray starts slightly inside top rim, goes to bottom cap.
        var result44 = cylinder.findIntersections(new Ray(
                new Point(2.9, 0, 1.9),
                new Vector(-0.1, 0, -1.9)));
        assertEquals(1, result44.size(),
                "ERROR: Wrong number of intersection points (inside top rim to bottom cap)");
        assertEquals(List.of(
                        new Point(2.8, 0, 0)),
                result44,
                "ERROR: Intersection point incorrect (inside top rim to bottom cap)");

        // TC45: Ray starts outside, hits top rim, then exits lateral surface (oblique).
        var result45 = cylinder.findIntersections(new Ray(
                new Point(0, 0, 2),
                new Vector(1, 0, 0)));
        assertEquals(2, result45.size(),
                "ERROR: Wrong number of intersection points (outside to top rim to lateral)");
        assertEquals(List.of(
                        new Point(1, 0, 2),
                        new Point(3, 0, 2)),
                result45,
                "ERROR: Intersection points incorrect (outside to top rim to lateral)");

        // TC46: Ray starts on lateral surface, near bottom rim, goes into bottom cap.
        var result46 = cylinder.findIntersections(new Ray(
                new Point(2.5, 0, 0.1),
                new Vector(0, 0, -1)));
        assertEquals(1, result46.size(),
                "ERROR: Wrong number of intersection points (lateral near rim to bottom cap)");
        assertEquals(List.of(
                        new Point(2.5, 0, 0)),
                result46,
                "ERROR: Intersection point incorrect (lateral near rim to bottom cap)");

        // TC47: Ray starts on lateral surface, near top rim, goes into top cap.
        var result47 = cylinder.findIntersections(new Ray(
                new Point(2.5, 0, 1.9),
                new Vector(0, 0, 1)));
        assertEquals(1, result47.size(),
                "ERROR: Wrong number of intersection points (lateral near rim to top cap)");
        assertEquals(List.of(
                        new Point(2.5, 0, 2)),
                result47,
                "ERROR: Intersection point incorrect (lateral near rim to top cap)");

        // TC48: Ray starts outside, crosses bottom rim twice (passing through diameter).
        var result48 = cylinder.findIntersections(new Ray(
                new Point(4, 0, 0),
                new Vector(-1, 0, 0)));
        assertEquals(2, result48.size(),
                "ERROR: Wrong number of intersection points (outside crosses bottom rim twice)");
        assertEquals(List.of(
                        new Point(3, 0, 0),
                        new Point(1, 0, 0)),
                result48,
                "ERROR: Intersection points incorrect (outside crosses bottom rim twice)");

        // TC49: Ray starts on top rim, goes outside (oblique).
        assertNull(cylinder.findIntersections(new Ray(
                        new Point(3, 0, 2),
                        new Vector(1, 0, 1))),
                "ERROR: Expected null - starts on top rim and goes outside obliquely");

        // TC50: Ray starts on bottom rim, goes outside (oblique).
        assertNull(cylinder.findIntersections(new Ray(
                        new Point(3, 0, 0),
                        new Vector(1, 0, -1))),
                "ERROR: Expected null - starts on bottom rim and goes outside obliquely");

        // TC51: Ray starts outside, enters bottom cap, exits lateral surface (oblique)
        var result51 = cylinder.findIntersections(new Ray(
                new Point(1.5, 0, -1),
                new Vector(1, 0, 1)));
        assertEquals(2, result51.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(2.5, 0, 0),
                        new Point(3, 0, 0.5)),
                result51,
                "ERROR: Intersection points incorrect");

        // TC52: Ray starts outside, grazes top cap rim (tangent to lateral and cap at same point)
        var result52 = cylinder.findIntersections(new Ray(
                new Point(3, 1.0001, 2),
                new Vector(0, -1, 0)));
        assertEquals(1, result52.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(3, 0, 2)),
                result52,
                "ERROR: Intersection point incorrect");

        // TC53: Ray starts on lateral surface, goes inside, hits bottom cap (oblique)
        var result53 = cylinder.findIntersections(new Ray(
                new Point(3, 0, 1),
                new Vector(-1, 0, -1)));
        assertEquals(1, result53.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(2, 0, 0)),
                result53,
                "ERROR: Intersection point incorrect");

        // TC54: Ray starts on lateral surface, goes inside, hits top cap (oblique)
        var result54 = cylinder.findIntersections(new Ray(
                new Point(3, 0, 1),
                new Vector(-1, 0, 1)));
        assertEquals(1, result54.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(2, 0, 2)),
                result54,
                "ERROR: Intersection point incorrect");

        // TC55: Ray starts outside, hits lateral surface at z=0 (bottom rim)
        var result55 = cylinder.findIntersections(new Ray(
                new Point(0, 0, 0),
                new Vector(1, 0, 0)));
        assertEquals(2, result55.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(1, 0, 0),
                        new Point(3, 0, 0)),
                result55,
                "ERROR: Intersection points incorrect");

        // TC56: Ray starts outside, hits lateral surface at z=2 (top rim)
        var result56 = cylinder.findIntersections(new Ray(
                new Point(0, 0, 2),
                new Vector(1, 0, 0)));
        assertEquals(2, result56.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(1, 0, 2),
                        new Point(3, 0, 2)),
                result56,
                "ERROR: Intersection points incorrect");

        // TC57: Ray starts on top cap, not orthogonal, goes inside (hits lateral)
        var result57 = cylinder.findIntersections(new Ray(
                new Point(2.5, 0, 2),
                new Vector(-1, 0, -1)));
        assertEquals(1, result57.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(1, 0, 0.5)),
                result57,
                "ERROR: Intersection point incorrect");

        // TC58: Ray starts on bottom cap, not orthogonal, goes inside (hits lateral)
        var result58 = cylinder.findIntersections(new Ray(
                new Point(2.5, 0, 0),
                new Vector(-1, 0, 1)));
        assertEquals(1, result58.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(1, 0, 1.5)),
                result58,
                "ERROR: Intersection point incorrect");

        // TC59: Ray starts outside, crosses lateral surface twice (at different Z)
        var result59 = cylinder.findIntersections(new Ray(
                new Point(0, 0, 0.5),
                new Vector(1, 0, 0)));
        assertEquals(2, result59.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(1, 0, 0.5),
                        new Point(3, 0, 0.5)),
                result59,
                "ERROR: Intersection points incorrect");

        // TC60: Ray starts outside, parallel to axis, passes through top and bottom caps.
        var result60 = cylinder.findIntersections(new Ray(
                new Point(2.9, 0, 3),
                new Vector(0, 0, -1)));
        assertEquals(2, result60.size(),
                "ERROR: Wrong number of intersection points (passes through both caps)");
        assertEquals(List.of(
                        new Point(2.9, 0, 2),
                        new Point(2.9, 0, 0)),
                result60,
                "ERROR: Intersection points incorrect (passes through both caps)");

        // TC61: Ray starts on axis, orthogonal to axis, hits one lateral surface
        var result61 = cylinder.findIntersections(new Ray(
                new Point(2, 0, 1),
                new Vector(-1, 0, 0)));
        assertEquals(1, result61.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(1, 0, 1)),
                result61,
                "ERROR: Intersection point incorrect");

        // TC62: Ray starts on axis at z=2 (top cap center), orthogonal to axis, hits one lateral surface
        var result62 = cylinder.findIntersections(new Ray(
                new Point(2, 0, 2),
                new Vector(0, -1, 0)));
        assertEquals(1, result62.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(2, -1, 2)),
                result62,
                "ERROR: Intersection point incorrect");

        // TC63: Ray starts on axis at z=0 (bottom cap center), orthogonal to axis, hits one lateral surface
        var result63 = cylinder.findIntersections(new Ray(
                new Point(2, 0, 0),
                new Vector(0, -1, 0)));
        assertEquals(1, result63.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(2, -1, 0)),
                result63,
                "ERROR: Intersection point incorrect");

        // TC64: Ray starts outside, orthogonal to axis, crosses lateral surface twice (offset in Y)
        var result64 = cylinder.findIntersections(new Ray(
                new Point(0, 0.5, 1),
                new Vector(1, 0, 0)));
        assertEquals(2, result64.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(2 - Math.sqrt(0.75), 0.5, 1),
                        new Point(2 + Math.sqrt(0.75), 0.5, 1)),
                result64,
                "ERROR: Intersection points incorrect");

        // TC65: Ray starts on bottom rim, goes inside, hits lateral surface and exits.
        var result65 = cylinder.findIntersections(new Ray(
                new Point(1, 0, 0),
                new Vector(1, 0, 0.5)));
        assertEquals(1, result65.size(),
                "ERROR: Wrong number of intersection points (bottom rim to lateral)");
        assertEquals(List.of(
                        new Point(3, 0, 1)),
                result65,
                "ERROR: Intersection point incorrect (bottom rim to lateral)");

        // TC66: Ray starts outside, not orthogonal, no intersections (outside Z range and radius)
        assertNull(cylinder.findIntersections(new Ray(
                        new Point(0, 2, 3),
                        new Vector(1, 0, 1))),
                "ERROR: Expected null - misses cylinder");

        // TC67: Ray starts on top cap, parallel to cap, crosses lateral surface (corrected to 1 intersection)
        var result67 = cylinder.findIntersections(new Ray(
                new Point(2.5, 0, 2),
                new Vector(0, 1, 0)));
        assertEquals(1, result67.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(2.5, Math.sqrt(0.75), 2)),
                result67,
                "ERROR: Intersection point incorrect");

        // TC68: Ray starts on bottom cap, parallel to cap, crosses lateral surface (corrected to 1 intersection)
        var result68 = cylinder.findIntersections(new Ray(
                new Point(2.5, 0, 0),
                new Vector(0, 1, 0)));
        assertEquals(1, result68.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(2.5, Math.sqrt(0.75), 0)),
                result68,
                "ERROR: Intersection point incorrect");

        // TC69: Ray starts outside, enters lateral surface, exits top cap (oblique)
        var result69 = cylinder.findIntersections(new Ray(
                new Point(0, 0, 0),
                new Vector(1, 0, 1)));
        assertEquals(2, result69.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(1, 0, 1),
                        new Point(2, 0, 2)),
                result69,
                "ERROR: Intersection points incorrect");

        // TC70: Ray starts inside, exits lateral surface (oblique)
        var result70 = cylinder.findIntersections(new Ray(
                new Point(2, 0, 1),
                new Vector(1, 1, 0)));
        assertEquals(1, result70.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(2 + Math.sqrt(0.5), Math.sqrt(0.5), 1)),
                result70,
                "ERROR: Intersection point incorrect");

        // TC71: Ray starts outside, crosses lateral surface twice (oblique)
        var result71 = cylinder.findIntersections(new Ray(
                new Point(0, 0, 1),
                new Vector(1, 0.1, 0)));
        assertEquals(2, result71.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point((4 - Math.sqrt(3.88)) / 2.02, (0.1 * (4 - Math.sqrt(3.88))) / 2.02, 1),
                        new Point((4 + Math.sqrt(3.88)) / 2.02, (0.1 * (4 + Math.sqrt(3.88))) / 2.02, 1)),
                result71,
                "ERROR: Intersection points incorrect");

        // TC72: Ray starts outside, parallel to axis, just inside lateral surface
        var result72 = cylinder.findIntersections(new Ray(
                new Point(2.9999, 0, 1),
                new Vector(0, 0, 1)));
        assertEquals(1, result72.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(2.9999, 0, 2)),
                result72,
                "ERROR: Intersection point incorrect");

        // TC73 (NEW): Ray starts outside, parallel to axis, just misses the lateral surface and misses the cylinder entirely.
        assertNull(cylinder.findIntersections(new Ray(
                        new Point(3.1, 0, 1),
                        new Vector(0, 0, 1))),
                "ERROR: Expected null - just misses lateral and cylinder entirely");

        // TC74 (NEW): Ray starts inside, very close to top rim, hits top cap.
        var result74 = cylinder.findIntersections(new Ray(
                new Point(2.5, 0, 1.99),
                new Vector(0, 0, 1)));
        assertEquals(1, result74.size(),
                "ERROR: Wrong number of intersection points (inside very close to top rim)");
        assertEquals(List.of(
                        new Point(2.5, 0, 2)),
                result74,
                "ERROR: Intersection point incorrect (inside very close to top rim)");

        // TC75 (NEW): Ray starts inside, very close to bottom rim, hits bottom cap.
        var result75 = cylinder.findIntersections(new Ray(
                new Point(2.5, 0, 0.01),
                new Vector(0, 0, -1)));
        assertEquals(1, result75.size(),
                "ERROR: Wrong number of intersection points (inside very close to bottom rim)");
        assertEquals(List.of(
                        new Point(2.5, 0, 0)),
                result75,
                "ERROR: Intersection point incorrect (inside very close to bottom rim)");

        // TC76: Ray starts outside, hits top cap only (grazing lateral) -> Corrected to 2 intersections
        var result76_new = cylinder.findIntersections(new Ray(
                new Point(2, 1.5, 2),
                new Vector(0, -1, 0)));
        assertEquals(2, result76_new.size(),
                "ERROR: Wrong number of intersection points (lateral surface hits)");
        assertEquals(List.of(
                        new Point(2, 1, 2),
                        new Point(2, -1, 2)),
                result76_new,
                "ERROR: Intersection points incorrect");

        // TC77: Ray starts outside, hits bottom cap only (grazing lateral) -> Corrected to 2 intersections
        var result77 = cylinder.findIntersections(new Ray(
                new Point(2, 1.5, 0),
                new Vector(0, -1, 0)));
        assertEquals(2, result77.size(),
                "ERROR: Wrong number of intersection points (lateral surface hits)");
        assertEquals(List.of(
                        new Point(2, 1, 0),
                        new Point(2, -1, 0)),
                result77,
                "ERROR: Intersection points incorrect");

        // TC78: Ray starts inside radial projection, but outside Z-range, parallel to axis, no intersection.
        assertNull(cylinder.findIntersections(new Ray(
                        new Point(2, 0, -1),
                        new Vector(0, 0, -1))),
                "ERROR: Expected null - outside Z-range and pointing away");

        // TC79: Ray starts outside, enters lateral, exits bottom cap (oblique, different ray from TC36).
        var result79 = cylinder.findIntersections(new Ray(
                new Point(0, 0.5, 1.5),
                new Vector(1, 0, -1)));
        assertEquals(2, result79.size(),
                "ERROR: Wrong number of intersection points");
        assertEquals(List.of(
                        new Point(2 - (Math.sqrt(3) / 2), 0.5, -0.5 + (Math.sqrt(3) / 2)),
                        new Point(1.5, 0.5, 0)),
                result79,
                "ERROR: Intersection points incorrect");

        // TC80: Ray starts inside, hits lateral surface, then hits top cap
        var result80 = cylinder.findIntersections(new Ray(
                new Point(2.5, 0, 0.5),
                new Vector(-1, 0, 1)));
        assertEquals(1, result80.size(),
                "ERROR: Wrong number of intersection points (hits lateral/top cap seam)");
        assertEquals(List.of(
                        new Point(1, 0, 2)),
                result80,
                "ERROR: Intersection point incorrect");
    }
}