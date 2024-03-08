package me.dannynguyen.aethel;

import me.dannynguyen.aethel.commands.DeveloperModeCommand;
import me.dannynguyen.aethel.commands.PingCommand;
import me.dannynguyen.aethel.commands.status.StatusCommand;
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
import me.dannynguyen.aethel.listeners.rpg.EquipmentAttributes;
import me.dannynguyen.aethel.listeners.rpg.PlayerDamage;
import me.dannynguyen.aethel.listeners.rpg.RpgEvent;
import me.dannynguyen.aethel.systems.plugin.PluginData;
import me.dannynguyen.aethel.systems.rpg.RpgEquipment;
import me.dannynguyen.aethel.systems.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.systems.rpg.RpgSystem;
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
 * @version 1.14.8
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
    this.getCommand("status").setExecutor(new StatusCommand());
    this.getCommand("playerstat").setExecutor(new PlayerStatCommand());
  }

  /**
   * Schedules the plugin's repeating tasks.
   */
  private void scheduleRepeatingTasks() {
    BukkitScheduler scheduler = Bukkit.getScheduler();
    scheduler.scheduleSyncRepeatingTask(this, this::updateMainHandEquipmentAttributes, 0, 10);
    scheduler.scheduleSyncRepeatingTask(this, this::updateOvershields, 0, 20);
    scheduler.scheduleSyncRepeatingTask(this, this::updateActionDisplay, 0, 40);
    scheduler.scheduleSyncRepeatingTask(this, this::updateEnvironmentalProtections, 0, 100);
  }

  /**
   * Adds an interval to compare the player's main hand item for updating equipment attributes.
   */
  private void updateMainHandEquipmentAttributes() {
    RpgSystem rpgSystem = data.getRpgSystem();
    for (UUID uuid : rpgSystem.getRpgPlayers().keySet()) {
      Player player = Bukkit.getPlayer(uuid);
      if (player != null) {
        RpgEquipment equipment = rpgSystem.getRpgPlayers().get(uuid).getEquipment();
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (!heldItem.equals(equipment.getHeldItem())) {
          equipment.setHeldItem(heldItem);
          equipment.readSlot(heldItem, RpgEquipmentSlot.HAND, true);
        }
      }
    }
  }

  /**
   * Adds an interval to update players' action bar health display.
   */
  private void updateActionDisplay() {
    RpgSystem rpgSystem = data.getRpgSystem();
    for (UUID uuid : rpgSystem.getRpgPlayers().keySet()) {
      if (Bukkit.getPlayer(uuid) != null) {
        rpgSystem.getRpgPlayers().get(uuid).getHealth().updateActionDisplay();
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
    RpgSystem rpgSystem = data.getRpgSystem();
    for (UUID uuid : rpgSystem.getRpgPlayers().keySet()) {
      if (Bukkit.getPlayer(uuid) != null) {
        rpgSystem.getRpgPlayers().get(uuid).getHealth().decayOvershield();
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
   * Gets the plugin's data.
   *
   * @return plugin data
   */
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
