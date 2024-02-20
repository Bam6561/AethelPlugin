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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
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
 * @version 1.12.0
 * @since 1.0.0
 */
public class Plugin extends JavaPlugin {
  /**
   * On startup:
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
   * Registers the plugin's event listeners.
   */
  private void registerEventListeners() {
    getServer().getPluginManager().registerEvents(new EquipmentAttributes(), this);
    getServer().getPluginManager().registerEvents(new PlayerDamage(), this);
    getServer().getPluginManager().registerEvents(new MenuClick(), this);
    getServer().getPluginManager().registerEvents(new MessageSent(), this);
    getServer().getPluginManager().registerEvents(new PluginEvent(), this);
    getServer().getPluginManager().registerEvents(new RpgEvent(), this);
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
    updateMainHandEquipmentAttributes();
  }

  /**
   * Adds an interval to store the player's main hand item into memory for future comparisons.
   */
  private void updateMainHandEquipmentAttributes() {
    Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
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
    }, 0, 10);
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
