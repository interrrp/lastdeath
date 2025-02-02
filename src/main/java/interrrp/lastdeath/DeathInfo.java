package interrrp.lastdeath;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public record DeathInfo(String world, Vec3d pos, float yaw, float pitch) {
    public static DeathInfo fromPlayer(ServerPlayerEntity player) {
        var world = player.getServerWorld().getRegistryKey().getValue().getPath();
        var pos = player.getPos();
        var yaw = player.getYaw();
        var pitch = player.getPitch();
        return new DeathInfo(world, pos, yaw, pitch);
    }
}
