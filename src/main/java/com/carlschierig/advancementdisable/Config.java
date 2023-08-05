package com.carlschierig.advancementdisable;

import org.quiltmc.config.api.WrappedConfig;
import org.quiltmc.config.api.values.ValueList;
import org.quiltmc.loader.api.config.QuiltConfig;

public class Config extends WrappedConfig {
	public static Config INSTANCE;

	static {
		INSTANCE = QuiltConfig.create("", "advancementdisable", Config.class);
	}

	private final ValueList<String> disabledMods = ValueList.create("");

	public ValueList<String> getDisabledMods() {
		return disabledMods;
	}
}
