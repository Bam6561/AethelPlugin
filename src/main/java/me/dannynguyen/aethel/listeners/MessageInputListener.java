package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.PluginEnum;
import me.dannynguyen.aethel.commands.itemeditor.ItemEditorMessageSent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * MessageInputListener is a player chat listener for the plugin's message inputs.
 *
 * @author Danny Nguyen
 * @version 1.9.21
 * @since 1.6.7
 */
public class MessageInputListener implements Listener {
  /**
   * Routes interactions for messages sent.
   *
   * @param e message event
   */
  @EventHandler
  public void onPlayerChat(AsyncPlayerChatEvent e) {
    Player user = e.getPlayer();
    if (user.hasMetadata(PluginEnum.PlayerMeta.MESSAGE.getMeta())) {
      String[] msgType = user.getMetadata(PluginEnum.PlayerMeta.MESSAGE.getMeta()).get(0).asString().split("\\.");
      switch (msgType[0]) {
        case "itemeditor" -> interpretItemEditor(e, msgType);
      }
      e.setCancelled(true);
    }
  }

  /**
   * Determines which ItemEditor message is being interacted with.
   *
   * @param e       inventory click event   user
   * @param msgType message type
   */
  private void interpretItemEditor(AsyncPlayerChatEvent e, String[] msgType) {
    ItemEditorMessageSent msg = new ItemEditorMessageSent(e);
    switch (msgType[1]) {
      case "display_name" -> msg.setDisplayName();
      case "custom_model_data" -> msg.setCustomModelData();
      case "lore-set" -> msg.setLore();
      case "lore-add" -> msg.addLore();
      case "lore-edit" -> msg.editLore();
      case "lore-remove" -> msg.removeLore();
      case "attributes" -> msg.setAttribute();
      case "enchantments" -> msg.setEnchant();
      case "tags" -> msg.setTag();
    }
  }
}
