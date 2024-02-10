package me.dannynguyen.aethel.commands.aethelitem;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginDirectory;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.listeners.InventoryMenuListener;
import me.dannynguyen.aethel.utility.ItemCreator;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

/**
 * Inventory click event listener for AethelItem menus.
 *
 * @author Danny Nguyen
 * @version 1.9.11
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
    this.slotClicked = e.getSlot();
  }

  /**
   * Either saves an item or opens an item category page.
   */
  public void interpretMainMenuClick() {
    if (slotClicked == 4) {
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
    switch (slotClicked) {
      case 0 -> previousPage(action);
      case 2 -> { // Help Context
      }
      case 4 -> saveItem();
      case 5 -> toggleAction(action);
      case 6 -> returnToMainMenu();
      case 8 -> nextPage(action);
      default -> interpretContextualClick(action);
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
          FileWriter fw = new FileWriter(PluginDirectory.AETHELITEM.file.getPath() + "/" + nameItemFile(item) + "_itm.txt");
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
    String itemName = ChatColor.stripColor(ItemReader.readName(e.getCurrentItem()));
    int pageRequest = user.getMetadata(PluginPlayerMeta.PAGE.getMeta()).get(0).asInt();

    user.setMetadata(PluginPlayerMeta.CATEGORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), itemName));
    user.openInventory(new ItemMenu(user, ItemMenuAction.GET).openCategoryPage(itemName, pageRequest));
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.AETHELITEM_GET.menu));
  }

  /**
   * Opens the previous item category page.
   *
   * @param action type of interaction
   */
  private void previousPage(ItemMenuAction action) {
    String categoryName = user.getMetadata(PluginPlayerMeta.CATEGORY.getMeta()).get(0).asString();
    int pageRequest = user.getMetadata(PluginPlayerMeta.PAGE.getMeta()).get(0).asInt();

    user.openInventory(new ItemMenu(user, action).openCategoryPage(categoryName, pageRequest - 1));
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), "aethelitem." + ItemMenuAction.asString(action)));
  }

  /**
   * Toggles between get and remove actions.
   *
   * @param action type of interaction
   */
  private void toggleAction(ItemMenuAction action) {
    String categoryName = user.getMetadata(PluginPlayerMeta.CATEGORY.getMeta()).get(0).asString();
    int pageRequest = user.getMetadata(PluginPlayerMeta.PAGE.getMeta()).get(0).asInt();

    if (action == ItemMenuAction.GET) {
      user.openInventory(new ItemMenu(user, ItemMenuAction.REMOVE).openCategoryPage(categoryName, pageRequest));
      user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.AETHELITEM_REMOVE.menu));
    } else {
      user.openInventory(new ItemMenu(user, ItemMenuAction.GET).openCategoryPage(categoryName, pageRequest));
      user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.AETHELITEM_GET.menu));
    }
  }

  /**
   * Returns to the AethelItem main menu.
   */
  private void returnToMainMenu() {
    user.openInventory(new ItemMenu(user, ItemMenuAction.VIEW).openMainMenu());
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.AETHELITEM_CATEGORY.menu));
    user.setMetadata(PluginPlayerMeta.PAGE.getMeta(), new FixedMetadataValue(Plugin.getInstance(), "0"));
  }

  /**
   * Opens the next item category page.
   *
   * @param action type of interaction
   */
  private void nextPage(ItemMenuAction action) {
    String categoryName = user.getMetadata(PluginPlayerMeta.CATEGORY.getMeta()).get(0).asString();
    int pageRequest = user.getMetadata(PluginPlayerMeta.PAGE.getMeta()).get(0).asInt();

    user.openInventory(new ItemMenu(user, action).openCategoryPage(categoryName, pageRequest + 1));
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), "aethelitem." + ItemMenuAction.asString(action)));
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
        ItemStack item = PluginData.itemRegistry.getItemMap().get(ItemReader.readName(clickedItem)).getItem();
        if (user.getInventory().firstEmpty() != -1) {
          user.getInventory().addItem(item);
        } else {
          user.getWorld().dropItem(user.getLocation(), item);
        }
      }
      case REMOVE -> {
        PersistentItem aethelItem = PluginData.itemRegistry.getItemMap().get(ItemReader.readName(clickedItem));
        if (aethelItem.delete()) {
          user.sendMessage(ChatColor.RED + "[Removed Aethel Item] " + ChatColor.WHITE + aethelItem.getName());
        } else {
          user.sendMessage(ChatColor.WHITE + aethelItem.getName() + ChatColor.RED + " has already been removed.");
        }
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
      return meta.getDisplayName().toLowerCase().replace(" ", "_");
    } else {
      return item.getType().name().toLowerCase();
    }
  }
}
