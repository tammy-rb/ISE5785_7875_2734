package renderer;

import lighting.LightSource;
import primitives.*;
import scene.Scene;
import geometries.Intersectable.Intersection;

import java.util.List;

import static primitives.Util.alignZero;

/**
 * A simple implementation of the {@link RayTracerBase} class.
 * This ray tracer calculates the color of a pixel by tracing a ray into the scene,
 * computing local and global lighting effects including shadows, reflection, and refraction.
 */
public class SimpleRayTracer extends RayTracerBase {
    private static final int MAX_CALC_COLOR_LEVEL = 10;
    private static final double MIN_CALC_COLOR_K = 0.001;
    private static final Double3 INITIAL_K = Double3.ONE;

    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    @Override
    public Color traceRay(Ray ray) {
        Intersection closestIntersection = findClosestIntersection(ray);
        if (closestIntersection == null)
            return scene.background;
        return calcColor(closestIntersection, ray);
    }

    /**
     * Calculates the full color at a given intersection.
     *
     * @param intersection the intersection point
     * @param ray          the original ray
     * @return the calculated color at the point
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        if (!preprocessIntersection(intersection, ray.getDirection()))
            return Color.BLACK;

        return scene.ambientLight.getIntensity().scale(intersection.material.kA)
                .add(calcColor(intersection, MAX_CALC_COLOR_LEVEL, INITIAL_K));
    }

    /**
     * Recursively calculates the color at the intersection using local and global effects.
     *
     * @param intersection the intersection point
     * @param level        recursion depth
     * @param k            attenuation factor
     * @return the calculated color
     */
    private Color calcColor(Intersection intersection, int level, Double3 k) {
        Color color = calcColorLocalEffects(intersection, k);
        return (level == 1) ? color : color.add(calcGlobalEffects(intersection, level, k));
    }

    /**
     * Calculates local lighting effects: emission, diffuse, specular.
     *
     * @param intersection the intersection point
     * @param k            attenuation factor
     * @return the local color at the point
     */
    private Color calcColorLocalEffects(Intersection intersection, Double3 k) {
        Color color = intersection.geometry.getEmission();

        for (LightSource lightSource : scene.lights) {
            if (!setLightSource(intersection, lightSource))
                continue;

            Double3 ktr = transparency(intersection);

            if (!ktr.product(k).lowerThan(MIN_CALC_COLOR_K)) {
                color = color.add(
                        lightSource.getIntensity(intersection.point).scale(ktr).scale(
                                calcDiffusive(intersection).add(calcSpecular(intersection))
                        )
                );
            }
        }

        return color;
    }

    /**
     * Calculates the specular reflection component using the Phong model.
     *
     * @param intersection the intersection point
     * @return specular reflection coefficient
     */
    private Double3 calcSpecular(Intersection intersection) {
        Vector r = intersection.l.subtract(intersection.normal.scale(2 * intersection.lNormal));
        return intersection.material.kS.scale(
                Math.pow(
                        Math.max(0, alignZero(intersection.v.scale(-1).dotProduct(r))),
                        intersection.material.nSH
                )
        );
    }

    /**
     * Calculates the diffuse reflection component using the Lambert model.
     *
     * @param intersection the intersection point
     * @return diffuse reflection coefficient
     */
    private Double3 calcDiffusive(Intersection intersection) {
        return intersection.material.kD.scale(Math.abs(intersection.lNormal));
    }

    /**
     * Calculates global effects: reflection and refraction.
     *
     * @param intersection the intersection point
     * @param level        recursion depth
     * @param k            attenuation factor
     * @return the global color contribution
     */
    private Color calcGlobalEffects(Intersection intersection, int level, Double3 k) {
        return calcGlobalEffect(constructRefractedRay(intersection), intersection.material.kT, level, k)
                .add(calcGlobalEffect(constructReflectedRay(intersection), intersection.material.kR, level, k));
    }

    /**
     * Constructs a refracted ray from the intersection point.
     *
     * @param intersection the intersection point
     * @return the refracted ray
     */
    private Ray constructRefractedRay(Intersection intersection) {
        return new Ray(
                intersection.point,
                intersection.v,
                intersection.normal.scale(intersection.vNormal < 0 ? -1 : 1)
        );
    }

