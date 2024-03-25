package me.dannynguyen.aethel.rpg.abilities;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.plugin.KeyHeader;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType;
import me.dannynguyen.aethel.listeners.EquipmentEvent;
import me.dannynguyen.aethel.rpg.Equipment;
import me.dannynguyen.aethel.rpg.RpgPlayer;
import me.dannynguyen.aethel.utils.TextFormatter;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents an {@link RpgPlayer}'s {@link PassiveAbility passive}
 * and {@link ActiveAbility active} abilities.
 *
 * @author Danny Nguyen
 * @version 1.17.14
 * @since 1.17.9
 */
public class Abilities {
  /**
   * Used to remove abilities upon {@link EquipmentEvent}.
   */
  private final Map<RpgEquipmentSlot, List<TriggerPassive>> slotPassives = new HashMap<>();

  /**
   * Used to identify unique {@link PassiveAbility passive abilities}
   * after a {@link PassiveTriggerType} is called.
   */
  private final Map<PassiveTriggerType, Map<SlotPassive, PassiveAbility>> triggerPassives = createPassiveTriggers();

  /**
   * {@link SlotPassive Passive abilities} on cooldown.
   */
  private final Map<PassiveTriggerType, Set<SlotPassive>> onCooldownPassives = createPassiveCooldownTriggers();

  /**
   * {@link ActiveAbility Active abilities} identified by their {@link RpgEquipmentSlot} trigger.
   */
  private final Map<RpgEquipmentSlot, List<ActiveAbility>> triggerActives = new HashMap<>();

  /**
   * {@link ActiveAbilityType Active abilities} on cooldown.
   */
  private final Map<RpgEquipmentSlot, Set<ActiveAbilityType>> onCooldownActives = new HashMap<>();

  /**
   * No parameter constructor.
   */
  public Abilities() {
  }

  /**
   * Creates a blank map of {@link PassiveTriggerType triggerable} {@link PassiveAbility passive abilities}.
   *
   * @return blank map of {@link PassiveTriggerType triggerable} {@link PassiveAbility passive abilities}
   */
  private Map<PassiveTriggerType, Map<SlotPassive, PassiveAbility>> createPassiveTriggers() {
    Map<PassiveTriggerType, Map<SlotPassive, PassiveAbility>> triggers = new HashMap<>();
    for (PassiveTriggerType passiveTriggerType : PassiveTriggerType.values()) {
      triggers.put(passiveTriggerType, new HashMap<>());
    }
    return triggers;
  }

  /**
   * Creates a blank map of {@link PassiveTriggerType triggerable}
   * {@link SlotPassive passive abilities} on cooldown.
   *
   * @return blank map of {@link PassiveTriggerType triggerable} {@link SlotPassive passive abilities} on cooldown
   */
  private Map<PassiveTriggerType, Set<SlotPassive>> createPassiveCooldownTriggers() {
    Map<PassiveTriggerType, Set<SlotPassive>> triggers = new HashMap<>();
    for (PassiveTriggerType passiveTriggerType : PassiveTriggerType.values()) {
      triggers.put(passiveTriggerType, new HashSet<>());
    }
    return triggers;
  }

  /**
   * Checks if the item is in the correct {@link RpgEquipmentSlot}
   * before updating the player's {@link PassiveAbility passive abilities}.
   *
   * @param eSlot         {@link RpgEquipmentSlot}
   * @param dataContainer item's persistent tags
   */
  public void readPassives(RpgEquipmentSlot eSlot, PersistentDataContainer dataContainer) {
    String[] passives = dataContainer.get(Key.PASSIVE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ");
    for (String passive : passives) {
      RpgEquipmentSlot slot = RpgEquipmentSlot.valueOf(TextFormatter.formatEnum(passive.substring(0, passive.indexOf("."))));
      if (slot == eSlot) {
        addPassives(eSlot, dataContainer, passive);
      }
    }
  }

  /**
   * Adds new {@link Equipment} {@link PassiveAbility passive abilities}.
   *
   * @param eSlot         {@link RpgEquipmentSlot}
   * @param dataContainer item's persistent tags
   * @param passive       {@link PassiveAbility} data
   */
  private void addPassives(RpgEquipmentSlot eSlot, PersistentDataContainer dataContainer, String passive) {
    NamespacedKey passiveKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.PASSIVE.getHeader() + passive);
    String[] abilityMeta = passive.split("\\.");
    PassiveTriggerType passiveTriggerType = PassiveTriggerType.valueOf(TextFormatter.formatEnum(abilityMeta[1]));
    PassiveAbilityType abilityType = PassiveAbilityType.valueOf(TextFormatter.formatEnum(abilityMeta[2]));
    slotPassives.get(eSlot).add(new TriggerPassive(passiveTriggerType, abilityType));
    triggerPassives.get(passiveTriggerType).put(new SlotPassive(eSlot, abilityType), new PassiveAbility(onCooldownPassives, eSlot, passiveTriggerType, abilityType, dataContainer.get(passiveKey, PersistentDataType.STRING).split(" ")));
  }

