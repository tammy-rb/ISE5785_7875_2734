package renderer.scenesTests;

import org.junit.jupiter.api.Test;

import geometries.*;
import lighting.*;
import primitives.*;
import renderer.Camera;
import renderer.RayTracerType;
import scene.Scene;

class AstronautTest {

    private final Scene scene = new Scene("Enhanced Realistic Astronaut Scene");
    private final Camera.Builder cameraBuilder = Camera.getBuilder()
            .setRayTracer(scene, RayTracerType.SIMPLE);

    @Test
    void enhancedAstronautScene() {
        // Deep space background with nebula effect
        scene.geometries.add(
                new Sphere(new Point(0, 0, -100), 600)
                        .setEmission(new Color(2, 4, 8))
                        .setMaterial(new Material().setKD(1).setKS(0).setShininess(0))
        );

        // === IMPROVED ASTRONAUT SUIT ===

        // MAIN TORSO - Single continuous cylinder like before but thicker
        scene.geometries.add(
                new Cylinder(6.5, new Ray(new Point(0, -12, -50), new Vector(0, 1, 0)), 20)
                        .setEmission(new Color(248, 248, 252))
                        .setMaterial(new Material().setKD(0.4).setKS(0.5).setShininess(80))
        );

        // IMPROVED HELMET SYSTEM
        // Main helmet sphere - larger and more proportional
        scene.geometries.add(
                new Sphere(new Point(0, 12, -50), 7.5)
                        .setEmission(new Color(255, 255, 255))
                        .setMaterial(new Material().setKD(0.05).setKS(0.9).setShininess(300).setKT(0.1))
        );

        // Gold reflective visor - properly positioned
        scene.geometries.add(
                new Sphere(new Point(0, 12.5, -44), 6.8)
                        .setEmission(new Color(255, 215, 0))
                        .setMaterial(new Material().setKD(0.02).setKS(0.95).setShininess(400).setKR(0.3))
        );

        // Helmet seal/gasket
        scene.geometries.add(
                new Cylinder(6.2, new Ray(new Point(0, 4.5, -50), new Vector(0, 1, 0)), 1.5)
                        .setEmission(new Color(180, 180, 185))
                        .setMaterial(new Material().setKD(0.6).setKS(0.3).setShininess(40))
        );

        // Helmet attachment ring
        scene.geometries.add(
                new Cylinder(7.8, new Ray(new Point(0, 4, -50), new Vector(0, 1, 0)), 0.8)
                        .setEmission(new Color(150, 150, 160))
                        .setMaterial(new Material().setKD(0.5).setKS(0.4).setShininess(60))
        );

        // === ENHANCED CHEST CONTROL PANEL ===
        scene.geometries.add(
                new Cylinder(3, new Ray(new Point(0, 2, -42), new Vector(0, 0, -1)), 2)
                        .setEmission(new Color(50, 55, 65))
                        .setMaterial(new Material().setKD(0.6).setKS(0.3).setShininess(40))
        );

        // Detailed control buttons
        scene.geometries.add(
                new Sphere(new Point(-1.5, 2.5, -40.5), 0.4)
                        .setEmission(new Color(255, 50, 50))
                        .setMaterial(new Material().setKD(0.3).setKS(0.7).setShininess(150))
        );
        scene.geometries.add(
                new Sphere(new Point(0, 2.5, -40.5), 0.4)
                        .setEmission(new Color(50, 255, 50))
                        .setMaterial(new Material().setKD(0.3).setKS(0.7).setShininess(150))
        );
        scene.geometries.add(
                new Sphere(new Point(1.5, 2.5, -40.5), 0.4)
                        .setEmission(new Color(50, 150, 255))
                        .setMaterial(new Material().setKD(0.3).setKS(0.7).setShininess(150))
        );

        // Digital display
        scene.geometries.add(
                new Cylinder(1.2, new Ray(new Point(0, 0.5, -40.2), new Vector(0, 0, -1)), 0.3)
                        .setEmission(new Color(0, 100, 200))
                        .setMaterial(new Material().setKD(0.1).setKS(0.8).setShininess(200))
        );

        // === BETTER PROPORTIONED ARMS - MORE RELAXED POSE ===

        // Improved shoulders with proper bulk
        scene.geometries.add(
                new Sphere(new Point(-8, 6, -50), 3.5)
                        .setEmission(new Color(240, 240, 245))
                        .setMaterial(new Material().setKD(0.4).setKS(0.4).setShininess(70))
        );
        scene.geometries.add(
                new Sphere(new Point(8, 6, -50), 3.5)
                        .setEmission(new Color(240, 240, 245))
                        .setMaterial(new Material().setKD(0.4).setKS(0.4).setShininess(70))
        );

        // Upper arms - angled outward for more natural pose
        scene.geometries.add(
                new Cylinder(2.5, new Ray(new Point(-9, 3, -50), new Vector(-0.3, -1, 0)), 7)
                        .setEmission(new Color(245, 245, 250))
                        .setMaterial(new Material().setKD(0.4).setKS(0.4).setShininess(70))
        );
        scene.geometries.add(
                new Cylinder(2.5, new Ray(new Point(9, 3, -50), new Vector(0.3, -1, 0)), 7)
                        .setEmission(new Color(245, 245, 250))
                        .setMaterial(new Material().setKD(0.4).setKS(0.4).setShininess(70))
        );

        // Elbow joints - positioned wider
        scene.geometries.add(
                new Sphere(new Point(-11, -4, -50), 2.8)
                        .setEmission(new Color(220, 220, 225))
                        .setMaterial(new Material().setKD(0.4).setKS(0.4).setShininess(70))
        );
        scene.geometries.add(
                new Sphere(new Point(11, -4, -50), 2.8)
                        .setEmission(new Color(220, 220, 225))
                        .setMaterial(new Material().setKD(0.4).setKS(0.4).setShininess(70))
        );

        // Forearms - angled slightly inward
        scene.geometries.add(
                new Cylinder(2.2, new Ray(new Point(-11, -7, -50), new Vector(0.2, -1, 0)), 6)
                        .setEmission(new Color(245, 245, 250))
                        .setMaterial(new Material().setKD(0.4).setKS(0.4).setShininess(70))
        );
        scene.geometries.add(
                new Cylinder(2.2, new Ray(new Point(11, -7, -50), new Vector(-0.2, -1, 0)), 6)
                        .setEmission(new Color(245, 245, 250))
                        .setMaterial(new Material().setKD(0.4).setKS(0.4).setShininess(70))
        );

        // Enhanced gloves with wrist seals - positioned naturally
        scene.geometries.add(
                new Cylinder(2.5, new Ray(new Point(-10, -13, -50), new Vector(0, -1, 0)), 1)
                        .setEmission(new Color(160, 160, 170))
                        .setMaterial(new Material().setKD(0.5).setKS(0.3).setShininess(50))
        );
        scene.geometries.add(
                new Cylinder(2.5, new Ray(new Point(10, -13, -50), new Vector(0, -1, 0)), 1)
                        .setEmission(new Color(160, 160, 170))
                        .setMaterial(new Material().setKD(0.5).setKS(0.3).setShininess(50))
        );

        scene.geometries.add(
                new Sphere(new Point(-10, -14.5, -50), 2.5)
                        .setEmission(new Color(180, 180, 190))
                        .setMaterial(new Material().setKD(0.5).setKS(0.3).setShininess(50))
        );
        scene.geometries.add(
                new Sphere(new Point(10, -14.5, -50), 2.5)
                        .setEmission(new Color(180, 180, 190))
                        .setMaterial(new Material().setKD(0.5).setKS(0.3).setShininess(50))
        );

        // === IMPROVED LEGS WITH BETTER PROPORTIONS - STRAIGHTER STANCE ===

        // Hip/pelvis area - more substantial
        scene.geometries.add(
                new Sphere(new Point(0, -12, -50), 6.5)
                        .setEmission(new Color(240, 240, 245))
                        .setMaterial(new Material().setKD(0.4).setKS(0.4).setShininess(70))
        );

        // Thighs - straighter down with minimal outward angle
        scene.geometries.add(
                new Cylinder(3, new Ray(new Point(-3, -16, -50), new Vector(-0.1, -1, 0)), 8)
                        .setEmission(new Color(245, 245, 250))
                        .setMaterial(new Material().setKD(0.4).setKS(0.4).setShininess(70))
        );
        scene.geometries.add(
                new Cylinder(3, new Ray(new Point(3, -16, -50), new Vector(0.1, -1, 0)), 8)
                        .setEmission(new Color(245, 245, 250))
                        .setMaterial(new Material().setKD(0.4).setKS(0.4).setShininess(70))
        );

        // Knee joints - positioned more naturally
        scene.geometries.add(
                new Sphere(new Point(-3.8, -24, -50), 3.2)
                        .setEmission(new Color(220, 220, 225))
                        .setMaterial(new Material().setKD(0.4).setKS(0.4).setShininess(70))
        );
        scene.geometries.add(
                new Sphere(new Point(3.8, -24, -50), 3.2)
                        .setEmission(new Color(220, 220, 225))
                        .setMaterial(new Material().setKD(0.4).setKS(0.4).setShininess(70))
        );

        // Shins - straight down, no inward angle
        scene.geometries.add(
                new Cylinder(2.5, new Ray(new Point(-3.8, -27, -50), new Vector(0, -1, 0)), 7)
                        .setEmission(new Color(245, 245, 250))
                        .setMaterial(new Material().setKD(0.4).setKS(0.4).setShininess(70))
        );
        scene.geometries.add(
                new Cylinder(2.5, new Ray(new Point(3.8, -27, -50), new Vector(0, -1, 0)), 7)
                        .setEmission(new Color(245, 245, 250))
                        .setMaterial(new Material().setKD(0.4).setKS(0.4).setShininess(70))
        );

        // Ankle joints - aligned with shins
        scene.geometries.add(
                new Sphere(new Point(-3.8, -34, -50), 2.8)
                        .setEmission(new Color(220, 220, 225))
                        .setMaterial(new Material().setKD(0.4).setKS(0.4).setShininess(70))
        );
        scene.geometries.add(
                new Sphere(new Point(3.8, -34, -50), 2.8)
                        .setEmission(new Color(220, 220, 225))
                        .setMaterial(new Material().setKD(0.4).setKS(0.4).setShininess(70))
        );

        // Enhanced boots - aligned with ankles
        scene.geometries.add(
                new Cylinder(3.2, new Ray(new Point(-3.8, -36, -52), new Vector(0, 0, -1)), 4)
                        .setEmission(new Color(35, 35, 45))
                        .setMaterial(new Material().setKD(0.7).setKS(0.2).setShininess(30))
        );
        scene.geometries.add(
                new Cylinder(3.2, new Ray(new Point(3.8, -36, -52), new Vector(0, 0, -1)), 4)
                        .setEmission(new Color(35, 35, 45))
                        .setMaterial(new Material().setKD(0.7).setKS(0.2).setShininess(30))
        );

        // Boot soles - aligned with boots
        scene.geometries.add(
                new Cylinder(3.5, new Ray(new Point(-3.8, -36, -55), new Vector(0, -1, 0)), 1)
                        .setEmission(new Color(25, 25, 35))
                        .setMaterial(new Material().setKD(0.8).setKS(0.1).setShininess(20))
        );
        scene.geometries.add(
                new Cylinder(3.5, new Ray(new Point(3.8, -36, -55), new Vector(0, -1, 0)), 1)
                        .setEmission(new Color(25, 25, 35))
                        .setMaterial(new Material().setKD(0.8).setKS(0.1).setShininess(20))
        );

        // === ENHANCED LIFE SUPPORT BACKPACK ===
        scene.geometries.add(
                new Cylinder(5, new Ray(new Point(0, -5, -60), new Vector(0, 1, 0)), 15)
                        .setEmission(new Color(110, 115, 125))
                        .setMaterial(new Material().setKD(0.5).setKS(0.4).setShininess(60))
        );

        // Main oxygen tanks
        scene.geometries.add(
                new Cylinder(2, new Ray(new Point(-4, -5, -62), new Vector(0, 1, 0)), 12)
                        .setEmission(new Color(150, 155, 165))
                        .setMaterial(new Material().setKD(0.4).setKS(0.5).setShininess(80))
        );
        scene.geometries.add(
                new Cylinder(2, new Ray(new Point(4, -5, -62), new Vector(0, 1, 0)), 12)
                        .setEmission(new Color(150, 155, 165))
                        .setMaterial(new Material().setKD(0.4).setKS(0.5).setShininess(80))
        );

        // Tank tops
        scene.geometries.add(
                new Sphere(new Point(-4, 7, -62), 2.2)
                        .setEmission(new Color(180, 185, 195))
                        .setMaterial(new Material().setKD(0.4).setKS(0.5).setShininess(80))
        );
        scene.geometries.add(
                new Sphere(new Point(4, 7, -62), 2.2)
                        .setEmission(new Color(180, 185, 195))
                        .setMaterial(new Material().setKD(0.4).setKS(0.5).setShininess(80))
        );

        // Enhanced antenna system
        scene.geometries.add(
                new Cylinder(0.2, new Ray(new Point(0, 10, -58), new Vector(0, 1, 0)), 8)
                        .setEmission(new Color(200, 200, 210))
                        .setMaterial(new Material().setKD(0.3).setKS(0.6).setShininess(100))
        );

        // Antenna tip
        scene.geometries.add(
                new Sphere(new Point(0, 18, -58), 0.4)
                        .setEmission(new Color(255, 100, 100))
                        .setMaterial(new Material().setKD(0.2).setKS(0.7).setShininess(150))
        );

        // === SPACE ENVIRONMENT ===

        // Earth - more detailed
        scene.geometries.add(
                new Sphere(new Point(-120, -40, -300), 35)
                        .setEmission(new Color(65, 105, 160))
                        .setMaterial(new Material().setKD(0.1).setKS(0.1).setShininess(10))
        );

        // Moon
        scene.geometries.add(
                new Sphere(new Point(90, 60, -180), 18)
                        .setEmission(new Color(200, 200, 180))
                        .setMaterial(new Material().setKD(0.8).setKS(0.1).setShininess(5))
        );

        // Mars
        scene.geometries.add(
                new Sphere(new Point(50, -80, -350), 12)
                        .setEmission(new Color(190, 110, 70))
                        .setMaterial(new Material().setKD(0.7).setKS(0.1).setShininess(10))
        );

        // Enhanced starfield with more variety
        addStarField();

        // === ENHANCED LIGHTING ===

        // Minimal ambient for deep space
        scene.setAmbientLight(new AmbientLight(new Color(5, 8, 15)));

        // Main sunlight - brighter and more realistic
        scene.lights.add(
                new DirectionalLight(new Color(255, 250, 230), new Vector(-0.4, -0.3, -0.7))
        );

        // Earth's reflected light
        scene.lights.add(
                new PointLight(new Color(20, 35, 60), new Point(-90, -30, -150))
                        .setKL(0.002).setKQ(0.0001)
        );

        // Helmet interior illumination
        scene.lights.add(
                new PointLight(new Color(10, 15, 25), new Point(0, 12, -45))
                        .setKL(0.1).setKQ(0.015)
        );

        // Control panel glow
        scene.lights.add(
                new PointLight(new Color(0, 20, 40), new Point(0, 2, -40))
                        .setKL(0.3).setKQ(0.1)
        );

        // === CAMERA SETUP ===
        cameraBuilder
                .setLocation(new Point(0, -10, 100))
                .enableBVH()

                .setDirection(new Point(0, -5, -50), Vector.AXIS_Y)
                .setViewPlaneDistance(90)
                .enableBVH()
                .setViewPlaneSize(120, 120)
                .setResolution(1200, 1200)
                .build()
                .renderImage()
                .writeToImage("scenesTests/enhancedRealisticAstronaut");
    }

