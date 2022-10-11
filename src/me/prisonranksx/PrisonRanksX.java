package me.prisonranksx;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import me.prisonranksx.commands.RankupCommand;
import me.prisonranksx.data.IUserController;
import me.prisonranksx.data.PrestigeStorage;
import me.prisonranksx.data.RankStorage;
import me.prisonranksx.data.RebirthStorage;
import me.prisonranksx.data.YamlUserController;
import me.prisonranksx.executors.IRankupExecutor;
import me.prisonranksx.executors.RankupExecutor;
import me.prisonranksx.hooks.IHologramManager;
import me.prisonranksx.listeners.PlayerChatListener;
import me.prisonranksx.listeners.PlayerLoginListener;
import me.prisonranksx.managers.ActionBarManager;
import me.prisonranksx.managers.ConversionManager;
import me.prisonranksx.managers.EconomyManager;
import me.prisonranksx.managers.HologramManager;
import me.prisonranksx.managers.MySQLManager;
import me.prisonranksx.managers.PermissionsManager;
import me.prisonranksx.managers.StringManager;
import me.prisonranksx.permissions.PlayerGroupUpdater;
import me.prisonranksx.reflections.UniqueId;
import me.prisonranksx.settings.GlobalSettings;
import me.prisonranksx.settings.HologramSettings;
import me.prisonranksx.settings.PlaceholderAPISettings;
import me.prisonranksx.settings.PrestigeSettings;
import me.prisonranksx.settings.RankSettings;
import me.prisonranksx.settings.RebirthSettings;

public class PrisonRanksX extends JavaPlugin {

	/*
	 * // GENERAL BOOLEAN FIELDS private boolean mvdwPlaceholderAPILoaded,
	 * actionUtil, debug, terminateMode, legacy, rankEnabled, prestigeEnabled,
	 * rebirthEnabled, forceSave, actionBarProgress, rankupMaxWarpFilter,
	 * vaultGroups, hologramsPlugin, placeholderAPILoaded, saveOnLeave, checkVault,
	 * saveNotification, allowEasterEggs, enabledInsteadOfDisabled,
	 * infinitePrestige, rankForceDisplay, prestigeForceDisplay,
	 * rebirthForceDisplay, formatChat, modernVersion; // ====================== //
	 * MYSQL FIELDS private MySQLManager mySqlManager; // ====================== //
	 * INTERFACE FIELDS private IGlobalStorage globalStorage;
	 * 
	 * private PlaceholderReplacer placeholderReplacer; private ChatColorReplacer
	 * chatColorReplacer; // ====================== // DATA STORAGE FIELDS private
	 * UserStorage userStorage; private RankStorage rankStorage; private
	 * IPrestigeStorage prestigeStorage; private RebirthStorage rebirthStorage;
	 * private MessagesStorage messagesStorage; // ====================== //
	 * PERMISSION FIELDS private Permission vaultPermissionService; private
	 * PermissionManager permissionManager; private String vaultPlugin; private
	 * IVaultDataUpdater vaultDataUpdater; private GMHook groupManager; //
	 * ====================== // API FIELDS private IRankupExecutor rankupExecutor;
	 * private IPrestigeExecutor prestigeExecutor; private IRebirthExecutor
	 * rebirthLegacy; private RanksList ranksList; private PrestigesList
	 * prestigesList; private RebirthsList rebirthList; private IngameStageEditor
	 * ingameStageEditor; // ====================== // COMMAND FIELDS private
	 * PRXCommand prxCommand; private RankupCommand rankupCommand; private
	 * PrestigeCommand prestigeCommand; private RankupMaxCommand rankupMaxCommand;
	 * private RanksCommand ranksCommand; private RebirthCommand rebirthCommand;
	 * private PrestigesCommand prestigesCommand; private RebirthsCommand
	 * rebirthsCommand; private AutoRankupCommand autoRankupCommand; private
	 * AutoPrestigeCommand autoPrestigeCommand; private ForceRankupCommand
	 * forceRankupCommand; private ForcePrestigeCommand forcePrestigeCommand;
	 * private TopPrestigesCommand topPrestigesCommand; private TopRebirthsCommand
	 * topRebirthsCommand; private PrestigeMaxCommand prestigeMaxCommand; //
	 * ====================== // GUI FIELDS private GUIManager guiManager; private
	 * GUIItemsManager guiItemsManager; // ====================== // UTIL FIELDS
	 * private HolidayUtils holidayUtils; private CommandLoader commandLoader;
	 * private ConfigManager configManager; private FireworkManager fireworkManager;
	 * // ====================== // HOOK FIELDS private MVdWPlaceholderAPIHook
	 * mvdwPlaceholderAPIHook; private PlaceholderAPIHook placeholderAPIHook;
	 * private Economy vaultEconomyService; private HologramManager hologramManager;
	 * // ====================== // BALANCE FORMAT FIELDS // private String k, M, B,
	 * T, q, Q, s, S, O, N, d, U, D, Z; // private String[] abbreviations; //
	 * private final DecimalFormat abb = new DecimalFormat("0.##"); private
	 * EconomyManager economyManager; // ====================== // OTHER FIELDS
	 * private BukkitScheduler SCHEDULER; private ConsoleCommandSender CONSOLE;
	 * private PluginManager pluginManager; private LeaderboardManager
	 * leaderboardManager; private ActionBarManager actionBarManager; private
	 * ErrorInspector errorInspector; private InfinitePrestigeSettings
	 * infinitePrestigeSettings; private static PrisonRanksX instance; private int
	 * autoSaveTime; private Set<String> disabledWorlds; private final List<String>
	 * oldVersions = Arrays.asList("1.15", "1.14", "1.13", "1.12", "1.11", "1.10",
	 * "1.9", "1.8", "1.7", "1.6", "1.5", "1.4"); private final List<String>
	 * ancientVersions = Arrays.asList("1.6", "1.5", "1.4"); public final static
	 * String PREFIX = "§e[§9PrisonRanks§cX§e]"; // ====================== //
	 * LISTENERS private IPlayerLoginListener playerLoginListener; private
	 * IPlayerChatListener playerChatListener; private IPlayerQuitListener
	 * playerQuitListener; private PrisonRanksXListener prisonRanksXListener;
	 * private InventoryListener inventoryListener; // ======================
	 * private TaskChainFactory taskChainFactory;
	 */
	private static final String PREFIX = "�e[�3PrisonRanks�cX�e]";
	private static final BukkitScheduler SCHEDULER = Bukkit.getScheduler();
	private static final ConsoleCommandSender CONSOLE = Bukkit.getConsoleSender();
	private static PrisonRanksX instance;

