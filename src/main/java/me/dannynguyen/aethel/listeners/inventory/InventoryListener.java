package me.dannynguyen.aethel.listeners.inventory;

import me.dannynguyen.aethel.AethelPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * InventoryListener is a general usage inventory action listener.
 *
 * @author Danny Nguyen
 * @version 1.6.8
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
        case "aethelitem" -> interpretAethelItem(e, player, invType);
        case "characterprofile" -> interpretCharacterProfile(e, player, invType);
        case "forge" -> interpretForge(e, player, invType);
        case "itemeditor" -> interpretItemEditor(e, player, invType);
        case "playerstat" -> interpretPlayerStat(e, player, invType);
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
  private void interpretAethelItem(InventoryClickEvent e, Player player, String[] invType) {
    switch (invType[1]) {
      case "category" -> AethelItemListener.readMainClick(e, player);
      case "get" -> AethelItemListener.readCategoryClick(e, player, "get");
      case "remove" -> AethelItemListener.readCategoryClick(e, player, "remove");
    }
  }

  /**
   * Determines which CharacterProfile inventory is being interacting with.
   *
   * @param e       inventory click event
   * @param player  interacting player
   * @param invType inventory type
   */
  private void interpretCharacterProfile(InventoryClickEvent e, Player player, String[] invType) {
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
      case "category" -> ForgeListener.interpretMainClick(e, player);
      case "craft" -> ForgeListener.interpretCategoryClick(e, player, "craft");
      case "craft-confirm" -> ForgeListener.interpretCraftConfirmClick(e, player);
      case "edit" -> ForgeListener.interpretCategoryClick(e, player, "edit");
      case "remove" -> ForgeListener.interpretCategoryClick(e, player, "remove");
      case "save" -> ForgeListener.interpretSaveClick(e, player);
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
      case "menu" -> ItemEditorInventoryListener.interpretMenuClick(e, player);
    }
  }

  /**
   * Determines which PlayerStat inventory is being interacting with.
   *
   * @param e       inventory click event
   * @param player  interacting player
   * @param invType inventory type
   */
  private void interpretPlayerStat(InventoryClickEvent e, Player player, String[] invType) {
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
