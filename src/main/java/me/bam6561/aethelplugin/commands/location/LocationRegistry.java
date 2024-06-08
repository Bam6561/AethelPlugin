package me.bam6561.aethelplugin.commands.location;

import me.bam6561.aethelplugin.enums.plugin.Directory;
import me.bam6561.aethelplugin.enums.plugin.Message;
import me.bam6561.aethelplugin.utils.InventoryPages;
import me.bam6561.aethelplugin.utils.item.ItemCreator;
import org.bukkit.*;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Represents a {@link me.bam6561.aethelplugin.plugin.PluginPlayer player's} saved locations.
 *
 * @author Danny Nguyen
 * @version 1.24.7
 * @since 1.22.5
 */
public class LocationRegistry {
  /**
   * LocationRegistry directory.
   */
  private final File directory;

  /**
   * Loaded {@link SavedLocation saved locations}.
   */
  private final Map<String, SavedLocation> locations = new HashMap<>();

  /**
   * {@link SavedLocation Saved locations} represented by groups of inventories.
   * <p>
   * An inventory from any of the groups is also referred to as a page.
   */
  private final Map<String, List<Inventory>> locationCategories = new HashMap<>();

  /**
   * Sorted {@link SavedLocation} category names.
   */
  private final List<String> locationCategoryNames = new ArrayList<>();

  /**
   * Associates a LocationRegistry with a player.
   *
   * @param uuid uuid
   */
  public LocationRegistry(@NotNull UUID uuid) {
    this.directory = new File(Directory.LOCATION.getFile().getPath() + "/" + Objects.requireNonNull(uuid, "Null UUID"));
    loadData();
  }

  /**
   * Loads {@link SavedLocation saved locations} into memory.
   */
  protected void loadData() {
    if (!directory.exists()) {
      return;
    }
    File[] files = directory.listFiles();
    if (files == null) {
      return;
    }

    locations.clear();
    locationCategories.clear();
    locationCategoryNames.clear();

    if (files.length == 0) {
      return;
    }

    Arrays.sort(files);
    Map<String, List<SavedLocation>> categories = new HashMap<>();
    parseDirectory(files, categories);

    if (locations.isEmpty()) {
      return;
    }

    for (String category : categories.keySet()) {
      locationCategories.put(category, createPages(categories.get(category)));
      locationCategoryNames.add(category);
    }
    Collections.sort(locationCategoryNames);
  }

  /**
   * Recursively parses the directory and reads location files.
   *
   * @param directory  location directory
   * @param categories location categories
   */
  private void parseDirectory(File[] directory, Map<String, List<SavedLocation>> categories) {
    for (File file : directory) {
      if (file.isFile()) {
        if (file.getName().endsWith("_loc.txt")) {
          readFile(file, categories);
        }
      } else {
        File[] subdirectory = file.listFiles();
        if (subdirectory.length == 0) {
          file.delete();
        } else {
          Arrays.sort(subdirectory);
          parseDirectory(subdirectory, categories);
        }
      }
    }
  }

  /**
   * Converts the designated location file into a
   * {@link SavedLocation} that is then sorted into a category.
   *
   * @param file       location file
   * @param categories location categories
   */
  private void readFile(File file, Map<String, List<SavedLocation>> categories) {
    try {
      Scanner scanner = new Scanner(file);
      String[] locationString = scanner.nextLine().split(" ");
      scanner.close();

      String name = locationString[0];
      World world = Bukkit.getServer().getWorld(locationString[1]);
      double x = Double.parseDouble(locationString[2]);
      double y = Double.parseDouble(locationString[3]);
      double z = Double.parseDouble(locationString[4]);

      SavedLocation savedLocation = new SavedLocation(file, name, new Location(world, x, y, z));
      locations.put(name, savedLocation);

      String category = file.getParentFile().getName();
      if (categories.containsKey(category)) {
        categories.get(category).add(savedLocation);
      } else {
        categories.put(category, new ArrayList<>(List.of(savedLocation)));
      }
    } catch (IOException ex) {
      Bukkit.getLogger().warning(Message.UNABLE_TO_READ_FILE.getMessage() + file.getName());
    }
  }

  /**
   * Creates an {@link SavedLocation} category's pages.
   *
   * @param locations saved locations from an {@link SavedLocation} category
   * @return {@link SavedLocation} category's pages
   */
  private List<Inventory> createPages(List<SavedLocation> locations) {
    int totalLocations = locations.size();
    int totalPages = InventoryPages.getTotalPages(totalLocations);

    List<Inventory> pages = new ArrayList<>();
    int pageStart = 0;
    int pageEnd = Math.min(totalLocations, 45);

    for (int page = 0; page < totalPages; page++) {
      Inventory inv = Bukkit.createInventory(null, 54);

      int invSlot = 9;
      for (int locationIndex = pageStart; locationIndex < pageEnd; locationIndex++) {
        SavedLocation savedLocation = locations.get(locationIndex);
        Location location = savedLocation.getLocation();
        String locationString = ChatColor.WHITE + location.getWorld().getName() + " " + location.getX() + " " + location.getY() + " " + location.getZ();
        inv.setItem(invSlot, ItemCreator.createItem(Material.PAPER, savedLocation.getName(), List.of(locationString)));
        invSlot++;
      }
      pages.add(inv);

      // Indices to use for the next page (if it exists)
      pageStart += 45;
      pageEnd = Math.min(totalLocations, pageEnd + 45);
    }
    return pages;
  }

  /**
   * Gets the location registry directory.
   *
   * @return location registry directory
   */
  private File getDirectory() {
    return this.directory;
  }

  /**
   * Gets {@link SavedLocation saved locations}.
   *
   * @return {@link SavedLocation saved locations}
   */
  @NotNull
  protected Map<String, SavedLocation> getLocations() {
    return this.locations;
  }

  /**
   * Gets {@link SavedLocation} categories.
   *
   * @return {@link SavedLocation} categories
   */
  @NotNull
  protected Map<String, List<Inventory>> getLocationCategories() {
    return this.locationCategories;
  }

  /**
   * Gets sorted {@link SavedLocation} category names.
   *
   * @return sorted {@link SavedLocation} category names
   */
  @NotNull
  protected List<String> getLocationCategoryNames() {
    return this.locationCategoryNames;
  }

  /**
   * Represents a saved location.
   *
   * @author Danny Nguyen
   * @version 1.24.7
   * @since 1.24.7
   */
  public static class SavedLocation {
    /**
     * Location file.
     */
    private final File file;

    /**
     * Location name.
     */
    private final String name;

    /**
     * World and coordinates.
     */
    private final Location location;

    /**
     * Associates a saved location with its file, name, and location.
     *
     * @param file     location file
     * @param name     location name
     * @param location Location
     */
    private SavedLocation(File file, String name, Location location) {
      this.file = file;
      this.name = name;
      this.location = location;
    }

    /**
     * Associates a new location file to save with its folder, name, and location.
     *
     * @param locationRegistry {@link LocationRegistry}
     * @param folder           folder to save the file under
     * @param name             location name
     * @param location         Location to save
     * @throws NullPointerException failed to write location to file
     */
    public SavedLocation(@NotNull LocationRegistry locationRegistry, @NotNull String folder, @NotNull String name, @NotNull Location location) throws NullPointerException {
      Objects.requireNonNull(locationRegistry, "Null location registry");
      Objects.requireNonNull(folder, "Null folder");
      Objects.requireNonNull(name, "Null name");
      Objects.requireNonNull(location, "Null location");

      this.file = saveFile(locationRegistry, folder, name, location);
      if (file == null) {
        throw new NullPointerException("Null file");
      }
      this.name = name;
      this.location = location;
    }

    /**
     * Saves the location file to the file system.
     *
     * @param locationRegistry {@link LocationRegistry}
     * @param folder           location folder
     * @param name             location name
     * @param location         location to save
     * @return location file
     */
    private File saveFile(LocationRegistry locationRegistry, String folder, String name, Location location) {
      File directory = new File(locationRegistry.getDirectory().getPath() + "/" + folder);
      directory.mkdirs();

      File file = new File(directory.getPath() + "/" + name + "_loc.txt");
      try {
        FileWriter fw = new FileWriter(file);
        fw.write(name + " " + location.getWorld().getName() + " " + location.getX() + " " + location.getY() + " " + location.getZ());
        fw.close();
        return file;
      } catch (IOException ex) {
        Bukkit.getLogger().warning("[Aethel] Failed to write location to file.");
        return null;
      }
    }

    /**
     * Deletes the location file from the file system.
     */
    protected void delete() {
      file.delete();
    }

    /**
     * Gets the saved location's name.
     *
     * @return saved location's name
     */
    @NotNull
    protected String getName() {
      return this.name;
    }

    /**
     * Gets the saved location.
     *
     * @return saved location
     */
    @NotNull
    protected Location getLocation() {
      return this.location;
    }
  }
}
