package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.systems.plugin.PluginData;
import me.dannynguyen.aethel.systems.plugin.enums.PlayerMeta;
import me.dannynguyen.aethel.systems.plugin.enums.PluginPlayerHead;
import me.dannynguyen.aethel.utility.InventoryPages;
import me.dannynguyen.aethel.utility.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a menu that supports categorical pagination for crafting, editing, and removing Forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.10.1
 * @since 1.0.6
 */
class RecipeMenu {
  /**
   * Recipe GUI.
   */
  private final Inventory menu;

  /**
   * GUI user.
   */
  private final Player user;

  /**
   * GUI action.
   */
  private final ForgeMenuAction action;

  /**
   * Associates a new Recipe menu with its user and action.
   *
   * @param user   user
   * @param action type of interaction
   */
  protected RecipeMenu(@NotNull Player user, @NotNull ForgeMenuAction action) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.action = Objects.requireNonNull(action, "Null action");
    this.menu = createMenu();
  }

  /**
   * Creates and names a Recipe menu with its action and category.
   *
   * @return Recipe menu
   */
  private Inventory createMenu() {
    String title = ChatColor.DARK_GRAY + "Forge";
    String category = ChatColor.WHITE + PluginData.pluginSystem.getPlayerMetadata().get(user).get(PlayerMeta.CATEGORY);
    switch (action) {
      case CRAFT -> title += ChatColor.BLUE + " Craft ";
      case EDIT -> title += ChatColor.YELLOW + " Edit ";
      case REMOVE -> title += ChatColor.RED + " Remove ";
    }
    return Bukkit.createInventory(user, 54, title + ChatColor.WHITE + category);
  }

  /**
   * Sets the menu to view recipe categories.
   *
   * @return Recipe menu
   */
  @NotNull
  protected Inventory openMainMenu() {
    addCategories();
    if (action != ForgeMenuAction.CRAFT) {
      addCreateButton();
    }
    return menu;
  }

  /**
   * Sets the menu to load a recipe category page.
   *
   * @param requestedCategory requested category
   * @param requestedPage     requested page
   * @return recipe category page
   */
  @NotNull
  protected Inventory openCategoryPage(String requestedCategory, int requestedPage) {
    List<Inventory> category = PluginData.recipeRegistry.getCategoryMap().get(requestedCategory);
    int numberOfPages = category.size();
    int pageViewed = InventoryPages.calculatePageViewed(numberOfPages, requestedPage);
    PluginData.pluginSystem.getPlayerMetadata().get(user).put(PlayerMeta.PAGE, String.valueOf(pageViewed));

    menu.setContents(category.get(pageViewed).getContents());
    addContext();
    addActions();
    InventoryPages.addBackButton(menu, 6);
    InventoryPages.addPageButtons(menu, numberOfPages, pageViewed);
    return menu;
  }

  /**
   * Adds contextual help.
   */
  private void addContext() {
    switch (action) {
      case CRAFT -> menu.setItem(4, ItemCreator.createPluginPlayerHead(PluginPlayerHead.QUESTION_MARK_WHITE.getHead(), ChatColor.GREEN + "Help", List.of(
          ChatColor.WHITE + "Expand a recipe to see its",
          ChatColor.WHITE + "results and materials.",
          "",
          ChatColor.WHITE + "Materials are matched",
          ChatColor.WHITE + "by material unless",
          ChatColor.WHITE + "they're unique items!")));
      case EDIT, REMOVE -> menu.setItem(2, ItemCreator.createPluginPlayerHead(PluginPlayerHead.QUESTION_MARK_WHITE.getHead(), ChatColor.GREEN + "Help", List.of(
          ChatColor.WHITE + "To undo a removal,",
          ChatColor.WHITE + "edit the item and",
          ChatColor.WHITE + "save it before reloading.")));
    }
  }

  /**
   * Adds the create button.
   */
  private void addCreateButton() {
    menu.setItem(3, ItemCreator.createPluginPlayerHead(PluginPlayerHead.CRAFTING_TABLE.getHead(), ChatColor.AQUA + "Create"));
  }

  /**
   * Adds create, edit, and remove buttons.
   */
  private void addActions() {
    switch (action) {
      case EDIT -> {
        menu.setItem(3, ItemCreator.createPluginPlayerHead(PluginPlayerHead.CRAFTING_TABLE.getHead(), ChatColor.AQUA + "Create"));
        menu.setItem(5, ItemCreator.createPluginPlayerHead(PluginPlayerHead.TRASH_CAN.getHead(), ChatColor.AQUA + "Remove"));
      }
      case REMOVE -> {
        menu.setItem(3, ItemCreator.createPluginPlayerHead(PluginPlayerHead.CRAFTING_TABLE.getHead(), ChatColor.AQUA + "Create"));
        menu.setItem(4, ItemCreator.createPluginPlayerHead(PluginPlayerHead.FILE_EXPLORER.getHead(), ChatColor.AQUA + "Edit"));
      }
    }
  }

  /**
   * Adds recipe categories.
   */
  private void addCategories() {
    Set<String> categories = PluginData.recipeRegistry.getCategoryMap().keySet();
    if (!categories.isEmpty()) {
      int i = 9;
      for (String category : categories) {
        menu.setItem(i, ItemCreator.createItem(Material.BOOK, ChatColor.WHITE + category));
        i++;
      }
    }
  }
}
