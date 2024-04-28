package me.dannynguyen.aethel.enums.plugin;

import me.dannynguyen.aethel.commands.location.LocationCommand;
import me.dannynguyen.aethel.rpg.Equipment;
import me.dannynguyen.aethel.rpg.Settings;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Plugin directories.
 *
 * @author Danny Nguyen
 * @version 1.22.5
 * @since 1.11.9
 */
public enum Directory {
  /**
   * Resources directory.
   */
  RESOURCES(new File("./plugins/Aethel")),

  /**
   * {@link me.dannynguyen.aethel.commands.aethelitem.ItemRegistry Aethel items} directory.
   */
  AETHELITEM(new File(RESOURCES.getFile().getPath() + "/aethelitem")),

  /**
   * {@link me.dannynguyen.aethel.commands.forge.RecipeRegistry Forge recipes} directory.
   */
  FORGE(new File(RESOURCES.getFile().getPath() + "/forge")),

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
