package primitives;

import geometries.Intersectable.Intersection;

import java.util.List;

import static primitives.Util.isZero;


/**
 * Represents a ray in 3D space, defined by a starting point (head) and a direction vector.
 */
public class Ray {

    /**
     * The starting point of the ray.
     */
    private final Point head;

    /**
     * The normalized direction vector of the ray.
     */
    private final Vector direction;
    private static final double DELTA = 0.1;


    /**
     * Constructs a ray with a given starting point and direction vector.
     * The direction vector is normalized upon creation.
     *
     * @param head      The starting point of the ray.
     * @param direction The direction vector of the ray.
     */
    public Ray(Point head, Vector direction) {
        this.head = head;
        this.direction = direction.normalize();
    }

    public Ray(Point head, Vector direction, Vector normal) {
        this.direction = direction.normalize();
        double nv = normal.dotProduct(this.direction);
        if (!Util.isZero(nv)) {
            this.head = head.add(normal.scale(nv > 0 ? DELTA : -DELTA));
        } else {
            this.head = head;
        }
    }

    public Vector getDirection() {
        return direction;
    }

    public Point getHead() {
        return head;
    }

    public Point findClosestPoint(List<Point> points) {
        return points == null || points.isEmpty() ? null
                : findClosestIntersection(
                points.stream().map(p -> new Intersection(null, p)).toList()).point;
    }

    public Intersection findClosestIntersection(List<Intersection> intersections) {
        if (intersections.isEmpty())
            return null;
        Intersection closestIntersection = intersections.get(0);
        double minDistance = head.distanceSquared(closestIntersection.point);
        double currentDistance;
        for (Intersection intersection : intersections) {
            currentDistance = head.distanceSquared(intersection.point);
            if (currentDistance < minDistance) {
                closestIntersection = intersection;
                minDistance = currentDistance;
            }
        }
        return closestIntersection;
    }

    /**
     * Returns a point located at a distance `t` along the ray, starting from the head.
     * The point is calculated as: {@code head + t * direction}.
     *
     * @param t The distance along the ray. A positive value moves the point in the direction
     *          of the ray, while a negative value moves the point in the opposite direction.
     * @return The point located at distance `t` along the ray from the head.
     */
    public Point getPoint(double t) {
        if (isZero(t))
            return head;
        return head.add(direction.scale(t));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return (obj instanceof Ray other) &&
                head.equals(other.head) && direction.equals(other.direction);
    }

    @Override
    public String toString() {
        return "Ray: head = " + head + direction;
    }
}

