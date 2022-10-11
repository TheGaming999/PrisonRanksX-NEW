package me.prisonranksx.managers;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import me.prisonranksx.utilities.ConfigCreator;
import me.prisonranksx.utilities.StaticCache;

/**
 * 
 * <p>
 * Controls plugin's config files, reloads, and saves them.
 */
public class ConfigManager extends StaticCache {

	static {
		ConfigCreator.copyAndSaveDefaults(false, "config.yml", "messages.yml");
		ConfigCreator.copyAndSaveDefaults(true, "ranks.yml", "prestiges.yml", "rebirths.yml", "rankdata.yml",
				"prestigedata.yml", "rebirthdata.yml", "infinite_prestige.yml", "commands.yml", "guis.yml");
	}

	/**
	 * @return FileConfiguration of "config.yml"
	 */
	public static FileConfiguration getConfig() {
		return ConfigCreator.getConfig("config.yml");
	}

	/**
	 * @param configFileName with (.yml), example: "config.yml"
	 * @return FileConfiguration of "configFileName"
	 */
	public static FileConfiguration getConfig(String configFileName) {
		return ConfigCreator.getConfig(configFileName);
	}

	/**
	 * @return FileConfiguration of "ranks.yml"
	 */
	public static FileConfiguration getRanksConfig() {
		return ConfigCreator.getConfig("ranks.yml");
	}

	/**
	 * @return FileConfiguration of "prestiges.yml"
	 */
	public static FileConfiguration getPrestigesConfig() {
		return ConfigCreator.getConfig("prestiges.yml");
	}

	/**
	 * @return FileConfiguration of "rebirths.yml"
	 */
	public static FileConfiguration getRebirthsConfig() {
		return ConfigCreator.getConfig("rebirths.yml");
	}

	/**
	 * @return FileConfiguration of "rankdata.yml"
	 */
	public static FileConfiguration getRankDataConfig() {
		return ConfigCreator.getConfig("rankdata.yml");
	}

	/**
	 * @return FileConfiguration of "prestigedata.yml"
	 */
	public static FileConfiguration getPrestigeDataConfig() {
		return ConfigCreator.getConfig("prestigedata.yml");
	}

	/**
	 * @return FileConfiguration of "rebirthdata.yml"
	 */
	public static FileConfiguration getRebirthDataConfig() {
		return ConfigCreator.getConfig("rebirthdata.yml");
	}

	/**
	 * @return FileConfiguration of "messages.yml"
	 */
	public static FileConfiguration getMessagesConfig() {
		return ConfigCreator.getConfig("messages.yml");
	}

	/**
	 * @return FileConfiguration of "infinite_prestige.yml"
	 */
	public static FileConfiguration getInfinitePrestigeConfig() {
		return ConfigCreator.getConfig("infinite_prestige.yml");
	}

	/**
	 * @return FileConfiguration of "commands.yml"
	 */
	public static FileConfiguration getCommandsConfig() {
		return ConfigCreator.getConfig("commands.yml");
	}

	/**
	 * @return FileConfiguration of "guis.yml"
	 */
	public static FileConfiguration getGUIConfig() {
		return ConfigCreator.getConfig("guis.yml");
	}

	/**
	 * @param configYmlName config file name with the extension ("config.yml")
	 * @return reloaded config file, this also updates the config files that are
	 *         retrieved from the getters
	 */
	public static synchronized FileConfiguration reloadConfig(String configYmlName) {
		return ConfigCreator.reloadConfig(configYmlName);
	}

	/**
	 * @param configYmlName config file name with the extension ("config.yml")
	 * @return saved config file, this also updates the config files that are
	 *         retrieved from the getters
	 */
	public static synchronized FileConfiguration saveConfig(String configYmlName) {
		return ConfigCreator.saveConfig(configYmlName);
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public static <T> T getOrElse(ConfigurationSection configurationSection, String... fields) {
		for (String field : fields) {
			if (configurationSection.contains(field)) {
				if (configurationSection.isList(field) && !configurationSection.getStringList(field).isEmpty())
					return (T) configurationSection.getStringList(field);
				else if (configurationSection.isConfigurationSection(field))
					return (T) configurationSection.getConfigurationSection(field);
				else
					return (T) configurationSection.get(field, null);
			}
		}
		return null;
	}

	@Nonnull
	public static double getDoubleOrElse(ConfigurationSection configurationSection, String... fields) {
		for (String field : fields)
			if (configurationSection.isDouble(field)) return configurationSection.getDouble(field);
		return 0.0;
	}

	@Nonnull
	public static long getLongOrElse(ConfigurationSection configurationSection, String... fields) {
		for (String field : fields) if (configurationSection.isLong(field)) return configurationSection.getLong(field);
		return 0l;
	}

	@Nonnull
	public static boolean getBooleanOrElse(ConfigurationSection configurationSection, String... fields) {
		for (String field : fields)
			if (configurationSection.isBoolean(field)) return configurationSection.getBoolean(field);
		return false;
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public static <T> T getOrElse(ConfigurationSection configurationSection, Class<T> type, String... fields) {
		for (String field : fields) {
			if (configurationSection.contains(field)) {
				if (configurationSection.isList(field) && !configurationSection.getStringList(field).isEmpty())
					return (T) configurationSection.getStringList(field);
				else if (configurationSection.isConfigurationSection(field))
					return (T) configurationSection.getConfigurationSection(field);
				else
					return (T) configurationSection.get(field, null);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public static <T> List<T> getListOrElse(ConfigurationSection configurationSection, Class<T> type,
			String... fields) {
		for (String field : fields) {
			if (configurationSection.contains(field)) {
				if (configurationSection.isList(field) && !configurationSection.getStringList(field).isEmpty())
					return (List<T>) configurationSection.getList(field, null);
			}
		}
		return null;
	}

}
