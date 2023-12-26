package me.dannynguyen.aethel.inventories.forge;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.objects.ForgeRecipe;
import me.dannynguyen.aethel.objects.ForgeRecipeReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * ForgeCraft is a menu option under the Forge command that crafts forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.1.0
 * @since 1.1.0
 */
public class ForgeCraft {
  /**
   * Crafts a forge recipe.
   *
   * @param e inventory click event
   */
  public void craftRecipe(InventoryClickEvent e, Player player) {
    boolean validItem = e.getCurrentItem() != null;
    if (validItem) {
      boolean notComponentDetails = !e.getCurrentItem().getItemMeta().getDisplayName().equals("Components");
      if (notComponentDetails) {
        ArrayList<ForgeRecipe> forgeRecipes = new ArrayList<>(AethelPlugin.getInstance().getForgeRecipes());
        ForgeRecipe forgeRecipe = forgeRecipes.get(getRecipeFileIndex(e, forgeRecipes));

        ArrayList<ItemStack> results = forgeRecipe.getResults();
        ArrayList<ItemStack> components = forgeRecipe.getComponents();

        if (checkSufficientComponents(player, components)) {
          for (ItemStack item : components) {
            player.getInventory().removeItem(new ItemStack(item.getType(), item.getAmount()));
          }
          for (ItemStack item : results) {
            player.getInventory().addItem(item);
          }
        } else {
          player.sendMessage(ChatColor.RED + "Insufficient components.");
        }
      }
    }
  }

  /**
   * Matches the clicked item to its forge recipe.
   *
   * @param e inventory click evnet
   * @return index of the matching item
   */
  private int getRecipeFileIndex(InventoryClickEvent e, ArrayList<ForgeRecipe> forgeRecipes) {
    String itemName = new ForgeRecipeReader().getItemName(e.getCurrentItem());
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
   * Checks if the player has sufficient components to craft the forge recipe.
   *
   * @param player     interacting player
   * @param components components in forge recipe
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
