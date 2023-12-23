package me.dannynguyen.aethel.gui;

import me.dannynguyen.aethel.AethelPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * ForgeGUI is an inventory listener for the Forge command invocation.
 *
 * @author Danny Nguyen
 * @version 1.0.3
 * @since 1.0.2
 */
public class ForgeGUI implements Listener {
  @EventHandler
  public void onClick(InventoryClickEvent e) {
    Player player = (Player) e.getWhoClicked();
    if (player.hasMetadata("Menu")) {
      if (player.getMetadata("Menu").get(0).asString().equals("Forge-Craft")) {
        e.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onClose(InventoryCloseEvent e) {
    Player player = (Player) e.getPlayer();
    if (player.hasMetadata("Menu")) {
      if (player.getMetadata("Menu").get(0).asString().equals("Forge-Craft")) {
        player.removeMetadata("Menu", AethelPlugin.getInstance());
      }
    }
  }
}
