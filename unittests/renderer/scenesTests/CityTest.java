//package renderer;
//
//import geometries.*;
//import lighting.*;
//import org.junit.jupiter.api.Test;
//import primitives.*;
//import scene.Scene;
//
//public class CityTest {
//    @Test
//    public void testWavySeaSunset() {
//        Scene scene = new Scene("Wavy Sea Sunset");
//
//        // Set ambient light (very low for dramatic sunset effect)
//        scene.setAmbientLight(new AmbientLight(new Color(15, 10, 5)));
//
//        // Gradient sunset background - deeper orange/red
//        scene.setBackground(new Color(255, 120, 40)); // warmer sunset
//
//        // === LIGHTING SETUP ===
//
//        // Main sun light - positioned for fixed camera view
//        scene.lights.add(new DirectionalLight(
//                new Color(1000, 800, 400), // bright golden sun
//                new Vector(-0.2, -0.3, -0.9).normalize() // adjusted for camera view
//        ));
//
//        // Warm fill light for sunset atmosphere
//        scene.lights.add(new DirectionalLight(
//                new Color(200, 150, 80), // secondary warm light
//                new Vector(0.1, -0.2, -0.9).normalize()
//        ));
//
//        // Sky light positioned in camera view
//        scene.lights.add(new PointLight(
//                new Color(150, 120, 60), // warm ambient
//                new Point(0, 80, -50) // positioned for camera at (0,0,400)
//        ).setKL(0.001).setKQ(0.0001));
//
//        // === SUN DISC ===
//        // Positioned to be visible from camera at (0,0,400)
//        scene.geometries.add(new Sphere(new Point(-30, 25, -60), 12)
//                .setEmission(new Color(255, 240, 150))
//                .setMaterial(new Material().setKD(0).setKS(0).setShininess(0)));
//
//        // Sun glow effect (multiple transparent spheres)
//        scene.geometries.add(new Sphere(new Point(-30, 25, -60), 16)
//                .setEmission(new Color(255, 200, 100))
//                .setMaterial(new Material().setKD(0).setKS(0).setKT(0.8)));
//
//        scene.geometries.add(new Sphere(new Point(-30, 25, -60), 20)
//                .setEmission(new Color(255, 180, 80))
//                .setMaterial(new Material().setKD(0).setKS(0).setKT(0.9)));
//
//        // === ENHANCED SEA SURFACE FOR REALISTIC SUN PATH ===
//        double seaSize = 1500;
//        int steps = 300;             // Higher resolution for better light scattering
//        double waveHeight = 2.5;     // More pronounced waves
//        double waveLength = 25;
//        double waveLength2 = 15;
//
//        // Adjusted sea material for more realistic water behavior
//        Material seaMaterial = new Material()
//                .setKD(0.15)    // Slightly more diffuse reflection
//                .setKS(0.75)    // Reduced specular for less mirror-like effect
//                .setKR(0.65)    // Reduced reflection coefficient
//                .setShininess(120); // Lower shininess for broader light scattering
//
//        Color seaColor = new Color(8, 18, 32); // Slightly darker base color
//
//        double stepSize = seaSize / steps;
//        double half = seaSize / 2;
//
//        double seaStartZ = 350;
//        double seaEndZ = -800;
//        double seaDepth = seaStartZ - seaEndZ;
//
//        // Create more varied sea surface for realistic light scattering
//        for (int i = 0; i < steps; i++) {
//            for (int j = 0; j < steps; j++) {
//                double x0 = -half + i * stepSize;
//                double x1 = x0 + stepSize;
//
//                double zFactor0 = (double) j / steps;
//                double zFactor1 = (double) (j + 1) / steps;
//                double z0 = seaStartZ - zFactor0 * seaDepth;
//                double z1 = seaStartZ - zFactor1 * seaDepth;
//
//                double baseSeaLevel = -80;
//
//                // Enhanced wave function with more variation
//                Point p00 = new Point(x0, baseSeaLevel + realisticWaveY(x0, z0, waveHeight, waveLength, waveLength2), z0);
//                Point p01 = new Point(x0, baseSeaLevel + realisticWaveY(x0, z1, waveHeight, waveLength, waveLength2), z1);
//                Point p10 = new Point(x1, baseSeaLevel + realisticWaveY(x1, z0, waveHeight, waveLength, waveLength2), z0);
//                Point p11 = new Point(x1, baseSeaLevel + realisticWaveY(x1, z1, waveHeight, waveLength, waveLength2), z1);
//
//                // Create triangles with slightly varied materials for more realistic scattering
//                Material triangleMaterial = createVariedSeaMaterial(seaMaterial, i, j);
//
//                scene.geometries.add(new Triangle(p00, p10, p11)
//                        .setEmission(seaColor)
//                        .setMaterial(triangleMaterial));
//
//                scene.geometries.add(new Triangle(p00, p11, p01)
//                        .setEmission(seaColor)
//                        .setMaterial(triangleMaterial));
//            }
//        }
//
//        // === CAMERA SETUP ===
//        Camera.Builder cameraBuilder = Camera.getBuilder()
//                .setRayTracer(scene, RayTracerType.SIMPLE);
//
//        cameraBuilder
//                .setLocation(new Point(0, 0, 400))
//                .setDirection(new Point(0, 0, -10), new Vector(0, 1, 0))
//                .setViewPlaneDistance(120)
//                .setResolution(1024, 1024) // Square resolution
//                .setViewPlaneSize(160, 160) // Square aspect ratio
//                .setMultithreading(4)
//                .setDebugPrint(1)
//                .build()
//                .renderImage()
//                .writeToImage("wavySeaSunset");
//    }
//
//
//    /**
//     * Enhanced wave function for more realistic water surface
//     */
//    private static double realisticWaveY(double x, double z, double amplitude, double wavelength1, double wavelength2) {
//        // Primary wave system with more variation
//        double wave1 = Math.sin((x + z * 0.8) / wavelength1) * amplitude * 0.6;
//
//        // Secondary perpendicular waves
//        double wave2 = Math.sin((x - z * 0.4) / wavelength2) * amplitude * 0.4;
//
//        // Add cross waves for more realistic surface
//        double crossWave = Math.sin((x * 0.7 + z * 1.2) / (wavelength1 * 0.8)) * amplitude * 0.3;
//
//        // Small random-like variations using multiple sine waves
//        double detail1 = Math.sin(x / (wavelength2 * 0.3)) * amplitude * 0.15;
//        double detail2 = Math.sin(z / (wavelength2 * 0.4)) * amplitude * 0.12;
//
//        // Large ocean swell
//        double swell = Math.sin((x + z) / (wavelength1 * 4)) * amplitude * 0.25;
//
//        return wave1 + wave2 + crossWave + detail1 + detail2 + swell;
//    }
//
//    /**
//     * Create slightly varied materials for different parts of the sea surface
//     * This helps create the scattered light path effect
//     */
//    private static Material createVariedSeaMaterial(Material baseMaterial, int i, int j) {
//        // Create slight variations in material properties
//        double variation = Math.sin(i * 0.1 + j * 0.15) * 0.1 + 1.0;
//
//        // Vary the shininess slightly to create different reflection patterns
//        int shininess = (int) (120 + Math.sin(i * 0.2 + j * 0.1) * 30);
//        shininess = Math.max(80, Math.min(160, shininess));
//
//        // Slightly vary specular reflection
//        double ks = 0.75 + Math.sin(i * 0.15 + j * 0.2) * 0.1;
//        ks = Math.max(0.6, Math.min(0.85, ks));
//
//        return new Material()
//                .setKD(0.15)
//                .setKS(ks)
//                .setKR(0.65)
//                .setShininess(shininess);
//    }
//
//    /**
//     * Add cloud layer to create dramatic sky - positioned for fixed camera
//     */
//    private void addCloudLayer(Scene scene, double z, double height, Color cloudColor) {
//        Material cloudMaterial = new Material()
//                .setKD(0.6)
//                .setKS(0.2)
//                .setKT(0.4) // Semi-transparent
//                .setShininess(15);
//
//        // Create cloud shapes positioned for camera at (0,0,400)
//        for (int i = 0; i < 6; i++) {
//            double x = -80 + i * 30 + (Math.random() - 0.5) * 15;
//            double y = height + (Math.random() - 0.5) * 8;
//
//            // Create overlapping cloud shapes
//            scene.geometries.add(new Sphere(new Point(x, y, z), 12 + Math.random() * 8)
//                    .setEmission(cloudColor)
//                    .setMaterial(cloudMaterial));
//
//            scene.geometries.add(new Sphere(new Point(x + 8, y - 3, z), 10 + Math.random() * 6)
//                    .setEmission(cloudColor)
//                    .setMaterial(cloudMaterial));
//
//            scene.geometries.add(new Sphere(new Point(x - 6, y + 2, z), 8 + Math.random() * 5)
//                    .setEmission(cloudColor)
//                    .setMaterial(cloudMaterial));
//        }
//    }
//
//    /**
//     * Adjust sea color based on wave height for more realistic appearance
//     */
//    private static Color adjustColorByHeight(Color baseColor, double height) {
//        double factor = 1.0 + height * 0.1; // Slight variation based on height
//        factor = Math.max(0.8, Math.min(1.2, factor)); // Clamp the factor
//
//        return new Color(
//                (int) (baseColor.getColor().getRed() * factor),
//                (int) (baseColor.getColor().getGreen() * factor),
//                (int) (baseColor.getColor().getBlue() * factor)
//        );
//    }
//}

