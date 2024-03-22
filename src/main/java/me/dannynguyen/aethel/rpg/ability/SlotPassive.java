package me.dannynguyen.aethel.rpg.ability;

import me.dannynguyen.aethel.rpg.enums.PassiveType;
import me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents an {@link RpgEquipmentSlot} {@link PassiveType} pair.
 * <p>
 * Used to identify unique {@link PassiveAbility passive abilities}
 * after a {@link me.dannynguyen.aethel.rpg.enums.Trigger} is called.
 *
 * @author Danny Nguyen
 * @version 1.16.3
 * @since 1.16.3
 **/
public class SlotPassive {
  /**
   * {@link RpgEquipmentSlot}
   */
  private final RpgEquipmentSlot eSlot;

  /**
   * {@link PassiveType}
   */
  private final PassiveType type;

  /**
   * Associates an {@link RpgEquipmentSlot} with an {@link PassiveType}.
   *
   * @param eSlot {@link RpgEquipmentSlot}
   * @param type  {@link PassiveType}
   */
  public SlotPassive(@NotNull RpgEquipmentSlot eSlot, @NotNull PassiveType type) {
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
   * Gets the {@link PassiveType}.
   *
   * @return {@link PassiveType}
   */
  @NotNull
  public PassiveType getType() {
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
    if (o instanceof SlotPassive slotPassive) {
      return (slotPassive.getSlot() == eSlot && slotPassive.getType() == type);
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
