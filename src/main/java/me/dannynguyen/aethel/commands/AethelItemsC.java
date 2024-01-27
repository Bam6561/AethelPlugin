package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.enums.PluginPermission;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.inventories.aethelItems.AethelItemsI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * AethelItems is a command invocation that allows the user to obtain items through clicking.
 * <p>
 * Additional Parameters:
 * - "reload", "r": reloads items into memory
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.7.7
 * @since 1.3.2
 */
public class AethelItemsC implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player user)) {
      sender.sendMessage(PluginMessage.PLAYER_ONLY_COMMAND.message);
      return true;
    }

    if (user.hasPermission(PluginPermission.AETHELITEMS.permission)) {
      readRequest(user, args);
    } else {
      user.sendMessage(PluginMessage.INSUFFICIENT_PERMISSION.message);
    }
    return true;
  }

  /**
   * Checks if the command request was formatted correctly before interpreting its usage.
   *
   * @param user user
   * @param args user provided parameters
   */
  private void readRequest(Player user, String[] args) {
    switch (args.length) {
      case 0 -> openMainMenu(user);
      case 1 -> readParameter(user, args[0].toLowerCase());
      default -> user.sendMessage(PluginMessage.UNRECOGNIZED_PARAMETERS.message);
    }
  }

  /**
   * Checks if the action is "reload" before reloading items into memory.
   *
   * @param user   user
   * @param action type of interaction
   */
  private void readParameter(Player user, String action) {
    switch (action) {
      case "reload", "r" -> {
        PluginData.aethelItemsData.loadItems();
        user.sendMessage(PluginMessage.AETHELITEMS_RELOAD.message);
      }
      default -> user.sendMessage(PluginMessage.UNRECOGNIZED_PARAMETER.message);
    }
  }

  /**
   * Opens an AethelItems main menu.
   *
   * @param user user
   */
  private void openMainMenu(Player user) {
    user.openInventory(AethelItemsI.openMainMenu(user, "view"));
    user.setMetadata(PluginPlayerMeta.Container.INVENTORY.name,
        new FixedMetadataValue(Plugin.getInstance(), PluginPlayerMeta.Value.AETHELITEMS_CATEGORY.value));
    user.setMetadata(PluginPlayerMeta.Container.PAGE.name, new FixedMetadataValue(Plugin.getInstance(), "0"));
  }
}
