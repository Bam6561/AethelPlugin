package me.dannynguyen.aethel.objects;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * ForgeRecipe is an object relating forge recipe results with their components.
 *
 * @author Danny Nguyen
 * @version 1.0.4
 * @since 1.0.3
 */
public class ForgeRecipe {
  private ArrayList<ItemStack> results;
  private ArrayList<ItemStack> components;

  public ForgeRecipe(ArrayList<ItemStack> results, ArrayList<ItemStack> components) {
    this.results = results;
    this.components = components;
  }

  public ArrayList<ItemStack> getResults() {
    return this.results;
  }

  public ArrayList<ItemStack> getComponents() {
    return this.components;
  }

  public void setResults(ArrayList<ItemStack> results) {
    this.results = results;
  }

  public void setComponents(ArrayList<ItemStack> components) {
    this.components = components;
  }
}
