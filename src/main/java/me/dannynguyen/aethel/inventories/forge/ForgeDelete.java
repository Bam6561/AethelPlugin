package me.dannynguyen.aethel.inventories.forge;


import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.objects.forge.ForgeRecipe;
import me.dannynguyen.aethel.readers.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * ForgeDelete is a utility class that deletes forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.4.13
 * @since 1.0.9
 */
public class ForgeDelete {
  /**
   * Deletes an existing recipe.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public static void deleteRecipe(InventoryClickEvent e, Player player) {
    ForgeRecipe recipe = AethelResources.forgeRecipeData.
        getRecipesMap().get(ItemReader.readItemName(e.getCurrentItem()));

    recipe.getFile().delete();
    player.sendMessage(ChatColor.RED + "[Deleted] " + ChatColor.WHITE +
        recipe.getName().toLowerCase().replace(" ", "_") + "_rcp.txt");
  }
}
