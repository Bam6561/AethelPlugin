package me.dannynguyen.aethel.systems.plugin;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

/**
 * Plugin directories.
 *
 * @author Danny Nguyen
 * @version 1.11.9
 * @since 1.11.9
 */
public enum PluginDirectory {
  /**
   * Resources directory.
   */
  RESOURCES(new File("./plugins/Aethel")),

  /**
   * AethelItems directory.
   */
  AETHELITEM(new File(RESOURCES.getFile().getPath() + "/aitem")),

  /**
   * Forge recipes directory.
   */
  FORGE(new File(RESOURCES.getFile().getPath() + "/forge"));

  /**
   * Directory file.
   */
  private final File file;

  /**
   * Associates a directory with a file path.
   *
   * @param file file
   */
  PluginDirectory(@NotNull File file) {
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
