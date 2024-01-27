package me.dannynguyen.aethel.enums;

import org.bukkit.ChatColor;

/**
 * PluginMessage is an enum containing the plugin's commonly sent messages.
 *
 * @author Danny Nguyen
 * @version 1.7.6
 * @since 1.7.6
 */
public enum PluginMessage {
  // General
  PLAYER_ONLY_COMMAND(ChatColor.RED + "Player-only command."),
  INSUFFICIENT_PERMISSION(ChatColor.RED + "Insufficient permission."),
  NO_PARAMETERS_PROVIDED(ChatColor.RED + "No parameters provided"),
  UNRECOGNIZED_PARAMETER(ChatColor.RED + "Unrecognized parameter."),
  UNRECOGNIZED_PARAMETERS(ChatColor.RED + "Unrecognized parameters."),
  NO_MAIN_HAND_ITEM(ChatColor.RED + "No main hand item."),

  // Commands
  AETHELITEMS_RELOAD(ChatColor.GREEN + "[Reloaded Aethel Items]"),
  AETHELTAGS_GET_TAGS(ChatColor.GREEN + "[Get Tags] "),
  AETHELTAGS_NO_TAGS_FOUND(ChatColor.RED + "No tags found."),
  AETHELTAGS_REMOVED_TAG(ChatColor.RED + "[Removed Tag] "),
  AETHELTAGS_SET_TAG(ChatColor.GREEN + "[Set Tag] "),
  DEVELOPERMODE_ON(ChatColor.GREEN + "[Developer Mode On]"),
  DEVELOPERMODE_OFF(ChatColor.RED + "[Developer Mode Off]"),
  FORGE_RELOAD(ChatColor.GREEN + "[Reloaded Forge Recipes]"),
  FORGE_CRAFT_INSUFFICIENT_COMPONENTS(ChatColor.RED + "Insufficient components.");

  public final String message;

  PluginMessage(String message) {
    this.message = message;
  }
}
