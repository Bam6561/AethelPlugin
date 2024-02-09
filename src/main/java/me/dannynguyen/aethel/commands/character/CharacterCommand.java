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

/**
 * Character is a command invocation that opens a player's RPG character sheet.
 * <p>
 * From the character sheet, the player can also access their quests, collectibles, and settings.
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.8.4
 * @since 1.6.3
 */
public class CharacterCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player user)) {
      sender.sendMessage(PluginMessage.Failure.PLAYER_ONLY_COMMAND.message);
      return true;
    }

    if (user.hasPermission(Permission.CHARACTER.permission)) {
      readRequest(user, args);
    } else {
      user.sendMessage(PluginMessage.Failure.INSUFFICIENT_PERMISSION.message);
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
      default -> user.sendMessage(PluginMessage.Failure.UNRECOGNIZED_PARAMETERS.message);
    }
  }

  /**
   * Opens the user's character sheet.
   *
   * @param user user
   */
  private void openCharacterSheet(Player user) {
    user.openInventory(CharacterSheet.openCharacterSheet(user));
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(),
        new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.CHARACTER_SHEET.menu));
  }

  private enum Permission {
    CHARACTER("aethel.character");

    public final String permission;

    Permission(String permission) {
      this.permission = permission;
    }
  }
}
