package net.jahus.classicbadomen.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.jahus.classicbadomen.ClassicBadOmen;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;

public final class ModConfig {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private static final Set<String> VALID_DROP = Set.of("otherwise", "always", "never");
	private static final Set<String> VALID_LEVELS = Set.of("stack", "drop");

	private static Path path;
	private static Data data = new Data();

	public static final class Data {
		/** Master switch. */
		public boolean enabled = true;
		/**
		 * Probability [0.0, 1.0) to apply Bad Omen when vanilla would drop an ominous bottle
		 * from a raider. 1.0 = always.
		 */
		public double bad_omen_probability = 1.0;
		/**
		 * Bottle policy:
		 * <ul>
		 *   <li>{@code "otherwise"}: classic; suppress bottle only when Bad Omen is applied</li>
		 *   <li>{@code "always"}: always keep the bottle (effect + bottle on success)</li>
		 *   <li>{@code "never"}: never drop the bottle from this path (even on probability fail).
		 *       Warning: removes a source of ominous bottles needed for Trial Chambers.</li>
		 * </ul>
		 */
		public String drop_ominous_bottle = "otherwise";
		/**
		 * How to choose the effect amplifier:
		 * <ul>
		 *   <li>{@code "stack"}: old style; +1 per kill (max V), independent of bottle level</li>
		 *   <li>{@code "drop"}: use the same amplifier as the ominous bottle vanilla rolled</li>
		 * </ul>
		 */
		public String levels = "stack";
		/** Bad Omen duration in ticks. Vanilla bottle uses 120000 (100 minutes). */
		public int duration_ticks = 120000;
	}

	private ModConfig() {}

	public static void load(Path configDir) {
		path = configDir.resolve("classic-bad-omen.json");
		if (Files.isRegularFile(path)) {
			try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
				Data loaded = GSON.fromJson(reader, Data.class);
				if (loaded != null) {
					data = loaded;
				}
			} catch (Exception e) {
				ClassicBadOmen.LOGGER.error("Failed to read config, using defaults", e);
				data = new Data();
			}
		}
		normalize();
		save();
	}

	private static void normalize() {
		if (data.bad_omen_probability < 0.0D) {
			data.bad_omen_probability = 0.0D;
		} else if (data.bad_omen_probability > 1.0D) {
			data.bad_omen_probability = 1.0D;
		}
		if (data.duration_ticks < 1) {
			data.duration_ticks = 120000;
		}

		if (data.drop_ominous_bottle == null || data.drop_ominous_bottle.isBlank()) {
			data.drop_ominous_bottle = "otherwise";
		} else {
			data.drop_ominous_bottle = data.drop_ominous_bottle.trim().toLowerCase(Locale.ROOT);
			if (!VALID_DROP.contains(data.drop_ominous_bottle)) {
				ClassicBadOmen.LOGGER.warn(
						"Unknown drop_ominous_bottle='{}', falling back to 'otherwise'",
						data.drop_ominous_bottle
				);
				data.drop_ominous_bottle = "otherwise";
			}
		}

		if (data.levels == null || data.levels.isBlank()) {
			data.levels = "stack";
		} else {
			data.levels = data.levels.trim().toLowerCase(Locale.ROOT);
			if (!VALID_LEVELS.contains(data.levels)) {
				ClassicBadOmen.LOGGER.warn(
						"Unknown levels='{}', falling back to 'stack'",
						data.levels
				);
				data.levels = "stack";
			}
		}
	}

	public static void save() {
		if (path == null) {
			return;
		}
		try {
			Files.createDirectories(path.getParent());
			try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
				GSON.toJson(data, writer);
			}
		} catch (IOException e) {
			ClassicBadOmen.LOGGER.error("Failed to write config", e);
		}
	}

	public static boolean enabled() {
		return data.enabled;
	}

	public static double badOmenProbability() {
		return data.bad_omen_probability;
	}

	public static String dropOminousBottle() {
		return data.drop_ominous_bottle;
	}

	/** {@code true} when the bottle should be suppressed for this drop. */
	public static boolean shouldSuppressBottle(boolean effectApplied) {
		return switch (data.drop_ominous_bottle) {
			case "never" -> true;
			case "always" -> false;
			default -> effectApplied;  // "otherwise"
		};
	}

	/** {@code true} when value is {@code "drop"}. */
	public static boolean levelsFromDrop() {
		return "drop".equals(data.levels);
	}

	public static String levelsMode() {
		return data.levels;
	}

	public static int durationTicks() {
		return data.duration_ticks;
	}
}
