package me.dannynguyen.aethel.commands.aethelItems;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.listeners.InventoryListener;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * AethelItemsInventoryListener is an inventory listener for the AethelItems inventory.
 *
 * @author Danny Nguyen
 * @version 1.8.4
 * @since 1.4.0
 */
public class AethelItemsInventoryListener {
  /**
   * Checks if the user's action is allowed based on the clicked inventory.
   * <p>
   * Additional Parameters:
   * - AethelItems: prevent adding new items to the inventory outside of the intended Save Item slot
   * - Player: prevent shift-clicks adding items to the AethelItems inventory
   * </p>
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void readMainClick(InventoryClickEvent e, Player user) {
    Inventory clickedInv = e.getClickedInventory();
    if (clickedInv != null && !clickedInv.getType().equals(InventoryType.PLAYER)) {
      if (e.getCurrentItem() != null) {
        interpretMainClick(e, user);
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
   * @param e    inventory click event
   * @param user user
   */
  private static void interpretMainClick(InventoryClickEvent e, Player user) {
    if (e.getSlot() == 4) {
      AethelItemsAction.readSaveClick(e, user);
    } else if (e.getSlot() > 8) {
      String itemName = ChatColor.stripColor(ItemReader.readName(e.getCurrentItem()));
      user.setMetadata(PluginPlayerMeta.Namespace.CATEGORY.namespace,
          new FixedMetadataValue(Plugin.getInstance(), itemName));
      int pageRequest = user.getMetadata(PluginPlayerMeta.Namespace.PAGE.namespace).get(0).asInt();

      user.openInventory(AethelItemsInventory.openCategoryPage(user, "get", itemName, pageRequest));
      user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
          new FixedMetadataValue(Plugin.getInstance(), InventoryListener.Inventory.AETHELITEMS_GET.inventory));
    }

    if (e.getSlot() != 3) {
      e.setCancelled(true);
    }
  }

  /**
   * Checks if the user's action is allowed based on the clicked inventory.
   * <p>
   * - AethelItems: Prevent adding new items to the inventory outside of the intended Save Item slot.
   * - Player: Prevent shift-clicks adding items to the AethelItems inventory.
   * </p>
   *
   * @param e      inventory click event
   * @param user   user
   * @param action type of interaction
   */
  public static void readCategoryClick(InventoryClickEvent e, Player user, String action) {
    Inventory clickedInv = e.getClickedInventory();
    if (clickedInv != null && !clickedInv.getType().equals(InventoryType.PLAYER)) {
      if (e.getCurrentItem() != null) {
        interpretCategoryClick(e, user, action);
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
   * @param user   user
   * @param action type of interaction
   */
  private static void interpretCategoryClick(InventoryClickEvent e, Player user, String action) {
    int slotClicked = e.getSlot();

    switch (slotClicked) {
      case 0 -> previousItemPage(user, action);
      case 2 -> { // Help Context
      }
      case 4 -> AethelItemsAction.readSaveClick(e, user);
      case 5 -> toggleAction(user, action);
      case 6 -> returnToMainMenu(user);
      case 8 -> nextItemPage(user, action);
      default -> interpretContextualClick(e, action, user);
    }

    if (e.getSlot() != 3) {
      e.setCancelled(true);
    }
  }

  /**
   * Opens the previous item page.
   *
   * @param user   user
   * @param action type of interaction
   */
  private static void previousItemPage(Player user, String action) {
    String categoryName = user.getMetadata(PluginPlayerMeta.Namespace.CATEGORY.namespace).get(0).asString();
    int pageRequest = user.getMetadata(PluginPlayerMeta.Namespace.PAGE.namespace).get(0).asInt();

    user.openInventory(AethelItemsInventory.openCategoryPage(user, action,
        categoryName, pageRequest - 1));
    user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(), "aethelitems." + action));
  }

  /**
   * Toggles between get and remove actions.
   *
   * @param user   user
   * @param action type of interaction
   */
  private static void toggleAction(Player user, String action) {
    String categoryName = user.getMetadata(PluginPlayerMeta.Namespace.CATEGORY.namespace).get(0).asString();
    int pageRequest = user.getMetadata(PluginPlayerMeta.Namespace.PAGE.namespace).get(0).asInt();

    if (action.equals("get")) {
      user.openInventory(AethelItemsInventory.openCategoryPage(user, "remove",
          categoryName, pageRequest));
      user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
          new FixedMetadataValue(Plugin.getInstance(), InventoryListener.Inventory.AETHELITEMS_REMOVE.inventory));
    } else {
      user.openInventory(AethelItemsInventory.openCategoryPage(user, "get",
          categoryName, pageRequest));
      user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
          new FixedMetadataValue(Plugin.getInstance(), InventoryListener.Inventory.AETHELITEMS_GET.inventory));
    }
  }

  /**
   * Opens an AethelItems inventory.
   *
   * @param user user
   */
  private static void returnToMainMenu(Player user) {
    user.openInventory(AethelItemsInventory.openMainMenu(user, "view"));
    user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(), InventoryListener.Inventory.AETHELITEMS_CATEGORY.inventory));
    user.setMetadata(PluginPlayerMeta.Namespace.PAGE.namespace, new FixedMetadataValue(Plugin.getInstance(), "0"));
  }

  /**
   * Opens the next item page.
   *
   * @param user   user
   * @param action type of interaction
   */
  private static void nextItemPage(Player user, String action) {
    String categoryName = user.getMetadata(PluginPlayerMeta.Namespace.CATEGORY.namespace).get(0).asString();
    int pageRequest = user.getMetadata(PluginPlayerMeta.Namespace.PAGE.namespace).get(0).asInt();

    user.openInventory(AethelItemsInventory.openCategoryPage(user, action,
        categoryName, pageRequest + 1));
    user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(), "aethelitems." + action));
  }

  /**
   * Either gets or remove an item.
   *
   * @param e      inventory click event
   * @param action interacting action
   * @param user   user
   */
  private static void interpretContextualClick(InventoryClickEvent e, String action, Player user) {
    switch (action) {
      case "get" -> AethelItemsAction.getItem(e, user);
      case "remove" -> AethelItemsAction.removeItem(e, user);
    }
  }
}
