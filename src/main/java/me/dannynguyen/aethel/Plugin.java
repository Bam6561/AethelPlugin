package me.dannynguyen.aethel;

import me.dannynguyen.aethel.commands.*;
import me.dannynguyen.aethel.commands.aethelitem.ItemCommand;
import me.dannynguyen.aethel.commands.character.CharacterCommand;
import me.dannynguyen.aethel.commands.forge.ForgeCommand;
import me.dannynguyen.aethel.commands.itemeditor.ItemEditorCommand;
import me.dannynguyen.aethel.commands.playerstat.StatCommand;
import me.dannynguyen.aethel.commands.showitem.ShowItemCommand;
import me.dannynguyen.aethel.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the plugin as an object.
 * <p>
 * Through event listeners and command executors, the plugin can
 * handle various requests given to it by its users and the server.
 *
 * @author Danny Nguyen
 * @version 1.22.5
 * @since 1.0.0
 */
public class Plugin extends JavaPlugin {
  /**
   * Plugin data.
   */
  private static final PluginData data = new PluginData();

  /**
   * No parameter constructor.
   */
  public Plugin() {
  }

  /**
   * On enable:
   * <ul>
   *  <li>{@link PluginData#loadResources() Loads} existing plugin data.
   *  <li>{@link #registerCommands() Registers} commands.
   *  <li>{@link #registerEventListeners() Registers} event listeners.
   *  <li>{@link #scheduleRepeatingTasks() Schedules} repeating tasks.
   * </ul>
   */
  @Override
  public void onEnable() {
    data.loadResources();
    registerCommands();
    registerEventListeners();
    scheduleRepeatingTasks();
  }

  /**
   * On disable:
   * <ul>
   *   <li>Cancels repeating tasks.
   *   <li>{@link PluginData#saveResources() Saves} persistent plugin data.
   * </ul>
   */
  @Override
  public void onDisable() {
    Bukkit.getScheduler().cancelTasks(this);
    data.saveResources();
  }

  /**
   * Registers the plugin's commands.
   * <ul>
   *  <li>{@link ItemCommand}
   *  <li>{@link BuffCommand}
   *  <li>{@link TagCommand}
   *  <li>{@link CharacterCommand}
   *  <li>{@link DeveloperCommand}
   *  <li>{@link ForgeCommand}
   *  <li>{@link ItemEditorCommand}
   *  <li>{@link LocationCommand}
   *  <li>{@link PingCommand}
   *  <li>{@link ShowItemCommand}
   *  <li>{@link StatusCommand}
   *  <li>{@link StatCommand}
   *  <li>{@link WhatsThatCommand}
   * </ul>
   */
  private void registerCommands() {
    this.getCommand("aethelitem").setExecutor(new ItemCommand());
    this.getCommand("buff").setExecutor(new BuffCommand());
    this.getCommand("aetheltag").setExecutor(new TagCommand());
    this.getCommand("character").setExecutor(new CharacterCommand());
    this.getCommand("developermode").setExecutor(new DeveloperCommand());
    this.getCommand("forge").setExecutor(new ForgeCommand());
    this.getCommand("itemeditor").setExecutor(new ItemEditorCommand());
    this.getCommand("location").setExecutor(new LocationCommand());
    this.getCommand("ping").setExecutor(new PingCommand());
    this.getCommand("showitem").setExecutor(new ShowItemCommand());
    this.getCommand("status").setExecutor(new StatusCommand());
    this.getCommand("playerstat").setExecutor(new StatCommand());
    this.getCommand("whatsthisfeature").setExecutor(new WhatsThatCommand());
  }

  /**
   * Registers the plugin's event listeners.
   * <ul>
   *  <li>{@link ActionListener}
   *  <li>{@link MenuListener}
   *  <li>{@link MessageListener}
   *  <li>{@link PluginListener}
   *  <li>{@link HealthListener}
   *  <li>{@link EquipmentListener}
   *  <li>{@link RpgListener}
   *  <li>{@link StatusListener}
   * </ul>
   */
  private void registerEventListeners() {
    PluginManager manager = getServer().getPluginManager();
    manager.registerEvents(new ActionListener(), this);
    manager.registerEvents(new MenuListener(), this);
    manager.registerEvents(new MessageListener(), this);
    manager.registerEvents(new PluginListener(), this);
    manager.registerEvents(new HealthListener(), this);
    manager.registerEvents(new EquipmentListener(), this);
    manager.registerEvents(new RpgListener(), this);
    manager.registerEvents(new StatusListener(), this);
  }

  /**
   * Schedules the plugin's repeating tasks.
   * <ul>
   *  <li>{@link PluginTask#updateMainHandEquipment()}
   *  <li>{@link PluginTask#triggerStatuses()}
   *  <li>{@link PluginTask#triggerBelowHealthPassives()}
   *  <li>{@link PluginTask#updateOvershields()}
   *  <li>{@link PluginTask#updateActionDisplay()}
   *  <li>{@link PluginTask#refreshEnchantmentEffects()}
   * </ul>
   * <p>
   * Same interval tasks are staggered by 5 ticks to distribute the workload.
   */
  private void scheduleRepeatingTasks() {
    BukkitScheduler scheduler = Bukkit.getScheduler();
    PluginTask pluginTask = new PluginTask();
    scheduler.scheduleSyncRepeatingTask(this, pluginTask::updateMainHandEquipment, 0, 10);
    scheduler.scheduleSyncRepeatingTask(this, pluginTask::triggerStatuses, 0, 20);
    scheduler.scheduleSyncRepeatingTask(this, pluginTask::triggerBelowHealthPassives, 5, 20);
    scheduler.scheduleSyncRepeatingTask(this, pluginTask::updateOvershields, 10, 20);
    scheduler.scheduleSyncRepeatingTask(this, pluginTask::updateActionDisplay, 0, 40);
    scheduler.scheduleSyncRepeatingTask(this, pluginTask::refreshEnchantmentEffects, 0, 100);
  }

  /**
   * Gets the {@link PluginData}.
   *
   * @return {@link PluginData}
   */
  @NotNull
  public static PluginData getData() {
    return data;
  }

  /**
   * Gets the plugin.
   *
   * @return plugin instance
   */
  @NotNull
  public static Plugin getInstance() {
    return getPlugin(Plugin.class);
  }
}
