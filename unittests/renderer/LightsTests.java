package renderer;

import static java.awt.Color.BLUE;

import org.junit.jupiter.api.Test;

import geometries.*;
import lighting.*;
import primitives.*;
import scene.Scene;

/**
 * Test rendering a basic image
 * @author Dan Zilberstein
 */
class LightsTests {
   /** Default constructor to satisfy JavaDoc generator */
   LightsTests() { /* to satisfy JavaDoc generator */ }

   /** First scene for some of tests */
   private final Scene          scene1                  = new Scene("Test scene");
   /** Second scene for some of tests */
   private final Scene          scene2                  = new Scene("Test scene")
      .setAmbientLight(new AmbientLight(new Color(38, 38, 38)));

   /** First camera builder for some of tests */
   private final Camera.Builder camera1                 = Camera.getBuilder()                                          //
      .setRayTracer(scene1, RayTracerType.SIMPLE)                                                                      //
      .setLocation(new Point(0, 0, 1000))                                                                              //
      .setDirection(Point.ZERO, Vector.AXIS_Y)                                                                         //
      .setViewPlaneSize(150, 150).setViewPlaneDistance(1000);

   /** Second camera builder for some of tests */
   private final Camera.Builder camera2                 = Camera.getBuilder()                                          //
      .setRayTracer(scene2, RayTracerType.SIMPLE)                                                                      //
      .setLocation(new Point(0, 0, 1000))                                                                              //
      .setDirection(Point.ZERO, Vector.AXIS_Y)                                                                         //
      .setViewPlaneSize(200, 200).setViewPlaneDistance(1000);

   /** Shininess value for most of the geometries in the tests */
   private static final int     SHININESS               = 301;
   /** Diffusion attenuation factor for some of the geometries in the tests */
   private static final double  KD                      = 0.5;
   /** Diffusion attenuation factor for some of the geometries in the tests */
   private static final Double3 KD3                     = new Double3(0.2, 0.6, 0.4);

   /** Specular attenuation factor for some of the geometries in the tests */
   private static final double  KS                      = 0.5;
   /** Specular attenuation factor for some of the geometries in the tests */
   private static final Double3 KS3                     = new Double3(0.2, 0.4, 0.3);

   /** Material for some of the geometries in the tests */
   private final Material       material                = new Material().setKD(KD3).setKS(KS3).setShininess(SHININESS);
   /** Light color for tests with triangles */
   private final Color          trianglesLightColor     = new Color(800, 500, 250);
   /** Light color for tests with sphere */
   private final Color          sphereLightColor        = new Color(800, 500, 0);
   /** Color of the sphere */
   private final Color          sphereColor             = new Color(BLUE).reduce(2);

   /** Center of the sphere */
   private final Point          sphereCenter            = new Point(0, 0, -50);
   /** Radius of the sphere */
   private static final double  SPHERE_RADIUS           = 50d;

   /** The triangles' vertices for the tests with triangles */
   private final Point[]        vertices                =
      {
        // the shared left-bottom:
        new Point(-110, -110, -150),
        // the shared right-top:
        new Point(95, 100, -150),
        // the right-bottom
        new Point(110, -110, -150),
        // the left-top
        new Point(-75, 78, 100)
      };
   /** Position of the light in tests with sphere */
   private final Point          sphereLightPosition     = new Point(-50, -50, 25);
   /** Light direction (directional and spot) in tests with sphere */
   private final Vector         sphereLightDirection    = new Vector(1, 1, -0.5);
   /** Position of the light in tests with triangles */
   private final Point          trianglesLightPosition  = new Point(30, 10, -100);
   /** Light direction (directional and spot) in tests with triangles */
   private final Vector         trianglesLightDirection = new Vector(-2, -2, -2);

   /** The sphere in appropriate tests */
   private final Geometry       sphere                  = new Sphere(sphereCenter, SPHERE_RADIUS)
      .setEmission(sphereColor).setMaterial(new Material().setKD(KD).setKS(KS).setShininess(SHININESS));
   /** The first triangle in appropriate tests */
   private final Geometry       triangle1               = new Triangle(vertices[0], vertices[1], vertices[2])
      .setMaterial(material);
   /** The first triangle in appropriate tests */
   private final Geometry       triangle2               = new Triangle(vertices[0], vertices[1], vertices[3])
      .setMaterial(material);

   /** Produce a picture of a sphere lighted by a directional light */
   @Test
   void sphereDirectional() {
      scene1.geometries.add(sphere);
      scene1.lights.add(new DirectionalLight(sphereLightColor, sphereLightDirection));

      camera1 //
         .setResolution(500, 500) //
         .build() //
         .renderImage() //
         .writeToImage("lightsTests/lightSphereDirectional");
   }

