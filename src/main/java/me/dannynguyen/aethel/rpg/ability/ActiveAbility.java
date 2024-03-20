package me.dannynguyen.aethel.rpg.ability;

import me.dannynguyen.aethel.rpg.enums.ActiveAbilityEffect;
import me.dannynguyen.aethel.rpg.enums.ActiveAbilityType;
import me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents an item's {@link ActiveAbilityType active ability}.
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
   * {@link RpgEquipmentSlot Equipment slot}.
   */
  private final RpgEquipmentSlot eSlot;

  /**
   * {@link ActiveAbilityType Type}.
   */
  private final ActiveAbilityType type;

  /**
   * {@link ActiveAbilityEffect Effect} data.
   */
  private final List<String> effectData = new ArrayList<>();

  /**
   * Associates an {@link ActiveAbilityType active ability} with its data.
   *
   * @param onCooldownActives actives on cooldown
   * @param eSlot             {@link RpgEquipmentSlot equipment slot}
   * @param type              {@link ActiveAbilityType type}
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
   * @param effect     {@link ActiveAbilityEffect effect}
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
   * Gets the {@link RpgEquipmentSlot equipment slot}.
   *
   * @return {@link RpgEquipmentSlot equipment slot}.
   */
  @NotNull
  public RpgEquipmentSlot getSlot() {
    return eSlot;
  }


  /**
   * Gets the {@link ActiveAbilityType type}.
   *
   * @return {@link ActiveAbilityType type}
   */
  @NotNull
  public ActiveAbilityType getType() {
    return type;
  }

  /**
   * Gets the {@link ActiveAbilityEffect effect} data.
   *
   * @return {@link ActiveAbilityEffect effect} data
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
    return onCooldownActives.get(eSlot).contains(type);
  }

  /**
   * Sets if the ability is on cooldown.
   *
   * @param onCooldown is on cooldown
   */
  public void setOnCooldown(boolean onCooldown) {
    if (onCooldown) {
      onCooldownActives.get(eSlot).add(type);
    } else {
      onCooldownActives.get(eSlot).remove(type);
    }
  }
}
