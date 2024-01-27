package me.dannynguyen.aethel.listeners.inventory;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.commands.aethelItems.AethelItemsInventoryListener;
import me.dannynguyen.aethel.listeners.inventory.itemeditor.ItemEditorInventoryFunctional;
import me.dannynguyen.aethel.listeners.inventory.itemeditor.ItemEditorInventoryMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * InventoryListener is a general usage inventory action listener.
 *
 * @author Danny Nguyen
 * @version 1.7.10
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
      String[] invType = player.getMetadata("inventory").get(0).asString().split("\\.");
      switch (invType[0]) {
        case "aethelitems" -> interpretAethelItems(e, player, invType);
        case "character" -> interpretCharacter(e, player, invType);
        case "forge" -> interpretForge(e, player, invType);
        case "itemeditor" -> interpretItemEditor(e, player, invType);
        case "playerstats" -> interpretPlayerStats(e, player, invType);
        case "showitem" -> e.setCancelled(true);
      }
    }
  }

  /**
   * Determines which AethelItem inventory is being interacting with.
   *
   * @param e       inventory click event
   * @param player  interacting player
   * @param invType inventory type
   */
  private void interpretAethelItems(InventoryClickEvent e, Player player, String[] invType) {
    switch (invType[1]) {
      case "category" -> AethelItemsInventoryListener.readMainClick(e, player);
      case "get" -> AethelItemsInventoryListener.readCategoryClick(e, player, "get");
      case "remove" -> AethelItemsInventoryListener.readCategoryClick(e, player, "remove");
    }
  }

  /**
   * Determines which Character inventory is being interacting with.
   *
   * @param e       inventory click event
   * @param player  interacting player
   * @param invType inventory type
   */
  private void interpretCharacter(InventoryClickEvent e, Player player, String[] invType) {
    switch (invType[1]) {
      case "sheet" -> e.setCancelled(true);
    }
  }

  /**
   * Determines which Forge inventory is being interacting with.
   *
   * @param e       inventory click event
   * @param player  interacting player
   * @param invType inventory type
   */
  private void interpretForge(InventoryClickEvent e, Player player, String[] invType) {
    switch (invType[1]) {
      case "category" -> ForgeInventory.interpretMainClick(e, player);
      case "craft" -> ForgeInventory.interpretCategoryClick(e, player, "craft");
      case "craft-confirm" -> ForgeInventory.interpretCraftConfirmClick(e, player);
      case "edit" -> ForgeInventory.interpretCategoryClick(e, player, "edit");
      case "remove" -> ForgeInventory.interpretCategoryClick(e, player, "remove");
      case "save" -> ForgeInventory.interpretSaveClick(e, player);
    }
  }

  /**
   * Determines which ItemEditor inventory is being interacting with.
   *
   * @param e       inventory click event
   * @param player  interacting player
   * @param invType inventory type
   */
  private void interpretItemEditor(InventoryClickEvent e, Player player, String[] invType) {
    switch (invType[1]) {
      case "menu" -> ItemEditorInventoryMenu.interpretMenuClick(e, player);
      case "attributes" -> ItemEditorInventoryFunctional.interpretAttributesClick(e, player);
      case "enchants" -> ItemEditorInventoryFunctional.interpretEnchantsClick(e, player);
      case "tags" -> ItemEditorInventoryFunctional.interpretTagsClick(e, player);
    }
  }

  /**
   * Determines which PlayerStats inventory is being interacting with.
   *
   * @param e       inventory click event
   * @param player  interacting player
   * @param invType inventory type
   */
  private void interpretPlayerStats(InventoryClickEvent e, Player player, String[] invType) {
    switch (invType[1]) {
      case "category" -> PlayerStatsInventory.readMainClick(e, player);
      case "past" -> e.setCancelled(true);
      case "stat" -> PlayerStatsInventory.readStatClick(e, player);
      case "substat" -> PlayerStatsInventory.readSubstatClick(e, player);
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
      player.removeMetadata("inventory", Plugin.getInstance());
    }
  }
}
