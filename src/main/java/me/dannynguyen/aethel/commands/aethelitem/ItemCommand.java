package me.dannynguyen.aethel.commands.aethelitem;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.systems.plugin.MenuMeta;
import me.dannynguyen.aethel.systems.plugin.Message;
import me.dannynguyen.aethel.systems.plugin.PlayerMeta;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Command invocation that allows the user to obtain items through clicking.
 * <p>
 * Additional Parameters:
 * - "reload", "r": reloads items into memory
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.14.5
 * @since 1.3.2
 */
public class ItemCommand implements CommandExecutor {
  /**
   * Executes the AethelItem command.
   *
   * @param sender  command source
   * @param command executed command
   * @param label   command alias used
   * @param args    command parameters
   * @return true if a valid command
   */
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
    if (sender instanceof Player user) {
      if (user.hasPermission("aethel.aethelitem")) {
        readRequest(user, args);
      } else {
        user.sendMessage(Message.INSUFFICIENT_PERMISSION.getMessage());
      }
    } else {
      sender.sendMessage(Message.PLAYER_ONLY_COMMAND.getMessage());
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
      case 0 -> openMenu(user);
      case 1 -> readParameter(user, args[0].toLowerCase());
      default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
    }
  }

  /**
   * Opens the AethelItem main menu.
   *
   * @param user user
   */
  private void openMenu(Player user) {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(user.getUniqueId());
    playerMeta.put(PlayerMeta.CATEGORY, " ");
    user.openInventory(new ItemMenu(user, ItemMenuAction.VIEW).openMenu());
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.AETHELITEM_CATEGORY.getMeta());
    playerMeta.put(PlayerMeta.PAGE, "0");
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
        Plugin.getData().getItemRegistry().loadData();
        user.sendMessage(ChatColor.GREEN + "[Reloaded Aethel Items]");
      }
      default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETER.getMessage());
    }
  }
}
