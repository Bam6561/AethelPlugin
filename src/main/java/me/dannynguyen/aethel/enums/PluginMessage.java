package me.dannynguyen.aethel.enums;

import org.bukkit.ChatColor;

/**
 * PluginMessage is a collection of enums containing the plugin's commonly sent messages.
 *
 * @author Danny Nguyen
 * @version 1.8.4
 * @since 1.7.6
 */
public class PluginMessage {
  public enum Success {
    NOTIFICATION_GLOBAL(ChatColor.GREEN + "[!] "),
    NOTIFICATION_INPUT(ChatColor.GOLD + "[!] ");

    public final String message;

    Success(String message) {
      this.message = message;
    }
  }

  public enum Failure {
    PLAYER_ONLY_COMMAND(ChatColor.RED + "Player-only command."),
    INSUFFICIENT_PERMISSION(ChatColor.RED + "Insufficient permission."),
    NO_PARAMETERS(ChatColor.RED + "No parameters provided."),
    UNRECOGNIZED_PARAMETER(ChatColor.RED + "Unrecognized parameter."),
    UNRECOGNIZED_PARAMETERS(ChatColor.RED + "Unrecognized parameters."),
    NO_MAIN_HAND_ITEM(ChatColor.RED + "No main hand item.");

    public final String message;

    Failure(String message) {
      this.message = message;
    }
  }
}
