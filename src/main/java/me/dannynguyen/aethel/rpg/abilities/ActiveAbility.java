package me.dannynguyen.aethel.rpg.abilities;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.rpg.AethelAttribute;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents an item's {@link ActiveAbilityType}.
 *
 * @author Danny Nguyen
 * @version 1.19.11
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
  private final int baseCooldown;

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
    this.baseCooldown = Integer.parseInt(dataValues[0]);
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
      case CLEAR_STATUS -> {
        // No data to initialize.
      }
      case MOVEMENT, SHATTER, TELEPORT -> effectData.add(dataValues[1]);
      case DISTANCE_DAMAGE, PROJECTION -> {
        effectData.add(dataValues[1]);
        effectData.add(dataValues[2]);
      }
      case POTION_EFFECT -> {
        effectData.add(dataValues[1]);
        effectData.add(dataValues[2]);
        effectData.add(dataValues[3]);
        effectData.add(dataValues[4]);
      }
    }
  }

  /**
   * Triggers the {@link ActiveAbilityType.Effect}.
   *
   * @param rpgPlayer {@link RpgPlayer}
   * @param caster    ability caster
   */
  public void doEffect(@NotNull RpgPlayer rpgPlayer, @NotNull Player caster) {
    double cooldownModifier = Objects.requireNonNull(rpgPlayer, "Null RPG player").getAethelAttributes().getAttributes().get(AethelAttribute.ITEM_COOLDOWN) / 100;
    Objects.requireNonNull(caster, "Null caster");
    switch (type.getEffect()) {
      case CLEAR_STATUS -> clearStatus(cooldownModifier, caster);
      case DISTANCE_DAMAGE -> dealDistanceDamage(cooldownModifier, caster);
      case MOVEMENT -> moveDistance(cooldownModifier, caster);
      case POTION_EFFECT -> applyPotionEffect(cooldownModifier, caster);
      case PROJECTION -> projectDistance(cooldownModifier, caster);
      case SHATTER -> shatterBrittle(cooldownModifier, caster);
      case TELEPORT -> teleportDistance(cooldownModifier, caster);
    }
  }

  /**
   * Performs {@link ActiveAbilityType.Effect#CLEAR_STATUS} for {@link Status} based
   * on the {@link StatusType.Type}.
   *
   * @param cooldownModifier cooldown modifier
   * @param caster           ability caster
   */
  private void clearStatus(double cooldownModifier, Player caster) {
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
    if (baseCooldown > 0) {
      setOnCooldown(true);
      int cooldown = (int) Math.min(1, baseCooldown - (baseCooldown * cooldownModifier));
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> setOnCooldown(false), cooldown);
    }
  }

  /**
   * Performs {@link ActiveAbilityType.Effect#DISTANCE_DAMAGE} across a distance.
   *
   * @param cooldownModifier cooldown modifier
   * @param caster           ability caster
   */
  private void dealDistanceDamage(double cooldownModifier, Player caster) {
    RpgPlayer rpgPlayer = Plugin.getData().getRpgSystem().getRpgPlayers().get(caster.getUniqueId());
    double damage = Double.parseDouble(effectData.get(0)) * (1 + rpgPlayer.getAethelAttributes().getAttributes().get(AethelAttribute.ITEM_DAMAGE) / 100);
    double distance = Double.parseDouble(effectData.get(1));

    Vector casterDirection = caster.getLocation().getDirection();
    Set<LivingEntity> targets = new HashSet<>();

    switch (type) {
      case EXPLODE -> {
        for (Entity entity : caster.getNearbyEntities(distance, distance, distance)) {
          if (entity instanceof LivingEntity livingEntity) {
            targets.add(livingEntity);
          }
        }
        targets.remove(caster);
      }
      case FORCE_SWEEP -> {
        for (Entity entity : caster.getNearbyEntities(distance, 1, distance)) {
          if (entity instanceof LivingEntity livingEntity) {
            if (getLivingEntityDirectionAngle(caster.getLocation(), casterDirection, livingEntity) <= 45) {
              targets.add(livingEntity);
            }
          }
        }
      }
      case FORCE_WAVE -> {
        getForceWaveTargets(targets, caster.getLocation(), casterDirection, distance);
        targets.remove(caster);
      }
      case QUAKE -> {
        for (Entity entity : caster.getNearbyEntities(distance, 1, distance)) {
          if (entity instanceof LivingEntity livingEntity) {
            targets.add(livingEntity);
          }
        }
        targets.remove(caster);
      }
    }

    for (LivingEntity livingEntity : targets) {
      if (livingEntity instanceof Player player) {
        if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
          player.damage(0.1);
          rpgPlayer.getHealth().damage(new DamageMitigation(player).mitigateArmorProtectionResistance(damage));
        }
      } else {
        livingEntity.damage(0.1);
        livingEntity.setHealth(Math.max(0, livingEntity.getHealth() + 0.1 - damage));
      }
    }

    if (baseCooldown > 0) {
      setOnCooldown(true);
      int cooldown = (int) Math.min(1, baseCooldown - (baseCooldown * cooldownModifier));
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> setOnCooldown(false), cooldown);
    }
  }

  /**
   * Performs {@link ActiveAbilityType.Effect#MOVEMENT} across a distance.
   *
   * @param cooldownModifier cooldown modifier
   * @param caster           ability caster
   */
  private void moveDistance(double cooldownModifier, Player caster) {
    Vector vector = new Vector();
    double multiplier = 0.325 + (0.65 * (Double.parseDouble(effectData.get(0)) / 100)) + caster.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue() * 3.25;
    switch (type) {
      case DASH -> {
        vector = caster.getLocation().getDirection().multiply(multiplier);
        vector.setY(0.2);
      }
      case LEAP -> vector = caster.getLocation().getDirection().multiply(multiplier);
      case SPRING -> {
        vector.setX(0);
        vector.setY(1);
        vector.setZ(0);
        vector = vector.multiply(multiplier);
      }
      case WITHDRAW -> {
        vector = caster.getLocation().getDirection().multiply(-multiplier);
        caster.setVelocity(vector.setY(0.2));
      }
    }
    caster.setVelocity(vector);
    if (baseCooldown > 0) {
      setOnCooldown(true);
      int cooldown = (int) Math.min(1, baseCooldown - (baseCooldown * cooldownModifier));
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> setOnCooldown(false), cooldown);
    }
  }

  /**
   * Applies {@link ActiveAbilityType.Effect#POTION_EFFECT} on the caster.
   *
   * @param cooldownModifier cooldown modifier
   * @param caster           ability caster
   */
  private void applyPotionEffect(double cooldownModifier, Player caster) {
    PotionEffectType potionEffectType = PotionEffectType.getByName(effectData.get(0));
    int amplifier = Integer.parseInt(effectData.get(1));
    int duration = Integer.parseInt(effectData.get(2));
    boolean ambient = Boolean.parseBoolean(effectData.get(3));

    caster.addPotionEffect(new PotionEffect(potionEffectType, duration, amplifier, ambient));

    if (baseCooldown > 0) {
      setOnCooldown(true);
      int cooldown = (int) Math.min(1, baseCooldown - (baseCooldown * cooldownModifier));
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> setOnCooldown(false), cooldown);
    }
  }

  /**
   * Performs {@link ActiveAbilityType.Effect#PROJECTION} across a distance.
   *
   * @param cooldownModifier cooldown modifier
   * @param caster           ability caster
   */
  private void projectDistance(double cooldownModifier, Player caster) {
    final Location castOrigin = caster.getLocation().clone();
    Location origin = caster.getLocation();
    caster.teleport(ifValidTeleportThroughBlock(origin, origin.getDirection(), origin, Integer.parseInt(effectData.get(0))));
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> caster.teleport(castOrigin), Integer.parseInt(effectData.get(1)));
    if (baseCooldown > 0) {
      setOnCooldown(true);
      int cooldown = (int) Math.min(1, baseCooldown - (baseCooldown * cooldownModifier));
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> setOnCooldown(false), cooldown);
    }
  }

  /**
   * Consumes {@link me.dannynguyen.aethel.enums.rpg.StatusType#BRITTLE}
   * stacks on entities.
   *
   * @param cooldownModifier cooldown modifier
   * @param caster           ability caster
   */
  private void shatterBrittle(double cooldownModifier, Player caster) {
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
              rpgPlayer.getHealth().damage(new DamageMitigation(player).mitigateArmorProtectionResistance(damage));
            }
          } else {
            livingEntity.damage(0.1);
            livingEntity.setHealth(Math.max(0, livingEntity.getHealth() + 0.1 - (0.5 * statuses.get(StatusType.BRITTLE).getStackAmount())));
          }
          statuses.remove(StatusType.BRITTLE);
        }
      }
    }
    if (baseCooldown > 0) {
      setOnCooldown(true);
      int cooldown = (int) Math.min(1, baseCooldown - (baseCooldown * cooldownModifier));
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> setOnCooldown(false), cooldown);
    }
  }

  /**
   * Performs {@link ActiveAbilityType.Effect#TELEPORT} across a distance.
   *
   * @param cooldownModifier cooldown modifier
   * @param caster           ability caster
   */
  private void teleportDistance(double cooldownModifier, Player caster) {
    Location origin = caster.getLocation();
    caster.teleport(ifValidTeleportThroughBlock(origin, origin.getDirection(), origin, Integer.parseInt(effectData.get(0))));
    if (baseCooldown > 0) {
      setOnCooldown(true);
      int cooldown = (int) Math.min(1, baseCooldown - (baseCooldown * cooldownModifier));
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> setOnCooldown(false), cooldown);
    }
  }

  /**
   * Gets a living entity's direction angle from on the location of the caster.
   *
   * @param casterLocation  caster's location
   * @param casterDirection caster's direction
   * @param livingEntity    living entity
   * @return living entity's direction angle
   */
  private double getLivingEntityDirectionAngle(Location casterLocation, Vector casterDirection, LivingEntity livingEntity) {
    Vector casterLocationVector = casterLocation.toVector();
    Vector entityDirection = livingEntity.getLocation().toVector().subtract(casterLocationVector);

    double x1 = casterDirection.getX();
    double z1 = casterDirection.getZ();
    double x2 = entityDirection.getX();
    double z2 = entityDirection.getZ();

    double dotProduct = x1 * x2 + z1 * z2;
    double vectorLengths = Math.sqrt(Math.pow(x1, 2) + Math.pow(z1, 2)) * Math.sqrt(Math.pow(x2, 2) + Math.pow(z2, 2));
    return Math.acos(dotProduct / vectorLengths) * (180 / Math.PI);
  }

  /**
   * Recursively finds forcewave-affected targets.
   *
   * @param targets         set of affected targets
   * @param origin          origin location
   * @param casterDirection caster's direction
   * @param meters          distance
   * @return set of forcewave-affected targets
   */
  private Set<LivingEntity> getForceWaveTargets(Set<LivingEntity> targets, Location origin, Vector casterDirection, double meters) {
    if (meters < 1.5) {
      return targets;
    }
    if (meters >= 1.5) {
      origin = origin.add(casterDirection.multiply(1.5)).clone();
      meters -= 1.5;
      for (Entity entity : origin.getWorld().getNearbyEntities(origin, 1.5, 1.5, 1.5)) {
        if (entity instanceof LivingEntity livingEntity) {
          targets.add(livingEntity);
        }
      }
    }
    return getForceWaveTargets(targets, origin, casterDirection, meters);
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
