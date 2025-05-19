package geometries.factory;

import geometries.Geometry;
import java.util.Map;

public interface GeometryFactory <T> {
    Geometry createGeometry(T item);
}