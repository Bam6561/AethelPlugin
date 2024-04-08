package me.dannynguyen.aethel.plugin;

import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType;
import me.dannynguyen.aethel.listeners.MenuListener;
import me.dannynguyen.aethel.listeners.MessageListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a player's plugin metadata.
 *
 * @author Danny Nguyen
 * @version 1.19.0
 * @since 1.17.16
 */
public class PluginPlayer {
  /**
   * {@link me.dannynguyen.aethel.commands.DeveloperCommand}
   */
  private boolean isDeveloper;
  /**
   * {@link MessageListener.Type}
   */
  private MessageListener.Type messageType;
  /**
   * {@link MenuListener.Menu}
   */
  private MenuListener.Menu menu;
  /**
   * {@link MenuListener.Mode}
   */
  private MenuListener.Mode mode;
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
  private PassiveTriggerType trigger;
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
   * Gets {@link MessageListener.Type}.
   *
   * @return {@link MessageListener.Type}
   */
  @Nullable
  public MessageListener.Type getMessageInput() {
    return this.messageType;
  }

  /**
   * Sets the {@link MessageListener.Type}.
   *
   * @param messageType {@link MessageListener.Type}
   */
  public void setMessageInput(@Nullable MessageListener.Type messageType) {
    this.messageType = messageType;
  }

  /**
   * Gets {@link MenuListener.Menu menu type}.
   *
   * @return {@link MenuListener.Menu menu type}
   */
  @Nullable
  public MenuListener.Menu getMenu() {
    return this.menu;
  }


  /**
   * Sets the {@link MenuListener.Menu menu type}.
   *
   * @param menu {@link MenuListener.Menu menu type}
   */
  public void setMenu(@Nullable MenuListener.Menu menu) {
    this.menu = menu;
  }

  /**
   * Gets {@link MenuListener.Mode menu mode}.
   *
   * @return {@link MenuListener.Mode menu mode}
   */
  @Nullable
  public MenuListener.Mode getMode() {
    return this.mode;
  }

  /**
   * Sets the {@link MenuListener.Mode menu mode}
   *
   * @param mode {@link MenuListener.Mode menu mode}
   */
  public void setMode(@Nullable MenuListener.Mode mode) {
    this.mode = mode;
  }

  /**
   * Gets menu category.
   *
   * @return menu category
   */
  @NotNull
  public String getCategory() {
    return this.category;
  }

  /**
   * Sets the menu category.
   *
   * @param category menu category
   */
  public void setCategory(@NotNull String category) {
    this.category = Objects.requireNonNull(category, "Null category");
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
  @Nullable
  public UUID getTarget() {
    return this.target;
  }

  /**
   * Sets the target's UUID.
   *
   * @param target target's UUID
   */
  public void setTarget(@Nullable UUID target) {
    this.target = target;
  }

  /**
   * Gets {@link RpgEquipmentSlot}.
   *
   * @return {@link RpgEquipmentSlot}
   */
  @Nullable
  public RpgEquipmentSlot getSlot() {
    return this.slot;
  }

  /**
   * Sets the {@link RpgEquipmentSlot}.
   *
   * @param slot {@link RpgEquipmentSlot}
   */
  public void setSlot(@Nullable RpgEquipmentSlot slot) {
    this.slot = slot;
  }

  /**
   * Gets {@link PassiveTriggerType}.
   *
   * @return {@link PassiveTriggerType}
   */
  @Nullable
  public PassiveTriggerType getTrigger() {
    return this.trigger;
  }

  /**
   * Sets the {@link PassiveTriggerType}.
   *
   * @param trigger {@link PassiveTriggerType}
   */
  public void setTrigger(@Nullable PassiveTriggerType trigger) {
    this.trigger = trigger;
  }

  /**
   * Gets object type.
   *
   * @return object type
   */
  @NotNull
  public String getObjectType() {
    return this.objectType;
  }

  /**
   * Sets the object type.
   *
   * @param objectType object type
   */
  public void setObjectType(@NotNull String objectType) {
    this.objectType = Objects.requireNonNull(objectType, "Null object type");
  }
}
