package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.commands.forge.objects.ForgeRecipeCategory;
import me.dannynguyen.aethel.enums.PluginContext;
import me.dannynguyen.aethel.enums.PluginPlayerHead;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.utility.InventoryPages;
import me.dannynguyen.aethel.utility.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;
import java.util.Set;

/**
 * Forge is a shared inventory that supports categorical
 * pagination for crafting, editing, and removing forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.7.13
 * @since 1.0.6
 */
public class ForgeInventory {
  /**
   * Creates a Forge main menu containing categories.
   *
   * @param user   user
   * @param action type of interaction
   * @return Forge inventory with recipe categories
   */
  public static Inventory openMainMenu(Player user, String action) {
    Inventory inv = createInventory(user, action);
    addRecipeCategories(inv);

    String futureAction = user.getMetadata(PluginPlayerMeta.Namespace.FUTURE.namespace).get(0).asString();
    List<String> helpLore = List.of(ChatColor.WHITE + "Recipe Categories");

    if (futureAction.equals("craft")) {
      inv.setItem(4, ItemCreator.createPluginPlayerHead(PluginPlayerHead.QUESTION_MARK_WHITE.head,
          ChatColor.GREEN + "Help", helpLore));
    } else {
      inv.setItem(2, ItemCreator.createPluginPlayerHead(PluginPlayerHead.QUESTION_MARK_WHITE.head,
          ChatColor.GREEN + "Help", helpLore));
      addCreateButton(inv);
    }
    return inv;
  }

  /**
   * Creates and names a Forge inventory with its action.
   *
   * @param user   user
   * @param action type of interaction
   * @return Forge inventory
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
   * @param user              user
   * @param action            type of interaction
   * @param requestedCategory requested category
   * @param pageRequest       page to view
   * @return Forge inventory with recipes
   */
  public static Inventory openForgeCategoryPage(Player user, String action,
                                                String requestedCategory, int pageRequest) {
    Inventory inv = createInventory(user, action);

    ForgeRecipeCategory category = PluginData.forgeData.
        getRecipeCategoriesMap().get(requestedCategory);
    int numberOfPages = category.getNumberOfPages();
    int pageViewed = InventoryPages.calculatePageViewed(numberOfPages, pageRequest);
    user.setMetadata(PluginPlayerMeta.Namespace.PAGE.namespace,
        new FixedMetadataValue(Plugin.getInstance(), pageViewed));

    inv.setContents(category.getPages().get(pageViewed).getContents());

    addContext(action, inv);
    addActions(action, inv);
    InventoryPages.addBackButton(inv, 6);
    InventoryPages.addPageButtons(inv, numberOfPages, pageViewed);
    return inv;
  }

  /**
   * Adds a help context to the Forge inventory.
   *
   * @param action type of interaction
   * @param inv    interacting inventory
   */
  private static void addContext(String action, Inventory inv) {
    List<String> helpLore;
    switch (action) {
      case "craft" -> {
        helpLore = PluginContext.FORGE_CRAFT.context;
        inv.setItem(4, ItemCreator.createPluginPlayerHead(PluginPlayerHead.QUESTION_MARK_WHITE.head,
            ChatColor.GREEN + "Help", helpLore));
      }
      case "edit", "remove" -> {
        helpLore = PluginContext.FORGE_EDITOR.context;
        inv.setItem(2, ItemCreator.createPluginPlayerHead(PluginPlayerHead.QUESTION_MARK_WHITE.head,
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
        createPluginPlayerHead(PluginPlayerHead.CRAFTING_TABLE.head, ChatColor.AQUA + "Create"));
  }

  /**
   * Adds create, edit, and remove buttons.
   *
   * @param action type of interaction
   * @param inv    interacting inventory
   */
  private static void addActions(String action, Inventory inv) {
    switch (action) {
      case "edit" -> {
        inv.setItem(3, ItemCreator.
            createPluginPlayerHead(PluginPlayerHead.CRAFTING_TABLE.head, ChatColor.AQUA + "Create"));
        inv.setItem(5, ItemCreator.
            createPluginPlayerHead(PluginPlayerHead.TRASH_CAN.head, ChatColor.AQUA + "Remove"));
      }
      case "remove" -> {
        inv.setItem(3, ItemCreator.
            createPluginPlayerHead(PluginPlayerHead.CRAFTING_TABLE.head, ChatColor.AQUA + "Create"));
        inv.setItem(4, ItemCreator.
            createPluginPlayerHead(PluginPlayerHead.FILE_EXPLORER.head, ChatColor.AQUA + "Edit"));
      }
    }
  }
}
