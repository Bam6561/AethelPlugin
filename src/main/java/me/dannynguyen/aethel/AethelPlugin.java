package me.dannynguyen.aethel;

import me.dannynguyen.aethel.commands.*;
import me.dannynguyen.aethel.listeners.inventory.InventoryListener;
import me.dannynguyen.aethel.listeners.message.MessageListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * AethelPlugin represents the plugin as an object. Through event listeners and command executors,
 * the plugin can process various requests given to it by its users and the server.
 *
 * @author Danny Nguyen
 * @version 1.7.0
 * @since 1.0.0
 */
public class AethelPlugin extends JavaPlugin {
  /**
   * On startup:
   * - Reads existing plugin-related data.
   * - Registers event listeners.
   * - Registers commands.
   */
  @Override
  public void onEnable() {
    readResourceFiles();

    getServer().getPluginManager().registerEvents(new InventoryListener(), this);
    getServer().getPluginManager().registerEvents(new MessageListener(), this);

    this.getCommand("aitem").setExecutor(new AethelItem());
    this.getCommand("atag").setExecutor(new AethelTag());
    this.getCommand("char").setExecutor(new CharacterProfile());
    this.getCommand("dev").setExecutor(new DevMode());
    this.getCommand("forge").setExecutor(new Forge());
    this.getCommand("iedit").setExecutor(new ItemEditor());
    this.getCommand("ping").setExecutor(new Ping());
    this.getCommand("show").setExecutor(new ShowItem());
    this.getCommand("stats").setExecutor(new PlayerStat());
  }

  /**
   * Reads existing plugin-related data. Creates data directories if they do not already exist.
   */
  private void readResourceFiles() {
    File resourceDirectory = new File(AethelResources.resourceDirectory);
    if (!resourceDirectory.exists()) resourceDirectory.mkdir();

    File aethelItemDirectory = new File(AethelResources.aethelItemDirectory);
    if (aethelItemDirectory.exists()) {
      AethelResources.aethelItemData.loadItems();
    } else {
      aethelItemDirectory.mkdir();
    }

    File forgeRecipeDirectory = new File(AethelResources.forgeRecipeDirectory);
    if (forgeRecipeDirectory.exists()) {
      AethelResources.forgeRecipeData.loadRecipes();
    } else {
      forgeRecipeDirectory.mkdir();
    }

    AethelResources.itemEditorData.loadSortedData();
    AethelResources.playerHeadData.loadPlayerHeads();
    AethelResources.playerStatData.loadStats();
  }

  public static AethelPlugin getInstance() {
    return getPlugin(AethelPlugin.class);
  }
}
