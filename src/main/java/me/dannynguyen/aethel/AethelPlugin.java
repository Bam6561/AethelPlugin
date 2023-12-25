package me.dannynguyen.aethel;

import me.dannynguyen.aethel.commands.ForgeCommand;
import me.dannynguyen.aethel.commands.PingCommand;
import me.dannynguyen.aethel.gui.ForgeGUI;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * AethelPlugin represents the plugin an as object. Through event listeners and command executors,
 * the plugin can process various requests given to it by its users and the server.
 *
 * @author Danny Nguyen
 * @version 1.0.5
 * @since 1.0.0
 */
public class AethelPlugin extends JavaPlugin {
  private String resourceDirectory = "./plugins/Aethel";

  /**
   * On startup:
   * - Reads existing plugin-related data.
   * - Registers event listeners.
   * - Registers commands.
   */
  @Override
  public void onEnable() {
    readResourceFiles(getResourceDirectory());

    getServer().getPluginManager().registerEvents(new ForgeGUI(), this);

    this.getCommand("forge").setExecutor(new ForgeCommand());
    this.getCommand("ping").setExecutor(new PingCommand());
  }

  /**
   * Reads existing plugin-related data. Creates data directories if one does not already exist.
   */
  private void readResourceFiles(String filePath) {
    File resourceFiles = new File(filePath);
    if (!resourceFiles.exists()) resourceFiles.mkdir();

    File forgeRecipes = new File(filePath + "/forge");
    if (!forgeRecipes.exists()) forgeRecipes.mkdir();
  }

  /**
   * Returns the file path of the plugin's resource directory.
   *
   * @return resource directory
   */
  public String getResourceDirectory() {
    return this.resourceDirectory;
  }

  /**
   * Returns an AethelPlugin object that identifies this plugin.
   *
   * @return plugin
   */
  public static AethelPlugin getInstance() {
    return getPlugin(AethelPlugin.class);
  }
}
