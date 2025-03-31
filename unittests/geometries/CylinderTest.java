package geometries;

import org.junit.jupiter.api.Test;
import primitives.*;

import static org.junit.jupiter.api.Assertions.*;

class CylinderTest {

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

}