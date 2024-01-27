package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.enums.PluginPermission;
import me.dannynguyen.aethel.inventories.character.CharacterSheet;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Character is a command invocation that opens a player's RPG character sheet.
 * <p>
 * From the character sheet, the player can also access their quests, collectibles, and settings.
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.7.7
 * @since 1.6.3
 */
public class Character implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player user)) {
      sender.sendMessage(PluginMessage.PLAYER_ONLY_COMMAND.message);
      return true;
    }

    if (user.hasPermission(PluginPermission.CHARACTER.permission)) {
      readRequest(user, args);
    } else {
      user.sendMessage(PluginMessage.INSUFFICIENT_PERMISSION.message);
    }
    return true;
  }

  /**
   * Checks if the command request was formatted correctly before opening a character sheet.
   *
   * @param user user
   * @param args user provided parameters
   */
  private void readRequest(Player user, String[] args) {
    switch (args.length) {
      case 0 -> openCharacterSheet(user);
      default -> user.sendMessage(PluginMessage.UNRECOGNIZED_PARAMETERS.message);
    }
  }

  /**
   * Opens the user's character sheet.
   *
   * @param user user
   */
  private void openCharacterSheet(Player user) {
    user.openInventory(CharacterSheet.openCharacterSheet(user));
    user.setMetadata(PluginPlayerMeta.Container.INVENTORY.name,
        new FixedMetadataValue(Plugin.getInstance(), PluginPlayerMeta.Value.CHARACTER_SHEET.value));
  }
}
