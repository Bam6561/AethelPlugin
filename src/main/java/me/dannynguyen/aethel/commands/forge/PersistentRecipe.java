package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.util.ItemReader;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

/**
 * Represents a recipe stored in the file system.
 * <p>
 * Loaded into memory when {@link RecipeRegistry#loadData()} is called.
 *
 * @author Danny Nguyen
 * @version 1.17.12
 * @since 1.0.3
 */
public class PersistentRecipe {
  /**
   * Recipe file.
   * <ul>
   *  <li>May be deleted from file system.
   *  <li>Path persists until data is reloaded.
   * </ul>
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
   * @throws IllegalArgumentException if provided file is not a file
   */
  PersistentRecipe(File file, List<ItemStack> results, List<ItemStack> materials) {
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
