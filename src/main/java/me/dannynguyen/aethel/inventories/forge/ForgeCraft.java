package me.dannynguyen.aethel.inventories.forge;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.creators.ItemCreator;
import me.dannynguyen.aethel.objects.forge.ForgeRecipe;
import me.dannynguyen.aethel.readers.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ForgeCraft is an inventory for crafting forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.4.13
 * @since 1.1.0
 */
public class ForgeCraft {
  /**
   * Creates and names a ForgeCraft inventory.
   *
   * @param player interacting player
   * @return ForgeCraft inventory
   */
  public static Inventory createInventory(Player player) {
    Inventory inv = Bukkit.createInventory(player, 27,
        ChatColor.DARK_GRAY + "Forge" + ChatColor.BLUE + " Craft");

    addCraftContext(inv);
    inv.setItem(25, ItemCreator.
        createPlayerHead("CRAFTING_TABLE", ChatColor.AQUA + "Craft"));
    inv.setItem(26, ItemCreator.
        createPlayerHead("GRAY_BACKWARD", ChatColor.AQUA + "Back"));
    return inv;
  }

  /**
   * Adds a help context to the expanded craft action.
   *
   * @param inv interacting inventory
   */
  private static void addCraftContext(Inventory inv) {
    List<String> helpLore = Arrays.asList(
        ChatColor.AQUA + "Rows",
        ChatColor.AQUA + "1 " + ChatColor.WHITE + "Results",
        ChatColor.AQUA + "2 " + ChatColor.WHITE + "Components",
        ChatColor.AQUA + "3 " + ChatColor.WHITE + "Components");
    inv.setItem(8, ItemCreator.createPlayerHead("WHITE_QUESTION_MARK",
        ChatColor.GREEN + "Help", helpLore));
  }

  /**
   * Expands the recipe's details to the player before crafting.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public static void expandRecipeDetails(InventoryClickEvent e, Player player) {
    ForgeRecipe recipe = AethelResources.forgeRecipeData.
        getRecipesMap().get(ItemReader.readItemName(e.getCurrentItem()));

    Inventory inv = createInventory(player);
    addExistingRecipeContents(recipe, inv);

    player.openInventory(inv);
    player.setMetadata("inventory",
        new FixedMetadataValue(AethelPlugin.getInstance(), "forge-craft-confirm"));
  }

  /**
   * Adds the recipe's existing results and components to the ForgeSave inventory.
   *
   * @param recipe forge recipe
   * @param inv    interacting inventory
   */
  private static void addExistingRecipeContents(ForgeRecipe recipe, Inventory inv) {
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
