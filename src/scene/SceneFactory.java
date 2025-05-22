package scene;

/**
 * A generic factory interface for creating {@link Scene} instances.
 *
 * @param <T> The type of input used to create the scene (e.g., a file path or configuration object).
 */
public interface SceneFactory<T> {

    /**
     * Creates a {@link Scene} instance based on the given input.
     *
     * @param item The input used to construct the scene.
     * @return A {@link Scene} instance.
     * @throws Exception If the scene cannot be created due to invalid input or errors during processing.
     */
    Scene createScene(T item) throws Exception;
}
