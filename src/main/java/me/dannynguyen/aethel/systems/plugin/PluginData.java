package me.dannynguyen.aethel.systems.plugin;

import me.dannynguyen.aethel.commands.aethelitem.ItemRegistry;
import me.dannynguyen.aethel.commands.forge.RecipeRegistry;
import me.dannynguyen.aethel.commands.itemeditor.EditedItemCache;
import me.dannynguyen.aethel.commands.playerstat.PastStatHistory;
import me.dannynguyen.aethel.commands.playerstat.PlayerStatRecord;
import me.dannynguyen.aethel.commands.showitem.PastItemHistory;
import me.dannynguyen.aethel.systems.plugin.enums.PluginDirectory;
import me.dannynguyen.aethel.systems.rpg.RpgSystem;
import org.bukkit.Bukkit;

import java.io.File;
import java.text.DecimalFormat;
import java.util.logging.Logger;

/**
 * Represents plugin's resources in memory.
 *
 * @author Danny Nguyen
 * @version 1.10.1
 * @since 1.1.7
 */
public class PluginData {
  /**
   * Registered items.
   */
  public static final ItemRegistry itemRegistry = new ItemRegistry(PluginDirectory.AETHELITEM.getFile());

  /**
   * Registered recipes.
   */
  public static final RecipeRegistry recipeRegistry = new RecipeRegistry(PluginDirectory.FORGE.getFile());

  /**
   * Available player stats.
   */
  public static final PlayerStatRecord playerStatRecord = new PlayerStatRecord();

  /**
   * Currently editing items.
   */
  public static final EditedItemCache editedItemCache = new EditedItemCache();

  /**
   * Past shown item history.
   */
  public static final PastItemHistory pastItemHistory = new PastItemHistory();

  /**
   * Past shared stat history.
   */
  public static final PastStatHistory pastStatHistory = new PastStatHistory();

  /**
   * Plugin system data.
   */
  public static final PluginSystem pluginSystem = new PluginSystem();

  /**
   * RPG system data.
   */
  public static final RpgSystem rpgSystem = new RpgSystem();

  /**
   * Loads persistent plugin data. Creates data directories if they do not already exist.
   */
  public static void loadResources() {
    Logger log = Bukkit.getLogger();
    long start;
    long finish;
    DecimalFormat hundredths = new DecimalFormat();
    hundredths.setMaximumFractionDigits(2);

    log.info("[Aethel] Loading Resources");
    File resourceDirectory = PluginDirectory.RESOURCES.getFile();
    if (!resourceDirectory.exists()) {
      resourceDirectory.mkdir();
    }

    File aethelItemDirectory = PluginDirectory.AETHELITEM.getFile();
    if (aethelItemDirectory.exists()) {
      start = System.nanoTime();
      PluginData.itemRegistry.loadData();
      finish = System.nanoTime();
      log.info("[Aethel] Loaded Aethel Items: " + convertToMs(hundredths, start, finish));
    } else {
      aethelItemDirectory.mkdir();
    }

    File forgeDirectory = PluginDirectory.FORGE.getFile();
    if (forgeDirectory.exists()) {
      start = System.nanoTime();
      PluginData.recipeRegistry.loadData();
      finish = System.nanoTime();
      log.info("[Aethel] Loaded Forge Recipes: " + convertToMs(hundredths, start, finish));
    } else {
      forgeDirectory.mkdir();
    }

    start = System.nanoTime();
    PluginData.playerStatRecord.loadData();
    finish = System.nanoTime();
    log.info("[Aethel] Loaded Player Stats: " + convertToMs(hundredths, start, finish));
  }

  /**
   * Converts the time duration of a process in nanoseconds to milliseconds.
   *
   * @param dc     decimal format
   * @param start  start time
   * @param finish finish time
   * @return milliseconds elapsed
   */
  private static String convertToMs(DecimalFormat dc, long start, long finish) {
    return dc.format(Double.parseDouble(String.valueOf(finish - start)) / 1000000) + " ms";
  }
}
