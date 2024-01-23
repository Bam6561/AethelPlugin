package me.dannynguyen.aethel.inventories.forge;

import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.creators.ItemCreator;
import me.dannynguyen.aethel.formatters.TextFormatter;
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
   * @param player interacting player
   * @return ForgeSave inventory
   */
  public static Inventory createInventory(Player player) {
    Inventory inv = Bukkit.createInventory(player, 27,
        ChatColor.DARK_GRAY + "Forge" + ChatColor.DARK_GREEN + " Create");

    addCreateHelp(inv);
    inv.setItem(25, ItemCreator.
        createLoadedPlayerHead("STACK_OF_PAPER", ChatColor.AQUA + "Save"));
    inv.setItem(26, ItemCreator.
        createLoadedPlayerHead("GRAY_BACKWARD", ChatColor.AQUA + "Back"));
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
    inv.setItem(8, ItemCreator.createLoadedPlayerHead("WHITE_QUESTION_MARK",
        ChatColor.GREEN + "Help", helpLore));
  }

  /**
   * Checks if the ForgeSave inventory was formatted correctly before saving the recipe.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public static void readSaveClick(InventoryClickEvent e, Player player) {
    ItemStack[] inv = e.getInventory().getContents();
    String fileName = nameRecipeFile(inv);
    if (fileName != null) {
      String encodedRecipe = encodeRecipe(inv);
      if (encodedRecipe != null) {
        saveRecipeToFile(player, fileName, encodedRecipe);
      } else {
        player.sendMessage(ChatColor.RED + "Empty recipe components.");
      }
    } else {
      player.sendMessage(ChatColor.RED + "Empty recipe results.");
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
   * @param player        interacting player
   * @param fileName      file name
   * @param encodedRecipe encoded recipe string
   * @throws IOException file could not be created
   */
  private static void saveRecipeToFile(Player player, String fileName, String encodedRecipe) {
    try {
      FileWriter fw = new FileWriter(AethelResources.forgeRecipeDirectory
          + "/" + fileName + "_rcp.txt");
      fw.write(encodedRecipe);
      fw.close();
      player.sendMessage(ChatColor.GREEN + "[Saved Recipe] " +
          ChatColor.WHITE + TextFormatter.capitalizeProperly(fileName));
    } catch (IOException ex) {
      player.sendMessage(ChatColor.RED + "Unable to save recipe.");
    }
  }
}
