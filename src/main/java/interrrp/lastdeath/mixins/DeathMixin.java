package interrrp.lastdeath.mixins;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import interrrp.lastdeath.DeathInfo;
import interrrp.lastdeath.Feedback;
import interrrp.lastdeath.LastDeathMod;

@Mixin(ServerPlayerEntity.class)
public abstract class DeathMixin {
    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(DamageSource source, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

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
