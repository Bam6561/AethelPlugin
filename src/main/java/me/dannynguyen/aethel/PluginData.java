package me.dannynguyen.aethel;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.commands.aethelitem.ItemRegistry;
import me.dannynguyen.aethel.commands.forge.RecipeRegistry;
import me.dannynguyen.aethel.commands.itemeditor.EditedItemCache;
import me.dannynguyen.aethel.commands.playerstat.PastStatHistory;
import me.dannynguyen.aethel.commands.playerstat.StatArchive;
import me.dannynguyen.aethel.commands.showitem.PastItemHistory;
import me.dannynguyen.aethel.plugin.enums.Directory;
import me.dannynguyen.aethel.plugin.system.PluginSystem;
import me.dannynguyen.aethel.rpg.system.RpgPlayer;
import me.dannynguyen.aethel.rpg.system.RpgSystem;
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
   * Registered {@link me.dannynguyen.aethel.commands.aethelitem.PersistentItem items}.
   */
  private final ItemRegistry itemRegistry = new ItemRegistry(Directory.AETHELITEM.getFile());

  /**
   * Registered {@link me.dannynguyen.aethel.commands.forge.PersistentRecipe recipes}.
   */
  private final RecipeRegistry recipeRegistry = new RecipeRegistry(Directory.FORGE.getFile());

  /**
   * Available {@link StatArchive player stats}.
   */
  private final StatArchive statArchive = new StatArchive();

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
   *  <li>{@link me.dannynguyen.aethel.commands.aethelitem.PersistentItem}
   *  <li>{@link me.dannynguyen.aethel.commands.forge.PersistentRecipe}
   *  <li>{@link me.dannynguyen.aethel.rpg.system.Equipment Jewelry}
   *  <li>{@link me.dannynguyen.aethel.rpg.system.Settings}
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
   * <ul>
   *  <li>{@link me.dannynguyen.aethel.rpg.system.Equipment Jewelry}
   *  <li>{@link me.dannynguyen.aethel.rpg.system.Settings}
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
   * Gets the plugin's {@link StatArchive}.
   *
   * @return plugin's {@link StatArchive}
   */
  @NotNull
  public StatArchive getPlayerStatRecord() {
    return this.statArchive;
  }

  /**
   * Gets the plugin's {@link EditedItemCache}.
   *
   * @return plugin's {@link EditedItemCache}
   */
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
}
