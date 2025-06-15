package renderer;

import geometries.*;
import lighting.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

public class CityTest {
    @Test
    public void testWavySeaSunset() {
        Scene scene = new Scene("Wavy Sea Sunset");

        // Set ambient light (very low for dramatic sunset effect)
        scene.setAmbientLight(new AmbientLight(new Color(15, 10, 5)));

        // Gradient sunset background - deeper orange/red
        scene.setBackground(new Color(255, 120, 40)); // warmer sunset

        // === LIGHTING SETUP ===

        // Main sun light - positioned for fixed camera view
        scene.lights.add(new DirectionalLight(
                new Color(1000, 800, 400), // bright golden sun
                new Vector(-0.2, -0.3, -0.9).normalize() // adjusted for camera view
        ));

        // Warm fill light for sunset atmosphere
        scene.lights.add(new DirectionalLight(
                new Color(200, 150, 80), // secondary warm light
                new Vector(0.1, -0.2, -0.9).normalize()
        ));

        // Sky light positioned in camera view
        scene.lights.add(new PointLight(
                new Color(150, 120, 60), // warm ambient
                new Point(0, 80, -50) // positioned for camera at (0,0,400)
        ).setKL(0.001).setKQ(0.0001));

        // === SUN DISC ===
        // Positioned to be visible from camera at (0,0,400)
        scene.geometries.add(new Sphere(new Point(-30, 25, -60), 12)
                .setEmission(new Color(255, 240, 150))
                .setMaterial(new Material().setKD(0).setKS(0).setShininess(0)));

        // Sun glow effect (multiple transparent spheres)
        scene.geometries.add(new Sphere(new Point(-30, 25, -60), 16)
                .setEmission(new Color(255, 200, 100))
                .setMaterial(new Material().setKD(0).setKS(0).setKT(0.8)));

        scene.geometries.add(new Sphere(new Point(-30, 25, -60), 20)
                .setEmission(new Color(255, 180, 80))
                .setMaterial(new Material().setKD(0).setKS(0).setKT(0.9)));

        // === ENDLESS SEA SURFACE ===
        // Create a large sea that extends infinitely in all directions
        double seaSize = 1500;       // Larger sea for full coverage
        int steps = 250;             // Higher resolution for smooth reflection
        double waveHeight = 1.5;     // Gentler waves for smoother reflection
        double waveLength = 30;      // Longer wavelength for smoother surface
        double waveLength2 = 20;     // Longer secondary wavelength

        // Enhanced sea material with better reflection properties
        Material seaMaterial = new Material()
                .setKD(0.05)    // Very low diffuse
                .setKS(0.95)    // Very high specular
                .setKR(0.85)    // High reflection
                .setShininess(200); // High shininess for water

        // Dark sea color that will reflect the sunset
        Color seaColor = new Color(10, 20, 35);

        double stepSize = seaSize / steps;
        double half = seaSize / 2;

        // Position sea to be visible from camera at (0,0,400) looking toward (0,0,-10)
        // The sea should extend from just in front of camera to far distance
        double seaStartZ = 350;   // Start even closer to camera
        double seaEndZ = -800;    // Extend further into distance
        double seaDepth = seaStartZ - seaEndZ;

        // Create sea surface extending in all directions
        for (int i = 0; i < steps; i++) {
            for (int j = 0; j < steps; j++) {
                double x0 = -half + i * stepSize;
                double x1 = x0 + stepSize;

                // Map j to z coordinates from near camera to far distance
                double zFactor0 = (double) j / steps;
                double zFactor1 = (double) (j + 1) / steps;
                double z0 = seaStartZ - zFactor0 * seaDepth;
                double z1 = seaStartZ - zFactor1 * seaDepth;

                // Calculate wave heights - position sea level lower to fill bottom of view
                double baseSeaLevel = -80; // Lower sea level to fill bottom half
                Point p00 = new Point(x0, baseSeaLevel + enhancedWaveY(x0, z0, waveHeight, waveLength, waveLength2), z0);
                Point p01 = new Point(x0, baseSeaLevel + enhancedWaveY(x0, z1, waveHeight, waveLength, waveLength2), z1);
                Point p10 = new Point(x1, baseSeaLevel + enhancedWaveY(x1, z0, waveHeight, waveLength, waveLength2), z0);
                Point p11 = new Point(x1, baseSeaLevel + enhancedWaveY(x1, z1, waveHeight, waveLength, waveLength2), z1);

                // Create triangles
                scene.geometries.add(new Triangle(p00, p10, p11)
                        .setEmission(seaColor)
                        .setMaterial(seaMaterial));

                scene.geometries.add(new Triangle(p00, p11, p01)
                        .setEmission(seaColor)
                        .setMaterial(seaMaterial));
            }
        }

        // === CAMERA SETUP ===
        Camera.Builder cameraBuilder = Camera.getBuilder()
                .setRayTracer(scene, RayTracerType.SIMPLE);

        cameraBuilder
                .setLocation(new Point(0, 0, 400))
                .setDirection(new Point(0, 0, -10), new Vector(0, 1, 0))
                .setViewPlaneDistance(120)
                .setResolution(1024, 1024) // Square resolution
                .setViewPlaneSize(160, 160) // Square aspect ratio
                .setMultithreading(4)
                .setDebugPrint(1)
                .build()
                .renderImage()
                .writeToImage("wavySeaSunset");
    }

