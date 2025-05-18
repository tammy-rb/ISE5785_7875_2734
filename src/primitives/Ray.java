package primitives;

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

    public Vector getDirection() {
        return direction;
    }

    public Point getHead() {
        return head;
    }

    public Point findClosestPoint(List<Point> points) {
        if (points.isEmpty())
            return null;
        Point closestPoint = points.get(0);
        double minDistance = head.distanceSquared(closestPoint);
        double currentDistance = 0;
        for (Point point : points) {
            currentDistance = head.distanceSquared(point);
            if (currentDistance < minDistance) {
                closestPoint = point;
                minDistance = currentDistance;
            }
        }
        return closestPoint;
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

