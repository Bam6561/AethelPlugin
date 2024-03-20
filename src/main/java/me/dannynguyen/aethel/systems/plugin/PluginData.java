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
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * Represents plugin's resources in memory.
 *
 * @author Danny Nguyen
 * @version 1.16.4
 * @since 1.1.7
 */
public class PluginData {
  /**
   * Registered items.
   */
  private final ItemRegistry itemRegistry = new ItemRegistry(Directory.AETHELITEM.getFile());

  /**
   * Registered recipes.
   */
  private final RecipeRegistry recipeRegistry = new RecipeRegistry(Directory.FORGE.getFile());

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
   * No parameter constructor.
   */
  public PluginData() {
  }

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
    log.info("[Aethel] Loaded Aethel Items: " + longToMs(df2, start, finish));

    start = System.nanoTime();
    Plugin.getData().getRecipeRegistry().loadData();
    finish = System.nanoTime();
    log.info("[Aethel] Loaded Forge Recipes: " + longToMs(df2, start, finish));

    File rpgJewelryDirectory = Directory.JEWELRY.getFile();
    if (!rpgJewelryDirectory.exists()) {
      rpgJewelryDirectory.mkdirs();
    }

    File rpgSettingsDirectory = Directory.SETTINGS.getFile();
    if (!rpgSettingsDirectory.exists()) {
      rpgSettingsDirectory.mkdirs();
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
   * Gets the plugin's item registry.
   *
   * @return plugin's item registry
   */
  @NotNull
  public ItemRegistry getItemRegistry() {
    return this.itemRegistry;
  }

  /**
   * Gets the plugin's recipe registry.
   *
   * @return plugin's recipe registry
   */
  @NotNull
  public RecipeRegistry getRecipeRegistry() {
    return this.recipeRegistry;
  }

  /**
   * Gets the plugin's player stat record.
   *
   * @return plugin's player stat record
   */
  @NotNull
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
  @NotNull
  public PastItemHistory getPastItemHistory() {
    return this.pastItemHistory;
  }

  /**
   * Gets the plugin's past stat history.
   *
   * @return plugin's past stat history
   */
  @NotNull
  public PastStatHistory getPastStatHistory() {
    return this.pastStatHistory;
  }

  /**
   * Gets the plugin's plugin system.
   *
   * @return plugin's plugin system
   */
  @NotNull
  public PluginSystem getPluginSystem() {
    return this.pluginSystem;
  }

  /**
   * Gets the plugin's RPG system.
   *
   * @return plugin's RPG system
   */
  @NotNull
  public RpgSystem getRpgSystem() {
    return this.rpgSystem;
  }
}
