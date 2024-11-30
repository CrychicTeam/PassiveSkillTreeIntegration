package org.crychicteam.passiveintegration.util;

import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.util.concurrent.atomic.AtomicReference;

public class TaczUtilHelpers {
    public static ItemStack getAmmoStackOfPlayer(Player player) {
        var mainHandStack = player.getMainHandItem();
        AtomicReference<ItemStack> result = new AtomicReference<>(ItemStack.EMPTY);
        if (mainHandStack.getItem() instanceof AbstractGunItem gunItem) {
            player.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(handler -> {
                for (int i = 0; i < handler.getSlots(); ++i) {
                    ItemStack checkAmmoStack = handler.getStackInSlot(i);
                    Item patt3712$temp = checkAmmoStack.getItem();
                    if (patt3712$temp instanceof IAmmo iAmmo) {
                        if (iAmmo.isAmmoOfGun(mainHandStack, checkAmmoStack)) {
                            result.set(checkAmmoStack);
                        }
                    }

                    patt3712$temp = checkAmmoStack.getItem();
                    if (patt3712$temp instanceof IAmmoBox iAmmoBox) {
                        if (iAmmoBox.isAmmoBoxOfGun(mainHandStack, checkAmmoStack)) {
                            assert result.get() != null;
                            result.set(checkAmmoStack);
                        }
                    }
                }
            });
        }
        return result.get();
    }
}
