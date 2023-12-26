package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.inventories.forge.ForgeCreate;
import me.dannynguyen.aethel.inventories.forge.ForgeMain;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Forge is a command invocation that opens an inventory that allows the fabrication of items through clicking.
 *
 * @author Danny Nguyen
 * @version 1.0.9
 * @since 1.0.2
 */
public class Forge implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("Only players can use this command.");
      return true;
    }
    readForgeRequest(sender, args);
    return true;
  }

  /**
   * Checks if the forge command request was formatted correctly before interpreting its usage.
   *
   * @param sender command sender
   * @param args   parameters
   */
  private void readForgeRequest(CommandSender sender, String[] args) {
    Player player = (Player) sender;
    if (args.length == 1) {
      if (player.isOp()) {
        interpretForgeRequest(args[0].toLowerCase(), player);
      } else {
        player.sendMessage(ChatColor.RED + "Insufficient permissions.");
      }
    } else {
      player.openInventory(new ForgeMain().populateView(player, 0));
      player.setMetadata("menu", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-craft"));
      player.setMetadata("page", new FixedMetadataValue(AethelPlugin.getInstance(), "0"));
    }
  }

  /**
   * Either creates or modifies a recipe.
   *
   * @param parameter user input parameters
   * @param player    interacting player
   */
  private void interpretForgeRequest(String parameter, Player player) {
    switch (parameter) {
      case "create", "add" -> {
        player.openInventory(new ForgeCreate().createDefaultView(player));
        player.setMetadata("menu", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-create"));
      }
      case "modify", "edit" -> {
        player.openInventory(new ForgeMain().populateView(player, 0));
        player.setMetadata("menu", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-modify"));
        player.setMetadata("page", new FixedMetadataValue(AethelPlugin.getInstance(), "0"));
      }
      default -> player.sendMessage(ChatColor.RED + "Parameter not recognized.");
    }
  }
}
