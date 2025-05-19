package scene;

public interface SceneFactory<T> {
    Scene createScene(T item) throws Exception;
}