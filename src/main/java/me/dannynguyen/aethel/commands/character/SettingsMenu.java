package me.dannynguyen.aethel.commands.character;

import me.dannynguyen.aethel.systems.plugin.PluginData;
import me.dannynguyen.aethel.utility.InventoryPages;
import me.dannynguyen.aethel.utility.ItemCreator;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a menu that shows the player's RPG settings.
 *
 * @author Danny Nguyen
 * @version 1.13.4
 * @since 1.11.5
 */
class SettingsMenu {
  /**
   * Settings GUI.
   */
  private final Inventory menu;

  /**
   * GUI user.
   */
  private final Player user;

  /**
   * User's UUID.
   */
  private final UUID userUUID;

  /**
   * Associates a new Settings menu with its user.
   *
   * @param user user
   */
  protected SettingsMenu(@NotNull Player user) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.userUUID = user.getUniqueId();
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
  protected Inventory openMenu() {
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
  }

  /**
   * Toggles the visibility of the health bar.
   */
  private void addDisplayHealthBar() {
    if (PluginData.rpgSystem.getRpgPlayers().get(userUUID).getHealthBar().getHealthBar().isVisible()) {
      menu.setItem(9, ItemCreator.createItem(Material.LIME_WOOL, ChatColor.WHITE + "Display Health Bar"));
    } else {
      menu.setItem(9, ItemCreator.createItem(Material.RED_WOOL, ChatColor.WHITE + "Display Health Bar"));
    }
  }
}
