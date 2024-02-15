package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginPlayerHead;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.listeners.InventoryMenuListener;
import me.dannynguyen.aethel.utility.ItemCreator;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Represents a menu containing a recipe's details.
 *
 * @author Danny Nguyen
 * @version 1.9.19
 * @since 1.9.15
 */
class RecipeDetailsMenu {
  /**
   * RecipeDetails GUI.
   */
  private final Inventory menu;

  /**
   * GUI user.
   */
  private final Player user;

  /**
   * Recipe details menu type.
   */
  private final RecipeDetailsType type;

  /**
   * Recipe.
   */
  private PersistentRecipe recipe;

  /**
   * Type of recipe details menu.
   */
  protected enum RecipeDetailsType {
    /**
     * Craft a recipe.
     */
    CRAFT,

    /**
     * Edit or save a recipe.
     */
    SAVE
  }

  /**
   * Associates a new RecipeDetails menu with its user.
   *
   * @param user user
   * @param type recipe details type
   */
  protected RecipeDetailsMenu(@NotNull Player user, @NotNull RecipeDetailsType type) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.type = Objects.requireNonNull(type, "Null type");
    this.menu = createMenu(this.type);
  }

  /**
   * Associates a new RecipeDetails menu with its user and recipe.
   *
   * @param user user
   * @param type recipe details type
   * @param item requested item
   */
  protected RecipeDetailsMenu(@NotNull Player user, @NotNull RecipeDetailsType type, @NotNull ItemStack item) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.type = Objects.requireNonNull(type, "Null type");
    this.recipe = Objects.requireNonNull(PluginData.recipeRegistry.getRecipeMap().get(ItemReader.readName(item)), "Null recipe");
    this.menu = createMenu(this.type);
  }

  /**
   * Creates and names a RecipeDetails menu with the intent to craft or save a recipe.
   *
   * @param type details menu type
   * @return RecipeDetails menu
   */
  private Inventory createMenu(RecipeDetailsType type) {
    switch (type) {
      case CRAFT -> {
        return Bukkit.createInventory(user, 27, ChatColor.DARK_GRAY + "Forge" + ChatColor.BLUE + " Craft");
      }
      case SAVE -> {
        return Bukkit.createInventory(user, 27, ChatColor.DARK_GRAY + "Forge" + ChatColor.DARK_GREEN + " Save");
      }
      default -> {
        return null;
      }
    }
  }

  /**
   * Expands the recipe's details to the user before crafting.
   */
  protected void craftRecipeDetails() {
    addRecipeContents();
    addContext();
    addActions();
    user.openInventory(menu);
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.FORGE_CRAFT_CONFIRM.menu));
  }

  /**
   * Expands the recipe's details to the user before crafting.
   */
  protected void editRecipeDetails() {
    addRecipeContents();
    addContext();
    addActions();
    user.openInventory(menu);
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.FORGE_SAVE.menu));
  }

  /**
   * Opens the RecipeDetails menu with the intent to save a recipe.
   */
  protected void saveRecipeDetails() {
    addContext();
    addActions();
    user.openInventory(menu);
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.FORGE_SAVE.menu));
  }

  /**
   * Adds contextual help.
   */
  private void addContext() {
    menu.setItem(8, ItemCreator.createPluginPlayerHead(PluginPlayerHead.QUESTION_MARK_WHITE.head,
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
    switch (type) {
      case CRAFT -> menu.setItem(25, ItemCreator.createPluginPlayerHead(PluginPlayerHead.CRAFTING_TABLE.head, ChatColor.AQUA + "Craft"));
      case SAVE -> menu.setItem(25, ItemCreator.createPluginPlayerHead(PluginPlayerHead.STACK_OF_PAPER.head, ChatColor.AQUA + "Save"));
    }
    menu.setItem(26, ItemCreator.createPluginPlayerHead(PluginPlayerHead.BACKWARD_GRAY.head, ChatColor.AQUA + "Back"));
  }

  /**
   * Adds the recipe's results and materials to the menu.
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
}
