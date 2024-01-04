package me.dannynguyen.aethel.inventories.forge;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.objects.ForgeRecipe;
import me.dannynguyen.aethel.readers.ItemReader;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;

/**
 * ForgeModify is an inventory under the Forge command that modifies forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.2.1
 * @since 1.0.9
 */
public class ForgeModify {
  /**
   * Modifies an existing recipe.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public void modifyRecipe(InventoryClickEvent e, Player player) {
    AethelResources resources = AethelPlugin.getInstance().getResources();
    ForgeRecipe recipe = resources.getForgeRecipeData().getRecipesMap().
        get(new ItemReader().readItemName(e.getCurrentItem()));

    Inventory inv = new ForgeCreate().createInventory(player);
    addExistingRecipeContents(recipe, inv);

    player.openInventory(inv);
    player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-create"));
  }

  /**
   * Adds the recipe's existing results and components to the ForgeCreate inventory.
   *
   * @param recipe forge recipe
   * @param inv    interacting inventory
   */
  private void addExistingRecipeContents(ForgeRecipe recipe, Inventory inv) {
    ArrayList<ItemStack> results = recipe.getResults();
    ArrayList<ItemStack> components = recipe.getComponents();

    for (int i = 0; i < results.size(); i++) {
      inv.setItem(i, results.get(i));
    }
    for (int i = 0; i < components.size(); i++) {
      inv.setItem(i + 9, components.get(i));
    }
  }
}
