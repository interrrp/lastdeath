package interrrp.lastdeath;

import net.fabricmc.api.ModInitializer;
import org.slf4j.LoggerFactory;

public final class LastDeathMod implements ModInitializer {
    @Override
    public void onInitialize() {
        final var logger = LoggerFactory.getLogger("lastdeath");
        final var storage = new DeathStorage(logger);
        final var command = new LastDeathCommand(storage);
        final var deathListener = new DeathListener(logger, storage);

        storage.loadFromDisk();
        command.register();
        deathListener.register();
    }
}
