package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.inventories.forge.ForgeMain;
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
 * - "reload", "rl": reloads forge recipes into memory
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.5.5
 * @since 1.0.2
 */
public class Forge implements CommandExecutor {
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
   * Checks if the command request was formatted correctly before interpreting its usage.
   *
   * @param player interacting player
   * @param args   player provided parameters
   */
  private void readRequest(Player player, String[] args) {
    switch (args.length) {
      case 0 -> openForgeCraftInventory(player);
      case 1 -> {
        if (player.isOp()) {
          interpretParameter(player, args[0].toLowerCase());
        } else {
          player.sendMessage(ChatColor.RED + "Insufficient permissions.");
        }
      }
      default -> player.sendMessage(ChatColor.RED + "Unrecognized parameters.");
    }
  }

  /**
   * Either modifies recipes or reloads them into memory.
   *
   * @param player interacting player
   * @param action type of interaction
   */
  private void interpretParameter(Player player, String action) {
    switch (action) {
      case "edit" -> openForgeModifyInventory(player);
      case "reload", "rl" -> {
        AethelResources.forgeRecipeData.loadRecipes();
        player.sendMessage(ChatColor.GREEN + "[Reloaded] " + ChatColor.WHITE + "Forge Recipes");
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
    player.setMetadata("action", new FixedMetadataValue(AethelPlugin.getInstance(), "craft"));

    player.openInventory(ForgeMain.openForgeMainPage(player, "craft"));
    player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-category"));
    player.setMetadata("page", new FixedMetadataValue(AethelPlugin.getInstance(), "0"));
  }

  /**
   * Opens a ForgeMain inventory with the intent to modify recipes.
   *
   * @param player interacting player
   */
  private void openForgeModifyInventory(Player player) {
    player.setMetadata("action", new FixedMetadataValue(AethelPlugin.getInstance(), "modify"));

    player.openInventory(ForgeMain.openForgeMainPage(player, "modify"));
    player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-category"));
    player.setMetadata("page", new FixedMetadataValue(AethelPlugin.getInstance(), "0"));
  }
}
