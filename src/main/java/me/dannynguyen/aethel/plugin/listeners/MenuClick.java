package me.dannynguyen.aethel.plugin.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.commands.aethelitem.ItemMenu;
import me.dannynguyen.aethel.commands.aethelitem.ItemMenuClick;
import me.dannynguyen.aethel.commands.character.CharacterMenuClick;
import me.dannynguyen.aethel.commands.forge.ForgeMenuClick;
import me.dannynguyen.aethel.commands.forge.RecipeMenu;
import me.dannynguyen.aethel.commands.itemeditor.ItemEditorMenuClick;
import me.dannynguyen.aethel.commands.playerstat.PlayerStatMenuClick;
import me.dannynguyen.aethel.plugin.enums.PlayerMeta;
import me.dannynguyen.aethel.util.item.ItemReader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;

import java.util.Map;

/**
 * Collection of inventory click listeners for the plugin's menus.
 * <p>
 * By default, all clicks within plugin menus are cancelled due to the possibility of an
 * internal error occurring and the associated methods never reaching their end result.
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.17.6
 * @since 1.0.2
 */
public class MenuClick implements Listener {
  /**
   * No parameter constructor.
   */
  public MenuClick() {
  }

  /**
   * Routes interactions between inventories.
   *
   * @param e inventory click event
   */
  @EventHandler
  private void onClick(InventoryClickEvent e) {
    if (e.getClickedInventory() != null) {
      Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(e.getWhoClicked().getUniqueId());
      if (playerMeta.containsKey(PlayerMeta.INVENTORY)) {
        e.setCancelled(true);
        if (e.getAction() != InventoryAction.COLLECT_TO_CURSOR) { // Prevents item duplication
          String[] invType = playerMeta.get(PlayerMeta.INVENTORY).split("\\.");
          switch (invType[0]) {
            case "aethelitem" -> interpretAethelItem(e, invType);
            case "character" -> interpretCharacter(e, invType);
            case "forge" -> interpretForge(e, invType);
            case "itemeditor" -> interpretItemEditor(e, invType);
            case "playerstat" -> interpretPlayerStat(e, invType);
            case "showitem" -> interpretShowItem(e, invType);
          }
        }
      }
    }
  }

  /**
   * Enables drag clicks while inside specific menus.
   *
   * @param e inventory drag event
   */
  @EventHandler
  private void onDrag(InventoryDragEvent e) {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(e.getWhoClicked().getUniqueId());
    if (playerMeta.containsKey(PlayerMeta.INVENTORY)) {
      e.setCancelled(true);
      String[] invType = playerMeta.get(PlayerMeta.INVENTORY).split("\\.");
      switch (invType[0]) {
        default -> e.setCancelled(true);
      }
    }
  }

  /**
   * Determines which AethelItem menu is being interacting with.
   *
   * @param e       inventory click event
   * @param invType inventory type
   */
  private void interpretAethelItem(InventoryClickEvent e, String[] invType) {
    if (e.getClickedInventory().getType() == InventoryType.CHEST) {
      if (ItemReader.isNotNullOrAir(e.getCurrentItem())) {
        ItemMenuClick click = new ItemMenuClick(e);
        switch (invType[1]) {
          case "category" -> click.interpretMenuClick();
          case "get" -> click.interpretCategoryClick(ItemMenu.Action.GET);
          case "remove" -> click.interpretCategoryClick(ItemMenu.Action.REMOVE);
        }
      } else {
        if (e.getSlot() == 3) {
          e.setCancelled(false);
        }
      }
    } else {
      if (!e.isShiftClick()) {
        e.setCancelled(false);
      }
    }
  }

