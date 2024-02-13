package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.commands.aethelitem.ItemMenuAction;
import me.dannynguyen.aethel.commands.aethelitem.ItemMenuClick;
import me.dannynguyen.aethel.commands.character.CharacterMenuClick;
import me.dannynguyen.aethel.commands.forge.ForgeMenuAction;
import me.dannynguyen.aethel.commands.forge.ForgeMenuClick;
import me.dannynguyen.aethel.commands.itemeditor.ItemEditorMenuClick;
import me.dannynguyen.aethel.commands.playerstat.PlayerStatMenuClick;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;

/**
 * InventoryMenuListener is an inventory click listener for the plugin's menus.
 *
 * @author Danny Nguyen
 * @version 1.9.17
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
    if (e.getClickedInventory() != null) {
      Player user = (Player) e.getWhoClicked();
      if (user.hasMetadata(PluginPlayerMeta.INVENTORY.getMeta())) {
        String[] invType = user.getMetadata(PluginPlayerMeta.INVENTORY.getMeta()).get(0).asString().split("\\.");
        switch (invType[0]) {
          case "aethelitem" -> interpretAethelItem(e, invType);
          case "character" -> interpretCharacter(e, invType);
          case "forge" -> interpretForge(e, invType);
          case "itemeditor" -> interpretItemEditor(e, invType);
          case "playerstats" -> interpretPlayerStat(e, invType);
          case "showitem" -> e.setCancelled(true);
        }
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
      String[] invType = user.getMetadata(PluginPlayerMeta.INVENTORY.getMeta()).get(0).asString().split("\\.");
      switch (invType[0]) {
        case "aethelitem", "character" -> e.setCancelled(true);
      }
    }
  }

  /**
   * Determines which AethelItem menu is being interacting with.
   * <p>
   * - AethelItem: prevent adding new items to the menu outside of the intended Save Item slot
   * - Player: prevent shift-clicks adding items to the AethelItem menu
   * </p>
   *
   * @param e       inventory click event
   * @param invType inventory type
   */
  private void interpretAethelItem(InventoryClickEvent e, String[] invType) {
    if (e.getClickedInventory().getType().equals(InventoryType.CHEST)) {
      int slot = e.getSlot();
      if (ItemReader.isNotNullOrAir(e.getCurrentItem())) {
        ItemMenuClick click = new ItemMenuClick(e);
        switch (invType[1]) {
          case "category" -> click.interpretMainMenuClick();
          case "get" -> click.interpretCategoryClick(ItemMenuAction.GET);
          case "remove" -> click.interpretCategoryClick(ItemMenuAction.REMOVE);
        }
      }
      if (slot != 3) {
        e.setCancelled(true);
      }
    } else if (e.getClick().isShiftClick()) {
      e.setCancelled(true);
    }
  }

  /**
   * Determines which Character menu is being interacting with.
   * <p>
   * - Character: prevent adding new items to the menu outside of the intended equipment slots.
   * - Player: prevent shift-clicks adding items to the Character menu and
   * remove the main hand item from the menu whenever the user clicks on it
   * </p>
   *
   * @param e       inventory click event
   * @param invType inventory type
   */
  private void interpretCharacter(InventoryClickEvent e, String[] invType) {
    // Prevents item duplication since copies of the user's items are stored inside the menu
    if (!e.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
      CharacterMenuClick click = new CharacterMenuClick(e);
      if (e.getClickedInventory().getType().equals(InventoryType.CHEST)) {
        switch (invType[1]) {
          case "sheet" -> click.interpretCharacterSheetClick();
        }
      } else {
        click.interpretPlayerInventoryClick();
      }
    }
  }

  /**
   * Determines which Forge menu is being interacting with.
   *
   * @param e       inventory click event
   * @param invType inventory type
   */
  private void interpretForge(InventoryClickEvent e, String[] invType) {
    switch (invType[1]) {
      case "category", "craft", "craft-confirm", "edit", "remove" -> {
        if (ItemReader.isNotNullOrAir(e.getCurrentItem()) && e.getClickedInventory().getType().equals(InventoryType.CHEST)) {
          ForgeMenuClick click = new ForgeMenuClick(e);
          switch (invType[1]) {
            case "category" -> click.interpretMainMenuClick();
            case "craft" -> click.interpretCategoryClick(ForgeMenuAction.CRAFT);
            case "craft-confirm" -> click.interpretCraftConfirmClick();
            case "edit" -> click.interpretCategoryClick(ForgeMenuAction.EDIT);
            case "remove" -> click.interpretCategoryClick(ForgeMenuAction.REMOVE);
          }
        }
        e.setCancelled(true);
      }
      case "save" -> new ForgeMenuClick(e).interpretSaveClick();
    }
  }

  /**
   * Determines which ItemEditor inventory is being interacting with.
   *
   * @param e       inventory click event
   * @param invType inventory type
   */
  private void interpretItemEditor(InventoryClickEvent e, String[] invType) {
    if (ItemReader.isNotNullOrAir(e.getCurrentItem()) && e.getClickedInventory().getType().equals(InventoryType.CHEST)) {
      ItemEditorMenuClick click = new ItemEditorMenuClick(e);
      switch (invType[1]) {
        case "cosmetics" -> click.interpretMainMenuClick();
        case "attributes" -> click.interpretAttributesMenuClick();
        case "enchants" -> click.interpretEnchantsMenuClick();
        case "tags" -> click.interpretTagsMenuClick();
      }
    }
    e.setCancelled(true);
  }

  /**
   * Determines which PlayerStats inventory is being interacting with.
   *
   * @param e       inventory click event
   * @param invType inventory type
   */
  private void interpretPlayerStat(InventoryClickEvent e, String[] invType) {
    if (ItemReader.isNotNullOrAir(e.getCurrentItem()) && e.getClickedInventory().getType().equals(InventoryType.CHEST)) {
      PlayerStatMenuClick click = new PlayerStatMenuClick(e);
      switch (invType[1]) {
        case "category" -> click.readMainClick();
        case "past" -> e.setCancelled(true);
        case "stat" -> click.readStatClick();
        case "substat" -> click.readSubstatClick();
      }
    }
    e.setCancelled(true);
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
    AETHELITEM_CATEGORY("aethelitem.category"),
    AETHELITEM_GET("aethelitem.get"),
    AETHELITEM_REMOVE("aethelitem.remove"),
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
    PLAYERSTAT_CATEGORY("playerstats.category"),
    PLAYERSTAT_PAST("playerstats.past"),
    PLAYERSTAT_STAT("playerstats.stat"),
    PLAYERSTAT_SUBSTAT("playerstats.substat"),
    SHOWITEM_PAST("showitem.past");

    public final String menu;

    Menu(String menu) {
      this.menu = menu;
    }
  }
}
