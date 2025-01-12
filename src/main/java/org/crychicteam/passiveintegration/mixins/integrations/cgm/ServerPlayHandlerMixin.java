package org.crychicteam.passiveintegration.mixins.integrations.cgm;

import com.mrcrayfish.guns.common.network.ServerPlayHandler;
import com.mrcrayfish.guns.network.message.C2SMessageShoot;
import daripher.skilltree.skill.bonus.player.ArrowRetrievalBonus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.crychicteam.passiveintegration.config.CgmConfig;
import org.crychicteam.passiveintegration.util.BonusHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author M1hono.
 */
@Mixin(value = ServerPlayHandler.class, remap = false)
@Pseudo
public class ServerPlayHandlerMixin {
    @Inject(
            method = "handleShoot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getItemEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/item/ItemStack;)I"
            ),
            cancellable = true)
    private static void passiveintegration$handleReclaimed(C2SMessageShoot message, ServerPlayer player, CallbackInfo ci) {
        if (!CgmConfig.ENABLE_ORIGINAL_DECLAIM.get()) {
            var item = player.getMainHandItem().getItem();
            CompoundTag tag = player.getMainHandItem().getOrCreateTag();
            float chance = 0.0F;

            for (ArrowRetrievalBonus bonus : BonusHandler.getSkillBonuses(player, ArrowRetrievalBonus.class)) {
                chance += bonus.getChance();
            }
            chance = Mth.clamp(chance, 0.0F, 1.0F);
            if (!(player.getRandom().nextFloat() >= chance) && !CgmConfig.ENABLE_RETRIEVAL_BONUS_ON_DECLAIM.get()) {
                tag.putInt("AmmoCount", Math.max(0, tag.getInt("AmmoCount") - 1));
            }
            player.awardStat(Stats.ITEM_USED.get(item));
            player.level().playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, 0.8F);
            ci.cancel();
        }
    }
}