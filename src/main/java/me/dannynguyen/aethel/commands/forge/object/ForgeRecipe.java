package me.dannynguyen.aethel.commands.forge.object;

import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;

/**
 * ForgeRecipe is an object relating recipe results with their components.
 *
 * @author Danny Nguyen
 * @version 1.7.13
 * @since 1.0.3
 */
public record ForgeRecipe(File file, String name,
                          ArrayList<ItemStack> results, ArrayList<ItemStack> components) {
  public File getFile() {
    return this.file;
  }

  public String getName() {
    return this.name;
  }

  public ArrayList<ItemStack> getResults() {
    return this.results;
  }

  public ArrayList<ItemStack> getComponents() {
    return this.components;
  }
}
