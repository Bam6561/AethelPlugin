package me.dannynguyen.aethel.rpg.abilities;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.rpg.AethelAttribute;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.enums.rpg.StatusType;
import me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType;
import me.dannynguyen.aethel.rpg.Buffs;
import me.dannynguyen.aethel.rpg.DamageMitigation;
import me.dannynguyen.aethel.rpg.Health;
import me.dannynguyen.aethel.rpg.Status;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents an item's {@link ActiveAbilityType}.
 *
 * @author Danny Nguyen
 * @version 1.22.18
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
    loadAbilityData(type.getEffect(), dataValues);
  }

  /**
   * Loads the {@link ActiveAbilityType active ability's} ability data.
   *
   * @param effect     {@link ActiveAbilityType.Effect}
   * @param dataValues ability data
   */
  private void loadAbilityData(ActiveAbilityType.Effect effect, String[] dataValues) {
    switch (effect) {
      case CLEAR_STATUS -> { // No data to load.
      }
      case MOVEMENT, SHATTER, TELEPORT -> effectData.add(dataValues[1]);
      case DISTANCE_DAMAGE, PROJECTION -> {
        effectData.add(dataValues[1]);
        effectData.add(dataValues[2]);
      }
      case BUFF -> {
        effectData.add(dataValues[1]);
        effectData.add(dataValues[2]);
        effectData.add(dataValues[3]);
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
   * @param caster ability caster
   */
  public void doEffect(@NotNull Player caster) {
    UUID uuid = Objects.requireNonNull(caster, "Null caster").getUniqueId();
    PersistentDataContainer entityTags = Bukkit.getPlayer(uuid).getPersistentDataContainer();
    Buffs buffs = Plugin.getData().getRpgSystem().getBuffs().get(uuid);

    double itemCooldownBase = entityTags.getOrDefault(Key.ATTRIBUTE_ITEM_COOLDOWN.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
    double cooldownModifierBuff = 0.0;
    if (buffs != null) {
      cooldownModifierBuff = buffs.getAethelAttribute(AethelAttribute.ITEM_COOLDOWN);
    }
    double cooldownModifier = (itemCooldownBase + cooldownModifierBuff) / 100;

    switch (type.getEffect()) {
      case BUFF -> applyBuff(cooldownModifier, caster);
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
   * Performs {@link ActiveAbilityType.Effect#BUFF} for the attribute.
   *
   * @param cooldownModifier cooldown modifier
   * @param caster           ability caster
   */
  private void applyBuff(double cooldownModifier, Player caster) {
    UUID uuid = caster.getUniqueId();
    Map<UUID, Buffs> entityBuffs = Plugin.getData().getRpgSystem().getBuffs();
    if (entityBuffs.get(uuid) == null) {
      entityBuffs.put(uuid, new Buffs(uuid));
    }
    Buffs buffs = entityBuffs.get(uuid);
    double value = Double.parseDouble(effectData.get(1));
    int duration = Integer.parseInt(effectData.get(2));

    try {
      Attribute attribute = Attribute.valueOf(effectData.get(0).toUpperCase());
      buffs.addAttribute(attribute, value, duration);
    } catch (IllegalArgumentException ex) {
      try {
        AethelAttribute aethelAttribute = AethelAttribute.valueOf(effectData.get(0).toUpperCase());
        buffs.addAethelAttribute(aethelAttribute, value, duration);
      } catch (IllegalArgumentException ignored) {
      }
    }

    if (baseCooldown > 0) {
      setOnCooldown(true);
      int cooldown = (int) Math.max(1, baseCooldown - (baseCooldown * cooldownModifier));
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> setOnCooldown(false), cooldown);
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
    World world = caster.getWorld();
    Map<UUID, Map<StatusType, Status>> entityStatuses = Plugin.getData().getRpgSystem().getStatuses();
    UUID uuid = caster.getUniqueId();
    Collection<PotionEffect> activePotionEffects = caster.getActivePotionEffects();
    List<PotionEffectType> potionEffectsToRemove = new ArrayList<>();
    Map<StatusType, Status> statuses = entityStatuses.get(uuid);

    switch (type) {
      case DISMISS -> {
        world.playSound(caster.getEyeLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1, 1.5f);
        world.spawnParticle(Particle.SCRAPE, caster.getLocation().add(0, 1, 0), 10, 0.75, 0.75, 0.75);
        for (PotionEffect potionEffect : activePotionEffects) {
          switch (potionEffect.getType().getName()) {
            case "BAD_OMEN", "BLINDNESS", "CONFUSION", "DARKNESS", "GLOWING", "HUNGER", "LEVITATION",
                "SLOW", "SLOW_DIGGING", "UNLUCK", "WEAKNESS" -> potionEffectsToRemove.add(potionEffect.getType());
          }
        }
        if (statuses != null) {
          for (StatusType type : StatusType.Type.NON_DAMAGE.getStatusTypes()) {
            statuses.remove(type);
          }
        }
      }
      case DISREGARD -> {
        world.playSound(caster.getEyeLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1, 2);
        world.spawnParticle(Particle.EGG_CRACK, caster.getLocation().add(0, 1, 0), 10, 0.75, 0.75, 0.75);
        for (PotionEffect potionEffect : activePotionEffects) {
          switch (potionEffect.getType().getName()) {
            case "HARM", "POISON", "WITHER" -> potionEffectsToRemove.add(potionEffect.getType());
          }
        }
        if (statuses != null) {
          for (StatusType type : StatusType.Type.DAMAGE.getStatusTypes()) {
            statuses.remove(type);
          }
        }
      }
    }
    for (PotionEffectType potionEffectType : potionEffectsToRemove) {
      caster.removePotionEffect(potionEffectType);
    }

    if (baseCooldown > 0) {
      setOnCooldown(true);
      int cooldown = (int) Math.max(1, baseCooldown - (baseCooldown * cooldownModifier));
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
    World world = caster.getWorld();
    PersistentDataContainer entityTags = caster.getPersistentDataContainer();
    Buffs buffs = Plugin.getData().getRpgSystem().getBuffs().get(caster.getUniqueId());

    double itemDamageBase = entityTags.getOrDefault(Key.ATTRIBUTE_ITEM_DAMAGE.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
    double damageModifierBuff = 0.0;
    if (buffs != null) {
      damageModifierBuff = buffs.getAethelAttribute(AethelAttribute.ITEM_DAMAGE);
    }
    double damageModifier = 1 + (itemDamageBase + damageModifierBuff) / 100;
    double damage = Double.parseDouble(effectData.get(0)) * damageModifier;
    int distance = Integer.parseInt(effectData.get(1));
    Set<LivingEntity> targets = new HashSet<>();

    switch (type) {
      case EXPLODE -> {
        world.playSound(caster.getEyeLocation(), Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.85f, 0.5f);
        world.spawnParticle(Particle.EXPLOSION_LARGE, caster.getEyeLocation(), 3, 0.5, 0.5, 0.5);
        for (Entity entity : caster.getNearbyEntities(distance, distance, distance)) {
          if (entity instanceof LivingEntity livingEntity) {
            if (isTargetUnobstructed(caster, livingEntity)) {
              targets.add(livingEntity);
            }
          }
        }
      }
      case FORCE_SWEEP -> {
        world.playSound(caster.getEyeLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 0.65f, 0);
        Vector casterDirection = caster.getLocation().getDirection();
        world.spawnParticle(Particle.SWEEP_ATTACK, caster.getLocation().add(0, 1, 0).add(casterDirection.setY(0).multiply(1.5)), 3, 0.125, 0.125, 0.125);
        for (Entity entity : caster.getNearbyEntities(distance, 1, distance)) {
          if (entity instanceof LivingEntity livingEntity) {
            if (getDirectionAngle(caster, livingEntity) <= 45 && isTargetUnobstructed(caster, livingEntity)) {
              targets.add(livingEntity);
            }
          }
        }
      }
      case FORCE_WAVE -> {
        world.playSound(caster.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, SoundCategory.PLAYERS, 0.65f, 0);
        Vector casterDirection = caster.getLocation().getDirection();
        getForceWaveTargets(world, targets, caster.getEyeLocation(), casterDirection, distance);
      }
      case QUAKE -> {
        world.playSound(caster.getEyeLocation(), Sound.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 0.25f, 2);
        world.spawnParticle(Particle.BLOCK_DUST, caster.getLocation(), 20, 1.5, 0.25, 1.5, Bukkit.createBlockData(Material.DIRT));
        for (Entity entity : caster.getNearbyEntities(distance, 1, distance)) {
          if (entity instanceof LivingEntity livingEntity) {
            if (isTargetUnobstructed(caster, livingEntity)) {
              targets.add(livingEntity);
            }
          }
        }
      }
    }
    targets.remove(caster);

    for (LivingEntity livingEntity : targets) {
      final double finalDamage = new DamageMitigation(livingEntity).mitigateProtectionResistance(damage);
      if (livingEntity instanceof Player player && (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE)) {
        Health health = Plugin.getData().getRpgSystem().getRpgPlayers().get(player.getUniqueId()).getHealth();
        player.damage(0.1);
        health.damage(finalDamage);
      } else {
        livingEntity.damage(0.1);
        livingEntity.setHealth(Math.max(0, livingEntity.getHealth() + 0.1 - finalDamage));
      }
    }

    if (baseCooldown > 0) {
      setOnCooldown(true);
      int cooldown = (int) Math.max(1, baseCooldown - (baseCooldown * cooldownModifier));
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
    World world = caster.getWorld();
    Vector vector = new Vector();
    double multiplier = 0.325 + (0.65 * (Double.parseDouble(effectData.get(0)) / 100)) + caster.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue() * 3.25;
    switch (type) {
      case DASH -> {
        world.playSound(caster.getEyeLocation(), Sound.ENTITY_WITHER_SHOOT, SoundCategory.PLAYERS, 0.25f, 0.5f);
        vector = caster.getLocation().getDirection().multiply(multiplier);
        vector.setY(0.2);
        world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, caster.getLocation(), 5, 0.125, 0.125, 0.125, 0.025);
        Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
          world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, caster.getLocation(), 5, 0.125, 0.125, 0.125, 0.025);
          Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, caster.getLocation(), 5, 0.125, 0.125, 0.125, 0.025), 5);
        }, 5);
      }
      case LEAP -> {
        world.playSound(caster.getLocation(), Sound.ENTITY_SLIME_JUMP, SoundCategory.PLAYERS, 1, 0);
        world.spawnParticle(Particle.SLIME, caster.getLocation(), 15, 0.75, 0.25, 0.75);
        vector = caster.getLocation().getDirection().multiply(multiplier);
      }
      case SPRING -> {
        world.playSound(caster.getEyeLocation(), Sound.BLOCK_BEEHIVE_ENTER, SoundCategory.PLAYERS, 1, 2);
        world.spawnParticle(Particle.SLIME, caster.getLocation(), 15, 0.5, 0.25, 0.25);
        vector.setX(0);
        vector.setY(1);
        vector.setZ(0);
        vector = vector.multiply(multiplier);
      }
      case WITHDRAW -> {
        world.playSound(caster.getEyeLocation(), Sound.ENTITY_WITCH_THROW, SoundCategory.PLAYERS, 0.65f, 0.5f);
        world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, caster.getLocation(), 10, 0.125, 0.125, 0.125, 0.025);
        vector = caster.getLocation().getDirection().multiply(-multiplier);
        caster.setVelocity(vector.setY(0.2));
      }
    }
    caster.setVelocity(vector);
    if (baseCooldown > 0) {
      setOnCooldown(true);
      int cooldown = (int) Math.max(1, baseCooldown - (baseCooldown * cooldownModifier));
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
      int cooldown = (int) Math.max(1, baseCooldown - (baseCooldown * cooldownModifier));
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
    World world = caster.getWorld();
    final Location abilityLocation = caster.getLocation().clone();
    Location casterLocation = caster.getLocation();
    world.spawnParticle(Particle.PORTAL, casterLocation, 15, 0.5, 0.5, 0.5);
    caster.teleport(ifValidTeleportThroughBlock(casterLocation, casterLocation, casterLocation.getDirection(), Integer.parseInt(effectData.get(0))));
    world.playSound(caster.getEyeLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1, 0.75f);
    world.spawnParticle(Particle.PORTAL, casterLocation, 15, 0.5, 0.5, 0.5);
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
      world.spawnParticle(Particle.PORTAL, casterLocation, 15, 0.5, 0.5, 0.5);
      caster.teleport(abilityLocation);
      world.playSound(caster.getEyeLocation(), Sound.ENTITY_WARDEN_SONIC_CHARGE, SoundCategory.PLAYERS, 0.5f, 0.75f);
      world.spawnParticle(Particle.PORTAL, casterLocation, 15, 0.5, 0.5, 0.5);
    }, Integer.parseInt(effectData.get(1)));
    if (baseCooldown > 0) {
      setOnCooldown(true);
      int cooldown = (int) Math.max(1, baseCooldown - (baseCooldown * cooldownModifier));
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
    World world = caster.getWorld();
    Map<UUID, Map<StatusType, Status>> entityStatuses = Plugin.getData().getRpgSystem().getStatuses();
    double meters = Double.parseDouble(effectData.get(0));

    world.playSound(caster.getEyeLocation(), Sound.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 0.5f, 0.25f);
    for (Entity entity : caster.getNearbyEntities(meters, meters, meters)) {
      if (entity instanceof LivingEntity livingEntity) {
        UUID livingEntityUUID = livingEntity.getUniqueId();

        if (entityStatuses.containsKey(livingEntityUUID) && entityStatuses.get(livingEntityUUID).containsKey(StatusType.BRITTLE)) {
          world.spawnParticle(Particle.ITEM_CRACK, livingEntity.getLocation().add(0, 1, 0), 10, 0.25, 0.5, 0.25, new ItemStack(Material.LIGHT_BLUE_DYE));

          Map<StatusType, Status> statuses = entityStatuses.get(livingEntity.getUniqueId());
          double damage = 0.5 * statuses.get(StatusType.BRITTLE).getStackAmount();
          final double finalDamage = new DamageMitigation(livingEntity).mitigateArmorProtectionResistance(damage);

          if (livingEntity instanceof Player player && (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE)) {
            Health health = Plugin.getData().getRpgSystem().getRpgPlayers().get(livingEntity.getUniqueId()).getHealth();
            player.damage(0.1);
            health.damage(finalDamage);
          } else {
            livingEntity.damage(0.1);
            livingEntity.setHealth(Math.max(0, livingEntity.getHealth() + 0.1 - finalDamage));
          }
          statuses.remove(StatusType.BRITTLE);
        }
      }
    }
    if (baseCooldown > 0) {
      setOnCooldown(true);
      int cooldown = (int) Math.max(1, baseCooldown - (baseCooldown * cooldownModifier));
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
    World world = caster.getWorld();
    Location casterLocation = caster.getLocation();
    world.spawnParticle(Particle.PORTAL, casterLocation, 15, 0.5, 0.5, 0.5);
    caster.teleport(ifValidTeleportThroughBlock(casterLocation, casterLocation, casterLocation.getDirection(), Integer.parseInt(effectData.get(0))));
    world.playSound(caster.getEyeLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1, 0.75f);
    world.spawnParticle(Particle.PORTAL, casterLocation, 15, 0.5, 0.5, 0.5);
    if (baseCooldown > 0) {
      setOnCooldown(true);
      int cooldown = (int) Math.max(1, baseCooldown - (baseCooldown * cooldownModifier));
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> setOnCooldown(false), cooldown);
    }
  }

  /**
   * Checks if the entity is unobstructed by solid blocks.
   * <p>
   * A path is drawn starting from the entity in the direction of
   * the caster because for long-distance computations, the likelihood
   * a block exists closer to the entity and obstructs the path is greater.
   *
   * @param caster ability caster
   * @param entity potential target entity
   * @return if the target is obstructed
   */
  private boolean isTargetUnobstructed(Player caster, LivingEntity entity) {
    // Checks eye-to-eye, covers edge cases where caster is against a wall
    Location casterEyeLocation = caster.getEyeLocation();
    Location entityEyeLocation = entity.getEyeLocation();
    Vector eyeDirection = casterEyeLocation.toVector().subtract(entityEyeLocation.toVector()).normalize();
    if (casterEyeLocation.add(eyeDirection.multiply(-0.5)).getBlock().getType().isSolid()) {
      return false;
    }

    // Checks feet-to-feet
    Location casterLocation = caster.getLocation();
    Location entityLocation = entity.getLocation();
    Vector direction = casterLocation.toVector().subtract(entityLocation.toVector()).normalize();
    return isUnobstructed(entityLocation, direction, (int) casterLocation.distance(entityLocation));
  }

  /**
   * Recursively finds if the path leading from a location is unobstructed by solid blocks.
   *
   * @param location  current location
   * @param direction direction
   * @param distance  number of meters to check
   * @return if the path from a location is obstructed by blocks
   */
  private boolean isUnobstructed(Location location, Vector direction, int distance) {
    if (distance <= 0) {
      return true;
    }
    if (location.getBlock().getType().isSolid()) {
      return false;
    }
    return isUnobstructed(location.add(direction), direction, distance - 1);
  }

  /**
   * Gets an entity's direction angle from the location of the caster.
   *
   * @param caster ability caster
   * @param entity potential target entity
   * @return entity's direction angle
   */
  private double getDirectionAngle(Player caster, LivingEntity entity) {
    Location casterLocation = caster.getLocation();
    Vector casterDirection = casterLocation.getDirection();
    Vector casterLocationVector = casterLocation.toVector();
    Vector entityDirection = entity.getLocation().toVector().subtract(casterLocationVector);

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
   * @param world     world
   * @param targets   set of affected targets
   * @param location  current location
   * @param direction caster's direction
   * @param distance  distance in meters to check
   * @return set of forcewave-affected targets
   */
  private Set<LivingEntity> getForceWaveTargets(World world, Set<LivingEntity> targets, Location location, Vector direction, int distance) {
    if (distance <= 0 || location.getBlock().getType().isSolid()) {
      return targets;
    }
    world.spawnParticle(Particle.CLOUD, location, 2, 0.125, 0.125, 0.125, 0.025);
    for (Entity entity : world.getNearbyEntities(location, 1, 1, 1)) {
      if (entity instanceof LivingEntity livingEntity) {
        targets.add(livingEntity);
      }
    }
    return getForceWaveTargets(world, targets, location.add(direction).clone(), direction, distance - 1);
  }

  /**
   * Gets if the teleport action can proceed through the next block.
   *
   * @param validTeleportLocation valid teleport location
   * @param location              current location
   * @param direction             caster's direction
   * @param distance              number of meters to check
   * @return the furthest valid teleport location
   */
  private Location ifValidTeleportThroughBlock(Location validTeleportLocation, Location location, Vector direction, int distance) {
    Material blockType = location.getBlock().getType();
    if (distance <= 0 || blockType == Material.BEDROCK || blockType == Material.BARRIER) {
      return validTeleportLocation;
    }
    if (!blockType.isSolid()) {
      validTeleportLocation = location.clone();
    }
    return ifValidTeleportThroughBlock(validTeleportLocation, location.add(direction), direction, distance - 1);
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
