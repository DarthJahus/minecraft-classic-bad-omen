package net.jahus.classicbadomen;

import net.jahus.classicbadomen.config.ModConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(ClassicBadOmen.MOD_ID)
public final class ClassicBadOmen {

	public static final String MOD_ID = "classicbadomen";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public ClassicBadOmen(IEventBus modEventBus) {
		ModConfig.load(FMLPaths.CONFIGDIR.get());
		LOGGER.info(
				"Classic Bad Omen (NeoForge) loaded (enabled={}, probability={}, drop_bottle={}, levels={}, duration={})",
				ModConfig.enabled(),
				ModConfig.badOmenProbability(),
				ModConfig.dropOminousBottle(),
				ModConfig.levelsMode(),
				ModConfig.durationTicks()
		);
	}
}
