package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.PlayerHead;
import me.dannynguyen.aethel.listeners.MenuEvent;
import me.dannynguyen.aethel.utils.item.ItemCreator;
import me.dannynguyen.aethel.utils.item.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a menu containing a {@link RecipeRegistry.Recipe recipe's} details.
 *
 * @author Danny Nguyen
 * @version 1.19.9
 * @since 1.9.15
 */
class RecipeDetailsMenu {
  /**
   * GUI.
   */
  private final Inventory menu;

  /**
   * Recipe details menu {@link Mode}.
   */
  private final Mode mode;

  /**
   * GUI user.
   */
  private final Player user;

  /**
   * User's UUID.
   */
  private final UUID uuid;

  /**
   * {@link RecipeRegistry.Recipe Recipe}
   */
  private RecipeRegistry.Recipe recipe;

  /**
   * Associates a new RecipeDetails menu with its user.
   *
   * @param user user
   * @param mode recipe details mode
   */
  RecipeDetailsMenu(@NotNull Player user, @NotNull Mode mode) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.mode = Objects.requireNonNull(mode, "Null mode");
    this.uuid = user.getUniqueId();
    this.menu = createMenu(mode);
  }

  /**
   * Associates a new RecipeDetails menu with its user and {@link RecipeRegistry.Recipe recipe}.
   *
   * @param user user
   * @param mode {@link RecipeDetailsMenu.Mode}
   * @param item requested item
   */
  RecipeDetailsMenu(@NotNull Player user, @NotNull Mode mode, @NotNull ItemStack item) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.mode = Objects.requireNonNull(mode, "Null mode");
    this.recipe = Plugin.getData().getRecipeRegistry().getRecipes().get(ItemReader.readName(Objects.requireNonNull(item, "Null item")));
    this.uuid = user.getUniqueId();
    this.menu = createMenu(mode);
  }

  /**
   * Creates and names a RecipeDetails menu with the intent to craft or save a {@link RecipeRegistry.Recipe recipe}.
   *
   * @param mode {@link RecipeDetailsMenu.Mode}
   * @return RecipeDetails menu
   */
  private Inventory createMenu(Mode mode) {
    switch (mode) {
      case CRAFT -> {
        return Bukkit.createInventory(user, 27, ChatColor.DARK_GRAY + "Forge" + ChatColor.BLUE + " Craft");
      }
      case EDIT, SAVE -> {
        return Bukkit.createInventory(user, 27, ChatColor.DARK_GRAY + "Forge" + ChatColor.DARK_GREEN + " Save");
      }
      default -> {
        return null;
      }
    }
  }

  /**
   * Sets the menu to display interactions with the {@link RecipeRegistry.Recipe recipe's} details.
   */
  protected void getRecipeDetails() {
    addContext();
    addActions();
    switch (mode) {
      case CRAFT -> {
        addRecipeContents();
        user.openInventory(menu);
        Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).setMenu(MenuEvent.Menu.FORGE_CRAFT_RECIPE);
      }
      case EDIT -> {
        addRecipeContents();
        user.openInventory(menu);
        Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).setMenu(MenuEvent.Menu.FORGE_SAVE);
      }
      case SAVE -> {
        user.openInventory(menu);
        Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).setMenu(MenuEvent.Menu.FORGE_SAVE);
      }
    }
  }

  /**
   * Adds contextual help.
   */
  private void addContext() {
    menu.setItem(8, ItemCreator.createPluginPlayerHead(PlayerHead.QUESTION_MARK_WHITE.getHead(),
        ChatColor.GREEN + "Help", List.of(
            ChatColor.AQUA + "Rows",
            ChatColor.AQUA + "1 " + ChatColor.WHITE + "Results",
            ChatColor.AQUA + "2 " + ChatColor.WHITE + "Materials",
            ChatColor.AQUA + "3 " + ChatColor.WHITE + "Materials")));
  }

  /**
   * Adds craft or save and back buttons.
   */
  private void addActions() {
    switch (mode) {
      case CRAFT -> menu.setItem(25, ItemCreator.createPluginPlayerHead(PlayerHead.CRAFTING_TABLE.getHead(), ChatColor.AQUA + "Craft"));
      case EDIT, SAVE -> menu.setItem(25, ItemCreator.createPluginPlayerHead(PlayerHead.STACK_OF_PAPER.getHead(), ChatColor.AQUA + "Save"));
    }
    menu.setItem(26, ItemCreator.createPluginPlayerHead(PlayerHead.BACKWARD_GRAY.getHead(), ChatColor.AQUA + "Back"));
  }

  /**
   * Adds the {@link RecipeRegistry.Recipe recipe's} results and materials to the menu.
   */
  private void addRecipeContents() {
    List<ItemStack> results = recipe.getResults();
    for (int i = 0; i < results.size(); i++) {
      menu.setItem(i, results.get(i));
    }
    List<ItemStack> materials = recipe.getMaterials();
    for (int i = 0; i < materials.size(); i++) {
      menu.setItem(i + 9, materials.get(i));
    }
  }

  /**
   * Recipe details menu modes.
   */
  protected enum Mode {
    /**
     * Craft a {@link RecipeRegistry.Recipe recipe}.
     */
    CRAFT,

    /**
     * Edit a {@link RecipeRegistry.Recipe recipe}.
     */
    EDIT,

    /**
     * Save a {@link RecipeRegistry.Recipe recipe}.
     */
    SAVE
  }
}
