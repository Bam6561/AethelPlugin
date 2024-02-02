package me.dannynguyen.aethel.commands.aethelItems;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.listeners.InventoryMenuListener;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * AethelItemsCommand is a command invocation that allows the user to obtain items through clicking.
 * <p>
 * Additional Parameters:
 * - "reload", "r": reloads items into memory
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.8.4
 * @since 1.3.2
 */
public class AethelItemsCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player user)) {
      sender.sendMessage(PluginMessage.Failure.PLAYER_ONLY_COMMAND.message);
      return true;
    }

    if (user.hasPermission(Permission.AETHELITEMS.permission)) {
      readRequest(user, args);
    } else {
      user.sendMessage(PluginMessage.Failure.INSUFFICIENT_PERMISSION.message);
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
      default -> user.sendMessage(PluginMessage.Failure.UNRECOGNIZED_PARAMETERS.message);
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
        user.sendMessage(Success.RELOAD.message);
      }
      default -> user.sendMessage(PluginMessage.Failure.UNRECOGNIZED_PARAMETER.message);
    }
  }

  /**
   * Opens an AethelItems main menu.
   *
   * @param user user
   */
  private void openMainMenu(Player user) {
    user.openInventory(AethelItemsInventory.openMainMenu(user, "view"));
    user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Inventory.AETHELITEMS_CATEGORY.inventory));
    user.setMetadata(PluginPlayerMeta.Namespace.PAGE.namespace,
        new FixedMetadataValue(Plugin.getInstance(), "0"));
  }

  private enum Permission {
    AETHELITEMS("aethel.aethelitems");

    public final String permission;

    Permission(String permission) {
      this.permission = permission;
    }
  }

  private enum Success {
    RELOAD(ChatColor.GREEN + "[Reloaded Aethel Items]");

    public final String message;

    Success(String message) {
      this.message = message;
    }
  }
}
