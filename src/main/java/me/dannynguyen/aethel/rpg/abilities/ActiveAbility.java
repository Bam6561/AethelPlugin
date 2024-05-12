package me.dannynguyen.aethel.rpg.abilities;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.rpg.AethelAttribute;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.enums.rpg.StatusType;
import me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType;
import me.dannynguyen.aethel.rpg.Buffs;
import me.dannynguyen.aethel.rpg.Equipment;
import me.dannynguyen.aethel.rpg.Status;
import me.dannynguyen.aethel.utils.entity.DamageMitigation;
import me.dannynguyen.aethel.utils.entity.HealthChange;
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
 * @version 1.25.0
 * @since 1.17.4
 */
public class ActiveAbility {
  /**
   * {@link Source}
   */
  private final Source source;

  /**
   * {@link ActiveAbilityType Active abilities} on cooldown.
   */
  private Map<RpgEquipmentSlot, Set<ActiveAbilityType>> onCooldownActives;

  /**
   * {@link RpgEquipmentSlot}
   */
  private RpgEquipmentSlot eSlot;

  /**
   * {@link ActiveAbilityType Edible active abilities} on cooldown.
   */
  private Set<String> onCooldownEdibles;

  /**
   * Edible item id.
   */
  private String id;

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
   * Associates an {@link Equipment} {@link ActiveAbilityType active ability} with its data.
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
    Objects.requireNonNull(dataValues, "Null data values");
    this.baseCooldown = Integer.parseInt(dataValues[0]);
    this.source = Source.EQUIPMENT;
    loadAbilityData(type.getEffect(), dataValues);
  }

  /**
   * Associates an edible {@link ActiveAbilityType active ability} with its data.
   *
   * @param onCooldownEdibles {@link ActiveAbilityType} on cooldown
   * @param id                edible item id
   * @param type              {@link ActiveAbilityType}
   * @param dataValues        ability data
   */
  public ActiveAbility(@NotNull Set<String> onCooldownEdibles, @NotNull String id, @NotNull ActiveAbilityType type, @NotNull String[] dataValues) {
    this.onCooldownEdibles = Objects.requireNonNull(onCooldownEdibles, "Null on cooldown edibles");
    this.id = Objects.requireNonNull(id, "Null item id");
    this.type = Objects.requireNonNull(type, "Null ability");
    Objects.requireNonNull(dataValues, "Null data values");
    this.baseCooldown = Integer.parseInt(dataValues[0]);
    this.source = Source.EDIBLE;
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
      case DISPLACEMENT, DISTANCE_DAMAGE, PROJECTION -> {
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
      case BUFF -> new Effect().applyBuff(cooldownModifier, caster);
      case CLEAR_STATUS -> new Effect().clearStatus(cooldownModifier, caster);
      case DISPLACEMENT -> new Effect().displaceEntities(cooldownModifier, caster);
      case DISTANCE_DAMAGE -> new Effect().dealDistanceDamage(cooldownModifier, caster);
      case MOVEMENT -> new Effect().moveDistance(cooldownModifier, caster);
      case POTION_EFFECT -> new Effect().applyPotionEffect(cooldownModifier, caster);
      case PROJECTION -> new Effect().projectDistance(cooldownModifier, caster);
      case SHATTER -> new Effect().shatterChill(cooldownModifier, caster);
      case TELEPORT -> new Effect().teleportDistance(cooldownModifier, caster);
    }
  }

  /**
   * Gets if the {@link ActiveAbilityType} is on cooldown.
   *
   * @return if the {@link ActiveAbilityType} is on cooldown
   */
  public boolean isOnCooldown() {
    switch (source) {
      case EQUIPMENT -> {
        return onCooldownActives.get(eSlot).contains(type);
      }
      case EDIBLE -> {
        return onCooldownEdibles.contains(id);
      }
    }
    return false;
  }

  /**
   * Represents an ability's effect.
   *
   * @author Danny Nguyen
   * @version 1.25.5
   * @since 1.23.13
   */
  private class Effect {
    /**
     * No parameter constructor.
     */
    Effect() {
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

      cooldownAbility(cooldownModifier);
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
          world.playSound(caster.getEyeLocation(), Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.PLAYERS, 1, 1.5f);
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
          world.playSound(caster.getEyeLocation(), Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.PLAYERS, 1, 2);
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

      cooldownAbility(cooldownModifier);
    }

    /**
     * {@link ActiveAbilityType.Effect#DISPLACEMENT Displaces} entities across a distance.
     *
     * @param cooldownModifier cooldown modifier
     * @param caster           ability caster
     */
    private void displaceEntities(double cooldownModifier, Player caster) {
      double modifier = 0.65 + (0.65 * (Double.parseDouble(effectData.get(0)) / 100));
      int distance = Integer.parseInt(effectData.get(1));

      switch (type) {
        case DRAG -> {
          caster.getWorld().playSound(caster.getEyeLocation(), Sound.ITEM_TRIDENT_RIPTIDE_1, SoundCategory.PLAYERS, 1, 2);
          new TargetDisplacement(caster, modifier, distance).getDragTargets(caster.getEyeLocation(), distance);
        }
        case THRUST -> {
          caster.getWorld().playSound(caster.getEyeLocation(), Sound.ITEM_TRIDENT_RIPTIDE_1, SoundCategory.PLAYERS, 1, 0);
          new TargetDisplacement(caster, modifier, distance).getThrustTargets(caster.getEyeLocation(), distance);
        }
        case ATTRACT -> {
          Location casterLocation = caster.getLocation().add(0, 1, 0);
          caster.getWorld().playSound(caster.getEyeLocation(), Sound.ENTITY_IRON_GOLEM_HURT, SoundCategory.PLAYERS, 0.8f, 0.5f);
          TargetValidation targetValidation = new TargetValidation();
          for (Entity entity : caster.getNearbyEntities(distance, distance, distance)) {
            if (!(entity instanceof LivingEntity livingEntity)) {
              continue;
            }
            if (targetValidation.isTargetUnobstructed(caster, livingEntity)) {
              Location entityLocation = livingEntity.getLocation().add(0, 1, 0);
              double distanceModifier = casterLocation.distance(entityLocation) / distance * modifier;
              Vector velocity = casterLocation.toVector().subtract(entityLocation.toVector()).normalize().multiply(distanceModifier);
              livingEntity.setVelocity(velocity);
            }
          }
        }
        case REPEL -> {
          Location casterLocation = caster.getLocation().add(0, 1, 0);
          caster.getWorld().playSound(caster.getEyeLocation(), Sound.ENTITY_IRON_GOLEM_REPAIR, SoundCategory.PLAYERS, 0.55f, 0.5f);
          TargetValidation targetValidation = new TargetValidation();
          for (Entity entity : caster.getNearbyEntities(distance, distance, distance)) {
            if (!(entity instanceof LivingEntity livingEntity)) {
              continue;
            }
            if (targetValidation.isTargetUnobstructed(caster, livingEntity)) {
              Location entityLocation = livingEntity.getLocation().add(0, 1, 0);
              double distanceModifier = (distance - casterLocation.distance(entityLocation)) / distance * modifier;
              Vector velocity = entityLocation.toVector().subtract(casterLocation.toVector()).normalize().multiply(distanceModifier);
              livingEntity.setVelocity(velocity);
            }
          }
        }
      }

      cooldownAbility(cooldownModifier);
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
        case BEAM -> {
          world.playSound(caster.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, SoundCategory.PLAYERS, 0.65f, 0);
          Vector casterDirection = caster.getLocation().getDirection();
          new TargetValidation().getBeamTargets(world, targets, caster.getEyeLocation(), casterDirection, distance);
          targets.remove(caster);
        }
        case BULLET -> {
          world.playSound(caster.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, SoundCategory.PLAYERS, 0.65f, 0);
          Vector casterDirection = caster.getLocation().getDirection();
          new TargetValidation().getBulletTargets(caster, world, targets, caster.getEyeLocation(), casterDirection, distance);
          targets.remove(caster);
        }
        case EXPLODE -> {
          world.playSound(caster.getEyeLocation(), Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.85f, 0.5f);
          world.spawnParticle(Particle.EXPLOSION_LARGE, caster.getEyeLocation(), 3, 0.5, 0.5, 0.5);

          TargetValidation targetValidation = new TargetValidation();
          for (Entity entity : caster.getNearbyEntities(distance, distance, distance)) {
            if (!(entity instanceof LivingEntity livingEntity)) {
              continue;
            }
            if (targetValidation.isTargetUnobstructed(caster, livingEntity)) {
              targets.add(livingEntity);
            }
          }
        }
        case SWEEP -> {
          world.playSound(caster.getEyeLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 0.65f, 0);
          Vector casterDirection = caster.getLocation().getDirection();
          world.spawnParticle(Particle.SWEEP_ATTACK, caster.getLocation().add(0, 1, 0).add(casterDirection.setY(0).multiply(1.5)), 3, 0.125, 0.125, 0.125);

          TargetValidation targetValidation = new TargetValidation();
          for (Entity entity : caster.getNearbyEntities(distance, 1, distance)) {
            if (!(entity instanceof LivingEntity livingEntity)) {
              continue;
            }
            if (targetValidation.getDirectionAngle(caster, livingEntity) <= 45 && targetValidation.isTargetUnobstructed(caster, livingEntity)) {
              targets.add(livingEntity);
            }
          }
        }
        case QUAKE -> {
          world.playSound(caster.getEyeLocation(), Sound.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 0.25f, 2);
          world.spawnParticle(Particle.BLOCK_DUST, caster.getLocation(), 20, 1.5, 0.25, 1.5, Bukkit.createBlockData(Material.DIRT));

          TargetValidation targetValidation = new TargetValidation();
          for (Entity entity : caster.getNearbyEntities(distance, 1, distance)) {
            if (!(entity instanceof LivingEntity livingEntity)) {
              continue;
            }
            if (targetValidation.isTargetUnobstructed(caster, livingEntity)) {
              targets.add(livingEntity);
            }
          }
        }
      }

      for (LivingEntity livingEntity : targets) {
        final double finalDamage = new DamageMitigation(livingEntity).mitigateProtectionResistance(damage);
        if (livingEntity instanceof Player player) {
          if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
            new HealthChange(livingEntity).damage(finalDamage);
          }
        } else {
          new HealthChange(livingEntity).damage(finalDamage);
        }
      }

      cooldownAbility(cooldownModifier);
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
      double modifier = 0.325 + (0.65 * (Double.parseDouble(effectData.get(0)) / 100)) + caster.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue() * 3.25;

      switch (type) {
        case DASH -> {
          world.playSound(caster.getEyeLocation(), Sound.ENTITY_WITHER_SHOOT, SoundCategory.PLAYERS, 0.25f, 0.5f);
          vector = caster.getLocation().getDirection().multiply(modifier);
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
          vector = caster.getLocation().getDirection().multiply(modifier);
        }
        case SPRING -> {
          world.playSound(caster.getEyeLocation(), Sound.BLOCK_BEEHIVE_ENTER, SoundCategory.PLAYERS, 1, 2);
          world.spawnParticle(Particle.SLIME, caster.getLocation(), 15, 0.5, 0.25, 0.25);
          vector.setX(0);
          vector.setY(1);
          vector.setZ(0);
          vector = vector.multiply(modifier);
        }
        case WITHDRAW -> {
          world.playSound(caster.getEyeLocation(), Sound.ENTITY_WITCH_THROW, SoundCategory.PLAYERS, 0.65f, 0.5f);
          world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, caster.getLocation(), 10, 0.125, 0.125, 0.125, 0.025);
          vector = caster.getLocation().getDirection().multiply(-modifier);
          vector.setY(0.2);
        }
      }
      caster.setVelocity(vector);

      cooldownAbility(cooldownModifier);
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
      boolean particles = Boolean.parseBoolean(effectData.get(3));

      caster.addPotionEffect(new PotionEffect(potionEffectType, duration, amplifier, particles, particles));

      cooldownAbility(cooldownModifier);
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
      caster.teleport(new TargetTeleport(caster).ifValidTeleportThroughBlock(casterLocation, casterLocation, Integer.parseInt(effectData.get(0))));
      world.playSound(caster.getEyeLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1, 0.75f);
      world.spawnParticle(Particle.PORTAL, casterLocation, 15, 0.5, 0.5, 0.5);

      int delay = Integer.parseInt(effectData.get(1));
      int taskId = Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
        world.spawnParticle(Particle.PORTAL, casterLocation, 15, 0.5, 0.5, 0.5);
        caster.teleport(abilityLocation);
        world.playSound(caster.getEyeLocation(), Sound.ENTITY_WARDEN_SONIC_CHARGE, SoundCategory.PLAYERS, 0.5f, 0.75f);
        world.spawnParticle(Particle.PORTAL, casterLocation, 15, 0.5, 0.5, 0.5);
      }, delay).getTaskId();

      Set<Integer> projections = Plugin.getData().getRpgSystem().getRpgPlayers().get(caster.getUniqueId()).getProjections();
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> projections.remove(taskId), delay);
      projections.add(taskId);

      cooldownAbility(cooldownModifier);
    }

    /**
     * Consumes {@link me.dannynguyen.aethel.enums.rpg.StatusType#CHILL}
     * stacks on entities.
     *
     * @param cooldownModifier cooldown modifier
     * @param caster           ability caster
     */
    private void shatterChill(double cooldownModifier, Player caster) {
      World world = caster.getWorld();
      Map<UUID, Map<StatusType, Status>> entityStatuses = Plugin.getData().getRpgSystem().getStatuses();
      double meters = Double.parseDouble(effectData.get(0));

      world.playSound(caster.getEyeLocation(), Sound.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 0.5f, 0.25f);
      for (Entity entity : caster.getNearbyEntities(meters, meters, meters)) {
        if (!(entity instanceof LivingEntity livingEntity)) {
          continue;
        }

        UUID livingEntityUUID = livingEntity.getUniqueId();
        if (entityStatuses.containsKey(livingEntityUUID) && entityStatuses.get(livingEntityUUID).containsKey(StatusType.CHILL)) {
          world.spawnParticle(Particle.ITEM_CRACK, livingEntity.getLocation().add(0, 1, 0), 10, 0.25, 0.5, 0.25, new ItemStack(Material.LIGHT_BLUE_DYE));

          Map<StatusType, Status> statuses = entityStatuses.get(livingEntity.getUniqueId());
          double damage = 0.5 * statuses.get(StatusType.CHILL).getStackAmount();
          final double finalDamage = new DamageMitigation(livingEntity).mitigateArmorProtectionResistance(damage);

          if (livingEntity instanceof Player player) {
            if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
              new HealthChange(livingEntity).damage(finalDamage);
            }
          } else {
            new HealthChange(livingEntity).damage(finalDamage);
          }
          statuses.remove(StatusType.CHILL);
        }
      }
      cooldownAbility(cooldownModifier);
    }

    /**
     * Performs {@link ActiveAbilityType.Effect#TELEPORT} across a distance.
     *
     * @param cooldownModifier cooldown modifier
     * @param caster           ability caster
     */
    private void teleportDistance(double cooldownModifier, Player caster) {
      int distance = Integer.parseInt(effectData.get(0));
      World world = caster.getWorld();
      Location casterLocation = caster.getLocation();

      switch (type) {
        case BLINK -> {
          world.spawnParticle(Particle.PORTAL, casterLocation, 15, 0.5, 0.5, 0.5);
          caster.teleport(new TargetTeleport(caster).ifValidTeleportThroughBlock(casterLocation, casterLocation, distance));
          world.playSound(caster.getEyeLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1, 0.75f);
          world.spawnParticle(Particle.PORTAL, casterLocation, 15, 0.5, 0.5, 0.5);
        }
        case EMERGE -> {
          LivingEntity entity = new TargetTeleport(caster).getTeleportTarget(world, caster.getEyeLocation(), distance);
          if (entity != null) {
            world.spawnParticle(Particle.PORTAL, casterLocation, 15, 0.5, 0.5, 0.5);

            Location entityLocation = entity.getLocation();
            Location behindEntity = entityLocation.clone().subtract(entityLocation.getDirection());
            Location frontEntity = entityLocation.clone().add(entityLocation.getDirection());

            if (!behindEntity.add(0, 1, 0).getBlock().getType().isSolid()) {
              caster.teleport(behindEntity);
            } else if (!frontEntity.add(0, 1, 0).getBlock().getType().isSolid()) {
              frontEntity.setYaw(frontEntity.getYaw() - 180);
              caster.teleport(frontEntity);
            }

            world.playSound(caster.getEyeLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1, 0.75f);
            world.spawnParticle(Particle.PORTAL, casterLocation, 15, 0.5, 0.5, 0.5);
          }
        }
        case HOOK -> {
          LivingEntity entity = new TargetTeleport(caster).getTeleportTarget(world, caster.getEyeLocation(), distance);
          if (entity != null) {
            Location hookLocation = casterLocation.add(casterLocation.getDirection());
            hookLocation.setYaw(hookLocation.getYaw() - 180);
            entity.teleport(hookLocation);
            world.spawnParticle(Particle.PORTAL, entity.getLocation(), 15, 0.5, 0.5, 0.5);
            world.playSound(entity.getEyeLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1, 0.75f);
          }
        }
        case SWITCH -> {
          LivingEntity entity = new TargetTeleport(caster).getTeleportTarget(world, caster.getEyeLocation(), distance);
          if (entity != null) {
            world.spawnParticle(Particle.PORTAL, casterLocation, 15, 0.5, 0.5, 0.5);
            caster.teleport(entity.getLocation());
            entity.teleport(casterLocation);
            world.spawnParticle(Particle.PORTAL, caster.getLocation(), 15, 0.5, 0.5, 0.5);
            world.playSound(caster.getEyeLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1, 0.75f);
          }
        }
      }
      cooldownAbility(cooldownModifier);
    }

    /**
     * Puts the ability on cooldown, if any.
     *
     * @param cooldownModifier cooldown modifier
     */
    private void cooldownAbility(double cooldownModifier) {
      if (baseCooldown > 0) {
        setOnCooldown(true);
        int cooldown = (int) Math.max(1, baseCooldown - (baseCooldown * cooldownModifier));
        Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> setOnCooldown(false), cooldown);
      }
    }

    /**
     * Sets if the {@link ActiveAbilityType} is on cooldown.
     *
     * @param isOnCooldown is on cooldown
     */
    private void setOnCooldown(boolean isOnCooldown) {
      switch (source) {
        case EQUIPMENT -> {
          if (isOnCooldown) {
            onCooldownActives.get(eSlot).add(type);
          } else {
            onCooldownActives.get(eSlot).remove(type);
          }
        }
        case EDIBLE -> {
          if (isOnCooldown) {
            onCooldownEdibles.add(id);
          } else {
            onCooldownEdibles.remove(id);
          }
        }
      }
    }

    /**
     * Represents an effect's target location or entity validation.
     *
     * @author Danny Nguyen
     * @version 1.23.13
     * @since 1.23.13
     */
    private class TargetValidation {
      /**
       * No parameter constructor.
       */
      TargetValidation() {
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
       * Recursively finds {@link ActiveAbilityType#BEAM} targets.
       *
       * @param world     world
       * @param targets   set of affected targets
       * @param location  current location
       * @param direction caster's direction
       * @param distance  distance in meters to check
       * @return set of forcewave-affected targets
       */
      private Set<LivingEntity> getBeamTargets(World world, Set<LivingEntity> targets, Location location, Vector direction, int distance) {
        if (distance <= 0 || location.getBlock().getType().isSolid()) {
          return targets;
        }
        world.spawnParticle(Particle.CLOUD, location, 2, 0.125, 0.125, 0.125, 0.025);
        for (Entity entity : world.getNearbyEntities(location, 1, 1, 1)) {
          if (entity instanceof LivingEntity livingEntity) {
            targets.add(livingEntity);
          }
        }
        return getBeamTargets(world, targets, location.add(direction).clone(), direction, distance - 1);
      }

      /**
       * Recursively finds the {@link ActiveAbilityType#BULLET} targets.
       *
       * @param caster    ability caster
       * @param world     world
       * @param targets   set of affected targets
       * @param location  current location
       * @param direction caster's direction
       * @param distance  distance in meters to check
       * @return bullet-affected targets
       */
      private Set<LivingEntity> getBulletTargets(Player caster, World world, Set<LivingEntity> targets, Location location, Vector direction, int distance) {
        if (distance <= 0 || location.getBlock().getType().isSolid()) {
          Location impactLocation = location.subtract(direction);
          world.playSound(impactLocation, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.85f, 0.5f);
          world.spawnParticle(Particle.EXPLOSION_LARGE, impactLocation, 1);
          return targets;
        }
        Collection<Entity> entities = world.getNearbyEntities(location, 0.5, 0.5, 0.5);
        switch (entities.size()) {
          case 0 -> {
            return getBulletTargets(caster, world, targets, location.add(direction).clone(), direction, distance - 1);
          }
          case 1 -> {
            if (entities.contains(caster)) {
              return getBulletTargets(caster, world, targets, location.add(direction).clone(), direction, distance - 1);
            }
          }
        }
        for (Entity entity : entities) {
          if (entity instanceof LivingEntity livingEntity) {
            targets.add(livingEntity);
          }
        }
        if (!targets.isEmpty()) {
          world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.85f, 0.5f);
          world.spawnParticle(Particle.EXPLOSION_LARGE, location, 1);
          return targets;
        }
        return getBulletTargets(caster, world, targets, location.add(direction).clone(), direction, distance - 1);
      }
    }

    /**
     * Represents a {@link ActiveAbilityType.Effect#DISPLACEMENT} effect's entity validation.
     *
     * @author Danny Nguyen
     * @version 1.24.0
     * @since 1.24.0
     */
    private class TargetDisplacement {
      /**
       * Ability caster.
       */
      private final Player caster;

      /**
       * Effect modifier.
       */
      private final double modifier;

      /**
       * Maximum range.
       */
      private final double distance;

      /**
       * Ability cast world.
       */
      private final World world;

      /**
       * Caster's body location.
       */
      private final Location casterLocation;

      /**
       * Caster's facing direction.
       */
      private final Vector direction;

      /**
       * Associates the target displacement operation with its caster and ability parameters.
       *
       * @param caster   ability caster
       * @param modifier effect modifier
       * @param distance maximum range
       */
      TargetDisplacement(Player caster, double modifier, double distance) {
        this.caster = caster;
        this.modifier = modifier;
        this.distance = distance;
        this.world = caster.getWorld();
        this.casterLocation = caster.getLocation().add(0, 1, 0);
        this.direction = caster.getLocation().getDirection();
      }

      /**
       * Recursively finds {@link ActiveAbilityType#DRAG} affected targets.
       *
       * @param location          current location
       * @param remainingDistance distance in meters to check
       */
      private void getDragTargets(Location location, int remainingDistance) {
        if (remainingDistance <= 0 || location.getBlock().getType().isSolid()) {
          return;
        }
        world.spawnParticle(Particle.SOUL, location, 1, 0.125, 0.125, 0.125, 0.025);
        for (Entity entity : world.getNearbyEntities(location, 1, 1, 1)) {
          if (!(entity instanceof LivingEntity livingEntity) || livingEntity.equals(caster)) {
            continue;
          }

          Location entityLocation = livingEntity.getLocation().add(0, 1, 0);
          double distanceModifier = (distance - remainingDistance) / distance * modifier;
          Vector velocity = casterLocation.toVector().subtract(entityLocation.toVector()).normalize().multiply(distanceModifier);
          livingEntity.setVelocity(velocity);
        }
        getDragTargets(location.add(direction).clone(), remainingDistance - 1);
      }

      /**
       * Recursively finds {@link ActiveAbilityType#THRUST} affected targets.
       *
       * @param location          current location
       * @param remainingDistance distance in meters to check
       */
      private void getThrustTargets(Location location, int remainingDistance) {
        if (remainingDistance <= 0 || location.getBlock().getType().isSolid()) {
          return;
        }
        world.spawnParticle(Particle.SCULK_SOUL, location, 1, 0.125, 0.125, 0.125, 0.025);
        for (Entity entity : world.getNearbyEntities(location, 1, 1, 1)) {
          if (!(entity instanceof LivingEntity livingEntity) || livingEntity.equals(caster)) {
            continue;
          }

          Location entityLocation = livingEntity.getLocation().add(0, 1, 0);
          double distanceModifier = remainingDistance / distance * modifier;
          Vector velocity = entityLocation.toVector().subtract(casterLocation.toVector()).normalize().multiply(distanceModifier);
          livingEntity.setVelocity(velocity);
        }
        getThrustTargets(location.add(direction).clone(), remainingDistance - 1);
      }
    }

    /**
     * Represents a {@link ActiveAbilityType.Effect#TELEPORT}
     * effect's target location or entity validation.
     *
     * @author Danny Nguyen
     * @version 1.24.4
     * @since 1.24.1
     */
    private class TargetTeleport {
      /**
       * Ability caster.
       */
      private final Player caster;

      /**
       * Caster's direction.
       */
      private final Vector direction;

      /**
       * Associates the target teleport operation with a caster.
       *
       * @param caster ability caster
       */
      TargetTeleport(Player caster) {
        this.caster = caster;
        this.direction = caster.getLocation().getDirection();
      }

      /**
       * Gets if the teleport action can proceed through the next block.
       *
       * @param validTeleportLocation valid teleport location
       * @param location              current location
       * @param distance              number of meters to check
       * @return the furthest valid teleport location
       */
      private Location ifValidTeleportThroughBlock(Location validTeleportLocation, Location location, int distance) {
        Material blockType = location.getBlock().getType();
        if (distance <= 0 || blockType == Material.BEDROCK || blockType == Material.BARRIER) {
          return validTeleportLocation;
        }
        if (!blockType.isSolid()) {
          validTeleportLocation = location.clone();
        }
        return ifValidTeleportThroughBlock(validTeleportLocation, location.add(direction), distance - 1);
      }

      /**
       * Recursively finds the teleport affected target.
       *
       * @param world    world
       * @param location current location
       * @param distance distance in meters to check
       * @return teleport target
       */
      private LivingEntity getTeleportTarget(World world, Location location, int distance) {
        Material blockType = location.getBlock().getType();
        if (distance <= 0 || blockType == Material.BEDROCK || blockType == Material.BARRIER) {
          return null;
        }
        for (Entity entity : world.getNearbyEntities(location, 1, 1, 1)) {
          if (!(entity instanceof LivingEntity livingEntity) || livingEntity.equals(caster)) {
            continue;
          }
          return livingEntity;
        }
        return getTeleportTarget(world, location.add(direction).clone(), distance - 1);
      }
    }
  }

  /**
   * Represents an ability's source.
   */
  public enum Source {
    /**
     * {@link me.dannynguyen.aethel.rpg.Equipment}
     */
    EQUIPMENT,

    /**
     * Edible.
     */
    EDIBLE
  }
}
