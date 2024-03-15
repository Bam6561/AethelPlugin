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
import me.dannynguyen.aethel.listeners.plugin.MenuClick;
import me.dannynguyen.aethel.listeners.plugin.MessageSent;
import me.dannynguyen.aethel.listeners.plugin.PluginEvent;
import me.dannynguyen.aethel.listeners.rpg.EntityDamage;
import me.dannynguyen.aethel.listeners.rpg.EquipmentAttributes;
import me.dannynguyen.aethel.listeners.rpg.RpgEvent;
import me.dannynguyen.aethel.systems.plugin.PluginData;
import me.dannynguyen.aethel.systems.rpg.*;
import org.bukkit.Bukkit;
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
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.14.12
 * @since 1.0.0
 */
public class Plugin extends JavaPlugin {
  /**
   * Plugin data.
   */
  private static final PluginData data = new PluginData();

  /**
   * On enable:
   * - Loads existing plugin data.
   * - Registers event listeners.
   * - Registers commands.
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
   * - Saves persistent plugin data.
   */
  @Override
  public void onDisable() {
    Bukkit.getScheduler().cancelTasks(this);
    data.saveResources();
  }

  /**
   * Registers the plugin's event listeners.
   */
  private void registerEventListeners() {
    PluginManager manager = getServer().getPluginManager();
    manager.registerEvents(new EquipmentAttributes(), this);
    manager.registerEvents(new EntityDamage(), this);
    manager.registerEvents(new MenuClick(), this);
    manager.registerEvents(new MessageSent(), this);
    manager.registerEvents(new PluginEvent(), this);
    manager.registerEvents(new RpgEvent(), this);
  }

  /**
   * Registers the plugin's commands.
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
   */
  private void scheduleRepeatingTasks() {
    BukkitScheduler scheduler = Bukkit.getScheduler();
    scheduler.scheduleSyncRepeatingTask(this, this::updateMainHandEquipmentAttributes, 0, 10);
    scheduler.scheduleSyncRepeatingTask(this, this::updateDamageOverTimeStatuses, 0, 20);
    scheduler.scheduleSyncRepeatingTask(this, this::updateOvershields, 0, 20);
    scheduler.scheduleSyncRepeatingTask(this, this::updateActionDisplay, 0, 40);
    scheduler.scheduleSyncRepeatingTask(this, this::updateEnvironmentalProtections, 0, 100);
  }

  /**
   * Adds an interval to compare the player's main hand item for updating equipment attributes.
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
   * Adds an interval to calculate damage taken from damage over time statuses.
   */
  private void updateDamageOverTimeStatuses() {
    Map<UUID, Map<StatusType, Status>> entityStatuses = data.getRpgSystem().getStatuses();
    for (UUID uuid : entityStatuses.keySet()) {
      Map<StatusType, Status> statuses = entityStatuses.get(uuid);
      if (statuses.containsKey(StatusType.BLEED) || statuses.containsKey(StatusType.ELECTROCUTE)) {
        if (Bukkit.getEntity(uuid) instanceof LivingEntity entity) {
          if (entity instanceof Player player) {
            handlePlayerDamageOverTime(uuid, statuses, player);
          } else {
            handleEntityDamageOverTime(uuid, statuses, entity);
          }
        }
      }
    }
  }

  /**
   * Adds an interval to update players' action bar health display.
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
   * Adds an interval to decay players' overcapped overshields.
   * <p>
   * An overshield begins to decay when current health
   * exceeds max health by a factor greater than x1.2.
   * </p>
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
   * Adds an interval to apply environmental potion effects
   * to players who've met enchantment level requirements.
   * <p>
   * - Feather Falling > 5: Slow falling effect
   * - Fire Protection > 10: Fire resistance effect
   * </p>
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
   * Deals damage from damage over time statuses to players.
   *
   * @param uuid     player uuid
   * @param statuses player statuses
   * @param player   interacting player
   */
  private void handlePlayerDamageOverTime(UUID uuid, Map<StatusType, Status> statuses, Player player) {
    RpgPlayer rpgPlayer = data.getRpgSystem().getRpgPlayers().get(uuid);
    int protection = Math.min(rpgPlayer.getEquipment().getTotalEnchantments().get(Enchantment.PROTECTION_ENVIRONMENTAL), 20);
    int resistance = 0;
    if (player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
      resistance = player.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE).getAmplifier() + 1;
    }
    if (statuses.containsKey(StatusType.BLEED)) {
      double damage = statuses.get(StatusType.BLEED).getStackAmount() * 0.2;
      player.damage(0.1);
      rpgPlayer.getHealth().damage(mitigateDamageOverTime(damage, protection, resistance));
    }
    if (statuses.containsKey(StatusType.ELECTROCUTE)) {
      double damage = statuses.get(StatusType.ELECTROCUTE).getStackAmount() * 0.2;
      player.damage(0.1);
      rpgPlayer.getHealth().damage(mitigateDamageOverTime(damage, protection, resistance));
    }
  }

  /**
   * Deals damage from damage over time statuses to entities.
   *
   * @param uuid     entity uuid
   * @param statuses entity statuses
   * @param entity   interacting entity
   */
  private void handleEntityDamageOverTime(UUID uuid, Map<StatusType, Status> statuses, LivingEntity entity) {
    if (statuses.containsKey(StatusType.BLEED)) {
      entity.damage(0.1);
      entity.setHealth(entity.getHealth() + 0.1 - statuses.get(StatusType.BLEED).getStackAmount() * 0.2);
    }
    if (statuses.containsKey(StatusType.ELECTROCUTE)) {
      entity.damage(0.1);
      entity.setHealth(entity.getHealth() + 0.1 - statuses.get(StatusType.ELECTROCUTE).getStackAmount() * 0.2);
    }
  }

  /**
   * Mitigates damage over time damage based on protection and resistance levels.
   *
   * @param damage     damage
   * @param protection protection levels
   * @param resistance resistance levels
   * @return damage taken
   */
  private double mitigateDamageOverTime(double damage, int protection, int resistance) {
    damage = damage - (damage * (protection * 0.04));
    damage = damage - (damage * (resistance * 0.05));
    return damage;
  }

  /**
   * Gets the plugin's data.
   *
   * @return plugin data
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
