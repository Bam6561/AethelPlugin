package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.systems.plugin.PluginData;
import me.dannynguyen.aethel.systems.plugin.enums.MenuMeta;
import me.dannynguyen.aethel.systems.plugin.enums.PlayerMeta;
import me.dannynguyen.aethel.systems.plugin.enums.PluginMessage;
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
 * @version 1.12.0
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
          user.sendMessage(PluginMessage.NO_MAIN_HAND_ITEM.getMessage());
        }
      } else {
        user.sendMessage(PluginMessage.INSUFFICIENT_PERMISSION.getMessage());
      }
    } else {
      sender.sendMessage(PluginMessage.PLAYER_ONLY_COMMAND.getMessage());
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
      openMenu(user, item);
    } else {
      user.sendMessage(PluginMessage.UNRECOGNIZED_PARAMETERS.getMessage());
    }
  }

  /**
   * Opens the CosmeticEditor menu.
   *
   * @param user user
   * @param item interacting item
   */
  private void openMenu(Player user, ItemStack item) {
    PluginData.editedItemCache.getEditedItemMap().put(user.getUniqueId(), item);
    user.openInventory(new CosmeticEditorMenu(user).openMenu());
    PluginData.pluginSystem.getPlayerMetadata().get(user.getUniqueId()).put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_COSMETIC.getMeta());
  }
}
