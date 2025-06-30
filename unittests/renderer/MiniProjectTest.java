package renderer;

import geometries.*;
import lighting.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

public class MiniProjectTest {

    private final Scene scene = new Scene("miniProject");
    private final Camera.Builder cameraBuilder = Camera.getBuilder()
            .setRayTracer(scene, RayTracerType.SIMPLE);

    @Test
    void TestManhattanScene() {
        // Adjusted ambient light for a darker, more realistic night scene
        scene.setAmbientLight(new AmbientLight(new Color(8, 8, 12)));

        // Diverse building colors
        Color[] buildingColors = {
                new Color(255, 200, 0),
                new Color(200, 70, 70),
                new Color(0, 130, 255),
                new Color(255, 100, 0),
                new Color(100, 200, 255),
                new Color(50, 160, 60),
                new Color(180, 80, 180),
                new Color(250, 250, 90),
                new Color(80, 80, 255)
        };

        // Island setup - create a thin horizon line effect with smaller radius
        double seaLevel = 0;
        double islandHeight = 0.3;  // Very thin - just barely visible as horizon line
        double islandRadius = 750;  // Increased to 750
        Point islandCenter = new Point(0, seaLevel, -950);

        // Create island cylinder - very thin horizon line with improved material
        scene.geometries.add(
                new Cylinder(islandRadius, new Ray(islandCenter, new Vector(0, 1, 0)), islandHeight)
                        .setEmission(new Color(60, 50, 30))  // More visible color
                        .setMaterial(new Material()
                                .setKD(0.9)     // Higher diffuse for better visibility
                                .setKS(0.2)     // Slight specular for texture
                                .setShininess(20)  // Low shininess for natural look
                                .setKR(0.02))   // Minimal reflection
        );

        // Building positions - better spaced arrangement
        int[][] positions = {
                {-500, -120, 240}, {-300, -20, 320}, {-150, -150, 280}, {0, 0, 200},
                {150, -80, 380}, {300, -40, 260}, {450, -130, 340}, {-400, -180, 300},
                {100, -100, 250}, {-100, 40, 220}, {350, 0, 290}, {-550, -90, 310},
                {-650, -50, 225}, {550, -70, 255}, {-200, -200, 245}, {50, 60, 275},
                {-450, 20, 350}, {400, -160, 305}, {-50, -120, 285}, {-250, -10, 195}
        };

        // Create buildings - make them much taller
        for (int i = 0; i < 20; i++) {
            int relativeX = positions[i][0];
            int relativeZ = positions[i][1];
            int height = positions[i][2];

            double worldX = islandCenter.get_xyz().d1() + relativeX;
            double worldZ = islandCenter.get_xyz().d3() + relativeZ;
            double islandSurfaceY = seaLevel + islandHeight;

            Point p1 = new Point(worldX - 30, islandSurfaceY, worldZ - 30);
            Point p2 = new Point(worldX + 30, islandSurfaceY + height, worldZ + 30);

            scene.geometries.add(
                    createBox(p1, p2, buildingColors[i % buildingColors.length],
                            new Material().setKD(0.6).setKS(0.3).setShininess(40))
            );

            // Windows with emission to act as light sources
            addBuildingWindows(p1, p2, new Color(40, 40, 80),
                    new Material().setKD(0.2).setKS(0.8).setShininess(150).setKT(0.3));
        }

        // === SEA SURFACE DISABLED ===
        createAdvancedSeaSurface(seaLevel);  // COMMENTED OUT

        // Add stars - make them bigger and more visible
        Color starColor = new Color(255, 255, 220);  // Brighter star color
        int numStars = 700;

        for (int i = 0; i < numStars; i++) {
            double starX = (Math.random() * 4000) - 2000;  // Wider spread
            double starY = 800 + (Math.random() * 1200);   // Much higher up
            double starZ = -8000 + (Math.random() * 6000); // Much further away
            double starSize = 2.0 + Math.random() * 3.0;   // Increased from 0.5-2.0 to 2.0-5.0

            scene.geometries.add(createStar(starX, starY, starZ, starSize, starColor));
        }

        // Add moon sphere - bigger and moved further right
        Point moonCenter = new Point(2000, 2400, -8000);  // Moved further right (1600->2000)
        scene.geometries.add(
                new Sphere(moonCenter, 500)  // Increased size from 400 to 500
                        .setEmission(new Color(200, 200, 180))
                        .setMaterial(new Material().setKD(0.6).setKS(0.2).setShininess(15))
        );

        // Add airplane
        scene.geometries.add(createAirplane(
                new Point(-300, 740, -900),
                12.0,
                new Color(180, 180, 200),
                new Material().setKD(0.3).setKS(0.7).setShininess(300).setKR(0.1)
        ));

        // === ENHANCED LIGHTING FOR GLITTER PATH EFFECTS ===
        setupAdvancedLighting(moonCenter);

        // Camera setup
        cameraBuilder
                .setLocation(new Point(0, 60, 500))
                .setDirection(
                        new Vector(0, -60, -1400).normalize(),
                        new Vector(0, 1400, -60).normalize()
                )
                .setViewPlaneDistance(120)
                .setResolution(1000, 1000)
                .setMultithreading(-2)
                .setDebugPrint(1)
                .setViewPlaneSize(200, 200)
                .setNumRays(17*17)
                .enableBVH()
                .build()
                .renderImage()
                .writeToImage("finalImage");  // Different filename
    }

