package org.crychicteam.passiveintegration.mixins.cgm.bonuscompat;

import com.mrcrayfish.guns.common.ReloadTracker;
import com.mrcrayfish.guns.util.GunEnchantmentHelper;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.crychicteam.passiveintegration.config.CgmConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author M1hono.
 */
@Mixin(ReloadTracker.class)
public class ReloadTrackerMixin {
    @Final
    @Shadow(remap = false)
    private int startTick;

    @Final
    @Shadow(remap = false)
    private ItemStack stack;

    @Inject(method = "canReload", at = @At("HEAD"), remap = false, cancellable = true)
    private void passiveIntegration$modifyReloadSpeed(Player player, CallbackInfoReturnable<Boolean> cir) {
        int deltaTicks = player.tickCount - this.startTick;
        if (deltaTicks <= 0) {
            cir.setReturnValue(false);
            return;
        }

        AttributeInstance attribute = player.getAttribute(Attributes.ATTACK_SPEED);
        if (attribute == null || !CgmConfig.ENABLE_RELOAD_INTERVAL_BONUS.get()) {
            int interval = GunEnchantmentHelper.getReloadInterval(this.stack);
            cir.setReturnValue(deltaTicks % interval == 0);
            return;
        }

        double baseAttackSpeed = attribute.getBaseValue();
        if (baseAttackSpeed == 0.0) {
            int interval = GunEnchantmentHelper.getReloadInterval(this.stack);
            cir.setReturnValue(deltaTicks % interval == 0);
            return;
        }

        double attackSpeedBonus = attribute.getValue() / baseAttackSpeed - 1.0;
        int interval = GunEnchantmentHelper.getReloadInterval(this.stack);

        int modifiedInterval = Math.max(1, (int)(interval * (1 / (1 + Math.max(0, attackSpeedBonus)))));
        cir.setReturnValue(deltaTicks % modifiedInterval == 0);
    }
}