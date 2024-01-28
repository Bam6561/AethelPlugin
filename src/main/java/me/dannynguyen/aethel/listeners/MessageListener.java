package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.commands.itemeditor.ItemEditorMessageCosmetic;
import me.dannynguyen.aethel.commands.itemeditor.ItemEditorMessageFunctional;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
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
 * @version 1.7.11
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
      case "display_name" -> ItemEditorMessageCosmetic.setDisplayName(e, user, item, meta);
      case "custom_model_data" -> ItemEditorMessageCosmetic.setCustomModelData(e, user, item, meta);
      case "lore-set" -> ItemEditorMessageCosmetic.setLore(e, user, item, meta);
      case "lore-add" -> ItemEditorMessageCosmetic.addLore(e, user, item, meta);
      case "lore-edit" -> ItemEditorMessageCosmetic.editLore(e, user, item, meta);
      case "lore-remove" -> ItemEditorMessageCosmetic.removeLore(e, user, item, meta);
      case "attributes" -> ItemEditorMessageFunctional.setAttribute(e, user, item);
      case "enchants" -> ItemEditorMessageFunctional.setEnchant(e, user, item);
      case "tags" -> ItemEditorMessageFunctional.setTag(e, user, item, meta);
    }
  }
}
