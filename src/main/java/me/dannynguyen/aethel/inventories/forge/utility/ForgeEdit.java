package me.dannynguyen.aethel.inventories.forge.utility;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.inventories.forge.ForgeSave;
import me.dannynguyen.aethel.objects.forge.ForgeRecipe;
import me.dannynguyen.aethel.readers.ItemReader;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;

/**
 * ForgeEdit is a utility class that edits forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.6.1
 * @since 1.0.9
 */
public class ForgeEdit {
  /**
   * Edits an existing recipe.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public static void editRecipe(InventoryClickEvent e, Player player) {
    ForgeRecipe recipe = AethelResources.forgeRecipeData.getRecipesMap().
        get(ItemReader.readItemName(e.getCurrentItem()));

    Inventory inv = ForgeSave.createInventory(player);
    addExistingRecipeContents(recipe, inv);

    player.openInventory(inv);
    player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "forge.save"));
  }

  /**
   * Adds the recipe's existing results and components to the ForgeSave inventory.
   *
   * @param recipe forge recipe
   * @param inv    interacting inventory
   */
  private static void addExistingRecipeContents(ForgeRecipe recipe, Inventory inv) {
    ArrayList<ItemStack> results = recipe.getResults();
    ArrayList<ItemStack> components = recipe.getComponents();

    for (int i = 0; i < results.size(); i++) {
      inv.setItem(i, results.get(i));
    }
    for (int i = 0; i < components.size(); i++) {
      inv.setItem(i + 9, components.get(i));
    }
  }
}
