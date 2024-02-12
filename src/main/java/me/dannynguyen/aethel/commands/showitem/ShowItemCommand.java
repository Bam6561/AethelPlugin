package me.dannynguyen.aethel.commands.showitem;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.listeners.InventoryMenuListener;
import me.dannynguyen.aethel.utility.ItemReader;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Item;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

/**
 * Command invocation that shows the user's main hand item to global chat.
 * <p>
 * Additional Parameters:
 * - "past", "p": opens a menu with the last 27 shown items
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.9.13
 * @since 1.4.5
 */
public class ShowItemCommand implements CommandExecutor {
  /**
   * Executes the ShowItem command.
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
      if (user.hasPermission("aethel.showitem")) {
        readRequest(user, args);
      } else {
        user.sendMessage(PluginMessage.Failure.INSUFFICIENT_PERMISSION.message);
      }
    } else {
      sender.sendMessage(PluginMessage.Failure.PLAYER_ONLY_COMMAND.message);
    }
    return true;
  }

  /**
   * Checks if the command request was formatted correctly before interpreting its usage.
   *
   * @param user user
   * @param args user provided parameters
   */
  private void readRequest(Player user, String[] args) {
    switch (args.length) {
      case 0 -> showItem(user);
      case 1 -> interpretParameter(user, args[0].toLowerCase());
      default -> user.sendMessage(PluginMessage.Failure.UNRECOGNIZED_PARAMETERS.message);
    }
  }

  /**
   * Sends a message to global chat that shows the item as a hover action.
   *
   * @param user user
   */
  private void showItem(Player user) {
    ItemStack item = user.getInventory().getItemInMainHand();
    if (ItemReader.isNotNullOrAir(item)) {
      TextComponent message = createShowItemTextComponent(user, item);
      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        onlinePlayer.spigot().sendMessage(message);
      }
      PluginData.pastItemHistory.addPastItem(user, item);
    } else {
      user.sendMessage(PluginMessage.Failure.NO_MAIN_HAND_ITEM.message);
    }
  }

  /**
   * Checks if the parameter is "past" before opening a PastItem menu.
   *
   * @param user   user
   * @param action type of interaction
   */
  private void interpretParameter(Player user, String action) {
    if (action.equals("p") || action.equals("past")) {
      user.openInventory(new PastItemMenu(user).openMenu());
      user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.SHOWITEM_PAST.menu));
    } else {
      user.sendMessage(PluginMessage.Failure.UNRECOGNIZED_PARAMETER.message);
    }
  }

  /**
   * Creates a text component with a show item hover action.
   *
   * @param user user
   * @param item interacting item
   * @return text component with hover action (show item)
   */
  private TextComponent createShowItemTextComponent(Player user, ItemStack item) {
    // [!] <ItemName> [PlayerName]
    TextComponent message = new TextComponent(PluginMessage.Success.NOTIFICATION_GLOBAL.message + ChatColor.DARK_PURPLE + user.getName() + " ");

    TextComponent itemName = new TextComponent(ChatColor.AQUA + ItemReader.readName(item) + " ");
    ItemTag itemTag = ItemTag.ofNbt(item.getItemMeta().getAsString());
    itemName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new Item(item.getType().getKey().toString(), item.getAmount(), itemTag)));

    message.addExtra(itemName);
    return message;
  }
}