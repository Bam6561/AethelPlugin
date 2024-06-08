package me.bam6561.aethelplugin.commands.character;

import me.bam6561.aethelplugin.interfaces.Menu;
import me.bam6561.aethelplugin.utils.InventoryPages;
import me.bam6561.aethelplugin.utils.item.ItemCreator;
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
 * @version 1.17.6
 * @since 1.14.3
 */
public class QuestsMenu implements Menu {
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
   * Associates a new Quests menu with its user.
   *
   * @param user user
   */
  public QuestsMenu(@NotNull Player user) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.uuid = user.getUniqueId();
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
  public Inventory getMainMenu() {
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
    // TODO
  }
}
