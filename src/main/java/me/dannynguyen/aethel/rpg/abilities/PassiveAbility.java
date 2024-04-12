package me.dannynguyen.aethel.rpg.abilities;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.rpg.AethelAttribute;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.enums.rpg.StatusType;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType;
import me.dannynguyen.aethel.rpg.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents an item's {@link PassiveAbilityType}.
 *
 * @author Danny Nguyen
 * @version 1.22.18
 * @since 1.16.2
 */
public class PassiveAbility {
  /**
   * {@link PassiveAbilityType Passive abilities} on cooldown.
   */
  private final Map<PassiveTriggerType, Set<Equipment.Abilities.SlotPassive>> onCooldownPassives;

  /**
   * {@link RpgEquipmentSlot}
   */
  private final RpgEquipmentSlot eSlot;

  /**
   * {@link PassiveTriggerType}
   */
  private final PassiveTriggerType trigger;

  /**
   * {@link PassiveAbilityType}
   */
  private final PassiveAbilityType type;

  /**
   * {@link PassiveTriggerType.Condition} data.
   */
  private final List<String> conditionData = new ArrayList<>();

  /**
   * {@link PassiveAbilityType.Effect} data.
   */
  private final List<String> effectData = new ArrayList<>();

  /**
   * Associates a {@link PassiveAbilityType passive ability} with its data.
   *
   * @param onCooldownPassives {@link PassiveAbilityType} on cooldown
   * @param eSlot              {@link RpgEquipmentSlot}
   * @param trigger            {@link PassiveTriggerType}
   * @param type               {@link PassiveAbilityType}
   * @param dataValues         ability data
   */
  public PassiveAbility(@NotNull Map<PassiveTriggerType, Set<Equipment.Abilities.SlotPassive>> onCooldownPassives, @NotNull RpgEquipmentSlot eSlot, @NotNull PassiveTriggerType trigger, @NotNull PassiveAbilityType type, @NotNull String[] dataValues) {
    this.onCooldownPassives = Objects.requireNonNull(onCooldownPassives, "Null on cooldown passives");
    this.eSlot = Objects.requireNonNull(eSlot, "Null slot");
    this.trigger = Objects.requireNonNull(trigger, "Null trigger");
    this.type = Objects.requireNonNull(type, "Null ability");
    loadAbilityData(trigger.getCondition(), type.getEffect(), dataValues);
  }

  /**
   * Loads the {@link PassiveAbilityType passive ability's}
   * {@link PassiveTriggerType.Condition} and {@link PassiveAbilityType.Effect} data.
   *
   * @param condition  {@link PassiveTriggerType.Condition}
   * @param effect     {@link PassiveAbilityType.Effect}
   * @param dataValues ability data
   */
  private void loadAbilityData(PassiveTriggerType.Condition condition, PassiveAbilityType.Effect effect, String[] dataValues) {
    switch (condition) {
      case CHANCE_COOLDOWN, HEALTH_COOLDOWN -> {
        conditionData.add(dataValues[0]);
        conditionData.add(dataValues[1]);
        switch (effect) {
          case STACK_INSTANCE, CHAIN_DAMAGE -> {
            effectData.add(dataValues[2]);
            effectData.add(dataValues[3]);
            effectData.add(dataValues[4]);
          }
          case BUFF -> {
            effectData.add(dataValues[2]);
            effectData.add(dataValues[3]);
            effectData.add(dataValues[4]);
            effectData.add(dataValues[5]);
          }
          case POTION_EFFECT -> {
            effectData.add(dataValues[2]);
            effectData.add(dataValues[3]);
            effectData.add(dataValues[4]);
            effectData.add(dataValues[5]);
            effectData.add(dataValues[6]);
          }
        }
      }
    }
  }

