package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.commands.forge.objects.ForgeRecipeCategory;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.utility.InventoryPages;
import me.dannynguyen.aethel.utility.ItemCreator;
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
 * @version 1.7.3
 * @since 1.0.6
 */
public class ForgeI {
  /**
   * Creates a ForgeMain page containing categories.
   *
   * @param user   user
   * @param action type of interaction
   * @return ForgeMain inventory with recipe categories
   */
  public static Inventory openMainMenu(Player user, String action) {
    Inventory inv = createInventory(user, action);
    addRecipeCategories(inv);

    String futureAction = user.getMetadata(PluginPlayerMeta.Namespace.FUTURE.namespace).get(0).asString();
    List<String> helpLore = List.of(ChatColor.WHITE + "Recipe Categories");

    if (futureAction.equals("craft")) {
      inv.setItem(4, ItemCreator.createPluginPlayerHead("WHITE_QUESTION_MARK",
          ChatColor.GREEN + "Help", helpLore));
    } else {
      inv.setItem(2, ItemCreator.createPluginPlayerHead("WHITE_QUESTION_MARK",
          ChatColor.GREEN + "Help", helpLore));
      addCreateButton(inv);
    }
    return inv;
  }

  /**
   * Creates and names a ForgeMain inventory with its action.
   *
   * @param user   user
   * @param action type of interaction
   * @return ForgeMain inventory
   */
  private static Inventory createInventory(Player user, String action) {
    String title = ChatColor.DARK_GRAY + "Forge";
    switch (action) {
      case "craft" -> title += ChatColor.BLUE + " Craft " +
          ChatColor.WHITE + user.getMetadata(PluginPlayerMeta.Namespace.CATEGORY.namespace).get(0).asString();
      case "edit" -> title += ChatColor.YELLOW + " Edit " +
          ChatColor.WHITE + user.getMetadata(PluginPlayerMeta.Namespace.CATEGORY.namespace).get(0).asString();
      case "remove" -> title += ChatColor.RED + " Remove " +
          ChatColor.WHITE + user.getMetadata(PluginPlayerMeta.Namespace.CATEGORY.namespace).get(0).asString();
    }
    return Bukkit.createInventory(user, 54, title);
  }

  /**
   * Adds recipe categories.
   *
   * @param inv interacting inventory
   */
  private static void addRecipeCategories(Inventory inv) {
    Set<String> categoryNames = PluginData.forgeData.getRecipeCategoriesMap().keySet();
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
   * @param user         user
   * @param action       type of interaction
   * @param categoryName category to view
   * @param pageRequest  page to view
   * @return ForgeMain inventory with recipes
   */
  public static Inventory openForgeCategoryPage(Player user, String action,
                                                String categoryName, int pageRequest) {
    Inventory inv = createInventory(user, action);

    ForgeRecipeCategory recipeCategory = PluginData.forgeData.
        getRecipeCategoriesMap().get(categoryName);
    int numberOfPages = recipeCategory.getNumberOfPages();
    int pageViewed = InventoryPages.calculatePageViewed(numberOfPages, pageRequest);
    user.setMetadata(PluginPlayerMeta.Namespace.PAGE.namespace, new FixedMetadataValue(Plugin.getInstance(), pageViewed));

    inv.setContents(recipeCategory.getPages().get(pageViewed).getContents());

    addForgeContext(action, inv);
    addActionButtons(action, inv);
    InventoryPages.addBackButton(inv, 6);
    InventoryPages.addPageButtons(inv, numberOfPages, pageViewed);
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
        inv.setItem(4, ItemCreator.createPluginPlayerHead("WHITE_QUESTION_MARK",
            ChatColor.GREEN + "Help", helpLore));
      }
      case "edit", "remove" -> {
        helpLore = Arrays.asList(
            ChatColor.WHITE + "To undo a removal,",
            ChatColor.WHITE + "edit the item and",
            ChatColor.WHITE + "save it before reloading.");
        inv.setItem(2, ItemCreator.createPluginPlayerHead("WHITE_QUESTION_MARK",
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
        createPluginPlayerHead("CRAFTING_TABLE", ChatColor.AQUA + "Create"));
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
            createPluginPlayerHead("CRAFTING_TABLE", ChatColor.AQUA + "Create"));
        inv.setItem(5, ItemCreator.
            createPluginPlayerHead("TRASH_CAN", ChatColor.AQUA + "Remove"));
      }
      case "remove" -> {
        inv.setItem(3, ItemCreator.
            createPluginPlayerHead("CRAFTING_TABLE", ChatColor.AQUA + "Create"));
        inv.setItem(4, ItemCreator.
            createPluginPlayerHead("FILE_EXPLORER", ChatColor.AQUA + "Edit"));
      }
    }
  }
}
