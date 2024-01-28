package me.dannynguyen.aethel.enums;

import java.io.File;

/**
 * PluginDirectory is an enum containing the plugin's resource directories.
 *
 * @author Danny Nguyen
 * @version 1.8.0
 * @since 1.7.11
 */
public enum PluginDirectory {
  RESOURCES(new File("./plugins/Aethel")),
  AETHELITEMS(new File(RESOURCES.file.getPath() + "/aitem")),
  FORGE(new File(RESOURCES.file.getPath() + "/forge"));

  public final File file;

  PluginDirectory(File file) {
    this.file = file;
  }
}
