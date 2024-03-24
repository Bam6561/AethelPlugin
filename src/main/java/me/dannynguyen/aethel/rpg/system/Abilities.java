package me.dannynguyen.aethel.rpg.system;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.listeners.EquipmentEvent;
import me.dannynguyen.aethel.enums.plugin.KeyHeader;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.rpg.ability.ActiveAbility;
import me.dannynguyen.aethel.rpg.ability.PassiveAbility;
import me.dannynguyen.aethel.rpg.ability.SlotPassive;
import me.dannynguyen.aethel.rpg.ability.TriggerPassive;
import me.dannynguyen.aethel.enums.rpg.abilities.ActiveType;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveType;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.enums.rpg.abilities.TriggerType;
import me.dannynguyen.aethel.util.TextFormatter;
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
   * after a {@link TriggerType} is called.
   */
  private final Map<TriggerType, Map<SlotPassive, PassiveAbility>> triggerPassives = createBlankPassiveTriggers();

  /**
   * {@link SlotPassive Passive abilities} on cooldown.
   */
  private final Map<TriggerType, Set<SlotPassive>> onCooldownPassives = createBlankPassiveCooldownTriggers();

  /**
   * {@link ActiveAbility Active abilities} identified by their {@link RpgEquipmentSlot} trigger.
   */
  private final Map<RpgEquipmentSlot, List<ActiveAbility>> triggerActives = new HashMap<>();

  /**
   * {@link ActiveType Active abilities} on cooldown.
   */
  private final Map<RpgEquipmentSlot, Set<ActiveType>> onCooldownActives = new HashMap<>();

  /**
   * No parameter constructor.
   */
  public Abilities() {
  }

  /**
   * Creates a blank map of {@link TriggerType triggerable} {@link PassiveAbility passive abilities}.
   *
   * @return blank map of {@link TriggerType triggerable} {@link PassiveAbility passive abilities}
   */
  private Map<TriggerType, Map<SlotPassive, PassiveAbility>> createBlankPassiveTriggers() {
    Map<TriggerType, Map<SlotPassive, PassiveAbility>> triggers = new HashMap<>();
    for (TriggerType triggerType : TriggerType.values()) {
      triggers.put(triggerType, new HashMap<>());
    }
    return triggers;
  }

  /**
   * Creates a blank map of {@link TriggerType triggerable}
   * {@link SlotPassive passive abilities} on cooldown.
   *
   * @return blank map of {@link TriggerType triggerable} {@link SlotPassive passive abilities} on cooldown
   */
  private Map<TriggerType, Set<SlotPassive>> createBlankPassiveCooldownTriggers() {
    Map<TriggerType, Set<SlotPassive>> triggers = new HashMap<>();
    for (TriggerType triggerType : TriggerType.values()) {
      triggers.put(triggerType, new HashSet<>());
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
    TriggerType triggerType = TriggerType.valueOf(TextFormatter.formatEnum(abilityMeta[1]));
    PassiveType abilityType = PassiveType.valueOf(TextFormatter.formatEnum(abilityMeta[2]));
    slotPassives.get(eSlot).add(new TriggerPassive(triggerType, abilityType));
    triggerPassives.get(triggerType).put(new SlotPassive(eSlot, abilityType), new PassiveAbility(onCooldownPassives, eSlot, triggerType, abilityType, dataContainer.get(passiveKey, PersistentDataType.STRING).split(" ")));
  }

  /**
   * Removes existing {@link Equipment} {@link PassiveAbility passive abilities} at an {@link RpgEquipmentSlot}.
   *
   * @param eSlot {@link RpgEquipmentSlot}
   */
  public void removePassives(@NotNull RpgEquipmentSlot eSlot) {
    List<TriggerPassive> abilitiesToRemove = new ArrayList<>();
    for (TriggerPassive triggerPassive : slotPassives.get(Objects.requireNonNull(eSlot, "Null slot"))) {
      triggerPassives.get(triggerPassive.triggerType()).remove(new SlotPassive(eSlot, triggerPassive.type()));
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
   * Gets the player's {@link TriggerType triggerable} {@link PassiveAbility passive abilities}.
   *
   * @return {@link TriggerType triggerable} {@link PassiveAbility passive abilities}
   */
  @NotNull
  public Map<TriggerType, Map<SlotPassive, PassiveAbility>> getTriggerPassives() {
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
}
