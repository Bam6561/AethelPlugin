package me.dannynguyen.aethel.commands.aethelitem;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.plugin.enums.Directory;
import me.dannynguyen.aethel.plugin.enums.MenuMeta;
import me.dannynguyen.aethel.plugin.enums.PlayerMeta;
import me.dannynguyen.aethel.plugin.interfaces.MenuClick;
import me.dannynguyen.aethel.util.TextFormatter;
import me.dannynguyen.aethel.util.item.ItemCreator;
import me.dannynguyen.aethel.util.item.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Inventory click event listener for AethelItem menus.
 *
 * @author Danny Nguyen
 * @version 1.17.5.1
 * @since 1.4.0
 */
public class ItemMenuClick implements MenuClick {
  /**
   * Inventory click event.
   */
  private final InventoryClickEvent e;

  /**
   * Player who clicked.
   */
  private final Player user;

  /**
   * User's UUID.
   */
  private final UUID uuid;

  /**
   * Slot clicked.
   */
  private final int slot;

  /**
   * Associates an inventory click event with its user in the context of an open AethelItem menu.
   *
   * @param e inventory click event
   */
  public ItemMenuClick(@NotNull InventoryClickEvent e) {
    this.e = Objects.requireNonNull(e, "Null inventory click event");
    this.user = (Player) e.getWhoClicked();
    this.uuid = user.getUniqueId();
    this.slot = e.getSlot();
  }

  /**
   * Either saves an item or gets an item category page.
   */
  public void interpretMenuClick() {
    switch (slot) {
      case 3 -> e.setCancelled(false); // Save Item Slot
      case 4 -> saveItem();
      default -> {
        if (slot > 8) {
          getCategoryPage();
        }
      }
    }
  }

  /**
   * Either:
   * <p>
   * - increments or decrements an item category page
   * </p>
   * <p>
   * - saves an item
   * </p>
   * <p>
   * - changes the interaction type
   * </p>
   * <p>
   * - contextualizes the click to get or remove items
   * </p>
   *
   * @param action type of interaction
   */
  public void interpretCategoryClick(@NotNull ItemMenu.Action action) {
    Objects.requireNonNull(action, "Null action");
    switch (slot) {
      case 0 -> previousPage(action);
      case 2 -> { // Context
      }
      case 3 -> e.setCancelled(false); // Save Item Slot
      case 4 -> saveItem();
      case 5 -> toggleAction(action);
      case 6 -> returnToMainMenu();
      case 8 -> nextPage(action);
      default -> {
        if (slot > 8) {
          interpretContextualClick(action);
        }
      }
    }
  }

  /**
   * Checks if there is an item in the designated save slot before saving the item to a file.
   */
  private void saveItem() {
    ItemStack item = e.getClickedInventory().getItem(3);
    if (ItemReader.isNotNullOrAir(item)) {
      String encodedItem = ItemCreator.encodeItem(item);
      if (encodedItem != null) {
        try {
          FileWriter fw = new FileWriter(Directory.AETHELITEM.getFile().getPath() + "/" + nameItemFile(item) + "_itm.txt");
          fw.write(encodedItem);
          fw.close();
          user.sendMessage(ChatColor.GREEN + "[Saved Aethel Item] " + ChatColor.WHITE + ItemReader.readName(item));
        } catch (IOException ex) {
          user.sendMessage(ChatColor.RED + "Failed to write item to file.");
        }
      } else {
        user.sendMessage(ChatColor.RED + "Failed to save item.");
      }
    } else {
      user.sendMessage(ChatColor.RED + "No item to save.");
    }
  }

  /**
   * Gets an item category page.
   */
  private void getCategoryPage() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(uuid);
    String item = ChatColor.stripColor(ItemReader.readName(e.getCurrentItem()));
    int pageRequest = Integer.parseInt(playerMeta.get(PlayerMeta.PAGE));

    playerMeta.put(PlayerMeta.CATEGORY, item);
    user.openInventory(new ItemMenu(user, ItemMenu.Action.GET).getCategoryPage(item, pageRequest));
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.AETHELITEM_GET.getMeta());
  }

  /**
   * Gets the previous item category page.
   *
   * @param action type of interaction
   */
  private void previousPage(ItemMenu.Action action) {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(uuid);
    String category = playerMeta.get(PlayerMeta.CATEGORY);
    int pageRequest = Integer.parseInt(playerMeta.get(PlayerMeta.PAGE));

    user.openInventory(new ItemMenu(user, action).getCategoryPage(category, pageRequest - 1));
    playerMeta.put(PlayerMeta.INVENTORY, "aethelitem." + action.name().toLowerCase());
  }

  /**
   * Toggles between get and remove actions.
   *
   * @param action type of interaction
   */
  private void toggleAction(ItemMenu.Action action) {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(uuid);
    String category = playerMeta.get(PlayerMeta.CATEGORY);
    int pageRequest = Integer.parseInt(playerMeta.get(PlayerMeta.PAGE));

    if (action == ItemMenu.Action.GET) {
      user.openInventory(new ItemMenu(user, ItemMenu.Action.REMOVE).getCategoryPage(category, pageRequest));
      playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.AETHELITEM_REMOVE.getMeta());
    } else {
      user.openInventory(new ItemMenu(user, ItemMenu.Action.GET).getCategoryPage(category, pageRequest));
      playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.AETHELITEM_GET.getMeta());
    }
  }

  /**
   * Returns to the AethelItem main menu.
   */
  private void returnToMainMenu() {
    user.openInventory(new ItemMenu(user, ItemMenu.Action.VIEW).getMainMenu());
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(uuid);
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.AETHELITEM_CATEGORY.getMeta());
    playerMeta.put(PlayerMeta.PAGE, "0");
  }

  /**
   * Gets the next item category page.
   *
   * @param action type of interaction
   */
  private void nextPage(ItemMenu.Action action) {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(uuid);
    String category = playerMeta.get(PlayerMeta.CATEGORY);
    int pageRequest = Integer.parseInt(playerMeta.get(PlayerMeta.PAGE));

    user.openInventory(new ItemMenu(user, action).getCategoryPage(category, pageRequest + 1));
    playerMeta.put(PlayerMeta.INVENTORY, "aethelitem." + action.name().toLowerCase());
  }

  /**
   * Either gets or remove an item.
   *
   * @param action interacting action
   */
  private void interpretContextualClick(ItemMenu.Action action) {
    ItemStack clickedItem = e.getCurrentItem();
    switch (action) {
      case GET -> {
        ItemStack item = Plugin.getData().getItemRegistry().getItems().get(ItemReader.readName(clickedItem)).getItem();
        if (user.getInventory().firstEmpty() != -1) {
          user.getInventory().addItem(item);
        } else {
          user.getWorld().dropItem(user.getLocation(), item);
        }
      }
      case REMOVE -> {
        PersistentItem pItem = Plugin.getData().getItemRegistry().getItems().get(ItemReader.readName(clickedItem));
        pItem.delete();
        user.sendMessage(ChatColor.RED + "[Removed Aethel Item] " + ChatColor.WHITE + pItem.getName());
      }
    }
  }

  /**
   * Names an item file by either its display name or material.
   *
   * @param item interacting item
   * @return item file name
   */
  private String nameItemFile(ItemStack item) {
    ItemMeta meta = item.getItemMeta();
    if (meta.hasDisplayName()) {
      return TextFormatter.formatId(meta.getDisplayName());
    } else {
      return TextFormatter.formatId(item.getType().name());
    }
  }
}
