package me.dannynguyen.aethel.plugin;

import me.dannynguyen.aethel.enums.plugin.Directory;
import me.dannynguyen.aethel.enums.plugin.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Represents saved locations.
 *
 * @author Danny Nguyen
 * @version 1.22.6
 * @since 1.22.5
 */
public class LocationRegistry {
  /**
   * Location registry owner.
   */
  private final UUID uuid;

  /**
   * Saved locations.
   */
  private final Map<String, Location> locations = new HashMap<>();

  /**
   * Associates a location registry with a player.
   *
   * @param uuid uuid
   */
  public LocationRegistry(@NotNull UUID uuid) {
    this.uuid = Objects.requireNonNull(uuid, "Null UUID");
    loadLocations();
  }

  /**
   * Loads locations from a file if it exists.
   */
  private void loadLocations() {
    File file = new File(Directory.LOCATION.getFile().getPath() + "/" + uuid.toString() + "_loc.txt");
    if (file.exists()) {
      try {
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
          String line = scanner.nextLine();
          if (line.isEmpty()) {
            continue;
          }
          String[] locationString = line.split(" ");
          String name = locationString[0];
          World world = Bukkit.getServer().getWorld(locationString[1]);
          double x = Double.parseDouble(locationString[2]);
          double y = Double.parseDouble(locationString[3]);
          double z = Double.parseDouble(locationString[4]);
          locations.put(name, new Location(world, x, y, z));
        }
        scanner.close();
      } catch (IOException ex) {
        Bukkit.getLogger().warning(Message.UNABLE_TO_READ_FILE.getMessage() + file.getName());
      }
    }
  }

  /**
   * Saves locations to a file.
   */
  public void saveLocations() {
    File file = new File(Directory.LOCATION.getFile().getPath() + "/" + uuid + "_loc.txt");
    try {
      FileWriter fw = new FileWriter(file);
      for (String name : locations.keySet()) {
        Location location = locations.get(name);
        fw.write(name + " " + location.getWorld().getName() + " " + location.getX() + " " + location.getY() + " " + location.getZ() + "\n");
      }
      fw.close();
    } catch (IOException ex) {
      Bukkit.getLogger().warning("[Aethel] Failed to write " + uuid + "'s locations to file.");
    }
  }

  /**
   * Gets saved locations.
   *
   * @return saved locations
   */
  @NotNull
  public Map<String, Location> getLocations() {
    return this.locations;
  }
}
