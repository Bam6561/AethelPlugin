package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * DeveloperMode is a command invocation that allows the
 * user to bypass conditions for various interactions.
 *
 * @author Danny Nguyen
 * @version 1.9.3
 * @since 1.4.6
 */
public class DeveloperModeCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player user)) {
      sender.sendMessage(PluginMessage.Failure.PLAYER_ONLY_COMMAND.message);
      return true;
    }

    if (user.hasPermission(Permission.DEVELOPERMODE.permission)) {
      readRequest(user, args);
    } else {
      user.sendMessage(PluginMessage.Failure.INSUFFICIENT_PERMISSION.message);
    }
    return true;
  }

  /**
   * Checks if the command request was formatted correctly before toggling developer mode.
   *
   * @param user user
   * @param args user provided parameters
   */
  private void readRequest(Player user, String[] args) {
    int numberOfParameters = args.length;
    if (numberOfParameters == 0) {
      toggleDeveloperMode(user);
    } else {
      user.sendMessage(PluginMessage.Failure.UNRECOGNIZED_PARAMETERS.message);
    }
  }

  /**
   * Toggles Developer mode on or off for the user.
   *
   * @param user user
   */
  private void toggleDeveloperMode(Player user) {
    if (!user.hasMetadata(PluginPlayerMeta.DEVELOPER.getMeta())) {
      user.setMetadata(PluginPlayerMeta.DEVELOPER.getMeta(),
          new FixedMetadataValue(Plugin.getInstance(), 1));
      user.sendMessage(Success.DEVELOPERMODE_ON.message);
    } else {
      user.removeMetadata(PluginPlayerMeta.DEVELOPER.getMeta(), Plugin.getInstance());
      user.sendMessage(Success.DEVELOPERMODE_OFF.message);
    }
  }

  private enum Permission {
    DEVELOPERMODE("aethel.developermode");

    public final String permission;

    Permission(String permission) {
      this.permission = permission;
    }
  }

  private enum Success {
    DEVELOPERMODE_ON(ChatColor.GREEN + "[Developer Mode On]"),
    DEVELOPERMODE_OFF(ChatColor.RED + "[Developer Mode Off]");

    public final String message;

    Success(String message) {
      this.message = message;
    }
  }
}