package me.dannynguyen.aethel.systems.plugin;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.commands.aethelitem.ItemRegistry;
import me.dannynguyen.aethel.commands.forge.RecipeRegistry;
import me.dannynguyen.aethel.commands.itemeditor.EditedItemCache;
import me.dannynguyen.aethel.commands.playerstat.PastStatHistory;
import me.dannynguyen.aethel.commands.playerstat.PlayerStatRecord;
import me.dannynguyen.aethel.commands.showitem.PastItemHistory;
import me.dannynguyen.aethel.systems.rpg.RpgPlayer;
import me.dannynguyen.aethel.systems.rpg.RpgSystem;
import org.bukkit.Bukkit;

import java.io.File;
import java.text.DecimalFormat;
import java.util.logging.Logger;

/**
 * Represents plugin's resources in memory.
 *
 * @author Danny Nguyen
 * @version 1.14.5
 * @since 1.1.7
 */
public class PluginData {
  /**
   * Registered items.
   */
  private final ItemRegistry itemRegistry = new ItemRegistry(PluginDirectory.AETHELITEM.getFile());

  /**
   * Registered recipes.
   */
  private final RecipeRegistry recipeRegistry = new RecipeRegistry(PluginDirectory.FORGE.getFile());

  /**
   * Available player stats.
   */
  private final PlayerStatRecord playerStatRecord = new PlayerStatRecord();

  /**
   * Currently editing items.
   */
  private final EditedItemCache editedItemCache = new EditedItemCache();

  /**
   * Past shown item history.
   */
  private final PastItemHistory pastItemHistory = new PastItemHistory();

  /**
   * Past shared stat history.
   */
  private final PastStatHistory pastStatHistory = new PastStatHistory();

  /**
   * Plugin system data.
   */
  private final PluginSystem pluginSystem = new PluginSystem();

  /**
   * RPG system data.
   */
  private final RpgSystem rpgSystem = new RpgSystem();

  /**
   * Loads persistent plugin data. Creates data directories if they do not already exist.
   */
  public void loadResources() {
    Logger log = Bukkit.getLogger();
    long start;
    long finish;
    DecimalFormat df2 = new DecimalFormat();
    df2.setMaximumFractionDigits(2);

    log.info("[Aethel] Loading Resources");

    start = System.nanoTime();
    Plugin.getData().getItemRegistry().loadData();
    finish = System.nanoTime();
    log.info("[Aethel] Loaded Aethel Items: " + convertToMs(df2, start, finish));

    start = System.nanoTime();
    Plugin.getData().getRecipeRegistry().loadData();
    finish = System.nanoTime();
    log.info("[Aethel] Loaded Forge Recipes: " + convertToMs(df2, start, finish));

    File rpgJewelryDirectory = PluginDirectory.JEWELRY.getFile();
    if (!rpgJewelryDirectory.exists()) {
      rpgJewelryDirectory.mkdirs();
    }
  }

  /**
   * Saves persistent plugin data.
   */
  public void saveResources() {
    Logger log = Bukkit.getLogger();
    long start;
    long finish;
    DecimalFormat df2 = new DecimalFormat();
    df2.setMaximumFractionDigits(2);

    log.info("[Aethel] Saving Resources");

    start = System.nanoTime();
    for (RpgPlayer rpgPlayer : Plugin.getData().getRpgSystem().getRpgPlayers().values()) {
      rpgPlayer.getEquipment().saveJewelry();
    }
    finish = System.nanoTime();
    log.info("[Aethel] Saved RPG Jewelry: " + convertToMs(df2, start, finish));
  }

  /**
   * Converts the time duration of a process in nanoseconds to milliseconds.
   *
   * @param df2    0.00
   * @param start  start time
   * @param finish finish time
   * @return milliseconds elapsed
   */
  private static String convertToMs(DecimalFormat df2, long start, long finish) {
    return df2.format(Double.parseDouble(String.valueOf(finish - start)) / 1000000) + " ms";
  }

  /**
   * Gets the plugin's item registry.
   *
   * @return plugin's item registry
   */
  public ItemRegistry getItemRegistry() {
    return this.itemRegistry;
  }

  /**
   * Gets the plugin's recipe registry.
   *
   * @return plugin's recipe registry
   */
  public RecipeRegistry getRecipeRegistry() {
    return this.recipeRegistry;
  }

  /**
   * Gets the plugin's player stat record.
   *
   * @return plugin's player stat record
   */
  public PlayerStatRecord getPlayerStatRecord() {
    return this.playerStatRecord;
  }

  /**
   * Gets the plugin's edited item cache
   *
   * @return plugin's edited item cache
   */
  public EditedItemCache getEditedItemCache() {
    return this.editedItemCache;
  }

  /**
   * Gets the plugin's past item history.
   *
   * @return plugin's past item history
   */
  public PastItemHistory getPastItemHistory() {
    return this.pastItemHistory;
  }

  /**
   * Gets the plugin's past stat history.
   *
   * @return plugin's past stat history
   */
  public PastStatHistory getPastStatHistory() {
    return this.pastStatHistory;
  }

  /**
   * Gets the plugin's plugin system.
   *
   * @return plugin's plugin system
   */
  public PluginSystem getPluginSystem() {
    return this.pluginSystem;
  }

  /**
   * Gets the plugin's RPG system.
   *
   * @return plugin's RPG system
   */
  public RpgSystem getRpgSystem() {
    return this.rpgSystem;
  }
}
