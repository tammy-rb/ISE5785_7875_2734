package primitives;

import java.util.Objects;

public class Ray {
    private final Point head;
    private final Vector direction;
    public Ray(Point head, Vector direction) {
        this.head = head;
        this.direction = direction.normalize();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return (obj instanceof Ray other) &&
            head.equals(other.head) && direction.equals(other.direction);
    }

    @Override
    public String toString() {
        return "Ray: head = " + head + "->" + direction;
    }
}
