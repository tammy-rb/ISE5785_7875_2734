package geometries;

import primitives.Double3;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Represents an infinite cylindrical surface (tube) in 3D space defined by a central axis and a fixed radius.
 * A tube has no finite length (i.e., it extends infinitely in both directions along the axis).
 */
public class Tube extends RadialGeometry {
    /**
     * The central axis of the tube represented as a ray.
     */
    protected final Ray axis;

    /**
     * Constructs a tube with the specified radius and axis.
     *
     * @param radius the radius of the tube
     * @param axis   the axis ray of the tube
     */
    public Tube(double radius, Ray axis) {
        super(radius);
        this.axis = axis;
    }

    @Override
    public Vector getNormal(Point p) {
        Vector u = p.subtract(axis.getHead());
        Vector v = axis.getDirection();
        double t = v.dotProduct(u);
        if (isZero(t))
            return u.normalize();
        Point o = axis.getPoint(t);
        return p.subtract(o).normalize();
    }

    /**
     * Helper method to check whether a given point lies on the tube's axis.
     *
     * @param p the point to check
     * @return true if the point is on the axis, false otherwise
     */
    private boolean isPointOnTubeAxis(Point p) {
        if (p.equals(axis.getHead())) {
            return true;
        }

        try {
            Vector v = p.subtract(axis.getHead());
            return isZero(v.normalize().dotProduct(axis.getDirection().normalize()) - 1);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray) {
        Vector vAxis = axis.getDirection(); // Axis direction
        Point p0 = axis.getHead();          // Axis origin
        Point p = ray.getHead();            // Ray origin
        Vector v = ray.getDirection();      // Ray direction

        // Special case: ray starts at the axis origin and goes in axis direction
        if (p.equals(p0) && isZero(v.normalize().dotProduct(vAxis.normalize()) - 1))
            return null;

        // Compute projection of ray direction perpendicular to the axis
        double vDotVa = alignZero(v.dotProduct(vAxis));
        Vector vPerp;
        try {
            vPerp = isZero(vDotVa) ? v : v.subtract(vAxis.scale(vDotVa));
        } catch (IllegalArgumentException e) {
            return null; // v is parallel to the axis
        }

        double a = vPerp.lengthSquared();

        double b, c;

        if (isPointOnTubeAxis(p)) {
            b = 0;
            c = -radius * radius;
        } else {
            Vector deltaP = p.subtract(p0);
            double dpDotVa = deltaP.dotProduct(vAxis);
            Vector dpPerp;
            try {
                dpPerp = isZero(dpDotVa) ? deltaP : deltaP.subtract(vAxis.scale(dpDotVa));
            } catch (IllegalArgumentException e) {
                dpPerp = deltaP;
            }
            b = 2 * vPerp.dotProduct(dpPerp);
            c = dpPerp.lengthSquared() - radius * radius;
        }

        // Solve the quadratic equation: at^2 + bt + c = 0
        double discriminant = alignZero(b * b - 4 * a * c);
        if (discriminant < 0) return null;

        double sqrtDiscriminant = Math.sqrt(discriminant);
        double t1 = alignZero((-b + sqrtDiscriminant) / (2 * a));
        double t2 = alignZero((-b - sqrtDiscriminant) / (2 * a));

        Point p1 = t1 > 0 ? ray.getPoint(t1) : null;
        Point p2 = t2 > 0 ? ray.getPoint(t2) : null;

        if (p1 != null && p2 != null) {
            if (alignZero(t1 - t2) == 0)
                return List.of(new Intersection(this, p1));
            return t1 < t2 ? List.of(
                    new Intersection(this, p1),
                    new Intersection(this, p2)
            ) :
                    List.of(
                            new Intersection(this, p2),
                            new Intersection(this, p1));
        }

        if (p1 != null)
            return List.of(new Intersection(this, p1));
        if (p2 != null)
            return List.of(new Intersection(this, p2));

        return null;
    }
}
