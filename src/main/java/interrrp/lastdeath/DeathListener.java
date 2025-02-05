package interrrp.lastdeath;

import org.slf4j.Logger;
import interrrp.lastdeath.storage.DeathStorage;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * An event listener to save deaths to a {@link DeathStorage} when players die.
 * 
 * @see #register()
 */
public final class DeathListener {
    private final Logger logger;
    private final DeathStorage storage;

    public DeathListener(Logger logger, DeathStorage storage) {
        this.logger = logger;
        this.storage = storage;
    }

    /**
     * Register the {@link ServerLivingEntityEvents.AfterDeath} event.
     */
    public void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register(this::onEntityDeath);
    }

    private void onEntityDeath(LivingEntity entity, DamageSource source) {
        if (!(entity instanceof ServerPlayerEntity))
            return;

        final var player = (ServerPlayerEntity) entity;
        final var playerName = player.getName().getString();

        final var deathInfo = DeathInfo.fromPlayer(player);
        storage.setLastDeathOf(playerName, deathInfo);

        logger.info("Saving death of {}", playerName);
        final var saveSuccessful = storage.saveToPersistentStorage();
        if (saveSuccessful) {
            Feedback.info(player, "Your death has been recorded.");
            Feedback.info(player, "Do /lastdeath to teleport to your death location.");
        } else {
            Feedback.error(player,
                    "Failed to save your death location. It may not persist after a restart.");
            Feedback.info(player, "You can still use /lastdeath for now.");
        }
    }
}
