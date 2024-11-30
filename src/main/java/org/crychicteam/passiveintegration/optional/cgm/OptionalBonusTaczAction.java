package org.crychicteam.passiveintegration.optional.cgm;

import com.tacz.guns.api.item.gun.AbstractGunItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class OptionalBonusTaczAction {
    public static boolean isRangedWeapon(ItemStack stack) {
        return stack.getItem() instanceof AbstractGunItem;
    }

    public static boolean isEnchanted(ItemStack stack) {
        return stack.getItem() instanceof AbstractGunItem &&
                !EnchantmentHelper.getEnchantments(stack).isEmpty();}
}
