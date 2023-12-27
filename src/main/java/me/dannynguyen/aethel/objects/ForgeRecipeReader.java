package me.dannynguyen.aethel.objects;

import me.dannynguyen.aethel.AethelPlugin;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;

/**
 * ForgeRecipeReader decodes forge recipes from storage.
 *
 * @author Danny Nguyen
 * @version 1.1.3
 * @since 1.0.9
 */
public class ForgeRecipeReader {
  /**
   * (Re)loads recipes into memory.
   */
  public void loadForgeRecipes() {
    AethelPlugin aethelPlugin = AethelPlugin.getInstance();
    ArrayList<ForgeRecipe> forgeRecipes = aethelPlugin.getForgeRecipes();
    forgeRecipes.clear();

    File[] forgeRecipeDirectory = new File(aethelPlugin.getResourceDirectory() + "/forge").listFiles();
    for (File file : forgeRecipeDirectory) {
      forgeRecipes.add(readRecipe(file));
    }
  }

  /**
   * Reads a recipe file.
   *
   * @param file recipe file
   * @throws FileNotFoundException file not found
   */
  public ForgeRecipe readRecipe(File file) {
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
      return new ForgeRecipe(file, getItemName(results.get(0)), results, components);
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
   * Returns either an item's renamed value or its material.
   *
   * @param item item
   * @return effective item name
   */
  private String getItemName(ItemStack item) {
    if (item.getItemMeta().hasDisplayName()) {
      return item.getItemMeta().getDisplayName();
    } else {
      return item.getType().name();
    }
  }
}
