package me.dannynguyen.aethel.commands.itemeditor;

/**
 * Represents a {@link me.dannynguyen.aethel.rpg.enums.PassiveAbilityType passive ability's}
 * {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot equipment slot} and
 * {@link me.dannynguyen.aethel.rpg.enums.TriggerCondition condition}.
 * <p>
 * Used for text display within the {@link PassiveMenu} menu.
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.17.7
 * @since 1.15.12
 */
class PassiveLoreIdentifier {
  /**
   * Ability's {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot equipment slot}.
   */
  private final String slot;

  /**
   * Ability's {@link me.dannynguyen.aethel.rpg.enums.TriggerCondition trigger condition}/
   */
  private final String condition;

  /**
   * Associates an {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot equipment slot} with
   * its {@link me.dannynguyen.aethel.rpg.enums.TriggerCondition trigger condition}.
   *
   * @param eSlot     {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot equipment slot}
   * @param condition {@link me.dannynguyen.aethel.rpg.enums.TriggerCondition trigger condition}.
   */
  protected PassiveLoreIdentifier(String eSlot, String condition) {
    this.slot = eSlot;
    this.condition = condition;
  }

  /**
   * Gets the ability's {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot equipment slot}.
   *
   * @return ability's {@link me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot equipment slot}
   */
  protected String getSlot() {
    return this.slot;
  }

  /**
   * Gets the ability's {@link me.dannynguyen.aethel.rpg.enums.TriggerCondition trigger condition}.
   *
   * @return ability's {@link me.dannynguyen.aethel.rpg.enums.TriggerCondition trigger condition}
   */
  protected String getCondition() {
    return this.condition;
  }
}
