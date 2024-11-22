package org.crychicteam.passiveintegration.mixins.cgm.bonuscompat.conditions;

import daripher.skilltree.skill.bonus.condition.damage.ProjectileDamageCondition;
import net.minecraft.world.damagesource.DamageSource;
import org.crychicteam.passiveintegration.PassiveIntegration;
import org.crychicteam.passiveintegration.optional.cgm.OptionalBonusAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author M1hono.
 */
@Mixin(ProjectileDamageCondition.class)
public class ProjectileDamageConditionMixin {
    @Inject(method = "met", at = @At(value = "RETURN"), cancellable = true, remap = false)
    private void onProjectileDamageConditionMet(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (PassiveIntegration.isLoaded("cgm")) {
            cir.setReturnValue(OptionalBonusAction.isProjectileDamage(source, cir.getReturnValue()));
        }
    }
}
