package primitives;

public class Vector extends Point{

    public Vector(double x, double y, double z) {
        super(x, y, z);
        if (this.xyz.equals(Double3.ZERO)){
            throw new IllegalArgumentException("Vector is zero");
        }
    }

    public Vector(Double3 xyz) {
        super(xyz);
        if (this.xyz.equals(Double3.ZERO)){
            throw new IllegalArgumentException("Vector is zero");
        }
    }

    public Vector add(Vector v){
        return new Vector(this.xyz.add(v.xyz));
    }

    public Vector scale(double x){
        return new Vector(this.xyz.scale(x));
    }

    public Double dotProduct(Vector v){
        return this.xyz.d1() * v.xyz.d1() + this.xyz.d2() * v.xyz.d2() + this.xyz.d3() * v.xyz.d3();
    }

    public Vector crossProduct(Vector v){
        double x = this.xyz.d2() * v.xyz.d3() - this.xyz.d3() * v.xyz.d2();
        double y = this.xyz.d3() * v.xyz.d1() - this.xyz.d1() * v.xyz.d3();
        double z = this.xyz.d1() * v.xyz.d2() - this.xyz.d2() * v.xyz.d1();
        return new Vector(x, y, z);
    }

    public double lengthSquared(){
        return this.dotProduct(this);
    }

    public double length(){
        return Math.sqrt(this.lengthSquared());
    }

    public Vector normalize(){
        return this.scale(1/this.length());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return (obj instanceof Vector other) && super.equals(other);
    }

    @Override
    public String toString() {
        return "v" + super.toString();
    }
}
