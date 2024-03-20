package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.plugin.enums.PlayerHead;
import me.dannynguyen.aethel.plugin.enums.PlayerMeta;
import me.dannynguyen.aethel.plugin.interfaces.CategoryMenu;
import me.dannynguyen.aethel.util.InventoryPages;
import me.dannynguyen.aethel.util.item.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a menu that supports categorical pagination for
 * crafting, editing, and removing {@link PersistentRecipe recipes}.
 *
 * @author Danny Nguyen
 * @version 1.17.6
 * @since 1.0.6
 */
public class RecipeMenu implements CategoryMenu {
  /**
   * GUI.
   */
  private final Inventory menu;

  /**
   * GUI user.
   */
  private final Player user;

  /**
   * User's UUID.
   */
  private final UUID uuid;

  /**
   * {@link Action action}
   */
  private final Action action;

  /**
   * Associates a new Recipe menu with its user and action.
   *
   * @param user   user
   * @param action type of interaction
   */
  public RecipeMenu(@NotNull Player user, @NotNull RecipeMenu.Action action) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.action = Objects.requireNonNull(action, "Null action");
    this.uuid = user.getUniqueId();
    this.menu = createMenu();
  }

  /**
   * Creates and names a Recipe menu with its action and category.
   *
   * @return Recipe menu
   */
  private Inventory createMenu() {
    String title = ChatColor.DARK_GRAY + "Forge";
    String category = ChatColor.WHITE + Plugin.getData().getPluginSystem().getPlayerMetadata().get(uuid).get(PlayerMeta.CATEGORY);
    switch (action) {
      case CRAFT -> title += ChatColor.BLUE + " Craft ";
      case EDIT -> title += ChatColor.YELLOW + " Edit ";
      case REMOVE -> title += ChatColor.RED + " Remove ";
    }
    return Bukkit.createInventory(user, 54, title + ChatColor.WHITE + category);
  }

  /**
   * Sets the menu to view {@link PersistentRecipe recipe} categories.
   *
   * @return Recipe menu
   */
  @NotNull
  public Inventory getMainMenu() {
    addCategories();
    if (action != Action.CRAFT) {
      addCreateButton();
    }
    return menu;
  }

  /**
   * Sets the menu to load a {@link PersistentRecipe recipe} category page.
   *
   * @param requestedCategory requested category
   * @param requestedPage     requested page
   * @return {@link PersistentRecipe recipe} category page
   */
  @NotNull
  public Inventory getCategoryPage(String requestedCategory, int requestedPage) {
    List<Inventory> category = Plugin.getData().getRecipeRegistry().getRecipeCategories().get(requestedCategory);
    int numberOfPages = category.size();
    int pageViewed = InventoryPages.calculatePageViewed(numberOfPages, requestedPage);
    Plugin.getData().getPluginSystem().getPlayerMetadata().get(uuid).put(PlayerMeta.PAGE, String.valueOf(pageViewed));

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
      case CRAFT -> menu.setItem(4, ItemCreator.createPluginPlayerHead(PlayerHead.QUESTION_MARK_WHITE.getHead(), ChatColor.GREEN + "Help", List.of(
          ChatColor.WHITE + "Expand a recipe to see its",
          ChatColor.WHITE + "results and materials.",
          "",
          ChatColor.WHITE + "Materials are matched",
          ChatColor.WHITE + "by material unless",
          ChatColor.WHITE + "they're unique items!")));
      case EDIT, REMOVE -> menu.setItem(2, ItemCreator.createPluginPlayerHead(PlayerHead.QUESTION_MARK_WHITE.getHead(), ChatColor.GREEN + "Help", List.of(
          ChatColor.WHITE + "To undo a removal,",
          ChatColor.WHITE + "edit the item and",
          ChatColor.WHITE + "save it before reloading.")));
    }
  }

  /**
   * Adds the create button.
   */
  private void addCreateButton() {
    menu.setItem(3, ItemCreator.createPluginPlayerHead(PlayerHead.CRAFTING_TABLE.getHead(), ChatColor.AQUA + "Create"));
  }

  /**
   * Adds create, edit, and remove buttons.
   */
  private void addActions() {
    switch (action) {
      case EDIT -> {
        menu.setItem(3, ItemCreator.createPluginPlayerHead(PlayerHead.CRAFTING_TABLE.getHead(), ChatColor.AQUA + "Create"));
        menu.setItem(5, ItemCreator.createPluginPlayerHead(PlayerHead.TRASH_CAN.getHead(), ChatColor.AQUA + "Remove"));
      }
      case REMOVE -> {
        menu.setItem(3, ItemCreator.createPluginPlayerHead(PlayerHead.CRAFTING_TABLE.getHead(), ChatColor.AQUA + "Create"));
        menu.setItem(4, ItemCreator.createPluginPlayerHead(PlayerHead.FILE_EXPLORER.getHead(), ChatColor.AQUA + "Edit"));
      }
    }
  }

  /**
   * Adds {@link PersistentRecipe recipe} categories.
   */
  private void addCategories() {
    Set<String> categories = Plugin.getData().getRecipeRegistry().getRecipeCategories().keySet();
    if (!categories.isEmpty()) {
      int i = 9;
      for (String category : categories) {
        menu.setItem(i, ItemCreator.createItem(Material.BOOK, ChatColor.WHITE + category));
        i++;
      }
    }
  }

  /**
   * Types of interactions.
   */
  public enum Action {
    /**
     * Craft {@link PersistentRecipe recipes}.
     */
    CRAFT,

    /**
     * Edit {@link PersistentRecipe recipes}.
     */
    EDIT,

    /**
     * Remove {@link PersistentRecipe recipes}.
     */
    REMOVE
  }
}
