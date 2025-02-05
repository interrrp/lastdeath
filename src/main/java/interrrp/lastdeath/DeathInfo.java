package interrrp.lastdeath;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

/**
 * Information of a player's death.
 * 
 * @see #fromPlayer(ServerPlayerEntity)
 */
public record DeathInfo(String world, Vec3d pos, float yaw, float pitch) {
    /**
     * Returns a new instance of {@link DeathInfo} from a dead player.
     * 
     * @param player The player.
     * @return The {@link DeathInfo} instance constructed from the player's current state.
     */
    public static DeathInfo fromPlayer(ServerPlayerEntity player) {
        final var world = player.getServerWorld().getRegistryKey().getValue().getPath();
        final var pos = player.getPos();
        final var yaw = player.getYaw();
        final var pitch = player.getPitch();
        return new DeathInfo(world, pos, yaw, pitch);
    }
}
