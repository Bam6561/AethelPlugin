package me.dannynguyen.aethel.systems.rpg.ability;

import me.dannynguyen.aethel.systems.rpg.RpgEquipmentSlot;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents an item's passive ability.
 *
 * @author Danny Nguyen
 * @version 1.17.1
 * @since 1.16.2
 */
public class PassiveAbility {
  /**
   * Passive abilities on cooldown.
   */
  private final Map<Trigger, Set<SlotPassiveAbility>> onCooldownPassives;

  /**
   * Equipment slot type.
   */
  private final RpgEquipmentSlot eSlot;

  /**
   * Trigger type.
   */
  private final Trigger trigger;

  /**
   * Passive ability type.
   */
  private final PassiveAbilityType abilityType;

  /**
   * Trigger data.
   */
  private final List<String> triggerData = new ArrayList<>();

  /**
   * Ability data.
   */
  private final List<String> effectData = new ArrayList<>();

  /**
   * Associates a passive ability with its data.
   *
   * @param onCooldownPassives passives on cooldown
   * @param eSlot              equipment slot
   * @param trigger            trigger type
   * @param abilityType        ability type
   * @param dataValues         ability data
   */
  public PassiveAbility(@NotNull Map<Trigger, Set<SlotPassiveAbility>> onCooldownPassives, @NotNull RpgEquipmentSlot eSlot, @NotNull Trigger trigger, @NotNull PassiveAbilityType abilityType, @NotNull String[] dataValues) {
    this.onCooldownPassives = Objects.requireNonNull(onCooldownPassives, "Null on cooldown passives");
    this.eSlot = Objects.requireNonNull(eSlot, "Null slot");
    this.trigger = Objects.requireNonNull(trigger, "Null trigger");
    this.abilityType = Objects.requireNonNull(abilityType, "Null ability");
    initializeAbilityData(trigger.getCondition(), abilityType.getEffect(), dataValues);
  }

  /**
   * Initializes the passive ability's trigger and ability data.
   *
   * @param condition     trigger condition
   * @param abilityEffect ability effect
   * @param dataValues    ability data
   */
  private void initializeAbilityData(TriggerCondition condition, PassiveAbilityEffect abilityEffect, String[] dataValues) {
    switch (condition) {
      case CHANCE_COOLDOWN, HEALTH_COOLDOWN -> {
        triggerData.add(dataValues[0]);
        triggerData.add(dataValues[1]);
        switch (abilityEffect) {
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
   * Gets the equipment slot.
   *
   * @return equipment slot
   */
  @NotNull
  public RpgEquipmentSlot getSlot() {
    return this.eSlot;
  }

  /**
   * Gets the trigger type.
   *
   * @return trigger type
   */
  @NotNull
  public Trigger getTrigger() {
    return this.trigger;
  }

  /**
   * Gets the passive ability type.
   *
   * @return passive ability type
   */
  @NotNull
  public PassiveAbilityType getAbilityType() {
    return this.abilityType;
  }

  /**
   * Gets the ability's trigger data.
   *
   * @return ability's trigger data
   */
  @NotNull
  public List<String> getTriggerData() {
    return this.triggerData;
  }

  /**
   * Gets the ability's effect data.
   *
   * @return ability's effect data
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
    return onCooldownPassives.get(trigger).contains(new SlotPassiveAbility(eSlot, abilityType));
  }

  /**
   * Sets if the ability is on cooldown.
   *
   * @param onCooldown is on cooldown
   */
  public void setOnCooldown(boolean onCooldown) {
    SlotPassiveAbility slotPassiveAbility = new SlotPassiveAbility(eSlot, abilityType);
    if (onCooldown) {
      onCooldownPassives.get(trigger).add(slotPassiveAbility);
    } else {
      onCooldownPassives.get(trigger).remove(slotPassiveAbility);
    }
  }
}
