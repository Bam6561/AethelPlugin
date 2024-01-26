package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.inventories.aethelItems.AethelItemsMain;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * AethelItems is a command invocation that allows the retrieval of items through clicking.
 * <p>
 * Additional Parameters:
 * - "reload", "r": reloads items into memory
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.6.6
 * @since 1.3.2
 */
public class AethelItems implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player player)) {
      sender.sendMessage("Player-only command.");
      return true;
    }

    if (player.isOp()) {
      readRequest(player, args);
    } else {
      player.sendMessage(ChatColor.RED + "Insufficient permissions.");
    }
    return true;
  }

  /**
   * Checks if the AethelItem command request was formatted correctly before interpreting its usage.
   *
   * @param player interacting player
   * @param args   user provided parameters
   */
  private void readRequest(Player player, String[] args) {
    switch (args.length) {
      case 0 -> openAethelItems(player);
      case 1 -> readParameter(player, args[0].toLowerCase());
      default -> player.sendMessage(ChatColor.RED + "Unrecognized parameters.");
    }
  }

  /**
   * Checks if the action is "reload" before reloading items into memory.
   *
   * @param player interacting player
   * @param action type of action
   */
  private void readParameter(Player player, String action) {
    switch (action) {
      case "reload", "r" -> {
        AethelResources.aethelItemsData.loadItems();
        player.sendMessage(ChatColor.GREEN + "[Reloaded Aethel Items]");
      }
      default -> player.sendMessage(ChatColor.RED + "Unrecognized parameter.");
    }
  }

  /**
   * Opens an AethelItemsMain inventory with the intent to view item categories.
   *
   * @param player interacting player
   */
  private void openAethelItems(Player player) {
    player.openInventory(AethelItemsMain.openItemMainPage(player, "view"));
    player.setMetadata("inventory",
        new FixedMetadataValue(AethelPlugin.getInstance(), "aethelitems.category"));
    player.setMetadata("page", new FixedMetadataValue(AethelPlugin.getInstance(), "0"));
  }
}