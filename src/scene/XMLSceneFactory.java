package scene;

import geometries.Geometries;
import geometries.factory.MapGeometryFactory;
import geometries.factory.GeometryFactory;
import lighting.AmbientLight;
import lighting.LightSource;
import lighting.factory.LightFactory;
import lighting.factory.MapLightFactory;
import util.parsers.PrimitiveParsers;
import util.parsers.SceneParser;
import util.parsers.XMLSceneParser;
import primitives.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static util.parsers.PrimitiveParsers.parseColor;

/**
 * A factory class for creating {@link Scene} instances from XML files.
 */
public class XMLSceneFactory implements SceneFactory<String> {

    @Override
    public Scene createScene(String xmlFilePath) throws Exception {
        SceneParser sceneParser = new XMLSceneParser();
        Map<String, Object> config = sceneParser.parse(xmlFilePath);

        // Use provided scene name or default
        String sceneName = (String) config.getOrDefault("name", "From XML");
        Scene scene = new Scene(sceneName);

        // Background color
        String bgColor = (String) config.get("background-color");
        if (bgColor != null) {
            scene.setBackground(parseColor(bgColor));
        }

        // Ambient light
        String ambientColor = (String) config.get("ambient-light");
        if (ambientColor != null) {
            scene.setAmbientLight(new AmbientLight(parseColor(ambientColor)));
        }

        // Geometries
        GeometryFactory<Map<String, String>> geometryFactory = new MapGeometryFactory();
        Geometries geometries = new Geometries();

        List<Map<String, String>> geometryList = (List<Map<String, String>>) config.get("geometries");
        if (geometryList != null) {
            for (Map<String, String> geometryData : geometryList) {
                geometries.add(geometryFactory.createGeometry(geometryData));
            }
        }
        scene.setGeometries(geometries);

        // Lights
        LightFactory<Map<String, String>> lightFactory = new MapLightFactory();
        List<Map<String, String>> lightList = (List<Map<String, String>>) config.get("lights");
        if (lightList != null) {
            List<LightSource> lights = new ArrayList<>();
            for (Map<String, String> lightData : lightList) {
                lights.add(lightFactory.createLight(lightData));
            }
            scene.setLights(lights);
        }

        return scene;
    }
}