  /**
   * Removes existing {@link Equipment} {@link PassiveAbility passive abilities} at an {@link RpgEquipmentSlot}.
   *
   * @param eSlot {@link RpgEquipmentSlot}
   */
  public void removePassives(@NotNull RpgEquipmentSlot eSlot) {
    List<TriggerPassive> abilitiesToRemove = new ArrayList<>();
    for (TriggerPassive triggerPassive : slotPassives.get(Objects.requireNonNull(eSlot, "Null slot"))) {
      triggerPassives.get(triggerPassive.passiveTriggerType()).remove(new SlotPassive(eSlot, triggerPassive.type()));
      abilitiesToRemove.add(triggerPassive);
    }
    slotPassives.get(eSlot).removeAll(abilitiesToRemove);
  }

  /**
   * Gets the player's {@link TriggerPassive passive abilities}
   * that exist on each {@link RpgEquipmentSlot}.
   *
   * @return {@link TriggerPassive passive abilities} that exist on each {@link RpgEquipmentSlot}
   */
  @NotNull
  public Map<RpgEquipmentSlot, List<TriggerPassive>> getSlotPassives() {
    return this.slotPassives;
  }

  /**
   * Gets the player's {@link PassiveTriggerType triggerable} {@link PassiveAbility passive abilities}.
   *
   * @return {@link PassiveTriggerType triggerable} {@link PassiveAbility passive abilities}
   */
  @NotNull
  public Map<PassiveTriggerType, Map<SlotPassive, PassiveAbility>> getTriggerPassives() {
    return this.triggerPassives;
  }

  /**
   * Gets the player's {@link RpgEquipmentSlot} {@link ActiveAbility active abilities}
   * triggered by crouch binds.
   *
   * @return {@link RpgEquipmentSlot} {@link ActiveAbility active abilities} triggered by crouch binds
   */
  @NotNull
  public Map<RpgEquipmentSlot, List<ActiveAbility>> getTriggerActives() {
    return this.triggerActives;
  }

  /**
   * Represents an {@link RpgEquipmentSlot} {@link PassiveAbilityType} pair.
   * <p>
   * Used to identify unique {@link PassiveAbility passive abilities}
   * after a {@link PassiveTriggerType} is called.
   *
   * @author Danny Nguyen
   * @version 1.18.1
   * @since 1.16.3
   */
  public record SlotPassive(RpgEquipmentSlot eSlot, PassiveAbilityType type) {
    /**
     * Associates an {@link RpgEquipmentSlot} with an {@link PassiveAbilityType}.
     *
     * @param eSlot {@link RpgEquipmentSlot}
     * @param type  {@link PassiveAbilityType}
     */
    public SlotPassive(@NotNull RpgEquipmentSlot eSlot, @NotNull PassiveAbilityType type) {
      this.eSlot = Objects.requireNonNull(eSlot, "Null slot");
      this.type = Objects.requireNonNull(type, "Null ability");
    }

    /**
     * Gets the {@link RpgEquipmentSlot}.
     *
     * @return {@link RpgEquipmentSlot}
     */
    @Override
    @NotNull
    public RpgEquipmentSlot eSlot() {
      return this.eSlot;
    }

    /**
     * Gets the {@link PassiveAbilityType}.
     *
     * @return {@link PassiveAbilityType}
     */
    @NotNull
    public PassiveAbilityType type() {
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
        return (slotPassive.eSlot() == eSlot && slotPassive.type() == type);
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

  /**
   * Represents a {@link PassiveTriggerType} {@link PassiveAbilityType} pair.
   * <p>
   * Used to remove abilities upon {@link EquipmentEvent}.
   *
   * @author Danny Nguyen
   * @version 1.18.1
   * @since 1.16.1
   */
  public record TriggerPassive(PassiveTriggerType passiveTriggerType, PassiveAbilityType type) {
    /**
     * Associates a {@link PassiveTriggerType} with a {@link PassiveAbilityType}.
     *
     * @param passiveTriggerType {@link PassiveTriggerType}
     * @param type    {@link PassiveAbilityType}
     */
    public TriggerPassive(@NotNull PassiveTriggerType passiveTriggerType, @NotNull PassiveAbilityType type) {
      this.passiveTriggerType = Objects.requireNonNull(passiveTriggerType, "Null trigger");
      this.type = Objects.requireNonNull(type, "Null ability");
    }

    /**
     * Gets the {@link PassiveTriggerType}.
     *
     * @return {@link PassiveTriggerType}
     */
    @NotNull
    public PassiveTriggerType passiveTriggerType() {
      return this.passiveTriggerType;
    }

    /**
     * Gets the {@link PassiveAbilityType}.
     *
     * @return {@link PassiveAbilityType}
     */
    @NotNull
    public PassiveAbilityType type() {
      return this.type;
    }
  }
}
