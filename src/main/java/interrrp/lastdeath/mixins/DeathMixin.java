package interrrp.lastdeath.mixins;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import interrrp.lastdeath.DeathInfo;
import interrrp.lastdeath.LastDeathMod;

@Mixin(ServerPlayerEntity.class)
public abstract class DeathMixin {
    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(DamageSource source, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        String name = player.getName().getString();
        LastDeathMod.STORAGE.setLastDeathInfo(name, DeathInfo.fromPlayer(player));
    }
}
