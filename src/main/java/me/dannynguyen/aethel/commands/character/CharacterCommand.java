package me.dannynguyen.aethel.commands.character;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.listeners.InventoryMenuListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

/**
 * Command invocation that allows the user to view a player's RPG character information.
 * <p>
 * From the main menu, the user can also view the player's quests and collectibles.
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.9.10
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
        user.sendMessage(PluginMessage.Failure.INSUFFICIENT_PERMISSION.message);
      }
    } else {
      sender.sendMessage(PluginMessage.Failure.PLAYER_ONLY_COMMAND.message);
    }
    return true;
  }

  /**
   * Checks if the command request was formatted correctly before opening a Character main menu.
   *
   * @param user user
   * @param args user provided parameters
   */
  private void readRequest(Player user, String[] args) {
    switch (args.length) {
      case 0 -> openMainMenu(user);
      default -> user.sendMessage(PluginMessage.Failure.UNRECOGNIZED_PARAMETERS.message);
    }
  }

  /**
   * Opens the player's Character main menu.
   *
   * @param user user
   */
  private void openMainMenu(Player user) {
    user.openInventory(new CharacterMenu(user).openMainMenu());
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.CHARACTER_SHEET.menu));
  }
}
