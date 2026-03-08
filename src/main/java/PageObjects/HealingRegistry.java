package PageObjects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HealingRegistry {
    private static final String CACHE_FILE = "src/test/resources/healing_registry.json";
    private static Map<String, String> cache = new HashMap<>();
    private static final Gson gson = new Gson();

    static {
        loadCache();
    }

    private static void loadCache() {
        try (Reader reader = new FileReader(CACHE_FILE)) {
            cache = gson.fromJson(reader, new TypeToken<Map<String, String>>(){}.getType());
            if (cache == null) cache = new HashMap<>();
        } catch (IOException e) {
            cache = new HashMap<>(); // File doesn't exist yet
        }
    }

    public static String getHealedPath(String original) {
        return cache.get(original);
    }

    public static void saveHealing(String original, String healed) {
        cache.put(original, healed);
        File file = new File(CACHE_FILE);

        // Create parent directories if they don't exist
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        try (Writer writer = new FileWriter(file)) {
            gson.toJson(cache, writer);
        } catch (IOException e) {
            System.err.println("Failed to save cache: " + e.getMessage());
        }
    }
}