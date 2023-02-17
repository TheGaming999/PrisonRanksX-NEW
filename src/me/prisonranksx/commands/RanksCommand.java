package me.prisonranksx.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;

public class RanksCommand extends AbstractCommand {

	private PrisonRanksX plugin;

	public static boolean isEnabled() {
		return CommandSetting.getSetting("ranks", "enable");
	}

	public RanksCommand(PrisonRanksX plugin) {
		super(CommandSetting.getStringSetting("ranks", "name", "ranks"));
		this.plugin = plugin;
		setLabel(getCommandSection().getString("label", "ranks"));
		setDescription(getCommandSection().getString("description"));
		setUsage(getCommandSection().getString("usage"));
		setPermission(getCommandSection().getString("permission"));
		setPermissionMessage(getCommandSection().getString("permission-message"));
		setAliases(getCommandSection().getStringList("aliases"));
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if (!testPermission(sender)) return true;
		if (!(sender instanceof Player)) {

			return true;
		}
		if (args.length == 0) {
			plugin.getRanksTextList().sendPagedList(sender, "1");
		} else {
			plugin.getRanksTextList().sendPagedList(sender, args[0]);
		}
		return true;
	}
}
