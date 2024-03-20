package me.dannynguyen.aethel.rpg.system;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.plugin.enums.KeyHeader;
import me.dannynguyen.aethel.plugin.enums.PluginNamespacedKey;
import me.dannynguyen.aethel.rpg.ability.ActiveAbility;
import me.dannynguyen.aethel.rpg.ability.PassiveAbility;
import me.dannynguyen.aethel.rpg.ability.SlotPassiveType;
import me.dannynguyen.aethel.rpg.ability.TriggerPassiveType;
import me.dannynguyen.aethel.rpg.enums.ActiveAbilityType;
import me.dannynguyen.aethel.rpg.enums.PassiveAbilityType;
import me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot;
import me.dannynguyen.aethel.rpg.enums.Trigger;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents an RPG player's abilities.
 *
 * @author Danny Nguyen
 * @version 1.17.9
 * @since 1.17.9
 */
public class Abilities {
  /**
   * Passive abilities by slot.
   */
  private final Map<RpgEquipmentSlot, List<TriggerPassiveType>> slotPassives = new HashMap<>();

  /**
   * Passive abilities by trigger method.
   */
  private final Map<Trigger, Map<SlotPassiveType, PassiveAbility>> triggerPassives = createBlankPassiveTriggers();

  /**
   * Passive abilities on cooldown.
   */
  private final Map<Trigger, Set<SlotPassiveType>> onCooldownPassives = createBlankPassiveCooldownTriggers();

  /**
   * Active abilities by trigger method.
   */
  private final Map<RpgEquipmentSlot, List<ActiveAbility>> triggerActives = new HashMap<>();

  /**
   * Active abilities on cooldown.
   */
  private final Map<RpgEquipmentSlot, Set<ActiveAbilityType>> onCooldownActives = new HashMap<>();

  /**
   * Associates RPG abilities together.
   */
  public Abilities() {
  }

  /**
   * Creates a blank map of ability triggers.
   *
   * @return blank ability triggers
   */
  private Map<Trigger, Map<SlotPassiveType, PassiveAbility>> createBlankPassiveTriggers() {
    Map<Trigger, Map<SlotPassiveType, PassiveAbility>> triggers = new HashMap<>();
    for (Trigger trigger : Trigger.values()) {
      triggers.put(trigger, new HashMap<>());
    }
    return triggers;
  }

  /**
   * Creates a blank map of ability cooldown triggers.
   *
   * @return blank ability cooldown triggers
   */
  private Map<Trigger, Set<SlotPassiveType>> createBlankPassiveCooldownTriggers() {
    Map<Trigger, Set<SlotPassiveType>> triggers = new HashMap<>();
    for (Trigger trigger : Trigger.values()) {
      triggers.put(trigger, new HashSet<>());
    }
    return triggers;
  }

  /**
   * Checks if the item is in the correct equipment slot before updating the player's passive abilities.
   *
   * @param eSlot         slot type
   * @param dataContainer item's persistent tags
   */
  public void readPassives(RpgEquipmentSlot eSlot, PersistentDataContainer dataContainer) {
    String[] passives = dataContainer.get(PluginNamespacedKey.PASSIVE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ");
    for (String passive : passives) {
      RpgEquipmentSlot equipmentSlot = RpgEquipmentSlot.valueOf(passive.substring(0, passive.indexOf(".")).toUpperCase());
      if (equipmentSlot == eSlot) {
        addPassives(eSlot, dataContainer, passive);
      }
    }
  }

  /**
   * Adds new passive abilities.
   *
   * @param eSlot         slot type
   * @param dataContainer item's persistent tags
   * @param passive       ability data
   */
  private void addPassives(RpgEquipmentSlot eSlot, PersistentDataContainer dataContainer, String passive) {
    NamespacedKey passiveKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.PASSIVE.getHeader() + passive);
    String[] abilityMeta = passive.split("\\.");
    Trigger trigger = Trigger.valueOf(abilityMeta[1].toUpperCase());
    PassiveAbilityType abilityType = PassiveAbilityType.valueOf(abilityMeta[2].toUpperCase());
    slotPassives.get(eSlot).add(new TriggerPassiveType(trigger, abilityType));
    triggerPassives.get(trigger).put(new SlotPassiveType(eSlot, abilityType), new PassiveAbility(onCooldownPassives, eSlot, trigger, abilityType, dataContainer.get(passiveKey, PersistentDataType.STRING).split(" ")));
  }

  /**
   * Removes existing passive abilities at an equipment slot.
   *
   * @param eSlot equipment slot
   */
  public void removePassives(@NotNull RpgEquipmentSlot eSlot) {
    List<TriggerPassiveType> abilitiesToRemove = new ArrayList<>();
    for (TriggerPassiveType triggerPassiveType : slotPassives.get(Objects.requireNonNull(eSlot, "Null slot"))) {
      triggerPassives.get(triggerPassiveType.getTrigger()).remove(new SlotPassiveType(eSlot, triggerPassiveType.getAbilityType()));
      abilitiesToRemove.add(triggerPassiveType);
    }
    slotPassives.get(eSlot).removeAll(abilitiesToRemove);
  }

  /**
   * Gets the player's passive abilities by slot.
   *
   * @return passive abilities by slot
   */
  @NotNull
  public Map<RpgEquipmentSlot, List<TriggerPassiveType>> getSlotPassives() {
    return this.slotPassives;
  }

  /**
   * Gets the player's passive abilities by trigger.
   *
   * @return passive abilities by trigger
   */
  @NotNull
  public Map<Trigger, Map<SlotPassiveType, PassiveAbility>> getTriggerPassives() {
    return this.triggerPassives;
  }

  /**
   * Gets the player's active ability triggers by slot.
   *
   * @return active abilities
   */
  @NotNull
  public Map<RpgEquipmentSlot, List<ActiveAbility>> getTriggerActives() {
    return this.triggerActives;
  }
}
