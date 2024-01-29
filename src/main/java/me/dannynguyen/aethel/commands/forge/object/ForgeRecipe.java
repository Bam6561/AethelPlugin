package me.dannynguyen.aethel.commands.forge.object;

import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

/**
 * ForgeRecipe is an object relating recipe results with their components.
 *
 * @author Danny Nguyen
 * @version 1.8.2
 * @since 1.0.3
 */
public record ForgeRecipe(File file, String name,
                          List<ItemStack> results, List<ItemStack> components) {
  public File getFile() {
    return this.file;
  }

  public String getName() {
    return this.name;
  }

  public List<ItemStack> getResults() {
    return this.results;
  }

  public List<ItemStack> getComponents() {
    return this.components;
  }
}
