package me.prisonranksx.lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.managers.ConfigManager;
import me.prisonranksx.managers.StringManager;
import me.prisonranksx.utilities.InventoryUpdate;
import me.prisonranksx.utilities.PlayerPagedGUI;
import me.prisonranksx.utilities.PlayerPagedGUI.GUIItem;

public abstract class AbstractGUIList {

	private PrisonRanksX plugin;
	private String parentSectionName;
	private ConfigurationSection parentSection;
	private PlayerPagedGUI playerPagedGUI;
	private GUIItem currentItem;
	private GUIItem completedItem;
	private GUIItem otherItem;
	private Map<String, GUIItem> specialCurrentItems;
	private Map<String, GUIItem> specialCompletedItems;
	private Map<String, GUIItem> specialOtherItems;
	private List<String> dynamicTitle;
	private boolean dynamic;
	private BukkitTask animationTask;
	private int animationIndex;
	private int animationSpeed;

	public AbstractGUIList(PrisonRanksX plugin, String parentSectionName) {
		this.parentSectionName = parentSectionName;
		this.parentSection = ConfigManager.getGUIConfig().getConfigurationSection(parentSectionName);
		this.plugin = plugin;
		specialCurrentItems = new HashMap<>();
		specialCompletedItems = new HashMap<>();
		specialOtherItems = new HashMap<>();
		dynamicTitle = new ArrayList<>();
		animationSpeed = 5;
	}

	public ConfigurationSection getParentSection() {
		return parentSection;
	}

	public String getParentSectionName() {
		return parentSectionName;
	}

	public PlayerPagedGUI getPlayerPagedGUI() {
		return playerPagedGUI;
	}

	public GUIItem getCurrentItem() {
		return currentItem;
	}

	public GUIItem getCompletedItem() {
		return completedItem;
	}

	public GUIItem getOtherItem() {
		return otherItem;
	}

	public Map<String, GUIItem> getSpecialCurrentItems() {
		return specialCurrentItems;
	}

	public Map<String, GUIItem> getSpecialCompletedItems() {
		return specialCompletedItems;
	}

	public Map<String, GUIItem> getSpecialOtherItems() {
		return specialOtherItems;
	}

	public abstract void openGUI(Player player);

	private String initTitle() {
		String plainTitle = parentSection.getString("title");
		if (!plainTitle.contains(";;")) return StringManager.parseColorsAndSymbols(plainTitle);
		dynamic = true;
		if (parentSection.isInt("speed")) animationSpeed = parentSection.getInt("speed");
		for (String title : plainTitle.split(";;")) dynamicTitle.add(StringManager.parseColorsAndSymbols(title));
		return StringManager.parseColorsAndSymbols(dynamicTitle.get(0));
	}

	public boolean isDynamic() {
		return dynamic;
	}

	private void startAnimations() {
		if (animationTask != null) return;
		animationTask = plugin.doAsyncRepeating(() -> {
			Bukkit.getOnlinePlayers().forEach(player -> {
				if (playerPagedGUI.isOpen(player.getName())) InventoryUpdate.updateInventory(player,
						dynamicTitle.get(animationIndex)
								.replace("%page%", String.valueOf(playerPagedGUI.getCurrentPage(player) + 1))
								.replace("%last_page%", String.valueOf(playerPagedGUI.getLastPage() + 1)));
			});
			if (animationIndex < dynamicTitle.size() - 1)
				animationIndex++;
			else
				animationIndex = 0;
		}, 1, animationSpeed);
	}

	public void stopAnimations() {
		if (animationTask != null) {
			animationTask.cancel();
			animationTask = null;
		}
	}

	/**
	 * Used for initialization and reloading GUI values
	 */
	public void setup() {
		FileConfiguration config = ConfigManager.getGUIConfig();

		parentSection = config.getConfigurationSection(parentSectionName);
		playerPagedGUI = new PlayerPagedGUI(parentSection.getInt("size"), initTitle());

		if (isDynamic()) startAnimations();
		/*
		 * Set static GUI items that don't need to be updated and are placed in every
		 * page
		 */
		setStatics();

		ConfigurationSection currentItemSection = parentSection.getConfigurationSection("current-item");
		ConfigurationSection completedItemSection = parentSection.getConfigurationSection("completed-item");
		ConfigurationSection otherItemSection = parentSection.getConfigurationSection("other-item");

		currentItem = GUIItemParser.parse(currentItemSection, playerPagedGUI);
		completedItem = GUIItemParser.parse(completedItemSection, playerPagedGUI);
		otherItem = GUIItemParser.parse(otherItemSection, playerPagedGUI);

		if (currentItemSection.isConfigurationSection("special")) {
			ConfigurationSection specialSection = currentItemSection.getConfigurationSection("special");
			specialSection.getKeys(false)
					.forEach(levelName -> specialCurrentItems.put(levelName,
							GUIItemParser.parse(specialSection.getConfigurationSection(levelName), playerPagedGUI,
									currentItem.getItemStack().clone())));
		}
		if (completedItemSection.isConfigurationSection("special")) {
			ConfigurationSection specialSection = completedItemSection.getConfigurationSection("special");
			specialSection.getKeys(false)
					.forEach(levelName -> specialCompletedItems.put(levelName,
							GUIItemParser.parse(specialSection.getConfigurationSection(levelName), playerPagedGUI,
									completedItem.getItemStack().clone())));
		}
		if (otherItemSection.isConfigurationSection("special")) {
			ConfigurationSection specialSection = otherItemSection.getConfigurationSection("special");
			specialSection.getKeys(false)
					.forEach(levelName -> specialOtherItems.put(levelName,
							GUIItemParser.parse(specialSection.getConfigurationSection(levelName), playerPagedGUI,
									otherItem.getItemStack().clone())));
		}
	}

	public void unregister() {
		playerPagedGUI.unregisterEvents();
	}

	private void setStatics() {
		ConfigurationSection globalSection = ConfigManager.getGUIConfig().getConfigurationSection("Global");
		globalSection.getKeys(false).forEach(itemSectionName -> {
			ConfigurationSection itemSection = globalSection.getConfigurationSection(itemSectionName);
			GUIItem guiItem = GUIItemParser.parse(itemSection, playerPagedGUI);
			if (itemSection.isInt("slot")) {
				playerPagedGUI.setStaticItem(itemSection.getInt("slot"), guiItem);
			} else {
				String stringSlot = itemSection.getString("slot").replace(",", ", ");
				for (String fillerSlot : stringSlot.split(", ")) {
					playerPagedGUI.setStaticItem(Integer.parseInt(fillerSlot), guiItem);
				}
			}
		});
	}

	public PrisonRanksX getPlugin() {
		return plugin;
	}

}
