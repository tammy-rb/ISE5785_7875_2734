package scene;

import geometries.Geometries;
import lighting.AmbientLight;
import primitives.Color;

/**
 * Represents a 3D scene containing geometry, background color, and lighting.
 */
public class Scene {

    /**
     * The name of the scene.
     */
    public String name;

    /**
     * The background color of the scene.
     * Defaults to black.
     */
    public Color background = Color.BLACK;

    /**
     * The ambient light in the scene.
     * Defaults to {@link AmbientLight#NONE}.
     */
    public AmbientLight ambientLight = AmbientLight.NONE;

    /**
     * The collection of geometrical objects in the scene.
     * Initialized as an empty collection.
     */
    public Geometries geometries = new Geometries();

    /**
     * Constructs a new {@code Scene} with the given name.
     *
     * @param sceneName The name of the scene.
     */
    public Scene(String sceneName) {
        name = sceneName;
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
}
