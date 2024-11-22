package org.crychicteam.passiveintegration.mixins.cgm.bonuscompat.crithandler;

import com.mrcrayfish.guns.entity.ProjectileEntity;
import com.mrcrayfish.guns.init.ModEnchantments;
import com.mrcrayfish.guns.util.GunEnchantmentHelper;
import daripher.skilltree.mixin.AbstractArrowAccessor;
import daripher.skilltree.skill.bonus.SkillBonusHandler;
import daripher.skilltree.skill.bonus.player.ArrowRetrievalBonus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import org.crychicteam.passiveintegration.config.CgmConfig;
import org.crychicteam.passiveintegration.util.BonusHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;

/**
 * @author M1hono.
 */
@Mixin(SkillBonusHandler.class)
public class SkillBonusHandlerMixin {
    @Inject(method = "applyCritBonuses(Lnet/minecraftforge/event/entity/living/LivingHurtEvent;)V", at = @At(value = "HEAD"), remap = false, cancellable = true)
    private static void passiveIntegration$applyCriticalBonuses(LivingHurtEvent event, CallbackInfo ci) {
        if (event.getSource().getDirectEntity() instanceof ProjectileEntity) {
            ci.cancel();
        }
    }

    @Inject(method = "applyArrowRetrievalBonus", at = @At(value = "HEAD"), remap = false)
    private static void passiveIntegration$applyArrowRetrievalBonus(LivingHurtEvent event, CallbackInfo ci) {
        Entity var2 = event.getSource().getDirectEntity();
        if (var2 instanceof ProjectileEntity projectile) {
            if (CgmConfig.ENABLE_AMMO_RETRIEVAL.get()) {
                Entity var3 = event.getSource().getEntity();
                if (var3 instanceof Player player) {
                    AbstractArrowAccessor var10 = (AbstractArrowAccessor) projectile;
                    ItemStack arrowStack = var10.invokeGetPickupItem();
                    if (arrowStack != null) {
                        float retrievalChance = 0.0F;

                        for (ArrowRetrievalBonus bonus : BonusHandler.getSkillBonuses(player, ArrowRetrievalBonus.class)) {
                            retrievalChance += bonus.getChance();
                        }

                        int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.RECLAIMED.get(),
                                player.getMainHandItem());
                        if (level > 0) {
                            retrievalChance += (level * CgmConfig.RECLAIMED_ENCHANTMENT_BONUS.get().floatValue());
                        }

                        if (player.getRandom().nextFloat() < retrievalChance) {
                            LivingEntity target = event.getEntity();
                            CompoundTag targetData = target.getPersistentData();
                            ListTag stuckArrowsTag = targetData.getList("StuckArrows", new CompoundTag().getId());
                            stuckArrowsTag.add(arrowStack.save(new CompoundTag()));
                            targetData.put("StuckArrows", stuckArrowsTag);
                        }
                    }
                }
            }
        }
    }
}
