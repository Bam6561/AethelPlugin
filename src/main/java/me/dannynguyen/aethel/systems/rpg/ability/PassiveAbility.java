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
  private final Map<Trigger, Set<SlotAbility>> onCooldownPassives;

  /**
   * Type of equipment slot.
   */
  private final RpgEquipmentSlot slot;

  /**
   * Type of trigger.
   */
  private final Trigger trigger;

  /**
   * Type of passive ability.
   */
  private final PassiveAbilityType ability;

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
   * @param trigger            trigger type
   * @param ability            ability type
   * @param dataValues         ability data
   */
  public PassiveAbility(@NotNull Map<Trigger, Set<SlotAbility>> onCooldownPassives, @NotNull RpgEquipmentSlot slot, @NotNull Trigger trigger, @NotNull PassiveAbilityType ability, @NotNull String[] dataValues) {
    this.onCooldownPassives = Objects.requireNonNull(onCooldownPassives, "Null on cooldown passives");
    this.slot = Objects.requireNonNull(slot, "Null slot");
    this.trigger = Objects.requireNonNull(trigger, "Null trigger");
    this.ability = Objects.requireNonNull(ability, "Null ability");
    initializeAbilityData(trigger.getCondition(), ability.getEffect(), dataValues);
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
   * Sets if the ability is on cooldown.
   *
   * @param onCooldown is on cooldown
   */
  public void setOnCooldown(boolean onCooldown) {
    SlotAbility slotAbility = new SlotAbility(slot, ability);
    if (onCooldown) {
      onCooldownPassives.get(trigger).add(slotAbility);
    } else {
      onCooldownPassives.get(trigger).remove(slotAbility);
    }
  }

  /**
   * Gets the equipment slot.
   *
   * @return equipment slot
   */
  @NotNull
  public RpgEquipmentSlot getSlot() {
    return this.slot;
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
  public PassiveAbilityType getAbility() {
    return this.ability;
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
    return onCooldownPassives.get(trigger).contains(new SlotAbility(slot, ability));
  }
}
