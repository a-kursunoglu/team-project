package fuze.framework.data;

import fuze.entity.clothing.ClothingArticle;
import fuze.entity.clothing.Photo;
import fuze.usecases.managewardrobe.WardrobeRepository;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple JSON-backed wardrobe repository. Stores items as an array of objects:
 * { name, category, weatherRating, waterproof, filePath }
 * Images themselves are not serialized; only the file path is stored.
 */
public class JsonWardrobeRepository implements WardrobeRepository {
    private final Path storageFile;
    private final List<ClothingArticle> cache = new ArrayList<>();

    public JsonWardrobeRepository(Path storageFile) {
        this.storageFile = storageFile;
        loadFromDisk();
    }

    public JsonWardrobeRepository(String storageFilePath) {
        this(Paths.get(storageFilePath));
    }

    @Override
    public synchronized void save(ClothingArticle article) {
        cache.add(article);
        persist();
    }

    @Override
    public synchronized boolean existsByName(String name) {
        return cache.stream().anyMatch(item -> item.getName().equalsIgnoreCase(name));
    }

    @Override
    public synchronized List<ClothingArticle> getAll() {
        return new ArrayList<>(cache);
    }

    @Override
    public synchronized boolean deleteByName(String name) {
        boolean removed = cache.removeIf(item -> item.getName().equalsIgnoreCase(name));
        if (removed) {
            persist();
        }
        return removed;
    }

    private void loadFromDisk() {
        try {
            ensureParentDir();
            if (!Files.exists(storageFile)) {
                return;
            }
            String json = Files.readString(storageFile, StandardCharsets.UTF_8);
            if (json.isBlank()) {
                return;
            }
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                ClothingArticle article = new ClothingArticle(
                        obj.optString("name", "Unknown"),
                        obj.optString("category", "Unknown"),
                        obj.optInt("weatherRating", 0),
                        obj.optBoolean("waterproof", false),
                        new Photo(obj.optString("filePath", ""))
                );
                cache.add(article);
            }
        } catch (Exception e) {
            // If loading fails, keep cache empty but don't crash the app.
            e.printStackTrace();
        }
    }

    private void persist() {
        try {
            ensureParentDir();
            JSONArray arr = new JSONArray();
            for (ClothingArticle item : cache) {
                JSONObject obj = new JSONObject();
                obj.put("name", item.getName());
                obj.put("category", item.getCategory());
                obj.put("weatherRating", item.getWeatherRating());
                obj.put("waterproof", item.isWaterproof());
                obj.put("filePath", item.getImage() != null ? item.getImage().getFilePath() : "");
                arr.put(obj);
            }
            Files.writeString(storageFile, arr.toString(2), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ensureParentDir() throws IOException {
        Path parent = storageFile.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }
}
