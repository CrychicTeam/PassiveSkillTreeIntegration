package org.crychicteam.passiveintegration.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CgmConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLE_CRIT_INTEGRATION;

    public static final ForgeConfigSpec.BooleanValue ENABLE_AMMO_RETRIEVAL;
    public static final ForgeConfigSpec.DoubleValue RECLAIMED_ENCHANTMENT_BONUS;
    public static final ForgeConfigSpec.BooleanValue ENABLE_ORIGINAL_DECLAIM;
    public static final ForgeConfigSpec.BooleanValue ENABLE_RETRIEVAL_BONUS_ON_DECLAIM;
    public static final ForgeConfigSpec.BooleanValue ENABLE_RELOAD_INTERVAL_BONUS;

    static {
        BUILDER.push("Cgm-Unofficial Integration Configuration");

        ENABLE_CRIT_INTEGRATION = BUILDER
                .comment("Replace Cgm-Unofficial critical hit system with Passive Skill Tree's.")
                .define("enableCriticalSystem", true);

        ENABLE_AMMO_RETRIEVAL = BUILDER
                .comment("Enable Ammo Retrieval bonus")
                .define("enableAmmoRetrievalBonus", true);

        ENABLE_ORIGINAL_DECLAIM = BUILDER
                .comment("Enable original Reclaimed logic")
                .comment("Would be useful when enable Ammo Retrieval Bonus to avoid similar behavior.")
                .define("enableOriginalReclaimed", false);

        ENABLE_RETRIEVAL_BONUS_ON_DECLAIM = BUILDER
                .comment("Replace original Reclaimed logic with Ammo Retrieval bonus")
                .comment("When disable OriginalReclaimed, the bonus of retrieval chance Reclaimed enchantment provides.")
                .define("enableRetrievalBonusOnDeath", true);

        RECLAIMED_ENCHANTMENT_BONUS = BUILDER
                .comment("The bonus value for Reclaimed enchantment")
                .comment("When enable the Ammo Retrieval, the bonus of retrieval chance reclaimed enchantment provides.")
                .defineInRange("declaimedEnchantmentBonus", 1.5F, 0.0D, 1);

        ENABLE_RELOAD_INTERVAL_BONUS = BUILDER
                .comment("Enable Reload Interval bonus")
                .define("enableReloadIntervalBonus", true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
