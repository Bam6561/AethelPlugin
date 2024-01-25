package me.dannynguyen.aethel.listeners.message;

import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.listeners.message.itemeditor.ItemEditorMessageCosmetic;
import me.dannynguyen.aethel.listeners.message.itemeditor.ItemEditorMessageFunctional;
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
 * @version 1.7.0
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
      case "display_name" -> ItemEditorMessageCosmetic.setDisplayName(e, player, item, meta);
      case "custom_model_data" -> ItemEditorMessageCosmetic.setCustomModelData(e, player, item, meta);
      case "lore-set" -> ItemEditorMessageCosmetic.setLore(e, player, item, meta);
      case "lore-add" -> ItemEditorMessageCosmetic.addLore(e, player, item, meta);
      case "lore-edit" -> ItemEditorMessageCosmetic.editLore(e, player, item, meta);
      case "lore-remove" -> ItemEditorMessageCosmetic.removeLore(e, player, item, meta);
      case "attributes" -> ItemEditorMessageFunctional.setAttribute(e, player, item);
      case "enchants" -> ItemEditorMessageFunctional.setEnchant(e, player, item);
      case "tags" -> ItemEditorMessageFunctional.setTag(e, player, item, meta);
    }
  }
}
