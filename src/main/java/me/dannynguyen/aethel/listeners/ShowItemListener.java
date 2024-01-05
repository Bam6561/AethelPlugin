package me.dannynguyen.aethel.listeners;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

/**
 * ForgeListener is an inventory listener for the Forge command invocation.
 *
 * @author Danny Nguyen
 * @version 1.4.5
 * @since 1.4.5
 */
public class ShowItemListener {
  /**
   * Cancels clicks outside of the player's personal inventory.
   *
   * @param e inventory click event
   */
  public void readShowItemPastClick(InventoryClickEvent e) {
    Inventory clickedInv = e.getClickedInventory();
    if (clickedInv == null || !clickedInv.getType().equals(InventoryType.PLAYER)) {
      e.setCancelled(true);
    }
  }
}
