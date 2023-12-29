package me.dannynguyen.aethel.inventories.forge;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.objects.ForgeRecipe;
import me.dannynguyen.aethel.readers.ItemMetaReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * ForgeCraft is an inventory under the Forge command that crafts forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.2.1
 * @since 1.1.0
 */
public class ForgeCraft {
  /**
   * Creates and names a ForgeCraft inventory.
   *
   * @param player interacting player
   * @return ForgeCraft inventory
   */
  public Inventory createInventory(Player player) {
    String title = ChatColor.DARK_GRAY + "Forge" + ChatColor.BLUE + " Craft";
    Inventory inv = Bukkit.createInventory(player, 27, title);
    HashMap<String, ItemStack> headsMap = AethelPlugin.getInstance().
        getResources().getPlayerHeadData().getHeadsMap();
    inv.setItem(25, headsMap.get("Create Recipe"));
    inv.setItem(26, headsMap.get("Back"));
    return inv;
  }

  /**
   * Expands the recipe's details to the player before crafting.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public void expandRecipeDetails(InventoryClickEvent e, Player player) {
    AethelResources resources = AethelPlugin.getInstance().getResources();
    ForgeRecipe recipe = resources.getForgeRecipeData().getRecipesMap().
        get(new ItemMetaReader().getItemName(e.getCurrentItem()));

    Inventory inv = createInventory(player);
    addExistingRecipeContents(recipe, inv);

    player.openInventory(inv);
    player.setMetadata("inventory",
        new FixedMetadataValue(AethelPlugin.getInstance(), "forge-craft-confirm"));
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

  /**
   * Crafts a recipe.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public void craftRecipe(InventoryClickEvent e, Player player) {
    AethelResources resources = AethelPlugin.getInstance().getResources();
    ForgeRecipe recipe = resources.getForgeRecipeData().getRecipesMap().
        get(new ItemMetaReader().getItemName(e.getClickedInventory().getItem(0)));

    ArrayList<ItemStack> results = recipe.getResults();
    ArrayList<ItemStack> components = recipe.getComponents();

    if (checkSufficientComponents(player, components)) {
      processCrafting(player, components, results);
    } else {
      player.sendMessage(ChatColor.RED + "Insufficient components.");
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

  /**
   * Removes the recipe's components and adds the results directly to the player's
   * inventory if there's space. Otherwise, the results are dropped at the player's feet.
   *
   * @param player     interacting player
   * @param components components in recipe
   * @param results    results in recipe
   */
  private void processCrafting(Player player, ArrayList<ItemStack> components, ArrayList<ItemStack> results) {
    for (ItemStack item : components) {
      player.getInventory().removeItem(item);
    }
    for (ItemStack item : results) {
      if (player.getInventory().firstEmpty() != -1) {
        player.getInventory().addItem(item);
      } else {
        player.getWorld().dropItem(player.getLocation(), item);
      }
    }
  }
}
