package me.dannynguyen.aethel.inventories.forge;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.creators.ItemCreator;
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
 * ForgeCreate is an inventory under the Forge command that creates forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.4.3
 * @since 1.0.5
 */
public class ForgeCreate {
  /**
   * Creates and names a ForgeCreate inventory.
   *
   * @param player interacting player
   * @return ForgeCreate inventory
   */
  public Inventory createInventory(Player player) {
    String title = ChatColor.DARK_GRAY + "Forge" + ChatColor.DARK_GREEN + " Create";
    Inventory inv = Bukkit.createInventory(player, 27, title);

    ItemCreator itemCreator = new ItemCreator();
    addCreateHelp(inv);
    inv.setItem(25, itemCreator.
        createPlayerHead("Stack of Paper", ChatColor.AQUA + "Save"));
    inv.setItem(26, itemCreator.
        createPlayerHead("Gray Backward", ChatColor.AQUA + "Back"));
    return inv;
  }

  /**
   * Adds a help context to the create action.
   *
   * @param inv interacting inventory
   */
  private void addCreateHelp(Inventory inv) {
    List<String> helpLore = Arrays.asList(
        ChatColor.AQUA + "Rows",
        ChatColor.AQUA + "1 " + ChatColor.WHITE + "Results",
        ChatColor.AQUA + "2 " + ChatColor.WHITE + "Components",
        ChatColor.AQUA + "3 " + ChatColor.WHITE + "Components");
    inv.setItem(8, new ItemCreator().createPlayerHead("White Question Mark",
        ChatColor.GREEN + "Help", helpLore));
  }

  /**
   * Checks if the ForgeCreate inventory was formatted correctly before saving the recipe.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public void readSaveClick(InventoryClickEvent e, Player player) {
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
  private String nameRecipeFile(ItemStack[] inv) {
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
  private String encodeRecipe(ItemStack[] inv) {
    ItemCreator itemCreator = new ItemCreator();

    StringBuilder components = new StringBuilder();
    for (int i = 9; i < 24; i++) {
      ItemStack item = inv[i];
      if (item != null) components.append(itemCreator.encodeItem(item) + " ");
    }

    if (components.toString().equals("")) return null;

    StringBuilder results = new StringBuilder();
    for (int i = 0; i < 8; i++) {
      ItemStack item = inv[i];
      if (item != null) results.append(itemCreator.encodeItem(item) + " ");
    }

    return results.append("\n" + components).toString();
  }

  /**
   * Saves a recipe file to the file system.
   *
   * @param player      interacting player
   * @param itemName    item name
   * @param encodedItem encoded item string
   * @throws IOException file could not be created
   */
  private void saveRecipeToFile(Player player, String itemName, String encodedItem) {
    try {
      FileWriter fw = new FileWriter(AethelPlugin.getInstance().getResources().getForgeRecipeDirectory()
          + "/" + itemName + "_rcp.txt");
      fw.write(encodedItem);
      fw.close();
      player.sendMessage(ChatColor.GREEN + "[Saved] " + ChatColor.WHITE + itemName + "_rcp.txt");
    } catch (IOException ex) {
      player.sendMessage(ChatColor.RED + "Unable to save recipe.");
    }
  }
}
