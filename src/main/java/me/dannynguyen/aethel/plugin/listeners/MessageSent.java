package me.dannynguyen.aethel.plugin.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.commands.itemeditor.ItemEditorMessageSent;
import me.dannynguyen.aethel.plugin.enums.PlayerMeta;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;

/**
 * Collection of player chat listeners for the plugin's message inputs.
 * <p>
 * By default, all message inputs are cancelled since they are used for only user inputs.
 *
 * @author Danny Nguyen
 * @version 1.17.16
 * @since 1.6.7
 */
public class MessageSent implements Listener {
  /**
   * No parameter constructor.
   */
  public MessageSent() {
  }

  /**
   * Routes interactions for messages sent.
   *
   * @param e message event
   */
  @EventHandler
  private void onPlayerChat(AsyncPlayerChatEvent e) {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(e.getPlayer().getUniqueId());
    if (playerMeta.containsKey(PlayerMeta.MESSAGE)) {
      e.setCancelled(true);
      String[] msgType = playerMeta.get(PlayerMeta.MESSAGE).split("\\.");
      switch (msgType[0]) {
        case "itemeditor" -> interpretItemEditor(e, msgType);
      }
    }
  }

  /**
   * Removes plugin {@link PlayerMeta#MESSAGE} when an inventory is opened.
   *
   * @param e inventory open event
   */
  @EventHandler
  private void onOpenInventory(InventoryOpenEvent e) {
    Plugin.getData().getPluginSystem().getPlayerMetadata().get(e.getPlayer().getUniqueId()).remove(PlayerMeta.MESSAGE);
  }

  /**
   * Determines which {@link me.dannynguyen.aethel.commands.itemeditor.ItemEditorCommand}
   * input is being interacted with.
   *
   * @param e       message event
   * @param msgType message type
   */
  private void interpretItemEditor(AsyncPlayerChatEvent e, String[] msgType) {
    ItemEditorMessageSent msg = new ItemEditorMessageSent(e);
    switch (msgType[1]) {
      case "display_name" -> msg.setDisplayName();
      case "custom_model_data" -> msg.setCustomModelData();
      case "durability" -> msg.setDurability();
      case "repair_cost" -> msg.setRepairCost();
      case "lore-set" -> msg.setLore();
      case "lore-add" -> msg.addLore();
      case "lore-edit" -> msg.editLore();
      case "lore-remove" -> msg.removeLore();
      case "potion-color" -> msg.setPotionColor();
      case "minecraft_attribute" -> msg.setMinecraftAttribute();
      case "aethel_attribute" -> msg.setAethelAttribute();
      case "enchantment" -> msg.setEnchantment();
      case "potion-effect" -> msg.setPotionEffect();
      case "passive_ability" -> msg.setPassive();
      case "active_ability" -> msg.setActive();
      case "tag" -> msg.setTag();
    }
  }

  /**
   * Message input types.
   */
  public enum Input {

  }
}
