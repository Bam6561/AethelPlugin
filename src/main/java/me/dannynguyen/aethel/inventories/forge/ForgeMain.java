package me.dannynguyen.aethel.inventories.forge;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.creators.ItemCreator;
import me.dannynguyen.aethel.data.ForgeRecipeData;
import me.dannynguyen.aethel.inventories.PageCalculator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.List;

/**
 * ForgeMain is a shared inventory under the Forge command that supports
 * pagination for crafting, modifying, and deleting forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.4.12
 * @since 1.0.6
 */
public class ForgeMain {
  /**
   * Loads a recipe page from memory.
   *
   * @param player      interacting player
   * @param action      type of interaction
   * @param pageRequest page to view
   * @return ForgeMain inventory with recipes
   */
  public static Inventory openRecipePage(Player player, String action, int pageRequest) {
    ForgeRecipeData recipeData = AethelPlugin.getInstance().getResources().getForgeRecipeData();

    int numberOfPages = recipeData.getNumberOfPages();
    int pageViewed = PageCalculator.calculatePageViewed(pageRequest, numberOfPages);
    player.setMetadata("page", new FixedMetadataValue(AethelPlugin.getInstance(), pageViewed));

    Inventory inv = createInventory(player, action);
    if (numberOfPages > 0) {
      inv.setContents(recipeData.getRecipePages().get(pageViewed).getContents());
      addPageButtons(inv, pageViewed, numberOfPages);
    }

    switch (action) {
      case "craft" -> addCraftContext(inv);
      case "modify" -> {
        addEditorContext(inv);
        addModifyActionButtons(inv);
      }
      case "delete" -> {
        addEditorContext(inv);
        addDeleteActionButtons(inv);
      }
    }
    return inv;
  }

  /**
   * Creates and names a ForgeMain inventory with its action.
   *
   * @param player interacting player
   * @param action type of interaction
   * @return ForgeMain inventory
   */
  private static Inventory createInventory(Player player, String action) {
    String title = ChatColor.DARK_GRAY + "Forge";
    switch (action) {
      case "craft" -> title += ChatColor.BLUE + " Craft";
      case "modify" -> title += ChatColor.YELLOW + " Modify";
      case "delete" -> title += ChatColor.RED + " Delete";
    }
    return Bukkit.createInventory(player, 54, title);
  }

  /**
   * Adds a help context to the craft action.
   *
   * @param inv interacting inventory
   */
  private static void addCraftContext(Inventory inv) {
    List<String> helpLore = Arrays.asList(
        ChatColor.WHITE + "Expand a recipe to see its",
        ChatColor.WHITE + "results and components.",
        "",
        ChatColor.WHITE + "Components are matched",
        ChatColor.WHITE + "by material unless",
        ChatColor.WHITE + "they're unique items!");

    inv.setItem(4, ItemCreator.createPlayerHead("White Question Mark",
        ChatColor.GREEN + "Help", helpLore));
  }

  /**
   * Adds a help context to the editor actions.
   *
   * @param inv interacting inventory
   */
  private static void addEditorContext(Inventory inv) {
    List<String> helpLore = Arrays.asList(
        ChatColor.WHITE + "To undo a deletion,",
        ChatColor.WHITE + "modify the item and",
        ChatColor.WHITE + "save it before reloading.");

    inv.setItem(2, ItemCreator.createPlayerHead("White Question Mark",
        ChatColor.GREEN + "Help", helpLore));
  }

  /**
   * Adds previous and next page buttons based on the page number.
   *
   * @param inv           interacting inventory
   * @param pageViewed    page viewed
   * @param numberOfPages number of recipe pages
   */
  private static void addPageButtons(Inventory inv, int pageViewed, int numberOfPages) {
    if (pageViewed > 0) {
      inv.setItem(0, ItemCreator.
          createPlayerHead("Red Backward", ChatColor.AQUA + "Previous Page"));
    }
    if (numberOfPages - 1 > pageViewed) {
      inv.setItem(8, ItemCreator.
          createPlayerHead("Lime Forward", ChatColor.AQUA + "Next Page"));
    }
  }

  /**
   * Adds create and delete recipe buttons.
   *
   * @param inv interacting inventory
   */
  private static void addModifyActionButtons(Inventory inv) {
    inv.setItem(3, ItemCreator.
        createPlayerHead("Crafting Table", ChatColor.AQUA + "Create"));
    inv.setItem(5, ItemCreator.
        createPlayerHead("Trash Can", ChatColor.AQUA + "Delete"));
  }


  /**
   * Adds create and modify recipe buttons.
   *
   * @param inv interacting inventory
   */
  private static void addDeleteActionButtons(Inventory inv) {
    inv.setItem(3, ItemCreator.
        createPlayerHead("Crafting Table", ChatColor.AQUA + "Create"));
    inv.setItem(4, ItemCreator.
        createPlayerHead("File Explorer", ChatColor.AQUA + "Modify"));
  }
}
