package renderer;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import scene.Scene;
import geometries.Intersectable.Intersection;

import java.util.List;

/**
 * A simple implementation of the {@link RayTracerBase} class.
 * <p>
 * This ray tracer returns the ambient light color if a ray intersects
 * any geometry in the scene, and the background color otherwise.
 */
public class SimpleRayTracer extends RayTracerBase {

    /**
     * Constructs a new {@code SimpleRayTracer} for a given scene.
     *
     * @param scene the {@link Scene} to render
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    @Override
    public Color traceRay(Ray ray) {
        List<Intersection> intersections = scene.geometries.calculateIntersections(ray);
        if (intersections == null)
            return scene.background;
        return calcColor(ray.findClosestIntersection(intersections));
    }

    /**
     * Calculates the color at a given point.
     * In this simple implementation, only the ambient light is considered.
     *
     * @param Intersection the point to calculate the color for
     * @return the ambient light {@link Color} at the point
     */
    private Color calcColor(Intersection intersection) {
        return scene.ambientLight.getIntensity().add(intersection.geometry.getEmission());
    }
}
