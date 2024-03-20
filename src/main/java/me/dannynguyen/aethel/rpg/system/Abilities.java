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
 * Represents an {@link RpgPlayer} abilities.
 *
 * @author Danny Nguyen
 * @version 1.17.9
 * @since 1.17.9
 */
public class Abilities {
  /**
   * {@link PassiveAbilityType Passive abilities} by {@link RpgEquipmentSlot}.
   */
  private final Map<RpgEquipmentSlot, List<TriggerPassiveType>> slotPassives = new HashMap<>();

  /**
   * {@link PassiveAbility Passive abilities} by {@link Trigger trigger}.
   */
  private final Map<Trigger, Map<SlotPassiveType, PassiveAbility>> triggerPassives = createBlankPassiveTriggers();

  /**
   * {@link PassiveAbility Passive abilities} on cooldown.
   */
  private final Map<Trigger, Set<SlotPassiveType>> onCooldownPassives = createBlankPassiveCooldownTriggers();

  /**
   * {@link ActiveAbility Active abilities} by {@link Trigger trigger}.
   */
  private final Map<RpgEquipmentSlot, List<ActiveAbility>> triggerActives = new HashMap<>();

  /**
   * {@link ActiveAbility Active abilities} on cooldown.
   */
  private final Map<RpgEquipmentSlot, Set<ActiveAbilityType>> onCooldownActives = new HashMap<>();

  /**
   * No parameter constructor.
   */
  public Abilities() {
  }

  /**
   * Creates a blank map of {@link PassiveAbilityType passive ability} {@link Trigger triggers}.
   *
   * @return blank {@link PassiveAbilityType passive ability} {@link Trigger triggers}.
   */
  private Map<Trigger, Map<SlotPassiveType, PassiveAbility>> createBlankPassiveTriggers() {
    Map<Trigger, Map<SlotPassiveType, PassiveAbility>> triggers = new HashMap<>();
    for (Trigger trigger : Trigger.values()) {
      triggers.put(trigger, new HashMap<>());
    }
    return triggers;
  }

  /**
   * Creates a blank map of {@link PassiveAbilityType passive ability} cooldown {@link Trigger triggers}.
   *
   * @return blank {@link PassiveAbilityType passive ability} cooldown {@link Trigger triggers}.
   */
  private Map<Trigger, Set<SlotPassiveType>> createBlankPassiveCooldownTriggers() {
    Map<Trigger, Set<SlotPassiveType>> triggers = new HashMap<>();
    for (Trigger trigger : Trigger.values()) {
      triggers.put(trigger, new HashSet<>());
    }
    return triggers;
  }

  /**
   * Checks if the item is in the correct {@link RpgEquipmentSlot equipment slot}
   * before updating the player's {@link PassiveAbility passive abilities}.
   *
   * @param eSlot         {@link RpgEquipmentSlot equipment slot}
   * @param dataContainer item's persistent tags
   */
  public void readPassives(RpgEquipmentSlot eSlot, PersistentDataContainer dataContainer) {
    String[] passives = dataContainer.get(PluginNamespacedKey.PASSIVE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ");
    for (String passive : passives) {
      RpgEquipmentSlot slot = RpgEquipmentSlot.valueOf(passive.substring(0, passive.indexOf(".")).toUpperCase());
      if (slot == eSlot) {
        addPassives(eSlot, dataContainer, passive);
      }
    }
  }

  /**
   * Adds new {@link PassiveAbility passive abilities}.
   *
   * @param eSlot         {@link RpgEquipmentSlot equipment slot}
   * @param dataContainer item's persistent tags
   * @param passive       {@link PassiveAbility ability} data
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
   * Removes existing {@link PassiveAbility passive abilities} at an {@link RpgEquipmentSlot equipment slot}.
   *
   * @param eSlot {@link RpgEquipmentSlot equipment slot}
   */
  public void removePassives(@NotNull RpgEquipmentSlot eSlot) {
    List<TriggerPassiveType> abilitiesToRemove = new ArrayList<>();
    for (TriggerPassiveType triggerPassiveType : slotPassives.get(Objects.requireNonNull(eSlot, "Null slot"))) {
      triggerPassives.get(triggerPassiveType.getTrigger()).remove(new SlotPassiveType(eSlot, triggerPassiveType.getType()));
      abilitiesToRemove.add(triggerPassiveType);
    }
    slotPassives.get(eSlot).removeAll(abilitiesToRemove);
  }

  /**
   * Gets the player's {@link PassiveAbility passive abilities} by {@link RpgEquipmentSlot slot}.
   *
   * @return {@link PassiveAbility passive abilities} by {@link RpgEquipmentSlot slot}
   */
  @NotNull
  public Map<RpgEquipmentSlot, List<TriggerPassiveType>> getSlotPassives() {
    return this.slotPassives;
  }

  /**
   * Gets the player's {@link PassiveAbility passive abilities} by {@link Trigger trigger}.
   *
   * @return {@link PassiveAbility passive abilities} by {@link Trigger trigger}
   */
  @NotNull
  public Map<Trigger, Map<SlotPassiveType, PassiveAbility>> getTriggerPassives() {
    return this.triggerPassives;
  }

  /**
   * Gets the player's {@link ActiveAbility active abilities} by {@link RpgEquipmentSlot slot}.
   *
   * @return {@link ActiveAbility active abilities} by {@link RpgEquipmentSlot slot}
   */
  @NotNull
  public Map<RpgEquipmentSlot, List<ActiveAbility>> getTriggerActives() {
    return this.triggerActives;
  }
}
