package me.dannynguyen.aethel.systems.plugin;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Plugin messages.
 *
 * @author Danny Nguyen
 * @version 1.17.7
 * @since 1.11.9
 **/
public enum Message {
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
  PLAYER_ONLY_COMMAND(ChatColor.RED + "Player-only command."),

  /**
   * Insufficient permission.
   */
  INSUFFICIENT_PERMISSION(ChatColor.RED + "Insufficient permission."),

  /**
   * No parameters provided.
   */
  NO_PARAMETERS(ChatColor.RED + "No parameters provided."),

  /**
   * Unrecognized parameter.
   */
  UNRECOGNIZED_PARAMETER(ChatColor.RED + "Unrecognized parameter."),

  /**
   * Unrecognized parameters.
   */
  UNRECOGNIZED_PARAMETERS(ChatColor.RED + "Unrecognized parameters."),

  /**
   * No main hand item.
   */
  NO_MAIN_HAND_ITEM(ChatColor.RED + "No main hand item.");

  /**
   * Message content.
   */
  private final String message;

  /**
   * Associates a message with its content/
   *
   * @param message message content
   */
  Message(String message) {
    this.message = message;
  }

  /**
   * Gets a message's content
   *
   * @return message content
   */
  @NotNull
  public String getMessage() {
    return this.message;
  }
}
