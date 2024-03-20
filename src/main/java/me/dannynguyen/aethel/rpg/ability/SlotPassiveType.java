package me.dannynguyen.aethel.rpg.ability;

import me.dannynguyen.aethel.rpg.enums.PassiveAbilityType;
import me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents an {@link RpgEquipmentSlot} {@link PassiveAbilityType} pair.
 *
 * @author Danny Nguyen
 * @version 1.16.3
 * @since 1.16.3
 **/
public class SlotPassiveType {
  /**
   * {@link RpgEquipmentSlot}
   */
  private final RpgEquipmentSlot eSlot;

  /**
   * {@link PassiveAbilityType}
   */
  private final PassiveAbilityType type;

  /**
   * Associates an {@link RpgEquipmentSlot} with an {@link PassiveAbilityType}.
   *
   * @param eSlot {@link RpgEquipmentSlot}
   * @param type  {@link PassiveAbilityType}
   */
  public SlotPassiveType(@NotNull RpgEquipmentSlot eSlot, @NotNull PassiveAbilityType type) {
    this.eSlot = Objects.requireNonNull(eSlot, "Null slot");
    this.type = Objects.requireNonNull(type, "Null ability");
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
   * Gets the {@link PassiveAbilityType}.
   *
   * @return {@link PassiveAbilityType}
   */
  @NotNull
  public PassiveAbilityType getType() {
    return this.type;
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
      return (slotPassiveType.getSlot() == eSlot && slotPassiveType.getType() == type);
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
    return Objects.hash(eSlot, type);
  }
}
