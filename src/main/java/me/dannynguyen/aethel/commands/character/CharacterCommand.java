package me.dannynguyen.aethel.commands.character;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.listeners.MenuEvent;
import me.dannynguyen.aethel.plugin.PluginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Command invocation that allows the user to view a player's RPG character data.
 * <p>
 * From the {@link SheetMenu}, the user can also view the player's quests and collectibles.
 * <p>
 * Registered through {@link Plugin}.
 * <p>
 * Parameters:
 * <ul>
 *  <li>"": opens a {@link SheetMenu} belonging to the user
 *  <li>playerName: opens a {@link SheetMenu} belonging to the player
 * </ul>
 *
 * @author Danny Nguyen
 * @version 1.18.0
 * @since 1.6.3
 */
public class CharacterCommand implements CommandExecutor {
  /**
   * No parameter constructor.
   */
  public CharacterCommand() {
  }

  /**
   * Executes the Character command.
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
      if (user.hasPermission("aethel.character")) {
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
   * Checks if the command request was formatted correctly before opening a {@link SheetMenu}.
   *
   * @param user user
   * @param args user provided parameters
   */
  private void readRequest(Player user, String[] args) {
    switch (args.length) {
      case 0 -> openSheetSelf(user);
      case 1 -> openSheetOther(user, args[0]);
      default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
    }
  }

  /**
   * Opens a {@link SheetMenu} belonging to the user.
   *
   * @param user user
   */
  private void openSheetSelf(Player user) {
    UUID target = user.getUniqueId();
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(target);
    pluginPlayer.setTarget(target);
    user.openInventory(new SheetMenu(user, user).getMainMenu());
    pluginPlayer.setMenu(MenuEvent.Menu.CHARACTER_SHEET);
  }

  /**
   * Opens a {@link SheetMenu} belonging to another player.
   *
   * @param user            user
   * @param requestedPlayer requested player's name
   */
  private void openSheetOther(Player user, String requestedPlayer) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (player.getName().equals(requestedPlayer)) {
        UUID target = player.getUniqueId();
        PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId());
        pluginPlayer.setTarget(target);
        user.openInventory(new SheetMenu(user, player).getMainMenu());
        pluginPlayer.setMenu(MenuEvent.Menu.CHARACTER_SHEET);
        return;
      }
    }
    user.sendMessage(ChatColor.RED + requestedPlayer + " not online.");
  }
}
