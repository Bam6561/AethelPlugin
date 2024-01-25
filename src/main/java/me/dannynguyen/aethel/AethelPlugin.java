package me.dannynguyen.aethel;

import me.dannynguyen.aethel.commands.Character;
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
 * @version 1.7.4
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

    this.getCommand("aethelitems").setExecutor(new AethelItems());
    this.getCommand("aetheltags").setExecutor(new AethelTags());
    this.getCommand("character").setExecutor(new Character());
    this.getCommand("developermode").setExecutor(new DeveloperMode());
    this.getCommand("forge").setExecutor(new Forge());
    this.getCommand("itemeditor").setExecutor(new ItemEditor());
    this.getCommand("ping").setExecutor(new Ping());
    this.getCommand("showitem").setExecutor(new ShowItem());
    this.getCommand("playerstats").setExecutor(new PlayerStats());
  }

  /**
   * Reads existing plugin-related data. Creates data directories if they do not already exist.
   */
  private void readResourceFiles() {
    File resourceDirectory = new File(AethelResources.resourceDirectory);
    if (!resourceDirectory.exists()) resourceDirectory.mkdir();

    File aethelItemsDirectory = new File(AethelResources.aethelItemsDirectory);
    if (aethelItemsDirectory.exists()) {
      AethelResources.aethelItemsData.loadItems();
    } else {
      aethelItemsDirectory.mkdir();
    }

    File forgeRecipeDirectory = new File(AethelResources.forgeRecipesDirectory);
    if (forgeRecipeDirectory.exists()) {
      AethelResources.forgeRecipeData.loadRecipes();
    } else {
      forgeRecipeDirectory.mkdir();
    }

    AethelResources.itemEditorData.loadAttributesEnchants();
    AethelResources.loadedPlayerHeadData.loadPlayerHeads();
    AethelResources.playerStatsData.loadStats();
  }

  public static AethelPlugin getInstance() {
    return getPlugin(AethelPlugin.class);
  }
}
