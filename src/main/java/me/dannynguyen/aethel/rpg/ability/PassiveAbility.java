package me.dannynguyen.aethel.rpg.ability;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.rpg.*;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveEffect;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveType;
import me.dannynguyen.aethel.enums.rpg.abilities.TriggerType;
import me.dannynguyen.aethel.enums.rpg.abilities.TriggerCondition;
import me.dannynguyen.aethel.rpg.system.PlayerDamageMitigation;
import me.dannynguyen.aethel.rpg.system.RpgPlayer;
import me.dannynguyen.aethel.rpg.system.Status;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents an item's {@link PassiveType}.
 *
 * @author Danny Nguyen
 * @version 1.17.12
 * @since 1.16.2
 */
public class PassiveAbility {
  /**
   * {@link PassiveType Passive abilities} on cooldown.
   */
  private final Map<TriggerType, Set<SlotPassive>> onCooldownPassives;

  /**
   * {@link RpgEquipmentSlot}
   */
  private final RpgEquipmentSlot eSlot;

  /**
   * {@link TriggerType}
   */
  private final TriggerType triggerType;

  /**
   * {@link PassiveType}
   */
  private final PassiveType type;

  /**
   * {@link TriggerCondition} data.
   */
  private final List<String> conditionData = new ArrayList<>();

  /**
   * {@link PassiveEffect} data.
   */
  private final List<String> effectData = new ArrayList<>();

  /**
   * Associates a {@link PassiveType passive ability} with its data.
   *
   * @param onCooldownPassives {@link PassiveType} on cooldown
   * @param eSlot              {@link RpgEquipmentSlot}
   * @param triggerType            {@link TriggerType}
   * @param type               {@link PassiveType}
   * @param dataValues         ability data
   */
  public PassiveAbility(@NotNull Map<TriggerType, Set<SlotPassive>> onCooldownPassives, @NotNull RpgEquipmentSlot eSlot, @NotNull TriggerType triggerType, @NotNull PassiveType type, @NotNull String[] dataValues) {
    this.onCooldownPassives = Objects.requireNonNull(onCooldownPassives, "Null on cooldown passives");
    this.eSlot = Objects.requireNonNull(eSlot, "Null slot");
    this.triggerType = Objects.requireNonNull(triggerType, "Null trigger");
    this.type = Objects.requireNonNull(type, "Null ability");
    initializeAbilityData(triggerType.getCondition(), type.getEffect(), dataValues);
  }

