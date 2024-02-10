package me.dannynguyen.aethel.commands.playerstat;

import me.dannynguyen.aethel.PluginData;
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
 * @version 1.9.13
 * @since 1.4.10
 */
public class PlayerStatPast {
  /**
   * PlayerStatPast GUI.
   */
  private final Inventory menu;

  /**
   * GUI user.
   */
  private final Player user;

  /**
   * Associates a new PlayerStatPast menu with its user.
   *
   * @param user user
   */
  public PlayerStatPast(@NotNull Player user) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.menu = createMenu();
  }

  /**
   * Sets the PlayerStatPast menu to show past shared stats.
   *
   * @return PlayerStatPast menu
   */
  @NotNull
  public Inventory openMenu() {
    int i = 0;
    for (ItemStack pastStat : PluginData.pastStatHistory.getPastStats()) {
      menu.setItem(i, pastStat);
      i++;
    }
    return menu;
  }

  /**
   * Creates and names a PlayerStatPast menu.
   *
   * @return PlayerStatPast menu
   */
  private Inventory createMenu() {
    return Bukkit.createInventory(user, 27, ChatColor.DARK_GRAY + "PlayerStat " + ChatColor.DARK_PURPLE + "Past");
  }
}
