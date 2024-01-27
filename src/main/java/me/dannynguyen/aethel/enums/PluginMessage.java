package me.dannynguyen.aethel.enums;

import org.bukkit.ChatColor;

/**
 * PluginMessage is an enum collection containing the plugin's commonly sent messages.
 *
 * @author Danny Nguyen
 * @version 1.7.9
 * @since 1.7.6
 */
public class PluginMessage {
  public enum Success {
    AETHELITEMS_SAVE(ChatColor.GREEN + "[Saved Aethel Item] "),
    AETHELITEMS_RELOAD(ChatColor.GREEN + "[Reloaded Aethel Items]"),
    AETHELTAGS_GET_TAGS(ChatColor.GREEN + "[Get Tags] "),
    AETHELTAGS_SET_TAG(ChatColor.GREEN + "[Set Tag] "),
    AETHELTAGS_REMOVED_TAG(ChatColor.RED + "[Removed Tag] "),
    DEVELOPERMODE_ON(ChatColor.GREEN + "[Developer Mode On]"),
    DEVELOPERMODE_OFF(ChatColor.RED + "[Developer Mode Off]"),
    FORGE_RELOAD(ChatColor.GREEN + "[Reloaded Forge Recipes]");

    public final String message;

    Success(String message) {
      this.message = message;
    }
  }

  public enum Failure {
    PLAYER_ONLY_COMMAND(ChatColor.RED + "Player-only command."),
    INSUFFICIENT_PERMISSION(ChatColor.RED + "Insufficient permission."),
    NO_PARAMETERS_PROVIDED(ChatColor.RED + "No parameters provided"),
    UNRECOGNIZED_PARAMETER(ChatColor.RED + "Unrecognized parameter."),
    UNRECOGNIZED_PARAMETERS(ChatColor.RED + "Unrecognized parameters."),
    NO_MAIN_HAND_ITEM(ChatColor.RED + "No main hand item."),

    AETHELITEMS_NO_ITEM(ChatColor.RED + "No item to save."),
    AETHELITEMS_SAVE_FAILED(ChatColor.RED + "Unable to save item."),
    AETHELTAGS_NO_TAGS(ChatColor.RED + "No tags found."),
    FORGE_CRAFT_INSUFFICIENT_COMPONENTS(ChatColor.RED + "Insufficient components.");

    public final String message;

    Failure(String message) {
      this.message = message;
    }
  }
}
