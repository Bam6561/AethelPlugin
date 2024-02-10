package me.dannynguyen.aethel;

import me.dannynguyen.aethel.commands.aethelitem.ItemRegistry;
import me.dannynguyen.aethel.commands.forge.ForgeData;
import me.dannynguyen.aethel.commands.itemeditor.ItemEditorData;
import me.dannynguyen.aethel.commands.playerstat.PastStatHistory;
import me.dannynguyen.aethel.commands.playerstat.PlayerStatData;
import me.dannynguyen.aethel.commands.showitem.PastItemHistory;
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
 * @version 1.9.13
 * @since 1.1.7
 */
public class PluginData {
  public static final ItemRegistry itemRegistry = new ItemRegistry(PluginDirectory.AETHELITEM.file);

  public static final PlayerStatData playerStatData = new PlayerStatData();

  public static final PastItemHistory pastItemHistory = new PastItemHistory();
  public static final PastStatHistory pastStatHistory = new PastStatHistory();

  public static final ItemEditorData itemEditorData = new ItemEditorData();
  public static final ForgeData forgeData = new ForgeData();
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

    log.info("[Aethel] Loading Resources");
    File resourceDirectory = PluginDirectory.RESOURCES.file;
    if (!resourceDirectory.exists()) {
      resourceDirectory.mkdir();
    }

    File aethelItemDirectory = PluginDirectory.AETHELITEM.file;
    if (aethelItemDirectory.exists()) {
      start = System.nanoTime();
      PluginData.itemRegistry.loadData();
      finish = System.nanoTime();
      log.info("[Aethel] Loaded Aethel Items: " + convertToMs(hundredths, start, finish));
    } else {
      aethelItemDirectory.mkdir();
    }

    File forgeDirectory = PluginDirectory.FORGE.file;
    if (forgeDirectory.exists()) {
      start = System.nanoTime();
      PluginData.forgeData.loadRecipes();
      finish = System.nanoTime();
      log.info("[Aethel] Loaded Forge Recipes: " + convertToMs(hundredths, start, finish));
    } else {
      forgeDirectory.mkdir();
    }

    start = System.nanoTime();
    PluginData.playerStatData.loadData();
    finish = System.nanoTime();
    log.info("[Aethel] Loaded Player Stats: " + convertToMs(hundredths, start, finish));
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
}
