package org.crychicteam.passiveintegration;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.crychicteam.passiveintegration.config.CgmConfig;

import java.util.logging.Logger;

@Mod(PassiveIntegration.MOD_ID)
public class PassiveIntegration
{
	public static final String MOD_ID = "passiveintegration";
	public static final Logger LOGGER = Logger.getLogger(MOD_ID);
	public static ResourceLocation id(String path)
	{
		return new ResourceLocation(MOD_ID, path);
	}

	public PassiveIntegration() {
		ModLoadingContext cxt = ModLoadingContext.get();
		registerConfig(cxt, "cgm", CgmConfig.SPEC);
	}

	private void registerConfig(ModLoadingContext cxt, String mod, ForgeConfigSpec config) {
		if (isLoaded(mod)) {
			cxt.registerConfig(ModConfig.Type.COMMON, config, "passive" + mod + "-integration.toml");
		}
	}

	public static Boolean isLoaded(String mod) {
        return ModList.get().isLoaded(mod);
	}
}
