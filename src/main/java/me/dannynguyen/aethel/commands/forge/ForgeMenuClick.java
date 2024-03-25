package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Directory;
import me.dannynguyen.aethel.interfaces.MenuClick;
import me.dannynguyen.aethel.listeners.MenuEvent;
import me.dannynguyen.aethel.plugin.PluginPlayer;
import me.dannynguyen.aethel.utils.TextFormatter;
import me.dannynguyen.aethel.utils.item.ItemCreator;
import me.dannynguyen.aethel.utils.item.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

/**
 * Inventory click event listener for {@link ForgeCommand} menus.
 * <p>
 * Called through {@link MenuEvent}.
 *
 * @author Danny Nguyen
 * @version 1.17.19
 * @since 1.0.9
 */
public class ForgeMenuClick implements MenuClick {
  /**
   * Inventory click event.
   */
  private final InventoryClickEvent e;

  /**
   * Player who clicked.
   */
  private final Player user;

  /**
   * User's UUID.
   */
  private final UUID uuid;

  /**
   * Slot clicked.
   */
  private final int slot;

  /**
   * Associates an inventory click event with its user in the context of an open Forge menu.
   *
   * @param e inventory click event
   */
  public ForgeMenuClick(@NotNull InventoryClickEvent e) {
    this.e = Objects.requireNonNull(e, "Null inventory click event");
    this.user = (Player) e.getWhoClicked();
    this.uuid = user.getUniqueId();
    this.slot = e.getSlot();
  }

  /**
   * Either saves a {@link RecipeRegistry.Recipe recipe} or opens a {@link RecipeRegistry.Recipe recipe} category page.
   */
  public void interpretMenuClick() {
    switch (slot) {
      case 2, 4 -> { // Context
      }
      case 3 -> new RecipeDetailsMenu(user, RecipeDetailsMenu.Type.SAVE).saveRecipeDetails();
      default -> viewRecipeCategory();
    }
  }

  /**
   * Either:
   * <ul>
   *  <li>increments or decrements a {@link RecipeRegistry.Recipe recipe} category page
   *  <li>saves a {@link RecipeRegistry.Recipe recipe}
   *  <li>changes the {@link RecipeMenu.Action interaction}
   *  <li>contextualizes the click to expand, edit, or remove {@link RecipeRegistry.Recipe recipe}
   * </ul>
   *
   * @param action type of interaction
   */
  public void interpretCategoryClick(@NotNull RecipeMenu.Action action) {
    Objects.requireNonNull(action, "Null action");
    switch (slot) {
      case 0 -> previousRecipePage(action);
      case 2 -> { // Context
      }
      case 3 -> new RecipeDetailsMenu(user, RecipeDetailsMenu.Type.SAVE).saveRecipeDetails();
      case 4 -> {
        if (Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMode() == MenuEvent.Mode.RECIPE_MENU_EDIT) {
          openForgeEdit();
        }
      }
      case 5 -> openForgeRemove();
      case 6 -> returnToMainMenu();
      case 8 -> nextRecipePage(action);
      default -> {
        if (e.getSlot() > 8) {
          interpretContextualClick(action);
        }
      }
    }
  }

  /**
   * Either crafts a {@link RecipeRegistry.Recipe recipe} or returns to a category
   * page with the intent to craft {@link RecipeRegistry.Recipe recipes}.
   */
  public void interpretCraftDetailsClick() {
    switch (e.getSlot()) {
      case 25 -> new RecipeCraft(user, e.getClickedInventory().getItem(0)).readRecipeMaterials();
      case 26 -> openForgeCraft();
    }
  }

  /**
   * Either saves a {@link RecipeRegistry.Recipe recipe} or returns to a category
   * page with the intent to edit {@link RecipeRegistry.Recipe recipes}.
   */
  public void interpretSaveClick() {
    switch (slot) {
      case 8 -> { // Context
      }
      case 25 -> readSaveClick();
      case 26 -> openForgeEdit();
      default -> e.setCancelled(false);
    }
  }

