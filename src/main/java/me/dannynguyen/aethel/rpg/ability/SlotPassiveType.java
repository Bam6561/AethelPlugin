package me.dannynguyen.aethel.rpg.ability;

import me.dannynguyen.aethel.rpg.enums.PassiveAbilityType;
import me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents an equipment slot passive ability pair.
 *
 * @author Danny Nguyen
 * @version 1.16.3
 * @since 1.16.3
 **/
public class SlotPassiveType {
  /**
   * Equipment slot type.
   */
  private final RpgEquipmentSlot eSlot;

  /**
   * Passive ability type.
   */
  private final PassiveAbilityType abilityType;

  /**
   * Associates an equipment slot with an ability.
   *
   * @param eSlot       equipment slot
   * @param abilityType passive ability
   */
  public SlotPassiveType(@NotNull RpgEquipmentSlot eSlot, @NotNull PassiveAbilityType abilityType) {
    this.eSlot = Objects.requireNonNull(eSlot, "Null slot");
    this.abilityType = Objects.requireNonNull(abilityType, "Null ability");
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
   * Gets the passive ability.
   *
   * @return passive ability
   */
  @NotNull
  public PassiveAbilityType getAbilityType() {
    return this.abilityType;
  }

  /**
   * Returns true if the slot ability has the same fields.
   *
   * @param o compared object
   * @return if the slot ability has the same fields
   */
  @Override
  public boolean equals(Object o) {
    if (o instanceof SlotPassiveType slotPassiveType) {
      return (slotPassiveType.getSlot() == eSlot && slotPassiveType.getAbilityType() == abilityType);
    }
    return false;
  }

  /**
   * Gets the hash value of the slot ability.
   *
   * @return hash value of the slot ability
   */
  @Override
  public int hashCode() {
    return Objects.hash(eSlot, abilityType);
  }
}