    /**
     * Add cloud layer to create dramatic sky - positioned for fixed camera
     */
    private void addCloudLayer(Scene scene, double z, double height, Color cloudColor) {
        Material cloudMaterial = new Material()
                .setKD(0.6)
                .setKS(0.2)
                .setKT(0.4) // Semi-transparent
                .setShininess(15);

        // Create cloud shapes positioned for camera at (0,0,400)
        for (int i = 0; i < 6; i++) {
            double x = -80 + i * 30 + (Math.random() - 0.5) * 15;
            double y = height + (Math.random() - 0.5) * 8;

            // Create overlapping cloud shapes
            scene.geometries.add(new Sphere(new Point(x, y, z), 12 + Math.random() * 8)
                    .setEmission(cloudColor)
                    .setMaterial(cloudMaterial));

            scene.geometries.add(new Sphere(new Point(x + 8, y - 3, z), 10 + Math.random() * 6)
                    .setEmission(cloudColor)
                    .setMaterial(cloudMaterial));

            scene.geometries.add(new Sphere(new Point(x - 6, y + 2, z), 8 + Math.random() * 5)
                    .setEmission(cloudColor)
                    .setMaterial(cloudMaterial));
        }
    }

    /**
     * Simplified wave function for smoother, more continuous reflections
     */
    private static double enhancedWaveY(double x, double z, double amplitude, double wavelength1, double wavelength2) {
        // Primary wave system - smoother pattern
        double wave1 = Math.sin((x + z) / wavelength1) * amplitude * 0.7;

        // Secondary wave system - gentler perpendicular waves
        double wave2 = Math.sin((x - z * 0.5) / wavelength2) * amplitude * 0.4;

        // Very gentle large swell for ocean-like movement
        double swell = Math.sin((x + z) / (wavelength1 * 3)) * amplitude * 0.3;

        // Combine wave systems for realistic but smooth water surface
        return wave1 + wave2 + swell;
    }

    /**
     * Adjust sea color based on wave height for more realistic appearance
     */
    private static Color adjustColorByHeight(Color baseColor, double height) {
        double factor = 1.0 + height * 0.1; // Slight variation based on height
        factor = Math.max(0.8, Math.min(1.2, factor)); // Clamp the factor

        return new Color(
                (int) (baseColor.getColor().getRed() * factor),
                (int) (baseColor.getColor().getGreen() * factor),
                (int) (baseColor.getColor().getBlue() * factor)
        );
    }
}