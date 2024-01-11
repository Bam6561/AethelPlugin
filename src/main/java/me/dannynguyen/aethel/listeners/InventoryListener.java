package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.AethelPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * InventoryListener is a general usage inventory action listener.
 *
 * @author Danny Nguyen
 * @version 1.6.1
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
      Bukkit.getLogger().warning(player.getMetadata("inventory").get(0).asString());
      String[] invType = player.getMetadata("inventory").get(0).asString().split("\\.");
      switch (invType[0]) {
        case "aethelitem" -> interpretAethelItemInventory(e, player, invType);
        case "forge" -> interpretForgeInventory(e, player, invType);
        case "playerstat" -> interpretPlayerStatInventory(e, player, invType);
        case "showitem" -> e.setCancelled(true);
      }
    }
  }

  private void interpretAethelItemInventory(InventoryClickEvent e, Player player, String[] invType) {
    switch (invType[1]) {
      case "category" -> AethelItemListener.readMainClick(e, player);
      case "delete" -> AethelItemListener.readCategoryClick(e, player, "delete");
      case "get" -> AethelItemListener.readCategoryClick(e, player, "get");
    }
  }

  private void interpretForgeInventory(InventoryClickEvent e, Player player, String[] invType) {
    switch (invType[1]) {
      case "category" -> ForgeListener.interpretMainClick(e, player);
      case "craft" -> ForgeListener.interpretCategoryClick(e, player, "craft");
      case "craft-confirm" -> ForgeListener.interpretCraftConfirmClick(e, player);
      case "delete" -> ForgeListener.interpretCategoryClick(e, player, "delete");
      case "modify" -> ForgeListener.interpretCategoryClick(e, player, "modify");
      case "save" -> ForgeListener.interpretSaveClick(e, player);
    }
  }

  private void interpretPlayerStatInventory(InventoryClickEvent e, Player player, String[] invType) {
    switch (invType[1]) {
      case "category" -> PlayerStatListener.readMainClick(e, player);
      case "past" -> e.setCancelled(true);
      case "stat" -> PlayerStatListener.readStatClick(e, player);
      case "substat" -> PlayerStatListener.readSubstatClick(e, player);
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
