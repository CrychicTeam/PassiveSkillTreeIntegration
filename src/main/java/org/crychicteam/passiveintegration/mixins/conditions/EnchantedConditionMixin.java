package org.crychicteam.passiveintegration.mixins.conditions;

import daripher.skilltree.skill.bonus.condition.item.EnchantedCondition;
import net.minecraft.world.item.ItemStack;
import org.crychicteam.passiveintegration.PassiveIntegration;
import org.crychicteam.passiveintegration.optional.cgm.OptionalBonusCgmAction;
import org.crychicteam.passiveintegration.optional.cgm.OptionalBonusTaczAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author M1hono.
 */
@Mixin(EnchantedCondition.class)
public class EnchantedConditionMixin {
    @Inject(method = "met", at = @At("RETURN"), remap = false, cancellable = true)
    private void passiveIntegration$met(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (PassiveIntegration.isLoaded("cgm")) {
            cir.setReturnValue(OptionalBonusCgmAction.isEnchanted(stack) || cir.getReturnValue());
        }
        if (PassiveIntegration.isLoaded("tacz")) {
            cir.setReturnValue(OptionalBonusTaczAction.isRangedWeapon(stack) || cir.getReturnValue());
        }
    }
}
