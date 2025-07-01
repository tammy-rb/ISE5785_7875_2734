package geometries;

import org.junit.jupiter.api.Test;
import primitives.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link geometries.Geometries} class.
 */
class GeometriesTest {

    /**
     * Test method for {@link Geometries#add(geometries.Intersectable...)}.
     * Verifies that geometries can be successfully added to the collection.
     */
    @Test
    void testAdd() {
        Geometries geometries = new Geometries();
        Plane plane = new Plane(
                new Point(1, 0, 0),
                new Point(0, 1, 0),
                new Point(1, 1, 0));

        // Add a single geometry (plane) to the collection
        geometries.add(plane);

        // Check that the size of the collection is now 1
        assertEquals(1, geometries.getGeometriesSize(),
                "Error: Adding a geometry to the collection doesn't work");
    }

    /**
     * Test method for {@link Geometries#findIntersections(Ray)}.
     * Verifies that the intersection method correctly returns the expected number of intersection points
     * for various scenarios including partial, none, and full intersections.
     */
    @Test
    void testFindIntersections() {
        Geometries geometries = new Geometries();
        geometries.add(
                new Plane(
                        new Point(1, 0, 0),
                        new Point(0, 1, 0),
                        new Point(1, 1, 0)),
                new Triangle(
                        new Point(1, 0, 0),
                        new Point(1, 0, 3),
                        new Point(3, 0, 0)),
                new Sphere(
                        new Point(0, 2, 0), 1
                ));

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray intersects part of the geometries (sphere and triangle)
        Ray ray01 = new Ray(new Point(0, 2, -0.5), new Vector(-3, -5, 7));
        assertEquals(2, geometries.findIntersections(ray01).size(),
                "Error: Wrong number of intersection points");

        // =============== Boundary Values Tests ==================

        // TC11: Empty collection of geometries - no intersections expected
        Geometries EmptyGeometries = new Geometries();
        Ray ray11 = new Ray(new Point(1, 2, 3), new Vector(1, 0, -1));
        assertNull(EmptyGeometries.findIntersections(ray11),
                "Error: Expected null when no geometries are present");

        // TC12: No intersections with any geometries
        Ray ray12 = new Ray(new Point(-1, -1, 2), new Vector(0, 0, 1));
        assertNull(geometries.findIntersections(ray12),
                "Error: Expected null when ray does not intersect any geometries");

        // TC13: Ray intersects only one geometry
        Ray ray13 = new Ray(new Point(-2, -2, -1), new Vector(-1, -1, 7));
        assertEquals(1, geometries.findIntersections(ray13).size(),
                "Error: Wrong number of intersection points for single hit");

        // TC14: Ray intersects all the geometries
        Ray ray14 = new Ray(new Point(-1, 5, -1), new Vector(3, -6, 2));
        assertEquals(4, geometries.findIntersections(ray14).size(),
                "Error: Wrong number of intersection points when intersecting all geometries");
    }

    @Test
    void testFindIntersectionsMaxDistance() {
        Geometries geometries = new Geometries();
        geometries.add(new Cylinder(
                        1,
                        new Ray(
                                new Point(2, 0, 0),
                                new Vector(0, 0, 1)),
                        4),
                new Triangle(
                        new Point(0, 0, 0),
                        new Point(4, 0, 0),
                        new Point(4, 4, 0)),
                new Tube(1,
                        new Ray(
                                new Point(-2, 0, 0),
                                new Vector(0, 0, 1))),
                new Plane(
                        new Point(0, 0, 0),
                        new Vector(0, 0, 1)),
                new Sphere(new Point(0, 3, 0), 1)
        );

        Ray ray = new Ray(new Point(1, 5, 0), new Vector(-3, -7, 0));
        assertEquals(1, geometries.calculateIntersections(ray, 2).size(), "Error");
        assertEquals(2, geometries.calculateIntersections(ray, 3.5).size(), "Error");
        assertEquals(4, geometries.calculateIntersections(ray, 11).size(), "Error");
        Ray ray1 = new Ray(new Point(3, 2, -1), new Vector(-1, -4, 6));
        assertEquals(2, geometries.calculateIntersections(ray1, 1.5).size(), "Error");
        assertEquals(3, geometries.calculateIntersections(ray1, 3).size(), "Error");
        assertEquals(4, geometries.calculateIntersections(ray1, 10).size(), "Error");
        Ray ray2 = new Ray(new Point(6, 1, -1), new Vector(-18, -1, 6));
        assertNull(geometries.calculateIntersections(ray2, 0.5), "error");
        assertEquals(6, geometries.calculateIntersections(ray2, 30).size(), "Error");

    }


    @Test
    void testCreateBoundingBoxHelper() {
        Geometries geometries = new Geometries();
        geometries.add(new Sphere(new Point(0,0,1), 1));
        geometries.add(new Cylinder(6, new Ray(new Point(0,0,0), new Vector(0,0,1)), 10));
        geometries.add(new Polygon(new Point(1, 0,0),new Point(0,0,0),new Point(0,1,0) ));
        CBR expected = new AABB(-6, -6, -6, 6,6, 16);
        assertEquals(
                expected,
                geometries.createCBR());
        geometries.add(new Plane(new Point(1, 0, 0), new Vector(0, 0, 1)));
        geometries.setBoundingBox(null);
        assertNull(geometries.createCBR());
    }

    @Test
    void testFindIntersectionsWithBVH() {
        Geometries geometries = new Geometries();
        geometries.add(
                new Plane(
                        new Point(1, 0, 0),
                        new Point(0, 1, 0),
                        new Point(1, 1, 0)),
                new Triangle(
                        new Point(1, 0, 0),
                        new Point(1, 0, 3),
                        new Point(3, 0, 0)),
                new Sphere(
                        new Point(0, 2, 0), 1
                ));
        geometries.createBVH();

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray intersects part of the geometries (sphere and triangle)
        Ray ray01 = new Ray(new Point(0, 2, -0.5), new Vector(-3, -5, 7));
        assertEquals(2, geometries.findIntersections(ray01).size(),
                "Error: Wrong number of intersection points");

        // =============== Boundary Values Tests ==================

        // TC11: Empty collection of geometries - no intersections expected
        Geometries EmptyGeometries = new Geometries();
        Ray ray11 = new Ray(new Point(1, 2, 3), new Vector(1, 0, -1));
        assertNull(EmptyGeometries.findIntersections(ray11),
                "Error: Expected null when no geometries are present");

        // TC12: No intersections with any geometries
        Ray ray12 = new Ray(new Point(-1, -1, 2), new Vector(0, 0, 1));
        assertNull(geometries.findIntersections(ray12),
                "Error: Expected null when ray does not intersect any geometries");

        // TC13: Ray intersects only one geometry
        Ray ray13 = new Ray(new Point(-2, -2, -1), new Vector(-1, -1, 7));
        assertEquals(1, geometries.findIntersections(ray13).size(),
                "Error: Wrong number of intersection points for single hit");

        // TC14: Ray intersects all the geometries
        Ray ray14 = new Ray(new Point(-1, 5, -1), new Vector(3, -6, 2));
        assertEquals(4, geometries.findIntersections(ray14).size(),
                "Error: Wrong number of intersection points when intersecting all geometries");
    }

}
