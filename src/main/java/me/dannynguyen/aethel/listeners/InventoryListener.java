package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.AethelPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * InventoryListener is a general usage inventory listener.
 *
 * @author Danny Nguyen
 * @version 1.4.11
 * @since 1.0.2
 */
public class InventoryListener implements Listener {
  /**
   * Routes interactions between inventories.
   *
   * @param e inventory click event
   */
  @EventHandler
  public void onClick(InventoryClickEvent e) {
    Player player = (Player) e.getWhoClicked();
    if (player.hasMetadata("inventory")) {
      String inventory = player.getMetadata("inventory").get(0).asString();
      switch (inventory) {
        case "aethelitem-get" -> new AethelItemListener().readAethelItemMainClick(e, "get");
        case "aethelitem-delete" -> new AethelItemListener().readAethelItemMainClick(e, "delete");
        case "forge-craft" -> new ForgeListener().interpretForgeMainClick(e, "craft");
        case "forge-craft-confirm" -> new ForgeListener().interpretForgeCraftConfirmClick(e, player);
        case "forge-create" -> new ForgeListener().interpretForgeCreateClick(e, player);
        case "forge-modify" -> new ForgeListener().interpretForgeMainClick(e, "modify");
        case "forge-delete" -> new ForgeListener().interpretForgeMainClick(e, "delete");
        case "playerstat-category" -> new PlayerStatListener().readPlayerStatCategoryClick(e, player);
        case "playerstat-past", "showitem-past" -> e.setCancelled(true);
        case "playerstat-substat" -> new PlayerStatListener().readPlayerStatSubstatClick(e, player);
        case "playerstat-stat" -> new PlayerStatListener().readPlayerStatStatClick(e, player);
      }
    }
  }

  /**
   * Removes player inventory metadata when an inventory is closed.
   * <p>
   * Since opening a new inventory while one already exists triggers
   * the InventoryCloseEvent, always add new inventory metadata AFTER
   * opening an inventory and not before, as it will be removed otherwise.
   * </p>
   *
   * @param e inventory close event
   */
  @EventHandler
  public void onClose(InventoryCloseEvent e) {
    Player player = (Player) e.getPlayer();
    if (player.hasMetadata("inventory")) {
      player.removeMetadata("inventory", AethelPlugin.getInstance());
    }
  }
}
