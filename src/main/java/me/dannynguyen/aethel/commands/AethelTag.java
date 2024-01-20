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
 * <p>
 * Additional Parameters:
 * - "get", "g": reads the item's tags
 * - "set", "s": sets the item's tag
 * - "remove", "r": removes the item's tag
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.6.6
 * @since 1.2.6
 */
public class AethelTag implements CommandExecutor {
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
   * Checks if the command request was formatted correctly before interpreting its usage.
   *
   * @param player interacting player
   * @param args   player provided parameters
   * @param item   item in main hand
   */
  private void readRequest(Player player, String[] args, ItemStack item) {
    String action = args[0].toLowerCase();
    switch (args.length) {
      case 0 -> player.sendMessage(ChatColor.RED + "No parameters provided.");
      case 1 -> {
        if (action.equals("get") || action.equals("g")) {
          getAethelTags(player, item);
        } else {
          player.sendMessage(ChatColor.RED + "Unrecognized parameter.");
        }
      }
      case 2 -> {
        if (action.equals("remove") || action.equals("r")) {
          removeAethelTag(player, args[1], item);
        } else {
          player.sendMessage(ChatColor.RED + "Unrecognized parameters.");
        }
      }
      case 3 -> {
        if (action.equals("set") || action.equals("s")) {
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
    String response = ItemReader.readAethelTags(item);
    if (!response.isEmpty()) {
      player.sendMessage(ChatColor.GREEN + "[Get] " + response);
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
   * @param args   player provided parameters
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