  /**
   * Triggers the {@link PassiveAbilityType.Effect}.
   *
   * @param casterUUID ability caster UUID
   * @param targetUUID target UUID
   */
  public void doEffect(@NotNull UUID casterUUID, @NotNull UUID targetUUID) {
    Objects.requireNonNull(casterUUID, "Null caster UUID");
    Objects.requireNonNull(targetUUID, "Null target UUID");
    PersistentDataContainer entityTags = Bukkit.getPlayer(casterUUID).getPersistentDataContainer();
    Buffs buffs = Plugin.getData().getRpgSystem().getBuffs().get(casterUUID);

    double itemCooldownBase = entityTags.getOrDefault(Key.ATTRIBUTE_ITEM_COOLDOWN.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
    double cooldownModifierBuff = 0.0;
    if (buffs != null) {
      cooldownModifierBuff = buffs.getAethelAttribute(AethelAttribute.ITEM_COOLDOWN);
    }
    double cooldownModifier = (itemCooldownBase + cooldownModifierBuff) / 100;

    switch (type.getEffect()) {
      case BUFF -> applyBuff(cooldownModifier, targetUUID);
      case STACK_INSTANCE -> applyStackInstance(cooldownModifier, targetUUID);
      case CHAIN_DAMAGE -> chainDamage(cooldownModifier, targetUUID);
      case POTION_EFFECT -> applyPotionEffect(cooldownModifier, targetUUID);
    }
  }

  /**
   * Applies {@link PassiveAbilityType.Effect#BUFF}.
   *
   * @param cooldownModifier cooldown modifier
   * @param targetUUID       entity to receive {@link PassiveAbilityType.Effect#BUFF}
   */
  private void applyBuff(double cooldownModifier, UUID targetUUID) {
    if (!(Bukkit.getEntity(targetUUID) instanceof LivingEntity)) {
      return;
    }

    Map<UUID, Buffs> entityBuffs = Plugin.getData().getRpgSystem().getBuffs();
    if (entityBuffs.get(targetUUID) == null) {
      entityBuffs.put(targetUUID, new Buffs(targetUUID));
    }
    Buffs buffs = entityBuffs.get(targetUUID);
    double value = Double.parseDouble(effectData.get(2));
    int duration = Integer.parseInt(effectData.get(3));

    try {
      Attribute attribute = Attribute.valueOf(effectData.get(1).toUpperCase());
      buffs.addAttribute(attribute, value, duration);
    } catch (IllegalArgumentException ex) {
      try {
        AethelAttribute aethelAttribute = AethelAttribute.valueOf(effectData.get(1).toUpperCase());
        buffs.addAethelAttribute(aethelAttribute, value, duration);
      } catch (IllegalArgumentException ignored) {
      }
    }

    int cooldown = Integer.parseInt(conditionData.get(1));
    if (cooldown > 0) {
      setOnCooldown(true);
      cooldown = (int) Math.max(1, cooldown - (cooldown * cooldownModifier));
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> setOnCooldown(false), cooldown);
    }
  }

  /**
   * Applies {@link PassiveAbilityType.Effect#STACK_INSTANCE}.
   *
   * @param cooldownModifier cooldown modifier
   * @param targetUUID       entity to receive {@link PassiveAbilityType.Effect#STACK_INSTANCE}
   */
  private void applyStackInstance(double cooldownModifier, UUID targetUUID) {
    Map<UUID, Map<StatusType, Status>> entityStatuses = Plugin.getData().getRpgSystem().getStatuses();
    Map<StatusType, Status> statuses;
    if (!entityStatuses.containsKey(targetUUID)) {
      entityStatuses.put(targetUUID, new HashMap<>());
    }
    statuses = entityStatuses.get(targetUUID);

    StatusType statusType = StatusType.valueOf(type.toString());
    int stacks = Integer.parseInt(effectData.get(1));
    int ticks = Integer.parseInt(effectData.get(2));

    Entity entity = Bukkit.getEntity(targetUUID);
    PersistentDataContainer entityTags = entity.getPersistentDataContainer();
    Buffs buffs = Plugin.getData().getRpgSystem().getBuffs().get(targetUUID);

    double tenacityBase = entityTags.getOrDefault(Key.ATTRIBUTE_TENACITY.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
    double tenacityBuff = 0.0;
    if (buffs != null) {
      tenacityBuff = buffs.getAethelAttribute(AethelAttribute.TENACITY);
    }

    ticks = (int) Math.max(1, ticks - (ticks * (tenacityBase + tenacityBuff) / 100));

    if (statuses.containsKey(statusType)) {
      statuses.get(statusType).addStacks(stacks, ticks);
    } else {
      statuses.put(statusType, new Status(targetUUID, statusType, stacks, ticks));
    }

    int cooldown = Integer.parseInt(conditionData.get(1));
    if (cooldown > 0) {
      setOnCooldown(true);
      cooldown = (int) Math.max(1, cooldown - (cooldown * cooldownModifier));
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> setOnCooldown(false), cooldown);
    }
  }

