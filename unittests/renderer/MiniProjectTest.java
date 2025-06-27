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

//    @Test
//    void TestManhattanSceneNoSea() {
//        // NO ambient light for pure darkness - only moon illumination
//        scene.setAmbientLight(new AmbientLight(new Color(0, 0, 0)));
//
//        // Diverse building colors (now used as material base colors, not emissions)
//        Color[] buildingColors = {
//                new Color(60, 40, 10),    // Dark brown
//                new Color(40, 20, 20),    // Dark red
//                new Color(10, 30, 60),    // Dark blue
//                new Color(50, 25, 5),     // Dark orange
//                new Color(20, 40, 50),    // Dark cyan
//                new Color(15, 35, 15),    // Dark green
//                new Color(45, 20, 45),    // Dark purple
//                new Color(55, 55, 20),    // Dark yellow
//                new Color(20, 20, 60)     // Dark blue
//        };
//
//        // Island setup - create a thin horizon line effect with smaller radius
//        double seaLevel = 0;
//        double islandHeight = 0.3;  // Very thin - just barely visible as horizon line
//        double islandRadius = 750;  // Increased to 750
//        Point islandCenter = new Point(0, seaLevel, -950);
//
//        // Create island cylinder - very thin horizon line with non-emissive material
//        scene.geometries.add(
//                new Cylinder(islandRadius, new Ray(islandCenter, new Vector(0, 1, 0)), islandHeight)
//                        .setEmission(new Color(30, 25, 15))  // No emission
//                        .setMaterial(new Material()
//                                .setKD(0.9)     // High diffuse to catch moonlight
//                                .setKS(0.1)     // Low specular
//                                .setShininess(20)
//                                .setKR(0.02)
//                        ) // Dark earth color
//        );
//
//        // Building positions - better spaced arrangement
//        int[][] positions = {
//                {-500, -120, 240}, {-300, -20, 320}, {-150, -150, 280}, {0, 0, 200},
//                {150, -80, 380}, {300, -40, 260}, {450, -130, 340}, {-400, -180, 300},
//                {100, -100, 250}, {-100, 40, 220}, {350, 0, 290}, {-550, -90, 310},
//                {-650, -50, 225}, {550, -70, 255}, {-200, -200, 245}, {50, 60, 275},
//                {-450, 20, 350}, {400, -160, 305}, {-50, -120, 285}, {-250, -10, 195}
//        };
//
//        // Create buildings - non-emissive, will only be lit by moon
//        for (int i = 0; i < 20; i++) {
//            int relativeX = positions[i][0];
//            int relativeZ = positions[i][1];
//            int height = positions[i][2];
//
//            double worldX = islandCenter.get_xyz().d1() + relativeX;
//            double worldZ = islandCenter.get_xyz().d3() + relativeZ;
//            double islandSurfaceY = seaLevel + islandHeight;
//
//            Point p1 = new Point(worldX - 30, islandSurfaceY, worldZ - 30);
//            Point p2 = new Point(worldX + 30, islandSurfaceY + height, worldZ + 30);
//
//            scene.geometries.add(
//                    createBox(p1, p2, buildingColors[i % buildingColors.length], // No emission - pure black
//                            new Material()
//                                    .setKD(0.8)  // High diffuse to catch moonlight
//                                    .setKS(0.2)  // Some specular for highlights
//                                    .setShininess(40)
//                    ));
//
//            // Very dim windows - barely visible unless directly lit by moon
//            addBuildingWindows(p1, p2, new Color(8, 8, 15), // No emission
//                    new Material()
//                            .setKD(0.4)
//                            .setKS(0.6)
//                            .setShininess(150)
//                            .setKT(0.7));
//        }
//
//        // Add very dim stars - barely visible pinpoints
//        Color starColor = new Color(15, 15, 12);  // Very dim star color
//        int numStars = 800;  // Fewer stars
//
//        for (int i = 0; i < numStars; i++) {
//            // Expanded star field to cover entire visible area
//            double starX = (Math.random() * 8000) - 4000;
//            double starY = 600 + (Math.random() * 2000);
//            double starZ = -12000 + (Math.random() * 10000);
//            double starSize = 0.8 + Math.random() * 1.5;    // Smaller stars
//
//            scene.geometries.add(createStar(starX, starY, starZ, starSize, starColor));
//        }
//
//        // Add moon sphere - the primary light source (still emissive for visibility)
//        Point moonCenter = new Point(2000, 2400, -8000);
//        scene.geometries.add(
//                new Sphere(moonCenter, 500)
//                        .setEmission(new Color(400, 400, 350))  // Keep moon bright for visibility
//                        .setMaterial(new Material()
//                                .setKD(0.8)
//                                .setKS(0.2)
//                                .setShininess(20))
//        );
//
//        // Add airplane - non-emissive
//        scene.geometries.add(createAirplane(
//                new Point(-300, 740, -900),
//                12.0,
//                new Color(45, 45, 50), // No emission
//                new Material()
//                        .setKD(0.6)
//                        .setKS(0.4)
//                        .setShininess(300)
//                        .setKR(0.1)
//        ));
//
//        // === PURE MOON SPOTLIGHT LIGHTING ===
//        setupMoonSpotlight(moonCenter);
//
//        // Camera setup
//        cameraBuilder
//                .setLocation(new Point(0, 60, 500))
//                .setDirection(
//                        new Vector(0, -60, -1400).normalize(),
//                        new Vector(0, 1400, -60).normalize()
//                )
//                .setViewPlaneDistance(120)
//                .setResolution(1024, 1024)
//                .setMultithreading(-2)
//                .setDebugPrint(1)
//                .setViewPlaneSize(200, 200)
//                .build()
//                .renderImage()
//                .writeToImage("manhattanMoonSpotlight");
//    }
@Test
void TestManhattanSceneNoSea() {
    // NO ambient light for pure darkness - only moon illumination
    scene.setAmbientLight(new AmbientLight(new Color(0, 0, 0)));

    // Diverse building colors (now used as material base colors, not emissions)
    Color[] buildingColors = {
            new Color(60, 40, 10),    // Dark brown
            new Color(40, 20, 20),    // Dark red
            new Color(10, 30, 60),    // Dark blue
            new Color(50, 25, 5),     // Dark orange
            new Color(20, 40, 50),    // Dark cyan
            new Color(15, 35, 15),    // Dark green
            new Color(45, 20, 45),    // Dark purple
            new Color(55, 55, 20),    // Dark yellow
            new Color(20, 20, 60)     // Dark blue
    };

    // Island setup - create a thin horizon line effect with smaller radius
    double seaLevel = 0;
    double islandHeight = 0.3;  // Very thin - just barely visible as horizon line
    double islandRadius = 750;  // Increased to 750
    Point islandCenter = new Point(0, seaLevel, -950);

    // Create island cylinder - very thin horizon line with non-emissive material
    scene.geometries.add(
            new Cylinder(islandRadius, new Ray(islandCenter, new Vector(0, 1, 0)), islandHeight)
                    .setEmission(new Color(0, 0, 0))  // No emission - pure black
                    .setMaterial(new Material()
                            .setKD(0.9)     // High diffuse to catch moonlight
                            .setKS(0.1)     // Low specular
                            .setShininess(20)
                            .setKR(0.02)
                    )
    );

    // Building positions - better spaced arrangement
    int[][] positions = {
            {-500, -120, 240}, {-300, -20, 320}, {-150, -150, 280}, {0, 0, 200},
            {150, -80, 380}, {300, -40, 260}, {450, -130, 340}, {-400, -180, 300},
            {100, -100, 250}, {-100, 40, 220}, {350, 0, 290}, {-550, -90, 310},
            {-650, -50, 225}, {550, -70, 255}, {-200, -200, 245}, {50, 60, 275},
            {-450, 20, 350}, {400, -160, 305}, {-50, -120, 285}, {-250, -10, 195}
    };

    // Create buildings - non-emissive, will only be lit by moon
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
                createBox(p1, p2, new Color(0, 0, 0), // No emission - pure black
                        new Material()
                                .setKD(0.8)  // High diffuse to catch moonlight
                                .setKS(0.2)  // Some specular for highlights
                                .setShininess(40)
                ));

        // Very dim windows - barely visible unless directly lit by moon
        addBuildingWindows(p1, p2, new Color(0, 0, 0), // No emission
                new Material()
                        .setKD(0.4)
                        .setKS(0.6)
                        .setShininess(150)
                        .setKT(0.7));
    }

    // Add very dim stars - barely visible pinpoints
    Color starColor = new Color(15, 15, 12);  // Very dim star color
    int numStars = 800;  // Fewer stars

    for (int i = 0; i < numStars; i++) {
        // Expanded star field to cover entire visible area
        double starX = (Math.random() * 8000) - 4000;
        double starY = 600 + (Math.random() * 2000);
        double starZ = -12000 + (Math.random() * 10000);
        double starSize = 0.8 + Math.random() * 1.5;    // Smaller stars

        scene.geometries.add(createStar(starX, starY, starZ, starSize, starColor));
    }

    // REPOSITIONED MOON - directly above the city center for proper illumination
    Point moonCenter = new Point(0, 1500, -400);  // Above city center
    scene.geometries.add(
            new Sphere(moonCenter, 200)  // Smaller moon
                    .setEmission(new Color(300, 300, 250))  // Bright moon for visibility
                    .setMaterial(new Material()
                            .setKD(0.8)
                            .setKS(0.2)
                            .setShininess(20))
    );

    // Add airplane - non-emissive
    scene.geometries.add(createAirplane(
            new Point(-300, 740, -900),
            12.0,
            new Color(0, 0, 0), // No emission
            new Material()
                    .setKD(0.6)
                    .setKS(0.4)
                    .setShininess(300)
                    .setKR(0.1)
    ));

    // === IMPROVED MOON SPOTLIGHT LIGHTING ===
    setupMoonSpotlight(moonCenter);

    // Camera setup
    cameraBuilder
            .setLocation(new Point(0, 60, 500))
            .setDirection(
                    new Vector(0, -60, -1400).normalize(),
                    new Vector(0, 1400, -60).normalize()
            )
            .setViewPlaneDistance(120)
            .setResolution(1024, 1024)
            .setMultithreading(-2)
            .setDebugPrint(1)
            .setViewPlaneSize(200, 200)
            .build()
            .renderImage()
            .writeToImage("manhattanMoonSpotlight");
}

    /**
     * Sets up pure moon spotlight for dramatic nighttime scene with strong shadows
     */
    private void setupMoonSpotlight(Point moonCenter) {
        // SPOTLIGHT pointing directly down from moon toward city center
        Vector moonDirection = new Vector(0, -1, 0).normalize(); // Straight down

        scene.lights.add(
                new SpotLight(new Color(2000, 2000, 1600), moonCenter, moonDirection)
                        .setKL(0.00001)       // Very low linear attenuation
                        .setKQ(0.0000001)     // Very low quadratic attenuation
                        .setNarrowBeam(35)    // Wide enough to cover the city
        );

        // Additional point light from moon for ambient fill (optional)
        scene.lights.add(
                new PointLight(new Color(800, 800, 600), moonCenter)
                        .setKL(0.0001)
                        .setKQ(0.000001)
        );
    }
    /**
     * Sets up pure moon spotlight for dramatic nighttime scene with strong shadows
     */
