package me.dannynguyen.aethel;

import me.dannynguyen.aethel.commands.aethelItems.AethelItemsData;
import me.dannynguyen.aethel.commands.forge.ForgeData;
import me.dannynguyen.aethel.commands.itemeditor.ItemEditorData;
import me.dannynguyen.aethel.commands.playerstats.PlayerStatsData;
import me.dannynguyen.aethel.commands.showitem.ShowItemData;
import me.dannynguyen.aethel.enums.PluginDirectory;
import me.dannynguyen.aethel.systems.RpgData;
import org.bukkit.Bukkit;

import java.io.File;
import java.text.DecimalFormat;
import java.util.logging.Logger;

/**
 * PluginData stores the plugin's resources in memory.
 *
 * @author Danny Nguyen
 * @version 1.8.13
 * @since 1.1.7
 */
public class PluginData {
  public static final AethelItemsData aethelItemsData = new AethelItemsData();
  public static final ItemEditorData itemEditorData = new ItemEditorData();
  public static final ForgeData forgeData = new ForgeData();
  public static final PlayerStatsData playerStatsData = new PlayerStatsData();
  public static final ShowItemData showItemData = new ShowItemData();
  public static final RpgData rpgData = new RpgData();

  /**
   * Loads existing plugin-related data. Creates data directories if they do not already exist.
   */
  public static void loadResources() {
    Logger log = Bukkit.getLogger();
    long start;
    long finish;
    DecimalFormat hundredths = new DecimalFormat();
    hundredths.setMaximumFractionDigits(2);

    log.info(Success.LOADING_RESOURCES.message);

    File resourceDirectory = PluginDirectory.RESOURCES.file;
    if (!resourceDirectory.exists()) {
      resourceDirectory.mkdir();
    }

    File aethelItemsDirectory = PluginDirectory.AETHELITEMS.file;
    if (aethelItemsDirectory.exists()) {
      start = System.nanoTime();
      PluginData.aethelItemsData.loadItems();
      finish = System.nanoTime();
      log.info(Success.LOADED_AETHELITEMS.message + convertToMs(hundredths, start, finish));
    } else {
      aethelItemsDirectory.mkdir();
    }

    File forgeDirectory = PluginDirectory.FORGE.file;
    if (forgeDirectory.exists()) {
      start = System.nanoTime();
      PluginData.forgeData.loadRecipes();
      finish = System.nanoTime();
      log.info(Success.LOADED_FORGE.message + convertToMs(hundredths, start, finish));
    } else {
      forgeDirectory.mkdir();
    }

    start = System.nanoTime();
    PluginData.playerStatsData.loadStats();
    finish = System.nanoTime();
    log.info(Success.LOADED_PLAYERSTATS.message + convertToMs(hundredths, start, finish));
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

  private enum Success {
    LOADING_RESOURCES("[Aethel] Loading Resources"),
    LOADED_AETHELITEMS("[Aethel] Loaded Aethel Items: "),
    LOADED_FORGE("[Aethel] Loaded Forge Recipes: "),
    LOADED_PLAYERSTATS("[Aethel] Loaded Player Stats: ");

    public final String message;

    Success(String message) {
      this.message = message;
    }
  }
}
