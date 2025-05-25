package geometries;

import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;

/**
 * Abstract class representing a geometric object.
 */
public abstract class Geometry extends Intersectable {
    protected Color emission = Color.BLACK;
    private Material material = new Material();

    public Color getEmission() {
        return emission;
    }

    public Geometry setEmission(Color emission) {
        this.emission = emission;
        return this;
    }

    public Material getMaterial() {
        return material;
    }

    public Geometry setMaterial(Material material) {
        this.material = material;
        return this;
    }

    /**
     * Gets the normal vector at a specific point on the geometry.
     *
     * @param p The point on the geometry.
     * @return The normal vector at the given point.
     */
    public abstract Vector getNormal(Point p);
}
