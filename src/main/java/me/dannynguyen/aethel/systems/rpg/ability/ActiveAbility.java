package me.dannynguyen.aethel.systems.rpg.ability;

import me.dannynguyen.aethel.systems.rpg.RpgEquipmentSlot;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents an item's active ability.
 *
 * @author Danny Nguyen
 * @version 1.17.4
 * @since 1.17.4
 */
public class ActiveAbility {
  /**
   * Active abilities on cooldown.
   */
  private final Map<RpgEquipmentSlot, Set<ActiveAbilityType>> onCooldownActives;

  /**
   * Type of equipment slot.
   */
  private final RpgEquipmentSlot slot;

  /**
   * Type of passive ability.
   */
  private final ActiveAbilityType ability;

  /**
   * Ability data.
   */
  private final List<String> effectData = new ArrayList<>();

  /**
   * Associates an active ability with its data.
   *
   * @param onCooldownActives actives on cooldown
   * @param slot              slot type
   * @param ability           ability type
   * @param dataValues        ability data
   */
  public ActiveAbility(@NotNull Map<RpgEquipmentSlot, Set<ActiveAbilityType>> onCooldownActives, @NotNull RpgEquipmentSlot slot, @NotNull ActiveAbilityType ability, @NotNull String[] dataValues) {
    this.onCooldownActives = Objects.requireNonNull(onCooldownActives, "Null on cooldown actives");
    this.slot = Objects.requireNonNull(slot, "Null slot");
    this.ability = Objects.requireNonNull(ability, "Null ability");
    initializeAbilityData(ability.getEffect(), dataValues);
  }

  /**
   * Initializes the active ability's ability data.
   *
   * @param abilityEffect ability effect
   * @param dataValues    ability data
   */
  private void initializeAbilityData(ActiveAbilityEffect abilityEffect, String[] dataValues) {
    switch (abilityEffect) {
      case MOVEMENT, PROJECTION -> {
        effectData.add(dataValues[0]);
        effectData.add(dataValues[1]);
      }
      case SHATTER -> {
        effectData.add(dataValues[0]);
        effectData.add(dataValues[1]);
        effectData.add(dataValues[2]);
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
    return slot;
  }


  /**
   * Gets the active ability type.
   *
   * @return active ability type
   */
  @NotNull
  public ActiveAbilityType getAbility() {
    return ability;
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
    return onCooldownActives.get(slot).contains(ability);
  }

  /**
   * Sets if the ability is on cooldown.
   *
   * @param onCooldown is on cooldown
   */
  public void setOnCooldown(boolean onCooldown) {
    if (onCooldown) {
      onCooldownActives.get(slot).add(ability);
    } else {
      onCooldownActives.get(slot).remove(ability);
    }
  }
}
