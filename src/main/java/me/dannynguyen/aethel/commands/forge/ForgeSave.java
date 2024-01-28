package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.utility.ItemCreator;
import me.dannynguyen.aethel.utility.TextFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * ForgeSave is an inventory for saving forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.7.2
 * @since 1.0.5
 */
public class ForgeSave {
  /**
   * Creates and names a ForgeSave inventory.
   *
   * @param user user
   * @return ForgeSave inventory
   */
  public static Inventory createInventory(Player user) {
    Inventory inv = Bukkit.createInventory(user, 27,
        ChatColor.DARK_GRAY + "Forge" + ChatColor.DARK_GREEN + " Create");

    addCreateHelp(inv);
    inv.setItem(25, ItemCreator.
        createPluginPlayerHead("STACK_OF_PAPER", ChatColor.AQUA + "Save"));
    inv.setItem(26, ItemCreator.
        createPluginPlayerHead("GRAY_BACKWARD", ChatColor.AQUA + "Back"));
    return inv;
  }

  /**
   * Adds a help context to the create action.
   *
   * @param inv interacting inventory
   */
  private static void addCreateHelp(Inventory inv) {
    List<String> helpLore = Arrays.asList(
        ChatColor.AQUA + "Rows",
        ChatColor.AQUA + "1 " + ChatColor.WHITE + "Results",
        ChatColor.AQUA + "2 " + ChatColor.WHITE + "Components",
        ChatColor.AQUA + "3 " + ChatColor.WHITE + "Components");
    inv.setItem(8, ItemCreator.createPluginPlayerHead("WHITE_QUESTION_MARK",
        ChatColor.GREEN + "Help", helpLore));
  }

  /**
   * Checks if the ForgeSave inventory was formatted correctly before saving the recipe.
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void readSaveClick(InventoryClickEvent e, Player user) {
    ItemStack[] inv = e.getInventory().getContents();
    String fileName = nameRecipeFile(inv);
    if (fileName != null) {
      String encodedRecipe = encodeRecipe(inv);
      if (encodedRecipe != null) {
        saveRecipeToFile(user, fileName, encodedRecipe);
      } else {
        user.sendMessage(PluginMessage.Failure.FORGE_SAVE_NO_COMPONENTS.message);
      }
    } else {
      user.sendMessage(PluginMessage.Failure.FORGE_SAVE_NO_RESULTS.message);
    }
    e.setCancelled(true);
  }

  /**
   * Names a recipe by the first item in the results row.
   *
   * @param inv items in the inventory
   * @return recipe file name
   */
  private static String nameRecipeFile(ItemStack[] inv) {
    for (int i = 0; i < 8; i++) {
      ItemStack item = inv[i];
      if (item != null) {
        ItemMeta meta = item.getItemMeta();
        if (meta.hasDisplayName()) {
          return meta.getDisplayName().toLowerCase().replace(" ", "_");
        } else {
          return item.getType().name().toLowerCase();
        }
      }
    }
    return null;
  }

  /**
   * Encodes the inventory by its results and components.
   * <p>
   * At this stage in the process, it is known the results are non-null,
   * so the method checks if the components are non-null first.
   * <p>
   *
   * @param inv items in the inventory
   * @return encoded recipe string
   */
  private static String encodeRecipe(ItemStack[] inv) {
    StringBuilder components = new StringBuilder();
    for (int i = 9; i < 24; i++) {
      ItemStack item = inv[i];
      if (item != null) {
        components.append(ItemCreator.encodeItem(item)).append(" ");
      }
    }

    if (components.toString().equals("")) {
      return null;
    }

    StringBuilder results = new StringBuilder();
    for (int i = 0; i < 8; i++) {
      ItemStack item = inv[i];
      if (item != null) {
        results.append(ItemCreator.encodeItem(item)).append(" ");
      }
    }

    return results.append("\n").append(components).toString();
  }

  /**
   * Saves a recipe file to the file system.
   *
   * @param user          user
   * @param fileName      file name
   * @param encodedRecipe encoded recipe string
   * @throws IOException file could not be created
   */
  private static void saveRecipeToFile(Player user, String fileName, String encodedRecipe) {
    try {
      FileWriter fw = new FileWriter(PluginData.forgeRecipes
          + "/" + fileName + "_rcp.txt");
      fw.write(encodedRecipe);
      fw.close();
      user.sendMessage(PluginMessage.Success.FORGE_SAVE.message +
          ChatColor.WHITE + TextFormatter.capitalizePhrase(fileName));
    } catch (IOException ex) {
      user.sendMessage(PluginMessage.Failure.FORGE_SAVE_FAILED.message);
    }
  }
}
