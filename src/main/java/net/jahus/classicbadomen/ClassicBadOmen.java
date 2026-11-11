package net.jahus.classicbadomen;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.jahus.classicbadomen.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ClassicBadOmen implements ModInitializer {

	public static final String MOD_ID = "classicbadomen";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModConfig.load(FabricLoader.getInstance().getConfigDir());
		LOGGER.info(
				"Classic Bad Omen loaded (enabled={}, probability={}, drop_bottle={}, levels={}, duration={})",
				ModConfig.enabled(),
				ModConfig.badOmenProbability(),
				ModConfig.dropOminousBottle(),
				ModConfig.levelsMode(),
				ModConfig.durationTicks()
		);
	}
}
