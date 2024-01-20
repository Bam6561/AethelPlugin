package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.inventories.ItemEditorMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * ItemEditor is a command invocation that allows
 * editing of the user's main hand item's metadata.
 *
 * @author Danny Nguyen
 * @version 1.6.7
 * @since 1.6.7
 */
public class ItemEditor implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player player)) {
      sender.sendMessage("Player-only command.");
      return true;
    }

    if (player.isOp()) {
      ItemStack item = player.getInventory().getItemInMainHand();
      if (item.getType() != Material.AIR) {
        readRequest(player, args, item);
      } else {
        player.sendMessage(ChatColor.RED + "No main hand item.");
      }
    } else {
      player.sendMessage(ChatColor.RED + "Insufficient permissions.");
    }
    return true;
  }

  /**
   * Checks if the command request was formatted correctly before opening an item editor menu.
   *
   * @param player interacting player
   * @param args   user provided parameters
   * @param item   interacting item
   */
  private void readRequest(Player player, String[] args, ItemStack item) {
    switch (args.length) {
      case 0 -> openEditorMenu(player, item);
      default -> player.sendMessage(ChatColor.RED + "Unrecognized parameters.");
    }
  }

  /**
   * Opens an ItemEditorMenu inventory.
   *
   * @param player interacting player
   * @param item   interacting item
   */
  private void openEditorMenu(Player player, ItemStack item) {
    AethelResources.itemEditorData.getEditedItemMap().put(player, item);
    player.openInventory(ItemEditorMenu.openEditorMenu(player, item));
    player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "itemeditor.menu"));
  }
}
