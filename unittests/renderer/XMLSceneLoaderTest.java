package renderer;

import org.junit.jupiter.api.Test;
import renderer.Camera;
import renderer.RayTracerType;
import scene.Scene;
import scene.XMLSceneFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Tests that a scene can be loaded from an XML file and rendered successfully.
 */
public class XMLSceneLoaderTest {

    @Test
    void testRenderCityscapeFromXML() throws Exception {
        Path xmlPath = Path.of("unittests/resources/scenes/xml/cityscape_scene.xml");

        Scene scene = new XMLSceneFactory().createScene(xmlPath.toString());


        Camera.getBuilder()
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setLocation(new primitives.Point(0, 0, 1000))
                .setDirection(new primitives.Point(0, 1, 0), new primitives.Vector(0, 0, -1))
                .setViewPlaneDistance(1000)
                .setResolution(1920, 1080)
                .setViewPlaneSize(350, 200)
                .setMultithreading(3)
                .setDebugPrint(1)
                .setNumRays(9)
                .build()
                .renderImage()
                .writeToImage("XMLCityscapeRender");
    }
}
