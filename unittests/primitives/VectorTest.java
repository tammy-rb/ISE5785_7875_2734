package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link primitives.Vector} class.
 */
class VectorTest {
    /**
     * Delta value for accuracy when comparing the numbers of type 'double' in
     * assertEquals
     */
    private static final double DELTA = 0.000001;

    /**
     * Test method for {@link primitives.Vector#Vector(double, double, double)}.
     */
    @Test
    void testVectorConstructorXYZ() {
        // =============== Boundary Values Tests ==================
        // TC11: Test constructing a zero vector using the first constructor throws an exception
        assertThrows(IllegalArgumentException.class, () -> new Vector(0, 0, 0),
                "ERROR: Zero vector using (x, y, z) constructor does not throw an exception");
    }

    /**
     * Test method for {@link primitives.Vector#Vector(Double3)}.
     */
    @Test
    void testVectorConstructorDouble3() {
        // =============== Boundary Values Tests ==================
        // TC11: Test constructing a zero vector using the second constructor throws an exception
        Double3 zeroXYZ = new Double3(0, 0, 0);
        assertThrows(IllegalArgumentException.class, () -> new Vector(zeroXYZ),
                "ERROR: Zero vector using (Double3) constructor does not throw an exception");
    }

    /**
     * Test method for {@link primitives.Vector#add(primitives.Vector)}.
     */
    @Test
    void testAdd() {
        // ============ Equivalence Partitions Tests ==============
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(-2, -4, -6);

        // TC01: Test adding two vectors results in the correct vector
        assertEquals(new Vector(-1, -2, -3), v1.add(v2),
                "ERROR: Vector + Vector does not work correctly");

        // =============== Boundary Values Tests ==================
        Vector v1Opposite = new Vector(-1, -2, -3);

        // TC11: Test adding a vector to its opposite throws an exception (zero vector)
        assertThrows(IllegalArgumentException.class, () -> v1.add(v1Opposite),
                "ERROR: Vector + -itself does not throw an exception");
    }

    /**
     * Test method for {@link primitives.Vector#scale(double)}.
     */
    @Test
    void testScale() {
        // ============ Equivalence Partitions Tests ==============
        Vector v = new Vector(1, 2, 3);

        // TC01: Test scaling a vector by a positive scalar
        assertEquals(new Vector(2, 4, 6), v.scale(2),
                "ERROR: Multiplying a vector with a scalar does not work correctly");

        // TC02: Test scaling a vector by a negative scalar
        assertEquals(new Vector(-2, -4, -6), v.scale(-2),
                "ERROR: Multiplying a vector with a scalar does not work correctly");

        // =============== Boundary Values Tests ==================
        // TC11: Test scaling a vector by zero throws an exception (zero vector)
        assertThrows(IllegalArgumentException.class, () -> v.scale(0),
                "ERROR: Multiplying a vector with 0 does not throw an exception");
    }

    /**
     * Unit tests for {@link Vector#dotProduct(Vector)} method.
     */
    @Test
    void testDotProduct() {
        // Vectors for testing
        Vector v1 = new Vector(1, 2, 3); // A base vector (1,2,3)
        Vector vAcute = new Vector(2, 3, 4); // A vector forming an acute (sharp) angle with v1
        Vector vObtuse = new Vector(-1, -1, 0); // A vector forming an obtuse angle with v1
        Vector vOrthogonal = new Vector(0, 3, -2); // A vector orthogonal to v1

        // ============ Equivalence Partitions Tests ==============
        // TC01: Test dot product between vectors with an acute (sharp) angle
        // The dot product should be positive.
        assertTrue(v1.dotProduct(vAcute) > 0,
                "ERROR: dotProduct() for acute angle vectors should be positive");

        // Verify expected value (computed manually)
        assertEquals(20, v1.dotProduct(vAcute), DELTA,
                "ERROR: dotProduct() wrong value for acute vectors");

        // TC02: Test dot product between vectors with an obtuse angle
        // The dot product should be negative.
        assertTrue(v1.dotProduct(vObtuse) < 0,
                "ERROR: dotProduct() for obtuse angle vectors should be negative");

        // Verify expected value
        assertEquals(-3, v1.dotProduct(vObtuse), DELTA,
                "ERROR: dotProduct() wrong value for obtuse vectors");

        // =============== Boundary Values Tests ==================
        // TC11: Test dot product between orthogonal vectors
        // The dot product should be zero.
        assertEquals(0, v1.dotProduct(vOrthogonal), DELTA,
                "ERROR: dotProduct() for orthogonal vectors is not zero");

        // TC12: Test dot product between vector to itself. angle = 0
        // the dot products should be the squared length of the vector
        assertEquals(0, v1.dotProduct(v1) - v1.lengthSquared(), DELTA,
                "ERROR: dotProduct() between vector to itself is wrong");
    }

