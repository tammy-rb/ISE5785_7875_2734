package renderer;

import static java.awt.Color.BLUE;

import org.junit.jupiter.api.Test;

import geometries.*;
import lighting.*;
import primitives.*;
import scene.Scene;

/**
 * Testing basic shadows and supersampling (soft shadows)
 */
class SuperSamplingTest {

    SuperSamplingTest() { }

    private final Scene scene = new Scene("SuperSampling Scene");

    private final Camera.Builder camera = Camera.getBuilder()
            .setLocation(new Point(0, 0, 1000))
            .setDirection(Point.ZERO, Vector.AXIS_Y)
            .setViewPlaneDistance(1000)
            .setViewPlaneSize(200, 200)
            .setNumRays(3 * 3) // Supersampling: 3x3 rays per pixel
            .setRayTracer(scene, RayTracerType.SIMPLE);

    private final Intersectable sphere = new Sphere(new Point(0, 0, -200), 60d)
            .setEmission(new Color(BLUE))
            .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(30));

    private final Material trMaterial = new Material().setKD(0.5).setKS(0.5).setShininess(30);

    private void sphereTriangleHelper(String pictName, Triangle triangle, Point spotLocation) {
        scene.geometries.add(
                sphere,
                triangle.setEmission(new Color(BLUE)).setMaterial(trMaterial)
        );

        scene.setAmbientLight(new AmbientLight(new Color(30, 30, 30)));

        // Add directional light
        scene.lights.add(new DirectionalLight(
                new Color(100, 100, 100),
                new Vector(0, 0, -1))
        );

        // Add spot light with radius for soft shadows
        scene.lights.add(new SpotLight(
                new Color(400, 240, 0),
                spotLocation,
                new Vector(1, 1, -3),
                15 // radius > 0 enables soft shadows
        ).setKL(1E-5).setKQ(1.5E-7));

        camera
                .setResolution(400, 400)
                .build()
                .renderImage()
                .writeToImage(pictName);
    }

    @Test
    void sphereTriangleInitial() {
        sphereTriangleHelper("superSampling_SphereTriangle_Initial",
                new Triangle(
                        new Point(-70, -40, 0),
                        new Point(-40, -70, 0),
                        new Point(-68, -68, -4)),
                new Point(-100, -100, 200));
    }

    @Test
    void sphereTriangleMove1() {
        sphereTriangleHelper("superSampling_SphereTriangle_Move1",
                new Triangle(
                        new Point(-60, -30, 0),
                        new Point(-30, -60, 0),
                        new Point(-58, -58, -4)),
                new Point(-100, -100, 200));
    }

    @Test
    void sphereTriangleMove2() {
        sphereTriangleHelper("superSampling_SphereTriangle_Move2",
                new Triangle(
                        new Point(-50, -20, 0),
                        new Point(-20, -50, 0),
                        new Point(-48, -48, 4)),
                new Point(-100, -100, 200));
    }

    @Test
    void sphereTriangleSpot1() {
        sphereTriangleHelper("superSampling_SphereTriangle_Spot1",
                new Triangle(
                        new Point(-70, -40, 0),
                        new Point(-40, -70, 0),
                        new Point(-68, -68, -4)),
                new Point(-90, -90, 140));
    }

    @Test
    void sphereTriangleSpot2() {
        sphereTriangleHelper("superSampling_SphereTriangle_Spot2",
                new Triangle(
                        new Point(-70, -40, 0),
                        new Point(-40, -70, 0),
                        new Point(-68, -68, -4)),
                new Point(-77, -77, 80));
    }
}
