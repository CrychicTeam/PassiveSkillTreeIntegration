package org.crychicteam.passiveintegration;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;

@Mod(PassiveIntegration.MOD_ID)
public class PassiveIntegration
{
	public static final String MOD_ID = "passiveintegration";
	public static ResourceLocation id(String path)
	{
		return new ResourceLocation(MOD_ID, path);
	}
}