    /**
     * Creates an advanced sea surface optimized for natural glitter path reflections
     */
    private void createAdvancedSeaSurface(double seaLevel) {
        double seaSize = 2000;
        int steps = 700;  // Higher resolution for better reflections
        double baseWaveHeight = 1.2;

        // Optimized material for realistic water reflections
        Material seaMaterial = new Material()
                .setKD(0.02)    // Very low diffuse - water is mostly reflective
                .setKS(0.08)    // Low specular to avoid competing with reflections
                .setKR(0.92)    // Very high reflection coefficient - key for glitter paths
                .setShininess(400)  // Very sharp reflections
                .setKT(0.03);   // Minimal transparency

        Color seaColor = new Color(2, 8, 15);  // Very dark water

        double stepSize = seaSize / steps;
        double half = seaSize / 2;
        double seaNearZ = 600;
        double seaFarZ = -1000;
        double seaDepth = seaNearZ - seaFarZ;

        // Use fixed random seed for consistent results
        java.util.Random random = new java.util.Random(54321);

        for (int i = 0; i < steps; i++) {
            for (int j = 0; j < steps; j++) {
                double x0 = -half + i * stepSize;
                double x1 = x0 + stepSize;

                double zFactor0 = (double) j / steps;
                double zFactor1 = (double) (j + 1) / steps;
                double z0 = seaNearZ - zFactor0 * seaDepth;
                double z1 = seaNearZ - zFactor1 * seaDepth;

                // Create complex wave patterns
                double wave0_0 = generateComplexWaves(x0, z0, baseWaveHeight, zFactor0, random);
                double wave0_1 = generateComplexWaves(x0, z1, baseWaveHeight, zFactor1, random);
                double wave1_0 = generateComplexWaves(x1, z0, baseWaveHeight, zFactor0, random);
                double wave1_1 = generateComplexWaves(x1, z1, baseWaveHeight, zFactor1, random);

                Point p00 = new Point(x0, seaLevel + wave0_0, z0);
                Point p01 = new Point(x0, seaLevel + wave0_1, z1);
                Point p10 = new Point(x1, seaLevel + wave1_0, z0);
                Point p11 = new Point(x1, seaLevel + wave1_1, z1);

                // Create triangles with optimized material
                scene.geometries.add(new Triangle(p00, p10, p11)
                        .setEmission(seaColor)
                        .setMaterial(seaMaterial));

                scene.geometries.add(new Triangle(p00, p11, p01)
                        .setEmission(seaColor)
                        .setMaterial(seaMaterial));
            }
        }
    }

    /**
     * Generates complex, realistic wave patterns
     */
    private double generateComplexWaves(double x, double z, double amplitude, double distanceFactor, java.util.Random random) {
        // Large ocean swells
        double primarySwell = Math.sin(x / 150.0) * Math.cos(z / 100.0) * amplitude * 0.35;
        double secondarySwell = Math.sin((x * 0.7 + z * 0.9) / 120.0) * amplitude * 0.25;

        // Wind-driven waves at multiple scales
        double windWave1 = Math.sin(x / 30.0) * Math.cos(z / 35.0) * amplitude * 0.3;
        double windWave2 = Math.sin((x * 0.9 - z * 0.7) / 25.0) * amplitude * 0.25;
        double windWave3 = Math.sin((x * 1.1 + z * 0.5) / 40.0) * amplitude * 0.2;

        // Capillary waves for surface texture
        double capillary1 = Math.sin(x / 10.0) * Math.cos(z / 8.0) * amplitude * 0.15;
        double capillary2 = Math.sin((x * 1.3 + z * 0.8) / 7.0) * amplitude * 0.12;
        double capillary3 = Math.sin((x * 0.6 - z * 1.2) / 9.0) * amplitude * 0.1;

        // Cross-waves for complexity
        double crossWave1 = Math.sin((x * 0.8 + z * 1.2) / 18.0) * amplitude * 0.12;
        double crossWave2 = Math.sin((x * 1.4 - z * 0.8) / 22.0) * amplitude * 0.1;

        // Micro-ripples and random variations
        double microRipples = Math.sin(x / 4.0) * Math.sin(z / 5.0) * amplitude * 0.06;
        double randomVariation = random.nextGaussian() * amplitude * 0.08;

        // Combine all components
        double totalWave = primarySwell + secondarySwell +
                windWave1 + windWave2 + windWave3 +
                capillary1 + capillary2 + capillary3 +
                crossWave1 + crossWave2 + microRipples + randomVariation;

        // Distance-based amplitude adjustment
        double amplitudeFactor = 1.0 - distanceFactor * 0.15;

        return totalWave * amplitudeFactor;
    }

