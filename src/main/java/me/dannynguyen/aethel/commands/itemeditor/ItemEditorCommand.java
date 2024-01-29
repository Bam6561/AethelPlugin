package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.commands.itemeditor.inventory.ItemEditorInventory;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.enums.PluginPermission;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * ItemEditor is a command invocation that allows the
 * user to edit their main hand item's metadata.
 *
 * @author Danny Nguyen
 * @version 1.8.2
 * @since 1.6.7
 */
public class ItemEditorCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player user)) {
      sender.sendMessage(PluginMessage.Failure.PLAYER_ONLY_COMMAND.message);
      return true;
    }

    if (user.hasPermission(PluginPermission.ITEMEDITOR.permission)) {
      ItemStack item = user.getInventory().getItemInMainHand();
      if (item.getType() != Material.AIR) {
        readRequest(user, args, item);
      } else {
        user.sendMessage(PluginMessage.Failure.NO_MAIN_HAND_ITEM.message);
      }
    } else {
      user.sendMessage(PluginMessage.Failure.INSUFFICIENT_PERMISSION.message);
    }
    return true;
  }

  /**
   * Checks if the command request was formatted correctly before opening an Item Editor cosmetic menu.
   *
   * @param user user
   * @param args user provided parameters
   * @param item interacting item
   */
  private void readRequest(Player user, String[] args, ItemStack item) {
    if (args.length == 0) {
      openMainMenu(user, item);
    } else {
      user.sendMessage(PluginMessage.Failure.UNRECOGNIZED_PARAMETERS.message);
    }
  }

  /**
   * Opens an ItemEditor main menu.
   *
   * @param user user
   * @param item interacting item
   */
  private void openMainMenu(Player user, ItemStack item) {
    PluginData.itemEditorData.getEditedItemMap().put(user, item);

    user.openInventory(ItemEditorInventory.openMainMenu(user, item));
    user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(), PluginPlayerMeta.Inventory.ITEMEDITOR_COSMETICS.inventory));
  }
}
