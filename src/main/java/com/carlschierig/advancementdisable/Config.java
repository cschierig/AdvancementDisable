package com.carlschierig.advancementdisable;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.List;

@Mod.EventBusSubscriber(modid = AdvancementDisableMod.MODID,
	bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {

	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	private static final ForgeConfigSpec.ConfigValue<List<? extends String>> NAMESPACE_STRINGS = BUILDER
		.comment("A list of namespaces to remove advancements.")
		.defineListAllowEmpty("namespaces", List.of("minecraft"), e -> true);

	static final ForgeConfigSpec SPEC = BUILDER.build();

	public static List<String> namespaces;

	@SubscribeEvent
	static void onLoad(final ModConfigEvent event)
	{
		namespaces = NAMESPACE_STRINGS.get().stream().map(String::valueOf).toList();
	}
}
