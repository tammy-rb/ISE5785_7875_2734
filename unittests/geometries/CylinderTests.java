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
    void testFindIntersections() {
        // Cylinder: axis ray at p0=(2,0,0), direction=(0,0,1), radius=1, height=2
        Cylinder cylinder = new Cylinder(1, new Ray(new Point(2, 0, 0), new Vector(0, 0, 1)), 2);

        // ============ Equivalence Partitions Tests ==============
        // TC01: Ray starts outside, crosses lateral surface twice
        // Ray: p=(0,0,1), d=(1,0,0). Lateral: t=1 (x=1), t=3 (x=3), z=1 in [0,2].
        // Order: t=1 < t=3.
        Ray ray01 = new Ray(new Point(0, 0, 1), new Vector(1, 0, 0));
        var result01 = cylinder.findIntersections(ray01);
        assertEquals(2, result01.size(), "TC01: Expected 2 intersections");
        assertEquals(List.of(new Point(1, 0, 1), new Point(3, 0, 1)), result01,
                "TC01: Intersection points incorrect");

        // TC02: Ray starts outside, misses cylinder
        // Ray: p=(0,2,1), d=(1,0,0). Lateral: y=2, (x-2)^2+4≠1, no cap hits.
        Ray ray02 = new Ray(new Point(0, 2, 1), new Vector(1, 0, 0));
        assertNull(cylinder.findIntersections(ray02), "TC02: Expected null - ray misses");

        // TC03: Ray starts inside, hits lateral surface once
        // Ray: p=(2.5,0,1), d=(1,0,0). Lateral: t=0.5 (x=3), z=1 in [0,2].
        Ray ray03 = new Ray(new Point(2.5, 0, 1), new Vector(1, 0, 0));
        var result03 = cylinder.findIntersections(ray03);
        assertEquals(1, result03.size(), "TC03: Expected 1 intersection");
        assertEquals(List.of(new Point(3, 0, 1)), result03, "TC03: Intersection point incorrect");

        // TC04: Ray starts outside, hits top and bottom caps
        // Ray: p=(2,0,3), d=(0,0,-1). Top cap: t=1, z=2, (x-2)^2+y^2=0≤1.
        // Bottom cap: t=3, z=0, (x-2)^2+y^2=0≤1.
        // Order: t=1 < t=3.
        Ray ray04 = new Ray(new Point(2, 0, 3), new Vector(0, 0, -1));
        var result04 = cylinder.findIntersections(ray04);
        assertEquals(2, result04.size(), "TC04: Expected 2 intersections");
        assertEquals(List.of(new Point(2, 0, 2), new Point(2, 0, 0)), result04,
                "TC04: Intersection points incorrect");

        // TC05: Ray starts outside, hits bottom and top caps
        // Ray: p=(2,0,-1), d=(0,0,1). Bottom cap: t=1, z=0, (x-2)^2+y^2=0≤1.
        // Top cap: t=3, z=2, (x-2)^2+y^2=0≤1.
        // Order: t=1 < t=3.
        Ray ray05 = new Ray(new Point(2, 0, -1), new Vector(0, 0, 1));
        var result05 = cylinder.findIntersections(ray05);
        assertEquals(2, result05.size(), "TC05: Expected 2 intersections");
        assertEquals(List.of(new Point(2, 0, 0), new Point(2, 0, 2)), result05,
                "TC05: Intersection points incorrect");

        // TC06: Ray starts inside, hits top cap
        // Ray: p=(2,0,1), d=(0,0,1). Top cap: t=1, z=2, (x-2)^2+y^2=0≤1.
        Ray ray06 = new Ray(new Point(2, 0, 1), new Vector(0, 0, 1));
        var result06 = cylinder.findIntersections(ray06);
        assertEquals(1, result06.size(), "TC06: Expected 1 intersection");
        assertEquals(List.of(new Point(2, 0, 2)), result06, "TC06: Intersection point incorrect");

        // TC07: Ray starts inside, hits bottom cap
        // Ray: p=(2,0,1), d=(0,0,-1). Bottom cap: t=1, z=0, (x-2)^2+y^2=0≤1.
        Ray ray07 = new Ray(new Point(2, 0, 1), new Vector(0, 0, -1));
        var result07 = cylinder.findIntersections(ray07);
        assertEquals(1, result07.size(), "TC07: Expected 1 intersection");
        assertEquals(List.of(new Point(2, 0, 0)), result07, "TC07: Intersection point incorrect");

        // TC08: Ray starts outside, enters bottom cap, exits top cap
        // Ray: p=(2.5,0,-1), d=(0,0,1). Bottom cap: t=1, z=0, (x-2)^2+y^2=0.25≤1.
        // Top cap: t=3, z=2, (x-2)^2+y^2=0.25≤1.
        // Order: t=1 < t=3.
        Ray ray08 = new Ray(new Point(2.5, 0, -1), new Vector(0, 0, 1));
        var result08 = cylinder.findIntersections(ray08);
        assertEquals(2, result08.size(), "TC08: Expected 2 intersections");
        assertEquals(List.of(new Point(2.5, 0, 0), new Point(2.5, 0, 2)), result08,
                "TC08: Intersection points incorrect");

        // TC09: Ray starts outside, enters lateral surface, exits top cap
        // Ray: p=(0,0,1), d=(1,0,0.5). Lateral: t=1, x=1, z=1.5 in [0,2].
        // Top cap: t=2, x=2, z=2, (x-2)^2+y^2=0≤1.
        // Order: t=1 < t=2.
        Ray ray09 = new Ray(new Point(0, 0, 1), new Vector(1, 0, 0.5));
        var result09 = cylinder.findIntersections(ray09);
        assertEquals(2, result09.size(), "TC09: Expected 2 intersections");
        assertEquals(List.of(new Point(1, 0, 1.5), new Point(2, 0, 2)), result09,
                "TC09: Intersection points incorrect");

        // TC10: Ray starts outside, enters bottom cap, exits top cap
        // Ray: p=(2,0,-1), d=(0,0,1). Bottom cap: t=1, z=0, (x-2)^2+y^2=0≤1.
        // Top cap: t=3, z=2, (x-2)^2+y^2=0≤1.
        // Order: t=1 < t=3.
        Ray ray10 = new Ray(new Point(2, 0, -1), new Vector(0, 0, 1));
        var result10 = cylinder.findIntersections(ray10);
        assertEquals(2, result10.size(), "TC10: Expected 2 intersections");
        assertEquals(List.of(new Point(2, 0, 0), new Point(2, 0, 2)), result10,
                "TC10: Intersection points incorrect");

        // =============== Boundary Values Tests ==================
        // TC11: Ray tangent to lateral surface, starts outside
        // Ray: p=(0,1,1), d=(1,0,0). Lateral: t=2, x=2, y=1, z=1 in [0,2].
        Ray ray11 = new Ray(new Point(0, 1, 1), new Vector(1, 0, 0));
        var result11 = cylinder.findIntersections(ray11);
        assertEquals(1, result11.size(), "TC11: Expected 1 intersection");
        assertEquals(List.of(new Point(2, 1, 1)), result11, "TC11: Intersection point incorrect");

        // TC12: Ray starts on lateral surface, tangent
        // Ray: p=(3,0,1), d=(0,1,0). Tangent at t=0, no further intersections.
        Ray ray12 = new Ray(new Point(3, 0, 1), new Vector(0, 1, 0));
        assertNull(cylinder.findIntersections(ray12), "TC12: Expected null - tangent from surface");

        // TC13: Ray starts on lateral surface, goes inside
        // Ray: p=(3,0,1), d=(-1,0,0). Lateral: t=2, x=1, y=0, z=1 in [0,2].
        Ray ray13 = new Ray(new Point(3, 0, 1), new Vector(-1, 0, 0));
        var result13 = cylinder.findIntersections(ray13);
        assertEquals(1, result13.size(), "TC13: Expected 1 intersection");
        assertEquals(List.of(new Point(1, 0, 1)), result13, "TC13: Intersection point incorrect");

        // TC14: Ray starts on lateral surface, goes outside
        // Ray: p=(3,0,1), d=(1,0,0). No further intersections.
        Ray ray14 = new Ray(new Point(3, 0, 1), new Vector(1, 0, 0));
        assertNull(cylinder.findIntersections(ray14), "TC14: Expected null - goes outside");

        // TC15: Ray starts on top cap, goes outside
        // Ray: p=(2,0,2), d=(0,0,1). No further intersections.
        Ray ray15 = new Ray(new Point(2, 0, 2), new Vector(0, 0, 1));
        assertNull(cylinder.findIntersections(ray15), "TC15: Expected null - goes outside");

        // TC16: Ray starts on top cap, goes inside
        // Ray: p=(2,0,2), d=(0,0,-1). Bottom cap: t=2, z=0, (x-2)^2+y^2=0≤1.
        Ray ray16 = new Ray(new Point(2, 0, 2), new Vector(0, 0, -1));
        var result16 = cylinder.findIntersections(ray16);
        assertEquals(1, result16.size(), "TC16: Expected 1 intersection");
        assertEquals(List.of(new Point(2, 0, 0)), result16, "TC16: Intersection point incorrect");

        // TC17: Ray starts on bottom cap, goes outside
        // Ray: p=(2,0,0), d=(0,0,-1). No further intersections.
        Ray ray17 = new Ray(new Point(2, 0, 0), new Vector(0, 0, -1));
        assertNull(cylinder.findIntersections(ray17), "TC17: Expected null - goes outside");

        // TC18: Ray starts on bottom cap, goes inside
        // Ray: p=(2,0,0), d=(0,0,1). Top cap: t=2, z=2, (x-2)^2+y^2=0≤1.
        Ray ray18 = new Ray(new Point(2, 0, 0), new Vector(0, 0, 1));
        var result18 = cylinder.findIntersections(ray18);
        assertEquals(1, result18.size(), "TC18: Expected 1 intersection");
        assertEquals(List.of(new Point(2, 0, 2)), result18, "TC18: Intersection point incorrect");

        // TC19: Ray on axis, starts at p0
        // Ray: p=(2,0,0), d=(0,0,1). Top cap: t=2, z=2, (x-2)^2+y^2=0≤1.
        Ray ray19 = new Ray(new Point(2, 0, 0), new Vector(0, 0, 1));
        var result19 = cylinder.findIntersections(ray19);
        assertEquals(1, result19.size(), "TC19: Expected 1 intersection");
        assertEquals(List.of(new Point(2, 0, 2)), result19, "TC19: Intersection point incorrect");

        // TC20: Ray on axis, opposite direction
        // Ray: p=(2,0,1), d=(0,0,-1). Bottom cap: t=1, z=0, (x-2)^2+y^2=0≤1.
        Ray ray20 = new Ray(new Point(2, 0, 1), new Vector(0, 0, -1));
        var result20 = cylinder.findIntersections(ray20);
        assertEquals(1, result20.size(), "TC20: Expected 1 intersection");
        assertEquals(List.of(new Point(2, 0, 0)), result20, "TC20: Intersection point incorrect");

        // TC21: Ray parallel to axis, inside cylinder
        // Ray: p=(2.5,0,1), d=(0,0,1). Top cap: t=1, z=2, (x-2)^2+y^2=0.25≤1.
        Ray ray21 = new Ray(new Point(2.5, 0, 1), new Vector(0, 0, 1));
        var result21 = cylinder.findIntersections(ray21);
        assertEquals(1, result21.size(), "TC21: Expected 1 intersection");
        assertEquals(List.of(new Point(2.5, 0, 2)), result21, "TC21: Intersection point incorrect");

        // TC22: Ray parallel to axis, outside cylinder
        // Ray: p=(4,0,1), d=(0,0,1). No intersections: (x-2)^2+y^2=4>1.
        Ray ray22 = new Ray(new Point(4, 0, 1), new Vector(0, 0, 1));
        assertNull(cylinder.findIntersections(ray22), "TC22: Expected null - outside and parallel");

        // TC23: Ray parallel to axis, on lateral surface, hits top cap rim
        // Ray: p=(3,0,1), d=(0,0,1). Top cap: t=1, x=3, y=0, z=2, (x-2)^2+y^2=1≤1.
        Ray ray23 = new Ray(new Point(3, 0, 1), new Vector(0, 0, 1));
        var result23 = cylinder.findIntersections(ray23);
        assertEquals(1, result23.size(), "TC23: Expected 1 intersection");
        assertEquals(List.of(new Point(3, 0, 2)), result23, "TC23: Intersection point incorrect");

        // TC24: Ray starts on top cap rim, goes inside
        // Ray: p=(3,0,2), d=(-1,0,-1). Bottom cap: t=3, x=1, y=0, z=0, (x-2)^2+y^2=1≤1.
        // Lateral: t=1, x=2, y=0, z=1 in [0,2].
        // Order: t=1 < t=3.
        Ray ray24 = new Ray(new Point(3, 0, 2), new Vector(-1, 0, -1));
        var result24 = cylinder.findIntersections(ray24);
        assertEquals(2, result24.size(), "TC24: Expected 2 intersections");
        assertEquals(List.of(new Point(2, 0, 1), new Point(1, 0, 0)), result24,
                "TC24: Intersection points incorrect");

        // TC25: Ray starts on bottom cap rim, goes inside
        // Ray: p=(3,0,0), d=(-1,0,1). Top cap: t=3, x=1, y=0, z=2, (x-2)^2+y^2=1≤1.
        // Lateral: t=1, x=2, y=0, z=1 in [0,2].
        // Order: t=1 < t=3.
        Ray ray25 = new Ray(new Point(3, 0, 0), new Vector(-1, 0, 1));
        var result25 = cylinder.findIntersections(ray25);
        assertEquals(2, result25.size(), "TC25: Expected 2 intersections");
        assertEquals(List.of(new Point(2, 0, 1), new Point(1, 0, 2)), result25,
                "TC25: Intersection points incorrect");

        // TC26: Ray orthogonal to axis, starts outside, intersects axis
        // Ray: p=(4,0,1), d=(-1,0,0). Lateral: t=1, x=3, y=0, z=1; t=3, x=1, y=0, z=1.
        // Order: t=1 < t=3.
        Ray ray26 = new Ray(new Point(4, 0, 1), new Vector(-1, 0, 0));
        var result26 = cylinder.findIntersections(ray26);
        assertEquals(2, result26.size(), "TC26: Expected 2 intersections");
        assertEquals(List.of(new Point(3, 0, 1), new Point(1, 0, 1)), result26,
                "TC26: Intersection points incorrect");

        // TC27: Ray orthogonal to axis, starts inside, intersects axis
        // Ray: p=(2.5,0,1), d=(-1,0,0). Lateral: t=1.5, x=1, y=0, z=1 in [0,2].
        Ray ray27 = new Ray(new Point(2.5, 0, 1), new Vector(-1, 0, 0));
        var result27 = cylinder.findIntersections(ray27);
        assertEquals(1, result27.size(), "TC27: Expected 1 intersection");
        assertEquals(List.of(new Point(1, 0, 1)), result27, "TC27: Intersection point incorrect");

        // TC28: Ray starts at p0, not parallel nor orthogonal
        // Ray: p=(2,0,0), d=(1,0,1). Lateral: t=1, x=3, y=0, z=1 in [0,2].
        Ray ray28 = new Ray(new Point(2, 0, 0), new Vector(1, 0, 1));
        var result28 = cylinder.findIntersections(ray28);
        assertEquals(1, result28.size(), "TC28: Expected 1 intersection");
        assertEquals(List.of(new Point(3, 0, 1)), result28, "TC28: Intersection point incorrect");

        // TC29: Ray starts outside, tangent to top cap rim
        // Ray: p=(3,0,3), d=(0,0,-1). Top cap: t=1, x=3, y=0, z=2, (x-2)^2+y^2=1≤1.
        Ray ray29 = new Ray(new Point(3, 0, 3), new Vector(0, 0, -1));
        var result29 = cylinder.findIntersections(ray29);
        assertEquals(1, result29.size(), "TC29: Expected 1 intersection");
        assertEquals(List.of(new Point(3, 0, 2)), result29, "TC29: Intersection point incorrect");

        // TC30: Ray starts outside, tangent to bottom cap rim
        // Ray: p=(3,0,-1), d=(0,0,1). Bottom cap: t=1, x=3, y=0, z=0, (x-2)^2+y^2=1≤1.
        Ray ray30 = new Ray(new Point(3, 0, -1), new Vector(0, 0, 1));
        var result30 = cylinder.findIntersections(ray30);
        assertEquals(1, result30.size(), "TC30: Expected 1 intersection");
        assertEquals(List.of(new Point(3, 0, 0)), result30, "TC30: Intersection point incorrect");

        // TC31: Ray starts on top cap center, goes outside
        // Ray: p=(2,0,2), d=(0,1,1). No intersections: leaves top cap outward.
        Ray ray31 = new Ray(new Point(2, 0, 2), new Vector(0, 1, 1));
        assertNull(cylinder.findIntersections(ray31), "TC31: Expected null - goes outside");

        // TC32: Ray starts on bottom cap center, goes outside
        // Ray: p=(2,0,0), d=(0,1,-1). No intersections: leaves bottom cap outward.
        Ray ray32 = new Ray(new Point(2, 0, 0), new Vector(0, 1, -1));
        assertNull(cylinder.findIntersections(ray32), "TC32: Expected null - goes outside");

        // TC33: Ray starts outside, enters lateral surface, exits lateral surface
        // Ray: p=(0,0,3), d=(1,0,-1). Lateral: t=1, x=1, y=0, z=2; t=3, x=3, y=0, z=0.
        // Order: t=1 < t=3.
        Ray ray33 = new Ray(new Point(0, 0, 3), new Vector(1, 0, -1));
        var result33 = cylinder.findIntersections(ray33);
        assertEquals(2, result33.size(), "TC33: Expected 2 intersections");
        assertEquals(List.of(new Point(1, 0, 2), new Point(3, 0, 0)), result33,
                "TC33: Intersection points incorrect");

        // TC34: Ray starts inside, exits top cap rim
        // Ray: p=(2.5,0,1.5), d=(1,0,-1). Top cap: t=0.5, x=3, y=0, z=1, (x-2)^2+y^2=1≤1.
        Ray ray34 = new Ray(new Point(2.5, 0, 1.5), new Vector(1, 0, -1));
        var result34 = cylinder.findIntersections(ray34);
        assertEquals(1, result34.size(), "TC34: Expected 1 intersection");
        assertEquals(List.of(new Point(3, 0, 1)), result34, "TC34: Intersection point incorrect");

        // TC35: Ray starts outside, enters bottom cap, exits top cap
        // Ray: p=(2.5,0,-1), d=(0,0,2). Bottom cap: t=0.5, z=0, (x-2)^2+y^2=0.25≤1.
        // Top cap: t=1.5, z=2, (x-2)^2+y^2=0.25≤1.
        // Order: t=0.5 < t=1.5.
        Ray ray35 = new Ray(new Point(2.5, 0, -1), new Vector(0, 0, 2));
        var result35 = cylinder.findIntersections(ray35);
        assertEquals(2, result35.size(), "TC35: Expected 2 intersections");
        assertEquals(List.of(new Point(2.5, 0, 0), new Point(2.5, 0, 2)), result35,
                "TC35: Intersection points incorrect");

        // TC36: Ray starts outside, enters lateral surface, exits bottom cap
        // Ray: p=(0,0,1), d=(1,0,-0.5). Lateral: t=1, x=1, y=0, z=0.5 in [0,2].
        // Bottom cap: t=2, x=2, y=0, z=0, (x-2)^2+y^2=0≤1.
        // Order: t=1 < t=2.
        Ray ray36 = new Ray(new Point(0, 0, 1), new Vector(1, 0, -0.5));
        var result36 = cylinder.findIntersections(ray36);
        assertEquals(2, result36.size(), "TC36: Expected 2 intersections");
        assertEquals(List.of(new Point(1, 0, 0.5), new Point(2, 0, 0)), result36,
                "TC36: Intersection points incorrect");

        // TC37: Ray starts on lateral surface at z=0, goes inside
        // Ray: p=(1,0,0), d=(1,0,1). Lateral: t=2, x=3, y=0, z=2 in [0,2].
        Ray ray37 = new Ray(new Point(1, 0, 0), new Vector(1, 0, 1));
        var result37 = cylinder.findIntersections(ray37);
        assertEquals(1, result37.size(), "TC37: Expected 1 intersection");
        assertEquals(List.of(new Point(3, 0, 2)), result37, "TC37: Intersection point incorrect");

        // TC38: Ray starts on lateral surface at z=2, goes inside
        // Ray: p=(1,0,2), d=(1,0,-1). Lateral: t=2, x=3, y=0, z=0 in [0,2].
        Ray ray38 = new Ray(new Point(1, 0, 2), new Vector(1, 0, -1));
        var result38 = cylinder.findIntersections(ray38);
        assertEquals(1, result38.size(), "TC38: Expected 1 intersection");
        assertEquals(List.of(new Point(3, 0, 0)), result38, "TC38: Intersection point incorrect");

        // TC39: Ray starts outside, orthogonal to axis, no intersections
        // Ray: p=(4,0,3), d=(-1,0,0). No intersections: z=3 not in [0,2].
        Ray ray39 = new Ray(new Point(4, 0, 3), new Vector(-1, 0, 0));
        assertNull(cylinder.findIntersections(ray39), "TC39: Expected null - misses cylinder");

        // TC40: Ray starts on top cap rim, tangent to cap
        // Ray: p=(3,0,2), d=(0,1,0). No intersections: travels along top cap plane.
        Ray ray40 = new Ray(new Point(3, 0, 2), new Vector(0, 1, 0));
        assertNull(cylinder.findIntersections(ray40), "TC40: Expected null - tangent to cap");

        // TC41: Ray starts on bottom cap rim, tangent to cap
        // Ray: p=(3,0,0), d=(0,1,0). No intersections: travels along bottom cap plane.
        Ray ray41 = new Ray(new Point(3, 0, 0), new Vector(0, 1, 0));
        assertNull(cylinder.findIntersections(ray41), "TC41: Expected null - tangent to cap");

        // TC42: Ray starts outside, hits top cap edge
        // Ray: p=(3,0,3), d=(0,0,-1). Top cap: t=1, x=3, y=0, z=2, (x-2)^2+y^2=1≤1.
        Ray ray42 = new Ray(new Point(3, 0, 3), new Vector(0, 0, -1));
        var result42 = cylinder.findIntersections(ray42);
        assertEquals(1, result42.size(), "TC42: Expected 1 intersection");
        assertEquals(List.of(new Point(3, 0, 2)), result42, "TC42: Intersection point incorrect");

        // TC43: Ray starts outside, hits bottom cap edge and top cap
        // Ray: p=(3,0,-1), d=(0,0,1). Bottom cap: t=1, x=3, y=0, z=0, (x-2)^2+y^2=1≤1.
        // Top cap: t=3, x=3, y=0, z=2, (x-2)^2+y^2=1≤1.
        // Order: t=1 < t=3.
        Ray ray43 = new Ray(new Point(3, 0, -1), new Vector(0, 0, 1));
        var result43 = cylinder.findIntersections(ray43);
        assertEquals(2, result43.size(), "TC43: Expected 2 intersections");
        assertEquals(List.of(new Point(3, 0, 0), new Point(3, 0, 2)), result43,
                "TC43: Intersection points incorrect");

        // TC44: Ray starts on axis, not at p0, goes along axis
        // Ray: p=(2,0,1), d=(0,0,1). Top cap: t=1, x=2, y=0, z=2, (x-2)^2+y^2=0≤1.
        Ray ray44 = new Ray(new Point(2, 0, 1), new Vector(0, 0, 1));
        var result44 = cylinder.findIntersections(ray44);
        assertEquals(1, result44.size(), "TC44: Expected 1 intersection");
        assertEquals(List.of(new Point(2, 0, 2)), result44, "TC44: Intersection point incorrect");

        // TC45: Ray starts outside, enters bottom cap, exits lateral surface
        // Ray: p=(1,0,-1), d=(1,0,1). Bottom cap: t=1, x=2, y=0, z=0, (x-2)^2+y^2=0≤1.
        // Lateral: t=2, x=3, y=0, z=1 in [0,2].
        // Order: t=1 < t=2.
        Ray ray45 = new Ray(new Point(1, 0, -1), new Vector(1, 0, 1));
        var result45 = cylinder.findIntersections(ray45);
        assertEquals(2, result45.size(), "TC45: Expected 2 intersections");
        assertEquals(List.of(new Point(2, 0, 0), new Point(3, 0, 1)), result45,
                "TC45: Intersection points incorrect");

        // TC46: Ray starts inside, hits bottom cap
        // Ray: p=(2.5,0,1), d=(0,0,-1). Bottom cap: t=1, x=2.5, y=0, z=0, (x-2)^2+y^2=0.25≤1.
        Ray ray46 = new Ray(new Point(2.5, 0, 1), new Vector(0, 0, -1));
        var result46 = cylinder.findIntersections(ray46);
        assertEquals(1, result46.size(), "TC46: Expected 1 intersection");
        assertEquals(List.of(new Point(2.5, 0, 0)), result46, "TC46: Intersection point incorrect");

        // TC47: Ray starts inside, hits top cap
        // Ray: p=(2.5,0,1), d=(0,0,1). Top cap: t=1, x=2.5, y=0, z=2, (x-2)^2+y^2=0.25≤1.
        Ray ray47 = new Ray(new Point(2.5, 0, 1), new Vector(0, 0, 1));
        var result47 = cylinder.findIntersections(ray47);
        assertEquals(1, result47.size(), "TC47: Expected 1 intersection");
        assertEquals(List.of(new Point(2.5, 0, 2)), result47, "TC47: Intersection point incorrect");

        // TC48: Ray starts outside, enters lateral surface, exits lateral surface
        // Ray: p=(0,0,1), d=(1,0,0). Lateral: t=1, x=1, y=0, z=1; t=3, x=3, y=0, z=1.
        // Order: t=1 < t=3.
        Ray ray48 = new Ray(new Point(0, 0, 1), new Vector(1, 0, 0));
        var result48 = cylinder.findIntersections(ray48);
        assertEquals(2, result48.size(), "TC48: Expected 2 intersections");
        assertEquals(List.of(new Point(1, 0, 1), new Point(3, 0, 1)), result48,
                "TC48: Intersection points incorrect");

        // TC49: Ray starts on top cap, orthogonal to cap
        // Ray: p=(2.5,0,2), d=(0,0,-1). Bottom cap: t=2, x=2.5, y=0, z=0, (x-2)^2+y^2=0.25≤1.
        Ray ray49 = new Ray(new Point(2.5, 0, 2), new Vector(0, 0, -1));
        var result49 = cylinder.findIntersections(ray49);
        assertEquals(1, result49.size(), "TC49: Expected 1 intersection");
        assertEquals(List.of(new Point(2.5, 0, 0)), result49, "TC49: Intersection point incorrect");

        // TC50: Ray starts on bottom cap, orthogonal to cap
        // Ray: p=(2.5,0,0), d=(0,0,1). Top cap: t=2, x=2.5, y=0, z=2, (x-2)^2+y^2=0.25≤1.
        Ray ray50 = new Ray(new Point(2.5, 0, 0), new Vector(0, 0, 1));
        var result50 = cylinder.findIntersections(ray50);
        assertEquals(1, result50.size(), "TC50: Expected 1 intersection");
        assertEquals(List.of(new Point(2.5, 0, 2)), result50, "TC50: Intersection point incorrect");

        // TC51: Ray starts outside, enters bottom cap, exits lateral surface
        // Ray: p=(1.5,0,-1), d=(1,0,1). Bottom cap: t=1, x=2.5, y=0, z=0, (x-2)^2+y^2=0.25≤1.
        // Lateral: t=1.5, x=3, y=0, z=0.5 in [0,2].
        // Order: t=1 < t=1.5.
        Ray ray51 = new Ray(new Point(1.5, 0, -1), new Vector(1, 0, 1));
        var result51 = cylinder.findIntersections(ray51);
        assertEquals(2, result51.size(), "TC51: Expected 2 intersections");
        assertEquals(List.of(new Point(2.5, 0, 0), new Point(3, 0, 0.5)), result51,
                "TC51: Intersection points incorrect");

        // TC52: Ray starts outside, enters top cap, exits bottom cap
        // Ray: p=(2,0,3), d=(0,0,-1). Top cap: t=1, x=2, y=0, z=2, (x-2)^2+y^2=0≤1.
        // Bottom cap: t=3, x=2, y=0, z=0, (x-2)^2+y^2=0≤1.
        // Order: t=1 < t=3.
        Ray ray52 = new Ray(new Point(2, 0, 3), new Vector(0, 0, -1));
        var result52 = cylinder.findIntersections(ray52);
        assertEquals(2, result52.size(), "TC52: Expected 2 intersections");
        assertEquals(List.of(new Point(2, 0, 2), new Point(2, 0, 0)), result52,
                "TC52: Intersection points incorrect");

        // TC53: Ray starts on lateral surface, goes inside, hits bottom cap
        // Ray: p=(3,0,1), d=(-1,0,-1). Bottom cap: t=2, x=2, y=0, z=0, (x-2)^2+y^2=0≤1.
        // Lateral: t=3, x=1, y=0, z=2 in [0,2].
        // Order: t=2 < t=3.
        Ray ray53 = new Ray(new Point(3, 0, 1), new Vector(-1, 0, -1));
        var result53 = cylinder.findIntersections(ray53);
        assertEquals(2, result53.size(), "TC53: Expected 2 intersections");
        assertEquals(List.of(new Point(2, 0, 0), new Point(1, 0, 2)), result53,
                "TC53: Intersection points incorrect");

        // TC54: Ray starts on lateral surface, goes inside, hits top cap
        // Ray: p=(3,0,1), d=(-1,0,1). Top cap: t=2, x=2, y=0, z=2, (x-2)^2+y^2=0≤1.
        // Lateral: t=3, x=1, y=0, z=0 in [0,2].
        // Order: t=2 < t=3.
        Ray ray54 = new Ray(new Point(3, 0, 1), new Vector(-1, 0, 1));
        var result54 = cylinder.findIntersections(ray54);
        assertEquals(2, result54.size(), "TC54: Expected 2 intersections");
        assertEquals(List.of(new Point(2, 0, 2), new Point(1, 0, 0)), result54,
                "TC54: Intersection points incorrect");

        // TC55: Ray starts outside, hits lateral surface at z=0
        // Ray: p=(0,0,0), d=(1,0,0). Lateral: t=1, x=1, y=0, z=0; t=3, x=3, y=0, z=0.
        // Order: t=1 < t=3.
        Ray ray55 = new Ray(new Point(0, 0, 0), new Vector(1, 0, 0));
        var result55 = cylinder.findIntersections(ray55);
        assertEquals(2, result55.size(), "TC55: Expected 2 intersections");
        assertEquals(List.of(new Point(1, 0, 0), new Point(3, 0, 0)), result55,
                "TC55: Intersection points incorrect");

        // TC56: Ray starts outside, hits lateral surface at z=2
        // Ray: p=(0,0,2), d=(1,0,0). Lateral: t=1, x=1, y=0, z=2; t=3, x=3, y=0, z=2.
        // Order: t=1 < t=3.
        Ray ray56 = new Ray(new Point(0, 0, 2), new Vector(1, 0, 0));
        var result56 = cylinder.findIntersections(ray56);
        assertEquals(2, result56.size(), "TC56: Expected 2 intersections");
        assertEquals(List.of(new Point(1, 0, 2), new Point(3, 0, 2)), result56,
                "TC56: Intersection points incorrect");

        // TC57: Ray starts on top cap, not orthogonal, goes inside
        // Ray: p=(2.5,0,2), d=(-1,0,-1). Lateral: t=1, x=1.5, y=0, z=1 in [0,2].
        Ray ray57 = new Ray(new Point(2.5, 0, 2), new Vector(-1, 0, -1));
        var result57 = cylinder.findIntersections(ray57);
        assertEquals(1, result57.size(), "TC57: Expected 1 intersection");
        assertEquals(List.of(new Point(1.5, 0, 1)), result57, "TC57: Intersection point incorrect");

        // TC58: Ray starts on bottom cap, not orthogonal, goes inside
        // Ray: p=(2.5,0,0), d=(-1,0,1). Lateral: t=1, x=1.5, y=0, z=1 in [0,2].
        Ray ray58 = new Ray(new Point(2.5, 0, 0), new Vector(-1, 0, 1));
        var result58 = cylinder.findIntersections(ray58);
        assertEquals(1, result58.size(), "TC58: Expected 1 intersection");
        assertEquals(List.of(new Point(1.5, 0, 1)), result58, "TC58: Intersection point incorrect");

        // TC59: Ray starts outside, enters bottom cap, exits top cap
        // Ray: p=(2.5,0,-1), d=(0,0,1). Bottom cap: t=1, z=0, (x-2)^2+y^2=0.25≤1.
        // Top cap: t=3, z=2, (x-2)^2+y^2=0.25≤1.
        // Order: t=1 < t=3.
        Ray ray59 = new Ray(new Point(2.5, 0, -1), new Vector(0, 0, 1));
        var result59 = cylinder.findIntersections(ray59);
        assertEquals(2, result59.size(), "TC59: Expected 2 intersections");
        assertEquals(List.of(new Point(2.5, 0, 0), new Point(2.5, 0, 2)), result59,
                "TC59: Intersection points incorrect");

        // TC60: Ray starts outside, enters top cap, exits bottom cap
        // Ray: p=(2.5,0,3), d=(0,0,-1). Top cap: t=1, z=2, (x-2)^2+y^2=0.25≤1.
        // Bottom cap: t=3, z=0, (x-2)^2+y^2=0.25≤1.
        // Order: t=1 < t=3.
        Ray ray60 = new Ray(new Point(2.5, 0, 3), new Vector(0, 0, -1));
        var result60 = cylinder.findIntersections(ray60);
        assertEquals(2, result60.size(), "TC60: Expected 2 intersections");
        assertEquals(List.of(new Point(2.5, 0, 2), new Point(2.5, 0, 0)), result60,
                "TC60: Intersection points incorrect");

        // TC61: Ray starts on axis, orthogonal to axis
        // Ray: p=(2,0,1), d=(1,0,0). Lateral: t=1, x=3, y=0, z=1 in [0,2].
        Ray ray61 = new Ray(new Point(2, 0, 1), new Vector(1, 0, 0));
        var result61 = cylinder.findIntersections(ray61);
        assertEquals(1, result61.size(), "TC61: Expected 1 intersection");
        assertEquals(List.of(new Point(3, 0, 1)), result61, "TC61: Intersection point incorrect");

        // TC62: Ray starts on axis at z=2, orthogonal to axis
        // Ray: p=(2,0,2), d=(1,0,0). Lateral: t=1, x=3, y=0, z=2 in [0,2].
        Ray ray62 = new Ray(new Point(2, 0, 2), new Vector(1, 0, 0));
        var result62 = cylinder.findIntersections(ray62);
        assertEquals(1, result62.size(), "TC62: Expected 1 intersection");
        assertEquals(List.of(new Point(3, 0, 2)), result62, "TC62: Intersection point incorrect");

        // TC63: Ray starts on axis at z=0, orthogonal to axis
        // Ray: p=(2,0,0), d=(1,0,0). Lateral: t=1, x=3, y=0, z=0 in [0,2].
        Ray ray63 = new Ray(new Point(2, 0, 0), new Vector(1, 0, 0));
        var result63 = cylinder.findIntersections(ray63);
        assertEquals(1, result63.size(), "TC63: Expected 1 intersection");
        assertEquals(List.of(new Point(3, 0, 0)), result63, "TC63: Intersection point incorrect");

        // TC64: Ray starts outside, orthogonal to axis, crosses lateral surface
        // Ray: p=(0,1,1), d=(1,0,0). Lateral: t=1, x=1, y=1, z=1; t=3, x=3, y=1, z=1.
        // Order: t=1 < t=3.
        Ray ray64 = new Ray(new Point(0, 1, 1), new Vector(1, 0, 0));
        var result64 = cylinder.findIntersections(ray64);
        assertEquals(2, result64.size(), "TC64: Expected 2 intersections");
        assertEquals(List.of(new Point(1, 1, 1), new Point(3, 1, 1)), result64,
                "TC64: Intersection points incorrect");

        // TC65: Ray starts inside, orthogonal to axis, hits lateral surface
        // Ray: p=(1.5,0,1), d=(1,0,0). Lateral: t=1.5, x=3, y=0, z=1 in [0,2].
        Ray ray65 = new Ray(new Point(1.5, 0, 1), new Vector(1, 0, 0));
        var result65 = cylinder.findIntersections(ray65);
        assertEquals(1, result65.size(), "TC65: Expected 1 intersection");
        assertEquals(List.of(new Point(3, 0, 1)), result65, "TC65: Intersection point incorrect");

        // TC66: Ray starts outside, not orthogonal, no intersections
        // Ray: p=(0,2,3), d=(1,0,1). No intersections: y=2, (x-2)^2+4>1, z>2.
        Ray ray66 = new Ray(new Point(0, 2, 3), new Vector(1, 0, 1));
        assertNull(cylinder.findIntersections(ray66), "TC66: Expected null - misses cylinder");

        // TC67: Ray starts on top cap, parallel to cap
        // Ray: p=(2.5,0,2), d=(0,1,0). No intersections: travels along top cap plane.
        Ray ray67 = new Ray(new Point(2.5, 0, 2), new Vector(0, 1, 0));
        assertNull(cylinder.findIntersections(ray67), "TC67: Expected null - parallel to cap");

        // TC68: Ray starts on bottom cap, parallel to cap
        // Ray: p=(2.5,0,0), d=(0,1,0). No intersections: travels along bottom cap plane.
        Ray ray68 = new Ray(new Point(2.5, 0, 0), new Vector(0, 1, 0));
        assertNull(cylinder.findIntersections(ray68), "TC68: Expected null - parallel to cap");

        // TC69: Ray starts outside, enters lateral surface, exits top cap
        // Ray: p=(0,0,0), d=(1,0,1). Lateral: t=1, x=1, y=0, z=1 in [0,2].
        // Top cap: t=2, x=2, y=0, z=2, (x-2)^2+y^2=0≤1.
        // Order: t=1 < t=2.
        Ray ray69 = new Ray(new Point(0, 0, 0), new Vector(1, 0, 1));
        var result69 = cylinder.findIntersections(ray69);
        assertEquals(2, result69.size(), "TC69: Expected 2 intersections");
        assertEquals(List.of(new Point(1, 0, 1), new Point(2, 0, 2)), result69,
                "TC69: Intersection points incorrect");

        // TC70: Ray starts inside, exits lateral surface
        // Ray: p=(1.5,0,1), d=(1,0,1). Lateral: t=0.5, x=2, y=0, z=1.5 in [0,2].
        Ray ray70 = new Ray(new Point(1.5, 0, 1), new Vector(1, 0, 1));
        var result70 = cylinder.findIntersections(ray70);
        assertEquals(1, result70.size(), "TC70: Expected 1 intersection");
        assertEquals(List.of(new Point(2, 0, 1.5)), result70, "TC70: Intersection point incorrect");

        // TC71: Ray starts outside, enters bottom cap, exits top cap
        // Ray: p=(2.5,0,-1), d=(0,0,1). Bottom cap: t=1, z=0, (x-2)^2+y^2=0.25≤1.
        // Top cap: t=3, z=2, (x-2)^2+y^2=0.25≤1.
        // Order: t=1 < t=3.
        Ray ray71 = new Ray(new Point(2.5, 0, -1), new Vector(0, 0, 1));
        var result71 = cylinder.findIntersections(ray71);
        assertEquals(2, result71.size(), "TC71: Expected 2 intersections");
        assertEquals(List.of(new Point(2.5, 0, 0), new Point(2.5, 0, 2)), result71,
                "TC71: Intersection points incorrect");

        // TC72: Ray starts outside, enters top cap, exits bottom cap
        // Ray: p=(2.5,0,3), d=(0,0,-1). Top cap: t=1, z=2, (x-2)^2+y^2=0.25≤1.
        // Bottom cap: t=3, z=0, (x-2)^2+y^2=0.25≤1.
        // Order: t=1 < t=3.
        Ray ray72 = new Ray(new Point(2.5, 0, 3), new Vector(0, 0, -1));
        var result72 = cylinder.findIntersections(ray72);
        assertEquals(2, result72.size(), "TC72: Expected 2 intersections");
        assertEquals(List.of(new Point(2.5, 0, 2), new Point(2.5, 0, 0)), result72,
                "TC72: Intersection points incorrect");

        // TC73: Ray starts on lateral surface, goes inside
        // Ray: p=(3,0,1), d=(-1,0,0). Lateral: t=2, x=1, y=0, z=1 in [0,2].
        Ray ray73 = new Ray(new Point(3, 0, 1), new Vector(-1, 0, 0));
        var result73 = cylinder.findIntersections(ray73);
        assertEquals(1, result73.size(), "TC73: Expected 1 intersection");
        assertEquals(List.of(new Point(1, 0, 1)), result73, "TC73: Intersection point incorrect");

        // TC74: Ray starts on top cap rim, goes outside
        // Ray: p=(3,0,2), d=(1,0,1). No intersections: leaves top cap outward.
        Ray ray74 = new Ray(new Point(3, 0, 2), new Vector(1, 0, 1));
        assertNull(cylinder.findIntersections(ray74), "TC74: Expected null - goes outside");

        // TC75: Ray starts on bottom cap rim, goes outside
        // Ray: p=(3,0,0), d=(1,0,-1). No intersections: leaves bottom cap outward.
        Ray ray75 = new Ray(new Point(3, 0, 0), new Vector(1, 0, -1));
        assertNull(cylinder.findIntersections(ray75), "TC75: Expected null - goes outside");

        // TC76: Ray starts outside, enters lateral surface, exits top cap
        // Ray: p=(0,0,1), d=(1,0,0.5). Lateral: t=1, x=1, y=0, z=1.5 in [0,2].
        // Top cap: t=2, x=2, y=0, z=2, (x-2)^2+y^2=0≤1.
        // Order: t=1 < t=2.
        Ray ray76 = new Ray(new Point(0, 0, 1), new Vector(1, 0, 0.5));
        var result76 = cylinder.findIntersections(ray76);
        assertEquals(2, result76.size(), "TC76: Expected 2 intersections");
        assertEquals(List.of(new Point(1, 0, 1.5), new Point(2, 0, 2)), result76,
                "TC76: Intersection points incorrect");

        // TC77: Ray starts outside, enters lateral surface, exits bottom cap
        // Ray: p=(0,0,1), d=(1,0,-0.5). Lateral: t=1, x=1, y=0, z=0.5 in [0,2].
        // Bottom cap: t=2, x=2, y=0, z=0, (x-2)^2+y^2=0≤1.
        // Order: t=1 < t=2.
        Ray ray77 = new Ray(new Point(0, 0, 1), new Vector(1, 0, -0.5));
        var result77 = cylinder.findIntersections(ray77);
        assertEquals(2, result77.size(), "TC77: Expected 2 intersections");
        assertEquals(List.of(new Point(1, 0, 0.5), new Point(2, 0, 0)), result77,
                "TC77: Intersection points incorrect");

        // TC78: Ray starts inside, exits lateral surface
        // Ray: p=(2.5,0,1), d=(1,0,0). Lateral: t=0.5, x=3, y=0, z=1 in [0,2].
        Ray ray78 = new Ray(new Point(2.5, 0, 1), new Vector(1, 0, 0));
        var result78 = cylinder.findIntersections(ray78);
        assertEquals(1, result78.size(), "TC78: Expected 1 intersection");
        assertEquals(List.of(new Point(3, 0, 1)), result78, "TC78: Intersection point incorrect");

        // TC79: Ray starts outside, enters bottom cap, exits top cap
        // Ray: p=(2,0,-1), d=(0,0,1). Bottom cap: t=1, x=2, y=0, z=0, (x-2)^2+y^2=0≤1.
        // Top cap: t=3, x=2, y=0, z=2, (x-2)^2+y^2=0≤1.
        // Order: t=1 < t=3.
        Ray ray79 = new Ray(new Point(2, 0, -1), new Vector(0, 0, 1));
        var result79 = cylinder.findIntersections(ray79);
        assertEquals(2, result79.size(), "TC79: Expected 2 intersections");
        assertEquals(List.of(new Point(2, 0, 0), new Point(2, 0, 2)), result79,
                "TC79: Intersection points incorrect");

        // TC80: Ray starts outside, enters top cap, exits bottom cap
        // Ray: p=(2,0,3), d=(0,0,-1). Top cap: t=1, x=2, y=0, z=2, (x-2)^2+y^2=0≤1.
        // Bottom cap: t=3, x=2, y=0, z=0, (x-2)^2+y^2=0≤1.
        // Order: t=1 < t=3.
        Ray ray80 = new Ray(new Point(2, 0, 3), new Vector(0, 0, -1));
        var result80 = cylinder.findIntersections(ray80);
        assertEquals(2, result80.size(), "TC80: Expected 2 intersections");
        assertEquals(List.of(new Point(2, 0, 2), new Point(2, 0, 0)), result80,
                "TC80: Intersection points incorrect");

        // TC81: Ray starts outside, intersects lateral surface and top cap
        // Ray: p=(0,0,-1), d=(1,0,2). Lateral: t=1, x=1, y=0, z=1 in [0,2].
        // Top cap: t=1.5, x=1.5, y=0, z=2, (x-2)^2+y^2=0.25≤1.
        // Order: t=1 < t=1.5.
        Ray ray81 = new Ray(new Point(0, 0, -1), new Vector(1, 0, 2));
        var result81 = cylinder.findIntersections(ray81);
        assertEquals(2, result81.size(), "TC81: Expected 2 intersections");
        assertEquals(List.of(new Point(1, 0, 1), new Point(1.5, 0, 2)), result81,
                "TC81: Intersection points incorrect");

        // TC82: Ray starts inside, tangent to lateral surface at z=1
        // Ray: p=(2,0,1), d=(0,1,0). Lateral: t=1, x=2, y=1, z=1 in [0,2].
        Ray ray82 = new Ray(new Point(2, 0, 1), new Vector(0, 1, 0));
        var result82 = cylinder.findIntersections(ray82);
        assertEquals(1, result82.size(), "TC82: Expected 1 intersection");
        assertEquals(List.of(new Point(2, 1, 1)), result82, "TC82: Intersection point incorrect");

        // TC83: Ray starts outside, intersects cylinder axis and bottom cap
        // Ray: p=(0,0,-1), d=(2,0,1). Bottom cap: t=1, x=2, y=0, z=0, (x-2)^2+y^2=0≤1.
        // Lateral: t=1.5, x=3, y=0, z=0.5 in [0,2].
        // Order: t=1 < t=1.5.
        Ray ray83 = new Ray(new Point(0, 0, -1), new Vector(2, 0, 1));
        var result83 = cylinder.findIntersections(ray83);
        assertEquals(2, result83.size(), "TC83: Expected 2 intersections");
        assertEquals(List.of(new Point(2, 0, 0), new Point(3, 0, 0.5)), result83,
                "TC83: Intersection points incorrect");

        // TC84: Ray starts on lateral surface, intersects axis
        // Ray: p=(3,0,1), d=(-1,0,0). Lateral: t=2, x=1, y=0, z=1 in [0,2].
        Ray ray84 = new Ray(new Point(3, 0, 1), new Vector(-1, 0, 0));
        var result84 = cylinder.findIntersections(ray84);
        assertEquals(1, result84.size(), "TC84: Expected 1 intersection");
        assertEquals(List.of(new Point(1, 0, 1)), result84, "TC84: Intersection point incorrect");

        // TC85: Ray starts near top cap rim, intersects bottom cap
        // Ray: p=(2.9,0,2), d=(-1,0,-2). Bottom cap: t=1, x=1.9, y=0, z=0, (x-2)^2+y^2=0.01≤1.
        Ray ray85 = new Ray(new Point(2.9, 0, 2), new Vector(-1, 0, -2));
        var result85 = cylinder.findIntersections(ray85);
        assertEquals(1, result85.size(), "TC85: Expected 1 intersection");
        assertEquals(List.of(new Point(1.9, 0, 0)), result85, "TC85: Intersection point incorrect");

        // TC86: Ray starts outside, parallel to axis, misses cylinder
        // Ray: p=(4,1,0), d=(0,0,1). No intersections: (x-2)^2+y^2=5>1.
        Ray ray86 = new Ray(new Point(4, 1, 0), new Vector(0, 0, 1));
        assertNull(cylinder.findIntersections(ray86), "TC86: Expected null - misses cylinder");

        // TC87: Ray starts on bottom cap, orthogonal to axis, misses lateral
        // Ray: p=(2.5,0,0), d=(0,1,0). No intersections: travels along bottom cap plane.
        Ray ray87 = new Ray(new Point(2.5, 0, 0), new Vector(0, 1, 0));
        assertNull(cylinder.findIntersections(ray87), "TC87: Expected null - along cap");

        // TC88: Ray starts outside, tangent to lateral surface, non-zero y
        // Ray: p=(0,1,1), d=(1,0,0). Lateral: t=2, x=2, y=1, z=1 in [0,2].
        Ray ray88 = new Ray(new Point(0, 1, 1), new Vector(1, 0, 0));
        var result88 = cylinder.findIntersections(ray88);
        assertEquals(1, result88.size(), "TC88: Expected 1 intersection");
        assertEquals(List.of(new Point(2, 1, 1)), result88, "TC88: Intersection point incorrect");

        // TC89: Ray starts on top cap, intersects lateral surface
        // Ray: p=(2.5,0,2), d=(1,0,-1). Lateral: t=0.5, x=3, y=0, z=1.5 in [0,2].
        Ray ray89 = new Ray(new Point(2.5, 0, 2), new Vector(1, 0, -1));
        var result89 = cylinder.findIntersections(ray89);
        assertEquals(1, result89.size(), "TC89: Expected 1 intersection");
        assertEquals(List.of(new Point(3, 0, 1.5)), result89, "TC89: Intersection point incorrect");

        // TC90: Ray starts outside, intersects both caps, no lateral
        // Ray: p=(2.2,0,-1), d=(0,0,2). Bottom cap: t=0.5, x=2.2, y=0, z=0, (x-2)^2+y^2=0.04≤1.
        // Top cap: t=1.5, x=2.2, y=0, z=2, (x-2)^2+y^2=0.04≤1.
        // Order: t=0.5 < t=1.5.
        Ray ray90 = new Ray(new Point(2.2, 0, -1), new Vector(0, 0, 2));
        var result90 = cylinder.findIntersections(ray90);
        assertEquals(2, result90.size(), "TC90: Expected 2 intersections");
        assertEquals(List.of(new Point(2.2, 0, 0), new Point(2.2, 0, 2)), result90,
                "TC90: Intersection points incorrect");
    }
}