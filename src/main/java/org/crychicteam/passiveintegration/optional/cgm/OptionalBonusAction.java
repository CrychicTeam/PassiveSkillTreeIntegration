package org.crychicteam.passiveintegration.optional.cgm;

import com.mrcrayfish.guns.entity.ProjectileEntity;
import com.mrcrayfish.guns.init.ModEnchantments;
import daripher.skilltree.mixin.AbstractArrowAccessor;
import daripher.skilltree.skill.bonus.player.ArrowRetrievalBonus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.crychicteam.passiveintegration.config.CgmConfig;
import org.crychicteam.passiveintegration.util.BonusHandler;

public class OptionalBonusAction {
    public static boolean shouldCancelCritBonuses(LivingHurtEvent event) {
        return event.getSource().getDirectEntity() instanceof ProjectileEntity;
    }

    public static void handleArrowRetrieval(LivingHurtEvent event) {
        if (!CgmConfig.ENABLE_AMMO_RETRIEVAL.get()) {
            return;
        }

        Entity sourceEntity = event.getSource().getDirectEntity();
        if (!(sourceEntity instanceof ProjectileEntity projectile)) {
            return;
        }

        Entity shooter = event.getSource().getEntity();
        if (!(shooter instanceof Player player)) {
            return;
        }

        AbstractArrowAccessor arrowAccessor = (AbstractArrowAccessor) projectile;
        ItemStack arrowStack = arrowAccessor.invokeGetPickupItem();
        if (arrowStack == null) {
            return;
        }

        float retrievalChance = calculateRetrievalChance(player);
        if (player.getRandom().nextFloat() < retrievalChance) {
            addArrowToTarget(event.getEntity(), arrowStack);
        }
    }

    private static float calculateRetrievalChance(Player player) {
        float chance = 0.0F;

        for (ArrowRetrievalBonus bonus : BonusHandler.getSkillBonuses(player, ArrowRetrievalBonus.class)) {
            chance += bonus.getChance();
        }

        int reclaimedLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.RECLAIMED.get(),
                player.getMainHandItem());
        if (reclaimedLevel > 0) {
            chance += (reclaimedLevel * CgmConfig.RECLAIMED_ENCHANTMENT_BONUS.get().floatValue());
        }

        return chance;
    }

    private static void addArrowToTarget(LivingEntity target, ItemStack arrowStack) {
        CompoundTag targetData = target.getPersistentData();
        ListTag stuckArrowsTag = targetData.getList("StuckArrows", new CompoundTag().getId());
        stuckArrowsTag.add(arrowStack.save(new CompoundTag()));
        targetData.put("StuckArrows", stuckArrowsTag);
    }
}