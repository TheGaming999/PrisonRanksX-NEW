package me.prisonranksx.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import me.prisonranksx.components.ActionBarComponent;
import me.prisonranksx.components.CommandsComponent;
import me.prisonranksx.components.FireworkComponent;
import me.prisonranksx.components.PermissionsComponent;
import me.prisonranksx.components.RandomCommandsComponent;
import me.prisonranksx.components.RequirementsComponent;
import me.prisonranksx.holders.Prestige;
import me.prisonranksx.managers.ConfigManager;
import me.prisonranksx.managers.StringManager;

public class PrestigeStorage {

	private static final PrestigeStorageHandler PRESTIGE_STORAGE_HANDLER = new PrestigeStorageHandler();

	public static PrestigeStorageHandler getHandler() {
		return PRESTIGE_STORAGE_HANDLER;
	}

	public static void loadPrestiges() {
		PRESTIGE_STORAGE_HANDLER.loadPrestiges();
	}

	public static PrestigeStorageHandler init(boolean infinite) {
		PRESTIGE_STORAGE_HANDLER.create(infinite);
		return PRESTIGE_STORAGE_HANDLER;
	}

	public static PrestigeStorageHandler initAndLoad(boolean infinite) {
		init(infinite).loadPrestiges();
		return PRESTIGE_STORAGE_HANDLER;
	}

	public static boolean isCreated() {
		return PRESTIGE_STORAGE_HANDLER.isCreated();
	}

	public static boolean prestigeExists(String name) {
		return PRESTIGE_STORAGE_HANDLER.prestigeExists(name);
	}

	public static boolean prestigeExists(long number) {
		return PRESTIGE_STORAGE_HANDLER.prestigeExists(number);
	}

	public static Prestige getPrestige(String name) {
		return PRESTIGE_STORAGE_HANDLER.getPrestige(name);
	}

	public static Prestige getPrestige(long number) {
		return PRESTIGE_STORAGE_HANDLER.getPrestige(number);
	}

	public static String getFirstPrestigeName() {
		return PRESTIGE_STORAGE_HANDLER.getFirstPrestigeName();
	}

	public static long getFirstPrestigeAsInt() {
		return PRESTIGE_STORAGE_HANDLER.getFirstPrestigeAsInt();
	}

	public static String getLastPrestigeName() {
		return PRESTIGE_STORAGE_HANDLER.getLastPrestigeName();
	}

	public static long getLastPrestigeAsInt() {
		return PRESTIGE_STORAGE_HANDLER.getLastPrestigeAsInt();
	}

	/**
	 * 
	 * @return a hash set of all registered prestiges names, or null if
	 *         infinite prestige is enabled
	 */
	public static Set<String> getPrestigeNames() {
		return PRESTIGE_STORAGE_HANDLER.getPrestigeNames();
	}

	/**
	 * 
	 * @return a hash set of all registered prestiges, or null if infinite
	 *         prestige is enabled
	 */
	public static Collection<Prestige> getPrestiges() {
		return PRESTIGE_STORAGE_HANDLER.getPrestiges();
	}

	public static String matchPrestigeName(String name) {
		return PRESTIGE_STORAGE_HANDLER.matchPrestigeName(name);
	}

	public static Prestige matchPrestige(String name) {
		return PRESTIGE_STORAGE_HANDLER.matchPrestige(name);
	}

	public static class PrestigeStorageHandler {

		private IPrestigeStorage prestigeStorage;

		public void create(boolean infinite) {
			prestigeStorage = infinite ? new InfinitePrestigeStorage() : new RegularPrestigeStorage();
		}

		public boolean isCreated() {
			return prestigeStorage != null;
		}

		public void loadPrestiges() {
			prestigeStorage.loadPrestiges();
		}

		public boolean prestigeExists(String name) {
			return prestigeStorage.prestigeExists(name);
		}

		public boolean prestigeExists(long number) {
			return prestigeStorage.prestigeExists(number);
		}

		public Prestige getPrestige(String name) {
			return prestigeStorage.getPrestige(name);
		}

		public Prestige getPrestige(long number) {
			return prestigeStorage.getPrestige(number);
		}

		public String getFirstPrestigeName() {
			return prestigeStorage.getFirstPrestigeName();
		}

		public long getFirstPrestigeAsInt() {
			return prestigeStorage.getFirstPrestigeAsInt();
		}

		public String getLastPrestigeName() {
			return prestigeStorage.getLastPrestigeName();
		}

