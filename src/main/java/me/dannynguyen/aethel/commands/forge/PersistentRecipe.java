package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * Represents a recipe stored in the file system.
 *
 * @author Danny Nguyen
 * @version 1.11.6
 * @since 1.0.3
 */
class PersistentRecipe {
  /**
   * Recipe file.
   * <p>
   * - May be deleted from file system.
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
  protected PersistentRecipe(@NotNull File file, @NotNull List<ItemStack> results, @NotNull List<ItemStack> materials) {
    if (file.isFile()) {
      this.file = Objects.requireNonNull(file, "Null file");
      this.results = Objects.requireNonNull(results, "Null results");
      this.materials = Objects.requireNonNull(materials, "Null materials");
      this.name = ItemReader.readName(results.get(0));
    } else {
      throw new IllegalArgumentException("Non-file");
    }
  }

  /**
   * Deletes the recipe file from the file system.
   *
   * @return true if the file was deleted
   */
  protected boolean delete() {
    return getFile().delete();
  }

  /**
   * Gets the file the recipe is stored in.
   *
   * @return recipe file
   */
  @NotNull
  protected File getFile() {
    return this.file;
  }

  /**
   * Gets the recipe's results.
   *
   * @return recipe results
   */
  @NotNull
  protected List<ItemStack> getResults() {
    return this.results;
  }

  /**
   * Gets the recipe's materials.
   *
   * @return recipe materials
   */
  @NotNull
  protected List<ItemStack> getMaterials() {
    return this.materials;
  }

  /**
   * Gets the recipe's effective name.
   *
   * @return recipe name
   */
  @NotNull
  protected String getName() {
    return this.name;
  }

}
