package interrrp.lastdeath;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mojang.brigadier.Command;

public class LastDeathMod implements ModInitializer {
    public static final String MOD_ID = "lastdeath";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final DeathStorage STORAGE = new DeathStorage();

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("lastdeath").executes(context -> {
                var player = context.getSource().getPlayer();
                var name = player.getName().getString();

                var deathInfo = STORAGE.getLastDeathInfo(name);
                if (deathInfo == null) {
                    return -1;
                }
                var deathPos = deathInfo.pos();

                ServerWorld world = player.server.getWorld(RegistryKey.of(
                        RegistryKey.ofRegistry(new Identifier("minecraft", "dimension")),
                        new Identifier(deathInfo.world())));

                player.teleport(world, deathPos.getX(), deathPos.getY(), deathPos.getZ(),
                        deathInfo.yaw(), deathInfo.pitch());
                return Command.SINGLE_SUCCESS;
            }));
        });
    }
}
