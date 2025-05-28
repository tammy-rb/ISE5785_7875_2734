package lighting;

import primitives.Color;

/**
 * Represents ambient light in a scene.
 */
public class AmbientLight extends Light {

    /**
     * A constant representing the absence of ambient light (black color).
     */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);


    /**
     * Constructs an ambient light with the specified intensity.
     *
     * @param intensity The {@link Color} representing the intensity of the ambient light.
     */
    public AmbientLight(Color intensity) {
        super(intensity);
    }
}
