package me.dannynguyen.aethel.gui;

import me.dannynguyen.aethel.AethelPlugin;
import org.bukkit.Bukkit;
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
 * ForgeCreate is a menu option under the Forge command that handles the creation of new Forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.0.5
 * @since 1.0.5
 */
public class ForgeCreate {
  private Inventory defaultView;

  public ForgeCreate(Player player) {
    this.defaultView = createDefaultView(player);
  }

  /**
   * Creates the default view for the Forge-Create menu.
   *
   * @param player interacting player
   * @return Forge-Create default view
   */
  private Inventory createDefaultView(Player player) {
    Inventory defaultView = Bukkit.createInventory(player, 27, "Forge-Create");
    defaultView.setItem(26, createMenuItem(Material.GREEN_CONCRETE, "Save Recipe"));
    return defaultView;
  }

  /**
   * Creates a named item.
   *
   * @param material    item material
   * @param displayName item name
   * @return named item
   */
  private ItemStack createMenuItem(Material material, String displayName) {
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
    ArrayList<ItemStack> view = new ArrayList<ItemStack>();
    Collections.addAll(view, e.getInventory().getContents());

    writeRecipeToFile(e, nameRecipe(view), createRecipe(view));
  }


  /**
   * Names a recipe by the first item in the results row.
   *
   * @param view items in the inventory
   * @return name of the recipe
   */
  private String nameRecipe(ArrayList<ItemStack> view) {
    String recipeName;
    for (int i = 0; i < 9; i++) {
      ItemStack item = view.get(i);
      if (item != null) {
        if (item.getItemMeta().hasDisplayName()) {
          recipeName = item.getItemMeta().getDisplayName().toLowerCase().replace(" ", "_");
        } else {
          recipeName = item.getType().name().toLowerCase();
        }
        return recipeName;
      }
    }
    return null;
  }

  /**
   * Serializes the inventory by its results and components.
   *
   * @param view items in the inventory
   * @return serialized recipe string
   */
  private String createRecipe(ArrayList<ItemStack> view) {
    StringBuilder recipe = new StringBuilder("Results\n");
    for (int i = 0; i < 9; i++) {
      ItemStack item = view.get(i);
      if (item != null) recipe.append(encodeItem(view.get(i)) + "\n");
    }
    recipe.append("Components\n");
    for (int i = 9; i < 25; i++) {
      ItemStack item = view.get(i);
      if (item != null) recipe.append(encodeItem(view.get(i)) + "\n");
    }
    return recipe.toString();
  }

  /**
   * Serializes an item into bytes.
   *
   * @param item item to serialize
   * @return serialized item string
   */
  private String encodeItem(ItemStack item) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      BukkitObjectOutputStream boos = new BukkitObjectOutputStream(baos);
      boos.writeObject(item);
      boos.flush();

      byte[] serializedItem = baos.toByteArray();

      return Base64.getEncoder().encodeToString(serializedItem);
    } catch (IOException e) {
      return null;
    }
  }

  /**
   * Writes a Forge recipe to storage.
   *
   * @param e           inventory click event
   * @param itemName    item name
   * @param encodedItem serialized item string
   */
  private void writeRecipeToFile(InventoryClickEvent e, String itemName, String encodedItem) {
    Player player = (Player) e.getWhoClicked();
    try {
      FileWriter fw = new FileWriter(AethelPlugin.getInstance().getResourceDirectory() + "/forge/" + itemName + ".txt");
      fw.write(encodedItem);
      fw.close();
      player.sendMessage("Forge Recipe saved as: " + itemName);
      e.setCancelled(true);
      player.closeInventory();
    } catch (IOException error) {
      error.printStackTrace();
    }
  }

  /**
   * Returns a generic Forge-Create inventory.
   *
   * @return blank Forge-Create view
   */
  public Inventory getDefaultView() {
    return this.defaultView;
  }
}
