package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.commands.forge.objects.ForgeRecipe;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.utility.ItemCreator;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ForgeCraft is an inventory for crafting forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.6.1
 * @since 1.1.0
 */
public class ForgeCraft {
  /**
   * Creates and names a ForgeCraft inventory.
   *
   * @param user user
   * @return ForgeCraft inventory
   */
  public static org.bukkit.inventory.Inventory createInventory(Player user) {
    org.bukkit.inventory.Inventory inv = Bukkit.createInventory(user, 27,
        ChatColor.DARK_GRAY + "Forge" + ChatColor.BLUE + " Craft");

    addCraftContext(inv);
    inv.setItem(25, ItemCreator.
        createPluginPlayerHead("CRAFTING_TABLE", ChatColor.AQUA + "Craft"));
    inv.setItem(26, ItemCreator.
        createPluginPlayerHead("GRAY_BACKWARD", ChatColor.AQUA + "Back"));
    return inv;
  }

  /**
   * Adds a help context to the expanded craft action.
   *
   * @param inv interacting inventory
   */
  private static void addCraftContext(org.bukkit.inventory.Inventory inv) {
    List<String> helpLore = Arrays.asList(
        ChatColor.AQUA + "Rows",
        ChatColor.AQUA + "1 " + ChatColor.WHITE + "Results",
        ChatColor.AQUA + "2 " + ChatColor.WHITE + "Components",
        ChatColor.AQUA + "3 " + ChatColor.WHITE + "Components");
    inv.setItem(8, ItemCreator.createPluginPlayerHead("WHITE_QUESTION_MARK",
        ChatColor.GREEN + "Help", helpLore));
  }

  /**
   * Expands the recipe's details to the user before crafting.
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void expandRecipeDetails(InventoryClickEvent e, Player user) {
    ForgeRecipe recipe = PluginData.forgeData.
        getRecipesMap().get(ItemReader.readName(e.getCurrentItem()));

    org.bukkit.inventory.Inventory inv = createInventory(user);
    addExistingRecipeContents(recipe, inv);

    user.openInventory(inv);
    user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(), PluginPlayerMeta.Inventory.FORGE_CRAFT_CONFIRM.inventory));
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
