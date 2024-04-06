package me.dannynguyen.aethel.enums.plugin;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

/**
 * Plugin messages.
 *
 * @author Danny Nguyen
 * @version 1.21.5
 * @since 1.11.9
 */
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
   * Unrecognized attribute.
   */
  UNRECOGNIZED_ATTRIBUTE(Message.ERROR.getMessage() + "Unrecognized attribute type."),

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
   * Invalid duration.
   */
  INVALID_DURATION(Message.ERROR.getMessage() + "Invalid duration."),

  /**
   * Invalid radius.
   */
  INVALID_RADIUS(Message.ERROR.getMessage() + "Invalid radius."),

  /**
   * Invalid damage.
   */
  INVALID_DAMAGE(Message.ERROR.getMessage() + "Invalid damage."),

  /**
   * Invalid modifier.
   */
  INVALID_MODIFIER(Message.ERROR.getMessage() + "Invalid modifier."),

  /**
   * Invalid delay.
   */
  INVALID_DELAY(Message.ERROR.getMessage() + "Invalid delay."),

  /**
   * Invalid distance.
   */
  INVALID_DISTANCE(Message.ERROR.getMessage() + "Invalid distance."),

  /**
   * Invalid type.
   */
  INVALID_TYPE(Message.ERROR.getMessage() + "Invalid type."),

  /**
   * Invalid amplifier.
   */
  INVALID_AMPLIFIER(Message.ERROR.getMessage() + "Invalid amplifier."),

  /**
   * Invalid Minecraft attribute or {@link me.dannynguyen.aethel.enums.rpg.AethelAttribute}.
   */
  INVALID_ATTRIBUTE(Message.ERROR.getMessage() + "Invalid attribute."),

  /**
   * Invalid X.
   */
  INVALID_X(Message.ERROR.getMessage() + "Invalid x."),

  /**
   * Invalid Y.
   */
  INVALID_Y(Message.ERROR.getMessage() + "Invalid y."),

  /**
   * Invalid Z.
   */
  INVALID_Z(Message.ERROR.getMessage() + "Invalid z."),

  /**
   * True only.
   */
  TRUE_ONLY(Message.ERROR.getMessage() + "True only.");

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
