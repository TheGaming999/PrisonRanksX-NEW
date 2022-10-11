package me.prisonranksx.utilities;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Color;

public class FireworkColor {

	private static final Map<String, Color> COLORS = new HashMap<>();

	static {
		for (Field field : Color.class.getFields()) {
			if (field.getType().equals(Color.class)) {
				String colorName = field.getName();
				try {
					registerColor(colorName, (Color) field.get(Color.class));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void registerColor(String name, Color color) {
		COLORS.put(name, color);
	}

	public static void registerColor(String name, int r, int g, int b) {
		COLORS.put(name, Color.fromRGB(r, g, b));
	}

	@Nullable
	public static Color getColorExact(@Nullable String name) {
		return COLORS.get(name);
	}

	@Nullable
	public static Color getColor(@Nonnull String name) {
		return COLORS.get(name.toUpperCase());
	}

	@Nonnull
	public static Color parseColor(@Nonnull String colorString) {
		Color color = COLORS.get(colorString.toUpperCase());
		if (color != null) return color;
		if (colorString.indexOf(",") == -1) return COLORS.get("WHITE");
		String rgbColorString = colorString.replace(" ,", ",");
		String[] rgbSplit = rgbColorString.split(",");
		if (rgbSplit.length != 3) return COLORS.get("WHITE");
		return Color.fromRGB(Integer.parseInt(rgbSplit[0]), Integer.parseInt(rgbSplit[1]),
				Integer.parseInt(rgbSplit[2]));
	}

}
