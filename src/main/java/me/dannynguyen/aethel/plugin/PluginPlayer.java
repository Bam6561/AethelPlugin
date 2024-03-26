package me.dannynguyen.aethel.plugin;

import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType;
import me.dannynguyen.aethel.listeners.MenuEvent;
import me.dannynguyen.aethel.listeners.MessageEvent;
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
   * Gets {@link MessageEvent.Type}.
   *
   * @return {@link MessageEvent.Type}
   */
  @Nullable
  public MessageEvent.Type getMessageInput() {
    return this.messageType;
  }

  /**
   * Sets the {@link MessageEvent.Type}.
   *
   * @param messageType {@link MessageEvent.Type}
   */
  public void setMessageInput(@Nullable MessageEvent.Type messageType) {
    this.messageType = messageType;
  }

  /**
   * Gets {@link MenuEvent.Menu menu type}.
   *
   * @return {@link MenuEvent.Menu menu type}
   */
  @Nullable
  public MenuEvent.Menu getMenu() {
    return this.menu;
  }


  /**
   * Sets the {@link MenuEvent.Menu menu type}.
   *
   * @param menu {@link MenuEvent.Menu menu type}
   */
  public void setMenu(@Nullable MenuEvent.Menu menu) {
    this.menu = menu;
  }

  /**
   * Gets {@link MenuEvent.Mode menu mode}.
   *
   * @return {@link MenuEvent.Mode menu mode}
   */
  @Nullable
  public MenuEvent.Mode getMode() {
    return this.mode;
  }

  /**
   * Sets the {@link MenuEvent.Mode menu mode}
   *
   * @param mode {@link MenuEvent.Mode menu mode}
   */
  public void setMode(@Nullable MenuEvent.Mode mode) {
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
