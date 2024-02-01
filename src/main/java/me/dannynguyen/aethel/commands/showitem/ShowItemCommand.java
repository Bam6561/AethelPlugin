package me.dannynguyen.aethel.commands.showitem;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.commands.showitem.object.ItemOwner;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.listeners.InventoryListener;
import me.dannynguyen.aethel.utility.ItemReader;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Item;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * ShowItemCommand is a command invocation that shows the user's main hand item to global chat.
 * <p>
 * Additional Parameters:
 * - "past", "p": opens an inventory with the last 9 shown items
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.8.4
 * @since 1.4.5
 */
public class ShowItemCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player user)) {
      sender.sendMessage(PluginMessage.Failure.PLAYER_ONLY_COMMAND.message);
      return true;
    }

    if (user.hasPermission(Permission.SHOWITEM.permission)) {
      readRequest(user, args);
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
    if (item.getType() != Material.AIR) {
      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        onlinePlayer.spigot().sendMessage(createShowItemTextComponent(user, item));
      }
      PluginData.showItemData.addPastItem(new ItemOwner(user.getName(), item.clone()));
    } else {
      user.sendMessage(PluginMessage.Failure.NO_MAIN_HAND_ITEM.message);
    }
  }

  /**
   * Checks if the parameter is "past" before opening a ShowItemPast inventory.
   *
   * @param user   user
   * @param action type of interaction
   */
  private void interpretParameter(Player user, String action) {
    if (action.equals("past") || action.equals("p")) {
      user.openInventory(ShowItemPast.openInventory(user));
      user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
          new FixedMetadataValue(Plugin.getInstance(), InventoryListener.Inventory.SHOWITEM_PAST.inventory));
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
    TextComponent message = new TextComponent(PluginMessage.Success.NOTIFICATION_GLOBAL.message +
        ChatColor.DARK_PURPLE + user.getName() + " ");
    TextComponent itemName = new TextComponent(ChatColor.AQUA + ItemReader.readName(item) + " ");
    message.addExtra(itemName);

    ItemTag itemTag = ItemTag.ofNbt(item.getItemMeta().getAsString());
    itemName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
        new Item(item.getType().getKey().toString(), item.getAmount(), itemTag)));

    return message;
  }

  private enum Permission {
    SHOWITEM("aethel.showitem");

    public final String permission;

    Permission(String permission) {
      this.permission = permission;
    }
  }
}