package me.dannynguyen.aethel.commands.showitem;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.listeners.MenuListener;
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
 * @version 1.23.12
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
        new Request(user, args).readRequest();
      } else {
        user.sendMessage(Message.INSUFFICIENT_PERMISSION.getMessage());
      }
    } else {
      sender.sendMessage(Message.PLAYER_ONLY_COMMAND.getMessage());
    }
    return true;
  }

  /**
   * Represents a ShowItem command request.
   *
   * @param user command user
   * @param args user provided parameters
   * @author Danny Nguyen
   * @version 1.23.12
   * @since 1.23.12
   */
  private record Request(Player user, String[] args) {
    /**
     * Checks if the command request was formatted correctly before interpreting its usage.
     */
    private void readRequest() {
      switch (args.length) {
        case 0 -> showItem();
        case 1 -> interpretParameter();
        default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      }
    }

    /**
     * Sends a message to global chat that shows the item as a hover action.
     */
    private void showItem() {
      ItemStack item = user.getInventory().getItemInMainHand();
      if (ItemReader.isNotNullOrAir(item)) {
        TextComponent message = createShowItemTextComponent(item);
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
     */
    private void interpretParameter() {
      String action = args[0].toLowerCase();
      if (action.equals("p") || action.equals("past")) {
        user.openInventory(new PastItemMenu(user).getMainMenu());
        Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId()).getMenuInput().setMenu(MenuListener.Menu.SHOWITEM_PAST);
      } else {
        user.sendMessage(Message.UNRECOGNIZED_PARAMETER.getMessage());
      }
    }

    /**
     * Creates a text component with a show item hover action.
     *
     * @param item interacting item
     * @return text component with hover action (show item)
     */
    private TextComponent createShowItemTextComponent(ItemStack item) {
      // [!] <ItemName> [PlayerName]
      TextComponent message = new TextComponent(Message.NOTIFICATION_GLOBAL.getMessage() + ChatColor.DARK_PURPLE + user.getName() + " ");

      TextComponent itemName = new TextComponent(ChatColor.AQUA + ItemReader.readName(item) + " ");
      ItemTag itemTag = ItemTag.ofNbt(item.getItemMeta().getAsString());
      itemName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new Item(item.getType().getKey().toString(), item.getAmount(), itemTag)));

      message.addExtra(itemName);
      return message;
    }
  }
}