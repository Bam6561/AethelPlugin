package me.dannynguyen.aethel.inventories.forge;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.creators.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;

/**
 * ForgeCreate is an inventory under the Forge command that creates forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.1.5
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
    inv.setItem(25, itemCreator.createItem(Material.GREEN_CONCRETE, "Save Recipe"));
    inv.setItem(26, itemCreator.createItem(Material.ARROW, "Back"));
    return inv;
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
        player.sendMessage(ChatColor.RED + "Recipe components cannot be empty.");
      }
    } else {
      player.sendMessage(ChatColor.RED + "Recipe results cannot be empty.");
    }
    e.setCancelled(true);
  }

  /**
   * Names a recipe by the first item in the results row.
   *
   * @param inv items in the inventory
   * @return name of the recipe
   */
  private String nameRecipeFile(ItemStack[] inv) {
    for (int i = 0; i < 9; i++) {
      ItemStack item = inv[i];
      if (item != null) {
        if (item.getItemMeta().hasDisplayName()) {
          return item.getItemMeta().getDisplayName().toLowerCase().replace(" ", "_");
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
    StringBuilder components = new StringBuilder("Components\n");
    for (int i = 9; i < 24; i++) {
      ItemStack item = inv[i];
      if (item != null) components.append(encodeItem(inv[i]) + "\n");
    }

    if (components.toString().equals("Components\n")) return null;

    StringBuilder results = new StringBuilder("Results\n");
    for (int i = 0; i < 9; i++) {
      ItemStack item = inv[i];
      if (item != null) results.append(encodeItem(inv[i]) + "\n");
    }

    return results.append(components).toString();
  }

  /**
   * Encodes an item into bytes.
   *
   * @param item item to encode
   * @return encoded item string
   * @throws IOException item could not be encoded
   */
  private String encodeItem(ItemStack item) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      BukkitObjectOutputStream boos = new BukkitObjectOutputStream(baos);
      boos.writeObject(item);
      boos.flush();
      return Base64.getEncoder().encodeToString(baos.toByteArray());
    } catch (IOException ex) {
      return null;
    }
  }

  /**
   * Saves a recipe file to storage.
   *
   * @param itemName    item name
   * @param encodedItem encoded item string
   * @throws IOException file could not be created
   */
  private void saveRecipeToFile(Player player, String itemName, String encodedItem) {
    try {
      FileWriter fw = new FileWriter(AethelPlugin.getInstance().getResourceDirectory()
          + "/forge/" + itemName + ".txt");
      fw.write(encodedItem);
      fw.close();
      player.sendMessage(ChatColor.GREEN + "[Saved] " + ChatColor.WHITE + itemName + ".txt");
    } catch (IOException ex) {
      player.sendMessage(ChatColor.RED + "An error occurred while saving the recipe.");
    }
  }
}
