package me.dannynguyen.aethel.plugin.enums;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

/**
 * Plugin messages.
 *
 * @author Danny Nguyen
 * @version 1.17.14
 * @since 1.11.9
 **/
public enum Message {
  /**
   * Invalid file format.
   */
  INVALID_FILE("[Aethel] Invalid file: "),

  /**
   * Unable to read file.
   */
  UNABLE_TO_READ_FILE("[Aethel] Unable to read file: "),

  /**
   * Error.
   */
  ERROR(ChatColor.RED + ""),

  /**
   * Globally sent.
   */
  NOTIFICATION_GLOBAL(ChatColor.GREEN + "[!] "),

  /**
   * User input.
   */
  NOTIFICATION_INPUT(ChatColor.GOLD + "[!] "),

  /**
   * Player only command.
   */
  PLAYER_ONLY_COMMAND(Message.ERROR.getMessage() + "Player-only command."),

  /**
   * Insufficient permission.
   */
  INSUFFICIENT_PERMISSION(Message.ERROR.getMessage() + "Insufficient permission."),

  /**
   * No parameters provided.
   */
  NO_PARAMETERS(Message.ERROR.getMessage() + "No parameters provided."),

  /**
   * Unrecognized parameter.
   */
  UNRECOGNIZED_PARAMETER(Message.ERROR.getMessage() + "Unrecognized parameter."),

  /**
   * Unrecognized parameters.
   */
  UNRECOGNIZED_PARAMETERS(Message.ERROR.getMessage() + "Unrecognized parameters."),

  /**
   * No main hand item.
   */
  NO_MAIN_HAND_ITEM(Message.ERROR.getMessage() + "No main hand item."),

  /**
   * Unrecognized equipment slot.
   */
  UNRECOGNIZED_EQUIPMENT_SLOT(Message.ERROR.getMessage() + "Unrecognized equipment slot."),

  /**
   * Unrecognized status.
   */
  UNRECOGNIZED_STATUS(Message.ERROR.getMessage() + "Unrecognized status type."),

  /**
   * Item has no lore.
   */
  LORE_DOES_NOT_EXIST(Message.ERROR.getMessage() + "Item has no lore."),

  /**
   * Line does not exist.
   */
  LINE_DOES_NOT_EXIST(Message.ERROR.getMessage() + "Line does not exist."),

  /**
   * Invalid line.
   */
  INVALID_LINE(Message.ERROR.getMessage() + "Invalid line."),

  /**
   * Invalid value.
   */
  INVALID_VALUE(Message.ERROR.getMessage() + "Invalid value."),

  /**
   * Invalid % health.
   */
  INVALID_HEALTH(Message.ERROR.getMessage() + "Invalid % health."),

  /**
   * Invalid chance.
   */
  INVALID_CHANCE(Message.ERROR.getMessage() + "Invalid chance."),

  /**
   * Invalid cooldown.
   */
  INVALID_COOLDOWN(Message.ERROR.getMessage() + "Invalid cooldown."),

  /**
   * Invalid true/false.
   */
  INVALID_BOOLEAN(Message.ERROR.getMessage() + "Invalid true/false."),

  /**
   * Invalid stacks.
   */
  INVALID_STACKS(Message.ERROR.getMessage() + "Invalid stacks."),

  /**
   * Invalid ticks.
   */
  INVALID_TICKS(Message.ERROR.getMessage() + "Invalid ticks."),

  /**
   * Invalid radius.
   */
  INVALID_RADIUS(Message.ERROR.getMessage() + "Invalid radius."),

  /**
   * Invalid damage.
   */
  INVALID_DAMAGE(Message.ERROR.getMessage() + "Invalid damage."),

  /**
   * Invalid distance.
   */
  INVALID_DISTANCE(Message.ERROR.getMessage() + "Invalid distance."),

  /**
   * Invalid delay.
   */
  INVALID_DELAY(Message.ERROR.getMessage() + "Invalid delay.");

  /**
   * Message content.
   */
  private final String message;

  /**
   * Associates a message with its content.
   *
   * @param message message content
   */
  Message(String message) {
    this.message = message;
  }

  /**
   * Gets a message's content.
   *
   * @return message content
   */
  @NotNull
  public String getMessage() {
    return this.message;
  }
}
