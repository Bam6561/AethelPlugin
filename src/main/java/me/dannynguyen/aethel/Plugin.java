package me.dannynguyen.aethel;

import me.dannynguyen.aethel.commands.Character;
import me.dannynguyen.aethel.commands.*;
import me.dannynguyen.aethel.listeners.inventory.InventoryListener;
import me.dannynguyen.aethel.listeners.message.MessageListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Plugin represents the plugin as an object. Through event listeners and command executors,
 * the plugin can process various requests given to it by its users and the server.
 *
 * @author Danny Nguyen
 * @version 1.7.8
 * @since 1.0.0
 */
public class Plugin extends JavaPlugin {
  /**
   * On startup:
   * - Reads existing plugin-related data.
   * - Registers event listeners.
   * - Registers commands.
   */
  @Override
  public void onEnable() {
    loadResources();

    getServer().getPluginManager().registerEvents(new InventoryListener(), this);
    getServer().getPluginManager().registerEvents(new MessageListener(), this);

    this.getCommand("aethelitems").setExecutor(new AethelItemsC());
    this.getCommand("aetheltags").setExecutor(new AethelTags());
    this.getCommand("character").setExecutor(new Character());
    this.getCommand("developermode").setExecutor(new DeveloperMode());
    this.getCommand("forge").setExecutor(new ForgeC());
    this.getCommand("itemeditor").setExecutor(new ItemEditorC());
    this.getCommand("ping").setExecutor(new Ping());
    this.getCommand("showitem").setExecutor(new ShowItem());
    this.getCommand("playerstats").setExecutor(new PlayerStatsC());
  }

  /**
   * Loads existing plugin-related data. Creates data directories if they do not already exist.
   */
  private void loadResources() {
    File resourceDirectory = new File(PluginData.resourceDirectory);
    if (!resourceDirectory.exists()) resourceDirectory.mkdir();

    File aethelItemsDirectory = new File(PluginData.aethelItemsDirectory);
    if (aethelItemsDirectory.exists()) {
      PluginData.aethelItemsData.loadItems();
    } else {
      aethelItemsDirectory.mkdir();
    }

    File forgeRecipeDirectory = new File(PluginData.forgeRecipesDirectory);
    if (forgeRecipeDirectory.exists()) {
      PluginData.forgeRecipeData.loadRecipes();
    } else {
      forgeRecipeDirectory.mkdir();
    }

    PluginData.itemEditorData.loadAttributesEnchants();
    PluginData.loadedPlayerHeadData.loadPlayerHeads();
    PluginData.playerStatsData.loadStats();
  }

  public static Plugin getInstance() {
    return getPlugin(Plugin.class);
  }
}
