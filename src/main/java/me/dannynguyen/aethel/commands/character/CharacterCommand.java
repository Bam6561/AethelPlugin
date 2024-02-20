package me.dannynguyen.aethel.commands.character;

import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.PluginEnum;
import me.dannynguyen.aethel.plugin.MenuMeta;
import me.dannynguyen.aethel.plugin.PlayerMeta;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Command invocation that allows the user to view a player's RPG character information.
 * <p>
 * From the Sheet, the user can also view the player's quests and collectibles.
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.10.5
 * @since 1.6.3
 */
public class CharacterCommand implements CommandExecutor {
  /**
   * Executes the Character command.
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
      if (user.hasPermission("aethel.character")) {
        readRequest(user, args);
      } else {
        user.sendMessage(PluginEnum.Message.INSUFFICIENT_PERMISSION.getMessage());
      }
    } else {
      sender.sendMessage(PluginEnum.Message.PLAYER_ONLY_COMMAND.getMessage());
    }
    return true;
  }

  /**
   * Checks if the command request was formatted correctly before opening a Sheet menu.
   *
   * @param user user
   * @param args user provided parameters
   */
  private void readRequest(Player user, String[] args) {
    switch (args.length) {
      case 0 -> openSheetSelf(user);
      case 1 -> openSheetOther(user, args[0]);
      default -> user.sendMessage(PluginEnum.Message.UNRECOGNIZED_PARAMETERS.getMessage());
    }
  }

  /**
   * Opens a Sheet menu belonging to the user.
   *
   * @param user user
   */
  private void openSheetSelf(Player user) {
    PluginData.pluginSystem.getPlayerMetadata().get(user).put(PlayerMeta.PLAYER, user.getName());
    user.openInventory(new SheetMenu(user, user).openMenu());
    PluginData.pluginSystem.getPlayerMetadata().get(user).put(PlayerMeta.INVENTORY, MenuMeta.CHARACTER_SHEET.getMeta());
  }

  /**
   * Opens a Sheet menu belonging to another player.
   *
   * @param user  user
   * @param owner requested player's name
   */
  private void openSheetOther(Player user, String owner) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (player.getName().equals(owner)) {
        Map<PlayerMeta, String> playerMeta = PluginData.pluginSystem.getPlayerMetadata().get(user);
        playerMeta.put(PlayerMeta.PLAYER, player.getName());
        user.openInventory(new SheetMenu(user, player).openMenu());
        playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.CHARACTER_SHEET.getMeta());
        return;
      }
    }
    user.sendMessage(ChatColor.RED + owner + " not online.");
  }
}
