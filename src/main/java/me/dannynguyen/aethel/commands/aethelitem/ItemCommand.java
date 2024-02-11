package me.dannynguyen.aethel.commands.aethelitem;

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
import org.jetbrains.annotations.NotNull;

/**
 * Command invocation that allows the user to obtain items through clicking.
 * <p>
 * Additional Parameters:
 * - "reload", "r": reloads items into memory
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.9.14
 * @since 1.3.2
 */
public class ItemCommand implements CommandExecutor {
  /**
   * Executes the AethelItem command.
   *
   * @param sender  command source
   * @param command executed command
   * @param label   command alias used
   * @param args    command arguments
   * @return true if a valid command
   */
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
    if (sender instanceof Player user) {
      if (user.hasPermission("aethel.aethelitem")) {
        readRequest(user, args);
      } else {
        user.sendMessage(PluginMessage.Failure.INSUFFICIENT_PERMISSION.message);
      }
    } else {
      sender.sendMessage(PluginMessage.Failure.PLAYER_ONLY_COMMAND.message);
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
   * Opens the AethelItem main menu.
   *
   * @param user user
   */
  private void openMainMenu(Player user) {
    user.setMetadata(PluginPlayerMeta.CATEGORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), " "));
    user.openInventory(new ItemMenu(user, ItemMenuAction.VIEW).openMainMenu());
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.AETHELITEM_CATEGORY.menu));
    user.setMetadata(PluginPlayerMeta.PAGE.getMeta(), new FixedMetadataValue(Plugin.getInstance(), "0"));
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
        PluginData.itemRegistry.loadData();
        user.sendMessage(ChatColor.GREEN + "[Reloaded Aethel Items]");
      }
      default -> user.sendMessage(PluginMessage.Failure.UNRECOGNIZED_PARAMETER.message);
    }
  }
}
