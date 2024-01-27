package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.enums.PluginMetadata;
import me.dannynguyen.aethel.enums.PluginPermission;
import me.dannynguyen.aethel.inventories.ShowItemPast;
import me.dannynguyen.aethel.objects.ItemOwner;
import me.dannynguyen.aethel.readers.ItemReader;
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
 * ShowItem is a command invocation that shows the user's main hand item to global chat.
 * <p>
 * Additional Parameters:
 * - "past", "p": opens an inventory with the last 9 shown items
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.7.6
 * @since 1.4.5
 */
public class ShowItem implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player user)) {
      sender.sendMessage(PluginMessage.PLAYER_ONLY_COMMAND.message);
      return true;
    }

    if (user.hasPermission(PluginPermission.SHOWITEM.permission)) {
      readRequest(user, args);
    } else {
      user.sendMessage(PluginMessage.INSUFFICIENT_PERMISSION.message);
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
      default -> user.sendMessage(PluginMessage.UNRECOGNIZED_PARAMETERS.message);
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
      AethelResources.showItemData.addPastItem(new ItemOwner(user.getName(), item.clone()));
    } else {
      user.sendMessage(PluginMessage.NO_MAIN_HAND_ITEM.message);
    }
  }

  /**
   * Checks if the parameter is "past" before opening a ShowItemPast inventory.
   *
   * @param user   interacting player
   * @param action type of interaction
   */
  private void interpretParameter(Player user, String action) {
    if (action.equals("past") || action.equals("p")) {
      user.openInventory(ShowItemPast.createInventory(user));
      user.setMetadata(PluginMetadata.INVENTORY.data,
          new FixedMetadataValue(AethelPlugin.getInstance(), PluginMetadata.SHOWITEM_PAST.data));
    } else {
      user.sendMessage(PluginMessage.UNRECOGNIZED_PARAMETER.message);
    }
  }

  /**
   * Creates a text component with a show item hover action.
   *
   * @param player interacting player
   * @param item   interacting item
   * @return text component with hover action (show item)
   */
  private TextComponent createShowItemTextComponent(Player player, ItemStack item) {
    // [!] <ItemName> [PlayerName]
    TextComponent message = new TextComponent(ChatColor.GREEN + "[!] "
        + ChatColor.DARK_PURPLE + player.getName() + " ");
    TextComponent itemName = new TextComponent(ChatColor.AQUA + ItemReader.readItemName(item) + " ");
    message.addExtra(itemName);

    ItemTag itemTag = ItemTag.ofNbt(item.getItemMeta().getAsString());
    itemName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
        new Item(item.getType().getKey().toString(), item.getAmount(), itemTag)));

    return message;
  }
}