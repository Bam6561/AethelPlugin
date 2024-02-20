package me.dannynguyen.aethel.commands.playerstat;

import me.dannynguyen.aethel.systems.plugin.PluginData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a menu that shows past shared stats.
 *
 * @author Danny Nguyen
 * @version 1.9.19
 * @since 1.4.10
 */
class PastStatMenu {
  /**
   * PastStat GUI.
   */
  private final Inventory menu;

  /**
   * GUI user.
   */
  private final Player user;

  /**
   * Associates a new PastStat menu with its user.
   *
   * @param user user
   */
  protected PastStatMenu(@NotNull Player user) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.menu = createMenu();
  }

  /**
   * Sets the menu to show past shared stats.
   *
   * @return PastStat menu
   */
  @NotNull
  protected Inventory openMenu() {
    int i = 0;
    for (ItemStack pastStat : PluginData.pastStatHistory.getPastStats()) {
      menu.setItem(i, pastStat);
      i++;
    }
    return menu;
  }

  /**
   * Creates and names a PastStat menu.
   *
   * @return PastStat menu
   */
  private Inventory createMenu() {
    return Bukkit.createInventory(user, 27, ChatColor.DARK_GRAY + "PlayerStat " + ChatColor.DARK_PURPLE + "Past");
  }
}
