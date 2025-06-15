package renderer;

import geometries.*;
import lighting.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

public class miniProjectTest {

    private final Scene scene = new Scene("miniProject");
    private final Camera.Builder cameraBuilder = Camera.getBuilder()
            .setRayTracer(scene, RayTracerType.SIMPLE);

    /**
     * Creates a 3D box (cuboid) from two opposite corner points.
     *
     * @param p1 The first corner point.
     * @param p2 The second corner point, opposite to p1.
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

    @Test
    void TestAllEffects() {
        scene.setAmbientLight(new AmbientLight(new Color(20, 20, 20)));

        cameraBuilder
                .setLocation(new Point(0, 0, 1000))
                .setDirection(new Vector(0,0,-1), new Vector(0, 1, 0))
                .setViewPlaneDistance(1000)
                .setResolution(1920, 1080)
                .setViewPlaneSize(350, 200)
                .build()
                .renderImage()
                .writeToImage("miniProjectTest");
    }

    @Test
    void TestManhattanScene() {
        // Adjusted ambient light for a darker, more realistic night scene
        scene.setAmbientLight(new AmbientLight(new Color(10, 10, 15)));

        // Gray ground polygon
        scene.geometries.add(
                new Polygon(
                        new Point(-300, -1, 100),
                        new Point(300, -1, 100),
                        new Point(300, -1, -300),
                        new Point(-300, -1, -300)
                )
                        .setEmission(new Color(101, 67, 33))  // Rich brown earth color
                        .setMaterial(new Material().setKD(0.7).setKS(0.1).setShininess(5))  // More matte, less reflective
        );

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

        // Building positions and heights
        int[][] positions = {
                {-120, -50, 90},
                {-70, -45, 150},
                {-20, -60, 120},
                {30, -55, 75},
                {-100, -90, 200},
                {-50, -85, 110},
                {0, -100, 180},
                {50, -95, 130},
                {100, -90, 100}
        };

        // Creating 9 buildings with windows
        for (int i = 0; i < 9; i++) {
            int x = positions[i][0];
            int z = positions[i][1];
            int height = positions[i][2];

            Point p1 = new Point(x, 0, z);
            Point p2 = new Point(x + 30, height, z - 30);

            scene.geometries.add(
                    createBox(p1, p2, buildingColors[i % buildingColors.length],
                            new Material().setKD(0.6).setKS(0.3).setShininess(40))
            );

            // Windows with a subtle blueish glow
            addBuildingWindows(p1, p2, new Color(20, 20, 60),
                    new Material().setKD(0.2).setKS(0.8).setShininess(150).setKT(0.3));
        }

        // Reflective sea surface
        scene.geometries.add(
                new Polygon(
                        new Point(-1000, -5, 1000),
                        new Point(1000, -5, 1000),
                        new Point(1000, -5, -1000),
                        new Point(-1000, -5, -1000)
                )
                        .setEmission(new Color(20, 50, 80)) // Deep blue sea color
                        .setMaterial(
                                new Material()
                                        .setKD(0.2)      // Diffuse (matte)
                                        .setKS(0.8)      // Glossy reflections
                                        .setShininess(300)
                                        .setKR(0.6)      // Reflection coefficient
                        )
        );

        // Add stars in the sky - More realistic positioning and size
        Color starColor = new Color(220, 220, 200); // Very subtle white-yellow for stars
        int numStars = 700; // Increased number of stars for denser sky

        for (int i = 0; i < numStars; i++) {
            // Randomize star positions over a much larger and deeper range
            double starX = (Math.random() * 2000) - 1000; // From -1000 to 1000
            double starY = 50 + (Math.random() * 450);   // From 50 to 500 (higher in the sky)
            double starZ = -2000 + (Math.random() * 3000); // From -2000 to 1000 (deeper)
            double starSize = 0.5 + Math.random() * 1.5; // Much smaller, slightly varied star sizes

            scene.geometries.add(
                    createStar(starX, starY, starZ, starSize, starColor)
            );
        }

        // Add moon sphere in the sky - Larger and much farther for realism
        Point moonCenter = new Point(1300, 2100, -5000); // Farther back, higher, slightly off-center
        scene.geometries.add(
                new Sphere(moonCenter, 400) // Significantly larger moon
                        .setEmission(new Color(180, 180, 170)) // Subtle gray-white moon color
                        .setMaterial(new Material().setKD(0.7).setKS(0.1).setShininess(10)) // More diffuse, less shiny material
        );

        // Add airplane flying above the city with a nice color
        scene.geometries.add(createAirplane(
                new Point(-300, 740, -900),  // Position above the city
                12.0,  // Scale for visibility
                new Color(180, 180, 200),  // Metallic light gray-blue color
                new Material()
                        .setKD(0.3)        // Diffuse reflection
                        .setKS(0.7)        // Specular reflection for shine
                        .setShininess(300) // High shininess for metal look
                        .setKR(0.1)        // Slight reflectivity
        ));

        // Add moonlight as point light - Subtle intensity for distant moon
        scene.lights.add(
                new PointLight(new Color(150, 150, 140), moonCenter) // Dimmer light matching moon color
                        .setKL(0.0001) // Very subtle light decay
                        .setKQ(0.00005)
        );

        // Subtle directional light acting as a very dim sky illumination
        scene.lights.add(
                new DirectionalLight(new Color(50, 50, 60), new Vector(-0.5, -0.8, -0.5)) // Very dim, slightly blueish
        );

        // Camera position - Looking at the scene from a slightly different perspective
        cameraBuilder
                .setLocation(new Point(0, 0, 400))
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0)) // Looking straight ahead, Y-axis up
                .setViewPlaneDistance(120)
                .setResolution(1024, 1024)
                .setViewPlaneSize(160, 160)
                .build()
                .renderImage()
                .writeToImage("manhattanCityScene1");
    }

    // Helper method to create a simple airplane
    private Geometries createAirplane(Point center, double scale, Color emission, Material material) {
        Geometries airplane = new Geometries();

        double x = center.get_xyz().d1();
        double y = center.get_xyz().d2();
        double z = center.get_xyz().d3();

        // Fuselage (main body)
        airplane.add(createBox(
                new Point(x - 3*scale, y - 0.5*scale, z - 0.5*scale),
                new Point(x + 3*scale, y + 0.5*scale, z + 0.5*scale),
                emission, material
        ));

        // Wings
        airplane.add(createBox(
                new Point(x - 0.5*scale, y - 4*scale, z - 0.2*scale),
                new Point(x + 0.5*scale, y + 4*scale, z + 0.2*scale),
                emission, material
        ));

        // Tail
        airplane.add(createBox(
                new Point(x - 3*scale, y - 0.2*scale, z - 2*scale),
                new Point(x - 2*scale, y + 0.2*scale, z + 2*scale),
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
     * @param size Radius of the star.
     * @param color Emission color of the star.
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
     * @param p1 Building's first corner point (min x, min y, min z).
     * @param p2 Building's second corner point (max x, max y, max z).
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
        double frontZ = (p1.get_xyz().d3() + p2.get_xyz().d3()) / 2 > 0 ? Math.max(z1,z2) + 0.1 : Math.min(z1,z2) - 0.1;

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
}
