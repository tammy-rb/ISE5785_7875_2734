package renderer;

import static java.awt.Color.BLUE;

import org.junit.jupiter.api.Test;

import geometries.*;
import lighting.*;
import primitives.*;
import scene.Scene;

public class SuperSamplingTest {
    @Test
    public void testGeometricShapes() {
        Scene scene = new Scene("Geometric Shapes");

        // Set ambient light (low for dramatic shadows)
        scene.setAmbientLight(new AmbientLight(new Color(20, 20, 20)));

        // Light gray background
        scene.setBackground(new Color(180, 180, 180));

        // === LIGHTING SETUP ===

        // Main directional light from upper left for dramatic shadows
        scene.lights.add(new DirectionalLight(
                new Color(800, 800, 800), // bright white light
                new Vector(-0.5, -0.7, -0.3).normalize() // from upper left
        ));

        // Secondary fill light to soften shadows slightly
        scene.lights.add(new DirectionalLight(
                new Color(200, 200, 200), // softer light
                new Vector(0.3, -0.2, -0.8).normalize()
        ));

        // === MATERIALS ===

        // Matte material for geometric shapes
        Material matteMaterial = new Material()
                .setKD(0.8)
                .setKS(0.2)
                .setShininess(30);

        // Slightly more reflective material for variety
        Material semiMatteMaterial = new Material()
                .setKD(0.7)
                .setKS(0.3)
                .setShininess(50);

        // === GEOMETRIC SHAPES ===

        // Cube (using a box or multiple planes)
        // Since you might not have a Box primitive, creating with planes
        double cubeSize = 30;
        Point cubeCenter = new Point(-40, -15, -20);

        // Create cube faces
        addCubeFaces(scene, cubeCenter, cubeSize, new Color(120, 120, 120), matteMaterial);

        // Cylinder
        scene.geometries.add(new Cylinder(12,
                new Ray(new Point(30, -30, -10), new Vector(0, 1, 0)),
                50)
                .setEmission(new Color(100, 100, 100))
                .setMaterial(semiMatteMaterial));

        // Sphere
        scene.geometries.add(new Sphere(new Point(-10, -20, 10), 15)
                .setEmission(new Color(140, 140, 140))
                .setMaterial(matteMaterial));

        // === GROUND PLANE ===
        // Large plane for ground with subtle texture
        scene.geometries.add(new Plane(
                new Point(0, -45, 0),
                new Vector(0, 1, 0))
                .setEmission(new Color(160, 160, 160))
                .setMaterial(new Material()
                        .setKD(0.9)
                        .setKS(0.1)
                        .setShininess(20)));

        // === CAMERA SETUP ===
        Camera.Builder cameraBuilder = Camera.getBuilder()
                .setRayTracer(scene, RayTracerType.SIMPLE);

        cameraBuilder
                .setLocation(new Point(0, 10, 100))
                .setDirection(new Point(0, -10, 0), new Vector(0, 1, 0))
                .setViewPlaneDistance(100)
                .setResolution(800, 600)
                .setViewPlaneSize(120, 90)

                .build()
                .renderImage()
                .writeToImage("geometricShapes");
    }

    /**
     * Helper method to create cube faces using planes
     */
    private void addCubeFaces(Scene scene, Point center, double size, Color color, Material material) {
        double half = size / 2;

        // Front face
        scene.geometries.add(new Polygon(
                new Point(center.get_xyz().d1() - half, center.get_xyz().d2() - half, center.get_xyz().d3() + half),
                new Point(center.get_xyz().d1() + half, center.get_xyz().d2() - half, center.get_xyz().d3() + half),
                new Point(center.get_xyz().d1() + half, center.get_xyz().d2() + half, center.get_xyz().d3() + half),
                new Point(center.get_xyz().d1() - half, center.get_xyz().d2() + half, center.get_xyz().d3() + half))
                .setEmission(color)
                .setMaterial(material));

        // Back face
        scene.geometries.add(new Polygon(
                new Point(center.get_xyz().d1() + half, center.get_xyz().d2() - half, center.get_xyz().d3() - half),
                new Point(center.get_xyz().d1() - half, center.get_xyz().d2() - half, center.get_xyz().d3() - half),
                new Point(center.get_xyz().d1() - half, center.get_xyz().d2() + half, center.get_xyz().d3() - half),
                new Point(center.get_xyz().d1() + half, center.get_xyz().d2() + half, center.get_xyz().d3() - half))
                .setEmission(color)
                .setMaterial(material));

        // Left face
        scene.geometries.add(new Polygon(
                new Point(center.get_xyz().d1() - half, center.get_xyz().d2() - half, center.get_xyz().d3() - half),
                new Point(center.get_xyz().d1() - half, center.get_xyz().d2() - half, center.get_xyz().d3() + half),
                new Point(center.get_xyz().d1() - half, center.get_xyz().d2() + half, center.get_xyz().d3() + half),
                new Point(center.get_xyz().d1() - half, center.get_xyz().d2() + half, center.get_xyz().d3() - half))
                .setEmission(color)
                .setMaterial(material));

        // Right face
        scene.geometries.add(new Polygon(
                new Point(center.get_xyz().d1() + half, center.get_xyz().d2() - half, center.get_xyz().d3() + half),
                new Point(center.get_xyz().d1() + half, center.get_xyz().d2() - half, center.get_xyz().d3() - half),
                new Point(center.get_xyz().d1() + half, center.get_xyz().d2() + half, center.get_xyz().d3() - half),
                new Point(center.get_xyz().d1() + half, center.get_xyz().d2() + half, center.get_xyz().d3() + half))
                .setEmission(color)
                .setMaterial(material));

        // Top face
        scene.geometries.add(new Polygon(
                new Point(center.get_xyz().d1() - half, center.get_xyz().d2() + half, center.get_xyz().d3() + half),
                new Point(center.get_xyz().d1() + half, center.get_xyz().d2() + half, center.get_xyz().d3() + half),
                new Point(center.get_xyz().d1() + half, center.get_xyz().d2() + half, center.get_xyz().d3() - half),
                new Point(center.get_xyz().d1() - half, center.get_xyz().d2() + half, center.get_xyz().d3() - half))
                .setEmission(color)
                .setMaterial(material));

        // Bottom face
        scene.geometries.add(new Polygon(
                new Point(center.get_xyz().d1() - half, center.get_xyz().d2() - half, center.get_xyz().d3() - half),
                new Point(center.get_xyz().d1() + half, center.get_xyz().d2() - half, center.get_xyz().d3() - half),
                new Point(center.get_xyz().d1() + half, center.get_xyz().d2() - half, center.get_xyz().d3() + half),
                new Point(center.get_xyz().d1() - half, center.get_xyz().d2() - half, center.get_xyz().d3() + half))
                .setEmission(color)
                .setMaterial(material));
}
}