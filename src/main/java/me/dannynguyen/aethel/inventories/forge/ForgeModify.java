package me.dannynguyen.aethel.inventories.forge;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.objects.ForgeRecipe;
import me.dannynguyen.aethel.readers.ItemMetaReader;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;

/**
 * ForgeModify is a menu option under the Forge command that modifies forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.1.10
 * @since 1.0.9
 */
public class ForgeModify {
  /**
   * Modifies an existing recipe.
   *
   * @param e      inventory click event
   * @param player interacting player
   * @throws NullPointerException recipe not found
   */
  public void modifyRecipe(InventoryClickEvent e, Player player) {
    try {
      // Match item to recipe
      AethelResources resources = AethelPlugin.getInstance().getResources();
      ForgeRecipe forgeRecipe = resources.getForgeRecipesMap().
          get(new ItemMetaReader().getItemName(e.getCurrentItem()));

      // Pre-fill a ForgeCreate inventory with the recipe contents
      Inventory inv = new ForgeCreate().createInventory(player);
      ArrayList<ItemStack> results = forgeRecipe.getResults();
      ArrayList<ItemStack> components = forgeRecipe.getComponents();

      for (int i = 0; i < results.size(); i++) {
        inv.setItem(i, results.get(i));
      }
      for (int i = 0; i < components.size(); i++) {
        inv.setItem(i + 9, components.get(i));
      }

      player.openInventory(inv);
      player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-create"));
    } catch (NullPointerException ex) {
      player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-modify"));
    }
  }
}
