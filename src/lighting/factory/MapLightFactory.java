package lighting.factory;

import lighting.DirectionalLight;
import lighting.LightSource;
import lighting.PointLight;
import lighting.SpotLight;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

import java.util.Map;

import static util.parsers.PrimitiveParsers.*;

public class MapLightFactory implements LightFactory<Map<String, String>> {

    @Override
    public LightSource createLight(Map<String, String> config) {
        String type = config.get("type");
        if (type == null) {
            throw new IllegalArgumentException("Missing 'type' in light configuration");
        }

        String intensityStr = config.get("intensity");
        if (intensityStr == null) {
            throw new IllegalArgumentException("Missing 'intensity' in light configuration of type: " + type);
        }

        Color intensity = parseColor(intensityStr);
        double radius = parseDouble(config.get("radius"), 0.0);
        double kC = parseDouble(config.get("kC"), 1.0);
        double kL = parseDouble(config.get("kL"), 0.0);
        double kQ = parseDouble(config.get("kQ"), 0.0);

        switch (type.toLowerCase()) {
            case "point": {
                Point position = parsePoint(config.get("position"));
                return new PointLight(intensity, position, radius)
                        .setKC(kC).setKL(kL).setKQ(kQ);
            }

            case "spot": {
                Point position = parsePoint(config.get("position"));
                Vector direction = parseVector(config.get("direction"));
                double narrowBeam = parseDouble(config.get("narrowBeam"), 1.0);
                return new SpotLight(intensity, position, direction, radius)
                        .setKC(kC).setKL(kL).setKQ(kQ)
                        .setNarrowBeam(narrowBeam);
            }

            case "directional": {
                Vector direction = parseVector(config.get("direction"));
                return new DirectionalLight(intensity, direction);
            }

            default:
                throw new IllegalArgumentException("Unsupported light type: " + type);
        }
    }
}
