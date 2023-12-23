package me.dannynguyen.aethel;

import me.dannynguyen.aethel.commands.ForgeCommand;
import me.dannynguyen.aethel.commands.PingCommand;
import me.dannynguyen.aethel.commands.ReadCommand;
import me.dannynguyen.aethel.commands.WriteCommand;
import me.dannynguyen.aethel.gui.ForgeGUI;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * AethelPlugin represents the plugin an as object. Through event listeners and command executors,
 * the plugin can process various requests given to it by its users and the server.
 *
 * @author Danny Nguyen
 * @version 1.0.3
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
    readResourceFiles(new File("./plugins/Aethel"));

    getServer().getPluginManager().registerEvents(new ForgeGUI(), this);

    this.getCommand("forge").setExecutor(new ForgeCommand());
    this.getCommand("ping").setExecutor(new PingCommand());
    this.getCommand("read").setExecutor(new ReadCommand());
    this.getCommand("write").setExecutor(new WriteCommand());
  }

  @Override
  public void onDisable() {
  }

  /**
   * Reads existing plugin-related data. Creates a data directory if one does not already exist.
   *
   * @param resourceDirectory directory containing plugin-related data
   */
  private void readResourceFiles(File resourceDirectory) {
    if (resourceDirectory.exists()) {

    } else {
      resourceDirectory.mkdir();
    }
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
