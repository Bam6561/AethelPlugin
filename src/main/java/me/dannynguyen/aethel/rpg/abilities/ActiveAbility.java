package me.dannynguyen.aethel.rpg.abilities;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.enums.rpg.StatusType;
import me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType;
import me.dannynguyen.aethel.rpg.DamageMitigation;
import me.dannynguyen.aethel.rpg.RpgPlayer;
import me.dannynguyen.aethel.rpg.Status;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents an item's {@link ActiveAbilityType}.
 *
 * @author Danny Nguyen
 * @version 1.19.4
 * @since 1.17.4
 */
public class ActiveAbility {
  /**
   * {@link ActiveAbilityType Active abilities} on cooldown.
   */
  private final Map<RpgEquipmentSlot, Set<ActiveAbilityType>> onCooldownActives;

  /**
   * {@link RpgEquipmentSlot}
   */
  private final RpgEquipmentSlot eSlot;

  /**
   * {@link ActiveAbilityType}
   */
  private final ActiveAbilityType type;

  /**
   * Ability cooldown in ticks.
   */
  private final int cooldown;

  /**
   * {@link ActiveAbilityType.Effect} data.
   */
  private final List<String> effectData = new ArrayList<>();

  /**
   * Associates an {@link ActiveAbilityType active ability} with its data.
   *
   * @param onCooldownActives {@link ActiveAbilityType} on cooldown
   * @param eSlot             {@link RpgEquipmentSlot}
   * @param type              {@link ActiveAbilityType}
   * @param dataValues        ability data
   */
  public ActiveAbility(@NotNull Map<RpgEquipmentSlot, Set<ActiveAbilityType>> onCooldownActives, @NotNull RpgEquipmentSlot eSlot, @NotNull ActiveAbilityType type, @NotNull String[] dataValues) {
    this.onCooldownActives = Objects.requireNonNull(onCooldownActives, "Null on cooldown actives");
    this.eSlot = Objects.requireNonNull(eSlot, "Null slot");
    this.type = Objects.requireNonNull(type, "Null ability");
    this.cooldown = Integer.parseInt(dataValues[0]);
    initializeAbilityData(type.getEffect(), dataValues);
  }

  /**
   * Initializes the {@link ActiveAbilityType active ability's} ability data.
   *
   * @param effect     {@link ActiveAbilityType.Effect}
   * @param dataValues ability data
   */
  private void initializeAbilityData(ActiveAbilityType.Effect effect, String[] dataValues) {
    switch (effect) {
      case MOVEMENT, SHATTER, TELEPORT -> effectData.add(dataValues[1]);
      case PROJECTION -> {
        effectData.add(dataValues[1]);
        effectData.add(dataValues[2]);
      }
    }
  }

  /**
   * Triggers the {@link ActiveAbilityType.Effect}.
   *
   * @param caster ability caster
   */
  public void doEffect(@NotNull Player caster) {
    Objects.requireNonNull(caster, "Null caster");
    switch (type.getEffect()) {
      case CLEAR_STATUS -> clearStatus(caster);
      case MOVEMENT -> moveDistance(caster);
      case PROJECTION -> projectDistance(caster);
      case SHATTER -> shatterBrittle(caster);
      case TELEPORT -> teleportDistance(caster);
    }
  }

