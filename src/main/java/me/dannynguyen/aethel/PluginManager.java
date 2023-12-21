package me.dannynguyen.aethel;

import me.dannynguyen.aethel.commands.ForgeCommand;
import me.dannynguyen.aethel.commands.PingCommand;
import me.dannynguyen.aethel.gui.ForgeGUI;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * PluginManager represents the plugin an as object. Through event listeners and command executors,
 * the plugin can process various requests given to it by its users and the server.
 *
 * @author Danny Nguyen
 * @version 1.0.2
 * @since 1.0.0
 */
public class PluginManager extends JavaPlugin {
  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(new ForgeGUI(), this);

    this.getCommand("forge").setExecutor(new ForgeCommand());
    this.getCommand("ping").setExecutor(new PingCommand());
  }

  @Override
  public void onDisable() {
  }

  public static PluginManager getInstance() {
    return getPlugin(PluginManager.class);
  }
}
