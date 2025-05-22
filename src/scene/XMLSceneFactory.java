package scene;

import geometries.Geometries;
import geometries.factory.MapGeometryFactory;
import geometries.factory.GeometryFactory;
import lighting.AmbientLight;
import parsers.scene.SceneParser;
import parsers.scene.XMLSceneParser;
import primitives.Color;

import java.util.List;
import java.util.Map;

/**
 * A factory class for creating {@link Scene} instances from XML files.
 */
public class XMLSceneFactory implements SceneFactory<String> {

    @Override
    public Scene createScene(String xmlFilePath) throws Exception {
        SceneParser sceneParser = new XMLSceneParser();
        Map<String, Object> config = sceneParser.parse(xmlFilePath);
        Scene scene = new Scene("From XML");

        String bgColor = (String) config.get("background-color");
        if (bgColor != null) {
            scene.setBackground(parseColor(bgColor));
        }

        String ambientColor = (String) config.get("ambient-light");
        if (ambientColor != null) {
            scene.setAmbientLight(new AmbientLight(parseColor(ambientColor)));
        }

        Geometries geometries = new Geometries();
        List<Map<String, String>> geometryList = (List<Map<String, String>>) config.get("geometries");

        GeometryFactory<Map<String, String>> geometryFactory = new MapGeometryFactory();

        for (Map<String, String> geometryData : geometryList) {
            geometries.add(geometryFactory.createGeometry(geometryData));
        }

        scene.setGeometries(geometries);
        return scene;
    }

    /**
     * Parses a color string in the format "R G B" into a {@link Color} object.
     *
     * @param s A string representing the RGB color.
     * @return A {@link Color} instance with the specified RGB values.
     */
    private static Color parseColor(String s) {
        String[] rgb = s.trim().split("\\s+");
        return new Color(
                Integer.parseInt(rgb[0]),
                Integer.parseInt(rgb[1]),
                Integer.parseInt(rgb[2])
        );
    }
}