	// Settings loaded from config files
	private GlobalSettings globalSettings;
	private RankSettings rankSettings;
	private PrestigeSettings prestigeSettings;
	private RebirthSettings rebirthSettings;
	private PlaceholderAPISettings placeholderAPISettings;
	private HologramSettings hologramSettings;

	// Executors
	private IRankupExecutor rankupExecutor;
	private IHologramManager hologramManager;

	// Interfaces holding classes
	private PlayerGroupUpdater playerGroupUpdater;

	// Commands
	private RankupCommand rankupCommand;

	// User Management
	private IUserController userController;

	// Listeners
	protected PlayerLoginListener playerLoginListener;
	protected PlayerChatListener playerChatListener;

	@Override
	public void onEnable() {

		ConversionManager.convertConfigFiles();

		if (GlobalSettings.SUPPORTS_ACTION_BAR) ActionBarManager.cache(); // Only load if using 1.8+ cuz action bars
																			// didn't exist in the older versions.
		StringManager.cache(); // Parse colors, PlaceholderAPI placeholders if PAPI is installed, and symbols.
		UniqueId.cache(); // UUID support for legacy versions and newer versions.
		EconomyManager.cache(); // Vault economy and Balance Formatter.
		HologramManager.cache(); // Load DecentHolograms, HolographicDisplays, or nothing.
		PermissionsManager.cache(); // Vault permissions.
		MySQLManager.cache(); // A check is inside the class to determine whether MySQL should be enabled or
								// not.
		globalSettings = new GlobalSettings();
		userController = new YamlUserController(this);
		playerGroupUpdater = new PlayerGroupUpdater(this);
		registerListeners();

		if (StringManager.isPlaceholderAPI()) {
			logNeutral("Loading PlaceholderAPI placeholders...");
			placeholderAPISettings = new PlaceholderAPISettings();
			// load placeholders here.
			log("PlaceholderAPI placeholders have been registered, loaded, and are ready to use! '/papi ecloud' is not needed.");
		} else if (globalSettings.isMvdwPlaceholderAPILoaded()) {
			placeholderAPISettings = new PlaceholderAPISettings();
			// load placeholders here.
			log("MVdWPlaceholderAPI dependency loaded.");
		} else {
			logWarning("The plugin 'PlaceholderAPI' is not installed, which means NO rank, prestige, and rebirth "
					+ "in your scoreboards and tab lists.");
		}
		if (globalSettings.isDecentHologramsLoaded()) {
			hologramSettings = new HologramSettings();
			log("DecentHolograms dependency loaded.");
		} else if (globalSettings.isHolographicDisplaysLoaded()) {
			hologramSettings = new HologramSettings();
			log("HolographicDisplays dependency loaded.");
		}
		if (globalSettings.isRankEnabled()) {
			RankStorage.loadRanks();
			rankSettings = new RankSettings();
			rankupExecutor = new RankupExecutor(this);
			if (RankupCommand.isEnabled()) {
				rankupCommand = new RankupCommand(this);
				rankupCommand.register();
			}
		}
		if (globalSettings.isPrestigeEnabled()) {
			PrestigeStorage.initAndLoad(globalSettings.isInfinitePrestige());
			prestigeSettings = new PrestigeSettings();
		}
		if (globalSettings.isRebirthEnabled()) {
			RebirthStorage.loadRebirths();
			rebirthSettings = new RebirthSettings();
		}

		log("Enabled.");
	}

