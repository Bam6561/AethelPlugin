package me.dannynguyen.aethel.plugin;

import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType;
import me.dannynguyen.aethel.listeners.ActionEvent;
import me.dannynguyen.aethel.listeners.MenuEvent;
import me.dannynguyen.aethel.listeners.MessageEvent;

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
   * {@link ActionEvent.Input}
   */
  private ActionEvent.Input actionInput;
  /**
   * {@link MessageEvent.Type}
   */
  private MessageEvent.Type messageType;
  /**
   * {@link MenuEvent.Menu}
   */
  private MenuEvent.Menu menu;
  /**
   * {@link MenuEvent.Mode}
   */
  private MenuEvent.Mode mode;
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
   * {@link PassiveTriggerType}
   */
  private PassiveTriggerType passiveTriggerType;
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
   * Gets {@link ActionEvent.Input}.
   *
   * @return {@link ActionEvent.Input}
   */
  public ActionEvent.Input getActionInput() {
    return this.actionInput;
  }

  /**
   * Sets the {@link ActionEvent.Input}.
   *
   * @param actionInput {@link ActionEvent.Input}
   */
  public void setActionInput(ActionEvent.Input actionInput) {
    this.actionInput = actionInput;
  }

  /**
   * Gets {@link MessageEvent.Type}.
   *
   * @return {@link MessageEvent.Type}
   */
  public MessageEvent.Type getMessageInput() {
    return this.messageType;
  }

  /**
   * Sets the {@link MessageEvent.Type}.
   *
   * @param messageType {@link MessageEvent.Type}
   */
  public void setMessageInput(MessageEvent.Type messageType) {
    this.messageType = messageType;
  }

  /**
   * Gets {@link MenuEvent.Menu menu type}.
   *
   * @return {@link MenuEvent.Menu menu type}
   */
  public MenuEvent.Menu getMenu() {
    return this.menu;
  }


  /**
   * Sets the {@link MenuEvent.Menu menu type}.
   *
   * @param menu {@link MenuEvent.Menu menu type}
   */
  public void setMenu(MenuEvent.Menu menu) {
    this.menu = menu;
  }

  /**
   * Gets {@link MenuEvent.Mode menu mode}.
   *
   * @return {@link MenuEvent.Mode menu mode}
   */
  public MenuEvent.Mode getMode() {
    return this.mode;
  }

  /**
   * Sets the {@link MenuEvent.Mode menu mode}
   *
   * @param mode {@link MenuEvent.Mode menu mode}
   */
  public void setMode(MenuEvent.Mode mode) {
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
   * Gets {@link PassiveTriggerType}.
   *
   * @return {@link PassiveTriggerType}
   */
  public PassiveTriggerType getTrigger() {
    return this.passiveTriggerType;
  }

  /**
   * Sets the {@link PassiveTriggerType}.
   *
   * @param passiveTriggerType {@link PassiveTriggerType}
   */
  public void setTrigger(PassiveTriggerType passiveTriggerType) {
    this.passiveTriggerType = passiveTriggerType;
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
