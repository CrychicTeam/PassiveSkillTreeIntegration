package org.crychicteam.passiveintegration.events.tacz;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import com.tacz.guns.api.event.common.GunFireEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.crychicteam.passiveintegration.util.TaczUtilHelpers;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author M1hono
 */
public class TACZGunsEvents {
    public static void onGunFire(GunFireEvent event) {
        var entity = event.getShooter();
        var stack = event.getGunItemStack();
//        if (entity instanceof Player player) {
//            // Do something
//            float retrievalChance = 0.0F;
//            for (ArrowRetrievalBonus bonus : BonusHandler.getSkillBonuses(player, ArrowRetrievalBonus.class)) {
//                retrievalChance += bonus.getChance();
//            }
//
//            if (player.getRandom().nextFloat() < retrievalChance) {
//                CompoundTag targetData = target.getPersistentData();
//                ListTag stuckArrowsTag = targetData.getList("StuckArrows", new CompoundTag().getId());
//                stuckArrowsTag.add(stack.save(new CompoundTag()));
//                targetData.put("StuckArrows", stuckArrowsTag);
//            }
//        }
    }

    public static void handleRetrievalBonus(EntityHurtByGunEvent.Post event) {
        Entity entity = event.getAttacker();
        if (!(entity instanceof Player player)) {
            return;
        }

        AtomicReference<ResourceLocation> ammo = new AtomicReference<>();
        AtomicReference<ItemStack> ammoItem = new AtomicReference<>();

        TimelessAPI.getCommonGunIndex(event.getGunId()).ifPresent(commonGunIndex -> {
            ammo.set(commonGunIndex.getGunData().getAmmoId());
        });

        if (ammo.get() != null) {
            TimelessAPI.getCommonAmmoIndex(ammo.get()).ifPresent(commonAmmoIndex -> {
                ammoItem.set(TaczUtilHelpers.getAmmoStackOfPlayer(player));
            });
        }

        if (ammoItem.get() == null || ammoItem.get().isEmpty()) {
            return;
        }

        var bullet = event.getBullet();
        var target = event.getHurtEntity();

        float retrievalChance = calculateRetrievalChance(player);
        if (player.getRandom().nextFloat() < retrievalChance) {
            if (target instanceof LivingEntity targetEntity) {
                CompoundTag targetData = targetEntity.getPersistentData();
                ListTag stuckAmmoTag = targetData.getList("StuckAmmo", (new CompoundTag()).getId());
                stuckAmmoTag.add(ammoItem.get().save(new CompoundTag()));
                targetData.put("StuckAmmo", stuckAmmoTag);
            }
        }
    }

    private static float calculateRetrievalChance(Player player) {
        return 0.5f;
    }

    public static  void entityKilledByGunEvent(EntityKillByGunEvent event) {
        LivingEntity entity = event.getKilledEntity();
        if (entity != null) {
            ListTag ammoTag = entity.getPersistentData().getList("StuckAmmo", (new CompoundTag()).getId());
            if (!ammoTag.isEmpty()) {
                for (Tag tag : ammoTag) {
                    ItemStack ammoStack = ItemStack.of((CompoundTag) tag);
                    entity.spawnAtLocation(ammoStack);
                }
            }
        }
    }
}
