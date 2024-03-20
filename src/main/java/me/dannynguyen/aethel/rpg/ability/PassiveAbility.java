package me.dannynguyen.aethel.rpg.ability;

import me.dannynguyen.aethel.rpg.enums.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents an item's {@link PassiveAbilityType passive ability}.
 *
 * @author Danny Nguyen
 * @version 1.17.1
 * @since 1.16.2
 */
public class PassiveAbility {
  /**
   * {@link PassiveAbilityType Passive abilities} on cooldown.
   */
  private final Map<Trigger, Set<SlotPassiveType>> onCooldownPassives;

  /**
   * {@link RpgEquipmentSlot Equipment slot}.
   */
  private final RpgEquipmentSlot eSlot;

  /**
   * {@link Trigger Trigger}.
   */
  private final Trigger trigger;

  /**
   * {@link PassiveAbilityType Type}.
   */
  private final PassiveAbilityType type;

  /**
   * {@link TriggerCondition Condition} data.
   */
  private final List<String> conditionData = new ArrayList<>();

  /**
   * {@link PassiveAbilityEffect Effect} data.
   */
  private final List<String> effectData = new ArrayList<>();

  /**
   * Associates a {@link PassiveAbility passive ability} with its data.
   *
   * @param onCooldownPassives passives on cooldown
   * @param eSlot              {@link RpgEquipmentSlot equipment slot}
   * @param trigger            {@link Trigger trigger}
   * @param type               {@link PassiveAbilityType type}
   * @param dataValues         ability data
   */
  public PassiveAbility(@NotNull Map<Trigger, Set<SlotPassiveType>> onCooldownPassives, @NotNull RpgEquipmentSlot eSlot, @NotNull Trigger trigger, @NotNull PassiveAbilityType type, @NotNull String[] dataValues) {
    this.onCooldownPassives = Objects.requireNonNull(onCooldownPassives, "Null on cooldown passives");
    this.eSlot = Objects.requireNonNull(eSlot, "Null slot");
    this.trigger = Objects.requireNonNull(trigger, "Null trigger");
    this.type = Objects.requireNonNull(type, "Null ability");
    initializeAbilityData(trigger.getCondition(), type.getEffect(), dataValues);
  }

  /**
   * Initializes the {@link PassiveAbilityType passive ability's}
   * {@link TriggerCondition condition} and {@link PassiveAbilityEffect effect} data.
   *
   * @param condition  {@link TriggerCondition condition}
   * @param effect     {@link PassiveAbilityEffect effect}
   * @param dataValues ability data
   */
  private void initializeAbilityData(TriggerCondition condition, PassiveAbilityEffect effect, String[] dataValues) {
    switch (condition) {
      case CHANCE_COOLDOWN, HEALTH_COOLDOWN -> {
        conditionData.add(dataValues[0]);
        conditionData.add(dataValues[1]);
        switch (effect) {
          case STACK_INSTANCE, CHAIN_DAMAGE -> {
            effectData.add(dataValues[2]);
            effectData.add(dataValues[3]);
            effectData.add(dataValues[4]);
          }
        }
      }
    }
  }

  /**
   * Gets the {@link RpgEquipmentSlot equipment slot}.
   *
   * @return {@link RpgEquipmentSlot equipment slot}
   */
  @NotNull
  public RpgEquipmentSlot getSlot() {
    return this.eSlot;
  }

  /**
   * Gets the {@link Trigger trigger}.
   *
   * @return {@link Trigger trigger}
   */
  @NotNull
  public Trigger getTrigger() {
    return this.trigger;
  }

  /**
   * Gets the {@link PassiveAbilityType type}.
   *
   * @return {@link PassiveAbilityType type}
   */
  @NotNull
  public PassiveAbilityType getType() {
    return this.type;
  }

  /**
   * Gets the {@link TriggerCondition condition} data.
   *
   * @return {@link TriggerCondition condition} data.
   */
  @NotNull
  public List<String> getConditionData() {
    return this.conditionData;
  }

  /**
   * Gets the {@link PassiveAbilityEffect effect} data.
   *
   * @return {@link PassiveAbilityEffect effect} data
   */
  @NotNull
  public List<String> getEffectData() {
    return this.effectData;
  }

  /**
   * Gets if the ability is on cooldown.
   *
   * @return if the ability is on cooldown
   */
  public boolean isOnCooldown() {
    return onCooldownPassives.get(trigger).contains(new SlotPassiveType(eSlot, type));
  }

  /**
   * Sets if the ability is on cooldown.
   *
   * @param onCooldown is on cooldown
   */
  public void setOnCooldown(boolean onCooldown) {
    SlotPassiveType slotPassiveType = new SlotPassiveType(eSlot, type);
    if (onCooldown) {
      onCooldownPassives.get(trigger).add(slotPassiveType);
    } else {
      onCooldownPassives.get(trigger).remove(slotPassiveType);
    }
  }
}
