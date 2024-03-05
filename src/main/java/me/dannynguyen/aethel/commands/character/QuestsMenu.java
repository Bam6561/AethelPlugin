package me.dannynguyen.aethel.commands.character;

import me.dannynguyen.aethel.utility.InventoryPages;
import me.dannynguyen.aethel.utility.ItemCreator;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a menu to view in-progress and completed quests.
 *
 * @author Danny Nguyen
 * @version 1.14.3
 * @since 1.14.3
 */
class QuestsMenu {
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
   * Associates a new Quests menu with its user.
   *
   * @param user user
   */
  protected QuestsMenu(@NotNull Player user) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.userUUID = user.getUniqueId();
    this.menu = createMenu();
  }

  /**
   * Creates and names a Quests menu to its user.
   *
   * @return Quests menu
   */
  private Inventory createMenu() {
    return Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "Quests " + ChatColor.DARK_PURPLE + user.getName());
  }

  /**
   * Opens a Quests menu.
   *
   * @return Quests menu.
   */
  @NotNull
  protected Inventory openMenu() {
    addOwner();
    addQuests();
    InventoryPages.addBackButton(menu, 6);
    return menu;
  }

  /**
   * Adds the quests owner's head.
   */
  private void addOwner() {
    menu.setItem(4, ItemCreator.createPlayerHead(user));
  }

  /**
   * Adds quests.
   */
  private void addQuests() {
    // TO DO
  }
}
