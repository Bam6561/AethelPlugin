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
 * @version 1.18.0
 * @since 1.17.16
 */
public class PluginPlayer {
  /**
   * {@link me.dannynguyen.aethel.commands.DeveloperCommand}
   */
  private boolean isDeveloper;
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
   * No parameter constructor.
   */
  public PluginPlayer() {
  }

  /**
   * Gets {@link me.dannynguyen.aethel.commands.DeveloperCommand}.
   *
   * @return {@link me.dannynguyen.aethel.commands.DeveloperCommand}
   */
  public boolean isDeveloper() {
    return this.isDeveloper;
  }

  /**
   * Sets the {@link me.dannynguyen.aethel.commands.DeveloperCommand}.
   *
   * @param developer {@link me.dannynguyen.aethel.commands.DeveloperCommand}
   */
  public void setIsDeveloper(boolean developer) {
    this.isDeveloper = developer;
  }

  /**
   * Gets {@link RPGAction.Input}.
   *
   * @return {@link RPGAction.Input}
   */
  public RPGAction.Input getActionInput() {
    return this.actionInput;
  }

  /**
   * Sets the {@link RPGAction.Input}.
   *
   * @param actionInput {@link RPGAction.Input}
   */
  public void setActionInput(RPGAction.Input actionInput) {
    this.actionInput = actionInput;
  }

  /**
   * Gets {@link MessageSent.Input}.
   *
   * @return {@link MessageSent.Input}
   */
  public MessageSent.Input getMessageInput() {
    return this.messageInput;
  }

  /**
   * Sets the {@link MessageSent.Input}.
   *
   * @param messageInput {@link MessageSent.Input}
   */
  public void setMessageInput(MessageSent.Input messageInput) {
    this.messageInput = messageInput;
  }

  /**
   * Gets {@link MenuClick.Menu menu type}.
   *
   * @return {@link MenuClick.Menu menu type}
   */
  public MenuClick.Menu getMenu() {
    return this.menu;
  }


  /**
   * Sets the {@link MenuClick.Menu menu type}.
   *
   * @param menu {@link MenuClick.Menu menu type}
   */
  public void setMenu(MenuClick.Menu menu) {
    this.menu = menu;
  }

  /**
   * Gets {@link MenuClick.Mode menu mode}.
   *
   * @return {@link MenuClick.Mode menu mode}
   */
  public MenuClick.Mode getMode() {
    return this.mode;
  }

  /**
   * Sets the {@link MenuClick.Mode menu mode}
   *
   * @param mode {@link MenuClick.Mode menu mode}
   */
  public void setMode(MenuClick.Mode mode) {
    this.mode = mode;
  }

  /**
   * Gets menu category.
   *
   * @return menu category
   */
  public String getCategory() {
    return this.category;
  }

  /**
   * Sets the menu category.
   *
   * @param category menu category
   */
  public void setCategory(String category) {
    this.category = category;
  }

  /**
   * Gets menu page number.
   *
   * @return menu page
   */
  public int getPage() {
    return this.page;
  }

  /**
   * Sets the menu page number.
   *
   * @param page menu page number
   */
  public void setPage(int page) {
    this.page = page;
  }

  /**
   * Gets target player's UUID.
   *
   * @return target player's UUID
   */
  public UUID getTarget() {
    return this.target;
  }

  /**
   * Sets the target's UUID.
   *
   * @param target target's UUID
   */
  public void setTarget(UUID target) {
    this.target = target;
  }

  /**
   * Gets {@link RpgEquipmentSlot}.
   *
   * @return {@link RpgEquipmentSlot}
   */
  public RpgEquipmentSlot getSlot() {
    return this.slot;
  }

  /**
   * Sets the {@link RpgEquipmentSlot}.
   *
   * @param slot {@link RpgEquipmentSlot}
   */
  public void setSlot(RpgEquipmentSlot slot) {
    this.slot = slot;
  }

  /**
   * Gets {@link Trigger}.
   *
   * @return {@link Trigger}
   */
  public Trigger getTrigger() {
    return this.trigger;
  }

  /**
   * Sets the {@link Trigger}.
   *
   * @param trigger {@link Trigger}
   */
  public void setTrigger(Trigger trigger) {
    this.trigger = trigger;
  }

  /**
   * Gets object type.
   *
   * @return object type
   */
  public String getObjectType() {
    return this.objectType;
  }

  /**
   * Sets the object type.
   *
   * @param objectType object type
   */
  public void setObjectType(String objectType) {
    this.objectType = objectType;
  }
}
