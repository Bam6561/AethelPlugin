package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.enums.PluginPermission;
import me.dannynguyen.aethel.inventories.itemeditor.ItemEditorI;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * ItemEditor is a command invocation that allows
 * user to edit their main hand item's metadata.
 *
 * @author Danny Nguyen
 * @version 1.7.7
 * @since 1.6.7
 */
public class ItemEditor implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player user)) {
      sender.sendMessage(PluginMessage.PLAYER_ONLY_COMMAND.message);
      return true;
    }

    if (user.hasPermission(PluginPermission.ITEMEDITOR.permission)) {
      ItemStack item = user.getInventory().getItemInMainHand();
      if (item.getType() != Material.AIR) {
        readRequest(user, args, item);
      } else {
        user.sendMessage(PluginMessage.NO_MAIN_HAND_ITEM.message);
      }
    } else {
      user.sendMessage(PluginMessage.INSUFFICIENT_PERMISSION.message);
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
    switch (args.length) {
      case 0 -> openCosmeticMenu(user, item);
      default -> user.sendMessage(PluginMessage.UNRECOGNIZED_PARAMETERS.message);
    }
  }

  /**
   * Opens an ItemEditor cosmetic menu.
   *
   * @param user user
   * @param item interacting item
   */
  private void openCosmeticMenu(Player user, ItemStack item) {
    AethelResources.itemEditorData.getEditedItemMap().put(user, item);

    user.openInventory(ItemEditorI.openCosmeticMenu(user, item));
    user.setMetadata(PluginPlayerMeta.Container.INVENTORY.name,
        new FixedMetadataValue(AethelPlugin.getInstance(), PluginPlayerMeta.Value.ITEMEDITOR_MENU.value));
  }
}
