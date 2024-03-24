package me.dannynguyen.aethel.rpg.abilities;

import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.enums.rpg.abilities.ActiveEffect;
import me.dannynguyen.aethel.enums.rpg.abilities.ActiveType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents an item's {@link ActiveType}.
 *
 * @author Danny Nguyen
 * @version 1.17.4
 * @since 1.17.4
 */
public class ActiveAbility {
  /**
   * {@link ActiveType Active abilities} on cooldown.
   */
  private final Map<RpgEquipmentSlot, Set<ActiveType>> onCooldownActives;

  /**
   * {@link RpgEquipmentSlot}
   */
  private final RpgEquipmentSlot eSlot;

  /**
   * {@link ActiveType}
   */
  private final ActiveType type;

  /**
   * {@link ActiveEffect} data.
   */
  private final List<String> effectData = new ArrayList<>();

  /**
   * Associates an {@link ActiveType active ability} with its data.
   *
   * @param onCooldownActives {@link ActiveType} on cooldown
   * @param eSlot             {@link RpgEquipmentSlot}
   * @param type              {@link ActiveType}
   * @param dataValues        ability data
   */
  public ActiveAbility(@NotNull Map<RpgEquipmentSlot, Set<ActiveType>> onCooldownActives, @NotNull RpgEquipmentSlot eSlot, @NotNull ActiveType type, @NotNull String[] dataValues) {
    this.onCooldownActives = Objects.requireNonNull(onCooldownActives, "Null on cooldown actives");
    this.eSlot = Objects.requireNonNull(eSlot, "Null slot");
    this.type = Objects.requireNonNull(type, "Null ability");
    initializeAbilityData(type.getEffect(), dataValues);
  }

  /**
   * Initializes the {@link ActiveType active ability's} ability data.
   *
   * @param effect     {@link ActiveEffect}
   * @param dataValues ability data
   */
  private void initializeAbilityData(ActiveEffect effect, String[] dataValues) {
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
   * Gets the {@link ActiveType}.
   *
   * @return {@link ActiveType}
   */
  @NotNull
  public ActiveType getType() {
    return type;
  }

  /**
   * Gets the {@link ActiveEffect} data.
   *
   * @return {@link ActiveEffect} data
   */
  @NotNull
  public List<String> getEffectData() {
    return effectData;
  }

  /**
   * Gets if the {@link ActiveType} is on cooldown.
   *
   * @return if the {@link ActiveType} is on cooldown
   */
  public boolean isOnCooldown() {
    return onCooldownActives.get(eSlot).contains(type);
  }

  /**
   * Sets if the {@link ActiveType} is on cooldown.
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
