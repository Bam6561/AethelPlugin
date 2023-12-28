package me.dannynguyen.aethel.inventories.forge;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.objects.ForgeRecipe;
import me.dannynguyen.aethel.readers.ItemMetaReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * ForgeCraft is an inventory under the Forge command that crafts forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.1.10
 * @since 1.1.0
 */
public class ForgeCraft {
  /**
   * Crafts a recipe.
   *
   * @param e      inventory click event
   * @param player interacting player
   * @throws NullPointerException recipe not found
   */
  public void craftRecipe(InventoryClickEvent e, Player player) {
    try {
      AethelResources resources = AethelPlugin.getInstance().getResources();
      ForgeRecipe forgeRecipe = resources.getForgeRecipesMap().
          get(new ItemMetaReader().getItemName(e.getCurrentItem()));

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
    } catch (NullPointerException ex) {
    }
  }

  /**
   * Checks if the player has sufficient components to craft the recipe.
   *
   * @param player     interacting player
   * @param components components in recipe
   * @return sufficient components
   */
  private boolean checkSufficientComponents(Player player, ArrayList<ItemStack> components) {
    for (ItemStack item : components) {
      if (!player.getInventory().containsAtLeast(item, item.getAmount())) return false;
    }
    return true;
  }
}
