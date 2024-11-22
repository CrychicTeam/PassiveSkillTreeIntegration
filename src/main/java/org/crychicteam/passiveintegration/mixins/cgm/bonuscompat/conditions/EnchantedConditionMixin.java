package org.crychicteam.passiveintegration.mixins.cgm.bonuscompat.conditions;

import com.mrcrayfish.guns.item.GunItem;
import daripher.skilltree.skill.bonus.condition.item.EnchantedCondition;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.crychicteam.passiveintegration.PassiveIntegration;
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
        if (!PassiveIntegration.isLoaded("cgm")) {
            return;
        }
        cir.setReturnValue(cir.getReturnValue() || ( stack.getItem() instanceof GunItem && !EnchantmentHelper.getEnchantments(stack).isEmpty() ));
    }
}
