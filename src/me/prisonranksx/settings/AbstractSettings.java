package me.prisonranksx.settings;

import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.configuration.ConfigurationSection;

import me.prisonranksx.managers.ConfigManager;
import me.prisonranksx.managers.StringManager;

public abstract class AbstractSettings {

	private String parentSectionName;
	private ConfigurationSection parentSection;
	private String configName;

	public AbstractSettings(String parentSectionName) {
		this.parentSectionName = parentSectionName;
		this.parentSection = ConfigManager.getConfig().getConfigurationSection(parentSectionName);
	}

	public AbstractSettings(String parentSectionName, String configName) {
		this.parentSectionName = parentSectionName;
		this.parentSection = ConfigManager.getConfig(configName).getConfigurationSection(parentSectionName);
		this.configName = configName;
	}

	protected boolean getBoolean(String configNode) {
		return parentSection.getBoolean(configNode);
	}

	protected String getString(String configNode) {
		String string = parentSection.getString(configNode);
		return configNode == null || string == null || string.isEmpty() ? null : string;
	}

	protected String getString(String configNode, boolean parseColors) {
		return parseColors ? parseLines(StringManager.parseColorsAndSymbols(getString(configNode)))
				: getString(configNode);
	}

	private String parseLines(String string) {
		return string == null || string.isEmpty() ? null : string.replace("\\n", "\n");
	}

	protected int getInt(String configNode) {
		return parentSection.getInt(configNode);
	}

	protected double getDouble(String configNode) {
		return parentSection.getDouble(configNode);
	}

	protected float getFloat(String configNode) {
		return (float) getDouble(configNode);
	}

	@Nullable
	protected List<String> getStringList(String configNode) {
		return !parentSection.contains(configNode) ? null : parentSection.getStringList(configNode);
	}

	@Nullable
	protected List<String> getStringList(String configNode, boolean parseColors) {
		return !parentSection.contains(configNode) || parentSection.getStringList(configNode).isEmpty() ? null
				: parseColors ? StringManager.parseColorsAndSymbols(parentSection.getStringList(configNode))
				: parentSection.getStringList(configNode);
	}

	public void refreshParentSection() {
		this.parentSection = configName == null ? ConfigManager.getConfig().getConfigurationSection(parentSectionName)
				: ConfigManager.getConfig(configName).getConfigurationSection(parentSectionName);
	}

	public ConfigurationSection getParentSection() {
		return parentSection;
	}

	public String getParentSectionName() {
		return parentSectionName;
	}

	/**
	 * Used for initialization and reloading settings values
	 */
	public abstract void setup();

}
