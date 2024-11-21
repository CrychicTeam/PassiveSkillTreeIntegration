package org.crychicteam.passiveintegration;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;

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
}