  /**
   * Determines which Character menu is being interacting with.
   *
   * @param e       inventory click event
   * @param invType inventory type
   */
  private void interpretCharacter(InventoryClickEvent e, String[] invType) {
    if (Plugin.getData().getPluginSystem().getPlayerMetadata().get(e.getWhoClicked().getUniqueId()).get(PlayerMeta.PLAYER).equals(e.getWhoClicked().getName())) {
      CharacterMenuClick click = new CharacterMenuClick(e);
      if (e.getClickedInventory().getType() == InventoryType.CHEST) {
        switch (invType[1]) {
          case "sheet" -> click.interpretMenuClick();
          case "quests" -> click.interpretQuestsClick();
          case "collectibles" -> click.interpretCollectiblesClick();
          case "settings" -> click.interpretSettingsClick();
        }
      } else {
        if (invType[1].equals("sheet") && !e.isShiftClick()) {
          e.setCancelled(false);
          click.interpretPlayerInventoryClick();
        }
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
    if (e.getClickedInventory().getType() == InventoryType.CHEST) {
      if (ItemReader.isNotNullOrAir(e.getCurrentItem())) {
        ForgeMenuClick click = new ForgeMenuClick(e);
        switch (invType[1]) {
          case "category" -> click.interpretMenuClick();
          case "craft" -> click.interpretCategoryClick(RecipeMenu.Action.CRAFT);
          case "craft-recipe" -> click.interpretCraftDetailsClick();
          case "edit" -> click.interpretCategoryClick(RecipeMenu.Action.EDIT);
          case "remove" -> click.interpretCategoryClick(RecipeMenu.Action.REMOVE);
          case "save" -> click.interpretSaveClick();
        }
      } else if (invType[1].equals("save")) {
        new ForgeMenuClick(e).interpretSaveClick();
      }
    } else {
      if ((e.isShiftClick() && invType[1].equals("save")) || !e.isShiftClick()) {
        e.setCancelled(false);
      }
    }
  }

  /**
   * Determines which ItemEditor menu is being interacting with.
   *
   * @param e       inventory click event
   * @param invType inventory type
   */
  private void interpretItemEditor(InventoryClickEvent e, String[] invType) {
    if (e.getClickedInventory().getType() == InventoryType.CHEST) {
      if (ItemReader.isNotNullOrAir(e.getCurrentItem())) {
        ItemEditorMenuClick click = new ItemEditorMenuClick(e);
        switch (invType[1]) {
          case "cosmetic" -> click.interpretMenuClick();
          case "minecraft_attribute" -> click.interpretAttributeClick();
          case "aethel_attribute" -> click.interpretAethelAttributeClick();
          case "enchantment" -> click.interpretEnchantmentClick();
          case "potion" -> click.interpretPotionClick();
          case "passive" -> click.interpretPassiveClick();
          case "active" -> click.interpretActiveClick();
          case "tag" -> click.interpretTagClick();
        }
      }
    } else {
      if (!e.isShiftClick()) {
        e.setCancelled(true);
      }
    }
  }

  /**
   * Determines which PlayerStats menu is being interacting with.
   *
   * @param e       inventory click event
   * @param invType inventory type
   */
  private void interpretPlayerStat(InventoryClickEvent e, String[] invType) {
    if (e.getClickedInventory().getType() == InventoryType.CHEST) {
      if (ItemReader.isNotNullOrAir(e.getCurrentItem())) {
        PlayerStatMenuClick click = new PlayerStatMenuClick(e);
        switch (invType[1]) {
          case "category" -> click.interpretMenuClick();
          case "stat" -> click.interpretStatClick();
          case "substat" -> click.interpretSubstatClick();
          case "past" -> doNothing();
        }
      }
    } else {
      if (!e.isShiftClick()) {
        e.setCancelled(false);
      }
    }
  }

  /**
   * Determines which ShowItem menu is being interacting with.
   *
   * @param e       inventory click event
   * @param invType inventory type
   */
  private void interpretShowItem(InventoryClickEvent e, String[] invType) {
    if (e.getClickedInventory().getType() == InventoryType.CHEST) {
      switch (invType[1]) {
        case "past" -> doNothing();
      }
    } else {
      if (!e.isShiftClick()) {
        e.setCancelled(false);
      }
    }
  }

  /**
   * Removes player menu metadata when a menu is closed.
   * <p>
   * Since opening a new inventory while one already exists triggers
   * the InventoryCloseEvent, always add new inventory metadata AFTER
   * opening an inventory and not before, as it will be removed otherwise.
   * </p>
   *
   * @param e inventory close event
   */
  @EventHandler
  private void onClose(InventoryCloseEvent e) {
    Plugin.getData().getPluginSystem().getPlayerMetadata().get(e.getPlayer().getUniqueId()).remove(PlayerMeta.INVENTORY);
  }

  /**
   * Placeholder method for menus that currently don't have any
   * additional features outside of cancelling the user's click.
   * <p>
   * Despite its non-functionality, it serves as a catalogue
   * of all possible menus belonging to a command or system.
   * </p>
   */
  private void doNothing() {
  }
}