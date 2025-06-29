package primitives;

public abstract class CBR {
    public abstract CBR surround(CBR other);

    public abstract boolean intersects(Ray ray);

    public abstract int longestAxis(); // 0 for x, 1 for y, 2 for z

    public abstract double center(int axis); // Returns center value along specified axis

    public abstract double surfaceArea();

}
