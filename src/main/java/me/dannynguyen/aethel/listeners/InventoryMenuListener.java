package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.commands.aethelitem.AethelItemAction;
import me.dannynguyen.aethel.commands.aethelitem.AethelItemListener;
import me.dannynguyen.aethel.commands.aethelitem.AethelItemMenu;
import me.dannynguyen.aethel.commands.character.CharacterInventoryListener;
import me.dannynguyen.aethel.commands.forge.ForgeInventoryListener;
import me.dannynguyen.aethel.commands.itemeditor.ItemEditorInventoryListener;
import me.dannynguyen.aethel.commands.playerstats.PlayerStatsInventoryListener;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;

/**
 * InventoryMenuListener is an inventory click listener for the plugin's menus.
 *
 * @author Danny Nguyen
 * @version 1.9.8
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
    if (user.hasMetadata(PluginPlayerMeta.INVENTORY.getMeta())) {
      String[] invType = user.getMetadata(
          PluginPlayerMeta.INVENTORY.getMeta()).get(0).asString().split("\\.");
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
   * Disables drag clicks while inside specific plugin menus.
   *
   * @param e inventory drag event
   */
  @EventHandler
  public void onDrag(InventoryDragEvent e) {
    Player user = (Player) e.getWhoClicked();
    if (user.hasMetadata(PluginPlayerMeta.INVENTORY.getMeta())) {
      String[] invType = user.getMetadata(
          PluginPlayerMeta.INVENTORY.getMeta()).get(0).asString().split("\\.");
      switch (invType[0]) {
        case "aethelitems", "character" -> e.setCancelled(true);
      }
    }
  }

  /**
   * Determines which AethelItem inventory is being interacting with.
   * <p>
   * - AethelItem: Prevent adding new items to the inventory outside of the intended Save Item slot.
   * - Player: Prevent shift-clicks adding items to the AethelItem inventory.
   * </p>
   *
   * @param e       inventory click event
   * @param user    user
   * @param invType inventory type
   */
  private void interpretAethelItems(InventoryClickEvent e, Player user, String[] invType) {
    org.bukkit.inventory.Inventory clickedInv = e.getClickedInventory();
    if (clickedInv != null) {
      if (clickedInv.getType().equals(InventoryType.CHEST)) {
        if (ItemReader.isNotNullOrAir(e.getCurrentItem())) {
          AethelItemListener listener = new AethelItemListener(e, user);
          switch (invType[1]) {
            case "category" -> listener.interpretMainMenuClick();
            case "get" -> listener.interpretCategoryClick(AethelItemAction.GET);
            case "remove" -> listener.interpretCategoryClick(AethelItemAction.REMOVE);
          }
        }
        if (e.getSlot() != 3) {
          e.setCancelled(true);
        }
      } else if (e.getClick().isShiftClick()) {
        e.setCancelled(true);
      }
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
      case "sheet" -> CharacterInventoryListener.readMainClick(e, user);
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
    if (player.hasMetadata(PluginPlayerMeta.INVENTORY.getMeta())) {
      player.removeMetadata(PluginPlayerMeta.INVENTORY.getMeta(), Plugin.getInstance());
    }
  }

  /**
   * Currently open menu.
   */
  public enum Menu {
    AETHELITEM_CATEGORY("aethelitems.category"),
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

    public final String menu;

    Menu(String menu) {
      this.menu = menu;
    }
  }
}
