package me.dannynguyen.aethel.systems.plugin;

/**
 * Represents a data storage container for data that exists
 * in files and must be loaded from the file system.
 * <p>
 * After the registry's creation, {@link #loadData() loadData} must
 * be called in order to load items from its associated directory.
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.17.5
 * @since 1.17.5
 */
public interface Registry {
  /**
   * Loads data from file system.
   */
  void loadData();
}
