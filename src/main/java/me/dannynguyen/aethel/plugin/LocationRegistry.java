package me.dannynguyen.aethel.plugin;

import me.dannynguyen.aethel.enums.plugin.Directory;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents saved locations.
 *
 * @author Danny Nguyen
 * @version 1.22.5
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
      // TODO
    }
  }

  /**
   * Gets the saved locations.
   *
   * @return saved locations
   */
  public Map<String, Location> getLocations() {
    return this.locations;
  }
}
