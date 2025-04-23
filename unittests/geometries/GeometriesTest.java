package geometries;

import org.junit.jupiter.api.Test;
import primitives.*;

import static org.junit.jupiter.api.Assertions.*;

class GeometriesTest {

    @Test
    void testAdd() {
        Geometries geometries = new Geometries();
        Plane plane = new Plane(
                new Point(1, 0, 0),
                new Point(0, 1, 0),
                new Point(1, 1, 0));
        geometries.add(plane);
        assertEquals(1, geometries.getGeometriesSize(),
                "Error: Adding a geometry to the collection doesn't work");
    }

    @Test
    void testFindIntersections() {
        Geometries geometries = new Geometries();
        geometries.add(new Plane(
                        new Point(1, 0, 0),
                        new Point(0, 1, 0),
                        new Point(1, 1, 0)),
                new Triangle(
                        new Point(1, 0, 0),
                        new Point(1, 0, 3),
                        new Point(3, 0, 0)),
                new Sphere(
                        1,
                        new Point(0, 2, 0)));

        // ============ Equivalence Partitions Tests ==============
        // TC01: Ray intersects part of the geometries (sphere and triangle)
        Ray ray01 = new Ray(new Point(0, 2, -0.5), new Vector(-3, -5, 7));
        assertEquals(2, geometries.findIntersections(ray01).size(),
                "Error: Wrong number of intersection points");


        // =============== Boundary Values Tests ==================
        // TC11: Empty collection of geometries - no intersections
        Geometries EmptyGeometries = new Geometries();
        Ray ray11 = new Ray(new Point(1, 2, 3), new Vector(1, 0, -1));
        assertNull(EmptyGeometries.findIntersections(ray11),
                "Error: Wrong number of intersection points");

        // TC12: No intersections with the geometries
        Ray ray12 = new Ray(new Point(-1, -1, 2), new Vector(0, 0, 1));
        assertNull(geometries.findIntersections(ray12),
                "Error: Wrong number of intersection points");

        // TC13: Intersects only one geometry
        Ray ray13 = new Ray(new Point(-2, -2, -1), new Vector(-1, -1, 7));
        assertEquals(1, geometries.findIntersections(ray13).size(),
                "Error: Wrong number of intersection points");

        // TC14: Ray intersects all the geometries
        Ray ray14 = new Ray(new Point(-1, 5, -1), new Vector(3, -6, 2));
        assertEquals(4, geometries.findIntersections(ray14).size(),
                "Error: Wrong number of intersection points");
    }
}