package me.dannynguyen.aethel.plugin.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.commands.itemeditor.ItemEditorMessageSent;
import me.dannynguyen.aethel.plugin.system.PluginPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Collection of player chat listeners for the plugin's message inputs.
 * <p>
 * By default, all message inputs are cancelled since they are used for only user inputs.
 *
 * @author Danny Nguyen
 * @version 1.17.18
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
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(e.getPlayer().getUniqueId());
    if (pluginPlayer.getMessageInput() != null) {
      e.setCancelled(true);
      ItemEditorMessageSent msg = new ItemEditorMessageSent(e);
      switch (pluginPlayer.getMessageInput()) {
        case DISPLAY_NAME -> msg.setDisplayName();
        case CUSTOM_MODEL_DATA -> msg.setCustomModelData();
        case DURABILITY -> msg.setDurability();
        case REPAIR_COST -> msg.setRepairCost();
        case LORE_SET -> msg.setLore();
        case LORE_ADD -> msg.addLore();
        case LORE_EDIT -> msg.editLore();
        case LORE_REMOVE -> msg.removeLore();
        case POTION_COLOR -> msg.setPotionColor();
        case MINECRAFT_ATTRIBUTE -> msg.setMinecraftAttribute();
        case AETHEL_ATTRIBUTE -> msg.setAethelAttribute();
        case ENCHANTMENT -> msg.setEnchantment();
        case POTION_EFFECT -> msg.setPotionEffect();
        case PASSIVE_ABILITY -> msg.setPassive();
        case ACTIVE_ABILITY -> msg.setActive();
        case AETHEL_TAG -> msg.setTag();
      }
    }
  }

  /**
   * Removes plugin {@link PluginPlayer#getMessageInput()} when an inventory is opened.
   *
   * @param e inventory open event
   */
  @EventHandler
  private void onOpenInventory(InventoryOpenEvent e) {
    Plugin.getData().getPluginSystem().getPluginPlayers().get(e.getPlayer().getUniqueId()).setMessageInput(null);
  }

  /**
   * Message input types.
   */
  public enum Input {
    /**
     * Display name.
     */
    DISPLAY_NAME,

    /**
     * Custom model data.
     */
    CUSTOM_MODEL_DATA,

    /**
     * Durability.
     */
    DURABILITY,

    /**
     * Repair cost.
     */
    REPAIR_COST,

    /**
     * Set lore.
     */
    LORE_SET,

    /**
     * Add lore.
     */
    LORE_ADD,

    /**
     * Edit lore.
     */
    LORE_EDIT,

    /**
     * Remove lore.
     */
    LORE_REMOVE,

    /**
     * Potion color.
     */
    POTION_COLOR,

    /**
     * Minecraft attribute.
     */
    MINECRAFT_ATTRIBUTE,

    /**
     * Aethel attribute.
     */
    AETHEL_ATTRIBUTE,

    /**
     * Enchantment.
     */
    ENCHANTMENT,

    /**
     * Potion effect.
     */
    POTION_EFFECT,

    /**
     * Passive ability.
     */
    PASSIVE_ABILITY,
    /**
     * Active ability.
     */
    ACTIVE_ABILITY,

    /**
     * Aethel tag.
     */
    AETHEL_TAG
  }
}
