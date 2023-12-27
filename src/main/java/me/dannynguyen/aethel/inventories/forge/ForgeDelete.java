package me.dannynguyen.aethel.inventories.forge;


import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.objects.ForgeRecipe;
import me.dannynguyen.aethel.objects.ForgeRecipeReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

/**
 * ForgeModify is a menu option under the Forge command that deletes forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.1.2
 * @since 1.0.9
 */
public class ForgeDelete {
  /**
   * Deletes an existing recipe.
   */
  public void deleteRecipeFile(InventoryClickEvent e) {
    Player player = (Player) e.getWhoClicked();
    Inventory view = new ForgeCreate().createDefaultView(player);

    ArrayList<ForgeRecipe> forgeRecipes = new ArrayList<>(AethelPlugin.getInstance().getForgeRecipes());
    ForgeRecipe forgeRecipe = forgeRecipes.get(getRecipeFileIndex(e, forgeRecipes));

    forgeRecipe.getRecipeFile().delete();
    player.sendMessage(ChatColor.RED + "[Delete] " + ChatColor.WHITE +
        forgeRecipe.getRecipeName().toLowerCase().replace(" ", "_") + ".txt");
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
}