   @Test
   void sphereMultipleLights(){
      scene1.geometries.add(sphere);
      scene1.lights.add(new DirectionalLight(
              new Color(800, 0, 0),                      // Strong Red
              new Vector(-1, -1, -1)                    // From top-left-front
      ));

      // --- Point Light (GREEN) ---
      // Characteristics: Intensity falls off with distance. Emits light in all directions from a single point.
      // Effect: Creates a bright spot near the light source, fading out. Good for light bulbs.
      scene1.lights.add(new PointLight(
              new Color(0, 800, 0),                      // Strong Green
              new Point(100, 0, 100)                      // To the right and slightly in front
      ).setKL(0.001).setKQ(0.0001)); // Adjust KL and KQ for more noticeable falloff or spread

      // --- Spot Light (BLUE) ---
      // Characteristics: Emits light in a cone shape, with intensity falling off from the center of the cone.
      // Effect: A focused beam of light. Good for flashlights, stage lights.
      scene1.lights.add(new SpotLight(new Color(0,255,0), new Point(-60,-50,30), new Vector(1,1,-0.8)) //
              .setKL(0.001).setKQ(0.0001)); // Narrow beam to emphasize the spotlight effect


      camera1 //
              .setResolution(500, 500) //
              .build() //
              .renderImage() //
              .writeToImage("lightsTests/sphereMultipleLights");
   }

   /** Produce a picture of a sphere lighted by a point light */
   @Test
   void spherePoint() {
      scene1.geometries.add(sphere);
      scene1.lights.add(new PointLight(sphereLightColor, sphereLightPosition) //
         .setKL(0.001).setKQ(0.0002));

      camera1 //
         .setResolution(500, 500) //
         .build() //
         .renderImage() //
         .writeToImage("lightsTests/lightSpherePoint");
   }

   /** Produce a picture of a sphere lighted by a spotlight */
   @Test
   void sphereSpot() {
      scene1.geometries.add(sphere);
      scene1.lights.add(new SpotLight(sphereLightColor, sphereLightPosition, sphereLightDirection) //
         .setKL(0.001).setKQ(0.0001));

      camera1 //
         .setResolution(500, 500) //
         .build() //
         .renderImage() //
         .writeToImage("lightsTests/lightSphereSpot");
   }

   /** Produce a picture of two triangles lighted by a directional light */
   @Test
   void trianglesDirectional() {
      scene2.geometries.add(triangle1, triangle2);
      scene2.lights.add(new DirectionalLight(trianglesLightColor, trianglesLightDirection));

      camera2.setResolution(500, 500) //
         .build() //
         .renderImage() //
         .writeToImage("lightsTests/lightTrianglesDirectional");
   }

   /** Produce a picture of two triangles lighted by a point light */
   @Test
   void trianglesPoint() {
      scene2.geometries.add(triangle1, triangle2);
      scene2.lights.add(new PointLight(trianglesLightColor, trianglesLightPosition) //
         .setKL(0.001).setKQ(0.0002));

      camera2.setResolution(500, 500) //
         .build() //
         .renderImage() //
         .writeToImage("lightsTests/lightTrianglesPoint");
   }

   /** Produce a picture of two triangles lighted by a spotlight */
   @Test
   void trianglesSpot() {
      scene2.geometries.add(triangle1, triangle2);
      scene2.lights.add(new SpotLight(trianglesLightColor, trianglesLightPosition, trianglesLightDirection) //
         .setKL(0.001).setKQ(0.0001));

      camera2.setResolution(500, 500) //
         .build() //
         .renderImage() //
         .writeToImage("lightsTests/lightTrianglesSpot");
   }

   @Test
   void multipleLightTriangle(){
      scene2.geometries.add(triangle1, triangle2);
      scene2.lights.add(new DirectionalLight(new Color(200,100,100), new Vector(-10,-2,-2)));
      scene2.lights.add(new PointLight(new Color(600,0,0), new Point(50, 50, -100)) //
              .setKL(0.001).setKQ(0.0002));
      scene2.lights.add(new SpotLight(new Color(0,255,0), new Point(-20, -10, -100), trianglesLightDirection) //
              .setKL(0.002).setKQ(0.0002));
      camera2.setResolution(500, 500) //
              .build() //
              .renderImage() //
              .writeToImage("lightsTests/MultipleLightsTriangles");
   }

   /** Produce a picture of a sphere lighted by a narrow spotlight */
   @Test
   void sphereSpotSharp() {
      scene1.geometries.add(sphere);
      scene1.lights
         .add(new SpotLight(sphereLightColor, sphereLightPosition, new Vector(1, 1, -0.5)) //
            .setKL(0.001).setKQ(0.00004).setNarrowBeam(10));

      camera1.setResolution(500, 500) //
         .build() //
         .renderImage() //
         .writeToImage("lightsTests/lightSphereSpotSharp");
   }

   /** Produce a picture of two triangles lighted by a narrow spotlight */
   @Test
   void trianglesSpotSharp() {
      scene2.geometries.add(triangle1, triangle2);
      scene2.lights.add(new SpotLight(trianglesLightColor, trianglesLightPosition, trianglesLightDirection) //
         .setKL(0.001).setKQ(0.00004).setNarrowBeam(10));

      camera2.setResolution(500, 500) //
         .build() //
         .renderImage() //
         .writeToImage("lightsTests/lightTrianglesSpotSharp");
   }

}
