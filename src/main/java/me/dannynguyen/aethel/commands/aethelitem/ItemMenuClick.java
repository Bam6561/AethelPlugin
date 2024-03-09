package me.dannynguyen.aethel.commands.aethelitem;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.systems.plugin.MenuMeta;
import me.dannynguyen.aethel.systems.plugin.PlayerMeta;
import me.dannynguyen.aethel.systems.plugin.Directory;
import me.dannynguyen.aethel.utility.ItemCreator;
import me.dannynguyen.aethel.utility.ItemReader;
import me.dannynguyen.aethel.utility.TextFormatter;
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
 * @version 1.14.6
 * @since 1.4.0
 */
public class ItemMenuClick {
  /**
   * Inventory click event.
   */
  private final InventoryClickEvent e;

  /**
   * Player who clicked.
   */
  private final Player user;

  /**
   * User's uuid.
   */
  private final UUID userUUID;

  /**
   * Slot clicked.
   */
  private final int slotClicked;

  /**
   * Associates an inventory click event with its user in the context of an open AethelItem menu.
   *
   * @param e inventory click event
   */
  public ItemMenuClick(@NotNull InventoryClickEvent e) {
    this.e = Objects.requireNonNull(e, "Null inventory click event");
    this.user = (Player) e.getWhoClicked();
    this.userUUID = user.getUniqueId();
    this.slotClicked = e.getSlot();
  }

  /**
   * Either saves an item or opens an item category page.
   */
  public void interpretMenuClick() {
    if (slotClicked == 3) { // Save Item Slot
      e.setCancelled(false);
    } else if (slotClicked == 4) {
      saveItem();
    } else if (slotClicked > 8) {
      viewItemCategory();
    }
  }

  /**
   * Either:
   * - increments or decrements an item category page
   * - saves an item
   * - changes the interaction type
   * - contextualizes the click to get or remove items
   *
   * @param action type of interaction
   */
  public void interpretCategoryClick(@NotNull ItemMenuAction action) {
    Objects.requireNonNull(action, "Null action");
    switch (slotClicked) {
      case 0 -> previousPage(action);
      case 2 -> { // Context
      }
      case 3 -> e.setCancelled(false); // Save Item Slot
      case 4 -> saveItem();
      case 5 -> toggleAction(action);
      case 6 -> returnToMenu();
      case 8 -> nextPage(action);
      default -> {
        if (slotClicked > 8) {
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
   * Views an item category.
   */
  private void viewItemCategory() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    String item = ChatColor.stripColor(ItemReader.readName(e.getCurrentItem()));
    int pageRequest = Integer.parseInt(playerMeta.get(PlayerMeta.PAGE));

    playerMeta.put(PlayerMeta.CATEGORY, item);
    user.openInventory(new ItemMenu(user, ItemMenuAction.GET).openCategoryPage(item, pageRequest));
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.AETHELITEM_GET.getMeta());
  }

  /**
   * Opens the previous item category page.
   *
   * @param action type of interaction
   */
  private void previousPage(ItemMenuAction action) {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    String category = playerMeta.get(PlayerMeta.CATEGORY);
    int pageRequest = Integer.parseInt(playerMeta.get(PlayerMeta.PAGE));

    user.openInventory(new ItemMenu(user, action).openCategoryPage(category, pageRequest - 1));
    playerMeta.put(PlayerMeta.INVENTORY, "aethelitem." + action.name().toLowerCase());
  }

  /**
   * Toggles between get and remove actions.
   *
   * @param action type of interaction
   */
  private void toggleAction(ItemMenuAction action) {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    String category = playerMeta.get(PlayerMeta.CATEGORY);
    int pageRequest = Integer.parseInt(playerMeta.get(PlayerMeta.PAGE));

    if (action == ItemMenuAction.GET) {
      user.openInventory(new ItemMenu(user, ItemMenuAction.REMOVE).openCategoryPage(category, pageRequest));
      playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.AETHELITEM_REMOVE.getMeta());
    } else {
      user.openInventory(new ItemMenu(user, ItemMenuAction.GET).openCategoryPage(category, pageRequest));
      playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.AETHELITEM_GET.getMeta());
    }
  }

  /**
   * Returns to the AethelItem main menu.
   */
  private void returnToMenu() {
    user.openInventory(new ItemMenu(user, ItemMenuAction.VIEW).openMenu());
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.AETHELITEM_CATEGORY.getMeta());
    playerMeta.put(PlayerMeta.PAGE, "0");
  }

  /**
   * Opens the next item category page.
   *
   * @param action type of interaction
   */
  private void nextPage(ItemMenuAction action) {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    String category = playerMeta.get(PlayerMeta.CATEGORY);
    int pageRequest = Integer.parseInt(playerMeta.get(PlayerMeta.PAGE));

    user.openInventory(new ItemMenu(user, action).openCategoryPage(category, pageRequest + 1));
    playerMeta.put(PlayerMeta.INVENTORY, "aethelitem." + action.name().toLowerCase());
  }

  /**
   * Either gets or remove an item.
   *
   * @param action interacting action
   */
  private void interpretContextualClick(ItemMenuAction action) {
    ItemStack clickedItem = e.getCurrentItem();
    switch (action) {
      case GET -> {
        ItemStack item = Plugin.getData().getItemRegistry().getItemMap().get(ItemReader.readName(clickedItem)).getItem();
        if (user.getInventory().firstEmpty() != -1) {
          user.getInventory().addItem(item);
        } else {
          user.getWorld().dropItem(user.getLocation(), item);
        }
      }
      case REMOVE -> {
        PersistentItem aethelItem = Plugin.getData().getItemRegistry().getItemMap().get(ItemReader.readName(clickedItem));
        aethelItem.delete();
        user.sendMessage(ChatColor.RED + "[Removed Aethel Item] " + ChatColor.WHITE + aethelItem.getName());
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
