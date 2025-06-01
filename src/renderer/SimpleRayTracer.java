package renderer;

import lighting.LightSource;
import primitives.*;
import scene.Scene;
import geometries.Intersectable.Intersection;

import java.util.List;

import static primitives.Util.alignZero;

/**
 * A simple implementation of the {@link RayTracerBase} class.
 * <p>
 * This ray tracer returns the ambient light color if a ray intersects
 * any geometry in the scene, and the background color otherwise.
 */
public class SimpleRayTracer extends RayTracerBase {
    private static final double DELTA = 0.1;

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
        return calcColor(ray.findClosestIntersection(intersections), ray);
    }

    /**
     * Calculates the color at a given point.
     * In this simple implementation, only the ambient light is considered.
     *
     * @param Intersection the point to calculate the color for
     * @return the ambient light {@link Color} at the point
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        if (!preprocessIntersection(intersection, ray.getDirection())) {
            return Color.BLACK;
        }
        return scene.ambientLight.getIntensity().scale(intersection.material.kA)
                .add(calcColorLocalEffects(intersection));
    }

    public boolean preprocessIntersection(Intersection intersection, Vector v) {
        intersection.v = v.normalize();
        intersection.normal = intersection.geometry.getNormal(intersection.point);
        intersection.vNormal = alignZero(v.dotProduct(intersection.normal));
        return intersection.vNormal != 0;
    }

    public boolean setLightSource(Intersection intersection, LightSource lightSource) {
        intersection.l = lightSource.getL(intersection.point).normalize();
        intersection.light = lightSource;
        intersection.lNormal = alignZero(intersection.l.dotProduct(intersection.normal));
        return intersection.lNormal * intersection.vNormal > 0;
    }

    private Color calcColorLocalEffects(Intersection intersection) {
        Color color = intersection.geometry.getEmission();
        for (LightSource lightSource : scene.lights) {
            if (!setLightSource(intersection, lightSource) || !unshaded(intersection, intersection.l)) {
                continue;
            }
            color = color.add(
                    lightSource.getIntensity(intersection.point).scale(
                            calcDiffusive(intersection).add(calcSpecular(intersection))
                    )
            );
        }
        return color;
    }

    private Double3 calcSpecular(Intersection intersection) {
        Vector r = intersection.l.subtract(intersection.normal.scale(2 * intersection.lNormal));
        return intersection.material.kS.scale(Math.pow(Math.max(0, alignZero(intersection.v.scale(-1).dotProduct(r))), intersection.material.nSH));
    }

    private Double3 calcDiffusive(Intersection intersection) {
        return intersection.material.kD.scale(Math.abs(intersection.lNormal));
    }

    private boolean unshaded(Intersection intersection, Vector l) {
        Vector pointToLight = l.scale(-1);
        Vector delta = intersection.normal.scale(intersection.lNormal < 0 ? DELTA : -DELTA);
        Ray shadowRay = new Ray(intersection.point.add(delta), pointToLight);
        var intersections = scene.geometries.calculateIntersections(
                shadowRay,
                intersection.light.getDistance(intersection.point));
        return intersections == null;
    }
}
