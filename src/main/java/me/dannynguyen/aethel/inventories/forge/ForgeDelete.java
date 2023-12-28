package me.dannynguyen.aethel.inventories.forge;


import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.objects.ForgeRecipe;
import me.dannynguyen.aethel.readers.ForgeRecipeReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;

/**
 * ForgeDelete is an inventory under the Forge command that deletes forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.1.5
 * @since 1.0.9
 */
public class ForgeDelete {
  /**
   * Deletes an existing recipe.
   *
   * @param e      inventory click event
   * @param player interacting player
   * @throws NullPointerException recipe not found
   */
  public void deleteRecipe(InventoryClickEvent e, Player player) {
    try {
      int recipeFileIndex = new ForgeRecipeReader().getRecipeIndex(e.getCurrentItem());

      ArrayList<ForgeRecipe> forgeRecipes = new ArrayList<>(AethelPlugin.getInstance().getForgeRecipes());
      ForgeRecipe forgeRecipe = forgeRecipes.get(recipeFileIndex);

      forgeRecipe.getRecipeFile().delete();
      player.sendMessage(ChatColor.RED + "[Deleted] " + ChatColor.WHITE +
          forgeRecipe.getRecipeName().toLowerCase().replace(" ", "_") + ".txt");
    } catch (NullPointerException ex) {
    }
  }
}
