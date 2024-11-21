package org.crychicteam.passiveintegration.util;

import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.effect.SkillBonusEffect;
import daripher.skilltree.entity.player.PlayerHelper;
import daripher.skilltree.item.ItemBonusProvider;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.player.CritChanceBonus;
import daripher.skilltree.skill.bonus.player.CritDamageBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class BonusHandler {
    private final BonusHandler INSTANCE = new BonusHandler();

    private BonusHandler () {}

    public BonusHandler getINSTANCE() {
        return INSTANCE;
    }

    public static <T> List<T> getSkillBonuses(@Nonnull Player player, Class<T> type) {
        if (!PlayerSkillsProvider.hasSkills(player)) {
            return List.of();
        } else {
            List<T> bonuses = new ArrayList();
            bonuses.addAll(getPlayerBonuses(player, type));
            bonuses.addAll(getEffectBonuses(player, type));
            bonuses.addAll(getEquipmentBonuses(player, type));
            return bonuses;
        }
    }

    public static <T> List<T> getPlayerBonuses(Player player, Class<T> type) {
        List<T> list = new ArrayList();
        Iterator var3 = PlayerSkillsProvider.get(player).getPlayerSkills().iterator();

        while(var3.hasNext()) {
            PassiveSkill skill = (PassiveSkill)var3.next();
            List<SkillBonus<?>> bonuses = skill.getBonuses();
            Iterator var6 = bonuses.iterator();

            while(var6.hasNext()) {
                SkillBonus<?> skillBonus = (SkillBonus)var6.next();
                if (type.isInstance(skillBonus)) {
                    list.add(type.cast(skillBonus));
                }
            }
        }

        return list;
    }

    public static <T> List<T> getEffectBonuses(Player player, Class<T> type) {
        List<T> bonuses = new ArrayList();
        Iterator var3 = player.getActiveEffects().iterator();

        while(var3.hasNext()) {
            MobEffectInstance e = (MobEffectInstance)var3.next();
            MobEffect var6 = e.getEffect();
            if (var6 instanceof SkillBonusEffect skillEffect) {
                SkillBonus<?> bonus = skillEffect.getBonus().copy();
                if (type.isInstance(bonus)) {
                    bonus = bonus.copy().multiply((double)e.getAmplifier());
                    bonuses.add(type.cast(bonus));
                }
            }
        }

        return bonuses;
    }

    public static <T> List<T> getEquipmentBonuses(Player player, Class<T> type) {
        return PlayerHelper.getAllEquipment(player).map((s) -> {
            return getItemBonuses(s, type);
        }).flatMap(Collection::stream).toList();
    }

    public static <T> List<T> getItemBonuses(ItemStack stack, Class<T> type) {
        List<ItemBonus<?>> itemBonuses = new ArrayList();
        Item var4 = stack.getItem();
        if (var4 instanceof ItemBonusProvider provider) {
            itemBonuses.addAll(provider.getItemBonuses());
        }

        itemBonuses.addAll(ItemHelper.getItemBonuses(stack));
        List<T> bonuses = new ArrayList();
        Iterator var9 = itemBonuses.iterator();

        while(var9.hasNext()) {
            ItemBonus<?> itemBonus = (ItemBonus)var9.next();
            if (itemBonus instanceof ItemSkillBonus skillBonus) {
                SkillBonus<?> bonus = skillBonus.getBonus();
                if (type.isInstance(bonus)) {
                    bonuses.add(type.cast(bonus));
                }
            }
        }

        return bonuses;
    }

    public static float getCritChance(ServerPlayer player, DamageSource source, LivingEntity target) {
        float critChance = 0.0F;

        CritChanceBonus bonus;
        for(Iterator var4 = getSkillBonuses(player, CritChanceBonus.class).iterator(); var4.hasNext(); critChance += bonus.getChanceBonus(source, player, target)) {
            bonus = (CritChanceBonus)var4.next();
        }

        return critChance;
    }

    public static float getCritDamageMultiplier(ServerPlayer player, DamageSource source, LivingEntity target) {
        float multiplier = 0.0F;

        CritDamageBonus bonus;
        for(Iterator var4 = getSkillBonuses(player, CritDamageBonus.class).iterator(); var4.hasNext(); multiplier += bonus.getDamageBonus(source, player, target)) {
            bonus = (CritDamageBonus)var4.next();
        }

        return multiplier;
    }
}
