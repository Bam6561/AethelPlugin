package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.utility.ItemReader;
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
 * AethelTags is a command invocation that allows the user to
 * retrieve, set, or remove Aethel tags to their main hand item.
 * <p>
 * Additional Parameters:
 * - "get", "g": reads the item's tags
 * - "set", "s": sets the item's tag
 * - "remove", "r": removes the item's tag
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.8.4
 * @since 1.2.6
 */
public class AethelTagsCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player user)) {
      sender.sendMessage(PluginMessage.Failure.PLAYER_ONLY_COMMAND.message);
      return true;
    }

    if (user.hasPermission(Permission.AETHELTAGS.permission)) {
      ItemStack item = user.getInventory().getItemInMainHand();
      if (item.getType() != Material.AIR) {
        readRequest(user, args, item);
      } else {
        user.sendMessage(PluginMessage.Failure.NO_MAIN_HAND_ITEM.message);
      }
    } else {
      user.sendMessage(PluginMessage.Failure.INSUFFICIENT_PERMISSION.message);
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
    if (numberOfParameters != 0) {
      action = args[0].toLowerCase();
    }
    switch (numberOfParameters) {
      case 0 -> user.sendMessage(PluginMessage.Failure.NO_PARAMETERS.message);
      case 1 -> {
        if (action.equals("get") || action.equals("g")) {
          getAethelTags(user, item);
        } else {
          user.sendMessage(PluginMessage.Failure.UNRECOGNIZED_PARAMETER.message);
        }
      }
      case 2 -> {
        if (action.equals("remove") || action.equals("r")) {
          removeAethelTag(user, args[1], item);
        } else {
          user.sendMessage(PluginMessage.Failure.UNRECOGNIZED_PARAMETERS.message);
        }
      }
      case 3 -> {
        if (action.equals("set") || action.equals("s")) {
          setAethelTag(user, args, item);
        } else {
          user.sendMessage(PluginMessage.Failure.UNRECOGNIZED_PARAMETERS.message);
        }
      }
      default -> user.sendMessage(PluginMessage.Failure.UNRECOGNIZED_PARAMETERS.message);
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
      user.sendMessage(Success.GET_TAGS.message + response);
    } else {
      user.sendMessage(Failure.NO_TAGS.message);
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
    ItemMeta meta = item.getItemMeta();
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
    NamespacedKey namespacedKey = new NamespacedKey(Plugin.getInstance(), "aethel." + tag);

    if (dataContainer.has(namespacedKey, PersistentDataType.STRING)) {
      dataContainer.remove(namespacedKey);
      item.setItemMeta(meta);
    }
    user.sendMessage(Success.REMOVE_TAG.message + ChatColor.AQUA + tag);
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
    user.sendMessage(Success.SET_TAG.message + ChatColor.AQUA +
        args[1].toLowerCase() + " " + ChatColor.WHITE + args[2]);
  }

  private enum Permission {
    AETHELTAGS("aethel.aetheltags");

    public final String permission;

    Permission(String permission) {
      this.permission = permission;
    }
  }

  private enum Success {
    GET_TAGS(ChatColor.GREEN + "[Get Tags] "),
    SET_TAG(ChatColor.GREEN + "[Set Tag] "),
    REMOVE_TAG(ChatColor.RED + "[Removed Tag] ");

    public final String message;

    Success(String message) {
      this.message = message;
    }
  }

  private enum Failure {
    NO_TAGS(ChatColor.RED + "No tags found.");

    public final String message;

    Failure(String message) {
      this.message = message;
    }
  }
}