	public void registerListeners() {
		playerLoginListener = new PlayerLoginListener(this,
				EventPriority.valueOf(globalSettings.getLoginEventHandlingPriority()));
		if (globalSettings.isFormatChat()) playerChatListener = new PlayerChatListener(this,
				EventPriority.valueOf(globalSettings.getChatEventHandlingPriority()));
	}

	public static void log(String message) {
		CONSOLE.sendMessage(PREFIX + " �a" + message);
	}

	public static void logNeutral(String message) {
		CONSOLE.sendMessage(PREFIX + " �7" + message);
	}

	public static void logWarning(String message) {
		CONSOLE.sendMessage(PREFIX + " �e[!] " + message);
	}

	public static void logSevere(String message) {
		CONSOLE.sendMessage(PREFIX + " �4[!] �c" + message);
	}

	public BukkitTask doSyncLater(Runnable runnable, int delay) {
		return SCHEDULER.runTaskLater(this, runnable, delay);
	}

	public BukkitTask doSync(Runnable runnable) {
		return SCHEDULER.runTask(this, runnable);
	}

	public BukkitTask doSyncRepeating(Runnable runnable, int delay, int speed) {
		return SCHEDULER.runTaskTimer(this, runnable, delay, speed);
	}

	public BukkitTask doAsync(Runnable runnable) {
		return SCHEDULER.runTaskAsynchronously(this, runnable);
	}

	public BukkitTask doAsyncRepeating(Runnable runnable, int delay, int speed) {
		return SCHEDULER.runTaskTimerAsynchronously(this, runnable, delay, speed);
	}

	public BukkitTask doAsyncLater(Runnable runnable, int delay) {
		return SCHEDULER.runTaskLaterAsynchronously(this, runnable, delay);
	}

	public GlobalSettings getGlobalSettings() {
		return globalSettings;
	}

	public static PrisonRanksX getInstance() {
		return instance;
	}

	public IUserController getUserController() {
		return userController;
	}

	public PlaceholderAPISettings getPlaceholderAPISettings() {
		return placeholderAPISettings;
	}

	public IRankupExecutor getRankupExecutor() {
		return rankupExecutor;
	}

	public RankSettings getRankSettings() {
		return rankSettings;
	}

	public PrestigeSettings getPrestigeSettings() {
		return prestigeSettings;
	}

	public RebirthSettings getRebirthSettings() {
		return rebirthSettings;
	}

	private TaskChainFactory taskChainFactory;

	public <T> TaskChain<T> newChain() {
		return taskChainFactory.newChain();
	}

	public <T> TaskChain<T> newSharedChain(String name) {
		return taskChainFactory.newSharedChain(name);
	}

	public PlayerGroupUpdater getPlayerGroupUpdater() {
		return playerGroupUpdater;
	}

	public IHologramManager getHologramManager() {
		return hologramManager;
	}

	public HologramSettings getHologramSettings() {
		return hologramSettings;
	}

}
