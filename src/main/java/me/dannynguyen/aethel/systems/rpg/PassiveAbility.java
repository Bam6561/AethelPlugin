package me.dannynguyen.aethel.systems.rpg;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an item's passive ability.
 *
 * @author Danny Nguyen
 * @version 1.16.2
 * @since 1.16.2
 */
public class PassiveAbility {
  /**
   * Type of passive ability.
   */
  private final PassiveAbilityType abilityType;

  /**
   * Type of trigger condition.
   */
  private final TriggerCondition condition;

  /**
   * Trigger data.
   */
  private final List<String> triggerData = new ArrayList<>();

  /**
   * Ability data.
   */
  private final List<String> effectData = new ArrayList<>();

  /**
   * If the ability is on cooldown.
   */
  private boolean isOnCooldown = false;

  /**
   * Associates a passive ability with its data.
   *
   * @param ability    ability type
   * @param dataValues ability data
   */
  public PassiveAbility(@NotNull TriggerCondition condition, @NotNull PassiveAbilityType ability, @NotNull String[] dataValues) {
    this.abilityType = ability;
    this.condition = condition;
    initializeAbilityData(dataValues);
  }

  /**
   * Initializes the passive ability's trigger and ability data.
   *
   * @param dataValues ability data
   */
  private void initializeAbilityData(String[] dataValues) {
    switch (condition) {
      case CHANCE_COOLDOWN -> {
        triggerData.add(dataValues[0]);
        triggerData.add(dataValues[1]);
        switch (abilityType.getEffect()) {
          case STACK_INSTANCE, CHAIN -> {
            effectData.add(dataValues[2]);
            effectData.add(dataValues[3]);
          }
        }
      }
      case HP_CHANCE_COOLDOWN -> {
        triggerData.add(dataValues[0]);
        triggerData.add(dataValues[1]);
        triggerData.add(dataValues[2]);
        switch (abilityType.getEffect()) {
          case STACK_INSTANCE, CHAIN -> {
            effectData.add(dataValues[3]);
            effectData.add(dataValues[4]);
          }
        }
      }
    }
  }

  /**
   * Sets if the ability is on cooldown
   *
   * @param onCooldown is on cooldown
   */
  public void setOnCooldown(boolean onCooldown) {
    isOnCooldown = onCooldown;
  }

  /**
   * Gets the ability's type.
   *
   * @return ability's type
   */
  @NotNull
  public PassiveAbilityType getAbilityType() {
    return abilityType;
  }

  /**
   * Gets the ability's trigger data.
   *
   * @return ability's trigger data
   */
  @NotNull
  public List<String> getTriggerData() {
    return triggerData;
  }

  /**
   * Gets the ability's effect data.
   *
   * @return ability's effect data
   */
  @NotNull
  public List<String> getEffectData() {
    return effectData;
  }

  /**
   * Gets if the ability is on cooldown.
   *
   * @return if the ability is on cooldown
   */
  public boolean isOnCooldown() {
    return isOnCooldown;
  }
}
