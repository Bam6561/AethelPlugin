package me.dannynguyen.aethel.commands.showitem;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.listeners.MenuEvent;
import me.dannynguyen.aethel.utils.item.ItemReader;
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
import org.jetbrains.annotations.NotNull;

/**
 * Command invocation that shows the user's main hand item to global chat.
 * <p>
 * Registered through {@link Plugin}.
 * <p>
 * Parameters:
 * <ul>
 *  <li>"": shows the user's main hand item to global chat
 *  <li>"past", "p": opens a {@link PastItemMenu} with the last 27 shown items
 * </ul>
 *
 * @author Danny Nguyen
 * @version 1.17.19
 * @since 1.4.5
 */
public class ShowItemCommand implements CommandExecutor {
  /**
   * No parameter constructor.
   */
  public ShowItemCommand() {
  }

  /**
   * Executes the ShowItem command.
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
      if (user.hasPermission("aethel.showitem")) {
        readRequest(user, args);
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
   */
  private void readRequest(Player user, String[] args) {
    switch (args.length) {
      case 0 -> showItem(user);
      case 1 -> interpretParameter(user, args[0].toLowerCase());
      default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
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
      Plugin.getData().getPastItemHistory().addPastItem(user, item);
    } else {
      user.sendMessage(Message.NO_MAIN_HAND_ITEM.getMessage());
    }
  }

  /**
   * Checks if the parameter is "past" before opening a {@link PastItemMenu}.
   *
   * @param user   user
   * @param action type of interaction
   */
  private void interpretParameter(Player user, String action) {
    if (action.equals("p") || action.equals("past")) {
      user.openInventory(new PastItemMenu(user).getMainMenu());
      Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId()).setMenu(MenuEvent.Menu.SHOWITEM_PAST);
    } else {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETER.getMessage());
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
    TextComponent message = new TextComponent(Message.NOTIFICATION_GLOBAL.getMessage() + ChatColor.DARK_PURPLE + user.getName() + " ");

    TextComponent itemName = new TextComponent(ChatColor.AQUA + ItemReader.readName(item) + " ");
    ItemTag itemTag = ItemTag.ofNbt(item.getItemMeta().getAsString());
    itemName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new Item(item.getType().getKey().toString(), item.getAmount(), itemTag)));

    message.addExtra(itemName);
    return message;
  }
}