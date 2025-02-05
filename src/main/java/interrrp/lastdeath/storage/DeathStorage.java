package interrrp.lastdeath.storage;

import java.util.HashMap;
import java.util.Map;
import interrrp.lastdeath.DeathInfo;

/**
 * An abstract base class storing deaths of players.
 * 
 * @see JsonDeathStorage
 */
public abstract class DeathStorage {
    protected final Map<String, DeathInfo> deaths = new HashMap<>();

    /**
     * Returns the information of a player's last death.
     * 
     * @param username The username of the player.
     * @return The {@link DeathInfo} associated with the player's last death.
     */
    public DeathInfo getLastDeathOf(final String username) {
        return deaths.get(username);
    }

    /**
     * Updates the information of a player's last death.
     * 
     * This will not save to persistent storage. To do so, call {@link #saveToPersistentStorage()}.
     * 
     * @param username The username of the player.
     * @param deathInfo The {@link DeathInfo} of the player's last death.
     */
    public void setLastDeathOf(final String username, final DeathInfo deathInfo) {
        deaths.put(username, deathInfo);
    }

    /**
     * Loads the information of players' last deaths from a persistent storage source.
     * 
     * You may then call {@link #getLastDeathOf(String)} or
     * {@link #setLastDeathOf(String, DeathInfo)} to interact with the loaded data.
     * 
     * @return {@code true} if the operation succeeded, or {@code false} if not.
     */
    public abstract boolean loadFromPersistentStorage();

    /**
     * Saves the information of players' last deaths to a persistent storage source.
     * 
     * You can expect {@link #loadFromPersistentStorage()} to load it back when the game restarts.
     * 
     * @return {@code true} if the operation succeeded, or {@code false} if not.
     */
    public abstract boolean saveToPersistentStorage();
}
