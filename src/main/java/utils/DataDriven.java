package utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads test data from testData/testData.json.
 */
public class DataDriven {

    private static final String JSON_PATH = "testData/testData.json";

    private static JSONObject readRoot() {
        try (FileReader reader = new FileReader(JSON_PATH)) {
            return (JSONObject) new JSONParser().parse(reader);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read " + JSON_PATH, e);
        }
    }

    /** Nested value, e.g. jsonReader("validUser", "username"). */
    public static String jsonReader(String category, String key) {
        JSONObject section = (JSONObject) readRoot().get(category);
        return (String) section.get(key);
    }

    /** Top-level string value, e.g. jsonReader("productToRemove"). */
    public static String jsonReader(String key) {
        return (String) readRoot().get(key);
    }

    /** Top-level list of strings, e.g. jsonArrayReader("cartProducts"). */
    public static List<String> jsonArrayReader(String key) {
        JSONArray array = (JSONArray) readRoot().get(key);
        List<String> result = new ArrayList<>();
        for (Object item : array) {
            result.add((String) item);
        }
        return result;
    }
}
