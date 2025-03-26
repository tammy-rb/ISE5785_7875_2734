package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VectorTest {

    @Test
    void testAdd() {
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(-2, -4, -6);
        assertEquals(new Vector(-1, -2, -3), v1.add(v2),
                "ERROR: Vector + Vector does not work correctly");

        Vector v1Opposite = new Vector(-1, -2, -3);
        assertThrows(IllegalArgumentException.class, () -> v1.add(v1Opposite),
                "ERROR: Vector + -itself does not throw an exception");
    }

    @Test
    void testScale() {
    }

    @Test
    void testDotProduct() {
    }

    @Test
    void testCrossProduct() {
    }

    @Test
    void testLengthSquared() {
    }

    @Test
    void testLength() {
    }

    @Test
    void testNormalize() {
    }
}