package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.gui.ForgeCraft;
import me.dannynguyen.aethel.gui.ForgeCreate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Forge is a command invocation that opens an inventory that allows the fabrication of items through clicking.
 *
 * @author Danny Nguyen
 * @version 1.0.6
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
        interpretForgeMenu(args[0].toLowerCase(), player);
      } else {
        player.sendMessage("Insufficient permissions.");
      }
    } else {
      player.openInventory(new ForgeCraft(player).populateView(player, 0));
      player.setMetadata("menu", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-craft"));
      player.setMetadata("page", new FixedMetadataValue(AethelPlugin.getInstance(), "0"));
    }
  }

  /**
   * Either creates, modifies, or deletes a forge recipe.
   *
   * @param parameter user input parameters
   * @param player    interacting player
   */
  private void interpretForgeMenu(String parameter, Player player) {
    switch (parameter) {
      case "create" -> {
        player.openInventory(new ForgeCreate(player).getDefaultView());
        player.setMetadata("menu", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-create"));
      }
    }
  }
}
