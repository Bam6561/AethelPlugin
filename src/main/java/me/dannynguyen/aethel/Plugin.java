package me.dannynguyen.aethel;

import me.dannynguyen.aethel.commands.AethelTagsCommand;
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
import me.dannynguyen.aethel.systems.rpg.EquipmentSlot;
import me.dannynguyen.aethel.systems.rpg.RpgProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

/**
 * Represents the plugin as an object.
 * <p>
 * Through event listeners and command executors, the plugin can
 * process various requests given to it by its users and the server.
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.12.2
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
    this.getCommand("aetheltags").setExecutor(new AethelTagsCommand());
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
    scheduler.scheduleSyncRepeatingTask(this, this::updateOvershield, 0, 20);
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
          PluginData.rpgSystem.getRpgProfiles().get(playerUUID).readEquipmentSlot(heldItem, EquipmentSlot.HAND);
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
  private void updateOvershield() {
    Map<UUID, RpgProfile> rpgProfiles = PluginData.rpgSystem.getRpgProfiles();
    for (Player player : Bukkit.getOnlinePlayers()) {
      RpgProfile rpgProfile = rpgProfiles.get(player.getUniqueId());
      if (rpgProfile.getCurrentHealth() > rpgProfile.getMaxHealth() * 1.2) {
        rpgProfile.decayOvershield();
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