  /**
   * Performs {@link ActiveAbilityType.Effect#CLEAR_STATUS} for {@link Status} based
   * on the {@link StatusType.Type}.
   *
   * @param caster ability caster
   */
  private void clearStatus(Player caster) {
    Map<UUID, Map<StatusType, Status>> entityStatuses = Plugin.getData().getRpgSystem().getStatuses();
    UUID uuid = caster.getUniqueId();
    if (entityStatuses.containsKey(uuid)) {
      Map<StatusType, Status> statuses = entityStatuses.get(uuid);
      switch (type) {
        case DISMISS -> {
          for (StatusType type : StatusType.Type.NON_DAMAGE.getStatusTypes()) {
            statuses.remove(type);
          }
        }
        case DISREGARD -> {
          for (StatusType type : StatusType.Type.DAMAGE.getStatusTypes()) {
            statuses.remove(type);
          }
        }
      }
    }
    if (cooldown > 0) {
      setOnCooldown(true);
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> setOnCooldown(false), cooldown);
    }
  }

  /**
   * Performs {@link ActiveAbilityType.Effect#MOVEMENT} across a distance.
   *
   * @param caster ability caster
   */
  private void moveDistance(Player caster) {
    double multiplier = 0.65 + (0.65 * (Double.parseDouble(effectData.get(0)) / 100)) + caster.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue();
    Vector vector = caster.getLocation().getDirection().multiply(multiplier);
    caster.setVelocity(vector.setY(0.2));
    if (cooldown > 0) {
      setOnCooldown(true);
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> setOnCooldown(false), cooldown);
    }
  }

  /**
   * Performs {@link ActiveAbilityType.Effect#PROJECTION} across a distance.
   *
   * @param caster ability caster
   */
  private void projectDistance(Player caster) {
    final Location castOrigin = caster.getLocation().clone();
    Location origin = caster.getLocation();
    caster.teleport(ifValidTeleportThroughBlock(origin, origin.getDirection(), origin, Integer.parseInt(effectData.get(0))));
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> caster.teleport(castOrigin), Integer.parseInt(effectData.get(1)));
    if (cooldown > 0) {
      setOnCooldown(true);
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> setOnCooldown(false), cooldown);
    }
  }

  /**
   * Consumes {@link me.dannynguyen.aethel.enums.rpg.StatusType#BRITTLE}
   * stacks on entities.
   *
   * @param caster ability caster
   */
  private void shatterBrittle(Player caster) {
    Map<UUID, Map<StatusType, Status>> entityStatuses = Plugin.getData().getRpgSystem().getStatuses();
    double meters = Double.parseDouble(effectData.get(0));

    for (Entity entity : caster.getNearbyEntities(meters, meters, meters)) {
      if (entity instanceof LivingEntity livingEntity) {
        UUID livingEntityUUID = livingEntity.getUniqueId();
        if (entityStatuses.containsKey(livingEntityUUID) && entityStatuses.get(livingEntityUUID).containsKey(StatusType.BRITTLE)) {
          Map<StatusType, Status> statuses = entityStatuses.get(livingEntity.getUniqueId());
          if (livingEntity instanceof Player player) {
            if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
              RpgPlayer rpgPlayer = Plugin.getData().getRpgSystem().getRpgPlayers().get(livingEntity.getUniqueId());
              double damage = 0.5 * statuses.get(StatusType.BRITTLE).getStackAmount();
              player.damage(0.1);
              rpgPlayer.getHealth().damage(new DamageMitigation(player).mitigateProtectionResistance(damage));
            }
          } else {
            livingEntity.damage(0.1);
            livingEntity.setHealth(Math.max(0, livingEntity.getHealth() + 0.1 - (0.5 * statuses.get(StatusType.BRITTLE).getStackAmount())));
          }
          statuses.remove(StatusType.BRITTLE);
        }
      }
    }
    if (cooldown > 0) {
      setOnCooldown(true);
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> setOnCooldown(false), cooldown);
    }
  }

  /**
   * Performs {@link ActiveAbilityType.Effect#TELEPORT} across a distance.
   *
   * @param caster ability caster
   */
  private void teleportDistance(Player caster) {
    Location origin = caster.getLocation();
    caster.teleport(ifValidTeleportThroughBlock(origin, origin.getDirection(), origin, Integer.parseInt(effectData.get(0))));
    if (cooldown > 0) {
      setOnCooldown(true);
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> setOnCooldown(false), cooldown);
    }
  }

  /**
   * Gets if the teleport action can proceed through the next block.
   *
   * @param origin   origin location
   * @param vector   direction
   * @param location current location
   * @param distance number of meters to check
   * @return the furthest valid teleport location
   */
  private Location ifValidTeleportThroughBlock(Location origin, Vector vector, Location location, int distance) {
    if (distance < 0) {
      return origin;
    }
    Material blockType = location.getBlock().getType();
    if (blockType == Material.BEDROCK || blockType == Material.BARRIER) {
      return origin;
    }
    distance -= 1;
    if (!blockType.isSolid()) {
      origin = location.clone();
    }
    return ifValidTeleportThroughBlock(origin, vector, location.add(vector), distance);
  }

  /**
   * Gets the {@link RpgEquipmentSlot}.
   *
   * @return {@link RpgEquipmentSlot}
   */
  @NotNull
  public RpgEquipmentSlot getSlot() {
    return eSlot;
  }


  /**
   * Gets the {@link ActiveAbilityType}.
   *
   * @return {@link ActiveAbilityType}
   */
  @NotNull
  public ActiveAbilityType getType() {
    return type;
  }

  /**
   * Gets the {@link ActiveAbilityType.Effect} data.
   *
   * @return {@link ActiveAbilityType.Effect} data
   */
  @NotNull
  public List<String> getEffectData() {
    return effectData;
  }

  /**
   * Gets if the {@link ActiveAbilityType} is on cooldown.
   *
   * @return if the {@link ActiveAbilityType} is on cooldown
   */
  public boolean isOnCooldown() {
    return onCooldownActives.get(eSlot).contains(type);
  }

  /**
   * Sets if the {@link ActiveAbilityType} is on cooldown.
   *
   * @param isOnCooldown is on cooldown
   */
  private void setOnCooldown(boolean isOnCooldown) {
    if (isOnCooldown) {
      onCooldownActives.get(eSlot).add(type);
    } else {
      onCooldownActives.get(eSlot).remove(type);
    }
  }
}
