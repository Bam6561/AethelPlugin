package me.dannynguyen.aethel.plugin.interfaces;

/**
 * Inventory click for plugin menus.
 *
 * @author Danny Nguyen
 * @version 1.17.5
 * @since 1.17.5
 */
public interface MenuClickEvent {
  /**
   * Determines which action to take when a click happens.
   */
  void interpretMenuClick();
}
