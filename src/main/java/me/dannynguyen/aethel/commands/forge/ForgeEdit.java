package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.commands.forge.objects.ForgeRecipe;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;

/**
 * ForgeEdit is a utility class that edits forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.6.1
 * @since 1.0.9
 */
public class ForgeEdit {
  /**
   * Edits an existing recipe.
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void editRecipe(InventoryClickEvent e, Player user) {
    ForgeRecipe recipe = PluginData.forgeData.getRecipesMap().
        get(ItemReader.readName(e.getCurrentItem()));

    org.bukkit.inventory.Inventory inv = ForgeSave.createInventory(user);
    addExistingRecipeContents(recipe, inv);

    user.openInventory(inv);
    user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(), PluginPlayerMeta.Inventory.FORGE_SAVE.inventory));
  }

  /**
   * Adds the recipe's existing results and components to the ForgeSave inventory.
   *
   * @param recipe forge recipe
   * @param inv    interacting inventory
   */
  private static void addExistingRecipeContents(ForgeRecipe recipe, org.bukkit.inventory.Inventory inv) {
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
