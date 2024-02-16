package me.dannynguyen.aethel.commands.character;

import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.PluginEnum;
import me.dannynguyen.aethel.systems.MenuMeta;
import me.dannynguyen.aethel.systems.PlayerMeta;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command invocation that allows the user to view a player's RPG character information.
 * <p>
 * From the Character Sheet, the user can also view the player's quests and collectibles.
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.10.1
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
   * Checks if the command request was formatted correctly before opening a CharacterSheet menu.
   *
   * @param user user
   * @param args user provided parameters
   */
  private void readRequest(Player user, String[] args) {
    switch (args.length) {
      case 0 -> openCharacterSheet(user);
      default -> user.sendMessage(PluginEnum.Message.UNRECOGNIZED_PARAMETERS.getMessage());
    }
  }

  /**
   * Opens the player's CharacterSheet menu.
   *
   * @param user user
   */
  private void openCharacterSheet(Player user) {
    user.openInventory(new CharacterSheet(user).openMenu());
    PluginData.pluginSystem.getPlayerMetadata().get(user).put(PlayerMeta.INVENTORY, MenuMeta.CHARACTER_SHEET.getMeta());
  }
}
