package interrrp.lastdeath;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import interrrp.lastdeath.storage.DeathStorage;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * A server command to teleport players to their last death.
 * 
 * @see #register()
 */
public final class LastDeathCommand {
    private static final int SUCCESS = 1;
    private static final int FAILURE = -1;
    private static final RegistryKey<Registry<World>> DIMENSION_REGISTRY_KEY =
            RegistryKey.ofRegistry(new Identifier("minecraft:dimension"));

    private final DeathStorage storage;

    public LastDeathCommand(DeathStorage storage) {
        this.storage = storage;
    }

    public void register() {
        CommandRegistrationCallback.EVENT
                .register((dispatcher, _0, _1) -> registerCommandToDispatcher(dispatcher));
    }

    private void registerCommandToDispatcher(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("lastdeath").executes(this::runCommand));
    }

    private int runCommand(CommandContext<ServerCommandSource> context) {
        final var player = context.getSource().getPlayer();
        if (player == null) {
            Feedback.error(player, "This command may only be used by players.");
            return FAILURE;
        }
        final var playerName = player.getName().getString();

        final var lastDeath = storage.getLastDeathOf(playerName);
        if (lastDeath == null) {
            Feedback.error(player, "Your last death has not been recorded.");
            return FAILURE;
        }
        final var worldId = lastDeath.world();
        final var pos = lastDeath.pos();
        final var yaw = lastDeath.yaw();
        final var pitch = lastDeath.pitch();

        final var world = getWorld(player.server, worldId);

        player.teleport(world, pos.getX(), pos.getY(), pos.getZ(), yaw, pitch);

        return SUCCESS;
    }

    private static ServerWorld getWorld(MinecraftServer server, String id) {
        final var registryKey = RegistryKey.of(DIMENSION_REGISTRY_KEY, new Identifier(id));
        return server.getWorld(registryKey);
    }
}
