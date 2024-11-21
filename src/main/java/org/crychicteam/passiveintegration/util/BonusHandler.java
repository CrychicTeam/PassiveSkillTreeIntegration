package org.crychicteam.passiveintegration.util;

import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.effect.SkillBonusEffect;
import daripher.skilltree.entity.player.PlayerHelper;
import daripher.skilltree.item.ItemBonusProvider;
import daripher.skilltree.item.ItemHelper;
import daripher.skilltree.mixin.AbstractArrowAccessor;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemSkillBonus;
import daripher.skilltree.skill.bonus.player.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * This class extract all static methods within {@link daripher.skilltree.skill.bonus.SkillBonusHandler}
 * </p>
 * To help with Bonus actions.
 * @author M1hono
 */
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

    public static float getDamageBonus(Player player, DamageSource damageSource, LivingEntity target, AttributeModifier.Operation operation) {
        float amount = 0.0F;

        DamageBonus bonus;
        for(Iterator var5 = getSkillBonuses(player, DamageBonus.class).iterator(); var5.hasNext(); amount += bonus.getDamageBonus(operation, damageSource, player, target)) {
            bonus = (DamageBonus)var5.next();
        }

        return amount;
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
    public float getJumpHeightMultiplier(Player player) {
        float multiplier = 1.0F;
        for (JumpHeightBonus bonus : getSkillBonuses(player, JumpHeightBonus.class)) {
            multiplier += bonus.getJumpHeightMultiplier(player);
        }
        return multiplier;
    }

    public void amplifyEnchantments(List<EnchantmentInstance> enchantments, RandomSource random, Player player) {
        enchantments.replaceAll(enchantment -> amplifyEnchantment(enchantment, random, player));
    }

    private EnchantmentInstance amplifyEnchantment(EnchantmentInstance enchantment, RandomSource random, Player player) {
        if (enchantment.enchantment.getMaxLevel() == 1) {
            return enchantment;
        }

        float amplificationChance = getAmplificationChance(enchantment, player);
        if (amplificationChance == 0.0F) {
            return enchantment;
        }

        int levelBonus = (int) amplificationChance;
        amplificationChance -= levelBonus;
        int enchantmentLevel = enchantment.level + levelBonus;

        if (random.nextFloat() < amplificationChance) {
            enchantmentLevel++;
        }

        return new EnchantmentInstance(enchantment.enchantment, enchantmentLevel);
    }

    public int adjustEnchantmentCost(int cost, @Nonnull Player player) {
        return (int) Math.max(1.0, cost * getEnchantmentCostMultiplier(player));
    }

    public float getFreeEnchantmentChance(@Nonnull Player player) {
        float chance = 0.0F;
        for (FreeEnchantmentBonus bonus : getSkillBonuses(player, FreeEnchantmentBonus.class)) {
            chance += bonus.getChance();
        }
        return chance;
    }

    private double getEnchantmentCostMultiplier(@Nonnull Player player) {
        float multiplier = 1.0F;
        for (EnchantmentRequirementBonus bonus : getSkillBonuses(player, EnchantmentRequirementBonus.class)) {
            multiplier += bonus.getMultiplier();
        }
        return multiplier;
    }

    private float getAmplificationChance(EnchantmentInstance enchantment, Player player) {
        float chance = 0.0F;
        for (EnchantmentAmplificationBonus bonus : getSkillBonuses(player, EnchantmentAmplificationBonus.class)) {
            if (bonus.getCondition().met(enchantment.enchantment.category)) {
                chance += bonus.getChance();
            }
        }
        return chance;
    }

    public float getHealthReservation(Player player) {
        float reservation = 0.0F;
        for (HealthReservationBonus bonus : getSkillBonuses(player, HealthReservationBonus.class)) {
            reservation += bonus.getAmount(player);
        }
        return reservation;
    }

    public void addAttributeModifiers(BiConsumer<Attribute, AttributeModifier> addFunction, ItemStack stack) {
        for (ItemBonus<?> itemBonus : ItemHelper.getItemBonuses(stack)) {
            if (itemBonus instanceof ItemSkillBonus itemSkillBonus) {
                SkillBonus<?> bonus = itemSkillBonus.getBonus();
                if (bonus instanceof AttributeBonus attributeBonus) {
                    if (!attributeBonus.hasMultiplier() && !attributeBonus.hasCondition()) {
                        addFunction.accept(attributeBonus.getAttribute(), attributeBonus.getModifier());
                    }
                }
            }
        }
    }

    public void handleArrowRetrieval(LivingEntity target, AbstractArrow arrow, Player player) {
        AbstractArrowAccessor arrowAccessor = (AbstractArrowAccessor) arrow;
        ItemStack arrowStack = arrowAccessor.invokeGetPickupItem();
        if (arrowStack == null) return;

        float retrievalChance = 0.0F;
        for (ArrowRetrievalBonus bonus : getSkillBonuses(player, ArrowRetrievalBonus.class)) {
            retrievalChance += bonus.getChance();
        }

        if (player.getRandom().nextFloat() < retrievalChance) {
            CompoundTag targetData = target.getPersistentData();
            ListTag stuckArrowsTag = targetData.getList("StuckArrows", new CompoundTag().getId());
            stuckArrowsTag.add(arrowStack.save(new CompoundTag()));
            targetData.put("StuckArrows", stuckArrowsTag);
        }
    }

    public List<ItemEntity> getDrops(LivingDropsEvent event) {
        List<ItemEntity> drops = new ArrayList<>();
        for (ItemEntity itemEntity : event.getDrops()) {
            drops.add(itemEntity.copy());
        }
        return drops;
    }

    public float getLootMultiplier(Player player, LootDuplicationBonus.LootType lootType) {
        Map<Float, Float> multipliers = getLootMultipliers(player, lootType);
        float multiplier = 0.0F;

        for (Map.Entry<Float, Float> entry : multipliers.entrySet()) {
            float chance = entry.getValue();
            while (chance > 1.0F) {
                multiplier += entry.getKey();
                chance--;
            }
            if (player.getRandom().nextFloat() < chance) {
                multiplier += entry.getKey();
            }
        }
        return multiplier;
    }

    @Nonnull
    private Map<Float, Float> getLootMultipliers(Player player, LootDuplicationBonus.LootType lootType) {
        Map<Float, Float> multipliers = new HashMap<>();
        for (LootDuplicationBonus bonus : getSkillBonuses(player, LootDuplicationBonus.class)) {
            if (bonus.getLootType() == lootType) {
                float chance = bonus.getChance() + multipliers.getOrDefault(bonus.getMultiplier(), 0.0F);
                multipliers.put(bonus.getMultiplier(), chance);
            }
        }
        return multipliers;
    }

    public float getExperienceMultiplier(Player player, GainedExperienceBonus.ExperienceSource source) {
        float multiplier = 0.0F;
        for (GainedExperienceBonus bonus : getSkillBonuses(player, GainedExperienceBonus.class)) {
            if (bonus.getSource() == source) {
                multiplier += bonus.getMultiplier();
            }
        }
        return multiplier;
    }
}
