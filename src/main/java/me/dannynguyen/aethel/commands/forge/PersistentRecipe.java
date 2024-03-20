package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * Represents a recipe stored in the file system.
 *
 * @author Danny Nguyen
 * @version 1.17.7
 * @since 1.0.3
 */
class PersistentRecipe {
  /**
   * Recipe file.
   * <p>
   * - May be deleted from file system.
   * </p>
   * <p>
   * - Path persists until data is reloaded.
   * </p>
   */
  private final File file;

  /**
   * Recipe results.
   */
  private final List<ItemStack> results;

  /**
   * Recipe materials.
   */
  private final List<ItemStack> materials;

  /**
   * Effective recipe name.
   */
  private final String name;

  /**
   * Associates a recipe with its file.
   *
   * @param file      recipe file
   * @param results   recipe results
   * @param materials recipe materials
   * @throws IllegalArgumentException provided file is not a file
   */
  protected PersistentRecipe(File file, List<ItemStack> results, List<ItemStack> materials) {
    this.file = file;
    this.results = results;
    this.materials = materials;
    this.name = ItemReader.readName(results.get(0));
  }

  /**
   * Deletes the recipe file from the file system.
   */
  protected void delete() {
    file.delete();
  }

  /**
   * Gets the recipe's results.
   *
   * @return recipe results
   */
  protected List<ItemStack> getResults() {
    return this.results;
  }

  /**
   * Gets the recipe's materials.
   *
   * @return recipe materials
   */
  protected List<ItemStack> getMaterials() {
    return this.materials;
  }

  /**
   * Gets the recipe's effective name.
   *
   * @return recipe name
   */
  protected String getName() {
    return this.name;
  }
}
