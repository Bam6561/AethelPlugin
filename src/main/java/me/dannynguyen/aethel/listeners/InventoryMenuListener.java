package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.commands.aethelItems.AethelItemsInventoryListener;
import me.dannynguyen.aethel.commands.forge.ForgeInventoryListener;
import me.dannynguyen.aethel.commands.itemeditor.ItemEditorInventoryListener;
import me.dannynguyen.aethel.commands.playerstats.PlayerStatsInventoryListener;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * InventoryMenuListener is an inventory click listener for the plugin's menus.
 *
 * @author Danny Nguyen
 * @version 1.8.7
 * @since 1.0.2
 */
public class InventoryMenuListener implements Listener {
  /**
   * Routes interactions between inventories.
   *
   * @param e inventory click event
   */
  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    Player user = (Player) e.getWhoClicked();
    if (user.hasMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace)) {
      String[] invType = user.getMetadata(
          PluginPlayerMeta.Namespace.INVENTORY.namespace).get(0).asString().split("\\.");
      switch (invType[0]) {
        case "aethelitems" -> interpretAethelItems(e, user, invType);
        case "character" -> interpretCharacter(e, user, invType);
        case "forge" -> interpretForge(e, user, invType);
        case "itemeditor" -> interpretItemEditor(e, user, invType);
        case "playerstats" -> interpretPlayerStats(e, user, invType);
        case "showitem" -> e.setCancelled(true);
      }
    }
  }

  /**
   * Determines which AethelItem inventory is being interacting with.
   *
   * @param e       inventory click event
   * @param user    user
   * @param invType inventory type
   */
  private void interpretAethelItems(InventoryClickEvent e, Player user, String[] invType) {
    switch (invType[1]) {
      case "category" -> AethelItemsInventoryListener.readMainClick(e, user);
      case "get" -> AethelItemsInventoryListener.readCategoryClick(e, user, "get");
      case "remove" -> AethelItemsInventoryListener.readCategoryClick(e, user, "remove");
    }
  }

  /**
   * Determines which Character inventory is being interacting with.
   *
   * @param e       inventory click event
   * @param user    user
   * @param invType inventory type
   */
  private void interpretCharacter(InventoryClickEvent e, Player user, String[] invType) {
    switch (invType[1]) {
      case "sheet" -> e.setCancelled(true);
    }
  }

  /**
   * Determines which Forge inventory is being interacting with.
   *
   * @param e       inventory click event
   * @param user    user
   * @param invType inventory type
   */
  private void interpretForge(InventoryClickEvent e, Player user, String[] invType) {
    switch (invType[1]) {
      case "category" -> ForgeInventoryListener.interpretMainClick(e, user);
      case "craft" -> ForgeInventoryListener.interpretCategoryClick(e, user, "craft");
      case "craft-confirm" -> ForgeInventoryListener.interpretCraftConfirmClick(e, user);
      case "edit" -> ForgeInventoryListener.interpretCategoryClick(e, user, "edit");
      case "remove" -> ForgeInventoryListener.interpretCategoryClick(e, user, "remove");
      case "save" -> ForgeInventoryListener.interpretSaveClick(e, user);
    }
  }

  /**
   * Determines which ItemEditor inventory is being interacting with.
   *
   * @param e       inventory click event
   * @param user    user
   * @param invType inventory type
   */
  private void interpretItemEditor(InventoryClickEvent e, Player user, String[] invType) {
    switch (invType[1]) {
      case "cosmetics" -> ItemEditorInventoryListener.interpretMainMenuClick(e, user);
      case "attributes" -> ItemEditorInventoryListener.interpretAttributesMenuClick(e, user);
      case "enchants" -> ItemEditorInventoryListener.interpretEnchantsMenuClick(e, user);
      case "tags" -> ItemEditorInventoryListener.interpretTagsMenuClick(e, user);
    }
  }

  /**
   * Determines which PlayerStats inventory is being interacting with.
   *
   * @param e       inventory click event
   * @param user    user
   * @param invType inventory type
   */
  private void interpretPlayerStats(InventoryClickEvent e, Player user, String[] invType) {
    switch (invType[1]) {
      case "category" -> PlayerStatsInventoryListener.readMainClick(e, user);
      case "past" -> e.setCancelled(true);
      case "stat" -> PlayerStatsInventoryListener.readStatClick(e, user);
      case "substat" -> PlayerStatsInventoryListener.readSubstatClick(e, user);
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
    if (player.hasMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace)) {
      player.removeMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace, Plugin.getInstance());
    }
  }

  /**
   * Currently open inventory.
   */
  public enum Inventory {
    AETHELITEMS_CATEGORY("aethelitems.category"),
    AETHELITEMS_GET("aethelitems.get"),
    AETHELITEMS_REMOVE("aethelitems.remove"),
    CHARACTER_SHEET("character.sheet"),
    FORGE_CATEGORY("forge.category"),
    FORGE_CRAFT("forge.craft"),
    FORGE_CRAFT_CONFIRM("forge.craft-confirm"),
    FORGE_EDIT("forge.edit"),
    FORGE_REMOVE("forge.remove"),
    FORGE_SAVE("forge.save"),
    ITEMEDITOR_ATTRIBUTES("itemeditor.attributes"),
    ITEMEDITOR_COSMETICS("itemeditor.cosmetics"),
    ITEMEDITOR_ENCHANTS("itemeditor.enchants"),
    ITEMEDITOR_TAGS("itemeditor.tags"),
    PLAYERSTATS_CATEGORY("playerstats.category"),
    PLAYERSTATS_PAST("playerstats.past"),
    PLAYERSTATS_STAT("playerstats.stat"),
    PLAYERSTATS_SUBSTAT("playerstats.substat"),
    SHOWITEM_PAST("showitem.past");

    public final String inventory;

    Inventory(String inventory) {
      this.inventory = inventory;
    }
  }
}
