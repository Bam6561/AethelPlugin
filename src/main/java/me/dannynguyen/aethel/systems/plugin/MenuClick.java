package me.dannynguyen.aethel.systems.plugin;

/**
 * Inventory click for plugin menus.
 *
 * @author Danny Nguyen
 * @version 1.17.5
 * @since 1.17.5
 */
public interface MenuClick {
  /**
   * Determines which action to take when a click happens.
   */
  void interpretMenuClick();
}
