package me.dannynguyen.aethel.readers;

import me.dannynguyen.aethel.objects.ForgeRecipe;
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
 * @version 1.1.8
 * @since 1.0.9
 */
public class ForgeRecipeReader {
  /**
   * Reads a recipe file.
   *
   * @param file recipe file
   * @return decoded recipe
   * @throws FileNotFoundException file not found
   */
  public ForgeRecipe readRecipe(File file) {
    ArrayList<ItemStack> results = new ArrayList<>();
    ArrayList<ItemStack> components = new ArrayList<>();
    int recipeDataType = 1;

    try {
      Scanner scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        String[] data = line.split(" ");
        for (String encodedItem : data) {
          ItemStack item = decodeItem(encodedItem);
          if (item != null) {
            switch (recipeDataType) {
              case 1 -> results.add(item);
              case 2 -> components.add(item);
            }
          }
        }
        recipeDataType++;
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
}
