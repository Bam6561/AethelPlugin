package me.dannynguyen.aethel.inventories.forge;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.creators.ItemCreator;
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
 * @version 1.4.2
 * @since 1.0.6
 */
public class ForgeMain {
  /**
   * Creates and names a ForgeMain inventory.
   *
   * @param player interacting player
   * @param action type of interaction
   * @return ForgeMain inventory
   */
  private Inventory createInventory(Player player, String action) {
    String title = ChatColor.DARK_GRAY + "Forge";
    switch (action) {
      case "craft" -> title += ChatColor.BLUE + " Craft";
      case "modify" -> title += ChatColor.YELLOW + " Modify";
      case "delete" -> title += ChatColor.RED + " Delete";
    }
    Inventory inv = Bukkit.createInventory(player, 54, title);
    return inv;
  }

  /**
   * Loads a recipe page from memory.
   *
   * @param player      interacting player
   * @param action      type of interaction
   * @param pageRequest page to view
   * @return ForgeMain inventory with recipes
   */
  public Inventory openRecipePage(Player player, String action, int pageRequest) {
    Inventory inv = new ForgeMain().createInventory(player, action);

    AethelResources resources = AethelPlugin.getInstance().getResources();

    int numberOfPages = resources.getForgeRecipeData().getNumberOfPages();
    int pageViewed;
    if (numberOfPages != 0) {
      pageViewed = new PageCalculator().calculatePageViewed(pageRequest, numberOfPages);
      inv.setContents(resources.getForgeRecipeData().getRecipePages().get(pageViewed).getContents());
      addPaginationButtons(inv, pageViewed, numberOfPages);
    } else {
      pageViewed = 0;
    }

    if (action.equals("craft")) {
      addCraftHelp(inv);
    } else {
      addEditorHelp(inv);
    }
    addActionButtons(inv, action);

    player.setMetadata("page", new FixedMetadataValue(AethelPlugin.getInstance(), pageViewed));
    return inv;
  }

  /**
   * Adds a help context to the craft action.
   *
   * @param inv interacting inventory
   */
  private void addCraftHelp(Inventory inv) {
    List<String> helpLore = Arrays.asList(
        ChatColor.WHITE + "Expand a recipe to see its",
        ChatColor.WHITE + "results and components.",
        "",
        ChatColor.WHITE + "Components are matched",
        ChatColor.WHITE + "by material unless",
        ChatColor.WHITE + "they're unique items!");

    inv.setItem(4, new ItemCreator().createPlayerHead("White Question Mark",
        ChatColor.GREEN + "Help", helpLore));
  }

  /**
   * Adds a help context to the editor actions.
   *
   * @param inv interacting inventory
   */
  private void addEditorHelp(Inventory inv) {
    List<String> helpLore = Arrays.asList(
        ChatColor.WHITE + "To undo a deletion,",
        ChatColor.WHITE + "modify the item and",
        ChatColor.WHITE + "save it before reloading.");

    inv.setItem(2, new ItemCreator().createPlayerHead("White Question Mark",
        ChatColor.GREEN + "Help", helpLore));
  }

  /**
   * Adds previous and next page buttons based on the page number.
   *
   * @param inv           interacting inventory
   * @param pageViewed    page viewed
   * @param numberOfPages number of recipe pages
   */
  private void addPaginationButtons(Inventory inv, int pageViewed, int numberOfPages) {
    if (pageViewed > 0) {
      inv.setItem(0, new ItemCreator().
          createPlayerHead("Red Backward", ChatColor.AQUA + "Previous Page"));
    }
    if (numberOfPages - 1 > pageViewed) {
      inv.setItem(8, new ItemCreator().
          createPlayerHead("Lime Forward", ChatColor.AQUA + "Next Page"));
    }
  }

  /**
   * Adds create, modify, and delete recipe buttons.
   *
   * @param inv    interacting inventory
   * @param action type of interaction
   */
  private void addActionButtons(Inventory inv, String action) {
    switch (action) {
      case "modify" -> {
        ItemCreator itemCreator = new ItemCreator();
        inv.setItem(3, itemCreator.
            createPlayerHead("Crafting Table", ChatColor.AQUA + "Create"));
        inv.setItem(5, itemCreator.
            createPlayerHead("Trash Can", ChatColor.AQUA + "Delete"));
      }
      case "delete" -> {
        ItemCreator itemCreator = new ItemCreator();
        inv.setItem(3, itemCreator.
            createPlayerHead("Crafting Table", ChatColor.AQUA + "Create"));
        inv.setItem(4, itemCreator.
            createPlayerHead("File Explorer", ChatColor.AQUA + "Modify"));
      }
    }
  }
}
