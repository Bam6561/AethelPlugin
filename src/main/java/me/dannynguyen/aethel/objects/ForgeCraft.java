package me.dannynguyen.aethel.objects;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * ForgeCraft is an object relating a craftable item with its recipe.
 *
 * @author Danny Nguyen
 * @version 1.0.3
 * @since 1.0.3
 */
public class ForgeCraft {
  private ItemStack craftItem;
  private ArrayList<ItemStack> craftRecipe;

  public ForgeCraft(ItemStack craftItem, ArrayList<ItemStack> recipeItems) {
    this.craftItem = craftItem;
    this.craftRecipe = recipeItems;
  }

  public ItemStack getCraftItem() {
    return this.craftItem;
  }

  public ArrayList<ItemStack> getCraftRecipe() {
    return this.craftRecipe;
  }

  public void setCraftItem(ItemStack craftItem) {
    this.craftItem = craftItem;
  }

  public void setCraftRecipe(ArrayList<ItemStack> craftRecipe) {
    this.craftRecipe = craftRecipe;
  }
}
