package me.dannynguyen.aethel.systems.rpg.ability;

import me.dannynguyen.aethel.systems.rpg.RpgEquipmentSlot;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents an equipment slot ability pair.
 *
 * @author Danny Nguyen
 * @version 1.16.3
 * @since 1.16.3
 **/
public class SlotAbility {
  /**
   * Type of equipment slot.
   */
  private final RpgEquipmentSlot slot;

  /**
   * Passive ability.
   */
  private final PassiveAbilityType ability;

  /**
   * Associates an equipment slot with an ability.
   *
   * @param slot    equipment slot
   * @param ability passive ability
   */
  public SlotAbility(@NotNull RpgEquipmentSlot slot, @NotNull PassiveAbilityType ability) {
    this.slot = Objects.requireNonNull(slot, "Null slot");
    this.ability = Objects.requireNonNull(ability, "Null ability");
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
   * Gets the passive ability.
   *
   * @return passive ability
   */
  @NotNull
  public PassiveAbilityType getAbility() {
    return this.ability;
  }

  /**
   * Returns true if the slot ability has the same fields.
   *
   * @param o compared object
   * @return if the slot ability has the same fields
   */
  @Override
  public boolean equals(Object o) {
    if (o instanceof SlotAbility slotAbility) {
      return (slotAbility.getSlot() == this.slot && slotAbility.getAbility() == this.ability);
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
    return Objects.hash(slot, ability);
  }
}
