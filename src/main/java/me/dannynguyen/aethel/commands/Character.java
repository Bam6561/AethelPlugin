package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.inventories.character.CharacterSheet;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Character is a command invocation that opens the player's RPG character sheet.
 * <p>
 * From the character sheet, the player can also access their quests, collectibles, and settings.
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.6.3
 * @since 1.6.3
 */
public class Character implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player player)) {
      sender.sendMessage("Player-only command.");
      return true;
    }
    readRequest(player, args);
    return true;
  }

  /**
   * Checks if the command request was formatted correctly before opening a character sheet.
   *
   * @param player interacting player
   * @param args   user provided parameters
   */
  private void readRequest(Player player, String[] args) {
    switch (args.length) {
      case 0 -> openCharacterSheet(player);
      default -> player.sendMessage(ChatColor.RED + "Unrecognized parameters.");
    }
  }

  /**
   * Opens the player's character sheet.
   *
   * @param player interacting player
   */
  private void openCharacterSheet(Player player) {
    player.openInventory(CharacterSheet.openCharacterSheet(player));
    player.setMetadata("inventory",
        new FixedMetadataValue(AethelPlugin.getInstance(), "character.sheet"));
  }
}
