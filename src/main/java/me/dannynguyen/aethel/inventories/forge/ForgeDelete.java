package me.dannynguyen.aethel.inventories.forge;


import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.objects.ForgeRecipe;
import me.dannynguyen.aethel.readers.ItemMetaReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * ForgeDelete is an inventory under the Forge command that deletes forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.2.1
 * @since 1.0.9
 */
public class ForgeDelete {
  /**
   * Deletes an existing recipe.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public void deleteRecipe(InventoryClickEvent e, Player player) {
    AethelResources resources = AethelPlugin.getInstance().getResources();
    ForgeRecipe recipe = resources.getForgeRecipeData().getRecipesMap().
        get(new ItemMetaReader().readItemName(e.getCurrentItem()));

    recipe.getFile().delete();
    player.sendMessage(ChatColor.RED + "[Deleted] " + ChatColor.WHITE +
        recipe.getName().toLowerCase().replace(" ", "_") + ".txt");
  }
}
