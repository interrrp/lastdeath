package interrrp.lastdeath.storage;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import org.slf4j.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import interrrp.lastdeath.DeathInfo;

/**
 * A death storage that stores deaths in a JSON file at {@code lastdeath.json}.
 */
public final class JsonDeathStorage extends DeathStorage {
    private static final Path JSON_FILE_PATH = Paths.get("lastdeath.json");
    private static final Type JSON_TYPE = new TypeToken<Map<String, DeathInfo>>() {}.getType();

    private final Logger logger;
    private final Gson gson = new Gson();

    public JsonDeathStorage(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public boolean loadFromPersistentStorage() {
        final var json = readJsonFile();
        if (json == null)
            return false;

        final var loaded = parseDeathsFromJson(json);
        if (loaded == null)
            return false;

        deaths.clear();
        deaths.putAll(loaded);

        return true;
    }

    @Override
    public boolean saveToPersistentStorage() {
        final var json = gson.toJson(deaths);
        return writeToJsonFile(json);
    }

    private String readJsonFile() {
        try {
            return Files.readString(JSON_FILE_PATH);
        } catch (NoSuchFileException noSuchFileException) {
            // The JSON file doesn't exist yet. Ignore the exception as the file will be created the
            // first time writeToJsonFile is called.
            return "{}";
        } catch (Exception exception) {
            logger.error(String.format("Failed to read %s", JSON_FILE_PATH), exception);
            return null;
        }
    }

    private Map<String, DeathInfo> parseDeathsFromJson(final String json) {
        try {
            return gson.fromJson(json, JSON_TYPE);
        } catch (JsonSyntaxException syntaxException) {
            logger.error(String.format("Malformed JSON in %s", JSON_FILE_PATH), syntaxException);
            return null;
        }
    }

    private boolean writeToJsonFile(final String json) {
        try {
            Files.writeString(JSON_FILE_PATH, json, StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        } catch (Exception exception) {
            logger.error(String.format("Failed to write to %s", JSON_FILE_PATH), exception);
            return false;
        }
    }
}
