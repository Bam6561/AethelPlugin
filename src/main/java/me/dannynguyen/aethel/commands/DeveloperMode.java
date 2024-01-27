package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.enums.PluginPermission;
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
 * @version 1.7.7
 * @since 1.4.6
 */
public class DeveloperMode implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player user)) {
      sender.sendMessage(PluginMessage.PLAYER_ONLY_COMMAND.message);
      return true;
    }

    if (user.hasPermission(PluginPermission.DEVELOPERMODE.permission)) {
      readRequest(user, args);
    } else {
      user.sendMessage(PluginMessage.INSUFFICIENT_PERMISSION.message);
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
      user.sendMessage(PluginMessage.UNRECOGNIZED_PARAMETERS.message);
    }
  }

  /**
   * Toggles Developer mode on or off for the user.
   *
   * @param user user
   */
  private void toggleDeveloperMode(Player user) {
    if (!user.hasMetadata(PluginPlayerMeta.Container.DEVELOPER.name)) {
      user.setMetadata(PluginPlayerMeta.Container.DEVELOPER.name,
          new FixedMetadataValue(Plugin.getInstance(), "on"));
      user.sendMessage(PluginMessage.DEVELOPERMODE_ON.message);
    } else {
      user.removeMetadata(PluginPlayerMeta.Container.DEVELOPER.name, Plugin.getInstance());
      user.sendMessage(PluginMessage.DEVELOPERMODE_OFF.message);
    }
  }
}