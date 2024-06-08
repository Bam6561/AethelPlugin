package me.bam6561.aethelplugin.plugin;

import me.bam6561.aethelplugin.enums.plugin.Directory;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents recorded logs.
 *
 * @author Danny Nguyen
 * @version 1.25.7
 * @since 1.25.7
 */
public class PluginLogger {
  /**
   * Log file.
   */
  private final File logFile;

  /**
   * Log entries in memory.
   */
  private final List<String> entries;

  /**
   * Associates a log with its file and entries.
   */
  public PluginLogger() {
    String header = Directory.LOG.getFile().getPath() + "/" + ZonedDateTime.now(ZoneId.of("America/New_York")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "-";
    int number = 1;
    File file = new File(header + number + "_log.txt");
    while (file.exists()) {
      number++;
      file = new File(header + number + "_log.txt");
    }
    this.logFile = file;
    try {
      logFile.createNewFile();
    } catch (IOException ex) {
      Bukkit.getLogger().warning("[Aethel] Failed to create log file.");
    }
    this.entries = new ArrayList<>();
  }

  /**
   * Adds a log entry to memory and writes it to the file once it reaches 100 entries.
   *
   * @param newEntry new log entry
   */
  public void addEntry(@NotNull String newEntry) {
    entries.add(newEntry);
    if (entries.size() < 100) {
      return;
    }
    saveEntries();
  }

  /**
   * Writes existing log entries to the file.
   */
  public void saveEntries() {
    try {
      FileWriter fw = new FileWriter(logFile, true);
      for (String entry : entries) {
        fw.write(entry + "\n");
      }
      fw.close();
      entries.clear();
    } catch (IOException ex) {
      Bukkit.getLogger().warning("[Aethel] Failed to write log to file.");
    }
  }
}
