package me.dannynguyen.aethel;

import me.dannynguyen.aethel.commands.DeveloperCommand;
import me.dannynguyen.aethel.commands.PingCommand;
import me.dannynguyen.aethel.commands.StatusCommand;
import me.dannynguyen.aethel.commands.TagCommand;
import me.dannynguyen.aethel.commands.aethelitem.ItemCommand;
import me.dannynguyen.aethel.commands.character.CharacterCommand;
import me.dannynguyen.aethel.commands.forge.ForgeCommand;
import me.dannynguyen.aethel.commands.itemeditor.ItemEditorCommand;
import me.dannynguyen.aethel.commands.playerstat.StatCommand;
import me.dannynguyen.aethel.commands.showitem.ShowItemCommand;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.enums.rpg.StatusType;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType;
import me.dannynguyen.aethel.listeners.*;
import me.dannynguyen.aethel.rpg.*;
import me.dannynguyen.aethel.rpg.abilities.Abilities;
import me.dannynguyen.aethel.rpg.abilities.PassiveAbility;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents the plugin as an object.
 * <p>
 * Through event listeners and command executors, the plugin can
 * handle various requests given to it by its users and the server.
 *
 * @author Danny Nguyen
 * @version 1.19.7
 * @since 1.0.0
 */
public class Plugin extends JavaPlugin {
  /**
   * Plugin data.
   */
  private static final PluginData data = new PluginData();

  /**
   * No parameter constructor.
   */
  public Plugin() {
  }

  /**
   * On enable:
   * <ul>
   *  <li>{@link PluginData#loadResources() Loads} existing plugin data.
   *  <li>{@link #registerCommands() Registers} commands.
   *  <li>{@link #registerEventListeners() Registers} event listeners.
   * </ul>
   */
  @Override
  public void onEnable() {
    data.loadResources();
    registerEventListeners();
    registerCommands();
    scheduleRepeatingTasks();
  }

  /**
   * On disable:
   * <ul>
   *  <li>{@link PluginData#saveResources() Saves} persistent plugin data.
   * </ul>
   */
  @Override
  public void onDisable() {
    Bukkit.getScheduler().cancelTasks(this);
    data.saveResources();
  }

  /**
   * Registers the plugin's event listeners.
   * <ul>
   *  <li>{@link ActionEvent}
   *  <li>{@link MenuEvent}
   *  <li>{@link MessageEvent}
   *  <li>{@link PluginEvent}
   *  <li>{@link HealthEvent}
   *  <li>{@link EquipmentEvent}
   *  <li>{@link RpgEvent}
   *  <li>{@link StatusEvent}
   * </ul>
   */
  private void registerEventListeners() {
    PluginManager manager = getServer().getPluginManager();
    manager.registerEvents(new ActionEvent(), this);
    manager.registerEvents(new MenuEvent(), this);
    manager.registerEvents(new MessageEvent(), this);
    manager.registerEvents(new PluginEvent(), this);
    manager.registerEvents(new HealthEvent(), this);
    manager.registerEvents(new EquipmentEvent(), this);
    manager.registerEvents(new RpgEvent(), this);
    manager.registerEvents(new StatusEvent(), this);
  }

  /**
   * Registers the plugin's commands.
   * <ul>
   *  <li>{@link ItemCommand}
   *  <li>{@link TagCommand}
   *  <li>{@link CharacterCommand}
   *  <li>{@link DeveloperCommand}
   *  <li>{@link ForgeCommand}
   *  <li>{@link ItemEditorCommand}
   *  <li>{@link PingCommand}
   *  <li>{@link ShowItemCommand}
   *  <li>{@link StatusCommand}
   *  <li>{@link StatCommand}
   * </ul>
   */
  private void registerCommands() {
    this.getCommand("aethelitem").setExecutor(new ItemCommand());
    this.getCommand("aetheltag").setExecutor(new TagCommand());
    this.getCommand("character").setExecutor(new CharacterCommand());
    this.getCommand("developermode").setExecutor(new DeveloperCommand());
    this.getCommand("forge").setExecutor(new ForgeCommand());
    this.getCommand("itemeditor").setExecutor(new ItemEditorCommand());
    this.getCommand("ping").setExecutor(new PingCommand());
    this.getCommand("showitem").setExecutor(new ShowItemCommand());
    this.getCommand("status").setExecutor(new StatusCommand());
    this.getCommand("playerstat").setExecutor(new StatCommand());
  }

