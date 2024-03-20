package me.dannynguyen.aethel.commands.character;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.plugin.enums.PlayerHead;
import me.dannynguyen.aethel.plugin.interfaces.Menu;
import me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot;
import me.dannynguyen.aethel.util.InventoryPages;
import me.dannynguyen.aethel.util.item.ItemCreator;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a menu that shows the player's {@link me.dannynguyen.aethel.rpg.system.Settings}.
 *
 * @author Danny Nguyen
 * @version 1.17.6
 * @since 1.11.5
 */
public class SettingsMenu implements Menu {
  /**
   * GUI.
   */
  private final Inventory menu;

  /**
   * GUI user.
   */
  private final Player user;

  /**
   * User's UUID.
   */
  private final UUID uuid;

  /**
   * Associates a new Settings menu with its user.
   *
   * @param user user
   */
  public SettingsMenu(@NotNull Player user) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.uuid = user.getUniqueId();
    this.menu = createMenu();
  }

  /**
   * Creates and names a Settings menu to its user.
   *
   * @return Settings menu
   */
  private Inventory createMenu() {
    return Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "Settings " + ChatColor.DARK_PURPLE + user.getName());
  }

  /**
   * Opens a Settings menu.
   *
   * @return Settings menu
   */
  @NotNull
  public Inventory getMainMenu() {
    addOwner();
    addSettings();
    InventoryPages.addBackButton(menu, 6);
    return menu;
  }

  /**
   * Adds the settings owner's head.
   */
  private void addOwner() {
    menu.setItem(4, ItemCreator.createPlayerHead(user));
  }

  /**
   * Adds settings.
   */
  private void addSettings() {
    addDisplayHealthBar();
    addDisplayHealthAction();
    addActiveAbilityCrouchBinds();
  }

  /**
   * Toggles the visibility of the health bar.
   */
  private void addDisplayHealthBar() {
    if (Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getSettings().isHealthBarVisible()) {
      menu.setItem(18, ItemCreator.createItem(Material.LIME_WOOL, ChatColor.AQUA + "Display Health Boss Bar"));
    } else {
      menu.setItem(18, ItemCreator.createItem(Material.RED_WOOL, ChatColor.AQUA + "Display Health Boss Bar"));
    }
  }

  /**
   * Toggles the visibility of health in the action bar.
   */
  private void addDisplayHealthAction() {
    if (Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getSettings().isHealthActionVisible()) {
      menu.setItem(19, ItemCreator.createItem(Material.LIME_WOOL, ChatColor.AQUA + "Display Health Action Bar"));
    } else {
      menu.setItem(19, ItemCreator.createItem(Material.RED_WOOL, ChatColor.AQUA + "Display Health Action Bar"));
    }
  }

  /**
   * Adds active ability crouch binds.
   */
  private void addActiveAbilityCrouchBinds() {
    Map<RpgEquipmentSlot, Integer> activeAbilityCrouchBinds = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getSettings().getActiveAbilityCrouchBinds();
    menu.setItem(9, ItemCreator.createPluginPlayerHead(PlayerHead.TRASH_CAN.getHead(), ChatColor.AQUA + "Reset Active Ability Crouch Binds"));
    menu.setItem(10, ItemCreator.createItem(Material.IRON_SWORD, ChatColor.AQUA + "Main Hand", List.of(ChatColor.WHITE + String.valueOf(activeAbilityCrouchBinds.get(RpgEquipmentSlot.HAND) + 1)), ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(11, ItemCreator.createItem(Material.SHIELD, ChatColor.AQUA + "Off Hand", List.of(ChatColor.WHITE + String.valueOf(activeAbilityCrouchBinds.get(RpgEquipmentSlot.OFF_HAND) + 1)), ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(12, ItemCreator.createItem(Material.IRON_HELMET, ChatColor.AQUA + "Head", List.of(ChatColor.WHITE + String.valueOf(activeAbilityCrouchBinds.get(RpgEquipmentSlot.HEAD) + 1)), ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(13, ItemCreator.createItem(Material.IRON_CHESTPLATE, ChatColor.AQUA + "Chest", List.of(ChatColor.WHITE + String.valueOf(activeAbilityCrouchBinds.get(RpgEquipmentSlot.CHEST) + 1)), ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(14, ItemCreator.createItem(Material.IRON_LEGGINGS, ChatColor.AQUA + "Legs", List.of(ChatColor.WHITE + String.valueOf(activeAbilityCrouchBinds.get(RpgEquipmentSlot.LEGS) + 1)), ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(15, ItemCreator.createItem(Material.IRON_BOOTS, ChatColor.AQUA + "Feet", List.of(ChatColor.WHITE + String.valueOf(activeAbilityCrouchBinds.get(RpgEquipmentSlot.FEET) + 1)), ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(16, ItemCreator.createItem(Material.IRON_NUGGET, ChatColor.AQUA + "Necklace", List.of(ChatColor.WHITE + String.valueOf(activeAbilityCrouchBinds.get(RpgEquipmentSlot.NECKLACE) + 1))));
    menu.setItem(17, ItemCreator.createItem(Material.GOLD_NUGGET, ChatColor.AQUA + "Ring", List.of(ChatColor.WHITE + String.valueOf(activeAbilityCrouchBinds.get(RpgEquipmentSlot.RING) + 1))));
  }
}
