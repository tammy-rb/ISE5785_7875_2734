package lighting;

import primitives.Color;

/**
 * Represents ambient light in a scene.
 */
public class AmbientLight {

    /**
     * A constant representing the absence of ambient light (black color).
     */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    /**
     * The intensity (color) of the ambient light.
     */
    private final Color intensity;

    /**
     * Constructs an ambient light with the specified intensity.
     *
     * @param intensity The {@link Color} representing the intensity of the ambient light.
     */
    public AmbientLight(Color intensity) {
        this.intensity = intensity;
    }

    /**
     * Returns the intensity of the ambient light.
     *
     * @return A {@link Color} object representing the intensity of the ambient light.
     */
    public Color getIntensity() {
        return intensity;
    }
}
