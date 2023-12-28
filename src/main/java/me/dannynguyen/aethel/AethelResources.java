package me.dannynguyen.aethel;

import me.dannynguyen.aethel.objects.ForgeRecipe;
import me.dannynguyen.aethel.readers.ForgeRecipeReader;
import me.dannynguyen.aethel.readers.ItemMetaReader;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * AethelResources represents the plugin's resources loaded in memory as an object.
 *
 * @author Danny Nguyen
 * @version 1.1.7
 * @since 1.1.7
 */
public class AethelResources {
  private String resourceDirectory = "./plugins/Aethel";
  private String forgeRecipeDirectory = resourceDirectory + "/forge";
  private ArrayList<ForgeRecipe> forgeRecipes = new ArrayList<>();
  private HashMap<String, Integer> forgeRecipesIndex = new HashMap<>();

  /**
   * (Re)loads recipes into memory.
   */
  public void loadForgeRecipes() {
    ArrayList<ForgeRecipe> forgeRecipes = getForgeRecipes();
    HashMap<String, Integer> forgeRecipesIndex = getForgeRecipesIndex();
    forgeRecipes.clear();
    forgeRecipesIndex.clear();

    File[] forgeRecipeDirectory = new File(getForgeRecipeDirectory()).listFiles();
    Collections.sort(Arrays.asList(forgeRecipeDirectory));
    for (int i = 0; i < forgeRecipeDirectory.length; i++) {
      ForgeRecipe forgeRecipe = new ForgeRecipeReader().readRecipe(forgeRecipeDirectory[i]);
      forgeRecipes.add(forgeRecipe);
      forgeRecipesIndex.put(forgeRecipe.getRecipeName(), i);
    }
  }

  /**
   * Matches the clicked item to its recipe index.
   *
   * @param e inventory click event
   * @return index of the matching item
   */
  public int getRecipeIndex(ItemStack e) {
    String itemName = new ItemMetaReader().getItemName(e);
    HashMap<String, Integer> forgeRecipesIndex =
        new HashMap<>(AethelPlugin.getInstance().getResources().getForgeRecipesIndex());
    return forgeRecipesIndex.get(itemName);
  }

  public String getResourceDirectory() {
    return this.resourceDirectory;
  }

  public String getForgeRecipeDirectory() {
    return this.forgeRecipeDirectory;
  }

  public ArrayList<ForgeRecipe> getForgeRecipes() {
    return this.forgeRecipes;
  }

  public HashMap<String, Integer> getForgeRecipesIndex() {
    return this.forgeRecipesIndex;
  }
}
