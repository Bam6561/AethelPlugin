package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.plugin.enums.Directory;
import me.dannynguyen.aethel.plugin.enums.MenuMeta;
import me.dannynguyen.aethel.plugin.enums.PlayerMeta;
import me.dannynguyen.aethel.plugin.interfaces.MenuClickEvent;
import me.dannynguyen.aethel.util.TextFormatter;
import me.dannynguyen.aethel.util.item.ItemCreator;
import me.dannynguyen.aethel.util.item.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Inventory click event listener for {@link ForgeCommand} menus.
 * <p>
 * Called through {@link me.dannynguyen.aethel.plugin.listeners.MenuClick}.
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.17.6
 * @since 1.0.9
 */
public class ForgeMenuClick implements MenuClickEvent {
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
   * Either saves a {@link PersistentRecipe recipe} or opens a {@link PersistentRecipe recipe} category page.
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
   * <p>
   * - increments or decrements a {@link PersistentRecipe recipe} category page
   * </p>
   * <p>
   * - saves a {@link PersistentRecipe recipe}
   * </p>
   * <p>
   * - changes the interaction type
   * </p>
   * <p>
   * - contextualizes the click to expand, edit, or remove {@link PersistentRecipe recipe}
   * </p>
   *
   * @param action type of {@link RecipeMenu.Action} interaction
   */
  public void interpretCategoryClick(@NotNull RecipeMenu.Action action) {
    Objects.requireNonNull(action, "Null action");
    switch (slot) {
      case 0 -> previousRecipePage(action);
      case 2 -> { // Context
      }
      case 3 -> new RecipeDetailsMenu(user, RecipeDetailsMenu.Type.SAVE).saveRecipeDetails();
      case 4 -> {
        if (Plugin.getData().getPluginSystem().getPlayerMetadata().get(uuid).get(PlayerMeta.FUTURE).equals("edit")) {
          openForgeEditMenu();
        }
      }
      case 5 -> openForgeRemoveMenu();
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
   * Either crafts a {@link PersistentRecipe recipe} or returns to a category
   * page with the intent to craft {@link PersistentRecipe recipes}.
   */
  public void interpretCraftDetailsClick() {
    switch (e.getSlot()) {
      case 25 -> new RecipeCraft(user, e.getClickedInventory().getItem(0)).readRecipeMaterials();
      case 26 -> openForgeCraftMenu();
    }
  }

  /**
   * Either saves a {@link PersistentRecipe recipe} or returns to a category
   * page with the intent to edit {@link PersistentRecipe recipes}.
   */
  public void interpretSaveClick() {
    switch (slot) {
      case 8 -> { // Context
      }
      case 25 -> readSaveClick();
      case 26 -> openForgeEditMenu();
      default -> e.setCancelled(false);
    }
  }

  /**
   * Views a {@link PersistentRecipe recipe} category.
   */
  private void viewRecipeCategory() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(uuid);
    RecipeMenu.Action action = RecipeMenu.Action.valueOf(playerMeta.get(PlayerMeta.FUTURE).toUpperCase());
    String item = ChatColor.stripColor(ItemReader.readName(e.getCurrentItem()));
    int requestedPage = Integer.parseInt(playerMeta.get(PlayerMeta.PAGE));

    playerMeta.put(PlayerMeta.CATEGORY, item);
    user.openInventory(new RecipeMenu(user, action).getCategoryPage(item, requestedPage));
    playerMeta.put(PlayerMeta.INVENTORY, "forge." + action.name().toLowerCase());
  }

  /**
   * Opens the previous {@link PersistentRecipe recipe} category page.
   *
   * @param action type of {@link RecipeMenu.Action} interaction
   */
  private void previousRecipePage(RecipeMenu.Action action) {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(uuid);
    String category = playerMeta.get(PlayerMeta.CATEGORY);
    int requestedPage = Integer.parseInt(playerMeta.get(PlayerMeta.PAGE));
    user.openInventory(new RecipeMenu(user, action).getCategoryPage(category, requestedPage - 1));
    playerMeta.put(PlayerMeta.INVENTORY, "forge." + action.name().toLowerCase());
  }

  /**
   * Opens the {@link RecipeMenu} menu with the intent to edit {@link PersistentRecipe recipe}.
   * <p>
   * The player can return to either the {@link RecipeMenu} menu or a {@link PersistentRecipe recipe} category.
   * </p>
   */
  private void openForgeEditMenu() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(uuid);
    String category = playerMeta.get(PlayerMeta.CATEGORY);
    if (category.equals("")) {
      user.openInventory(new RecipeMenu(user, RecipeMenu.Action.EDIT).getMainMenu());
      playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.FORGE_CATEGORY.getMeta());
    } else {
      int requestedPage = Integer.parseInt(playerMeta.get(PlayerMeta.PAGE));
      user.openInventory(new RecipeMenu(user, RecipeMenu.Action.EDIT).getCategoryPage(category, requestedPage));
      playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.FORGE_EDIT.getMeta());
    }
  }

  /**
   * Opens the {@link RecipeMenu} menu with the intent to remove {@link PersistentRecipe recipe}.
   */
  private void openForgeRemoveMenu() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(uuid);
    String category = playerMeta.get(PlayerMeta.CATEGORY);
    int requestedPage = Integer.parseInt(playerMeta.get(PlayerMeta.PAGE));
    user.openInventory(new RecipeMenu(user, RecipeMenu.Action.REMOVE).getCategoryPage(category, requestedPage));
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.FORGE_REMOVE.getMeta());
  }

  /**
   * Opens the {@link RecipeMenu} menu with the future action in mind.
   */
  private void returnToMainMenu() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(uuid);
    RecipeMenu.Action action = RecipeMenu.Action.valueOf(playerMeta.get(PlayerMeta.FUTURE).toUpperCase());
    playerMeta.put(PlayerMeta.CATEGORY, "");
    user.openInventory(new RecipeMenu(user, action).getMainMenu());
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.FORGE_CATEGORY.getMeta());
    playerMeta.put(PlayerMeta.PAGE, "0");
  }

  /**
   * Opens the {@link RecipeMenu} menu with the intent to craft {@link PersistentRecipe recipes}.
   */
  private void openForgeCraftMenu() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(uuid);
    String category = playerMeta.get(PlayerMeta.CATEGORY);
    int requestedPage = Integer.parseInt(playerMeta.get(PlayerMeta.PAGE));
    user.openInventory(new RecipeMenu(user, RecipeMenu.Action.CRAFT).getCategoryPage(category, requestedPage));
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.FORGE_CRAFT.getMeta());
  }

  /**
   * Opens the next {@link PersistentRecipe recipe} category page.
   *
   * @param action type of {@link RecipeMenu.Action} interaction
   */
  private void nextRecipePage(RecipeMenu.Action action) {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(uuid);
    String category = playerMeta.get(PlayerMeta.CATEGORY);
    int requestedPage = Integer.parseInt(playerMeta.get(PlayerMeta.PAGE));
    user.openInventory(new RecipeMenu(user, action).getCategoryPage(category, requestedPage + 1));
    playerMeta.put(PlayerMeta.INVENTORY, "forge." + action.name().toLowerCase());
  }

  /**
   * Either crafts, edits, or removes a {@link PersistentRecipe recipe}.
   *
   * @param action type of {@link RecipeMenu.Action} interaction
   */
  private void interpretContextualClick(RecipeMenu.Action action) {
    switch (action) {
      case CRAFT -> new RecipeDetailsMenu(user, RecipeDetailsMenu.Type.CRAFT, e.getCurrentItem()).craftRecipeDetails();
      case EDIT -> new RecipeDetailsMenu(user, RecipeDetailsMenu.Type.SAVE, e.getCurrentItem()).editRecipeDetails();
      case REMOVE -> removeRecipe();
    }
  }

  /**
   * Checks if the recipe's details were formatted correctly before saving the {@link PersistentRecipe recipe}.
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
   * Removes an existing {@link PersistentRecipe recipe}.
   */
  private void removeRecipe() {
    PersistentRecipe recipe = Plugin.getData().getRecipeRegistry().getRecipes().get(ItemReader.readName(e.getCurrentItem()));
    recipe.delete();
    user.sendMessage(ChatColor.RED + "[Removed Recipe] " + ChatColor.WHITE + recipe.getName());
  }

  /**
   * Names a recipe by the first item in the results row.
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
   * Encodes the recipe by its results and materials.
   * <p>
   * At this stage, the results are non-null, so the
   * method checks if the materials are non-null first.
   * </p>
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
