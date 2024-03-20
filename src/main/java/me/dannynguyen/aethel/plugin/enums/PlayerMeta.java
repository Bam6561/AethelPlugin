package me.dannynguyen.aethel.plugin.enums;

/**
 * Plugin player metadata.
 *
 * @author Danny Nguyen
 * @version 1.17.3
 * @since 1.10.1
 */
public enum PlayerMeta {
  /**
   * Action input type.
   */
  ACTION,

  /**
   * Menu category.
   */
  CATEGORY,

  /**
   * {@link me.dannynguyen.aethel.commands.DeveloperModeCommand}.
   */
  DEVELOPER,

  /**
   * Future action.
   */
  FUTURE,

  /**
   * {@link me.dannynguyen.aethel.plugin.listeners.MenuClick}
   */
  INVENTORY,

  /**
   * {@link me.dannynguyen.aethel.plugin.listeners.MessageSent}
   */
  MESSAGE,

  /**
   * Menu page.
   */
  PAGE,

  /**
   * Target player.
   */
  PLAYER,

  /**
   * {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot}
   */
  SLOT,

  /**
   * Condition type.
   */
  CONDITION,

  /**
   * Interacting object type.
   */
  TYPE
}