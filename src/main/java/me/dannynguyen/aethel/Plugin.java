package me.dannynguyen.aethel;

import me.dannynguyen.aethel.commands.AethelTagsCommand;
import me.dannynguyen.aethel.commands.DeveloperModeCommand;
import me.dannynguyen.aethel.commands.PingCommand;
import me.dannynguyen.aethel.commands.aethelitem.AethelItemCommand;
import me.dannynguyen.aethel.commands.character.CharacterCommand;
import me.dannynguyen.aethel.commands.forge.ForgeCommand;
import me.dannynguyen.aethel.commands.itemeditor.ItemEditorCommand;
import me.dannynguyen.aethel.commands.playerstats.PlayerStatsCommand;
import me.dannynguyen.aethel.commands.showitem.ShowItemCommand;
import me.dannynguyen.aethel.listeners.EquipmentAttributeListener;
import me.dannynguyen.aethel.listeners.InventoryMenuListener;
import me.dannynguyen.aethel.listeners.MessageInputListener;
import me.dannynguyen.aethel.listeners.RpgPlayerDamageListener;
import me.dannynguyen.aethel.systems.object.RpgPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

/**
 * Plugin represents the plugin as an object. Through event listeners and command executors,
 * the plugin can process various requests given to it by its users and the server.
 *
 * @author Danny Nguyen
 * @version 1.9.8
 * @since 1.0.0
 */
public class Plugin extends JavaPlugin {
  /**
   * On startup:
   * - Loads existing plugin-related data.
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
    getServer().getPluginManager().registerEvents(new RpgPlayerDamageListener(), this);
    getServer().getPluginManager().registerEvents(new EquipmentAttributeListener(), this);
    getServer().getPluginManager().registerEvents(new InventoryMenuListener(), this);
    getServer().getPluginManager().registerEvents(new MessageInputListener(), this);
  }

  /**
   * Registers the plugin's commands.
   */
  private void registerCommands() {
    this.getCommand("aethelitem").setExecutor(new AethelItemCommand());
    this.getCommand("aetheltags").setExecutor(new AethelTagsCommand());
    this.getCommand("character").setExecutor(new CharacterCommand());
    this.getCommand("developermode").setExecutor(new DeveloperModeCommand());
    this.getCommand("forge").setExecutor(new ForgeCommand());
    this.getCommand("itemeditor").setExecutor(new ItemEditorCommand());
    this.getCommand("ping").setExecutor(new PingCommand());
    this.getCommand("showitem").setExecutor(new ShowItemCommand());
    this.getCommand("playerstats").setExecutor(new PlayerStatsCommand());
  }

  /**
   * Schedules the plugin's repeating tasks.
   */
  private void scheduleRepeatingTasks() {
    addMainHandEquipmentAttributesInterval();
  }

  /**
   * Adds an interval to store the player's main hand item into memory for future comparisons.
   */
  private void addMainHandEquipmentAttributesInterval() {
    Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
      Map<Player, ItemStack> playerHeldItemMap = PluginData.rpgData.getPlayerHeldItemMap();

      for (Player player : Bukkit.getOnlinePlayers()) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if (playerHeldItemMap.containsKey(player)) {
          if (!playerHeldItemMap.get(player).equals(heldItem)) {
            playerHeldItemMap.put(player, heldItem);

            RpgPlayer rpgPlayer = PluginData.rpgData.getRpgPlayers().get(player);
            PluginData.rpgData.readEquipmentSlot(
                rpgPlayer.getEquipmentAttributes(),
                rpgPlayer.getAethelAttributes(),
                heldItem, "hand");
          }
        } else {
          playerHeldItemMap.put(player, heldItem);
        }
      }
    }, 0, 20);
  }

  public static Plugin getInstance() {
    return getPlugin(Plugin.class);
  }
}
