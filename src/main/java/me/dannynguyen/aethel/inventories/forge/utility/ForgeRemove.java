package me.dannynguyen.aethel.inventories.forge.utility;


import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.objects.forge.ForgeRecipe;
import me.dannynguyen.aethel.readers.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * ForgeRemove is a utility class that removes forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.4.13
 * @since 1.0.9
 */
public class ForgeRemove {
  /**
   * Removes an existing recipe.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public static void removeRecipe(InventoryClickEvent e, Player player) {
    ForgeRecipe recipe = PluginData.forgeRecipeData.
        getRecipesMap().get(ItemReader.readName(e.getCurrentItem()));

    recipe.getFile().delete();
    player.sendMessage(ChatColor.RED + "[Removed Recipe] " + ChatColor.WHITE + recipe.getName());
  }
}
