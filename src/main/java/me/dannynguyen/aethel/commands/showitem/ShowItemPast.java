package me.dannynguyen.aethel.commands.showitem;

import me.dannynguyen.aethel.PluginData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a menu that shows past shown items.
 *
 * @author Danny Nguyen
 * @version 1.9.12
 * @since 1.4.5
 */
public class ShowItemPast {
  /**
   * ShowItemPast GUI.
   */
  private final Inventory menu;

  /**
   * Associates a new ShowItemPast menu with its user.
   *
   * @param user user
   */
  public ShowItemPast(@NotNull Player user) {
    this.menu = Bukkit.createInventory(user, 27, ChatColor.DARK_GRAY + "Show " + ChatColor.DARK_PURPLE + "Past");
  }

  /**
   * Sets the ShowItemPast menu to show past shown items.
   *
   * @return ShowItemPast menu
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
}