  /**
   * {@link PassiveAbilityType.Effect#CHAIN_DAMAGE} between entities.
   *
   * @param cooldownModifier cooldown modifier
   * @param targetUUID       {@link PassiveAbilityType.Effect#CHAIN_DAMAGE} source
   */
  private void chainDamage(double cooldownModifier, UUID targetUUID) {
    Map<UUID, Map<StatusType, Status>> entityStatuses = Plugin.getData().getRpgSystem().getStatuses();

    double chainDamage = Double.parseDouble(effectData.get(1));
    double meters = Double.parseDouble(effectData.get(2));

    Map<LivingEntity, Integer> soakedTargets = new HashMap<>();
    getSoakedTargets(entityStatuses, soakedTargets, targetUUID, meters);

    for (LivingEntity livingEntity : soakedTargets.keySet()) {
      Map<StatusType, Status> statuses = entityStatuses.get(livingEntity.getUniqueId());
      double damage = chainDamage * (1 + statuses.get(StatusType.SOAKED).getStackAmount() / 50.0);
      final double finalDamage = new DamageMitigation(livingEntity).mitigateProtectionResistance(damage);

      if (livingEntity instanceof Player player && (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE)) {
        Health health = Plugin.getData().getRpgSystem().getRpgPlayers().get(livingEntity.getUniqueId()).getHealth();
        player.damage(0.1);
        health.damage(finalDamage);
      } else {
        livingEntity.damage(0.1);
        livingEntity.setHealth(Math.max(0, livingEntity.getHealth() + 0.1 - (finalDamage)));
      }
    }

    int cooldown = Integer.parseInt(conditionData.get(1));
    if (cooldown > 0) {
      setOnCooldown(true);
      cooldown = (int) Math.max(1, cooldown - (cooldown * cooldownModifier));
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> setOnCooldown(false), cooldown);
    }
  }

  /**
   * Applies a potion effect.
   *
   * @param cooldownModifier cooldown modifier
   * @param targetUUID       entity to receive potion effects
   */
  private void applyPotionEffect(double cooldownModifier, UUID targetUUID) {
    PotionEffectType potionEffectType = PotionEffectType.getByName(effectData.get(1));
    int amplifier = Integer.parseInt(effectData.get(2));
    int duration = Integer.parseInt(effectData.get(3));
    boolean ambient = Boolean.parseBoolean(effectData.get(4));
    LivingEntity target = (LivingEntity) Bukkit.getEntity(targetUUID);

    target.addPotionEffect(new PotionEffect(potionEffectType, duration, amplifier, ambient));

    int cooldown = Integer.parseInt(conditionData.get(1));
    if (cooldown > 0) {
      setOnCooldown(true);
      cooldown = (int) Math.max(1, cooldown - (cooldown * cooldownModifier));
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> setOnCooldown(false), cooldown);
    }
  }

  /**
   * Recursively finds new {@link StatusType#SOAKED} targets around the source entity.
   *
   * @param entityStatuses entity {@link Status statuses}
   * @param soakedTargets  {@link StatusType#SOAKED} targets
   * @param targetUUID     source entity
   * @param meters         distance
   */
  private void getSoakedTargets(Map<UUID, Map<StatusType, Status>> entityStatuses, Map<LivingEntity, Integer> soakedTargets, UUID targetUUID, double meters) {
    Set<LivingEntity> newSoakedTargets = new HashSet<>();
    for (Entity entity : Bukkit.getEntity(targetUUID).getNearbyEntities(meters, meters, meters)) {
      if (entity instanceof LivingEntity livingEntity) {
        UUID livingEntityUUID = livingEntity.getUniqueId();
        if (entityStatuses.containsKey(livingEntityUUID) && entityStatuses.get(livingEntityUUID).containsKey(StatusType.SOAKED)) {
          if (!soakedTargets.containsKey(livingEntity)) {
            newSoakedTargets.add(livingEntity);
            soakedTargets.put(livingEntity, entityStatuses.get(livingEntityUUID).get(StatusType.SOAKED).getStackAmount());
          }
        }
      }
    }
    if (newSoakedTargets.isEmpty()) {
      return;
    }
    for (LivingEntity livingEntity : newSoakedTargets) {
      getSoakedTargets(entityStatuses, soakedTargets, livingEntity.getUniqueId(), meters);
    }
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
   * Gets the {@link PassiveTriggerType.Condition} data.
   *
   * @return {@link PassiveTriggerType.Condition} data.
   */
  @NotNull
  public List<String> getConditionData() {
    return this.conditionData;
  }

  /**
   * Gets the {@link PassiveAbilityType.Effect} data.
   *
   * @return {@link PassiveAbilityType.Effect} data
   */
  @NotNull
  public List<String> getEffectData() {
    return this.effectData;
  }

  /**
   * Gets if the {@link PassiveAbilityType} is on cooldown.
   *
   * @return if the {@link PassiveAbilityType} is on cooldown
   */
  public boolean isOnCooldown() {
    return onCooldownPassives.get(trigger).contains(new Equipment.Abilities.SlotPassive(eSlot, type));
  }

  /**
   * Sets if the {@link PassiveAbilityType} is on cooldown.
   *
   * @param isOnCooldown is on cooldown
   */
  private void setOnCooldown(boolean isOnCooldown) {
    Equipment.Abilities.SlotPassive slotPassive = new Equipment.Abilities.SlotPassive(eSlot, type);
    if (isOnCooldown) {
      onCooldownPassives.get(trigger).add(slotPassive);
    } else {
      onCooldownPassives.get(trigger).remove(slotPassive);
    }
  }
}
