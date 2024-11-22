package org.crychicteam.passiveintegration.mixins.cgm.bonuscompat.crithandler;

import com.mrcrayfish.guns.entity.ProjectileEntity;
import com.mrcrayfish.guns.init.ModDamageTypes;
import com.mrcrayfish.guns.util.GunModifierHelper;
import daripher.skilltree.mixin.AbstractArrowAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import org.crychicteam.passiveintegration.config.CgmConfig;
import org.crychicteam.passiveintegration.util.BonusHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author M1hono.
 */
@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityCritMixin implements AbstractArrowAccessor {
    @Shadow(remap = false)
    public abstract LivingEntity getShooter();
    @Shadow(remap = false)
    private ItemStack item;

    @Inject(method = "getCriticalDamage", at = @At("HEAD"), cancellable = true, remap = false)
    private void passiveIntegration$modifyCriticalDamage(ItemStack weapon, RandomSource rand, float damage, CallbackInfoReturnable<Float> cir) {
        if (CgmConfig.ENABLE_CRIT_INTEGRATION.get()) {
            if(!(this.getShooter() instanceof ServerPlayer player)) {
                return;
            }

            DamageSource source = ModDamageTypes.Sources.projectile(
                    ((ProjectileEntity)(Object)this).level().registryAccess(),
                    (ProjectileEntity)(Object)this,
                    this.getShooter()
            );

            float criticalChance = BonusHandler.getCritChance(player, source, null);

            criticalChance = Mth.clamp(criticalChance + GunModifierHelper.getCriticalChance(weapon), 0.0F, 1.0F);

            if(rand.nextFloat() < criticalChance) {
                float criticalMultiplier = 1.5F + BonusHandler.getCritDamageMultiplier(player, source, null);
                var criticalEvent = new CriticalHitEvent(player, (ProjectileEntity)(Object)this, damage * criticalMultiplier, true);
                MinecraftForge.EVENT_BUS.post(criticalEvent);
                cir.setReturnValue(damage * criticalMultiplier);
            } else {
                cir.setReturnValue(damage);
            }
        }
    }

    @Override
    public @Nullable ItemStack invokeGetPickupItem() {
        return item;
    }
}