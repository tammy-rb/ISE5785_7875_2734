package geometries;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import geometries.Plane;
import geometries.Polygon;
import primitives.*;

/**
 * Testing Polygons
 *
 * @author Dan
 */
class PolygonTests {
    /**
     * Delta value for accuracy when comparing the numbers of type 'double' in
     * assertEquals
     */
    private static final double DELTA = 0.000001;

    /**
     * Test method for {@link geometries.Polygon#Polygon(primitives.Point...)}.
     */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Correct concave quadrangular with vertices in correct order
        assertDoesNotThrow(() -> new Polygon(new Point(0, 0, 1),
                        new Point(1, 0, 0),
                        new Point(0, 1, 0),
                        new Point(-1, 1, 1)),
                "Failed constructing a correct polygon");

        // TC02: Wrong vertices order
        assertThrows(IllegalArgumentException.class, //
                () -> new Polygon(new Point(0, 0, 1), new Point(0, 1, 0), new Point(1, 0, 0), new Point(-1, 1, 1)), //
                "Constructed a polygon with wrong order of vertices");

        // TC03: Not in the same plane
        assertThrows(IllegalArgumentException.class, //
                () -> new Polygon(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0), new Point(0, 2, 2)), //
                "Constructed a polygon with vertices that are not in the same plane");

        // TC04: Concave quadrangular
        assertThrows(IllegalArgumentException.class, //
                () -> new Polygon(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0),
                        new Point(0.5, 0.25, 0.5)), //
                "Constructed a concave polygon");

        // =============== Boundary Values Tests ==================

        // TC10: Vertex on a side of a quadrangular
        assertThrows(IllegalArgumentException.class, //
                () -> new Polygon(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0),
                        new Point(0, 0.5, 0.5)),
                "Constructed a polygon with vertix on a side");

        // TC11: Last point = first point
        assertThrows(IllegalArgumentException.class, //
                () -> new Polygon(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0), new Point(0, 0, 1)),
                "Constructed a polygon with vertice on a side");

        // TC12: Co-located points
        assertThrows(IllegalArgumentException.class, //
                () -> new Polygon(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0), new Point(0, 1, 0)),
                "Constructed a polygon with vertice on a side");

    }

    /**
     * Test method for {@link geometries.Polygon#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: There is a simple single test here - using a quad
        Point[] pts =
                {new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0), new Point(-1, 1, 1)};
        Polygon pol = new Polygon(pts);
        // ensure there are no exceptions
        assertDoesNotThrow(() -> pol.getNormal(new Point(0, 0, 1)), "");
        // generate the test result
        Vector result = pol.getNormal(new Point(0, 0, 1));
        // ensure |result| = 1
        assertEquals(1, result.length(), DELTA, "Polygon's normal is not a unit vector");
        // ensure the result is orthogonal to all the edges
        for (int i = 0; i < 3; ++i)
            assertEquals(0d, result.dotProduct(pts[i].subtract(pts[i == 0 ? 3 : i - 1])), DELTA,
                    "Polygon's normal is not orthogonal to one of the edges");
    }

    /**
     * Test method for {@link geometries.Polygon#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        final Point gp1 = new Point(0, 1, 0);
        final Point gp2 = new Point(4, 1, 0);
        final Point gp3 = new Point(4, 5, 0);
        final Point gp4 = new Point(0, 5, 0);
        final Polygon polygon = new Polygon(new Point[]{gp1, gp2, gp3, gp4});

        // ============ Equivalence Partitions Tests ==============
        // TC01: Ray intersects inside the polygon (1 point)
        final Ray ray01 = new Ray(
                new Point(0, 0, -3),
                new Vector(2, 3, 3));
        final var result01 = polygon.findIntersections(ray01);
        assertNotNull(result01,
                "Error: Ray intersects inside the polygon — result should not be null");
        assertEquals(1, result01.size(),
                "Error: Wrong number of intersection points");
        assertEquals(List.of(new Point(2, 3, 0)), result01,
                "Error: Intersection point inside polygon is incorrect");

        // TC02: Ray intersects outside the polygon against an edge (0 points)
        final Ray ray02 = new Ray(
                new Point(-2, 0, -3),
                new Vector(5, 0, 3));
        assertNull(polygon.findIntersections(ray02),
                "Error: Ray intersects outside polygon against edge — should return null");

        // TC03: Ray intersects outside the polygon against a vertex (0 points)
        final Ray ray03 = new Ray(
                new Point(3, 0, -3),
                new Vector(-4, 0, 3));
        assertNull(polygon.findIntersections(ray03),
                "Error: Ray intersects outside polygon against vertex — should return null");

        // =============== Boundary Values Tests ==================
        // TC11: Ray intersects exactly on an edge continues (0 points)
        final Ray ray11 = new Ray(
                new Point(0, 0, -3),
                new Vector(4, 0, 3));
        final var result11 = polygon.findIntersections(ray11);
        assertNull(result11,
                "Error: Ray intersects on an edge continues — should return null");

        // TC12: Ray intersects exactly on a vertex (0 points)
        final Ray ray12 = new Ray(
                new Point(-1, 0, -3),
                new Vector(5, 5, 3));
        assertNull(polygon.findIntersections(ray12),
                "Error: Ray intersects exactly on a vertex — should return null");

        // TC13: Ray intersects exactly on an edge (0 points)
        final Ray ray13 = new Ray(
                new Point(2, 0, -3),
                new Vector(-2, 3, 3));
        assertNull(polygon.findIntersections(ray13),
                "Error: Ray intersects exactly on edge — should return null");
    }

    @Test
    void testFindIntersectionsMaxDistanceTest() {
        Polygon polygon = new Polygon(new Point[]{
                new Point(0, 0, 0),
                new Point(0, 6, 0),
                new Point(6, 6, 0),
                new Point(6, 0, 0)});

        Ray ray = new Ray(new Point(0, 1, 1), new Vector(3, 2, -2));

        assertEquals(1, polygon.calculateIntersections(ray, 10).size(),
                "Error: Ray doesn't intersect — should return null");

        assertNull(polygon.calculateIntersections(new Ray(new Point(2, 1, -1), new Vector(1, 0, -1)), 0.5),
                "Error: Ray doesn't intersect — should return null");


        assertNull(polygon.calculateIntersections(new Ray(new Point(2, 1, 0), new Vector(1, 0, -2)), 0.5),
                "Error: Ray doesn't intersect — should return null");

        assertEquals(1, polygon.calculateIntersections(new Ray(new Point(3, 3, 1), new Vector(0, 0, -1)), 1).size(),
                "Error: Ray doesn't intersect — should return null");

    }

    @Test
    void testCreateBoundingBoxHelper() {
        Polygon polygon = new Polygon(
                new Point(-1, -1, 0),
                new Point(1, -1, 0),
                new Point(1, 1, 0),
                new Point(-1, 1, 0)
        );        CBR expected = new AABB(-1, -1, 0, 1,1, 0);
        assertEquals(
                expected,
                polygon.createCBR());
    }
}
