package geometries;

import primitives.Point;
import primitives.Vector;

/**
 * Abstract class representing a geometric object.
 */
public abstract class Geometry {

    /**
     * Gets the normal vector at a specific point on the geometry.
     * @param p The point on the geometry.
     * @return The normal vector at the given point.
     */
    public abstract Vector getNormal(Point p);
}
