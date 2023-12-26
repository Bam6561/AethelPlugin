package me.dannynguyen.aethel.objects;

import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;

/**
 * ForgeRecipe is an object relating forge recipe results with their components.
 *
 * @author Danny Nguyen
 * @version 1.0.9
 * @since 1.0.3
 */
public class ForgeRecipe {
  private File recipeFile;
  private String recipeName;
  private ArrayList<ItemStack> results;
  private ArrayList<ItemStack> components;

  public ForgeRecipe(File recipeFile, String recipeName, ArrayList<ItemStack> results, ArrayList<ItemStack> components) {
    this.recipeFile = recipeFile;
    this.recipeName = recipeName;
    this.results = results;
    this.components = components;
  }

  public File getRecipeFile() {
    return this.recipeFile;
  }

  public String getRecipeName() {
    return this.recipeName;
  }

  public ArrayList<ItemStack> getResults() {
    return this.results;
  }

  public ArrayList<ItemStack> getComponents() {
    return this.components;
  }

  public void setRecipeFile(File recipeFile) {
    this.recipeFile = recipeFile;
  }

  public void setRecipeName(String recipeName) {
    this.recipeName = recipeName;
  }

  public void setResults(ArrayList<ItemStack> results) {
    this.results = results;
  }

  public void setComponents(ArrayList<ItemStack> components) {
    this.components = components;
  }
}