    private void addStarField() {
        // Bright main sequence stars
        scene.geometries.add(
                new Sphere(new Point(-60, 90, -250), 0.5)
                        .setEmission(new Color(255, 255, 255))
                        .setMaterial(new Material().setKD(0).setKS(0).setShininess(0))
        );
        scene.geometries.add(
                new Sphere(new Point(100, -60, -280), 0.4)
                        .setEmission(new Color(255, 240, 200))
                        .setMaterial(new Material().setKD(0).setKS(0).setShininess(0))
        );
        scene.geometries.add(
                new Sphere(new Point(-150, 70, -320), 0.45)
                        .setEmission(new Color(200, 220, 255))
                        .setMaterial(new Material().setKD(0).setKS(0).setShininess(0))
        );

        // Medium distance stars
        scene.geometries.add(
                new Sphere(new Point(130, 100, -400), 0.25)
                        .setEmission(new Color(255, 255, 255))
                        .setMaterial(new Material().setKD(0).setKS(0).setShininess(0))
        );
        scene.geometries.add(
                new Sphere(new Point(-170, -50, -380), 0.3)
                        .setEmission(new Color(220, 240, 255))
                        .setMaterial(new Material().setKD(0).setKS(0).setShininess(0))
        );
        scene.geometries.add(
                new Sphere(new Point(80, -140, -420), 0.22)
                        .setEmission(new Color(255, 220, 180))
                        .setMaterial(new Material().setKD(0).setKS(0).setShininess(0))
        );

        // Distant stars
        scene.geometries.add(
                new Sphere(new Point(180, 80, -500), 0.15)
                        .setEmission(new Color(255, 255, 255))
                        .setMaterial(new Material().setKD(0).setKS(0).setShininess(0))
        );
        scene.geometries.add(
                new Sphere(new Point(-200, -90, -480), 0.12)
                        .setEmission(new Color(200, 200, 255))
                        .setMaterial(new Material().setKD(0).setKS(0).setShininess(0))
        );
        scene.geometries.add(
                new Sphere(new Point(160, -120, -550), 0.18)
                        .setEmission(new Color(255, 240, 200))
                        .setMaterial(new Material().setKD(0).setKS(0).setShininess(0))
        );

        // Very distant background stars
        scene.geometries.add(
                new Sphere(new Point(-250, 170, -600), 0.1)
                        .setEmission(new Color(255, 200, 150))
                        .setMaterial(new Material().setKD(0).setKS(0).setShininess(0))
        );
        scene.geometries.add(
                new Sphere(new Point(220, -160, -580), 0.11)
                        .setEmission(new Color(255, 255, 255))
                        .setMaterial(new Material().setKD(0).setKS(0).setShininess(0))
        );
        scene.geometries.add(
                new Sphere(new Point(-180, 200, -620), 0.09)
                        .setEmission(new Color(180, 200, 255))
                        .setMaterial(new Material().setKD(0).setKS(0).setShininess(0))
        );
    }
}