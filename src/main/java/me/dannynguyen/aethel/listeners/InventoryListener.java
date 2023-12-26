package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.inventories.forge.ForgeCreate;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * InventoryListener is a general usage inventory listener.
 *
 * @author Danny Nguyen
 * @version 1.0.9
 * @since 1.0.2
 */
public class InventoryListener implements Listener {
  /**
   * Routes interactions between menus.
   *
   * @param e inventory click event
   */
  @EventHandler
  public void onClick(InventoryClickEvent e) {
    Player player = (Player) e.getWhoClicked();
    if (player.hasMetadata("menu")) {
      String menuType = player.getMetadata("menu").get(0).asString();
      switch (menuType) {
        case "forge-craft" -> new ForgeListener().interpretMainClick(e, "forge-craft");
        case "forge-create" -> {
          if (e.getSlot() == 26) new ForgeCreate().processSaveClick(e);
        }
        case "forge-modify" -> new ForgeListener().interpretMainClick(e, "forge-modify");
      }
    }
  }

  /**
   * Removes player metadata pertaining to any open menus.
   *
   * @param e inventory close event
   */
  @EventHandler
  public void onClose(InventoryCloseEvent e) {
    Player player = (Player) e.getPlayer();
    if (player.hasMetadata("menu")) {
      player.removeMetadata("menu", AethelPlugin.getInstance());
    }
  }
}
