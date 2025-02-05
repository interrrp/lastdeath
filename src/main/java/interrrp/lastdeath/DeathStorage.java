package interrrp.lastdeath;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public final class DeathStorage {
    private static final Path FILE_PATH = Paths.get("lastdeath.json");
    private static final Type JSON_TYPE = new TypeToken<Map<String, DeathInfo>>() {}.getType();

    private final Logger logger;
    private final Gson gson = new Gson();
    private final Map<String, DeathInfo> lastDeaths = new HashMap<>();

    public DeathStorage(final Logger logger) {
        this.logger = logger;
    }

    public boolean setLastDeath(final String username, final DeathInfo deathInfo) {
        lastDeaths.put(username, deathInfo);
        return saveToDisk();
    }

    public DeathInfo getLastDeath(final String username) {
        return lastDeaths.get(username);
    }

    public void loadFromDisk() {
        createFileIfNotExists();
        loadFromFile();
        logger.info("Loaded {} death(s) from {}", lastDeaths.size(), FILE_PATH);
    }

    private void loadFromFile() {
        try {
            final var json = Files.readString(FILE_PATH);
            final Map<String, DeathInfo> loaded = gson.fromJson(json, JSON_TYPE);
            if (loaded != null) {
                lastDeaths.clear();
                lastDeaths.putAll(loaded);
            }
        } catch (IOException ioException) {
            logger.error(String.format("Failed to read %s", FILE_PATH), ioException);
        } catch (JsonSyntaxException jsonSyntaxException) {
            logger.error(String.format("Malformed JSON in %s", FILE_PATH), jsonSyntaxException);
        }
    }

    private boolean saveToDisk() {
        if (!createFileIfNotExists()) {
            return false;
        }
        return writeToFile();
    }

    private boolean writeToFile() {
        try {
            final var json = gson.toJson(lastDeaths);
            Files.writeString(FILE_PATH, json, StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        } catch (IOException ioException) {
            logger.error(String.format("Failed to save deaths to %s", FILE_PATH), ioException);
            return false;
        }
    }

    private boolean createFileIfNotExists() {
        if (Files.exists(FILE_PATH)) {
            return true;
        }

        logger.info("Creating {} as it does not exist yet", FILE_PATH);
        try {
            Files.createFile(FILE_PATH);
            return true;
        } catch (IOException ioException) {
            logger.error(String.format("Failed to create %s", FILE_PATH), ioException);
            return false;
        }
    }
}
