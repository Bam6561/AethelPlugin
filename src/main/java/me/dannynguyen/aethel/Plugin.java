package me.dannynguyen.aethel;

import me.dannynguyen.aethel.commands.aetheltag.AethelTagCommand;
import me.dannynguyen.aethel.commands.DeveloperModeCommand;
import me.dannynguyen.aethel.commands.PingCommand;
import me.dannynguyen.aethel.commands.aethelitem.ItemCommand;
import me.dannynguyen.aethel.commands.character.CharacterCommand;
import me.dannynguyen.aethel.commands.forge.ForgeCommand;
import me.dannynguyen.aethel.commands.itemeditor.ItemEditorCommand;
import me.dannynguyen.aethel.commands.playerstat.PlayerStatCommand;
import me.dannynguyen.aethel.commands.showitem.ShowItemCommand;
import me.dannynguyen.aethel.listeners.plugin.MenuClick;
import me.dannynguyen.aethel.listeners.plugin.MessageSent;
import me.dannynguyen.aethel.listeners.plugin.PluginEvent;
import me.dannynguyen.aethel.listeners.rpg.EquipmentAttributes;
import me.dannynguyen.aethel.listeners.rpg.PlayerDamage;
import me.dannynguyen.aethel.listeners.rpg.RpgEvent;
import me.dannynguyen.aethel.systems.plugin.PluginData;
import me.dannynguyen.aethel.systems.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.systems.rpg.RpgHealthBar;
import me.dannynguyen.aethel.systems.rpg.RpgPlayer;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

/**
 * Represents the plugin as an object.
 * <p>
 * Through event listeners and command executors, the plugin can
 * handle various requests given to it by its users and the server.
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.13.9
 * @since 1.0.0
 */
public class Plugin extends JavaPlugin {
  /**
   * On enable:
   * - Loads existing plugin data.
   * - Registers event listeners.
   * - Registers commands.
   */
  @Override
  public void onEnable() {
    PluginData.loadResources();
    registerEventListeners();
    registerCommands();
    scheduleRepeatingTasks();
  }

  /**
   * On disable:
   * - Saves persistent plugin data.
   */
  public void onDisable() {
    Bukkit.getScheduler().cancelTasks(this);
    PluginData.saveResources();
  }

  /**
   * Registers the plugin's event listeners.
   */
  private void registerEventListeners() {
    PluginManager manager = getServer().getPluginManager();
    manager.registerEvents(new EquipmentAttributes(), this);
    manager.registerEvents(new PlayerDamage(), this);
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
    this.getCommand("playerstat").setExecutor(new PlayerStatCommand());
  }

  /**
   * Schedules the plugin's repeating tasks.
   */
  private void scheduleRepeatingTasks() {
    BukkitScheduler scheduler = Bukkit.getScheduler();
    scheduler.scheduleSyncRepeatingTask(this, this::updateMainHandEquipmentAttributes, 0, 10);
    scheduler.scheduleSyncRepeatingTask(this, this::updateOvershields, 0, 20);
    scheduler.scheduleSyncRepeatingTask(this, this::updateEnvironmentalProtections, 0, 100);
  }

  /**
   * Adds an interval to store the player's main hand item into memory for future comparisons.
   */
  private void updateMainHandEquipmentAttributes() {
    Map<UUID, ItemStack> playerHeldItemMap = PluginData.rpgSystem.getPlayerHeldItemMap();
    for (Player player : Bukkit.getOnlinePlayers()) {
      ItemStack heldItem = player.getInventory().getItemInMainHand();
      UUID playerUUID = player.getUniqueId();
      if (playerHeldItemMap.containsKey(playerUUID)) {
        if (!playerHeldItemMap.get(playerUUID).equals(heldItem)) {
          playerHeldItemMap.put(playerUUID, heldItem);
          PluginData.rpgSystem.getRpgPlayers().get(playerUUID).getEquipment().readSlot(heldItem, RpgEquipmentSlot.HAND);
        }
      } else {
        playerHeldItemMap.put(playerUUID, heldItem);
      }
    }
  }

  /**
   * Adds an interval to check whether a player's current health decays.
   * <p>
   * Overshields (current health > maximum health) are capped to x1.2 a
   * player's maximum health before it starts decaying every second.
   * </p>
   */
  private void updateOvershields() {
    Map<UUID, RpgPlayer> rpgProfiles = PluginData.rpgSystem.getRpgPlayers();
    for (Player player : Bukkit.getOnlinePlayers()) {
      RpgHealthBar rpgHealthBar = rpgProfiles.get(player.getUniqueId()).getHealthBar();
      if (rpgHealthBar.getCurrentHealth() > rpgHealthBar.getMaxHealth() * 1.2) {
        rpgHealthBar.decayOvershield();
      }
    }
  }

  /**
   * Adds an interval to apply environmental protections
   * based on the player's equipment enchantment levels.
   * <p>
   * - Feather Falling > 5: Slow falling effect
   * - Fire Protection > 10: Fire resistance effect
   * </p>
   */
  private void updateEnvironmentalProtections() {
    Map<UUID, RpgPlayer> rpgProfiles = PluginData.rpgSystem.getRpgPlayers();
    for (Player player : Bukkit.getOnlinePlayers()) {
      Map<Enchantment, Integer> enchantments = rpgProfiles.get(player.getUniqueId()).getEquipment().getTotalEnchantments();
      if (enchantments.get(Enchantment.PROTECTION_FALL) >= 5) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 101, 0, false));
      }
      if (enchantments.get(Enchantment.PROTECTION_FIRE) >= 10) {
        player.setFireTicks(-20);
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 101, 0, false));
      }
    }
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