    /**
     * Unit test for the {@link Vector#crossProduct(Vector)} method.
     * Tests the correctness of the cross product calculation, including the length and orthogonality of the resulting vector,
     * as well as behavior when the vectors are parallel.
     */
    @Test
    void testCrossProduct() {
        // Vectors for testing
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(-3, 0, 1);
        Vector v1Parallel = new Vector(-2, -4, -6); // v1Parallel is a scalar multiple of v1 (parallel vector)

        // ============ Equivalence Partitions Tests ==============
        // TC01: Test the cross-product between two non-parallel vectors
        // Perform cross-product between v1 and v2
        Vector vr = v1.crossProduct(v2);

        // Verify the length of the cross-product: It should be the product of the lengths of v1 and v2
        assertEquals(v1.length() * v2.length(), vr.length(), DELTA,
                "ERROR: crossProduct() wrong result length");

        // Verify that the cross-product result is orthogonal to the first operand (v1)
        assertEquals(0, vr.dotProduct(v1),
                "ERROR: crossProduct() result is not orthogonal to 1st operand");

        // Verify that the cross-product result is orthogonal to the second operand (v2)
        assertEquals(0, vr.dotProduct(v2),
                "ERROR: crossProduct() result is not orthogonal to 2nd operand");

        // Verify that the resulting vector matches the expected result (orthogonal vector)
        assertEquals(new Vector(2, -10, 6), vr,
                "ERROR: Vector crossProduct does not work correctly");

        // =============== Boundary Values Tests ==================
        // TC11: Test for zero vector from cross-product of parallel vectors
        // v1Parallel is parallel to v1, so the cross-product should result in a zero vector
        assertThrows(IllegalArgumentException.class, () -> v1.crossProduct(v1Parallel),
                "ERROR: crossProduct() for parallel vectors does not throw an exception");

        // TC12: Test cross-product with itself: It should result in a zero vector since the vectors are parallel
        assertThrows(IllegalArgumentException.class, () -> v1.crossProduct(v1),
                "ERROR: crossProduct() for parallel vectors does not throw an exception");
    }

    /**
     * Test method for {@link primitives.Vector#lengthSquared()}.
     */
    @Test
    void testLengthSquared() {
        // ============ Equivalence Partitions Tests ==============
        Vector v = new Vector(3, 4, 0);

        // TC01: Test squared length of a vector
        assertEquals(25, v.lengthSquared(),
                "ERROR: lengthSquared() wrong value");
    }

    /**
     * Test method for {@link primitives.Vector#length()}.
     */
    @Test
    void testLength() {
        // ============ Equivalence Partitions Tests ==============
        Vector v = new Vector(3, 4, 0);

        // TC01: Test length of a vector
        assertEquals(5, v.length(),
                "ERROR: length() wrong value");
    }

    /**
     * Test method for {@link primitives.Vector#normalize()}.
     */
    @Test
    void testNormalize() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Test normalizing a vector results in a unit vector
        // Create a vector with coordinates (3, 4, 0)
        Vector v = new Vector(3, 4, 0);
        // Normalize the vector
        Vector normalized = v.normalize();

        // Check if the length of the normalized vector is 1 (unit vector)
        // The difference between the length of the normalized vector and 1 should be 0
        assertEquals(0, normalized.length() - 1,
                "ERROR: the normalized vector is not a unit vector");

        // check that the normalized vector parallel to the original
        assertThrows(IllegalArgumentException.class, () -> v.crossProduct(normalized),
                "ERROR: the normalized vector is not parallel to the original one");

        // check that the normalized the same direction as the original
        assertTrue(v.dotProduct(normalized) > 0,
                "ERROR: the normalized vector is opposite to the original one");
    }

    /**
     * Unit test for {@link Vector#subtract(Point)} method.
     * Tests the subtraction of a Point from a Vector, as well as handling of invalid operations.
     *
     * @author Your Name
     */
    @Test
    void testSubtruct() {
        // Creating a vector v and points p1 and p2 for testing
        Vector v = new Vector(1, 2, 3); // Vector with components (1, 2, 3)
        Point p1 = new Point(-2, -4, -6); // Point with components (-2, -4, -6)
        Point p2 = new Point(1, 2, 3); // Point with components (1, 2, 3)

        // ============ Equivalence Partitions Tests ==============
        // TC01: Test subtracting a Point from a Vector (valid operation)
        // Subtracting the point p1 from vector v should result in a new vector.
        assertEquals(new Vector(3, 6, 9), v.subtract(p1),
                "ERROR: Vector - Point does not work correctly");

        // =============== Boundary Values Tests ==================
        // TC11: Test subtracting a Point from a vector where the Point is the same as the vector
        // This should throw an IllegalArgumentException as subtracting a point from itself is an invalid operation.
        assertThrows(IllegalArgumentException.class, () -> v.subtract(p2),
                "ERROR: Vector - its point does not throw an exception");
    }
}