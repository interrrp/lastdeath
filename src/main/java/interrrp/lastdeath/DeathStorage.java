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

/**
 * Handles persistent storage of death info.
 * 
 * Deaths are saved to a JSON file named `lastdeath.json`.
 * 
 * @see #setLastDeath()
 * @see #getLastDeath()
 * @see #loadFromDisk()
 */
public final class DeathStorage {
    private static final Path FILE_PATH = Paths.get("lastdeath.json");
    private static final Type JSON_TYPE = new TypeToken<Map<String, DeathInfo>>() {}.getType();

    private final Logger logger;
    private final Gson gson = new Gson();
    private final Map<String, DeathInfo> lastDeaths = new HashMap<>();

    public DeathStorage(final Logger logger) {
        this.logger = logger;
    }

    /**
     * Sets the last death of a player and saves it to disk.
     * 
     * @param username The username of the player
     * @param deathInfo The info of the player's last death
     * @throws IOException When the file storing deaths cannot be written to
     */
    public void setLastDeath(final String username, final DeathInfo deathInfo) {
        lastDeaths.put(username, deathInfo);
        saveToDisk();
    }

    /**
     * Returns the info of a player's last death.
     * 
     * @param username The username of the player
     * @return The {@link DeathInfo} associated with the player's last death, or null if no deaths
     *         have been recorded for the player yet
     */
    public DeathInfo getLastDeath(final String username) {
        return lastDeaths.get(username);
    }

    /**
     * Loads death storages from disk.
     * 
     * @throws IOException When the file storing deaths cannot be read
     * @throws JsonSyntaxException When the file storing deaths contains invalid JSON
     */
    public void loadFromDisk() {
        createFileIfNotExists();
        loadFromFile();
        logger.info("Loaded {} death(s) from {}", lastDeaths.size(), FILE_PATH);
    }

    private void loadFromFile() {
        try {
            final var json = Files.readString(FILE_PATH);
            Map<String, DeathInfo> loaded = gson.fromJson(json, JSON_TYPE);
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

    private void saveToDisk() {
        createFileIfNotExists();
        writeToFile();
        logger.info("Saved {} death(s) to {}", lastDeaths.size(), FILE_PATH);
    }

    private void writeToFile() {
        try {
            final var json = gson.toJson(lastDeaths);
            Files.writeString(FILE_PATH, json, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ioException) {
            logger.error(String.format("Failed to save deaths to %s", FILE_PATH), ioException);
        }
    }

    private void createFileIfNotExists() {
        if (Files.exists(FILE_PATH))
            return;

        logger.info("Creating {} as it does not exist yet", FILE_PATH);
        try {
            Files.createFile(FILE_PATH);
        } catch (IOException ioException) {
            logger.error(String.format("Failed to create %s", FILE_PATH), ioException);
        }
    }
}
