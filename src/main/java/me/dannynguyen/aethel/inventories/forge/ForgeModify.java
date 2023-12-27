package me.dannynguyen.aethel.inventories.forge;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.objects.ForgeRecipe;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * ForgeModify is a menu option under the Forge command that modifies forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.1.3
 * @since 1.0.9
 */
public class ForgeModify {
  /**
   * Modifies an existing recipe.
   *
   * @param e inventory click event
   */
  public void modifyRecipeFile(InventoryClickEvent e) {
    Player player = (Player) e.getWhoClicked();
    Inventory inv = new ForgeCreate().createInventory(player);

    ArrayList<ForgeRecipe> forgeRecipes = new ArrayList<>(AethelPlugin.getInstance().getForgeRecipes());
    ForgeRecipe forgeRecipe = forgeRecipes.get(getRecipeFileIndex(e, forgeRecipes));

    ArrayList<ItemStack> results = forgeRecipe.getResults();
    ArrayList<ItemStack> components = forgeRecipe.getComponents();

    for (int i = 0; i < results.size(); i++) {
      inv.setItem(i, results.get(i));
    }
    for (int i = 0; i < components.size(); i++) {
      inv.setItem(i + 9, components.get(i));
    }

    player.openInventory(inv);
  }

  /**
   * Matches the clicked item to its recipe.
   *
   * @param e            inventory click event
   * @param forgeRecipes forge recipes
   * @return index of the matching item
   */
  private int getRecipeFileIndex(InventoryClickEvent e, ArrayList<ForgeRecipe> forgeRecipes) {
    String itemName = getItemName(e.getCurrentItem());
    int matchingIndex = -1;
    for (int i = 0; i < forgeRecipes.size(); i++) {
      String recipeName = forgeRecipes.get(i).getRecipeName();
      if (itemName.equals(recipeName)) {
        matchingIndex = i;
        break;
      }
    }
    return matchingIndex;
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
