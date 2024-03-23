package me.dannynguyen.aethel.commands.aetheltag;

import me.dannynguyen.aethel.plugin.enums.Message;
import me.dannynguyen.aethel.plugin.enums.PluginKey;
import me.dannynguyen.aethel.util.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Command invocation that allows the user to retrieve, set, or remove
 * {@link PluginKey Aethel tags} to their main hand item.
 * <p>
 * Registered through {@link me.dannynguyen.aethel.Plugin}.
 * <p>
 * Parameters:
 * <ul>
 *  <li>"get", "g": reads the item's {@link PluginKey tags}
 *  <li>"set", "s": sets the item's {@link PluginKey tag}
 *  <li>"remove", "r": removes the item's {@link PluginKey tag}
 * </ul>
 *
 * @author Danny Nguyen
 * @version 1.17.14
 * @since 1.2.6
 */
public class TagCommand implements CommandExecutor {
  /**
   * No parameter constructor.
   */
  public TagCommand() {
  }

  /**
   * Executes the AethelTag command.
   *
   * @param sender  command source
   * @param command executed command
   * @param label   command alias used
   * @param args    command parameters
   * @return true if a valid command
   */
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
    if (sender instanceof Player user) {
      if (user.hasPermission("aethel.aetheltag")) {
        ItemStack item = user.getInventory().getItemInMainHand();
        if (ItemReader.isNotNullOrAir(item)) {
          readRequest(user, args, item);
        } else {
          user.sendMessage(Message.NO_MAIN_HAND_ITEM.getMessage());
        }
      } else {
        user.sendMessage(Message.INSUFFICIENT_PERMISSION.getMessage());
      }
    } else {
      sender.sendMessage(Message.PLAYER_ONLY_COMMAND.getMessage());
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
      case 0 -> user.sendMessage(Message.NO_PARAMETERS.getMessage());
      case 1 -> {
        switch (action) {
          case "g", "get" -> getAethelTags(user, item);
          default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETER.getMessage());
        }
      }
      case 2 -> {
        switch (action) {
          case "r", "remove" -> removeAethelTag(user, args[1], item);
          default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
        }
      }
      default -> {
        switch (action) {
          case "s", "set" -> setAethelTag(user, args, item);
          default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
        }
      }
    }
  }

  /**
   * Responds with the item's {@link PluginKey Aethel tags}.
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
   * Removes the {@link PluginKey Aethel tag} from the item.
   *
   * @param user user
   * @param tag  {@link PluginKey Aethel tag} to be removed
   * @param item main hand item
   */
  private void removeAethelTag(Player user, String tag, ItemStack item) {
    if (new TagModifier(user, item, tag).removeTag()) {
      user.sendMessage(ChatColor.RED + "[Removed Tag] " + ChatColor.AQUA + tag);
    } else {
      user.sendMessage(ChatColor.RED + "Tag does not exist.");
    }
  }

  /**
   * Sets the {@link PluginKey Aethel tag} to the item.
   *
   * @param user user
   * @param args user provided parameters
   * @param item main hand item
   */
  private void setAethelTag(Player user, String[] args, ItemStack item) {
    String tag = args[1];
    StringBuilder value = new StringBuilder();
    if (args.length == 3) {
      value = new StringBuilder(args[2]);
    } else {
      for (int i = 2; i < args.length; i++) {
        value.append(args[i]).append(" ");
      }
    }
    new TagModifier(user, item, tag).setTag(value.toString());
  }
}