    /**
     * Sets up advanced lighting for optimal glitter path visibility
     */
    private void setupAdvancedLighting(Point moonCenter) {
        // Primary moon light - bright enough to create strong glitter path
        scene.lights.add(
                new PointLight(new Color(280, 280, 240), moonCenter)
                        .setKL(0.000003)
                        .setKQ(0.00000008)
        );

        // Secondary moon light for enhanced glitter effect
        scene.lights.add(
                new PointLight(new Color(200, 200, 180), moonCenter.add(new Vector(100, -50, 200)))
                        .setKL(0.000008)
                        .setKQ(0.0000003)
        );

        // Ambient sky light
        scene.lights.add(
                new DirectionalLight(new Color(25, 25, 40), new Vector(-0.1, -0.9, -0.4))
        );

        // Building lights that will also create glitter paths
        scene.lights.add(
                new PointLight(new Color(255, 180, 80), new Point(-300, 200, -200))
                        .setKL(0.00015)
                        .setKQ(0.00002)
        );

        scene.lights.add(
                new PointLight(new Color(80, 180, 255), new Point(400, 250, -300))
                        .setKL(0.00012)
                        .setKQ(0.000015)
        );

        scene.lights.add(
                new PointLight(new Color(255, 120, 180), new Point(0, 180, -100))
                        .setKL(0.0002)
                        .setKQ(0.00003)
        );
    }

    // Helper method to create a simple airplane
    private Geometries createAirplane(Point center, double scale, Color emission, Material material) {
        Geometries airplane = new Geometries();

        double x = center.get_xyz().d1();
        double y = center.get_xyz().d2();
        double z = center.get_xyz().d3();

        // Fuselage (main body)
        airplane.add(createBox(
                new Point(x - 3 * scale, y - 0.5 * scale, z - 0.5 * scale),
                new Point(x + 3 * scale, y + 0.5 * scale, z + 0.5 * scale),
                emission, material
        ));

        // Wings
        airplane.add(createBox(
                new Point(x - 0.5 * scale, y - 4 * scale, z - 0.2 * scale),
                new Point(x + 0.5 * scale, y + 4 * scale, z + 0.2 * scale),
                emission, material
        ));

        // Tail
        airplane.add(createBox(
                new Point(x - 3 * scale, y - 0.2 * scale, z - 2 * scale),
                new Point(x - 2 * scale, y + 0.2 * scale, z + 2 * scale),
                emission, material
        ));

        return airplane;
    }

    /**
     * Creates a 2D star from triangles, positioned on the XZ plane at a given location.
     * The material is set to primarily emit light, simulating a star.
     *
     * @param centerX X coordinate of the star center.
     * @param centerY Y coordinate (height) of the star.
     * @param centerZ Z coordinate of the star center.
     * @param size    Radius of the star.
     * @param color   Emission color of the star.
     * @return A Geometries object representing the star.
     */
    public Geometries createStar(double centerX, double centerY, double centerZ, double size, Color color) {
        Geometries star = new Geometries();
        Point center = new Point(centerX, centerY, centerZ);
        int spikes = 5;
        double angleStep = Math.PI / spikes;

        for (int i = 0; i < 2 * spikes; i++) {
            double outerRadius = (i % 2 == 0) ? size : size * 0.5;

            double angle1 = i * angleStep;
            double angle2 = (i + 1) * angleStep;

            Point p1 = new Point(
                    centerX + outerRadius * Math.cos(angle1),
                    centerY,
                    centerZ + outerRadius * Math.sin(angle1)
            );

            Point p2 = new Point(
                    centerX + outerRadius * Math.cos(angle2),
                    centerY,
                    centerZ + outerRadius * Math.sin(angle2)
            );

            // Stars are primarily emissive, with minimal reflection
            star.add(
                    new Triangle(p1, p2, center).setEmission(color)
                            .setMaterial(new Material().setKS(0).setShininess(0)) // No specularity for a star
            );
        }

        return star;
    }