		public long getLastPrestigeAsInt() {
			return prestigeStorage.getLastPrestigeAsInt();
		}

		public Set<String> getPrestigeNames() {
			return prestigeStorage.getPrestigeNames();
		}

		public Collection<Prestige> getPrestiges() {
			return prestigeStorage.getPrestiges();
		}

		public String matchPrestigeName(String name) {
			return prestigeStorage.matchPrestigeName(name);
		}

		public Prestige matchPrestige(String name) {
			return prestigeStorage.matchPrestige(name);
		}

	}

	private static interface IPrestigeStorage {

		void loadPrestiges();

		boolean prestigeExists(String name);

		boolean prestigeExists(long number);

		Prestige getPrestige(String name);

		Prestige getPrestige(long number);

		String getFirstPrestigeName();

		long getFirstPrestigeAsInt();

		String getLastPrestigeName();

		long getLastPrestigeAsInt();

		Set<String> getPrestigeNames();

		Collection<Prestige> getPrestiges();

		String matchPrestigeName(String name);

		Prestige matchPrestige(String name);

	}

	private static class RegularPrestigeStorage implements IPrestigeStorage {

		private Map<String, Prestige> prestiges = new HashMap<>();
		private Map<String, String> alternativeNames = new HashMap<>();
		private List<String> prestigeNames = new ArrayList<>();
		private String firstPrestigeName;
		private String lastPrestigeName;
		private long lastPrestigeNumber;

		@SuppressWarnings("unchecked")
		@Override
		public void loadPrestiges() {
			prestiges.clear();
			alternativeNames.clear();
			prestigeNames.clear();
			firstPrestigeName = null;
			lastPrestigeName = null;
			lastPrestigeNumber = 0;
			FileConfiguration prestigesConfig = ConfigManager.getPrestigesConfig();
			ConfigurationSection prestigeSection = prestigesConfig.getConfigurationSection("Prestiges");
			for (String prestigeName : prestigeSection.getKeys(false)) {
				ConfigurationSection current = prestigeSection.getConfigurationSection(prestigeName);
				Prestige prestige = new Prestige(prestigeName,
						StringManager.parseColorsAndSymbols(
								ConfigManager.getOrElse(current, String.class, "display-name", "display", "prefix")),
						ConfigManager.getOrElse(current, "next-prestige", "nextprestige"),
						ConfigManager.getDoubleOrElse(current, "cost", "price"),
						StringManager.parseColorsAndSymbols(prestigeSection.getStringList("broadcast")),
						StringManager.parseColorsAndSymbols(
								ConfigManager.getOrElse(current, List.class, "message", "msg", "messages")),
						CommandsComponent.parseCommands(
								ConfigManager.getOrElse(current, "commands", "executecmds", "command", "cmd")),
						RequirementsComponent.parseRequirements(
								ConfigManager.getOrElse(current, "requirements", "requirement", "require", "requires")),
						ActionBarComponent.parseActionBar(ConfigManager.getOrElse(current, "action-bar", "actionbar")),
						PermissionsComponent.parsePermissions(
								ConfigManager.getOrElse(current, "add-permissions", "addpermission", "add-permission",
										"addperm", "add-perm", "add-perms"),
								ConfigManager.getOrElse(current, "delete-permissions", "delpermission",
										"del-permission", "delete-permission", "remove-permissions",
										"remove-permission", "del-perms")),
						FireworkComponent.parseFirework(ConfigManager.getOrElse(current, "firework", "firework-builder",
								"fireworks", "fire-work")),
						RandomCommandsComponent.parseRandomCommands(ConfigManager.getOrElse(current, "random-commands",
								"randomcmds", "random-command", "randomcmd", "random-cmds", "random-cmd")),
						StringManager.parseColorsAndSymbols(ConfigManager.getOrElse(current, List.class,
								"requirements-fail-message", "custom-requirement-message",
								"custom-requirements-message", "requirement-fail-message", "requirements-fail-messages",
								"requirements-message", "requirement-message")),
						ConfigManager.getDoubleOrElse(current, "cost-increase", "rankup_cost_increase_percentage",
								"cost-increase-percentage", "cost_increase", "rankup-cost-increase-percentage"));
				lastPrestigeNumber += 1;
				prestiges.put(prestigeName, prestige);
				alternativeNames.put(prestigeName.toLowerCase(), prestigeName);
				prestigeNames.add(prestigeName);
				if (firstPrestigeName == null) firstPrestigeName = prestigeName;
			}
			lastPrestigeName = prestigeNames.get((int) (lastPrestigeNumber - 1));
		}

