package me.dannynguyen.aethel.plugin.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.commands.itemeditor.ItemEditorMessageSent;
import me.dannynguyen.aethel.plugin.system.PluginPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Collection of player chat listeners for the plugin's message inputs.
 * <p>
 * By default, all message inputs are cancelled since they are used for only user inputs.
 *
 * @author Danny Nguyen
 * @version 1.18.0
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
    Input input = Plugin.getData().getPluginSystem().getPluginPlayers().get(e.getPlayer().getUniqueId()).getMessageInput();
    if (input != null) {
      e.setCancelled(true);
      String inputType = input.getId();
      switch (inputType) {
        case "itemeditor" -> interpretItemEditor(e, input);
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
   * Determines which {@link me.dannynguyen.aethel.commands.itemeditor.ItemEditorCommand}
   * input is being interacted with.
   *
   * @param e     message event
   * @param input {@link Input}
   */
  private void interpretItemEditor(AsyncPlayerChatEvent e, Input input) {
    ItemEditorMessageSent msg = new ItemEditorMessageSent(e);
    switch (input) {
      case ITEMEDITOR_DISPLAY_NAME -> msg.setDisplayName();
      case ITEMEDITOR_CUSTOM_MODEL_DATA -> msg.setCustomModelData();
      case ITEMEDITOR_DURABILITY -> msg.setDurability();
      case ITEMEDITOR_REPAIR_COST -> msg.setRepairCost();
      case ITEMEDITOR_LORE_SET -> msg.setLore();
      case ITEMEDITOR_LORE_ADD -> msg.addLore();
      case ITEMEDITOR_LORE_EDIT -> msg.editLore();
      case ITEMEDITOR_LORE_REMOVE -> msg.removeLore();
      case ITEMEDITOR_POTION_COLOR -> msg.setPotionColor();
      case ITEMEDITOR_MINECRAFT_ATTRIBUTE -> msg.setMinecraftAttribute();
      case ITEMEDITOR_AETHEL_ATTRIBUTE -> msg.setAethelAttribute();
      case ITEMEDITOR_ENCHANTMENT -> msg.setEnchantment();
      case ITEMEDITOR_POTION_EFFECT -> msg.setPotionEffect();
      case ITEMEDITOR_PASSIVE_ABILITY -> msg.setPassive();
      case ITEMEDITOR_ACTIVE_ABILITY -> msg.setActive();
      case ITEMEDITOR_AETHEL_TAG -> msg.setTag();
    }
  }

  /**
   * Message input types.
   */
  public enum Input {
    /**
     * Display name.
     */
    ITEMEDITOR_DISPLAY_NAME("itemeditor"),

    /**
     * Custom model data.
     */
    ITEMEDITOR_CUSTOM_MODEL_DATA("itemeditor"),

    /**
     * Durability.
     */
    ITEMEDITOR_DURABILITY("itemeditor"),

    /**
     * Repair cost.
     */
    ITEMEDITOR_REPAIR_COST("itemeditor"),

    /**
     * Set lore.
     */
    ITEMEDITOR_LORE_SET("itemeditor"),

    /**
     * Add lore.
     */
    ITEMEDITOR_LORE_ADD("itemeditor"),

    /**
     * Edit lore.
     */
    ITEMEDITOR_LORE_EDIT("itemeditor"),

    /**
     * Remove lore.
     */
    ITEMEDITOR_LORE_REMOVE("itemeditor"),

    /**
     * Potion color.
     */
    ITEMEDITOR_POTION_COLOR("itemeditor"),

    /**
     * Minecraft attribute.
     */
    ITEMEDITOR_MINECRAFT_ATTRIBUTE("itemeditor"),

    /**
     * Aethel attribute.
     */
    ITEMEDITOR_AETHEL_ATTRIBUTE("itemeditor"),

    /**
     * Enchantment.
     */
    ITEMEDITOR_ENCHANTMENT("itemeditor"),

    /**
     * Potion effect.
     */
    ITEMEDITOR_POTION_EFFECT("itemeditor"),

    /**
     * Passive ability.
     */
    ITEMEDITOR_PASSIVE_ABILITY("itemeditor"),

    /**
     * Active ability.
     */
    ITEMEDITOR_ACTIVE_ABILITY("itemeditor"),

    /**
     * Aethel tag.
     */
    ITEMEDITOR_AETHEL_TAG("itemeditor");

    /**
     * Input ID.
     */
    private final String id;

    /**
     * Associates an input with its ID.
     *
     * @param id ID
     */
    Input(String id) {
      this.id = id;
    }

    /**
     * Gets the input's ID.
     *
     * @return input's ID
     */
    @NotNull
    public String getId() {
      return this.id;
    }
  }
}
