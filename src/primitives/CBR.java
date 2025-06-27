package primitives;

public abstract class CBR {
    public abstract CBR surround(CBR other);
    public abstract boolean intersects(Ray ray);
}
