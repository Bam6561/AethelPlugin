package me.dannynguyen.aethel.inventories.forge;


import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.objects.ForgeRecipe;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * ForgeDelete is an inventory under the Forge command that deletes forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.1.9
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
      // Match item to recipe
      AethelResources resources = AethelPlugin.getInstance().getResources();
      ForgeRecipe forgeRecipe = resources.getForgeRecipes().get(resources.getRecipeIndex(e.getCurrentItem()));

      forgeRecipe.getRecipeFile().delete();
      player.sendMessage(ChatColor.RED + "[Deleted] " + ChatColor.WHITE +
          forgeRecipe.getRecipeName().toLowerCase().replace(" ", "_") + ".txt");
    } catch (NullPointerException ex) {
    }
  }
}
