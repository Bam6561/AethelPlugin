package me.dannynguyen.aethel.systems.plugin;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

/**
 * Plugin directories.
 *
 * @author Danny Nguyen
 * @version 1.12.1
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
  JEWELRY(new File(RESOURCES.getFile().getPath() + "/rpg/jewelry"));

  /**
   * Directory file.
   */
  private final File file;

  /**
   * Associates a directory with a file path.
   *
   * @param file file
   */
  Directory(@NotNull File file) {
    this.file = Objects.requireNonNull(file, "Null file");
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
