package interrrp.lastdeath;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mojang.brigadier.Command;

public final class LastDeathMod implements ModInitializer {
    public static final String MOD_ID = "lastdeath";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static DeathStorage STORAGE = new DeathStorage();

    @Override
    public void onInitialize() {
        try {
            STORAGE.load();
        } catch (Exception exc) {
            LOGGER.warn("Failed to load last deaths", exc);
            LOGGER.warn("Previously stored death data was lost due to the above error");
        }

        ServerLivingEntityEvents.AFTER_DEATH.register(this::onPlayerDeath);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("lastdeath").executes(context -> {
                var player = context.getSource().getPlayer();
                var name = player.getName().getString();

                var lastDeath = STORAGE.getLastDeath(name);
                if (lastDeath == null) {
                    Feedback.error(player, "The server has not recorded your recent death.");
                    return -1;
                }
                var pos = lastDeath.pos();

                ServerWorld world = player.server.getWorld(RegistryKey.of(
                        RegistryKey.ofRegistry(new Identifier("minecraft", "dimension")),
                        new Identifier(lastDeath.world())));

                player.teleport(world, pos.getX(), pos.getY(), pos.getZ(), lastDeath.yaw(),
                        lastDeath.pitch());
                return Command.SINGLE_SUCCESS;
            }));
        });
    }

    private void onPlayerDeath(LivingEntity entity, DamageSource source) {
        if (!(entity instanceof ServerPlayerEntity))
            return;
        var player = (ServerPlayerEntity) entity;

        String name = player.getName().getString();
        try {
            LastDeathMod.STORAGE.setLastDeath(name, DeathInfo.fromPlayer(player));
            Feedback.info(player, String.format("Your death at %s has been recorded.",
                    player.getBlockPos().toShortString()));
            Feedback.info(player, "Do /lastdeath to teleport to your death location.");
        } catch (Exception exc) {
            LastDeathMod.LOGGER.error("Failed to save death info", exc);
            Feedback.error(player, "Failed to save death info");
        }
    }
}