package renderer.scenesTests;

import geometries.*;
import lighting.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import renderer.Camera;
import renderer.RayTracerType;
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
        double seaSize = 1500;       // Large sea for full coverage
        int steps = 200;             // High resolution for smooth surface
        double waveHeight = 1.5;     // Restore better wave height
        double waveLength = 25;      // Original wavelength
        double waveLength2 = 15;     // Original secondary wavelength

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
        double seaEndZ = -800;    // Extend even further into distance
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

                // Calculate wave heights - position sea level much lower to fill entire bottom half
                double baseSeaLevel = -80; // Much lower sea level to fill entire bottom of view
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
                .setResolution(1024, 1024) // Changed resolution to match image
                .setViewPlaneSize(160, 160) // Adjusted view plane size for square aspect ratio
                .setMultithreading(-2)
                .setDebugPrint(1)
                .build()
                .renderImage()
                .writeToImage("scenesTests/wavySeaSunset");
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
     * Enhanced wave function with more realistic water movement
     */
    private static double enhancedWaveY(double x, double z, double amplitude, double wavelength1, double wavelength2) {
        // Primary wave system (diagonal)
        double wave1 = Math.sin((x + z * 0.8) / wavelength1) * amplitude;

        // Secondary wave system (perpendicular)
        double wave2 = Math.sin((x * 0.7 - z) / wavelength2) * amplitude * 0.6;

        // Tertiary small ripples
        double wave3 = Math.sin(x / (wavelength1 * 0.3)) * Math.cos(z / (wavelength2 * 0.4)) * amplitude * 0.2;

        // Large swell
        double swell = Math.sin((x + z) / (wavelength1 * 2)) * amplitude * 0.4;

        // Combine all wave systems
        return wave1 + wave2 + wave3 + swell;
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