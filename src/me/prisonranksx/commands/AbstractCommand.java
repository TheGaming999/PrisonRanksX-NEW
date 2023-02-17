package me.prisonranksx.commands;

import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.ConfigurationSection;

import me.prisonranksx.managers.ConfigManager;

public abstract class AbstractCommand extends BukkitCommand {

	private String sectionName;
	private ConfigurationSection section;

	protected AbstractCommand(String name) {
		super(name);
		this.sectionName = name;
		this.section = ConfigManager.getCommandsConfig().getConfigurationSection("commands." + sectionName);
	}

	public ConfigurationSection getCommandSection() {
		return section;
	}

	public boolean register() {
		return CommandLoader.registerCommand(section.getString("plugin", "PrisonRanksX"), this);
	}

	public void unregister() {
		CommandLoader.unregisterCommand(this);
	}

}