//    private void setupMoonSpotlight(Point moonCenter) {
//        // SINGLE SPOTLIGHT from moon position toward the city
//        Vector moonDirection = new Vector(0, -1, 0.2).normalize(); // Angled down toward buildings
//
//        scene.lights.add(
//                new SpotLight(new Color(1200, 1200, 1000), moonCenter, moonDirection)
//                        .setKL(0.0001)        // Linear attenuation
//                        .setKQ(0.000001)      // Quadratic attenuation
//                        .setNarrowBeam(25)    // Focused beam angle for spotlight effect
//        );
//
//        // Alternative: If SpotLight doesn't exist in your system, use highly attenuated PointLight
//        /*
//        scene.lights.add(
//                new PointLight(new Color(1500, 1500, 1200), moonCenter)
//                        .setKL(0.001)         // High linear attenuation for dramatic falloff
//                        .setKQ(0.00001)       // High quadratic attenuation
//        );
//        */
//    }

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

        Color seaColor = new Color(0, 0, 0);  // Pure black emission - no self-illumination

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

                // Create triangles with non-emissive material
                scene.geometries.add(new Triangle(p00, p10, p11)
                        .setEmission(seaColor)
                        .setMaterial(seaMaterial)); // Very dark blue
//.setEmission(new Color(1, 4, 8)
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
     * Smoother wave function with better wave continuity and less harsh breaks
     */
    private static double smoothWaveY(double x, double z, double amplitude, double wavelength1, double wavelength2) {
        // Primary wave system - smoother and more continuous
        double wave1 = Math.sin((x + z * 0.5) / wavelength1) * amplitude * 0.6;

        // Secondary cross waves - gentler
        double wave2 = Math.sin((x * 0.7 - z * 0.3) / wavelength2) * amplitude * 0.3;

        // Large ocean swell - very gentle
        double swell = Math.sin((x + z) / (wavelength1 * 4)) * amplitude * 0.3;

        // Small surface ripples - subtle
        double ripples = Math.sin(x / (wavelength2 * 0.3)) * Math.sin(z / (wavelength2 * 0.4)) * amplitude * 0.1;

        // Additional smooth wave for continuity
        double smoothWave = Math.cos((x - z * 0.4) / (wavelength1 * 1.5)) * amplitude * 0.2;

        return wave1 + wave2 + swell + ripples + smoothWave;
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

            // Stars are minimally emissive
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