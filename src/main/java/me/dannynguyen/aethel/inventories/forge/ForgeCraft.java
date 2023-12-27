package me.dannynguyen.aethel.inventories.forge;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.objects.ForgeRecipe;
import me.dannynguyen.aethel.objects.ItemMetaReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * ForgeCraft is an inventory under the Forge command that crafts forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.1.4
 * @since 1.1.0
 */
public class ForgeCraft {
  /**
   * Crafts a recipe.
   *
   * @param e inventory click event
   */
  public void craftRecipe(InventoryClickEvent e, Player player) {
    boolean validItem = e.getCurrentItem() != null;
    if (validItem) {
      ArrayList<ForgeRecipe> forgeRecipes = new ArrayList<>(AethelPlugin.getInstance().getForgeRecipes());
      ForgeRecipe forgeRecipe = forgeRecipes.get(getRecipeIndex(e, forgeRecipes));

      ArrayList<ItemStack> results = forgeRecipe.getResults();
      ArrayList<ItemStack> components = forgeRecipe.getComponents();

      if (checkSufficientComponents(player, components)) {
        for (ItemStack item : components) {
          player.getInventory().removeItem(item);
        }
        for (ItemStack item : results) {
          player.getInventory().addItem(item);
        }
      } else {
        player.sendMessage(ChatColor.RED + "Insufficient components.");
      }
    }
  }

  /**
   * Matches the clicked item to its recipe.
   *
   * @param e inventory click event
   * @return index of the matching item
   */
  private int getRecipeIndex(InventoryClickEvent e, ArrayList<ForgeRecipe> forgeRecipes) {
    String itemName = new ItemMetaReader().getItemName(e.getCurrentItem());
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
   * Checks if the player has sufficient components to craft the recipe.
   *
   * @param player     interacting player
   * @param components components in recipe
   * @return sufficient resources
   */
  private boolean checkSufficientComponents(Player player, ArrayList<ItemStack> components) {
    for (ItemStack item : components) {
      if (!player.getInventory().containsAtLeast(item, item.getAmount())) {
        return false;
      }
    }
    return true;
  }
}
