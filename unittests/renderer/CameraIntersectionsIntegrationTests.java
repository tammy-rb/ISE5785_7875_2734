package renderer;

import geometries.*;
import primitives.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link renderer.Camera} ray construction and intersection with geometric shapes.
 */
public class CameraIntersectionsIntegrationTests {

    private static final int NX = 3; // Number of columns
    private static final int NY = 3; // Number of rows

    private static final String ERROR_INTERSECTION_COUNT = "ERROR: Wrong number of intersections";

    private static final Camera.Builder CAMERA_BUILDER = Camera.getBuilder()
            .setLocation(Point.ZERO)
            .setViewPlaneDistance(1)
            .setViewPlaneSize(3, 3)
            .setResolution(NX, NY)
            .setDirection(new Vector(0, 0, -1), new Vector(0, -1, 0));

    private static final Camera CAMERA_DEFAULT = CAMERA_BUILDER.build();
    private static final Camera CAMERA_SHIFTED = CAMERA_BUILDER
            .setLocation(new Point(0, 0, 0.5))
            .build();

    /**
     * Calculates the total number of intersections between camera rays and a given geometry.
     */
    private int calculateTotalIntersections(Geometry geometry, Camera camera) {
        int intersections = 0;
        for (int i = 0; i < NX; i++)
            for (int j = 0; j < NY; j++) {
                Ray ray = camera.constructRay(NX, NY, j, i);
                var intersectionPoints = geometry.findIntersections(ray);
                intersections += (intersectionPoints != null) ? intersectionPoints.size() : 0;
            }
        return intersections;
    }

    /**
     * Helper method for asserting intersection counts.
     */
    private void assertIntersections(int expected, Geometry geometry, Camera camera) {
        assertEquals(expected,
                calculateTotalIntersections(geometry, camera),
                ERROR_INTERSECTION_COUNT);
    }

    /**
     * Integration tests for ray-sphere intersections.
     */
    @Test
    void testRaySphereIntersections() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Small sphere in front of the view plane center — 2 rays should intersect
        assertIntersections(2,
                new Sphere(new Point(0, 0, -3), 1),
                CAMERA_DEFAULT);

        // TC02: Large sphere covering the entire view plane — 18 rays should intersect
        assertIntersections(18,
                new Sphere(new Point(0, 0, -2.5), 2.5),
                CAMERA_SHIFTED);

        // TC03: Medium sphere partially intersected — 10 rays should intersect
        assertIntersections(10,
                new Sphere(new Point(0, 0, -2), 2),
                CAMERA_SHIFTED);

        // =============== Boundary Values Tests ==================
        // TC11: Very large sphere encompassing the camera — 9 rays should intersect
        assertIntersections(9,
                new Sphere(new Point(0, 0, 0), 4),
                CAMERA_DEFAULT);

        // TC12: Small sphere behind the camera — no intersections expected
        assertIntersections(0,
                new Sphere(new Point(0, 0, 1), 0.5),
                CAMERA_DEFAULT);
    }

    /**
     * Integration tests for ray-plane intersections.
     */
    @Test
    void testRayPlaneIntersections() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Plane orthogonal to the view direction — all 9 rays intersect
        assertIntersections(9,
                new Plane(new Point(0, 0, -5), new Vector(0, 0, 1)),
                CAMERA_DEFAULT);

        // TC02: Plane slightly tilted — intersects 9 rays
        assertIntersections(9,
                new Plane(new Point(0, 0, -10), new Vector(1, 1, 10)),
                CAMERA_DEFAULT);

        // =============== Boundary Values Tests ==================
        // TC11: Plane significantly tilted — only some rays intersect
        assertIntersections(6,
                new Plane(new Point(0, 0, -5), new Vector(0, 1, 1)),
                CAMERA_DEFAULT);
    }

    /**
     * Integration tests for ray-triangle intersections.
     */
    @Test
    void testRayTriangleIntersections() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Small triangle in the center of the view plane — only 1 ray intersects
        assertIntersections(1, new Triangle(
                new Point(0, 1, -2),
                new Point(1, -1, -2),
                new Point(-1, -1, -2)
        ), CAMERA_DEFAULT);

        // =============== Boundary Values Tests ==================
        // TC11: Larger triangle — 2 rays should intersect
        assertIntersections(2, new Triangle(
                new Point(0, 20, -2),
                new Point(1, -1, -2),
                new Point(-1, -1, -2)
        ), CAMERA_DEFAULT);
    }
}
