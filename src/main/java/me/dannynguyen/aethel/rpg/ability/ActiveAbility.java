package me.dannynguyen.aethel.rpg.ability;

import me.dannynguyen.aethel.rpg.enums.ActiveAbilityEffect;
import me.dannynguyen.aethel.rpg.enums.ActiveAbilityType;
import me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot;
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
   * Equipment slot type.
   */
  private final RpgEquipmentSlot eSlot;

  /**
   * Passive ability type.
   */
  private final ActiveAbilityType abilityType;

  /**
   * Ability data.
   */
  private final List<String> effectData = new ArrayList<>();

  /**
   * Associates an active ability with its data.
   *
   * @param onCooldownActives actives on cooldown
   * @param eSlot             slot type
   * @param abilityType       ability type
   * @param dataValues        ability data
   */
  public ActiveAbility(@NotNull Map<RpgEquipmentSlot, Set<ActiveAbilityType>> onCooldownActives, @NotNull RpgEquipmentSlot eSlot, @NotNull ActiveAbilityType abilityType, @NotNull String[] dataValues) {
    this.onCooldownActives = Objects.requireNonNull(onCooldownActives, "Null on cooldown actives");
    this.eSlot = Objects.requireNonNull(eSlot, "Null slot");
    this.abilityType = Objects.requireNonNull(abilityType, "Null ability");
    initializeAbilityData(abilityType.getEffect(), dataValues);
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
    return eSlot;
  }


  /**
   * Gets the active ability type.
   *
   * @return active ability type
   */
  @NotNull
  public ActiveAbilityType getAbilityType() {
    return abilityType;
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
    return onCooldownActives.get(eSlot).contains(abilityType);
  }

  /**
   * Sets if the ability is on cooldown.
   *
   * @param onCooldown is on cooldown
   */
  public void setOnCooldown(boolean onCooldown) {
    if (onCooldown) {
      onCooldownActives.get(eSlot).add(abilityType);
    } else {
      onCooldownActives.get(eSlot).remove(abilityType);
    }
  }
}
