package util.parsers;

import java.io.File;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class XMLSceneParser implements SceneParser {

    @Override
    public Map<String, Object> parse(String xmlFilePath) throws Exception {
        Map<String, Object> sceneData = new HashMap<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(xmlFilePath));
        doc.getDocumentElement().normalize();

        Element sceneElement = doc.getDocumentElement();

        // Scene name (optional)
        if (sceneElement.hasAttribute("name")) {
            sceneData.put("name", sceneElement.getAttribute("name"));
        }

        // Background color
        String backgroundColor = sceneElement.getAttribute("background-color");
        if (!backgroundColor.isEmpty()) {
            sceneData.put("background-color", backgroundColor);
        }

        // Ambient Light
        NodeList ambientList = doc.getElementsByTagName("ambient-light");
        if (ambientList.getLength() > 0) {
            Element ambientElement = (Element) ambientList.item(0);
            sceneData.put("ambient-light", ambientElement.getAttribute("color"));
        }

        // Geometries
        List<Map<String, String>> geometries = new ArrayList<>();
        NodeList geometriesList = doc.getElementsByTagName("geometries");
        if (geometriesList.getLength() > 0) {
            NodeList geometryNodes = geometriesList.item(0).getChildNodes();
            for (int i = 0; i < geometryNodes.getLength(); i++) {
                Node node = geometryNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element geomElement = (Element) node;
                    Map<String, String> geometryData = new HashMap<>();
                    geometryData.put("type", geomElement.getNodeName());

                    NamedNodeMap attributes = geomElement.getAttributes();
                    for (int j = 0; j < attributes.getLength(); j++) {
                        Node attr = attributes.item(j);
                        geometryData.put(attr.getNodeName(), attr.getNodeValue());
                    }
                    geometries.add(geometryData);
                }
            }
        }
        sceneData.put("geometries", geometries);

        // Lights
        List<Map<String, String>> lights = new ArrayList<>();
        NodeList lightsList = doc.getElementsByTagName("lights");
        if (lightsList.getLength() > 0) {
            NodeList lightNodes = lightsList.item(0).getChildNodes();
            for (int i = 0; i < lightNodes.getLength(); i++) {
                Node node = lightNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element lightElement = (Element) node;
                    Map<String, String> lightData = new HashMap<>();
                    lightData.put("type", lightElement.getNodeName());

                    NamedNodeMap attributes = lightElement.getAttributes();
                    for (int j = 0; j < attributes.getLength(); j++) {
                        Node attr = attributes.item(j);
                        lightData.put(attr.getNodeName(), attr.getNodeValue());
                    }
                    lights.add(lightData);
                }
            }
        }
        sceneData.put("lights", lights);

        return sceneData;
    }
}
