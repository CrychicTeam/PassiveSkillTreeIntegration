package org.crychicteam.passiveintegration.mixins.cgm.bonuscompat.conditions;

import com.mrcrayfish.guns.item.GunItem;
import daripher.skilltree.skill.bonus.condition.item.EquipmentCondition;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author M1hono.
 */
@Mixin(EquipmentCondition.class)
public class EquipmentConditionMixin {
    @Inject(method = "isRangedWeapon", at = @At("RETURN"), remap = false, cancellable = true)
    private static void passiveIntegration$isRangedWeapon(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() || stack.getItem() instanceof GunItem);
    }
}