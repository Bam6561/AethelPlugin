package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.systems.plugin.PluginMessage;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

/**
 * Command invocation that allows the user to retrieve,
 * set, or remove Aethel tags to their main hand item.
 * <p>
 * Additional Parameters:
 * - "get", "g": reads the item's tags
 * - "set", "s": sets the item's tag
 * - "remove", "r": removes the item's tag
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.9.21
 * @since 1.2.6
 */
public class AethelTagsCommand implements CommandExecutor {
  /**
   * Executes the AethelTags command.
   *
   * @param sender  command source
   * @param command executed command
   * @param label   command alias used
   * @param args    command arguments
   * @return true if a valid command
   */
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
    if (sender instanceof Player user) {
      if (user.hasPermission("aethel.aetheltags")) {
        ItemStack item = user.getInventory().getItemInMainHand();
        if (ItemReader.isNotNullOrAir(item)) {
          readRequest(user, args, item);
        } else {
          user.sendMessage(PluginMessage.NO_MAIN_HAND_ITEM.getMessage());
        }
      } else {
        user.sendMessage(PluginMessage.INSUFFICIENT_PERMISSION.getMessage());
      }
    } else {
      sender.sendMessage(PluginMessage.PLAYER_ONLY_COMMAND.getMessage());
    }
    return true;
  }

  /**
   * Checks if the command request was formatted correctly before interpreting its usage.
   *
   * @param user user
   * @param args user provided parameters
   * @param item main hand item
   */
  private void readRequest(Player user, String[] args, ItemStack item) {
    int numberOfParameters = args.length;
    String action = "";
    if (numberOfParameters > 0) {
      action = args[0].toLowerCase();
    }
    switch (numberOfParameters) {
      case 0 -> user.sendMessage(PluginMessage.NO_PARAMETERS.getMessage());
      case 1 -> {
        if (action.equals("g") || action.equals("get")) {
          getAethelTags(user, item);
        } else {
          user.sendMessage(PluginMessage.UNRECOGNIZED_PARAMETER.getMessage());
        }
      }
      case 2 -> {
        if (action.equals("r") || action.equals("remove")) {
          removeAethelTag(user, args[1], item);
        } else {
          user.sendMessage(PluginMessage.UNRECOGNIZED_PARAMETERS.getMessage());
        }
      }
      case 3 -> {
        if (action.equals("set") || action.equals("s")) {
          setAethelTag(user, args, item);
        } else {
          user.sendMessage(PluginMessage.UNRECOGNIZED_PARAMETERS.getMessage());
        }
      }
      default -> user.sendMessage(PluginMessage.UNRECOGNIZED_PARAMETERS.getMessage());
    }
  }

  /**
   * Responds with the item's Aethel tags.
   *
   * @param user user
   * @param item main hand item
   */
  private void getAethelTags(Player user, ItemStack item) {
    String response = ItemReader.readAethelTags(item);
    if (!response.isEmpty()) {
      user.sendMessage(ChatColor.GREEN + "[Get Tags] " + response);
    } else {
      user.sendMessage(ChatColor.RED + "No tags found.");
    }
  }

  /**
   * Removes an Aethel tag from the item.
   *
   * @param user user
   * @param tag  Aethel tag to be removed
   * @param item main hand item
   */
  private void removeAethelTag(Player user, String tag, ItemStack item) {
    if (!tag.contains("attribute.")) {
      ItemMeta meta = item.getItemMeta();
      PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
      NamespacedKey namespacedKey = new NamespacedKey(Plugin.getInstance(), "aethel." + tag);

      if (dataContainer.has(namespacedKey, PersistentDataType.STRING)) {
        dataContainer.remove(namespacedKey);
        item.setItemMeta(meta);
        user.sendMessage(ChatColor.RED + "[Removed Tag] " + ChatColor.AQUA + tag);
      } else {
        user.sendMessage(ChatColor.RED + "Tag does not exist.");
      }
    } else {
      user.sendMessage(ChatColor.RED + "Use the ItemEditor to modify Aethel attributes.");
    }
  }

  /**
   * Sets the item's Aethel tag.
   *
   * @param user user
   * @param args user provided parameters
   * @param item main hand item
   */
  private void setAethelTag(Player user, String[] args, ItemStack item) {
    ItemMeta meta = item.getItemMeta();
    NamespacedKey namespacedKey = new NamespacedKey(Plugin.getInstance(), "aethel." + args[1]);
    meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, args[2]);
    item.setItemMeta(meta);
    user.sendMessage(ChatColor.GREEN + "[Set Tag] " + ChatColor.AQUA + args[1].toLowerCase() + " " + ChatColor.WHITE + args[2]);
  }
}
