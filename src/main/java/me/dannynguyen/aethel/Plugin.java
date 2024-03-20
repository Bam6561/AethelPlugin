package me.dannynguyen.aethel;

import me.dannynguyen.aethel.commands.DeveloperModeCommand;
import me.dannynguyen.aethel.commands.PingCommand;
import me.dannynguyen.aethel.commands.StatusCommand;
import me.dannynguyen.aethel.commands.aethelitem.ItemCommand;
import me.dannynguyen.aethel.commands.aetheltag.AethelTagCommand;
import me.dannynguyen.aethel.commands.character.CharacterCommand;
import me.dannynguyen.aethel.commands.forge.ForgeCommand;
import me.dannynguyen.aethel.commands.itemeditor.ItemEditorCommand;
import me.dannynguyen.aethel.commands.playerstat.PlayerStatCommand;
import me.dannynguyen.aethel.commands.showitem.ShowItemCommand;
import me.dannynguyen.aethel.plugin.listeners.MenuClick;
import me.dannynguyen.aethel.plugin.listeners.MessageSent;
import me.dannynguyen.aethel.plugin.listeners.PluginEvent;
import me.dannynguyen.aethel.plugin.system.PluginData;
import me.dannynguyen.aethel.rpg.ability.PassiveAbility;
import me.dannynguyen.aethel.rpg.ability.PassiveAbilityTrigger;
import me.dannynguyen.aethel.rpg.ability.SlotPassiveType;
import me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot;
import me.dannynguyen.aethel.rpg.enums.StatusType;
import me.dannynguyen.aethel.rpg.enums.Trigger;
import me.dannynguyen.aethel.rpg.listeners.*;
import me.dannynguyen.aethel.rpg.system.Equipment;
import me.dannynguyen.aethel.rpg.system.PlayerDamageMitigation;
import me.dannynguyen.aethel.rpg.system.RpgPlayer;
import me.dannynguyen.aethel.rpg.system.Status;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Represents the plugin as an object.
 * <p>
 * Through event listeners and command executors, the plugin can
 * handle various requests given to it by its users and the server.
 *
 * @author Danny Nguyen
 * @version 1.17.9
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
   * <p><ul>
   * <li>{@link PluginData#loadResources() Loads} existing plugin data.
   * <li>{@link #registerCommands() Registers} commands.
   * <li>{@link #registerEventListeners() Registers} event listeners.
   * </ul></p>
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
   * <p><ul>
   * <li>{@link PluginData#saveResources() Saves} persistent plugin data.
   * </ul></p>
   */
  @Override
  public void onDisable() {
    Bukkit.getScheduler().cancelTasks(this);
    data.saveResources();
  }

  /**
   * Registers the plugin's event listeners.
   * <p><ul>
   * <li>{@link Crouch}
   * <li>{@link MenuClick}
   * <li>{@link MessageSent}
   * <li>{@link PluginEvent}
   * <li>{@link EntityDamage}
   * <li>{@link EquipmentUpdate}
   * <li>{@link RpgEvent}
   * <li>{@link StatusUpdate}
   * </ul></p>
   */
  private void registerEventListeners() {
    PluginManager manager = getServer().getPluginManager();
    manager.registerEvents(new Crouch(), this);
    manager.registerEvents(new MenuClick(), this);
    manager.registerEvents(new MessageSent(), this);
    manager.registerEvents(new PluginEvent(), this);
    manager.registerEvents(new EntityDamage(), this);
    manager.registerEvents(new EquipmentUpdate(), this);
    manager.registerEvents(new RpgEvent(), this);
    manager.registerEvents(new StatusUpdate(), this);
  }

  /**
   * Registers the plugin's commands.
   * <p><ul>
   * <li>{@link ItemCommand}
   * <li>{@link AethelTagCommand}
   * <li>{@link CharacterCommand}
   * <li>{@link DeveloperModeCommand}
   * <li>{@link ForgeCommand}
   * <li>{@link ItemEditorCommand}
   * <li>{@link PingCommand}
   * <li>{@link ShowItemCommand}
   * <li>{@link StatusCommand}
   * <li>{@link PlayerStatCommand}
   * </ul></p>
   */
  private void registerCommands() {
    this.getCommand("aethelitem").setExecutor(new ItemCommand());
    this.getCommand("aetheltag").setExecutor(new AethelTagCommand());
    this.getCommand("character").setExecutor(new CharacterCommand());
    this.getCommand("developermode").setExecutor(new DeveloperModeCommand());
    this.getCommand("forge").setExecutor(new ForgeCommand());
    this.getCommand("itemeditor").setExecutor(new ItemEditorCommand());
    this.getCommand("ping").setExecutor(new PingCommand());
    this.getCommand("showitem").setExecutor(new ShowItemCommand());
    this.getCommand("status").setExecutor(new StatusCommand());
    this.getCommand("playerstat").setExecutor(new PlayerStatCommand());
  }

  /**
   * Schedules the plugin's repeating tasks.
   * <p><ul>
   * <li>{@link #updateMainHandEquipmentAttributes()}
   * <li>{@link #updateDamageOverTimeStatuses()}
   * <li>{@link #updateOvershields()}
   * <li>{@link #updateBelowHealthPassives()}
   * <li>{@link #updateActionDisplay()}
   * <li>{@link #updateEnvironmentalProtections()}
   * </ul></p>
   */
  private void scheduleRepeatingTasks() {
    BukkitScheduler scheduler = Bukkit.getScheduler();
    scheduler.scheduleSyncRepeatingTask(this, this::updateMainHandEquipmentAttributes, 0, 10);
    scheduler.scheduleSyncRepeatingTask(this, this::updateDamageOverTimeStatuses, 0, 20);
    scheduler.scheduleSyncRepeatingTask(this, this::updateOvershields, 0, 20);
    scheduler.scheduleSyncRepeatingTask(this, this::updateBelowHealthPassives, 0, 20);
    scheduler.scheduleSyncRepeatingTask(this, this::updateActionDisplay, 0, 40);
    scheduler.scheduleSyncRepeatingTask(this, this::updateEnvironmentalProtections, 0, 100);
  }

  /**
   * Adds an interval to compare the player's main hand item for updating {@link Equipment}.
   */
  private void updateMainHandEquipmentAttributes() {
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
              handlePlayerDamageOverTime(uuid, statuses, player);
            }
          } else {
            handleEntityDamageOverTime(statuses, entity);
          }
        }
      }
    }
  }

  /**
   * Adds an interval to update players'
   * {@link me.dannynguyen.aethel.rpg.system.Health health in action bar} display.
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
   * Adds an interval to decay players' overcapped {@link me.dannynguyen.aethel.rpg.system.Health overshields}.
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
   * Adds an interval to trigger {@link Trigger#BELOW_HEALTH} {@link PassiveAbility passive abilities}.
   */
  private void updateBelowHealthPassives() {
    for (RpgPlayer rpgPlayer : data.getRpgSystem().getRpgPlayers().values()) {
      Map<SlotPassiveType, PassiveAbility> belowHealthTriggers = rpgPlayer.getAbilities().getTriggerPassives().get(Trigger.BELOW_HEALTH);
      if (!belowHealthTriggers.isEmpty()) {
        for (PassiveAbility ability : belowHealthTriggers.values()) {
          if (!ability.isOnCooldown()) {
            switch (ability.getType().getEffect()) {
              case STACK_INSTANCE -> readBelowHealthStackInstance(ability, rpgPlayer);
              case CHAIN_DAMAGE -> readBelowHealthChainDamage(ability, rpgPlayer);
            }
          }
        }
      }
    }
  }

  /**
   * Adds an interval to apply environmental potion effects
   * to players who've met enchantment level requirements.
   * <p><ul>
   * <li>Feather Falling >= 5: Slow Falling
   * <li>Fire Protection >= 10: Fire Resistance
   * </ul></p>
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
  private void handlePlayerDamageOverTime(UUID uuid, Map<StatusType, Status> statuses, Player damagee) {
    RpgPlayer rpgPlayer = data.getRpgSystem().getRpgPlayers().get(uuid);
    PlayerDamageMitigation mitigation = new PlayerDamageMitigation(damagee);
    if (statuses.containsKey(StatusType.BLEED)) {
      double damage = statuses.get(StatusType.BLEED).getStackAmount() * 0.2;
      damagee.damage(0.1);
      rpgPlayer.getHealth().damage(mitigation.mitigateProtectionResistance(damage));
    }
    if (statuses.containsKey(StatusType.ELECTROCUTE)) {
      double damage = statuses.get(StatusType.ELECTROCUTE).getStackAmount() * 0.2;
      damagee.damage(0.1);
      rpgPlayer.getHealth().damage(mitigation.mitigateProtectionResistance(damage));
    }
  }

  /**
   * Deals damage from damage over time {@link Status statuses} to entities.
   *
   * @param statuses {@link Status statuses}
   * @param entity   entity taking damage
   */
  private void handleEntityDamageOverTime(Map<StatusType, Status> statuses, LivingEntity entity) {
    if (statuses.containsKey(StatusType.BLEED)) {
      entity.damage(0.1);
      entity.setHealth(Math.max(0, entity.getHealth() + 0.1 - statuses.get(StatusType.BLEED).getStackAmount() * 0.2));
    }
    if (statuses.containsKey(StatusType.ELECTROCUTE)) {
      entity.damage(0.1);
      entity.setHealth(Math.max(0, entity.getHealth() + 0.1 - statuses.get(StatusType.ELECTROCUTE).getStackAmount() * 0.2));
    }
  }

  /**
   * Checks if the {@link me.dannynguyen.aethel.rpg.enums.PassiveAbilityEffect#STACK_INSTANCE}
   * was successful before applying stack instances.
   *
   * @param ability   passive ability
   * @param rpgPlayer interacting player
   */
  private void readBelowHealthStackInstance(PassiveAbility ability, RpgPlayer rpgPlayer) {
    double healthPercent = Double.parseDouble(ability.getConditionData().get(0));
    if (rpgPlayer.getHealth().getHealthPercent() <= healthPercent) {
      boolean self = Boolean.parseBoolean(ability.getEffectData().get(0));
      if (self) {
        new PassiveAbilityTrigger(ability).applyStackInstance(rpgPlayer.getUUID());
      }
    }
  }

  /**
   * Checks if the {@link me.dannynguyen.aethel.rpg.enums.PassiveAbilityEffect#CHAIN_DAMAGE}
   * was successful before dealing chain damage.
   *
   * @param ability   passive ability
   * @param rpgPlayer interacting player
   */
  private void readBelowHealthChainDamage(PassiveAbility ability, RpgPlayer rpgPlayer) {
    double healthPercent = Double.parseDouble(ability.getConditionData().get(0));
    if (rpgPlayer.getHealth().getHealthPercent() <= healthPercent) {
      boolean self = Boolean.parseBoolean(ability.getEffectData().get(0));
      if (self) {
        new PassiveAbilityTrigger(ability).chainDamage(rpgPlayer.getUUID());
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
