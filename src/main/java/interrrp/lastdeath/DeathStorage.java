package interrrp.lastdeath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public final class DeathStorage {
    private static final Gson GSON = new Gson();
    private static final Path FILE_PATH = Paths.get("mods/lastdeath.json");
    private Map<String, DeathInfo> lastDeathPositions = new HashMap<>();

    public DeathStorage() {
        load();
    }

    public void setLastDeathInfo(String username, DeathInfo deathInfo) {
        lastDeathPositions.put(username, deathInfo);
        save();
    }

    public DeathInfo getLastDeathInfo(String username) {
        return lastDeathPositions.get(username);
    }

    private void load() {
        createFileIfNotExists();
        try {
            String json = Files.readString(FILE_PATH);
            var gsonType = new TypeToken<Map<String, DeathInfo>>() {}.getType();
            lastDeathPositions = GSON.fromJson(json, gsonType);
        } catch (IOException ioException) {
            LastDeathMod.LOGGER.info("Failed to read last deaths from {}", FILE_PATH, ioException);
        } catch (JsonSyntaxException jsonException) {
            LastDeathMod.LOGGER.info("Failed to parse last deaths from {}", FILE_PATH,
                    jsonException);
        }
    }

    private void save() {
        createFileIfNotExists();
        try {
            byte[] bytes = GSON.toJson(lastDeathPositions).getBytes();
            Files.write(FILE_PATH, bytes, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException exception) {
            LastDeathMod.LOGGER.error("Could not save last deaths to {}", FILE_PATH, exception);
        }
    }

    private void createFileIfNotExists() {
        if (!Files.exists(FILE_PATH)) {
            try {
                Files.write(FILE_PATH, "{}".getBytes(), StandardOpenOption.CREATE);
            } catch (IOException exception) {
                LastDeathMod.LOGGER.error("Could not create {}", FILE_PATH, exception);
            }
        }
    }
}
