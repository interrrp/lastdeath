package interrrp.lastdeath;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * Handles persistent storage of death info.
 * 
 * Deaths are saved to a JSON file named `lastdeath.json` inside the `mods` folder.
 * 
 * @see #setLastDeath()
 * @see #getLastDeath()
 * @see #load()
 */
public final class DeathStorage {
	private static final Path FILE_PATH = Paths.get("mods", "lastdeath.json");
	private static final Type JSON_TYPE = new TypeToken<Map<String, DeathInfo>>() {}.getType();

	private final Gson gson = new Gson();
	private final Map<String, DeathInfo> lastDeaths = new HashMap<>();

	/**
	 * Sets the last death of a player and saves it to disk.
	 * 
	 * @param username The username of the player
	 * @param deathInfo The info of the player's last death
	 * @throws IOException When the file storing deaths cannot be written to
	 */
	public void setLastDeath(String username, DeathInfo deathInfo) throws IOException {
		lastDeaths.put(username, deathInfo);
		save();
	}

	/**
	 * Returns the info of a player's last death.
	 * 
	 * @param username The username of the player
	 * @return The {@link DeathInfo} associated with the player's last death, or null if no deaths
	 *         have been recorded for the player yet
	 */
	public DeathInfo getLastDeath(String username) {
		return lastDeaths.get(username);
	}

	/**
	 * Loads death storages from disk.
	 * 
	 * @throws IOException When the file storing deaths cannot be read
	 * @throws JsonSyntaxException When the file storing deaths contains invalid JSON
	 */
	public void load() throws IOException, JsonSyntaxException {
		createFileIfNotExists();
		loadFromFile();
	}

	private void loadFromFile() throws IOException, JsonSyntaxException {
		try {
			var json = Files.readString(FILE_PATH);
			HashMap<String, DeathInfo> loaded = gson.fromJson(json, JSON_TYPE);
			if (loaded != null) {
				lastDeaths.clear();
				lastDeaths.putAll(loaded);
			}
		} catch (IOException exc) {
			throw new IOException(String.format("Failed to read %s", FILE_PATH), exc);
		} catch (JsonSyntaxException exc) {
			throw new JsonSyntaxException(String.format("Malformed JSON at %s", FILE_PATH), exc);
		}
	}

	private void save() throws IOException {
		createFileIfNotExists();
		writeToFile();
	}

	private void writeToFile() throws IOException {
		try {
			var json = gson.toJson(lastDeaths);
			Files.writeString(FILE_PATH, json, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException exc) {
			throw new IOException(String.format("Failed to save deaths to %s", FILE_PATH), exc);
		}
	}

	private void createFileIfNotExists() throws IOException {
		if (Files.exists(FILE_PATH))
			return;

		LastDeathMod.LOGGER.info("Creating {} as it does not exist yet", FILE_PATH);
		try {
			Files.createFile(FILE_PATH);
		} catch (IOException exc) {
			throw new IOException(String.format("Failed to create %s"), exc);
		}
	}
}
