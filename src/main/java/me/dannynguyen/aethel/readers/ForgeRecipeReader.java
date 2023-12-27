package me.dannynguyen.aethel.readers;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.objects.ForgeRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Scanner;

/**
 * ForgeRecipeReader decodes forge recipes from storage and retrieves loaded recipes from memory.
 *
 * @author Danny Nguyen
 * @version 1.1.5
 * @since 1.0.9
 */
public class ForgeRecipeReader {
  /**
   * (Re)loads recipes into memory.
   */
  public void loadForgeRecipes() {
    AethelPlugin aethelPlugin = AethelPlugin.getInstance();
    ArrayList<ForgeRecipe> forgeRecipes = aethelPlugin.getForgeRecipes();
    HashMap<String, Integer> forgeRecipesIndex = aethelPlugin.getForgeRecipesIndex();
    forgeRecipes.clear();
    forgeRecipesIndex.clear();

    File[] forgeRecipeDirectory = new File(aethelPlugin.getResourceDirectory() + "/forge").listFiles();
    for (int i = 0; i < forgeRecipeDirectory.length; i++) {
      ForgeRecipe forgeRecipe = readRecipe(forgeRecipeDirectory[i]);
      forgeRecipes.add(forgeRecipe);
      forgeRecipesIndex.put(forgeRecipe.getRecipeName(), i);
    }
  }

  /**
   * Reads a recipe file.
   *
   * @param file recipe file
   * @return decoded recipe
   * @throws FileNotFoundException file not found
   */
  private ForgeRecipe readRecipe(File file) {
    ArrayList<ItemStack> results = new ArrayList<>();
    ArrayList<ItemStack> components = new ArrayList<>();
    int recipeDataType = 1;

    try {
      Scanner scanner = new Scanner(file);
      scanner.nextLine(); // Skip Results line
      while (scanner.hasNextLine()) {
        String data = scanner.nextLine();

        if (data.equals("Components")) {
          recipeDataType++;
          data = scanner.nextLine();
        }

        ItemStack item = decodeItem(data);
        if (item != null) {
          switch (recipeDataType) {
            case 1 -> results.add(decodeItem(data));
            case 2 -> components.add(decodeItem(data));
          }
        }
      }
      return new ForgeRecipe(file, new ItemMetaReader().getItemName(results.get(0)), results, components);
    } catch (FileNotFoundException ex) {
      return null;
    }
  }

  /**
   * Deserializes an item.
   *
   * @param data serialized item string
   * @return item
   * @throws IOException            file not found
   * @throws ClassNotFoundException item could not be decoded
   */
  private ItemStack decodeItem(String data) {
    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(data));
      BukkitObjectInputStream bois = new BukkitObjectInputStream(bais);
      ItemStack item = (ItemStack) bois.readObject();
      return item;
    } catch (IOException | ClassNotFoundException ex) {
      return null;
    }
  }

  /**
   * Matches the clicked item to its recipe.
   *
   * @param e inventory click event
   * @return index of the matching item
   */
  public int getRecipeIndex(ItemStack e) {
    String itemName = new ItemMetaReader().getItemName(e);
    HashMap<String, Integer> forgeRecipesIndex = new HashMap<>(AethelPlugin.getInstance().getForgeRecipesIndex());
    return forgeRecipesIndex.get(itemName);
  }
}
