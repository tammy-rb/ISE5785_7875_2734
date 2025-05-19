package parsers.scene;
import java.util.Map;

public interface SceneParser {
    Map<String, Object> parse(String filePath) throws Exception;
}
