package com.carlschierig.advancementdisable;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AdvancementDisableMod.MODID)
public class AdvancementDisableMod
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "advancementdisable";
    // Directly reference a slf4j logger

    public AdvancementDisableMod(FMLJavaModLoadingContext context)
    {
        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
