package me.dannynguyen.aethel.enums;

import java.io.File;

/**
 * PluginDirectory is an enum containing file paths to the plugin's resource directories.
 *
 * @author Danny Nguyen
 * @version 1.7.11
 * @since 1.7.11
 */
public enum PluginDirectory {
  RESOURCES(new File("./plugins/Aethel")),
  AETHELITEMS(new File(RESOURCES + "/aitem")),
  FORGE(new File(RESOURCES + "/forge"));

  public File filePath;

  PluginDirectory(File filePath) {
    this.filePath = filePath;
  }
}
