package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.inventories.aethelItem.AethelItemCreate;
import me.dannynguyen.aethel.inventories.aethelItem.AethelItemDelete;
import me.dannynguyen.aethel.inventories.aethelItem.AethelItemGet;
import me.dannynguyen.aethel.inventories.aethelItem.AethelItemMain;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * AethelItemListener is an inventory listener for the AethelItem command invocation.
 *
 * @author Danny Nguyen
 * @version 1.4.2
 * @since 1.4.0
 */
public class AethelItemListener {
  /**
   * Checks if the player's action is allowed based on the clicked inventory.
   * <p>
   * - AethelItem: Prevent adding new items to the inventory outside of the intended Save Item slot.
   * - Player: Prevent shift-clicks adding items to the AethelItem inventory.
   * </p>
   *
   * @param e      inventory click event
   * @param action type of interaction
   */
  public void readAethelItemMainClick(InventoryClickEvent e, String action) {
    if (!e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      if (e.getCurrentItem() != null) {
        interpretAethelItemMainClick(e, action);
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
   * - contextualizes the click to get or delete items
   *
   * @param e      inventory click event
   * @param action type of interaction
   */
  private void interpretAethelItemMainClick(InventoryClickEvent e, String action) {
    Player player = (Player) e.getWhoClicked();
    int slotClicked = e.getSlot();

    switch (slotClicked) {
      case 0 -> previousItemPage(player, action);
      case 2 -> { // Help Context
        break;
      }
      case 4 -> new AethelItemCreate().readSaveClick(e, player);
      case 5 -> toggleGetDeleteAction(player, action);
      case 8 -> nextItemPage(player, action);
      default -> interpretContextualClick(e, action, player);
    }

    if (e.getSlot() != 3) e.setCancelled(true);
  }

  /**
   * Opens the previous item page.
   *
   * @param player interacting player
   * @param action type of interaction
   */
  private void previousItemPage(Player player, String action) {
    int pageRequest = player.getMetadata("page").get(0).asInt();
    player.openInventory(new AethelItemMain().openItemPage(player, action, pageRequest - 1));
    player.setMetadata("inventory",
        new FixedMetadataValue(AethelPlugin.getInstance(), "aethelitem-" + action));
  }

  /**
   * Toggles between AethelItem Get and Delete actions.
   *
   * @param player interacting player
   * @param action type of interaction
   */
  private void toggleGetDeleteAction(Player player, String action) {
    int pageRequest = player.getMetadata("page").get(0).asInt();

    if (action.equals("get")) {
      player.openInventory(new AethelItemMain().openItemPage(player, "delete", pageRequest));
      player.setMetadata("inventory",
          new FixedMetadataValue(AethelPlugin.getInstance(), "aethelitem-delete"));
    } else {
      player.openInventory(new AethelItemMain().openItemPage(player, "get", pageRequest));
      player.setMetadata("inventory",
          new FixedMetadataValue(AethelPlugin.getInstance(), "aethelitem-get"));
    }
  }

  /**
   * Opens the next item page.
   *
   * @param player interacting player
   * @param action type of interaction
   */
  private void nextItemPage(Player player, String action) {
    int pageRequest = player.getMetadata("page").get(0).asInt();
    player.openInventory(new AethelItemMain().openItemPage(player, action, pageRequest + 1));
    player.setMetadata("inventory",
        new FixedMetadataValue(AethelPlugin.getInstance(), "aethelitem-" + action));
  }

  /**
   * Either gets or deletes an item.
   *
   * @param e      inventory click event
   * @param action interacting action
   * @param player interacting player
   */
  private void interpretContextualClick(InventoryClickEvent e, String action, Player player) {
    switch (action) {
      case "get" -> new AethelItemGet().getItem(e, player);
      case "delete" -> new AethelItemDelete().deleteItem(e, player);
    }
  }
}