  /**
   * Schedules the plugin's repeating tasks.
   * <ul>
   *  <li>{@link #updateMainHandEquipment()}
   *  <li>{@link #updateDamageOverTimeStatuses()}
   *  <li>{@link #updateOvershields()}
   *  <li>{@link #updateBelowHealthPassives()}
   *  <li>{@link #updateActionDisplay()}
   *  <li>{@link #updateEnvironmentalProtections()}
   * </ul>
   */
  private void scheduleRepeatingTasks() {
    BukkitScheduler scheduler = Bukkit.getScheduler();
    scheduler.scheduleSyncRepeatingTask(this, this::updateMainHandEquipment, 0, 10);
    scheduler.scheduleSyncRepeatingTask(this, this::updateDamageOverTimeStatuses, 0, 20);
    scheduler.scheduleSyncRepeatingTask(this, this::updateOvershields, 0, 20);
    scheduler.scheduleSyncRepeatingTask(this, this::updateBelowHealthPassives, 0, 20);
    scheduler.scheduleSyncRepeatingTask(this, this::updateActionDisplay, 0, 40);
    scheduler.scheduleSyncRepeatingTask(this, this::updateEnvironmentalProtections, 0, 100);
  }

  /**
   * Adds an interval to compare the player's main hand item for updating {@link Equipment}.
   */
  private void updateMainHandEquipment() {
    Map<UUID, RpgPlayer> rpgPlayers = data.getRpgSystem().getRpgPlayers();
    for (UUID uuid : rpgPlayers.keySet()) {
      Player player = Bukkit.getPlayer(uuid);
      if (player != null) {
        Equipment equipment = rpgPlayers.get(uuid).getEquipment();
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (!heldItem.equals(equipment.getHeldItem())) {
          equipment.setHeldItem(heldItem);
          equipment.readSlot(heldItem, RpgEquipmentSlot.HAND, true);
        }
      }
    }
  }

