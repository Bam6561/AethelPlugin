package me.dannynguyen.aethel.objects;

import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;

/**
 * ForgeRecipe is an object relating forge recipe results with their components.
 *
 * @author Danny Nguyen
 * @version 1.1.5
 * @since 1.0.3
 */
public class ForgeRecipe {
  private File file;
  private String name;
  private ArrayList<ItemStack> results;
  private ArrayList<ItemStack> components;

  public ForgeRecipe(File recipeFile, String recipeName, ArrayList<ItemStack> results, ArrayList<ItemStack> components) {
    this.file = recipeFile;
    this.name = recipeName;
    this.results = results;
    this.components = components;
  }

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