  /**
   * Initializes the {@link PassiveType passive ability's}
   * {@link TriggerCondition} and {@link PassiveEffect} data.
   *
   * @param condition  {@link TriggerCondition}
   * @param effect     {@link PassiveEffect}
   * @param dataValues ability data
   */
  private void initializeAbilityData(TriggerCondition condition, PassiveEffect effect, String[] dataValues) {
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
        }
      }
    }
  }

  /**
   * Triggers the {@link PassiveEffect}.
   *
   * @param targetUUID target UUID
   */
  public void doEffect(UUID targetUUID) {
    switch (type.getEffect()) {
      case STACK_INSTANCE -> applyStackInstance(targetUUID);
      case CHAIN_DAMAGE -> chainDamage(targetUUID);
    }
  }

  /**
   * Applies {@link PassiveEffect#STACK_INSTANCE}.
   *
   * @param targetUUID entity to receive {@link PassiveEffect#STACK_INSTANCE}
   */
  private void applyStackInstance(UUID targetUUID) {
    Map<UUID, Map<StatusType, Status>> entityStatuses = Plugin.getData().getRpgSystem().getStatuses();
    Map<StatusType, Status> statuses;
    if (!entityStatuses.containsKey(targetUUID)) {
      entityStatuses.put(targetUUID, new HashMap<>());
    }
    statuses = entityStatuses.get(targetUUID);

    StatusType statusType = StatusType.valueOf(type.toString());
    int stacks = Integer.parseInt(effectData.get(1));
    int ticks = Integer.parseInt(effectData.get(2));
    if (statuses.containsKey(statusType)) {
      statuses.get(statusType).addStacks(stacks, ticks);
    } else {
      statuses.put(statusType, new Status(targetUUID, statusType, stacks, ticks));
    }

    int cooldown = Integer.parseInt(conditionData.get(1));
    if (cooldown > 0) {
      setOnCooldown(true);
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> setOnCooldown(false), cooldown);
    }
  }

  /**
   * {@link PassiveEffect#CHAIN_DAMAGE} between entities.
   *
   * @param targetUUID {@link PassiveEffect#CHAIN_DAMAGE} source
   */
  private void chainDamage(UUID targetUUID) {
    Map<UUID, Map<StatusType, Status>> entityStatuses = Plugin.getData().getRpgSystem().getStatuses();

    double chainDamage = Double.parseDouble(effectData.get(1));
    double meters = Double.parseDouble(effectData.get(2));

    Map<LivingEntity, Integer> soakedTargets = new HashMap<>();
    getSoakedTargets(entityStatuses, soakedTargets, targetUUID, meters);

    for (LivingEntity livingEntity : soakedTargets.keySet()) {
      Map<StatusType, Status> statuses = entityStatuses.get(livingEntity.getUniqueId());
      if (livingEntity instanceof Player player) {
        if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
          RpgPlayer rpgPlayer = Plugin.getData().getRpgSystem().getRpgPlayers().get(livingEntity.getUniqueId());
          double damage = chainDamage * (1 + statuses.get(StatusType.SOAKED).getStackAmount() / 50.0);
          player.damage(0.1);
          rpgPlayer.getHealth().damage(new PlayerDamageMitigation(player).mitigateProtectionResistance(damage));
        }
      } else {
        livingEntity.damage(0.1);
        livingEntity.setHealth(Math.max(0, livingEntity.getHealth() + 0.1 - chainDamage * (1 + statuses.get(StatusType.SOAKED).getStackAmount() / 50.0)));
      }
    }

    int cooldown = Integer.parseInt(conditionData.get(1));
    if (cooldown > 0) {
      setOnCooldown(true);
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
  private void getSoakedTargets(Map<UUID, Map<StatusType, Status>> entityStatuses, Map<LivingEntity, Integer> soakedTargets, UUID targetUUID, Double meters) {
    List<LivingEntity> newSoakedTargets = new ArrayList<>();
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
   * Gets the {@link RpgEquipmentSlot}.
   *
   * @return {@link RpgEquipmentSlot}
   */
  @NotNull
  public RpgEquipmentSlot getSlot() {
    return this.eSlot;
  }

  /**
   * Gets the {@link TriggerType}.
   *
   * @return {@link TriggerType}
   */
  @NotNull
  public TriggerType getTrigger() {
    return this.triggerType;
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
   * Gets the {@link TriggerCondition} data.
   *
   * @return {@link TriggerCondition} data.
   */
  @NotNull
  public List<String> getConditionData() {
    return this.conditionData;
  }

  /**
   * Gets the {@link PassiveEffect} data.
   *
   * @return {@link PassiveEffect} data
   */
  @NotNull
  public List<String> getEffectData() {
    return this.effectData;
  }

  /**
   * Gets if the {@link PassiveType} is on cooldown.
   *
   * @return if the {@link PassiveType} is on cooldown
   */
  public boolean isOnCooldown() {
    return onCooldownPassives.get(triggerType).contains(new SlotPassive(eSlot, type));
  }

  /**
   * Sets if the {@link PassiveType} is on cooldown.
   *
   * @param isOnCooldown is on cooldown
   */
  public void setOnCooldown(boolean isOnCooldown) {
    SlotPassive slotPassive = new SlotPassive(eSlot, type);
    if (isOnCooldown) {
      onCooldownPassives.get(triggerType).add(slotPassive);
    } else {
      onCooldownPassives.get(triggerType).remove(slotPassive);
    }
  }
}
