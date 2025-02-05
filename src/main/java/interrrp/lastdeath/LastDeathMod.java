package interrrp.lastdeath;

import net.fabricmc.api.ModInitializer;
import org.slf4j.LoggerFactory;
import interrrp.lastdeath.storage.DeathStorage;
import interrrp.lastdeath.storage.JsonDeathStorage;

public final class LastDeathMod implements ModInitializer {
    @Override
    public void onInitialize() {
        final var logger = LoggerFactory.getLogger("lastdeath");
        final DeathStorage storage = new JsonDeathStorage(logger);
        final var command = new LastDeathCommand(storage);
        final var deathListener = new DeathListener(logger, storage);

        storage.loadFromPersistentStorage();
        command.register();
        deathListener.register();
    }
}