		@Override
		public boolean prestigeExists(String name) {
			return getPrestige(name) != null;
		}

		@Override
		public boolean prestigeExists(long number) {
			return number > 0 && number <= lastPrestigeNumber;
		}

		@Override
		public Prestige getPrestige(String name) {
			return prestiges.get(name);
		}

		@Override
		public Prestige getPrestige(long number) {
			return getPrestige(prestigeNames.get((int) (number - 1)));
		}

		@Override
		public String getFirstPrestigeName() {
			return firstPrestigeName;
		}

		@Override
		public long getFirstPrestigeAsInt() {
			return prestigeNames.indexOf(firstPrestigeName) + 1;
		}

		@Override
		public String getLastPrestigeName() {
			return lastPrestigeName;
		}

		@Override
		public long getLastPrestigeAsInt() {
			return lastPrestigeNumber;
		}

		@Override
		public Set<String> getPrestigeNames() {
			return prestiges.keySet();
		}

		@Override
		public Collection<Prestige> getPrestiges() {
			return prestiges.values();
		}

		@Override
		public String matchPrestigeName(String name) {
			return alternativeNames.get(name.toLowerCase());
		}

		@Override
		public Prestige matchPrestige(String name) {
			return prestiges.get(matchPrestigeName(name));
		}

	}

	private static class InfinitePrestigeStorage implements IPrestigeStorage {

		private Map<Long, Prestige> prestiges = new HashMap<>();
		private final String firstPrestigeName = "1";
		private final long firstPrestigeNumber = 1;
		private String lastPrestigeName;
		private long lastPrestigeNumber;

		@SuppressWarnings("unchecked")
		@Override
		public void loadPrestiges() {
			prestiges.clear();
			lastPrestigeName = null;
			lastPrestigeNumber = 0;
			FileConfiguration infinitePrestigeConfig = ConfigManager.getInfinitePrestigeConfig();
			ConfigurationSection prestigeSection = infinitePrestigeConfig.getConfigurationSection("Prestiges-Settings");
			for (String prestigeName : prestigeSection.getKeys(false)) {
				ConfigurationSection current = prestigeSection.getConfigurationSection(prestigeName);
				Prestige prestige = new Prestige(prestigeName, null, null, 0.0,
						StringManager.parseColorsAndSymbols(current.getStringList("broadcast")),
						StringManager.parseColorsAndSymbols(
								ConfigManager.getOrElse(current, List.class, "message", "msg", "messages")),
						CommandsComponent.parseCommands(
								ConfigManager.getOrElse(current, "commands", "executecmds", "command", "cmd")),
						null, null, null, null, null, null, 0.0);
				prestiges.put(Long.parseLong(prestigeName), prestige);
			}
			ConfigurationSection globalSection = infinitePrestigeConfig.getConfigurationSection("Global-Settings");
			lastPrestigeNumber = ConfigManager.getOrElse(globalSection, "last-prestige", "final-prestige");
			lastPrestigeName = String.valueOf(lastPrestigeNumber);
		}

		@Override
		public boolean prestigeExists(String name) {
			return prestigeExists(Integer.parseInt(name));
		}

		@Override
		public boolean prestigeExists(long number) {
			return number > 0 && number <= lastPrestigeNumber;
		}

		@Override
		public Prestige getPrestige(String name) {
			return prestiges.get(Long.parseLong(name));
		}

		@Override
		public Prestige getPrestige(long number) {
			return prestiges.get(number);
		}

		@Override
		public String getFirstPrestigeName() {
			return firstPrestigeName;
		}

		@Override
		public long getFirstPrestigeAsInt() {
			return firstPrestigeNumber;
		}

		@Override
		public String getLastPrestigeName() {
			return lastPrestigeName;
		}

		@Override
		public long getLastPrestigeAsInt() {
			return lastPrestigeNumber;
		}

		@Override
		public Set<String> getPrestigeNames() {
			return null;
		}

		@Override
		public Collection<Prestige> getPrestiges() {
			return null;
		}

		@Override
		public String matchPrestigeName(String name) {
			return name;
		}

		@Override
		public Prestige matchPrestige(String name) {
			return prestiges.get(Long.parseLong(name));
		}

	}

}
