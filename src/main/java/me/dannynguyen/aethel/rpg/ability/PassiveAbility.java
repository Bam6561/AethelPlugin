package me.dannynguyen.aethel.rpg.ability;

import me.dannynguyen.aethel.rpg.enums.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents an item's {@link PassiveAbilityType}.
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
   * {@link RpgEquipmentSlot}
   */
  private final RpgEquipmentSlot eSlot;

  /**
   * {@link Trigger}
   */
  private final Trigger trigger;

  /**
   * {@link PassiveAbilityType}
   */
  private final PassiveAbilityType type;

  /**
   * {@link TriggerCondition} data.
   */
  private final List<String> conditionData = new ArrayList<>();

  /**
   * {@link PassiveAbilityEffect} data.
   */
  private final List<String> effectData = new ArrayList<>();

  /**
   * Associates a {@link PassiveAbilityType passive ability} with its data.
   *
   * @param onCooldownPassives {@link PassiveAbilityType} on cooldown
   * @param eSlot              {@link RpgEquipmentSlot}
   * @param trigger            {@link Trigger}
   * @param type               {@link PassiveAbilityType}
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
   * {@link TriggerCondition} and {@link PassiveAbilityEffect} data.
   *
   * @param condition  {@link TriggerCondition}
   * @param effect     {@link PassiveAbilityEffect}
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
   * Gets the {@link RpgEquipmentSlot}.
   *
   * @return {@link RpgEquipmentSlot}
   */
  @NotNull
  public RpgEquipmentSlot getSlot() {
    return this.eSlot;
  }

  /**
   * Gets the {@link Trigger}.
   *
   * @return {@link Trigger}
   */
  @NotNull
  public Trigger getTrigger() {
    return this.trigger;
  }

  /**
   * Gets the {@link PassiveAbilityType}.
   *
   * @return {@link PassiveAbilityType}
   */
  @NotNull
  public PassiveAbilityType getType() {
    return this.type;
  }

  /**
   * Gets the {@link TriggerCondition} data.
   *
   * @return {@link TriggerCondition} data.
   */
  @NotNull
  public List<String> getConditionData() {
    return this.conditionData;
  }

  /**
   * Gets the {@link PassiveAbilityEffect} data.
   *
   * @return {@link PassiveAbilityEffect} data
   */
  @NotNull
  public List<String> getEffectData() {
    return this.effectData;
  }

  /**
   * Gets if the {@link PassiveAbilityType} is on cooldown.
   *
   * @return if the {@link PassiveAbilityType} is on cooldown
   */
  public boolean isOnCooldown() {
    return onCooldownPassives.get(trigger).contains(new SlotPassiveType(eSlot, type));
  }

  /**
   * Sets if the {@link PassiveAbilityType} is on cooldown.
   *
   * @param isOnCooldown is on cooldown
   */
  public void setOnCooldown(boolean isOnCooldown) {
    SlotPassiveType slotPassiveType = new SlotPassiveType(eSlot, type);
    if (isOnCooldown) {
      onCooldownPassives.get(trigger).add(slotPassiveType);
    } else {
      onCooldownPassives.get(trigger).remove(slotPassiveType);
    }
  }
}
