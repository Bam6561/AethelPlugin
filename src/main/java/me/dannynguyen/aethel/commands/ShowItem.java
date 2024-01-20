package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
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
 * ShowItem is a command invocation that shows the user's main hand item to chat.
 * <p>
 * Additional Parameters:
 * - "past", "p": opens an inventory with the last 9 shown items
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.6.6
 * @since 1.4.5
 */
public class ShowItem implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player player)) {
      sender.sendMessage("Player-only command.");
      return true;
    }
    readRequest(player, args);
    return true;
  }

  /**
   * Checks if the Show command request was formatted correctly before interpreting its usage.
   *
   * @param player interacting player
   * @param args   player provided parameters
   */
  private void readRequest(Player player, String[] args) {
    switch (args.length) {
      case 0 -> showItemToChat(player);
      case 1 -> interpretParameter(player, args[0].toLowerCase());
      default -> player.sendMessage(ChatColor.RED + "Unrecognized parameters.");
    }
  }

  /**
   * Sends a message to chat that shows the item as a hover action.
   *
   * @param player interacting player
   */
  private void showItemToChat(Player player) {
    ItemStack item = player.getInventory().getItemInMainHand();
    if (item.getType() != Material.AIR) {
      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        onlinePlayer.spigot().sendMessage(createShowItemTextComponent(player, item));
      }
      AethelResources.showItemData.addPastItem(new ItemOwner(player.getName(), item));
    } else {
      player.sendMessage(ChatColor.RED + "No main hand item.");
    }
  }

  /**
   * Checks if the parameter is "past" before opening a ShowItemPast inventory.
   *
   * @param player interacting player
   * @param action type of interaction
   */
  private void interpretParameter(Player player, String action) {
    if (action.equals("past") || action.equals("p")) {
      player.openInventory(ShowItemPast.createInventory(player));
      player.setMetadata("inventory",
          new FixedMetadataValue(AethelPlugin.getInstance(), "showitem.past"));
    } else {
      player.sendMessage(ChatColor.RED + "Unrecognized parameter.");
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
    TextComponent chatMessage = new TextComponent(ChatColor.GREEN + "[!] "
        + ChatColor.DARK_PURPLE + player.getName() + " ");
    TextComponent itemName = new TextComponent(ChatColor.AQUA + ItemReader.readItemName(item) + " ");
    chatMessage.addExtra(itemName);

    ItemTag itemTag = ItemTag.ofNbt(item.getItemMeta().getAsString());
    itemName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
        new Item(item.getType().getKey().toString(), item.getAmount(), itemTag)));

    return chatMessage;
  }
}