package scene;

import geometries.Geometries;
import lighting.AmbientLight;
import lighting.LightSource;
import primitives.Color;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a 3D scene containing geometry, background color, and lighting.
 */
public class Scene {

    public String name;

    public Color background = Color.BLACK;

    public AmbientLight ambientLight = AmbientLight.NONE;

    public Geometries geometries;

    public List<LightSource> lights = new LinkedList<LightSource>();

    public Scene(String sceneName, boolean enableCBR) {
        name = sceneName;
        this.geometries = new Geometries(enableCBR);
    }

    public Scene(String sceneName) {
        this(sceneName, false);
    }

    /**
     * Sets the background color of the scene.
     *
     * @param background A {@link Color} object representing the background color.
     * @return The current {@code Scene} instance (for method chaining).
     */
    public Scene setBackground(Color background) {
        this.background = background;
        return this;
    }

    /**
     * Sets the ambient light of the scene.
     *
     * @param ambientLight An {@link AmbientLight} object representing the ambient light.
     * @return The current {@code Scene} instance (for method chaining).
     */
    public Scene setAmbientLight(AmbientLight ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }

    /**
     * Sets the geometries of the scene.
     *
     * @param geometries A {@link Geometries} object representing the geometrical shapes in the scene.
     * @return The current {@code Scene} instance (for method chaining).
     */
    public Scene setGeometries(Geometries geometries) {
        this.geometries = geometries;
        return this;
    }


    public Scene setLights(List<LightSource> lights) {
        this.lights = lights;
        return this;
    }
}