    /**
     * Adds simple rectangular windows to a building defined by two corner points.
     *
     * @param p1             Building's first corner point (min x, min y, min z).
     * @param p2             Building's second corner point (max x, max y, max z).
     * @param windowEmission The emission color of the windows.
     * @param windowMaterial The material properties of the windows.
     */
    private void addBuildingWindows(Point p1, Point p2, Color windowEmission, Material windowMaterial) {
        double x1 = Math.min(p1.get_xyz().d1(), p2.get_xyz().d1());
        double x2 = Math.max(p1.get_xyz().d1(), p2.get_xyz().d1());
        double y1 = Math.min(p1.get_xyz().d2(), p2.get_xyz().d2());
        double y2 = Math.max(p1.get_xyz().d2(), p2.get_xyz().d2());
        double z1 = Math.min(p1.get_xyz().d3(), p2.get_xyz().d3());
        double z2 = Math.max(p1.get_xyz().d3(), p2.get_xyz().d3());

        double windowWidth = (x2 - x1) * 0.2; // 20% of building width
        double windowHeight = (y2 - y1) * 0.1; // 10% of building height
        double hSpace = (x2 - x1) * 0.15; // Horizontal spacing between windows
        double vSpace = (y2 - y1) * 0.1;  // Vertical spacing between windows

        // Iterate through front and side faces
        // Adjust z for the front face to be slightly in front of the building face
        double frontZ = (p1.get_xyz().d3() + p2.get_xyz().d3()) / 2 > 0 ? Math.max(z1, z2) + 0.1 : Math.min(z1, z2) - 0.1;

        for (double y = y1 + vSpace; y < y2 - windowHeight; y += windowHeight + vSpace) {
            for (double x = x1 + hSpace; x < x2 - windowWidth; x += windowWidth + hSpace) {
                // Add window on the front face
                scene.geometries.add(
                        new Polygon(
                                new Point(x, y, frontZ),
                                new Point(x + windowWidth, y, frontZ),
                                new Point(x + windowWidth, y + windowHeight, frontZ),
                                new Point(x, y + windowHeight, frontZ)
                        )
                                .setEmission(windowEmission)
                                .setMaterial(windowMaterial)
                );
            }
        }
    }

    /**
     * Creates a 3D box (cuboid) from two opposite corner points.
     *
     * @param p1       The first corner point.
     * @param p2       The second corner point, opposite to p1.
     * @param emission The emission color of the box.
     * @param material The material properties of the box.
     * @return A Geometries object containing the six polygons forming the box.
     */
    public Geometries createBox(Point p1, Point p2, Color emission, Material material) {
        Geometries box = new Geometries();

        double x1 = Math.min(p1.get_xyz().d1(), p2.get_xyz().d1());
        double x2 = Math.max(p1.get_xyz().d1(), p2.get_xyz().d1());
        double y1 = Math.min(p1.get_xyz().d2(), p2.get_xyz().d2());
        double y2 = Math.max(p1.get_xyz().d2(), p2.get_xyz().d2());
        double z1 = Math.min(p1.get_xyz().d3(), p2.get_xyz().d3());
        double z2 = Math.max(p1.get_xyz().d3(), p2.get_xyz().d3());

        Point A = new Point(x1, y1, z1);
        Point B = new Point(x2, y1, z1);
        Point C = new Point(x2, y2, z1);
        Point D = new Point(x1, y2, z1);
        Point E = new Point(x1, y1, z2);
        Point F = new Point(x2, y1, z2);
        Point G = new Point(x2, y2, z2);
        Point H = new Point(x1, y2, z2);

        box.add(
                new Polygon(D, C, B, A).setEmission(emission).setMaterial(material), // Bottom face
                new Polygon(E, F, G, H).setEmission(emission).setMaterial(material), // Top face
                new Polygon(A, B, F, E).setEmission(emission).setMaterial(material), // Front face
                new Polygon(B, C, G, F).setEmission(emission).setMaterial(material), // Right face
                new Polygon(C, D, H, G).setEmission(emission).setMaterial(material), // Back face
                new Polygon(D, A, E, H).setEmission(emission).setMaterial(material)  // Left face
        );

        return box;
    }
}