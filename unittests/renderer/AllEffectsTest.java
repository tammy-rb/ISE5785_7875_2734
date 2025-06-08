package renderer;

import geometries.*;
import lighting.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

import static java.awt.Color.*;

public class AllEffectsTest {

    AllEffectsTest() {}

    private final Scene scene = new Scene("Cityscape");
    private final Camera.Builder cameraBuilder = Camera.getBuilder()
            .setRayTracer(scene, RayTracerType.SIMPLE);

    @Test
    void TestAllEffects() {
        scene.setAmbientLight(new AmbientLight(new Color(20, 20, 20)));

        scene.geometries.add(
                new Sphere(new Point(0, 0, -400), 60)
                        .setEmission(new Color(30, 30, 80))
                        .setMaterial(new Material()
                                .setKD(0.2)
                                .setKS(0.8)
                                .setShininess(300)
                                .setKT(0.9)
                                .setKR(0.05))
        );

        scene.geometries.add(
                new Sphere(new Point(-100, 20, -350), 40)
                        .setEmission(new Color(10, 10, 10))
                        .setMaterial(new Material()
                                .setKD(0.1)
                                .setKS(0.9)
                                .setShininess(500)
                                .setKR(0.9))
        );

        scene.geometries.add(
                new Sphere(new Point(100, 20, -350), 40)
                        .setEmission(new Color(150, 20, 20))
                        .setMaterial(new Material()
                                .setKD(0.2)
                                .setKS(0.4)
                                .setShininess(200)
                                .setKT(0.5))
        );

        scene.geometries.add(
                new Polygon(
                        new Point(-200, 60, -500),
                        new Point(200, 60, -500),
                        new Point(200, 60, 200),
                        new Point(-200, 60, 200)
                )
                        .setEmission(new Color(50, 50, 50))
                        .setMaterial(new Material()
                                .setKD(0.4)
                                .setKS(0.6)
                                .setShininess(150)
                                .setKR(0.6))
        );

        scene.geometries.add(
                new Polygon(
                        new Point(-200, 60, -600),
                        new Point(200, 60, -600),
                        new Point(200, -200, -600),
                        new Point(-200, -200, -600)
                )
                        .setEmission(new Color(0, 20, 60))
                        .setMaterial(new Material()
                                .setKD(0.1)
                                .setKS(0.5)
                                .setShininess(100)
                                .setKT(0.6))
        );

        scene.geometries.add(
                new Cylinder(
                        10,
                        new Ray(new Point(-50, 60, -450), new Vector(0, -1, 0)),
                        150
                ).setEmission(new Color(255, 215, 0))
                        .setMaterial(new Material()
                                .setKD(0.3)
                                .setKS(0.8)
                                .setShininess(400)
                                .setKR(0.5))
        );

        scene.lights.add(
                new SpotLight(new Color(1000, 800, 800),
                        new Point(-100, -150, 300),
                        new Vector(0, 1, -1))
                        .setKL(0.0004)
                        .setKQ(0.00006)
                        .setNarrowBeam(20)
        );

        scene.lights.add(
                new PointLight(new Color(500, 100, 100), new Point(150, -100, 200))
                        .setKL(0.0004)
                        .setKQ(0.0002)
        );

        scene.lights.add(
                new DirectionalLight(new Color(100, 100, 300),
                        new Vector(1, -1, -2))
        );

        cameraBuilder
                .setLocation(new Point(0, 0, 1000))
                .setDirection(new Point(0, 1, 0), new Vector(0, 0, -1))
                .setViewPlaneDistance(1000)
                .setResolution(1920, 1080)
                .setViewPlaneSize(350, 200)
                .build()
                .renderImage()
                .writeToImage("CityscapeRender");
    }
}