  /**
   * Adds an interval to calculate damage taken from damage over time {@link Status statuses}.
   */
  private void updateDamageOverTimeStatuses() {
    Map<UUID, Map<StatusType, Status>> entityStatuses = data.getRpgSystem().getStatuses();
    for (UUID uuid : entityStatuses.keySet()) {
      Map<StatusType, Status> statuses = entityStatuses.get(uuid);
      if (statuses.containsKey(StatusType.BLEED) || statuses.containsKey(StatusType.ELECTROCUTE)) {
        if (Bukkit.getEntity(uuid) instanceof LivingEntity entity) {
          if (entity instanceof Player player) {
            if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
              dealPlayerDamageOverTime(uuid, statuses, player);
            }
          } else {
            dealEntityDamageOverTime(statuses, entity);
          }
        }
      }
    }
  }

  /**
   * Adds an interval to update players'
   * {@link Health health in action bar} display.
   */
  private void updateActionDisplay() {
    Map<UUID, RpgPlayer> rpgPlayers = data.getRpgSystem().getRpgPlayers();
    for (UUID uuid : rpgPlayers.keySet()) {
      if (Bukkit.getPlayer(uuid) != null) {
        rpgPlayers.get(uuid).getHealth().updateActionDisplay();
      }
    }
  }

  /**
   * Adds an interval to decay players' overcapped {@link Health overshields}.
   * <p>
   * An overshield begins to decay when current health
   * exceeds max health by a factor greater than x1.2.
   */
  private void updateOvershields() {
    Map<UUID, RpgPlayer> rpgPlayers = data.getRpgSystem().getRpgPlayers();
    for (UUID uuid : rpgPlayers.keySet()) {
      if (Bukkit.getPlayer(uuid) != null) {
        rpgPlayers.get(uuid).getHealth().decayOvershield();
      }
    }
  }

  /**
   * Adds an interval to trigger {@link PassiveTriggerType#BELOW_HEALTH} {@link PassiveAbility passive abilities}.
   * <p>
   * {@link PassiveTriggerType#BELOW_HEALTH}{@link PassiveAbility} can only be triggered on self.
   */
  private void updateBelowHealthPassives() {
    for (RpgPlayer rpgPlayer : data.getRpgSystem().getRpgPlayers().values()) {
      Map<Abilities.SlotPassive, PassiveAbility> belowHealthTriggers = rpgPlayer.getAbilities().getTriggerPassives().get(PassiveTriggerType.BELOW_HEALTH);
      if (!belowHealthTriggers.isEmpty()) {
        for (PassiveAbility ability : belowHealthTriggers.values()) {
          if (!ability.isOnCooldown()) {
            double healthPercent = Double.parseDouble(ability.getConditionData().get(0));
            if (rpgPlayer.getHealth().getHealthPercent() <= healthPercent) {
              boolean self = Boolean.parseBoolean(ability.getEffectData().get(0));
              if (self) {
                ability.doEffect(rpgPlayer, rpgPlayer.getUUID());
              }
            }
          }
        }
      }
    }
  }

  /**
   * Adds an interval to apply environmental potion effects
   * to players who've met enchantment level requirements.
   * <ul>
   *  <li>Feather Falling >= 5: Slow Falling
   *  <li>Fire Protection >= 10: Fire Resistance
   * </ul>
   */
  private void updateEnvironmentalProtections() {
    Map<Enchantment, Set<UUID>> sufficientEnchantments = data.getRpgSystem().getSufficientEnchantments();
    for (UUID uuid : sufficientEnchantments.get(Enchantment.PROTECTION_FALL)) {
      Player player = Bukkit.getPlayer(uuid);
      if (player != null) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 101, 0, false));
      }
    }
    for (UUID uuid : sufficientEnchantments.get(Enchantment.PROTECTION_FIRE)) {
      Player player = Bukkit.getPlayer(uuid);
      if (player != null) {
        player.setFireTicks(-20);
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 101, 0, false));
      }
    }
  }

  /**
   * Deals damage from damage over time {@link Status statuses} to players.
   *
   * @param uuid     uuid
   * @param statuses {@link Status statuses}
   * @param damagee  player taking damage
   */
  private void dealPlayerDamageOverTime(UUID uuid, Map<StatusType, Status> statuses, Player damagee) {
    RpgPlayer rpgPlayer = data.getRpgSystem().getRpgPlayers().get(uuid);
    DamageMitigation mitigation = new DamageMitigation(damagee);
    if (statuses.containsKey(StatusType.BLEED)) {
      double damage = statuses.get(StatusType.BLEED).getStackAmount() * 0.2;
      damagee.damage(0.1);
      rpgPlayer.getHealth().damage(mitigation.mitigateProtectionResistance(damage));
    }
    if (statuses.containsKey(StatusType.ELECTROCUTE)) {
      double damage = statuses.get(StatusType.ELECTROCUTE).getStackAmount() * 0.2;
      damagee.damage(0.1);
      rpgPlayer.getHealth().damage(mitigation.mitigateProtectionResistance(damage));
      if (rpgPlayer.getHealth().getCurrentHealth() < 0) {
        propagateElectrocuteStacks(damagee, rpgPlayer.getHealth().getCurrentHealth());
      }
    }
  }

  /**
   * Deals damage from damage over time {@link Status statuses} to entities.
   *
   * @param statuses {@link Status statuses}
   * @param entity   entity taking damage
   */
  private void dealEntityDamageOverTime(Map<StatusType, Status> statuses, LivingEntity entity) {
    if (statuses.containsKey(StatusType.BLEED)) {
      entity.damage(0.1);
      entity.setHealth(Math.max(0, entity.getHealth() + 0.1 - statuses.get(StatusType.BLEED).getStackAmount() * 0.2));
    }
    if (statuses.containsKey(StatusType.ELECTROCUTE)) {
      double remainingHealth = entity.getHealth() + 0.1 - statuses.get(StatusType.ELECTROCUTE).getStackAmount() * 0.2;
      entity.damage(0.1);
      if (remainingHealth > 0) {
        entity.setHealth(remainingHealth);
      } else {
        entity.setHealth(0);
        propagateElectrocuteStacks(entity, remainingHealth);
      }
    }
  }

  /**
   * Propagates remaining electrocute stacks to nearby targets.
   *
   * @param sourceEntity    source entity that died
   * @param remainingHealth negative health value
   */
  private void propagateElectrocuteStacks(LivingEntity sourceEntity, double remainingHealth) {
    Set<LivingEntity> nearbyLivingEntities = new HashSet<>();
    for (Entity entity : sourceEntity.getNearbyEntities(4, 4, 4)) {
      if (entity instanceof LivingEntity livingEntity) {
        nearbyLivingEntities.add(livingEntity);
      }
    }

    if (!nearbyLivingEntities.isEmpty()) {
      double remainingStacks = Math.abs(remainingHealth / 0.2);
      int appliedStacks = (int) Math.max(1, remainingStacks / nearbyLivingEntities.size());
      Map<UUID, Map<StatusType, Status>> entityStatuses = Plugin.getData().getRpgSystem().getStatuses();
      for (LivingEntity livingEntity : nearbyLivingEntities) {
        UUID uuid = livingEntity.getUniqueId();
        if (!entityStatuses.containsKey(uuid)) {
          entityStatuses.put(uuid, new HashMap<>());
        }
        Map<StatusType, Status> statuses = entityStatuses.get(uuid);
        if (statuses.containsKey(StatusType.ELECTROCUTE)) {
          statuses.get(StatusType.ELECTROCUTE).addStacks(appliedStacks, 60);
        } else {
          statuses.put(StatusType.ELECTROCUTE, new Status(uuid, StatusType.ELECTROCUTE, appliedStacks, 60));
        }
      }
    }
  }

  /**
   * Gets the {@link PluginData}.
   *
   * @return {@link PluginData}
   */
  @NotNull
  public static PluginData getData() {
    return data;
  }

  /**
   * Gets the plugin.
   *
   * @return plugin instance
   */
  @NotNull
  public static Plugin getInstance() {
    return getPlugin(Plugin.class);
  }
}
