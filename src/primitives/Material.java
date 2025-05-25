package primitives;

public class Material {
    public Double3 kA = Double3.ONE;

    public Material setKA(Double kA) {
        this.kA = new Double3(kA);
        return this;
    }

    public Material setKA(Double3 kA) {
        this.kA = kA;
        return this;
    }
}
