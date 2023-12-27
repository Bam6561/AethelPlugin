package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.inventories.forge.ForgeMain;
import me.dannynguyen.aethel.readers.ForgeRecipeReader;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Forge is a command invocation that opens an inventory to allow the fabrication of items through clicking.
 * <p>
 * Additional Parameters:
 * - "edit": allows the user to create, modify, or delete forge recipes
 * - "reload": reloads forge recipes into memory
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.1.3
 * @since 1.0.2
 */
public class Forge implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("Only players can use this command.");
      return true;
    }
    readRequest(sender, args);
    return true;
  }

  /**
   * Checks if the forge command request was formatted correctly before interpreting its usage.
   *
   * @param sender command sender
   * @param args   parameters
   */
  private void readRequest(CommandSender sender, String[] args) {
    Player player = (Player) sender;
    switch (args.length) {
      case 0 -> openForgeCraftInventory(player);
      case 1 -> interpretParameter(args, player);
      default -> player.sendMessage(ChatColor.RED + "Unrecognized parameters.");
    }
  }

  /**
   * Either modifies recipes or reloads them into memory.
   *
   * @param args   user provided parameters
   * @param player interacting player
   */
  private void interpretParameter(String[] args, Player player) {
    switch (args[0]) {
      case "edit" -> {
        if (player.isOp()) {
          openForgeModifyInventory(player);
        } else {
          player.sendMessage(ChatColor.RED + "Insufficient permissions.");
        }
      }
      case "reload" -> {
        if (player.isOp()) {
          new ForgeRecipeReader().loadForgeRecipes();
          player.sendMessage(ChatColor.GREEN + "[Reloaded] " + ChatColor.WHITE + "Forge Recipes");
        } else {
          player.sendMessage(ChatColor.RED + "Insufficient permissions.");
        }
      }
      default -> player.sendMessage(ChatColor.RED + "Unrecognized parameter.");
    }
  }

  /**
   * Opens a ForgeMain inventory with the intent to craft recipes.
   *
   * @param player interacting player
   */
  private void openForgeCraftInventory(Player player) {
    player.openInventory(new ForgeMain().processPageToDisplay(player, "craft", 0));
    player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-craft"));
  }

  /**
   * Opens a ForgeMain inventory with the intent to modify recipes.
   *
   * @param player interacting player
   */
  private void openForgeModifyInventory(Player player) {
    player.openInventory(new ForgeMain().processPageToDisplay(player, "modify", 0));
    player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-modify"));
  }
}
