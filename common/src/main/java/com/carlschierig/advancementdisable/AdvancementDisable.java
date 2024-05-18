package com.carlschierig.advancementdisable;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.FileConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AdvancementDisable {
	public static final String MODID = "advancementdisable";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static final List<String> DISABLED_MODS;

	private static final String DISABLED_KEY = "disabledMods";

	static {
		var spec = new ConfigSpec();
		spec.defineList(DISABLED_KEY, new ArrayList<String>() , (object) -> object instanceof String);

		var config = FileConfig.of("config/" + MODID + ".toml");
		config.load();

		if (!spec.isCorrect(config)) {
			LOGGER.warn("Failed to load config from {}, correcting.", MODID);
			spec.correct(config);
			config.save();
		}

		DISABLED_MODS = config.get(DISABLED_KEY);
		config.close();
	}
}
