package renderer;

import geometries.*;
import primitives.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Camera ray construction and intersection finding with geometric bodies.
 */
public class CameraIntersectionsIntegrationTests {

    // Constants for camera view plane
    private static final int NX = 3; // Number of columns
    private static final int NY = 3; // Number of rows

    private static final String ERROR_INTERSECTION_COUNT = "Wrong number of intersections";

    private static final Camera.Builder CAMERA_BUILDER = Camera.getBuilder()
            .setLocation(Point.ZERO)
            .setViewPlaneDistance(1)
            .setViewPlaneSize(3, 3) // <-- Added width and height for ViewPlane
            .setResolution(NX, NY)
            .setDirection(new Vector(0, 0, -1), new Vector(0, -1, 0));

    private static final Camera CAMERA_DEFAULT = CAMERA_BUILDER.build();
    private static final Camera CAMERA_SHIFTED = CAMERA_BUILDER
            .setLocation(new Point(0, 0, 0.5))
            .build();

    /**
     * Calculates the total number of intersections between rays from the camera and a given geometry.
     */
    private int calculateTotalIntersections(Geometry geometry, Camera camera) {
        int intersections = 0;
        for (int i = 0; i < NX; i++) {
            for (int j = 0; j < NY; j++) {
                Ray ray = camera.constructRay(NX, NY, j, i);
                var intersectionPoints = geometry.findIntersections(ray);
                intersections += (intersectionPoints != null) ? intersectionPoints.size() : 0;
            }
        }
        return intersections;
    }

    /**
     * Helper method for asserting intersections.
     */
    private void assertIntersections(String testCaseId, int expected, Geometry geometry, Camera camera) {
        assertEquals(expected, calculateTotalIntersections(geometry, camera),
                testCaseId + " - " + ERROR_INTERSECTION_COUNT);
    }

    @Test
    void testRaySphereIntersections() {
        // TC01: Small sphere in front of view plane center
        Sphere sphere1 = new Sphere(1, new Point(0, 0, -3));
        assertIntersections("TC01 sphere", 2, sphere1, CAMERA_DEFAULT);

        // TC02: Bigger sphere covering whole view plane, camera slightly shifted
        Sphere sphere2 = new Sphere(2.5, new Point(0, 0, -2.5));
        assertIntersections("TC02 sphere", 18, sphere2, CAMERA_SHIFTED);

        // TC03: Medium-sized sphere, partial intersections, camera slightly shifted
        Sphere sphere3 = new Sphere(2, new Point(0, 0, -2));
        assertIntersections("TC03 sphere", 10, sphere3, CAMERA_SHIFTED);

        // TC04: Large sphere that encompasses the camera
        Sphere sphere4 = new Sphere(4, new Point(0, 0, 0));
        assertIntersections("TC04 sphere", 9, sphere4, CAMERA_DEFAULT);

        // TC05: Small sphere located behind the camera (no intersections)
        Sphere sphere5 = new Sphere(0.5, new Point(0, 0, 1));
        assertIntersections("TC05 sphere", 0, sphere5, CAMERA_DEFAULT);
    }

    @Test
    void testRayPlaneIntersections() {
        // TC01: Plane parallel to the view plane
        Plane plane1 = new Plane(new Point(0, 0, -5), new Vector(0, 0, 1));
        assertIntersections("TC01 plane", 9, plane1, CAMERA_DEFAULT);

        // TC02: Plane slightly tilted relative to the view plane (all rays still intersect)
        Plane plane2 = new Plane(new Point(0, 0, -10), new Vector(1, 1, 10));
        assertIntersections("TC02 plane", 9, plane2, CAMERA_DEFAULT);

        // TC03: Plane significantly tilted (only some rays intersect)
        Plane plane3 = new Plane(new Point(0, 0, -5), new Vector(0, 1, 1));
        assertIntersections("TC03 plane", 6, plane3, CAMERA_DEFAULT);
    }

    @Test
    void testRayTriangleIntersections() {
        // TC01: Small triangle located in center of view plane
        Triangle triangle1 = new Triangle(
                new Point(0, 1, -2),
                new Point(1, -1, -2),
                new Point(-1, -1, -2)
        );
        assertIntersections("TC01 triangle", 1, triangle1, CAMERA_DEFAULT);

        // TC02: Large triangle covering more of the view plane
        Triangle triangle2 = new Triangle(
                new Point(0, 20, -2),
                new Point(1, -1, -2),
                new Point(-1, -1, -2)
        );
        assertIntersections("TC02 triangle", 2, triangle2, CAMERA_DEFAULT);
    }
}
