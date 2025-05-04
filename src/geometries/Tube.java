package geometries;

import primitives.Double3;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Represents a tube, a radial geometry that extends along a ray.
 */
public class Tube extends RadialGeometry {
    protected final Ray axis;

    /**
     * Constructor for creating a tube.
     *
     * @param radius The radius of the tube.
     * @param axis   The axis along which the tube extends.
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
        if (isZero(t)) //p-p0 orthogonal to ray
            return u.normalize();
        Point o = axis.getPoint(t);
        return p.subtract(o).normalize();
    }
    @Override
    public List<Point> findIntersections(Ray ray) {
        Vector vAxis = axis.getDirection(); // Tube's axis direction
        Point p0 = axis.getHead(); // Tube's axis base point
        Point p = ray.getHead(); // Ray's starting point
        Vector v = ray.getDirection(); // Ray's direction

        // Special case: ray is along the tube axis
        if (p.equals(p0) && isZero(v.normalize().dotProduct(vAxis.normalize()) - 1)) {
            return null;
        }

        // Step 1: Compute vPerp = v - (v·vAxis) * vAxis
        // vPerp is the component of v perpendicular to the tube axis
        double vDotVa = alignZero(v.dotProduct(vAxis));
        Vector vPerp;
        try {
            vPerp = isZero(vDotVa) ? v : v.subtract(vAxis.scale(vDotVa));
        } catch (IllegalArgumentException e) {
            return null; // v is parallel to the axis
        }

        double a = vPerp.lengthSquared();

        double b, c;

        // Special case: ray starts on tube axis
        if (isPointOnTubeAxis(p)) {
            b = 0;
            c = -radius * radius;
        } else {
            Vector deltaP = p.subtract(p0);
            double dpDotVa = deltaP.dotProduct(vAxis);
            // dpPerp is the component of deltaP perpendicular to the tube axis
            Vector dpPerp;
            try {
                dpPerp = isZero(dpDotVa) ? deltaP : deltaP.subtract(vAxis.scale(dpDotVa));
            } catch (IllegalArgumentException e) {
                dpPerp = deltaP; // deltaP is parallel to axis
            }

            b = 2 * vPerp.dotProduct(dpPerp);
            c = dpPerp.lengthSquared() - radius * radius;
        }

        // Step 3: Solve quadratic equation: at^2 + bt + c = 0
        double discriminant = alignZero(b * b - 4 * a * c);
        if (discriminant < 0) return null;

        double sqrtDiscriminant = Math.sqrt(discriminant);
        double t1 = alignZero((-b + sqrtDiscriminant) / (2 * a));
        double t2 = alignZero((-b - sqrtDiscriminant) / (2 * a));

        Point p1 = t1 > 0 ? ray.getPoint(t1) : null;
        Point p2 = t2 > 0 ? ray.getPoint(t2) : null;

        if (p1 != null && p2 != null) {
            if (alignZero(t1 - t2) == 0) { // Tangent case (discriminant is zero)
                return List.of(p1);
            }
            return t1 < t2 ? List.of(p1, p2) : List.of(p2, p1);
        }
        if (p1 != null)
            return List.of(p1);
        if (p2 != null)
            return List.of(p2);

        return null;
    }

    // Helper method to check if a point is on the tube axis
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

}
