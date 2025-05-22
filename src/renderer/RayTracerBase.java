package renderer;

import primitives.Color;
import primitives.Ray;
import scene.Scene;

/**
 * Abstract base class for ray tracing algorithms.
 * <p>
 * This class serves as a blueprint for various implementations of ray tracing engines,
 * which compute the color resulting from a ray intersecting a scene.
 * </p>
 */
public abstract class RayTracerBase {

    /**
     * The 3D scene to be rendered using ray tracing.
     */
    protected final Scene scene;

    /**
     * Constructs a new ray tracer for the given scene.
     *
     * @param scene the {@link Scene} to trace rays through
     */
    public RayTracerBase(Scene scene) {
        this.scene = scene;
    }

    /**
     * Traces a given ray through the scene and computes the resulting color.
     *
     * @param ray the {@link Ray} to trace
     * @return the computed {@link Color} based on scene geometry and lighting
     */
    public abstract Color traceRay(Ray ray);
}
