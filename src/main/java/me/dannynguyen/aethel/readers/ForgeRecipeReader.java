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
 * ForgeRecipeReader decodes forge recipes from the file system.
 *
 * @author Danny Nguyen
 * @version 1.2.3
 * @since 1.0.9
 */
public class ForgeRecipeReader {
  /**
   * Reads a recipe file.
   * <p>
   * Data is stored in two lines of text, represented by the variable dataType.
   * - [1] Results
   * - [2] Components
   * </p>
   *
   * @param file recipe file
   * @return decoded recipe
   * @throws FileNotFoundException file not found
   */
  public ForgeRecipe readRecipe(File file) {
    ArrayList<ItemStack> results = new ArrayList<>();
    ArrayList<ItemStack> components = new ArrayList<>();
    int dataType = 1;

    try {
      Scanner scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        readLine(scanner.nextLine(), dataType, results, components);
        dataType++;
      }
      return new ForgeRecipe(file, new ItemMetaReader().getItemName(results.get(0)), results, components);
    } catch (FileNotFoundException ex) {
      return null;
    }
  }

  /**
   * Reads a line of text from the file and adds decoded items to the recipe.
   * <p>
   * Individual encoded items are separated by spaces.
   * </p>
   *
   * @param line       text line
   * @param dataType   [1] Results | [2] Components
   * @param results    recipe results
   * @param components recipe components
   */
  private void readLine(String line, int dataType, ArrayList<ItemStack> results, ArrayList<ItemStack> components) {
    String[] data = line.split(" ");
    for (String encodedItem : data) {
      ItemStack item = decodeItem(encodedItem);
      if (item != null) {
        switch (dataType) {
          case 1 -> results.add(item);
          case 2 -> components.add(item);
        }
      }
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
