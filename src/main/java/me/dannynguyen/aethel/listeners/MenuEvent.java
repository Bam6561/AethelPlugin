package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.commands.aethelitem.ItemMenu;
import me.dannynguyen.aethel.commands.aethelitem.ItemMenuClick;
import me.dannynguyen.aethel.commands.character.CharacterMenuClick;
import me.dannynguyen.aethel.commands.forge.ForgeMenuClick;
import me.dannynguyen.aethel.commands.forge.RecipeMenu;
import me.dannynguyen.aethel.commands.itemeditor.ItemEditorMenuClick;
import me.dannynguyen.aethel.commands.playerstat.StatCommand;
import me.dannynguyen.aethel.commands.playerstat.StatMenuClick;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.plugin.PluginPlayer;
import me.dannynguyen.aethel.rpg.Settings;
import me.dannynguyen.aethel.utils.item.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.jetbrains.annotations.NotNull;

/**
 * Collection of {@link me.dannynguyen.aethel.interfaces.Menu menu}
 * click listeners.
 * <p>
 * By default, all clicks within plugin menus are cancelled due to the possibility of an
 * internal error occurring and the associated methods never reaching their end result.
 *
 * @author Danny Nguyen
 * @version 1.18.0
 * @since 1.0.2
 */
public class MenuEvent implements Listener {
  /**
   * No parameter constructor.
   */
  public MenuEvent() {
  }

