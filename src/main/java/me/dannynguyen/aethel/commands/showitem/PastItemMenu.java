package me.dannynguyen.aethel.commands.showitem;

import me.dannynguyen.aethel.PluginData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a menu that shows past shown items.
 *
 * @author Danny Nguyen
 * @version 1.9.13
 * @since 1.4.5
 */
public class PastItemMenu {
  /**
   * PastItem GUI.
   */
  private final Inventory menu;

  /**
   * GUI user.
   */
  private final Player user;

  /**
   * Associates a new PastItem menu with its user.
   *
   * @param user user
   */
  public PastItemMenu(@NotNull Player user) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.menu = createMenu();
  }

  /**
   * Sets the PastItem menu to show past shown items.
   *
   * @return PastItem menu
   */
  @NotNull
  public Inventory openMenu() {
    int index = 0;
    for (ItemStack item : PluginData.pastItemHistory.getPastItems()) {
      menu.setItem(index, item);
      index++;
    }
    return menu;
  }

  /**
   * Creates and names a PastItem menu.
   *
   * @return PastItem menu
   */
  private Inventory createMenu() {
    return Bukkit.createInventory(user, 27, ChatColor.DARK_GRAY + "Show " + ChatColor.DARK_PURPLE + "Past");
  }
}
