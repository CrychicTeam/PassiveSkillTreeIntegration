package org.crychicteam.passiveintegration.mixins.cgm.bonuscompat;

import com.mrcrayfish.guns.entity.ProjectileEntity;
import daripher.skilltree.skill.bonus.condition.damage.ProjectileDamageCondition;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProjectileDamageCondition.class)
public class ProjectileDamageConditionMixin {

    @Inject(method = "met", at = @At(value = "RETURN"), cancellable = true, remap = false)
    private void onProjectileDamageConditionMet(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() || source.getDirectEntity() instanceof ProjectileEntity);
    }
}