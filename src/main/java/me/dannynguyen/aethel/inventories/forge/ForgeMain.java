package me.dannynguyen.aethel.inventories.forge;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.creators.ItemCreator;
import me.dannynguyen.aethel.inventories.utility.Pagination;
import me.dannynguyen.aethel.objects.forge.ForgeRecipeCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * ForgeMain is a shared inventory under the Forge command that supports
 * categorical pagination for crafting, editing, and removing forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.6.1
 * @since 1.0.6
 */
public class ForgeMain {
  /**
   * Creates a ForgeMain page containing categories.
   *
   * @param player interacting player
   * @param action type of interaction
   * @return ForgeMain inventory with recipe categories
   */
  public static Inventory openForgeMainPage(Player player, String action) {
    Inventory inv = createInventory(player, action);
    addRecipeCategories(inv);

    String futureAction = player.getMetadata("future-action").get(0).asString();
    List<String> helpLore = List.of(ChatColor.WHITE + "Recipe Categories");

    if (futureAction.equals("craft")) {
      inv.setItem(4, ItemCreator.createPlayerHead("WHITE_QUESTION_MARK",
          ChatColor.GREEN + "Help", helpLore));
    } else {
      inv.setItem(2, ItemCreator.createPlayerHead("WHITE_QUESTION_MARK",
          ChatColor.GREEN + "Help", helpLore));
      addCreateButton(inv);
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
      case "craft" -> title += ChatColor.BLUE + " Craft " +
          ChatColor.WHITE + player.getMetadata("category").get(0).asString();
      case "edit" -> title += ChatColor.YELLOW + " Edit " +
          ChatColor.WHITE + player.getMetadata("category").get(0).asString();
      case "remove" -> title += ChatColor.RED + " Remove " +
          ChatColor.WHITE + player.getMetadata("category").get(0).asString();
    }
    return Bukkit.createInventory(player, 54, title);
  }

  /**
   * Adds recipe categories.
   *
   * @param inv interacting inventory
   */
  private static void addRecipeCategories(Inventory inv) {
    Set<String> categoryNames = AethelResources.forgeRecipeData.getRecipeCategoriesMap().keySet();
    if (!categoryNames.isEmpty()) {
      int i = 9;
      for (String categoryName : categoryNames) {
        inv.setItem(i, ItemCreator.createItem(Material.BOOK, ChatColor.WHITE + categoryName));
        i++;
      }
    }
  }

  /**
   * Loads a recipe category page from memory.
   *
   * @param player       interacting player
   * @param action       type of interaction
   * @param categoryName category to view
   * @param pageRequest  page to view
   * @return ForgeMain inventory with recipes
   */
  public static Inventory openForgeCategoryPage(Player player, String action,
                                                String categoryName, int pageRequest) {
    Inventory inv = createInventory(player, action);

    ForgeRecipeCategory recipeCategory = AethelResources.forgeRecipeData.
        getRecipeCategoriesMap().get(categoryName);
    int numberOfPages = recipeCategory.getNumberOfPages();
    int pageViewed = Pagination.calculatePageViewed(numberOfPages, pageRequest);
    player.setMetadata("page", new FixedMetadataValue(AethelPlugin.getInstance(), pageViewed));

    inv.setContents(recipeCategory.getPages().get(pageViewed).getContents());

    addForgeContext(action, inv);
    addActionButtons(action, inv);
    Pagination.addBackButton(inv, 6);
    Pagination.addPageButtons(inv, numberOfPages, pageViewed);
    return inv;
  }

  /**
   * Adds a help context to the ForgeMain inventory.
   *
   * @param action type of interaction
   * @param inv    interacting inventory
   */
  private static void addForgeContext(String action, Inventory inv) {
    List<String> helpLore;
    switch (action) {
      case "craft" -> {
        helpLore = Arrays.asList(
            ChatColor.WHITE + "Expand a recipe to see its",
            ChatColor.WHITE + "results and components.",
            "",
            ChatColor.WHITE + "Components are matched",
            ChatColor.WHITE + "by material unless",
            ChatColor.WHITE + "they're unique items!");
        inv.setItem(4, ItemCreator.createPlayerHead("WHITE_QUESTION_MARK",
            ChatColor.GREEN + "Help", helpLore));
      }
      case "edit", "remove" -> {
        helpLore = Arrays.asList(
            ChatColor.WHITE + "To undo a removal,",
            ChatColor.WHITE + "edit the item and",
            ChatColor.WHITE + "save it before reloading.");
        inv.setItem(2, ItemCreator.createPlayerHead("WHITE_QUESTION_MARK",
            ChatColor.GREEN + "Help", helpLore));
      }
    }
  }

  /**
   * Adds the create button.
   *
   * @param inv interacting inventory
   */
  private static void addCreateButton(Inventory inv) {
    inv.setItem(3, ItemCreator.
        createPlayerHead("CRAFTING_TABLE", ChatColor.AQUA + "Create"));
  }

  /**
   * Adds create, edit, and remove buttons.
   *
   * @param action type of interaction
   * @param inv    interacting inventory
   */
  private static void addActionButtons(String action, Inventory inv) {
    switch (action) {
      case "edit" -> {
        inv.setItem(3, ItemCreator.
            createPlayerHead("CRAFTING_TABLE", ChatColor.AQUA + "Create"));
        inv.setItem(5, ItemCreator.
            createPlayerHead("TRASH_CAN", ChatColor.AQUA + "Remove"));
      }
      case "remove" -> {
        inv.setItem(3, ItemCreator.
            createPlayerHead("CRAFTING_TABLE", ChatColor.AQUA + "Create"));
        inv.setItem(4, ItemCreator.
            createPlayerHead("FILE_EXPLORER", ChatColor.AQUA + "Edit"));
      }
    }
  }
}
