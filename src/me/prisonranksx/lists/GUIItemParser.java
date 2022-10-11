package me.prisonranksx.lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.prisonranksx.components.CommandsComponent;
import me.prisonranksx.data.RankStorage;
import me.prisonranksx.holders.Rank;
import me.prisonranksx.managers.EconomyManager;
import me.prisonranksx.managers.StringManager;
import me.prisonranksx.utilities.InventoryUpdate;
import me.prisonranksx.utilities.NBTEditor;
import me.prisonranksx.utilities.PlayerPagedGUI;
import me.prisonranksx.utilities.PlayerPagedGUI.GUIItem;
import me.prisonranksx.utilities.XEnchantment;
import me.prisonranksx.utilities.XMaterial;

public class GUIItemParser {

	public static GUIItem parse(ConfigurationSection section, PlayerPagedGUI gui) {
		return parse(section, gui, new ItemStack(Material.BEDROCK, 1));
	}

	@SuppressWarnings("deprecation")
	public static GUIItem parse(ConfigurationSection section, PlayerPagedGUI gui, ItemStack itemStack) {
		ItemMeta itemMeta = itemStack.getItemMeta();

		String material = section.getString("material", null);
		int amount = section.getInt("amount", 1);
		short data = (short) section.getInt("data", 0);
		String name = StringManager.parseColorsAndSymbols(section.getString("name", null));
		List<String> lore = StringManager.parseColorsAndSymbols(section.getStringList("lore"));
		List<String> enchantments = section.getStringList("enchantments");
		List<String> flags = section.getStringList("flags");
		if (material != null)
			itemStack.setType(XMaterial.matchXMaterial(material).orElse(XMaterial.BEDROCK).parseMaterial());
		itemStack.setAmount(amount);
		if (data != 0) itemStack.setDurability(data);
		if (name != null) itemMeta.setDisplayName(name);
		if (lore != null) itemMeta.setLore(lore);
		if (!enchantments.isEmpty()) enchantments.forEach(line -> {
			String[] split = line.split(" ");
			itemMeta.addEnchant(XEnchantment.matchXEnchantment(split[0]).orElse(XEnchantment.DURABILITY).getEnchant(),
					Integer.parseInt(split[1]), true);
		});
		if (!flags.isEmpty()) flags.forEach(flag -> itemMeta.addItemFlags(ItemFlag.valueOf(flag.toUpperCase())));
		itemStack.setItemMeta(itemMeta);
		GUIItem guiItem = GUIItem.create(itemStack);

		// Convert list of string into actual click actions
		List<String> unformattedClickActions = section.getStringList("click-actions");
		List<ClickAction> clickActions = new ArrayList<>(Arrays.asList(new ClickAction(e -> e.setCancelled(true))));
		for (String unformatted : unformattedClickActions) if (unformatted.startsWith("[update-title] "))
			// Code formatter is drunk
			clickActions
					.add(new ClickAction(
							e -> InventoryUpdate
									.updateInventory((Player) e.getWhoClicked(),
											StringManager.parsePlaceholders(
													formatPlaceholders(e.getCurrentItem(),
															unformatted
																	.replace("%page%",
																			String.valueOf(gui.getCurrentPage(
																					(Player) e.getWhoClicked()) + 1))
																	.replace("%last_page%",
																			String.valueOf(gui.getLastPage() + 1))
																	.replace("[update-title] ", "")),
													(Player) e.getWhoClicked()))));
		else if (unformatted.startsWith("[switch-page]"))
			clickActions.add(new ClickAction(e -> gui.openInventory((Player) e.getWhoClicked(),
					gui.getCurrentPage((Player) e.getWhoClicked()) + Integer
							.parseInt(unformatted.replace("[switch-page] ", "").replace("[switch-page]", "")))));
		else if (unformatted.startsWith("[go-to-page]"))
			clickActions.add(new ClickAction(e -> gui.openInventory((Player) e.getWhoClicked(),
					Integer.parseInt(unformatted.replace("[go-to-page] ", "").replace("[go-to-page]", "")))));
		else if (unformatted.startsWith("[close]"))
			clickActions.add(new ClickAction(e -> e.getWhoClicked().closeInventory()));
		else if (unformatted.startsWith("[console]")) {
			String commandLine = unformatted.replace("[console] ", "").replace("[console]", "");
			clickActions.add(new ClickAction(e -> CommandsComponent.dispatchConsoleCommand(e.getWhoClicked(),
					formatPlaceholders(e.getCurrentItem(), commandLine))));
		} else if (unformatted.startsWith("[player]")) {
			String commandLine = unformatted.replace("[player] ", "").replace("[player]", "");
			clickActions.add(new ClickAction(e -> CommandsComponent.dispatchPlayerCommand(e.getWhoClicked(),
					formatPlaceholders(e.getCurrentItem(), commandLine))));
		}
		guiItem.onClick(e -> clickActions.forEach(clickAction -> clickAction.consumer.accept(e)));
		return guiItem;
	}

	public static String formatPlaceholders(ItemStack itemStack, String unformatted) {
		if (itemStack == null || itemStack.getType() == Material.AIR) return unformatted;
		String rankName = NBTEditor.getString(itemStack, "prx-rank");
		if (rankName == null) return unformatted;
		String pathName = NBTEditor.getString(itemStack, "prx-path");
		if (pathName == null) return unformatted;
		Rank rank = RankStorage.getRank(rankName, pathName);
		double rankCost = rank.getCost();
		String formatted = unformatted.replace("%rank%", rankName)
				.replace("%rank_display%", rank.getDisplayName())
				.replace("%rank_cost%", String.valueOf(rankCost))
				.replace("%rank_cost_formatted%", EconomyManager.shortcutFormat(rankCost));
		return formatted;
	}

	public static class ClickAction {

		private Consumer<InventoryClickEvent> consumer;

		public ClickAction(Consumer<InventoryClickEvent> consumer) {
			this.consumer = consumer;
		}

		public void perform(InventoryClickEvent e) {
			consumer.accept(e);
		}

		public Consumer<InventoryClickEvent> getConsumer() {
			return consumer;
		}

	}

}
