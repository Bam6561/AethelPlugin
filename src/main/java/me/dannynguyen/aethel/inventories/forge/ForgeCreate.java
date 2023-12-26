package me.dannynguyen.aethel.inventories.forge;

import me.dannynguyen.aethel.AethelPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;

/**
 * ForgeCreate is a menu option under the Forge command that processes the creation of new forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.0.9
 * @since 1.0.5
 */
public class ForgeCreate {
  /**
   * Creates the default view for the Forge-Create menu.
   *
   * @param player interacting player
   * @return Forge-Create default view
   */
  public Inventory createDefaultView(Player player) {
    Inventory defaultView = Bukkit.createInventory(player, 27, "Forge");
    defaultView.setItem(26, createItem(Material.GREEN_CONCRETE, "Save Recipe"));
    return defaultView;
  }

  /**
   * Creates a named item.
   *
   * @param material    item material
   * @param displayName item name
   * @return named item
   */
  private ItemStack createItem(Material material, String displayName) {
    ItemStack item = new ItemStack(material, 1);
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(displayName);
    item.setItemMeta(meta);
    return item;
  }

  /**
   * Converts the user's Forge-Create inventory into a readable view to name & save the recipe.
   *
   * @param e inventory click event
   */
  public void processSaveClick(InventoryClickEvent e) {
    ArrayList<ItemStack> view = new ArrayList<>();
    Collections.addAll(view, e.getInventory().getContents());

    String fileName = nameRecipeFile(view);
    String encodedRecipe = encodeRecipe(view);

    Player player = (Player) e.getWhoClicked();
    boolean invalidFileName = fileName == null;
    boolean invalidRecipe = encodedRecipe == null;

    if (invalidFileName) {
      player.sendMessage(ChatColor.RED + "Recipe results cannot be empty.");
      e.setCancelled(true);
    } else if (invalidRecipe) {
      player.sendMessage(ChatColor.RED + "Recipe components cannot be empty.");
      e.setCancelled(true);
    } else {
      saveRecipeToFile(e, fileName, encodedRecipe);
    }
  }

  /**
   * Names a recipe by the first item in the results row.
   *
   * @param view items in the inventory
   * @return name of the recipe
   */
  private String nameRecipeFile(ArrayList<ItemStack> view) {
    for (int i = 0; i < 9; i++) {
      ItemStack item = view.get(i);
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
   *
   * @param view items in the inventory
   * @return encoded recipe string
   */
  private String encodeRecipe(ArrayList<ItemStack> view) {
    StringBuilder results = new StringBuilder("Results\n");
    for (int i = 0; i < 9; i++) {
      ItemStack item = view.get(i);
      if (item != null) results.append(encodeItem(view.get(i)) + "\n");
    }

    StringBuilder components = new StringBuilder("Components\n");
    for (int i = 9; i < 25; i++) {
      ItemStack item = view.get(i);
      if (item != null) components.append(encodeItem(view.get(i)) + "\n");
    }

    if (components.toString().equals("Components\n")) {
      return null;
    } else {
      return results.append(components).toString();
    }
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

      byte[] encodedItem = baos.toByteArray();

      return Base64.getEncoder().encodeToString(encodedItem);
    } catch (IOException ex) {
      return null;
    }
  }

  /**
   * Saves a recipe file to storage.
   *
   * @param e           inventory click event
   * @param itemName    item name
   * @param encodedItem encoded item string
   * @throws IOException file could not be created
   */
  private void saveRecipeToFile(InventoryClickEvent e, String itemName, String encodedItem) {
    Player player = (Player) e.getWhoClicked();
    try {
      FileWriter fw = new FileWriter(AethelPlugin.getInstance().getResourceDirectory() + "/forge/" + itemName + ".txt");
      fw.write(encodedItem);
      fw.close();
      player.sendMessage(ChatColor.GREEN + "[Save] " + ChatColor.WHITE + itemName + ".txt");
      e.setCancelled(true);
      player.closeInventory();
    } catch (IOException ex) {
      player.sendMessage(ChatColor.RED + "An error occurred while saving the recipe.");
    }
  }
}
