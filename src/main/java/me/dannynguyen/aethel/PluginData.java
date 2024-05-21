package me.dannynguyen.aethel;

import me.dannynguyen.aethel.commands.aethelitem.ItemRegistry;
import me.dannynguyen.aethel.commands.forge.RecipeRegistry;
import me.dannynguyen.aethel.commands.itemeditor.EditedItemCache;
import me.dannynguyen.aethel.commands.playerstat.PastStatHistory;
import me.dannynguyen.aethel.commands.showitem.PastItemHistory;
import me.dannynguyen.aethel.enums.plugin.Directory;
import me.dannynguyen.aethel.plugin.PluginLogger;
import me.dannynguyen.aethel.plugin.PluginSystem;
import me.dannynguyen.aethel.rpg.Equipment;
import me.dannynguyen.aethel.rpg.RpgPlayer;
import me.dannynguyen.aethel.rpg.RpgSystem;
import me.dannynguyen.aethel.rpg.Settings;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * Represents plugin's resources in memory.
 *
 * @author Danny Nguyen
 * @version 1.25.7
 * @since 1.1.7
 */
public class PluginData {
  /**
   * {@link PluginLogger};
   */
  private final PluginLogger pluginLogger = new PluginLogger();

  /**
   * Registered {@link ItemRegistry items}.
   */
  private final ItemRegistry itemRegistry = new ItemRegistry(Directory.AETHELITEM.getFile());

  /**
   * Registered {@link RecipeRegistry recipes}.
   */
  private final RecipeRegistry recipeRegistry = new RecipeRegistry(Directory.FORGE.getFile());

  /**
   * Currently {@link EditedItemCache editing items}.
   */
  private final EditedItemCache editedItemCache = new EditedItemCache();

  /**
   * Past shown {@link PastItemHistory item history}.
   */
  private final PastItemHistory pastItemHistory = new PastItemHistory();

  /**
   * Past shared {@link PastStatHistory stat history}.
   */
  private final PastStatHistory pastStatHistory = new PastStatHistory();

  /**
   * {@link PluginSystem Plugin system} data.
   */
  private final PluginSystem pluginSystem = new PluginSystem();

  /**
   * {@link RpgSystem RPG system} data.
   */
  private final RpgSystem rpgSystem = new RpgSystem();

  /**
   * No parameter constructor.
   */
  public PluginData() {
  }

  /**
   * Loads persistent plugin data. Creates data directories if they do not already exist.
   * <ul>
   *  <li>{@link ItemRegistry}
   *  <li>{@link RecipeRegistry}
   *  <li>{@link Equipment Jewelry}
   *  <li>{@link Settings}
   *  <li>{@link PluginLogger}
   * </ul>
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
    log.info("[Aethel] Loaded Aethel Items: " + longToMs(df2, start, finish));

    start = System.nanoTime();
    Plugin.getData().getRecipeRegistry().loadData();
    finish = System.nanoTime();
    log.info("[Aethel] Loaded Forge Recipes: " + longToMs(df2, start, finish));

    File locationDirectory = Directory.LOCATION.getFile();
    if (!locationDirectory.exists()) {
      locationDirectory.mkdirs();
    }

    File rpgJewelryDirectory = Directory.JEWELRY.getFile();
    if (!rpgJewelryDirectory.exists()) {
      rpgJewelryDirectory.mkdirs();
    }

    File rpgSettingsDirectory = Directory.SETTINGS.getFile();
    if (!rpgSettingsDirectory.exists()) {
      rpgSettingsDirectory.mkdirs();
    }

    File logDirectory = Directory.LOG.getFile();
    if (!logDirectory.exists()) {
      logDirectory.mkdirs();
    }
  }

  /**
   * Saves persistent plugin data.
   * <ul>
   *  <li>{@link Equipment Jewelry}
   *  <li>{@link Settings}
   * </ul>
   */
  public void saveResources() {
    Logger log = Bukkit.getLogger();
    long start;
    long finish;
    DecimalFormat df2 = new DecimalFormat();
    df2.setMaximumFractionDigits(2);

    log.info("[Aethel] Saving Resources");

    Collection<RpgPlayer> rpgPlayers = Plugin.getData().getRpgSystem().getRpgPlayers().values();

    start = System.nanoTime();
    for (RpgPlayer rpgPlayer : rpgPlayers) {
      rpgPlayer.getEquipment().saveJewelry();
    }
    finish = System.nanoTime();
    log.info("[Aethel] Saved RPG Jewelry: " + longToMs(df2, start, finish));

    start = System.nanoTime();
    for (RpgPlayer rpgPlayer : rpgPlayers) {
      rpgPlayer.getSettings().saveSettings();
    }
    finish = System.nanoTime();
    log.info("[Aethel] Saved RPG Settings: " + longToMs(df2, start, finish));

    pluginLogger.saveEntries();
  }

  /**
   * Converts the time duration of a process in nanoseconds to milliseconds.
   *
   * @param df2    0.00
   * @param start  start time
   * @param finish finish time
   * @return milliseconds elapsed
   */
  private static String longToMs(DecimalFormat df2, long start, long finish) {
    return df2.format(Double.parseDouble(String.valueOf(finish - start)) / 1000000) + " ms";
  }

  /**
   * Gets the plugin's {@link ItemRegistry}.
   *
   * @return plugin's {@link ItemRegistry}
   */
  @NotNull
  public ItemRegistry getItemRegistry() {
    return this.itemRegistry;
  }

  /**
   * Gets the plugin's {@link RecipeRegistry}.
   *
   * @return plugin's {@link RecipeRegistry}
   */
  @NotNull
  public RecipeRegistry getRecipeRegistry() {
    return this.recipeRegistry;
  }

  /**
   * Gets the plugin's {@link EditedItemCache}.
   *
   * @return plugin's {@link EditedItemCache}
   */
  @NotNull
  public EditedItemCache getEditedItemCache() {
    return this.editedItemCache;
  }

  /**
   * Gets the plugin's {@link PastItemHistory}.
   *
   * @return plugin's {@link PastItemHistory}
   */
  @NotNull
  public PastItemHistory getPastItemHistory() {
    return this.pastItemHistory;
  }

  /**
   * Gets the plugin's {@link PastStatHistory}.
   *
   * @return plugin's {@link PastStatHistory}
   */
  @NotNull
  public PastStatHistory getPastStatHistory() {
    return this.pastStatHistory;
  }

  /**
   * Gets the plugin's {@link PluginSystem}.
   *
   * @return plugin's {@link PluginSystem}
   */
  @NotNull
  public PluginSystem getPluginSystem() {
    return this.pluginSystem;
  }

  /**
   * Gets the plugin's {@link RpgSystem}.
   *
   * @return plugin's {@link RpgSystem}
   */
  @NotNull
  public RpgSystem getRpgSystem() {
    return this.rpgSystem;
  }

  /**
   * Gets the plugin's {@link PluginLogger}.
   *
   * @return plugin's {@link PluginLogger}
   */
  public PluginLogger getPluginLogger() {
    return this.pluginLogger;
  }
}
