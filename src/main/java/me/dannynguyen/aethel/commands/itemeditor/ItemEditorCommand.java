package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.plugin.PluginData;
import me.dannynguyen.aethel.plugin.PluginEnum;
import me.dannynguyen.aethel.plugin.MenuMeta;
import me.dannynguyen.aethel.plugin.PlayerMeta;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Command invocation that allows the user to
 * edit their main hand item's metadata.
 *
 * @author Danny Nguyen
 * @version 1.10.1
 * @since 1.6.7
 */
public class ItemEditorCommand implements CommandExecutor {
  /**
   * Executes the ItemEditor command.
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
      if (user.hasPermission("aethel.itemeditor")) {
        ItemStack item = user.getInventory().getItemInMainHand();
        if (ItemReader.isNotNullOrAir(item)) {
          readRequest(user, args, item);
        } else {
          user.sendMessage(PluginEnum.Message.NO_MAIN_HAND_ITEM.getMessage());
        }
      } else {
        user.sendMessage(PluginEnum.Message.INSUFFICIENT_PERMISSION.getMessage());
      }
    } else {
      sender.sendMessage(PluginEnum.Message.PLAYER_ONLY_COMMAND.getMessage());
    }
    return true;
  }

  /**
   * Checks if the command request was formatted correctly before opening an CosmeticEditor menu.
   *
   * @param user user
   * @param args user provided parameters
   * @param item interacting item
   */
  private void readRequest(Player user, String[] args, ItemStack item) {
    if (args.length == 0) {
      openMainMenu(user, item);
    } else {
      user.sendMessage(PluginEnum.Message.UNRECOGNIZED_PARAMETERS.getMessage());
    }
  }

  /**
   * Opens the CosmeticEditor menu.
   *
   * @param user user
   * @param item interacting item
   */
  private void openMainMenu(Player user, ItemStack item) {
    PluginData.editedItemCache.getEditedItemMap().put(user, item);
    user.openInventory(new CosmeticEditorMenu(user).openMenu());
    PluginData.pluginSystem.getPlayerMetadata().get(user).put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_COSMETIC.getMeta());
  }
}
