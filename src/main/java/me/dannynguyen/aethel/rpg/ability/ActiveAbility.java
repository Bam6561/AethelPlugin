package me.dannynguyen.aethel.rpg.ability;

import me.dannynguyen.aethel.rpg.enums.ActiveAbilityEffect;
import me.dannynguyen.aethel.rpg.enums.ActiveAbilityType;
import me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents an item's {@link ActiveAbilityType}.
 *
 * @author Danny Nguyen
 * @version 1.17.4
 * @since 1.17.4
 */
public class ActiveAbility {
  /**
   * {@link ActiveAbilityType Active abilities} on cooldown.
   */
  private final Map<RpgEquipmentSlot, Set<ActiveAbilityType>> onCooldownActives;

  /**
   * {@link RpgEquipmentSlot}
   */
  private final RpgEquipmentSlot eSlot;

  /**
   * {@link ActiveAbilityType}
   */
  private final ActiveAbilityType type;

  /**
   * {@link ActiveAbilityEffect} data.
   */
  private final List<String> effectData = new ArrayList<>();

  /**
   * Associates an {@link ActiveAbilityType active ability} with its data.
   *
   * @param onCooldownActives {@link ActiveAbilityType} on cooldown
   * @param eSlot             {@link RpgEquipmentSlot}
   * @param type              {@link ActiveAbilityType}
   * @param dataValues        ability data
   */
  public ActiveAbility(@NotNull Map<RpgEquipmentSlot, Set<ActiveAbilityType>> onCooldownActives, @NotNull RpgEquipmentSlot eSlot, @NotNull ActiveAbilityType type, @NotNull String[] dataValues) {
    this.onCooldownActives = Objects.requireNonNull(onCooldownActives, "Null on cooldown actives");
    this.eSlot = Objects.requireNonNull(eSlot, "Null slot");
    this.type = Objects.requireNonNull(type, "Null ability");
    initializeAbilityData(type.getEffect(), dataValues);
  }

  /**
   * Initializes the {@link ActiveAbilityType active ability's} ability data.
   *
   * @param effect     {@link ActiveAbilityEffect}
   * @param dataValues ability data
   */
  private void initializeAbilityData(ActiveAbilityEffect effect, String[] dataValues) {
    switch (effect) {
      case MOVEMENT, PROJECTION -> {
        this.effectData.add(dataValues[0]);
        this.effectData.add(dataValues[1]);
      }
      case SHATTER -> {
        this.effectData.add(dataValues[0]);
        this.effectData.add(dataValues[1]);
        this.effectData.add(dataValues[2]);
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
    return eSlot;
  }


  /**
   * Gets the {@link ActiveAbilityType}.
   *
   * @return {@link ActiveAbilityType}
   */
  @NotNull
  public ActiveAbilityType getType() {
    return type;
  }

  /**
   * Gets the {@link ActiveAbilityEffect} data.
   *
   * @return {@link ActiveAbilityEffect} data
   */
  @NotNull
  public List<String> getEffectData() {
    return effectData;
  }

  /**
   * Gets if the {@link ActiveAbilityType} is on cooldown.
   *
   * @return if the {@link ActiveAbilityType} is on cooldown
   */
  public boolean isOnCooldown() {
    return onCooldownActives.get(eSlot).contains(type);
  }

  /**
   * Sets if the {@link ActiveAbilityType} is on cooldown.
   *
   * @param isOnCooldown is on cooldown
   */
  public void setOnCooldown(boolean isOnCooldown) {
    if (isOnCooldown) {
      onCooldownActives.get(eSlot).add(type);
    } else {
      onCooldownActives.get(eSlot).remove(type);
    }
  }
}
