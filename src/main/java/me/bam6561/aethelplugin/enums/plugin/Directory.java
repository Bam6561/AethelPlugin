package me.bam6561.aethelplugin.enums.plugin;

import me.bam6561.aethelplugin.commands.location.LocationCommand;
import me.bam6561.aethelplugin.rpg.Equipment;
import me.bam6561.aethelplugin.rpg.Settings;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Plugin directories.
 *
 * @author Danny Nguyen
 * @version 1.25.7
 * @since 1.11.9
 */
public enum Directory {
  /**
   * Resources directory.
   */
  RESOURCES(new File("./plugins/Aethel")),

  /**
   * {@link me.bam6561.aethelplugin.commands.aethelitem.ItemRegistry Aethel items} directory.
   */
  AETHELITEM(new File(RESOURCES.getFile().getPath() + "/aethelitem")),

  /**
   * {@link me.bam6561.aethelplugin.commands.forge.RecipeRegistry Forge recipes} directory.
   */
  FORGE(new File(RESOURCES.getFile().getPath() + "/forge")),

  /**
   * {@link me.bam6561.aethelplugin.plugin.PluginLogger Log} directory.
   */
  LOG(new File(RESOURCES.getFile().getPath() + "/log/")),

  /**
   * {@link LocationCommand Locations} directory.
   */
  LOCATION(new File(RESOURCES.getFile().getPath() + "/location")),

  /**
   * {@link Equipment RPG jewelry} directory.
   */
  JEWELRY(new File(RESOURCES.getFile().getPath() + "/rpg/jewelry")),

  /**
   * {@link Settings RPG settings} directory.
   */
  SETTINGS(new File(RESOURCES.getFile().getPath() + "/rpg/settings"));

  /**
   * Directory file.
   */
  private final File file;

  /**
   * Associates a directory with a file path.
   *
   * @param file file
   */
  Directory(File file) {
    this.file = file;
  }

  /**
   * Gets the directory as a file.
   *
   * @return directory as a file
   */
  @NotNull
  public File getFile() {
    return this.file;
  }
}
