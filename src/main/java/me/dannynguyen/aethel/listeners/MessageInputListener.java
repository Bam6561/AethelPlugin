package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.commands.itemeditor.ItemEditorMessageListener;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * MessageInputListener is a player chat listener for the plugin's message inputs.
 *
 * @author Danny Nguyen
 * @version 1.8.7
 * @since 1.6.7
 */
public class MessageInputListener implements Listener {
  /**
   * Routes interactions for messages sent.
   *
   * @param e message event
   */
  @EventHandler
  public void onMessage(AsyncPlayerChatEvent e) {
    Player user = e.getPlayer();
    if (user.hasMetadata(PluginPlayerMeta.Namespace.MESSAGE.namespace)) {
      String[] msgType =
          user.getMetadata(PluginPlayerMeta.Namespace.MESSAGE.namespace).get(0).asString().split("\\.");
      switch (msgType[0]) {
        case "itemeditor" -> interpretItemEditor(e, user, msgType);
      }
      e.setCancelled(true);
    }
  }

  /**
   * Determines which ItemEditor message is being interacted with.
   *
   * @param e       inventory click event
   * @param user    user
   * @param msgType message type
   */
  private void interpretItemEditor(AsyncPlayerChatEvent e, Player user, String[] msgType) {
    ItemStack item = PluginData.itemEditorData.getEditedItemMap().get(user);
    ItemMeta meta = item.getItemMeta();

    switch (msgType[1]) {
      case "display_name" -> ItemEditorMessageListener.setDisplayName(e, user, item, meta);
      case "custom_model_data" -> ItemEditorMessageListener.setCustomModelData(e, user, item, meta);
      case "lore-set" -> ItemEditorMessageListener.setLore(e, user, item, meta);
      case "lore-add" -> ItemEditorMessageListener.addLore(e, user, item, meta);
      case "lore-edit" -> ItemEditorMessageListener.editLore(e, user, item, meta);
      case "lore-remove" -> ItemEditorMessageListener.removeLore(e, user, item, meta);
      case "attributes" -> ItemEditorMessageListener.setAttribute(e, user, item);
      case "enchants" -> ItemEditorMessageListener.setEnchant(e, user, item);
      case "tags" -> ItemEditorMessageListener.setTag(e, user, item, meta);
    }
  }
}
