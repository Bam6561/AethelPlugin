package me.dannynguyen.aethel;

import me.dannynguyen.aethel.commands.Ping;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * PluginManager represents the plugin an as object. Through event
 * listeners and command executors, the plugin can process various
 * requests given to it by its users and the server.
 *
 * @author Danny Nguyen
 * @version 1.0.0
 * @since 1.0.0
 */
public class PluginManager extends JavaPlugin {
  @Override
  public void onEnable() {
    this.getCommand("Ping").setExecutor(new Ping());
  }

  @Override
  public void onDisable() {
  }
}
