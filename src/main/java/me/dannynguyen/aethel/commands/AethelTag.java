package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.readers.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

/**
 * AethelTag is a command invocation that allows the retrieval, addition,
 * or deletion of Aethel plugin tags to the player's main hand item.
 *
 * @author Danny Nguyen
 * @version 1.3.2
 * @since 1.2.6
 */
public class AethelTag implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("Player-only command.");
      return true;
    }

    Player player = (Player) sender;
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
   * Checks if the AethelTag request was formatted correctly before interpreting its intent.
   *
   * @param player interacting player
   * @param args   user provided parameters
   * @param item   item in main hand
   */
  private void readRequest(Player player, String[] args, ItemStack item) {
    switch (args.length) {
      case 0 -> player.sendMessage(ChatColor.RED + "No parameters provided.");
      case 1 -> {
        if (args[0].equalsIgnoreCase("get")) {
          getAethelTags(player, item);
        } else {
          player.sendMessage(ChatColor.RED + "Unrecognized parameter.");
        }
      }
      case 2 -> {
        if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("rm")) {
          removeAethelTag(player, args[1], item);
        } else {
          player.sendMessage(ChatColor.RED + "Unrecognized parameters.");
        }
      }
      case 3 -> {
        if (args[0].equalsIgnoreCase("set")) {
          setAethelTag(player, args, item);
        } else {
          player.sendMessage(ChatColor.RED + "Unrecognized parameters.");
        }
      }
      default -> player.sendMessage(ChatColor.RED + "Unrecognized parameters.");
    }
  }

  /**
   * Responds with the item's Aethel tags.
   *
   * @param player interacting player
   * @param item   item in main hand
   */
  private void getAethelTags(Player player, ItemStack item) {
    String response = new ItemReader().readAethelTags(item);
    if (!response.isEmpty()) {
      player.sendMessage(response);
    } else {
      player.sendMessage(ChatColor.RED + "No tags found.");
    }
  }

  /**
   * Removes an Aethel tag from the item.
   *
   * @param player interacting player
   * @param tag    Aethel tag to be removed
   * @param item   item in main hand
   */
  private void removeAethelTag(Player player, String tag, ItemStack item) {
    ItemMeta meta = item.getItemMeta();
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
    NamespacedKey namespacedKey = new NamespacedKey(AethelPlugin.getInstance(), "aethel." + tag);

    if (dataContainer.has(namespacedKey, PersistentDataType.STRING)) {
      dataContainer.remove(namespacedKey);
      item.setItemMeta(meta);
      player.sendMessage(ChatColor.RED + "[Removed] " + ChatColor.AQUA + tag);
    } else {
      player.sendMessage(ChatColor.RED + "Nonexistent tag.");
    }
  }

  /**
   * Adds or sets an Aethel tag to the item.
   *
   * @param player interacting player
   * @param args   user provided parameters
   * @param item   item in main hand
   */
  private void setAethelTag(Player player, String[] args, ItemStack item) {
    ItemMeta meta = item.getItemMeta();
    NamespacedKey namespacedKey = new NamespacedKey(AethelPlugin.getInstance(), "aethel." + args[1]);
    meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, args[2]);
    item.setItemMeta(meta);
    player.sendMessage(ChatColor.GREEN + "[Set] "
        + ChatColor.AQUA + args[1].toLowerCase() + " " + ChatColor.WHITE + args[2]);
  }
}
