package net.jahus.classicbadomen;

import net.jahus.classicbadomen.config.ModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(ClassicBadOmen.MOD_ID)
public final class ClassicBadOmen {

	public static final String MOD_ID = "classicbadomen";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public ClassicBadOmen() {
		ModConfig.load(FMLPaths.CONFIGDIR.get());
		LOGGER.info(
				"Classic Bad Omen (Forge) loaded (enabled={}, probability={}, drop_bottle={}, levels={}, duration={})",
				ModConfig.enabled(),
				ModConfig.badOmenProbability(),
				ModConfig.dropOminousBottle(),
				ModConfig.levelsMode(),
				ModConfig.durationTicks()
		);
	}
}
