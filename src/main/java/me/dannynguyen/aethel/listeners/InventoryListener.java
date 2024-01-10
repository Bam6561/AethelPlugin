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
 * @version 1.5.4
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
        case "aethelitem-category" -> AethelItemListener.readAethelItemMainClick(e, player);
        case "aethelitem-get" -> AethelItemListener.readAethelCategoryClick(e, player, "get");
        case "aethelitem-delete" -> AethelItemListener.readAethelCategoryClick(e, player, "delete");

        case "forge-category" -> ForgeListener.interpretForgeMainClickNew(e, player);
        case "forge-modify" -> ForgeListener.interpretForgeCategoryClickNew(e, player, "modify");

        case "forge-craft" -> ForgeListener.interpretForgeMainClick(e, player, "craft");
        case "forge-craft-confirm" -> ForgeListener.interpretForgeCraftConfirmClick(e, player);
        case "forge-save" -> ForgeListener.interpretForgeSaveClick(e, player);
        case "forge-delete" -> ForgeListener.interpretForgeMainClick(e, player, "delete");

        case "playerstat-category" -> PlayerStatListener.readPlayerStatMainClick(e, player);
        case "playerstat-past", "showitem-past" -> e.setCancelled(true);
        case "playerstat-stat" -> PlayerStatListener.readPlayerStatStatClick(e, player);
        case "playerstat-substat" -> PlayerStatListener.readPlayerStatSubstatClick(e, player);
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
