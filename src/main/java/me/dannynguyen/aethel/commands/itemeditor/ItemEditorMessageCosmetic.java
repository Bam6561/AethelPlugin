package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.user.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

/**
 * ItemEditorMessageCosmetic is a utility class that edits an item's cosmetic-related metadata.
 *
 * @author Danny Nguyen
 * @version 1.7.0
 * @since 1.7.0
 */
public class ItemEditorMessageCosmetic {
  /**
   * Sets the item's display name.
   *
   * @param e    message event
   * @param user interacting user
   * @param item interacting item
   * @param meta item meta
   */
  public static void setDisplayName(AsyncPlayerChatEvent e, Player user,
                                    ItemStack item, ItemMeta meta) {
    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
    item.setItemMeta(meta);
    user.sendMessage(PluginMessage.Success.ITEMEDITOR_NAME_ITEM.message + ChatColor.WHITE + e.getMessage());
    openMainMenu(user, item);
  }

  /**
   * Sets the item's custom model data.
   *
   * @param e    message event
   * @param user interacting user
   * @param item interacting item
   * @param meta item meta
   * @throws NumberFormatException not an integer
   */
  public static void setCustomModelData(AsyncPlayerChatEvent e, Player user,
                                        ItemStack item, ItemMeta meta) {
    try {
      meta.setCustomModelData(Integer.parseInt(e.getMessage()));
      item.setItemMeta(meta);
      user.sendMessage(PluginMessage.Success.ITEMEDITOR_SET_CUSTOMMODELDATA.message + ChatColor.WHITE + e.getMessage());
    } catch (NumberFormatException ex) {
      user.sendMessage(PluginMessage.Failure.ITEMEDITOR_INVALID_CUSTOMMODELDATA.message);
    }
    openMainMenu(user, item);
  }

  /**
   * Sets the lore.
   *
   * @param e    message event
   * @param user interacting user
   * @param item interacting item
   * @param meta item meta
   */
  public static void setLore(AsyncPlayerChatEvent e, Player user,
                             ItemStack item, ItemMeta meta) {
    meta.setLore(List.of(e.getMessage().split(",, ")));
    item.setItemMeta(meta);
    user.sendMessage(PluginMessage.Success.ITEMEDITOR_SET_LORE.message);
    openMainMenu(user, item);
  }

  /**
   * Clears the lore.
   *
   * @param user interacting user
   * @param item interacting item
   * @param meta item meta
   */
  public static void clearLore(Player user, ItemStack item, ItemMeta meta) {
    meta.setLore(new ArrayList<>());
    item.setItemMeta(meta);
    user.sendMessage(PluginMessage.Success.ITEMEDITOR_CLEAR_LORE.message);
    openMainMenu(user, item);
  }

  /**
   * Adds a line of lore.
   *
   * @param e    message event
   * @param user interacting user
   * @param item interacting item
   * @param meta item meta
   */
  public static void addLore(AsyncPlayerChatEvent e, Player user,
                             ItemStack item, ItemMeta meta) {
    if (meta.hasLore()) {
      List<String> lore = meta.getLore();
      lore.add(e.getMessage());
      meta.setLore(lore);
    } else {
      meta.setLore(List.of(e.getMessage()));
    }
    item.setItemMeta(meta);
    user.sendMessage(PluginMessage.Success.ITEMEDITOR_ADD_LORE.message);
    openMainMenu(user, item);
  }

  /**
   * Edits a line of lore.
   *
   * @param e    message event
   * @param user interacting user
   * @param item interacting item
   * @param meta item meta
   * @throws NumberFormatException     not a number
   * @throws IndexOutOfBoundsException invalid index
   */
  public static void editLore(AsyncPlayerChatEvent e, Player user,
                              ItemStack item, ItemMeta meta) {
    String[] input = e.getMessage().split(" ", 2);
    try {
      List<String> lore = meta.getLore();
      lore.set(Integer.parseInt(input[0]) - 1, input[1]);
      meta.setLore(lore);
      item.setItemMeta(meta);
      user.sendMessage(PluginMessage.Success.ITEMEDITOR_EDIT_LORE.message);
    } catch (NumberFormatException ex) {
      user.sendMessage(PluginMessage.Failure.ITEMEDITOR_INVALID_LINE.message);
    } catch (IndexOutOfBoundsException ex) {
      user.sendMessage(PluginMessage.Failure.ITEMEDITOR_NONEXISTENT_LINE.message);
    }
    openMainMenu(user, item);
  }

  /**
   * Removes a line of lore.
   *
   * @param e    message event
   * @param user interacting user
   * @param item interacting item
   * @param meta item meta
   * @throws NumberFormatException     not a number
   * @throws IndexOutOfBoundsException invalid index
   */
  public static void removeLore(AsyncPlayerChatEvent e, Player user,
                                ItemStack item, ItemMeta meta) {
    try {
      List<String> lore = meta.getLore();
      lore.remove(Integer.parseInt(e.getMessage()) - 1);
      meta.setLore(lore);
      item.setItemMeta(meta);
      user.sendMessage(PluginMessage.Success.ITEMEDITOR_REMOVE_LORE.message);
    } catch (NumberFormatException ex) {
      user.sendMessage(PluginMessage.Failure.ITEMEDITOR_INVALID_LINE.message);
    } catch (IndexOutOfBoundsException ex) {
      user.sendMessage(PluginMessage.Failure.ITEMEDITOR_NONEXISTENT_LINE.message);
    }
    openMainMenu(user, item);
  }


  /**
   * Returns to the editor menu.
   *
   * @param user interacting user
   * @param item interacting item
   */
  private static void openMainMenu(Player user, ItemStack item) {
    user.removeMetadata("message", Plugin.getInstance());
    Bukkit.getScheduler().runTask(Plugin.getInstance(),
        () -> {
          user.openInventory(ItemEditorI.openCosmeticMenu(user, item));
          user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
              new FixedMetadataValue(Plugin.getInstance(), PluginPlayerMeta.Inventory.ITEMEDITOR_COSMETICS.inventory));
        });
  }
}
