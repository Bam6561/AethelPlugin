package me.dannynguyen.aethel.plugin.system;

import me.dannynguyen.aethel.plugin.listeners.MenuClick;
import me.dannynguyen.aethel.plugin.listeners.MessageSent;
import me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot;
import me.dannynguyen.aethel.rpg.enums.Trigger;
import me.dannynguyen.aethel.rpg.listeners.RPGAction;

import java.util.UUID;

/**
 * Represents a player's plugin metadata.
 *
 * @author Danny Nguyen
 * @version 1.17.16
 * @since 1.17.16
 */
public class PluginPlayer {
  /**
   * No parameter constructor.
   */
  public PluginPlayer() {

  }

  /**
   * {@link me.dannynguyen.aethel.commands.DeveloperCommand}
   */
  private boolean developer;

  /**
   * {@link RPGAction.Input}
   */
  private RPGAction.Input actionInput;

  /**
   * {@link MessageSent.Input}
   */
  private MessageSent.Input messageInput;

  /**
   * {@link MenuClick.Menu}
   */
  private MenuClick.Menu menu;

  /**
   * {@link MenuClick.Mode}
   */
  private MenuClick.Mode mode;

  /**
   * Menu category.
   */
  private String category;

  /**
   * Menu page number.
   */
  private int page;

  /**
   * Target player's UUID.
   */
  private UUID target;

  /**
   * {@link RpgEquipmentSlot}
   */
  private RpgEquipmentSlot slot;

  /**
   * {@link Trigger}
   */
  private Trigger trigger;

  /**
   * Interacting object type.
   */
  private String objectType;

  /**
   * Gets {@link me.dannynguyen.aethel.commands.DeveloperCommand}.
   *
   * @return {@link me.dannynguyen.aethel.commands.DeveloperCommand}
   */
  public boolean isDeveloper() {
    return developer;
  }

  /**
   * Gets {@link RPGAction.Input}.
   *
   * @return {@link RPGAction.Input}
   */
  public RPGAction.Input getActionInput() {
    return actionInput;
  }

  /**
   * Gets {@link MessageSent.Input}.
   *
   * @return {@link MessageSent.Input}
   */
  public MessageSent.Input getMessageInput() {
    return messageInput;
  }

  /**
   * Gets {@link MenuClick.Menu menu type}.
   *
   * @return {@link MenuClick.Menu menu type}
   */
  public MenuClick.Menu getMenu() {
    return menu;
  }

  /**
   * Gets {@link MenuClick.Mode menu mode}.
   *
   * @return {@link MenuClick.Mode menu mode}
   */
  public MenuClick.Mode getMode() {
    return mode;
  }

  /**
   * Gets menu category.
   *
   * @return menu category
   */
  public String getCategory() {
    return category;
  }

  /**
   * Gets menu page number.
   *
   * @return menu page
   */
  public int getPage() {
    return page;
  }

  /**
   * Gets target player's UUID.
   *
   * @return target player's UUID
   */
  public UUID getTarget() {
    return target;
  }

  /**
   * Gets {@link RpgEquipmentSlot}.
   *
   * @return {@link RpgEquipmentSlot}
   */
  public RpgEquipmentSlot getSlot() {
    return slot;
  }

  /**
   * Gets {@link Trigger}.
   *
   * @return {@link Trigger}
   */
  public Trigger getTrigger() {
    return trigger;
  }

  /**
   * Gets object type.
   *
   * @return object type
   */
  public String getObjectType() {
    return objectType;
  }
}
