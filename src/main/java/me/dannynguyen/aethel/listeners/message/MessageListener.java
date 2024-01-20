package me.dannynguyen.aethel.listeners.message;

import me.dannynguyen.aethel.AethelResources;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * MessageListener is a general usage player message listener.
 *
 * @author Danny Nguyen
 * @version 1.6.8
 * @since 1.6.7
 */
public class MessageListener implements Listener {
  /**
   * Routes interactions for messages sent.
   *
   * @param e message event
   */
  @EventHandler
  public void onMessage(AsyncPlayerChatEvent e) {
    Player player = e.getPlayer();
    if (player.hasMetadata("message")) {
      String[] msgType = player.getMetadata("message").get(0).asString().split("\\.");
      switch (msgType[0]) {
        case "itemeditor" -> interpretItemEditor(e, player, msgType);
      }
      e.setCancelled(true);
    }
  }

  /**
   * Determines which ItemEditor message is being interacted with.
   *
   * @param e       inventory click event
   * @param player  interacting player
   * @param msgType message type
   */
  private void interpretItemEditor(AsyncPlayerChatEvent e, Player player, String[] msgType) {
    ItemStack item = AethelResources.itemEditorData.getEditedItemMap().get(player);
    ItemMeta meta = item.getItemMeta();

    switch (msgType[1]) {
      case "display_name" -> ItemEditorMessageListener.setDisplayName(e, player, item, meta);
      case "custom_model_data" -> ItemEditorMessageListener.setCustomModelData(e, player, item, meta);
      case "lore" -> ItemEditorMessageListener.readLoreRequest(e, player, item, meta);
    }
  }
}
