package me.dannynguyen.aethel.rpg.ability;

import me.dannynguyen.aethel.rpg.enums.PassiveAbilityType;
import me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents an {@link RpgEquipmentSlot equipment slot} {@link PassiveAbilityType passive ability} pair.
 *
 * @author Danny Nguyen
 * @version 1.16.3
 * @since 1.16.3
 **/
public class SlotPassiveType {
  /**
   * {@link RpgEquipmentSlot Equipment slot}.
   */
  private final RpgEquipmentSlot eSlot;

  /**
   * {@link PassiveAbilityType Type}.
   */
  private final PassiveAbilityType type;

  /**
   * Associates an {@link RpgEquipmentSlot equipment slot} with an {@link PassiveAbilityType ability}.
   *
   * @param eSlot {@link RpgEquipmentSlot equipment slot}
   * @param type  {@link PassiveAbilityType type}
   */
  public SlotPassiveType(@NotNull RpgEquipmentSlot eSlot, @NotNull PassiveAbilityType type) {
    this.eSlot = Objects.requireNonNull(eSlot, "Null slot");
    this.type = Objects.requireNonNull(type, "Null ability");
  }

  /**
   * Gets the {@link RpgEquipmentSlot equipment slot}.
   *
   * @return {@link RpgEquipmentSlot equipment slot}
   */
  @NotNull
  public RpgEquipmentSlot getSlot() {
    return this.eSlot;
  }

  /**
   * Gets the {@link PassiveAbilityType type}.
   *
   * @return {@link PassiveAbilityType type}
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
