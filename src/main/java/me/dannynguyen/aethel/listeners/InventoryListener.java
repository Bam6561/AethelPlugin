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
 * @version 1.1.3
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
        case "forge-craft" -> new ForgeListener().interpretForgeMainClick(e, "craft");
        case "forge-create" -> new ForgeListener().interpretForgeCreateClick(e);
        case "forge-modify" -> new ForgeListener().interpretForgeMainClick(e, "modify");
        case "forge-delete" -> new ForgeListener().interpretForgeMainClick(e, "delete");
      }
    }
  }

  /**
   * Removes player metadata pertaining to any open inventories.
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
