package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.listeners.InventoryMenuListener;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

/**
 * Command invocation that allows the user to
 * edit their main hand item's metadata.
 *
 * @author Danny Nguyen
 * @version 1.9.17
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
          user.sendMessage(PluginMessage.Failure.NO_MAIN_HAND_ITEM.message);
        }
      } else {
        user.sendMessage(PluginMessage.Failure.INSUFFICIENT_PERMISSION.message);
      }
    } else {
      sender.sendMessage(PluginMessage.Failure.PLAYER_ONLY_COMMAND.message);
    }
    return true;
  }

  /**
   * Checks if the command request was formatted correctly before opening an ItemEditorCosmetic menu.
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
   * Opens an ItemEditorCosmetic menu.
   *
   * @param user user
   * @param item interacting item
   */
  private void openMainMenu(Player user, ItemStack item) {
    PluginData.editedItemCache.getEditedItemMap().put(user, item);
    user.openInventory(new ItemEditorCosmetic(user, item).openMenu());
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.ITEMEDITOR_COSMETICS.menu));
  }
}
