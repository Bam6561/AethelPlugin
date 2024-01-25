package me.dannynguyen.aethel.listeners.inventory;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.inventories.aethelItems.AethelItemsMain;
import me.dannynguyen.aethel.inventories.aethelItems.utility.AethelItemSave;
import me.dannynguyen.aethel.inventories.aethelItems.utility.AethelItemsGet;
import me.dannynguyen.aethel.inventories.aethelItems.utility.AethelItemsRemove;
import me.dannynguyen.aethel.readers.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * AethelItemsInventory is an inventory listener for the AethelItem command.
 *
 * @author Danny Nguyen
 * @version 1.6.1
 * @since 1.4.0
 */
public class AethelItemsInventory {
  /**
   * Checks if the player's action is allowed based on the clicked inventory.
   * <p>
   * - AethelItems: Prevent adding new items to the inventory outside of the intended Save Item slot.
   * - Player: Prevent shift-clicks adding items to the AethelItems inventory.
   * </p>
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public static void readMainClick(InventoryClickEvent e, Player player) {
    Inventory clickedInv = e.getClickedInventory();
    if (clickedInv != null && !clickedInv.getType().equals(InventoryType.PLAYER)) {
      if (e.getCurrentItem() != null) {
        interpretMainClick(e, player);
      } else if (e.getSlot() != 3) {
        e.setCancelled(true);
      }
    } else {
      if (e.getClick().isShiftClick()) {
        e.setCancelled(true);
      }
    }
  }

  /**
   * Either saves an item file or opens an item category page.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  private static void interpretMainClick(InventoryClickEvent e, Player player) {
    if (e.getSlot() == 4) {
      AethelItemSave.readSaveClick(e, player);
    } else if (e.getSlot() > 8) {
      String itemName = ChatColor.stripColor(ItemReader.readItemName(e.getCurrentItem()));
      player.setMetadata("category", new FixedMetadataValue(AethelPlugin.getInstance(), itemName));
      int pageRequest = player.getMetadata("page").get(0).asInt();

      player.openInventory(AethelItemsMain.openItemCategoryPage(player, "get", itemName, pageRequest));
      player.setMetadata("inventory",
          new FixedMetadataValue(AethelPlugin.getInstance(), "aethelitems.get"));
    }

    if (e.getSlot() != 3) {
      e.setCancelled(true);
    }
  }

  /**
   * Checks if the player's action is allowed based on the clicked inventory.
   * <p>
   * - AethelItems: Prevent adding new items to the inventory outside of the intended Save Item slot.
   * - Player: Prevent shift-clicks adding items to the AethelItem inventory.
   * </p>
   *
   * @param e      inventory click event
   * @param player interacting player
   * @param action type of interaction
   */
  public static void readCategoryClick(InventoryClickEvent e, Player player, String action) {
    Inventory clickedInv = e.getClickedInventory();
    if (clickedInv != null && !clickedInv.getType().equals(InventoryType.PLAYER)) {
      if (e.getCurrentItem() != null) {
        interpretCategoryClick(e, player, action);
      } else if (e.getSlot() != 3) {
        e.setCancelled(true);
      }
    } else {
      if (e.getClick().isShiftClick()) {
        e.setCancelled(true);
      }
    }
  }

  /**
   * Either:
   * - increments or decrements an item page
   * - saves an item file
   * - changes the interaction type
   * - contextualizes the click to get or remove items
   *
   * @param e      inventory click event
   * @param player interacting player
   * @param action type of interaction
   */
  private static void interpretCategoryClick(InventoryClickEvent e, Player player, String action) {
    int slotClicked = e.getSlot();

    switch (slotClicked) {
      case 0 -> previousItemPage(player, action);
      case 2 -> { // Help Context
      }
      case 4 -> AethelItemSave.readSaveClick(e, player);
      case 5 -> toggleGetRemoveAction(player, action);
      case 6 -> returnToMainPage(player);
      case 8 -> nextItemPage(player, action);
      default -> interpretContextualClick(e, action, player);
    }

    if (e.getSlot() != 3) {
      e.setCancelled(true);
    }
  }

  /**
   * Opens the previous item page.
   *
   * @param player interacting player
   * @param action type of interaction
   */
  private static void previousItemPage(Player player, String action) {
    String categoryName = player.getMetadata("category").get(0).asString();
    int pageRequest = player.getMetadata("page").get(0).asInt();

    player.openInventory(AethelItemsMain.openItemCategoryPage(player, action,
        categoryName, pageRequest - 1));
    player.setMetadata("inventory",
        new FixedMetadataValue(AethelPlugin.getInstance(), "aethelitems." + action));
  }

  /**
   * Toggles between get and remove actions.
   *
   * @param player interacting player
   * @param action type of interaction
   */
  private static void toggleGetRemoveAction(Player player, String action) {
    String categoryName = player.getMetadata("category").get(0).asString();
    int pageRequest = player.getMetadata("page").get(0).asInt();

    if (action.equals("get")) {
      player.openInventory(AethelItemsMain.openItemCategoryPage(player, "remove",
          categoryName, pageRequest));
      player.setMetadata("inventory",
          new FixedMetadataValue(AethelPlugin.getInstance(), "aethelitems.remove"));
    } else {
      player.openInventory(AethelItemsMain.openItemCategoryPage(player, "get",
          categoryName, pageRequest));
      player.setMetadata("inventory",
          new FixedMetadataValue(AethelPlugin.getInstance(), "aethelitems.get"));
    }
  }

  /**
   * Opens a AethelItemsMain inventory.
   *
   * @param player interacting player
   */
  private static void returnToMainPage(Player player) {
    player.openInventory(AethelItemsMain.openItemMainPage(player, "view"));
    player.setMetadata("inventory",
        new FixedMetadataValue(AethelPlugin.getInstance(), "aethelitems.category"));
    player.setMetadata("page", new FixedMetadataValue(AethelPlugin.getInstance(), "0"));
  }

  /**
   * Opens the next item page.
   *
   * @param player interacting player
   * @param action type of interaction
   */
  private static void nextItemPage(Player player, String action) {
    String categoryName = player.getMetadata("category").get(0).asString();
    int pageRequest = player.getMetadata("page").get(0).asInt();

    player.openInventory(AethelItemsMain.openItemCategoryPage(player, action,
        categoryName, pageRequest + 1));
    player.setMetadata("inventory",
        new FixedMetadataValue(AethelPlugin.getInstance(), "aethelitems." + action));
  }

  /**
   * Either gets or remove an item.
   *
   * @param e      inventory click event
   * @param action interacting action
   * @param player interacting player
   */
  private static void interpretContextualClick(InventoryClickEvent e, String action, Player player) {
    switch (action) {
      case "get" -> AethelItemsGet.getItem(e, player);
      case "remove" -> AethelItemsRemove.removeItem(e, player);
    }
  }
}
