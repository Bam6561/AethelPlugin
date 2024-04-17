package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.listeners.MenuListener;
import me.dannynguyen.aethel.utils.item.ItemReader;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Command invocation that allows the user to edit their main hand item's metadata.
 * <p>
 * Registered through {@link Plugin}.
 *
 * @author Danny Nguyen
 * @version 1.23.11
 * @since 1.6.7
 */
public class ItemEditorCommand implements CommandExecutor {
  /**
   * No parameter constructor.
   */
  public ItemEditorCommand() {
  }

  /**
   * Executes the ItemEditor command.
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
      if (user.hasPermission("aethel.itemeditor")) {
        ItemStack item = user.getInventory().getItemInMainHand();
        if (ItemReader.isNotNullOrAir(item)) {
          new Request(user, args, item).readRequest();
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
   * Represents a Character command request.
   *
   * @param user command user
   * @param args user provided parameters
   * @param item interacting item
   * @author Danny Nguyen
   * @version 1.23.11
   * @since 1.23.11
   */
  private record Request(Player user, String[] args, ItemStack item) {
    /**
     * Checks if the command request was formatted correctly before opening a {@link CosmeticMenu}.
     */
    private void readRequest() {
      if (args.length == 0) {
        openMenu();
      } else {
        user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      }
    }

    /**
     * Opens the {@link CosmeticMenu}.
     */
    private void openMenu() {
      UUID uuid = user.getUniqueId();
      Plugin.getData().getEditedItemCache().getEditedItems().put(uuid, item);
      user.openInventory(new CosmeticMenu(user).getMainMenu());
      Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput().setMenu(MenuListener.Menu.ITEMEDITOR_COSMETIC);
    }
  }
}
