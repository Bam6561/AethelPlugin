package me.dannynguyen.aethel;

import me.dannynguyen.aethel.commands.AethelTagsCommand;
import me.dannynguyen.aethel.commands.DeveloperModeCommand;
import me.dannynguyen.aethel.commands.PingCommand;
import me.dannynguyen.aethel.commands.aethelItems.AethelItemsCommand;
import me.dannynguyen.aethel.commands.character.CharacterCommand;
import me.dannynguyen.aethel.commands.forge.ForgeCommand;
import me.dannynguyen.aethel.commands.itemeditor.ItemEditorCommand;
import me.dannynguyen.aethel.commands.playerstats.PlayerStatsCommand;
import me.dannynguyen.aethel.commands.showitem.ShowItemCommand;
import me.dannynguyen.aethel.enums.PluginDirectory;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.listeners.InventoryListener;
import me.dannynguyen.aethel.listeners.MessageListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.text.DecimalFormat;
import java.util.logging.Logger;

/**
 * Plugin represents the plugin as an object. Through event listeners and command executors,
 * the plugin can process various requests given to it by its users and the server.
 *
 * @author Danny Nguyen
 * @version 1.8.3
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
    loadResources();

    getServer().getPluginManager().registerEvents(new InventoryListener(), this);
    getServer().getPluginManager().registerEvents(new MessageListener(), this);

    this.getCommand("aethelitems").setExecutor(new AethelItemsCommand());
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
   * Loads existing plugin-related data. Creates data directories if they do not already exist.
   */
  private void loadResources() {
    Logger log = Bukkit.getLogger();
    long start;
    long finish;
    DecimalFormat hundredths = new DecimalFormat();
    hundredths.setMaximumFractionDigits(2);

    log.info(PluginMessage.Success.PLUGIN_LOAD_RESOURCES.message);

    File resourceDirectory = PluginDirectory.RESOURCES.file;
    if (!resourceDirectory.exists()) {
      resourceDirectory.mkdir();
    }

    File aethelItemsDirectory = PluginDirectory.AETHELITEMS.file;
    if (aethelItemsDirectory.exists()) {
      start = System.nanoTime();
      PluginData.aethelItemsData.loadItems();
      finish = System.nanoTime();
      log.info(PluginMessage.Success.PLUGIN_LOAD_AETHELITEMS.message +
          convertToMs(hundredths, start, finish));
    } else {
      aethelItemsDirectory.mkdir();
    }

    File forgeDirectory = PluginDirectory.FORGE.file;
    if (forgeDirectory.exists()) {
      start = System.nanoTime();
      PluginData.forgeData.loadRecipes();
      finish = System.nanoTime();
      log.info(PluginMessage.Success.PLUGIN_LOAD_FORGE.message +
          convertToMs(hundredths, start, finish));
    } else {
      forgeDirectory.mkdir();
    }

    start = System.nanoTime();
    PluginData.playerStatsData.loadStats();
    finish = System.nanoTime();
    log.info(PluginMessage.Success.PLUGIN_LOAD_PLAYERSTATS.message +
        convertToMs(hundredths, start, finish));
  }

  /**
   * Converts the time duration of a process in nanoseconds to milliseconds.
   *
   * @param start  start time
   * @param finish finish time
   * @return milliseconds elapsed
   */
  private static String convertToMs(DecimalFormat dc, long start, long finish) {
    return dc.format(Double.parseDouble(String.valueOf(finish - start)) / 1000000) + " ms";
  }

  public static Plugin getInstance() {
    return getPlugin(Plugin.class);
  }
}
