package me.prisonranksx.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import me.prisonranksx.reflections.UniqueId;

public class PlayerPagedGUI implements Listener {

	private final JavaPlugin plugin = UniqueId.getProvidingPlugin(PlayerPagedGUI.class);
	private Map<Integer, GUIItem> staticItems;
	private Map<Integer, GUIItem> virtualInventory;
	private Map<String, Integer> playersCurrentPage;
	private List<Integer> pagedItemsSlots;
	private Inventory staticInventory;
	private List<Inventory> initialInventory;
	private Map<String, List<Inventory>> playersInventories;
	private Set<String> pageSwitchers;
	private String title;
	private int size;
	private int virtualSlot;
	private int cyclingSlot;
	private int pageCounter;
	private int lastPage = 1;

	public PlayerPagedGUI(int size, String title) {
		this(size, title, -1);
	}

	public PlayerPagedGUI(int size, String title, int expectedUsedSlots) {
		staticItems = new HashMap<>();
		virtualInventory = new HashMap<>();
		pagedItemsSlots = new ArrayList<>();
		playersInventories = new HashMap<>();
		playersCurrentPage = new HashMap<>();
		pageSwitchers = new HashSet<>();
		this.size = size;
		this.title = title;
		staticInventory = Bukkit.createInventory(null, size, title);
		initialInventory = new ArrayList<>(Arrays.asList(staticInventory));
		if (expectedUsedSlots != -1) {
			int inventoriesAmount = (int) Math.ceil((double) expectedUsedSlots / (double) size);
			for (int i = 1; i < inventoriesAmount; i++) lastPage++;
		} else {
			lastPage = (int) Math.ceil((double) expectedUsedSlots / (double) size);
		}
		for (int i = 0; i < size; i++) pagedItemsSlots.add(i);
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void setStaticItem(int slot, GUIItem guiItem) {
		staticItems.put(slot, guiItem);
		pagedItemsSlots.remove(pagedItemsSlots.indexOf(slot));
		staticInventory.setItem(slot, guiItem.getItemStack());
	}

	public void addPagedItem(GUIItem guiItem, Player player) {
		virtualSlot = pagedItemsSlots.get(cyclingSlot);
		virtualInventory.put(virtualSlot, guiItem);
		getOrCreate(pageCounter, player).setItem(virtualSlot, guiItem.getItemStack());
		cyclingSlot++;
		if (cyclingSlot > pagedItemsSlots.size() - 1) {
			cyclingSlot = 0;
			pageCounter++;
		}
	}

	private Inventory getOrCreate(int pageIndex, Player player) {
		return getOrCreate(pageIndex, player.getName());
	}

	private Inventory getOrCreate(int pageIndex, String playerName) {
		if (!playersInventories.containsKey(playerName)) playersInventories.put(playerName, initialInventory);
		List<Inventory> playerInventories = playersInventories.get(playerName);
		int inventoriesLastIndex = playerInventories.size() - 1;
		if (pageIndex <= inventoriesLastIndex) return playerInventories.get(inventoriesLastIndex);

		playersInventories.get(playerName).add(Bukkit.createInventory(null, size, title));
		lastPage++;
		Inventory createdInventory = playersInventories.get(playerName)
				.get(playersInventories.get(playerName).size() - 1);
		staticItems.forEach((slot, guiItem) -> createdInventory.setItem(slot, guiItem.getItemStack()));
		return createdInventory;
	}

	public InventoryView openInventory(String playerName, int pageIndex) {
		Player player = Bukkit.getPlayer(playerName);
		pageSwitchers.add(playerName);
		player.closeInventory();
		List<Inventory> playerInventories = playersInventories.get(playerName);
		int inventoriesLastIndex = playerInventories.size() - 1;
		int actualPage = pageIndex > inventoriesLastIndex ? pageIndex = inventoriesLastIndex : pageIndex < 0 ? 0
				: pageIndex;
		playersCurrentPage.put(playerName, actualPage);
		pageSwitchers.remove(playerName);
		return Bukkit.getPlayer(playerName).openInventory(playerInventories.get(actualPage));
	}

	public InventoryView openInventory(Player player, int pageIndex) {
		String playerName = player.getName();
		pageSwitchers.add(playerName);
		player.closeInventory();
		List<Inventory> playerInventories = playersInventories.get(playerName);
		int inventoriesLastIndex = playerInventories.size() - 1;
		int actualPage = pageIndex > inventoriesLastIndex ? pageIndex = inventoriesLastIndex : pageIndex < 0 ? 0
				: pageIndex;
		playersCurrentPage.put(playerName, actualPage);
		pageSwitchers.remove(playerName);
		return player.openInventory(playerInventories.get(actualPage));
	}

	public InventoryView openInventory(Player player) {
		return openInventory(player, 0);
	}

	public boolean isOpen(String playerName) {
		return playersCurrentPage.containsKey(playerName);
	}

	public boolean isSwitchingPages(String playerName) {
		return pageSwitchers.contains(playerName);
	}

	public int getCurrentPage(String playerName) {
		return playersCurrentPage.get(playerName);
	}

	public int getCurrentPage(Player player) {
		return getCurrentPage(player.getName());
	}

	public int getLastPage() {
		return lastPage;
	}

	public Inventory getPage(Player player, int pageIndex) {
		return playersInventories.get(player.getName()).get(pageIndex);
	}

	public List<Inventory> getPages(Player player) {
		return playersInventories.get(player.getName());
	}

	public String getTitle() {
		return title;
	}

	public int getSize() {
		return size;
	}

	public Inventory getStaticInventory() {
		return staticInventory;
	}

	public void unregisterEvents() {
		InventoryClickEvent.getHandlerList().unregister(this);
		InventoryCloseEvent.getHandlerList().unregister(this);
	}

	@EventHandler
	private void onInventoryClick(InventoryClickEvent e) {
		String clickerName = e.getWhoClicked().getName();
		if (!isOpen(clickerName)) return;
		if (e.getClickedInventory() instanceof PlayerInventory) {
			e.setCancelled(true);
			return;
		}
		int slot = e.getSlot();
		GUIItem guiItem = virtualInventory.get(slot);
		if (guiItem != null) guiItem.getOnClick().accept(e);
		GUIItem staticGuiItem = staticItems.get(slot);
		if (staticGuiItem != null) staticGuiItem.getOnClick().accept(e);
	}

	@EventHandler
	private void onInventoryClose(InventoryCloseEvent e) {
		String clickerName = e.getPlayer().getName();
		if (isOpen(clickerName)) {
			playersCurrentPage.remove(clickerName);
			if (!isSwitchingPages(clickerName)) playersInventories.remove(clickerName);
		}
	}

	public static class GUIItem {

		private ItemStack itemStack;
		private Consumer<InventoryClickEvent> onClick;

		public GUIItem(ItemStack itemStack) {
			this.itemStack = itemStack;
			this.onClick = e -> {};
		}

		public static GUIItem create(ItemStack itemStack) {
			return new GUIItem(itemStack);
		}

		public ItemStack getItemStack() {
			return itemStack;
		}

		public void setItemStack(ItemStack itemStack) {
			this.itemStack = itemStack;
		}

		public void onClick(Consumer<InventoryClickEvent> onClick) {
			this.onClick = onClick;
		}

		public void onClickAdd(Consumer<InventoryClickEvent> onClick) {
			this.onClick = this.onClick.andThen(onClick);
		}

		public Consumer<InventoryClickEvent> getOnClick() {
			return onClick;
		}

	}

}
