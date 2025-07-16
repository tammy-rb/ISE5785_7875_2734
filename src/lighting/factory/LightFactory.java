package lighting.factory;

import lighting.LightSource;

public interface LightFactory<T> {
    LightSource createLight(T config);
}