  /**
   * Views a {@link RecipeRegistry.Recipe recipe} category.
   */
  private void viewRecipeCategory() {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    RecipeMenu.Action action = RecipeMenu.Action.valueOf(TextFormatter.formatEnum(pluginPlayer.getMode().getId()));
    String category = ChatColor.stripColor(ItemReader.readName(e.getCurrentItem()));
    int requestedPage = pluginPlayer.getPage();

    pluginPlayer.setCategory(category);
    user.openInventory(new RecipeMenu(user, action).getCategoryPage(category, requestedPage));
    pluginPlayer.setMenu(MenuEvent.Menu.valueOf("FORGE_" + action.name()));
  }

  /**
   * Opens the previous {@link RecipeRegistry.Recipe recipe} category page.
   *
   * @param action type of interaction
   */
  private void previousRecipePage(RecipeMenu.Action action) {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    String category = pluginPlayer.getCategory();
    int requestedPage = pluginPlayer.getPage();

    user.openInventory(new RecipeMenu(user, action).getCategoryPage(category, requestedPage - 1));
    pluginPlayer.setMenu(MenuEvent.Menu.valueOf("FORGE_" + action.name()));
  }

  /**
   * Opens the {@link RecipeMenu} with the intent to edit {@link RecipeRegistry.Recipe recipe}.
   * <p>
   * The player can return to either the {@link RecipeMenu} or a {@link RecipeRegistry.Recipe recipe} category.
   */
  private void openForgeEdit() {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    String category = pluginPlayer.getCategory();
    if (category.equals("")) {
      user.openInventory(new RecipeMenu(user, RecipeMenu.Action.EDIT).getMainMenu());
      pluginPlayer.setMenu(MenuEvent.Menu.FORGE_CATEGORY);
    } else {
      int requestedPage = pluginPlayer.getPage();
      user.openInventory(new RecipeMenu(user, RecipeMenu.Action.EDIT).getCategoryPage(category, requestedPage));
      pluginPlayer.setMenu(MenuEvent.Menu.FORGE_EDIT);
    }
  }

  /**
   * Opens the {@link RecipeMenu} with the intent to remove {@link RecipeRegistry.Recipe recipe}.
   */
  private void openForgeRemove() {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    String category = pluginPlayer.getCategory();
    int requestedPage = pluginPlayer.getPage();

    user.openInventory(new RecipeMenu(user, RecipeMenu.Action.REMOVE).getCategoryPage(category, requestedPage));
    pluginPlayer.setMenu(MenuEvent.Menu.FORGE_REMOVE);
  }

  /**
   * Opens the {@link RecipeMenu} with the {@link MenuEvent.Mode} in mind.
   */
  private void returnToMainMenu() {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    RecipeMenu.Action action = RecipeMenu.Action.valueOf(TextFormatter.formatEnum(pluginPlayer.getMode().getId()));

    pluginPlayer.setCategory("");
    user.openInventory(new RecipeMenu(user, action).getMainMenu());
    pluginPlayer.setMenu(MenuEvent.Menu.FORGE_CATEGORY);
    pluginPlayer.setPage(0);
  }

  /**
   * Opens the {@link RecipeMenu} with the intent to craft {@link RecipeRegistry.Recipe recipes}.
   */
  private void openForgeCraft() {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    String category = pluginPlayer.getCategory();
    int requestedPage = pluginPlayer.getPage();

    user.openInventory(new RecipeMenu(user, RecipeMenu.Action.CRAFT).getCategoryPage(category, requestedPage));
    pluginPlayer.setMenu(MenuEvent.Menu.FORGE_CRAFT);
  }

  /**
   * Opens the next {@link RecipeRegistry.Recipe recipe} category page.
   *
   * @param action type of interaction
   */
  private void nextRecipePage(RecipeMenu.Action action) {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    String category = pluginPlayer.getCategory();
    int requestedPage = pluginPlayer.getPage();

    user.openInventory(new RecipeMenu(user, action).getCategoryPage(category, requestedPage + 1));
    pluginPlayer.setMenu(MenuEvent.Menu.valueOf("FORGE_" + action.name()));
  }

