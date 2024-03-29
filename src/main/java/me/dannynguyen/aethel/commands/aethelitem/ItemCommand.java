package me.dannynguyen.aethel.commands.aethelitem;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.listeners.MenuEvent;
import me.dannynguyen.aethel.plugin.PluginPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command invocation that allows the user to obtain
 * {@link ItemRegistry.Item items} through clicking.
 * <p>
 * Registered through {@link Plugin}.
 * <p>
 * Parameters:
 * <ul>
 *  <li>"": opens {@link ItemMenu}
 *  <li>"reload", "r": reloads {@link ItemRegistry.Item items} into {@link ItemRegistry}
 * </ul>
 *
 * @author Danny Nguyen
 * @version 1.17.19
 * @since 1.3.2
 */
public class ItemCommand implements CommandExecutor {
  /**
   * No parameter constructor.
   */
  public ItemCommand() {
  }

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
   * Opens the {@link ItemMenu}.
   *
   * @param user user
   */
  private void openMenu(Player user) {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId());
    pluginPlayer.setCategory("");
    user.openInventory(new ItemMenu(user, ItemMenu.Action.VIEW).getMainMenu());
    pluginPlayer.setMenu(MenuEvent.Menu.AETHELITEM_CATEGORY);
    pluginPlayer.setPage(0);
  }

  /**
   * Checks if the action is "reload" before reloading
   * {@link ItemRegistry.Item items} into {@link ItemRegistry}.
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
