package me.dannynguyen.aethel.plugin.enums;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Plugin directories.
 *
 * @author Danny Nguyen
 * @version 1.17.7
 * @since 1.11.9
 */
public enum Directory {
  /**
   * Resources directory.
   */
  RESOURCES(new File("./plugins/aethel")),

  /**
   * Aethel items directory.
   */
  AETHELITEM(new File(RESOURCES.getFile().getPath() + "/aethelitem")),

  /**
   * Forge recipes directory.
   */
  FORGE(new File(RESOURCES.getFile().getPath() + "/forge")),

  /**
   * RPG jewelry directory.
   */
  JEWELRY(new File(RESOURCES.getFile().getPath() + "/rpg/jewelry")),

  /**
   * RPG settings directory.
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
