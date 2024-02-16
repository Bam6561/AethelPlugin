package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.commands.aethelitem.ItemMenuAction;
import me.dannynguyen.aethel.commands.aethelitem.ItemMenuClick;
import me.dannynguyen.aethel.commands.character.CharacterMenuClick;
import me.dannynguyen.aethel.commands.forge.ForgeMenuAction;
import me.dannynguyen.aethel.commands.forge.ForgeMenuClick;
import me.dannynguyen.aethel.commands.itemeditor.ItemEditorMenuClick;
import me.dannynguyen.aethel.commands.playerstat.PlayerStatMenuClick;
import me.dannynguyen.aethel.systems.PlayerMeta;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;

import java.util.Map;

/**
 * Collection of inventory click listeners for the plugin's menus.
 *
 * @author Danny Nguyen
 * @version 1.10.1
 * @since 1.0.2
 */
public class MenuClick implements Listener {
  /**
   * Routes interactions between inventories.
   *
   * @param e inventory click event
   */
  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    if (e.getClickedInventory() != null) {
      Map<PlayerMeta, String> playerMeta = PluginData.pluginSystem.getPlayerMetadata().get((Player) e.getWhoClicked());
      if (playerMeta.containsKey(PlayerMeta.INVENTORY)) {
        Bukkit.getLogger().warning(playerMeta.get(PlayerMeta.INVENTORY));
        String[] invType = playerMeta.get(PlayerMeta.INVENTORY).split("\\.");
        switch (invType[0]) {
          case "aethelitem" -> interpretAethelItem(e, invType);
          case "character" -> interpretCharacter(e, invType);
          case "forge" -> interpretForge(e, invType);
          case "itemeditor" -> interpretItemEditor(e, invType);
          case "playerstat" -> interpretPlayerStat(e, invType);
          case "showitem" -> e.setCancelled(true);
        }
      }
    }
  }

  /**
   * Disables drag clicks while inside specific menus.
   *
   * @param e inventory drag event
   */
  @EventHandler
  public void onDrag(InventoryDragEvent e) {
    Map<PlayerMeta, String> playerMeta = PluginData.pluginSystem.getPlayerMetadata().get((Player) e.getWhoClicked());
    if (playerMeta.containsKey(PlayerMeta.INVENTORY)) {
      String[] invType = playerMeta.get(PlayerMeta.INVENTORY).split("\\.");
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
    if (e.getClickedInventory().getType().equals(InventoryType.CHEST) && !e.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
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
      case "category", "craft", "craft-recipe", "edit", "remove" -> {
        if (ItemReader.isNotNullOrAir(e.getCurrentItem()) && e.getClickedInventory().getType().equals(InventoryType.CHEST)) {
          ForgeMenuClick click = new ForgeMenuClick(e);
          switch (invType[1]) {
            case "category" -> click.interpretMainMenuClick();
            case "craft" -> click.interpretCategoryClick(ForgeMenuAction.CRAFT);
            case "craft-recipe" -> click.interpretCraftDetailsClick();
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
        case "cosmetic" -> click.interpretCosmeticEditorClick();
        case "attribute" -> click.interpretAttributeEditorClick();
        case "enchantment" -> click.interpretEnchantmentEditorClick();
        case "tag" -> click.interpretTagEditorClick();
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
    Map<PlayerMeta, String> playerMeta = PluginData.pluginSystem.getPlayerMetadata().get((Player) e.getPlayer());
    if (playerMeta.containsKey(PlayerMeta.INVENTORY)) {
      playerMeta.remove(PlayerMeta.INVENTORY);
    }
  }
}
