package me.dannynguyen.aethel;

import me.dannynguyen.aethel.commands.Debug;
import me.dannynguyen.aethel.commands.Forge;
import me.dannynguyen.aethel.commands.Ping;
import me.dannynguyen.aethel.listeners.InventoryListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * AethelPlugin represents the plugin as an object. Through event listeners and command executors,
 * the plugin can process various requests given to it by its users and the server.
 *
 * @author Danny Nguyen
 * @version 1.2.3
 * @since 1.0.0
 */
public class AethelPlugin extends JavaPlugin {
  private AethelResources resources = new AethelResources();

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

    this.getCommand("debug").setExecutor(new Debug());
    this.getCommand("forge").setExecutor(new Forge());
    this.getCommand("ping").setExecutor(new Ping());
  }

  /**
   * Reads existing plugin-related data. Creates data directories if they do not already exist.
   */
  private void readResourceFiles() {
    File resourceDirectory = new File(resources.getResourceDirectory());
    if (!resourceDirectory.exists()) resourceDirectory.mkdir();

    File forgeRecipeDirectory = new File(resources.getForgeRecipeDirectory());
    if (forgeRecipeDirectory.exists()) {
      resources.getForgeRecipeData().loadRecipes();
    } else {
      forgeRecipeDirectory.mkdir();
    }

    resources.getPlayerHeadData().loadPlayerHeads();
  }

  public AethelResources getResources() {
    return this.resources;
  }

  public static AethelPlugin getInstance() {
    return getPlugin(AethelPlugin.class);
  }
}
