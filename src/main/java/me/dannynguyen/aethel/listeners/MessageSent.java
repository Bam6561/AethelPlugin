package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.commands.itemeditor.ItemEditorMessageSent;
import me.dannynguyen.aethel.systems.PlayerMeta;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;

/**
 * Collection of player chat listeners for the plugin's message inputs.
 * <p>
 * By default, all message inputs are cancelled since they are used for only user inputs.
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.11.3
 * @since 1.6.7
 */
public class MessageSent implements Listener {
  /**
   * Routes interactions for messages sent.
   *
   * @param e message event
   */
  @EventHandler
  private void onPlayerChat(AsyncPlayerChatEvent e) {
    Map<PlayerMeta, String> playerMeta = PluginData.pluginSystem.getPlayerMetadata().get(e.getPlayer());
    if (playerMeta.containsKey(PlayerMeta.MESSAGE)) {
      e.setCancelled(true);
      String[] msgType = playerMeta.get(PlayerMeta.MESSAGE).split("\\.");
      switch (msgType[0]) {
        case "itemeditor" -> interpretItemEditor(e, msgType);
      }
    }
  }

  /**
   * Determines which ItemEditor input is being interacted with.
   *
   * @param e       inventory click event
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
      case "attribute" -> msg.setAttribute();
      case "enchantment" -> msg.setEnchant();
      case "tag" -> msg.setTag();
    }
  }
}