    /**
     * Constructs a reflected ray from the intersection point.
     *
     * @param intersection the intersection point
     * @return the reflected ray
     */
    private Ray constructReflectedRay(Intersection intersection) {
        return new Ray(
                intersection.point,
                intersection.v.subtract(intersection.normal.scale(2 * intersection.vNormal)),
                intersection.normal.scale(intersection.vNormal < 0 ? 1 : -1)
        );
    }

    /**
     * Recursively calculates the color from global effects (reflection/refraction).
     *
     * @param ray   the reflection/refraction ray
     * @param kx    reflection/refraction coefficient
     * @param level recursion depth
     * @param k     total attenuation factor
     * @return the global effect color
     */
    private Color calcGlobalEffect(Ray ray, Double3 kx, int level, Double3 k) {
        Double3 kkx = kx.product(k);
        if (kkx.lowerThan(MIN_CALC_COLOR_K))
            return Color.BLACK;

        Intersection intersection = findClosestIntersection(ray);
        if (intersection == null)
            return scene.background.scale(kx);

        return preprocessIntersection(intersection, ray.getDirection())
                ? calcColor(intersection, level - 1, kkx).scale(kx)
                : Color.BLACK;
    }

    /**
     * Prepares intersection data for shading.
     *
     * @param intersection the intersection
     * @param v            the viewing vector (ray direction)
     * @return true if the intersection is valid (i.e., not parallel to surface), false otherwise
     */
    public boolean preprocessIntersection(Intersection intersection, Vector v) {
        intersection.v = v.normalize();
        intersection.normal = intersection.geometry.getNormal(intersection.point);
        intersection.vNormal = alignZero(v.dotProduct(intersection.normal));
        return intersection.vNormal != 0;
    }

    /**
     * Sets up the light source direction vector and dot product at the intersection.
     *
     * @param intersection the intersection
     * @param lightSource  the light source
     * @return true if the light is on the same side as the viewer, false otherwise
     */
    public boolean setLightSource(Intersection intersection, LightSource lightSource) {
        intersection.l = lightSource.getL(intersection.point).normalize();
        intersection.light = lightSource;
        intersection.lNormal = alignZero(intersection.l.dotProduct(intersection.normal));
        return intersection.lNormal * intersection.vNormal > 0;
    }

    /**
     * Checks if the intersection point is unshaded (i.e., not blocked from light).
     *
     * @param intersection the point to test
     * @param l            direction to the light
     * @return true if unshaded, false if in shadow
     */
    private boolean unshaded(Intersection intersection, Vector l) {
        Vector pointToLight = l.scale(-1);
        Ray shadowRay = new Ray(intersection.point, pointToLight, intersection.normal);

        var intersections = scene.geometries.calculateIntersections(
                shadowRay,
                intersection.light.getDistance(intersection.point)
        );

        if (intersections == null || intersections.isEmpty())
            return true;

        for (Intersection shadowIntersection : intersections) {
            if (shadowIntersection.material.kT.lowerThan(MIN_CALC_COLOR_K))
                return false;
        }

        return true;
    }

    /**
     * Calculates the transparency (ktr) along the path to the light.
     *
     * @param intersection the point to test
     * @return the transparency coefficient
     */
    private Double3 transparency( Intersection intersection) {
        Vector pointToLight = intersection.l.scale(-1);
        Ray shadowRay = new Ray(intersection.point, pointToLight, intersection.normal);

        var intersections = scene.geometries.calculateIntersections(
                shadowRay,
                intersection.light.getDistance(intersection.point)
        );

        if (intersections == null || intersections.isEmpty())
            return Double3.ONE;

        Double3 ktr = Double3.ONE;
        for (Intersection shadowIntersection : intersections) {
            ktr = ktr.product(shadowIntersection.material.kT);
        }

        return ktr;
    }

    /**
     * Finds the closest intersection point of a given ray with the scene geometries.
     *
     * @param ray the ray to trace
     * @return the closest intersection or null if none found
     */
    private Intersection findClosestIntersection(Ray ray) {
        List<Intersection> intersections = scene.geometries.calculateIntersections(ray);
        return (intersections == null) ? null : ray.findClosestIntersection(intersections);
    }
}