  /**
   * Routes interactions between inventories.
   *
   * @param e inventory click event
   */
  @EventHandler
  private void onClick(InventoryClickEvent e) {
    if (e.getClickedInventory() != null) {
      Menu menu = Plugin.getData().getPluginSystem().getPluginPlayers().get(e.getWhoClicked().getUniqueId()).getMenu();
      if (menu != null && e.getAction() != InventoryAction.COLLECT_TO_CURSOR) {// Prevents item duplication
        e.setCancelled(true);
        String invType = menu.getId();
        switch (invType) {
          case "aethelitem" -> interpretAethelItem(e, menu);
          case "character" -> interpretCharacter(e, menu);
          case "forge" -> interpretForge(e, menu);
          case "itemeditor" -> interpretItemEditor(e, menu);
          case "playerstat" -> interpretPlayerStat(e, menu);
          case "showitem" -> interpretShowItem(e, menu);
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
    Menu menu = Plugin.getData().getPluginSystem().getPluginPlayers().get(e.getWhoClicked().getUniqueId()).getMenu();
    if (menu != null) {
      e.setCancelled(true);
      switch (menu) {
        default -> e.setCancelled(true);
      }
    }
  }

  /**
   * Determines which {@link me.dannynguyen.aethel.commands.aethelitem.ItemCommand}
   * menu is being interacting with.
   *
   * @param e    inventory click event
   * @param menu {@link Menu}
   */
  private void interpretAethelItem(InventoryClickEvent e, Menu menu) {
    if (e.getClickedInventory().getType() == InventoryType.CHEST) {
      if (ItemReader.isNotNullOrAir(e.getCurrentItem())) {
        ItemMenuClick click = new ItemMenuClick(e);
        switch (menu) {
          case AETHELITEM_CATEGORY -> click.interpretMenuClick();
          case AETHELITEM_GET -> click.interpretCategoryClick(ItemMenu.Action.GET);
          case AETHELITEM_REMOVE -> click.interpretCategoryClick(ItemMenu.Action.REMOVE);
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
   * Determines which {@link me.dannynguyen.aethel.commands.character.CharacterCommand}
   * menu is being interacting with.
   *
   * @param e    inventory click event
   * @param menu {@link Menu}
   */
  private void interpretCharacter(InventoryClickEvent e, Menu menu) {
    String owner = Bukkit.getOfflinePlayer(Plugin.getData().getPluginSystem().getPluginPlayers().get(e.getWhoClicked().getUniqueId()).getTarget()).getName();
    if (owner.equals(e.getWhoClicked().getName())) {
      CharacterMenuClick click = new CharacterMenuClick(e);
      if (e.getClickedInventory().getType() == InventoryType.CHEST) {
        switch (menu) {
          case CHARACTER_SHEET -> click.interpretMenuClick();
          case CHARACTER_QUESTS -> click.interpretQuestsClick();
          case CHARACTER_COLLECTIBLES -> click.interpretCollectiblesClick();
          case CHARACTER_SETTINGS -> click.interpretSettingsClick();
        }
      } else {
        if (menu == Menu.CHARACTER_SHEET && !e.isShiftClick()) {
          e.setCancelled(false);
          click.interpretPlayerInventoryClick();
        }
      }
    }
  }

  /**
   * Determines which {@link me.dannynguyen.aethel.commands.forge.ForgeCommand}
   * menu is being interacting with.
   *
   * @param e    inventory click event
   * @param menu {@link Menu}
   */
  private void interpretForge(InventoryClickEvent e, Menu menu) {
    if (e.getClickedInventory().getType() == InventoryType.CHEST) {
      if (ItemReader.isNotNullOrAir(e.getCurrentItem())) {
        ForgeMenuClick click = new ForgeMenuClick(e);
        switch (menu) {
          case FORGE_CATEGORY -> click.interpretMenuClick();
          case FORGE_CRAFT -> click.interpretCategoryClick(RecipeMenu.Action.CRAFT);
          case FORGE_CRAFT_RECIPE -> click.interpretCraftDetailsClick();
          case FORGE_EDIT -> click.interpretCategoryClick(RecipeMenu.Action.EDIT);
          case FORGE_REMOVE -> click.interpretCategoryClick(RecipeMenu.Action.REMOVE);
          case FORGE_SAVE -> click.interpretSaveClick();
        }
      } else if (menu == Menu.FORGE_SAVE) {
        new ForgeMenuClick(e).interpretSaveClick();
      }
    } else {
      if ((e.isShiftClick() && menu == Menu.FORGE_SAVE) || !e.isShiftClick()) {
        e.setCancelled(false);
      }
    }
  }

  /**
   * Determines which {@link me.dannynguyen.aethel.commands.itemeditor.ItemEditorCommand}
   * menu is being interacting with.
   *
   * @param e    inventory click event
   * @param menu {@link Menu}
   */
  private void interpretItemEditor(InventoryClickEvent e, Menu menu) {
    if (e.getClickedInventory().getType() == InventoryType.CHEST) {
      if (ItemReader.isNotNullOrAir(e.getCurrentItem())) {
        ItemEditorMenuClick click = new ItemEditorMenuClick(e);
        switch (menu) {
          case ITEMEDITOR_COSMETIC -> click.interpretMenuClick();
          case ITEMEDITOR_MINECRAFT_ATTRIBUTE -> click.interpretAttributeClick();
          case ITEMEDITOR_AETHEL_ATTRIBUTE -> click.interpretAethelAttributeClick();
          case ITEMEDITOR_ENCHANTMENT -> click.interpretEnchantmentClick();
          case ITEMEDITOR_POTION -> click.interpretPotionClick();
          case ITEMEDITOR_PASSIVE -> click.interpretPassiveClick();
          case ITEMEDITOR_ACTIVE -> click.interpretActiveClick();
          case ITEMEDITOR_TAG -> click.interpretTagClick();
        }
      }
    } else {
      if (!e.isShiftClick()) {
        e.setCancelled(true);
      }
    }
  }

  /**
   * Determines which {@link StatCommand}
   * menu is being interacting with.
   *
   * @param e    inventory click event
   * @param menu {@link Menu}
   */
  private void interpretPlayerStat(InventoryClickEvent e, Menu menu) {
    if (e.getClickedInventory().getType() == InventoryType.CHEST) {
      if (ItemReader.isNotNullOrAir(e.getCurrentItem())) {
        StatMenuClick click = new StatMenuClick(e);
        switch (menu) {
          case PLAYERSTAT_CATEGORY -> click.interpretMenuClick();
          case PLAYERSTAT_STAT -> click.interpretStatClick();
          case PLAYERSTAT_SUBSTAT -> click.interpretSubstatClick();
          case PLAYERSTAT_PAST -> doNothing();
        }
      }
    } else {
      if (!e.isShiftClick()) {
        e.setCancelled(false);
      }
    }
  }

  /**
   * Determines which {@link me.dannynguyen.aethel.commands.showitem.ShowItemCommand}
   * menu is being interacting with.
   *
   * @param e    inventory click event
   * @param menu {@link Menu}
   */
  private void interpretShowItem(InventoryClickEvent e, Menu menu) {
    if (e.getClickedInventory().getType() == InventoryType.CHEST) {
      switch (menu) {
        case SHOWITEM_PAST -> doNothing();
      }
    } else {
      if (!e.isShiftClick()) {
        e.setCancelled(false);
      }
    }
  }

  /**
   * Removes plugin {@link PluginPlayer#getMenu()} when a menu is closed.
   * <p>
   * Since opening a new inventory while one already exists triggers
   * the InventoryCloseEvent, always add new inventory metadata AFTER
   * opening an inventory and not before, as it will be removed otherwise.
   *
   * @param e inventory close event
   */
  @EventHandler
  private void onClose(InventoryCloseEvent e) {
    Plugin.getData().getPluginSystem().getPluginPlayers().get(e.getPlayer().getUniqueId()).setMenu(null);
  }

  /**
   * Placeholder method for menus that currently don't have any
   * additional features outside of cancelling the user's click.
   * <p>
   * Despite its non-functionality, it serves as a catalogue
   * of all possible menus belonging to a command or system.
   */
  private void doNothing() {
  }

  /**
   * {@link me.dannynguyen.aethel.interfaces.Menu} types.
   */
  public enum Menu {
    /**
     * View {@link me.dannynguyen.aethel.commands.aethelitem.ItemRegistry item} categories.
     */
    AETHELITEM_CATEGORY("aethelitem"),

    /**
     * Get {@link me.dannynguyen.aethel.commands.aethelitem.ItemRegistry items}.
     */
    AETHELITEM_GET("aethelitem"),

    /**
     * Remove {@link me.dannynguyen.aethel.commands.aethelitem.ItemRegistry items}.
     */
    AETHELITEM_REMOVE("aethelitem"),

    /**
     * Interact with {@link me.dannynguyen.aethel.commands.character.SheetMenu}.
     */
    CHARACTER_SHEET("character"),

    /**
     * View quests.
     */
    CHARACTER_QUESTS("character"),

    /**
     * View collectibles.
     */
    CHARACTER_COLLECTIBLES("character"),

    /**
     * Interact with {@link Settings RPG settings}.
     */
    CHARACTER_SETTINGS("character"),

    /**
     * View {@link me.dannynguyen.aethel.commands.forge.RecipeRegistry recipe} categories.
     */
    FORGE_CATEGORY("forge"),

    /**
     * Craft {@link me.dannynguyen.aethel.commands.forge.RecipeRegistry recipes}.
     */
    FORGE_CRAFT("forge"),

    /**
     * Craft {@link me.dannynguyen.aethel.commands.forge.RecipeRegistry recipe} operation.
     */
    FORGE_CRAFT_RECIPE("forge"),

    /**
     * Edit {@link me.dannynguyen.aethel.commands.forge.RecipeRegistry recipes}.
     */
    FORGE_EDIT("forge"),

    /**
     * Remove {@link me.dannynguyen.aethel.commands.forge.RecipeRegistry recipes}.
     */
    FORGE_REMOVE("forge"),

    /**
     * Save {@link me.dannynguyen.aethel.commands.forge.RecipeRegistry recipes}.
     */
    FORGE_SAVE("forge"),

    /**
     * Edit item cosmetics.
     */
    ITEMEDITOR_COSMETIC("itemeditor"),

    /**
     * Edit item Minecraft attributes.
     */
    ITEMEDITOR_MINECRAFT_ATTRIBUTE("itemeditor"),

    /**
     * Edit item {@link Key#ATTRIBUTE_LIST}.
     */
    ITEMEDITOR_AETHEL_ATTRIBUTE("itemeditor"),

    /**
     * Edit item enchantments.
     */
    ITEMEDITOR_ENCHANTMENT("itemeditor"),

    /**
     * Edit item potion effects.
     */
    ITEMEDITOR_POTION("itemeditor"),

    /**
     * Edit item {@link Key#PASSIVE_LIST}.
     */
    ITEMEDITOR_PASSIVE("itemeditor"),

    /**
     * Edit item {@link Key#ACTIVE_LIST}.
     */
    ITEMEDITOR_ACTIVE("itemeditor"),

    /**
     * Edit item {@link Key Aethel tags}.
     */
    ITEMEDITOR_TAG("itemeditor"),

    /**
     * View stat categories.
     */
    PLAYERSTAT_CATEGORY("playerstat"),

    /**
     * View past stats.
     */
    PLAYERSTAT_PAST("playerstat"),

    /**
     * Interact with statistics.
     */
    PLAYERSTAT_STAT("playerstat"),

    /**
     * Interact with sub-statistics.
     */
    PLAYERSTAT_SUBSTAT("playerstat"),

    /**
     * View past shown items.
     */
    SHOWITEM_PAST("showitem");

    /**
     * Menu ID.
     */
    private final String id;

    /**
     * Associates a menu with its ID.
     *
     * @param id ID
     */
    Menu(String id) {
      this.id = id;
    }

    /**
     * Gets the menu's ID.
     *
     * @return menu's ID
     */
    @NotNull
    public String getId() {
      return this.id;
    }
  }

  /**
   * {@link me.dannynguyen.aethel.interfaces.Menu} modes.
   */
  public enum Mode {
    /**
     * Craft {@link me.dannynguyen.aethel.commands.forge.RecipeRegistry recipes}.
     */
    RECIPE_MENU_CRAFT("craft"),

    /**
     * Edit {@link me.dannynguyen.aethel.commands.forge.RecipeRegistry recipes}.
     */
    RECIPE_MENU_EDIT("edit");

    /**
     * Mode ID.
     */
    private final String id;

    /**
     * Associates a menu mode with its ID.
     *
     * @param id ID
     */
    Mode(String id) {
      this.id = id;
    }

    /**
     * Gets the mode's ID.
     *
     * @return mode's ID
     */
    @NotNull
    public String getId() {
      return this.id;
    }
  }
}
