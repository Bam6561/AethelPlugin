package me.bam6561.aethelplugin.interfaces;

import org.bukkit.inventory.Inventory;

/**
 * Plugin menu.
 *
 * @author Danny Nguyen
 * @version 1.17.5
 * @since 1.17.5
 */
public interface Menu {
  /**
   * Populates the menu with items.
   *
   * @return plugin menu
   */
  Inventory getMainMenu();
}