  /**
   * Either crafts, edits, or removes a {@link RecipeRegistry.Recipe recipe}.
   *
   * @param action type of interaction
   */
  private void interpretContextualClick(RecipeMenu.Action action) {
    switch (action) {
      case CRAFT -> new RecipeDetailsMenu(user, RecipeDetailsMenu.Type.CRAFT, e.getCurrentItem()).craftRecipeDetails();
      case EDIT -> new RecipeDetailsMenu(user, RecipeDetailsMenu.Type.SAVE, e.getCurrentItem()).editRecipeDetails();
      case REMOVE -> removeRecipe();
    }
  }

  /**
   * Checks if the {@link RecipeRegistry.Recipe recipe's} details were
   * formatted correctly before saving the {@link RecipeRegistry.Recipe recipe}.
   */
  private void readSaveClick() {
    ItemStack[] contents = e.getInventory().getContents();
    String file = nameFile(contents);
    if (file != null) {
      String encodedRecipe = encodeRecipe(contents);
      if (encodedRecipe != null) {
        try {
          FileWriter fw = new FileWriter(Directory.FORGE.getFile().getPath() + "/" + file + "_rcp.txt");
          fw.write(encodedRecipe);
          fw.close();
          user.sendMessage(ChatColor.GREEN + "[Saved Recipe] " + ChatColor.WHITE + TextFormatter.capitalizePhrase(file));
        } catch (IOException ex) {
          user.sendMessage(ChatColor.RED + "Failed to write recipe to file.");
        }
      } else {
        user.sendMessage(ChatColor.RED + "No recipe materials.");
      }
    } else {
      user.sendMessage(ChatColor.RED + "No recipe results.");
    }
  }

  /**
   * Removes an existing {@link RecipeRegistry.Recipe recipe}.
   */
  private void removeRecipe() {
    RecipeRegistry.Recipe recipe = Plugin.getData().getRecipeRegistry().getRecipes().get(ItemReader.readName(e.getCurrentItem()));
    recipe.delete();
    user.sendMessage(ChatColor.RED + "[Removed Recipe] " + ChatColor.WHITE + recipe.getName());
  }

  /**
   * Names a {@link RecipeRegistry.Recipe recipe} by the first item in the results row.
   *
   * @param menuContents items in menu
   * @return file name
   */
  private String nameFile(ItemStack[] menuContents) {
    for (int i = 0; i < 8; i++) {
      ItemStack item = menuContents[i];
      if (ItemReader.isNotNullOrAir(item)) {
        ItemMeta meta = item.getItemMeta();
        if (meta.hasDisplayName()) {
          return TextFormatter.formatId(meta.getDisplayName());
        } else {
          return TextFormatter.formatId(item.getType().name());
        }
      }
    }
    return null;
  }

  /**
   * Encodes the {@link RecipeRegistry.Recipe recipe} by its results and materials.
   * <p>
   * At this stage, the results are non-null, so the
   * method checks if the materials are non-null first.
   *
   * @param menuContents items in the menu
   * @return encoded recipe string
   */
  private String encodeRecipe(ItemStack[] menuContents) {
    StringBuilder materials = new StringBuilder();
    for (int i = 9; i < 24; i++) {
      ItemStack item = menuContents[i];
      if (ItemReader.isNotNullOrAir(item)) {
        materials.append(ItemCreator.encodeItem(item)).append(" ");
      }
    }

    if (materials.isEmpty()) {
      return null;
    }

    StringBuilder results = new StringBuilder();
    for (int i = 0; i < 8; i++) {
      ItemStack item = menuContents[i];
      if (ItemReader.isNotNullOrAir(item)) {
        results.append(ItemCreator.encodeItem(item)).append(" ");
      }
    }
    return results.append("\n").append(materials).toString();
  }
}
