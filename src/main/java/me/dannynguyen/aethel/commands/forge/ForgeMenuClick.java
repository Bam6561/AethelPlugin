package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.systems.plugin.PluginData;
import me.dannynguyen.aethel.systems.plugin.enums.MenuMeta;
import me.dannynguyen.aethel.systems.plugin.enums.PlayerMeta;
import me.dannynguyen.aethel.systems.plugin.enums.PluginDirectory;
import me.dannynguyen.aethel.utility.ItemCreator;
import me.dannynguyen.aethel.utility.ItemReader;
import me.dannynguyen.aethel.utility.TextFormatter;
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
 * Inventory click event listener for Forge menus.
 *
 * @author Danny Nguyen
 * @version 1.12.1
 * @since 1.0.9
 */
public class ForgeMenuClick {
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
  private final UUID userUUID;

  /**
   * Slot clicked.
   */
  private final int slotClicked;

  /**
   * Associates an inventory click event with its user in the context of an open Forge menu.
   *
   * @param e inventory click event
   */
  public ForgeMenuClick(@NotNull InventoryClickEvent e) {
    this.e = Objects.requireNonNull(e, "Null inventory click event");
    this.user = (Player) e.getWhoClicked();
    this.userUUID = user.getUniqueId();
    this.slotClicked = e.getSlot();
  }

  /**
   * Either saves a recipe or opens a recipe category page.
   */
  public void interpretMainMenuClick() {
    switch (slotClicked) {
      case 2, 4 -> { // Context
      }
      case 3 -> new RecipeDetailsMenu(user, RecipeDetailsMenu.RecipeDetailsType.SAVE).saveRecipeDetails();
      default -> viewRecipeCategory();
    }
  }

  /**
   * Either:
   * - increments or decrements a recipe category page
   * - saves a recipe
   * - changes the interaction type
   * - contextualizes the click to expand, edit, or remove recipes
   *
   * @param action type of interaction
   */
  public void interpretCategoryClick(ForgeMenuAction action) {
    switch (slotClicked) {
      case 0 -> previousRecipePage(action);
      case 2 -> { // Context
      }
      case 3 -> new RecipeDetailsMenu(user, RecipeDetailsMenu.RecipeDetailsType.SAVE).saveRecipeDetails();
      case 4 -> {
        if (PluginData.pluginSystem.getPlayerMetadata().get(userUUID).get(PlayerMeta.FUTURE).equals("edit")) {
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
   * Either crafts a recipe or returns to a category page with the intent to craft recipes.
   */
  public void interpretCraftDetailsClick() {
    switch (e.getSlot()) {
      case 25 -> new RecipeCraft(user, e.getClickedInventory().getItem(0)).craftRecipe();
      case 26 -> openForgeCraftMenu();
    }
  }

  /**
   * Either saves a recipe or returns to a category page with the intent to edit recipes.
   */
  public void interpretSaveClick() {
    switch (slotClicked) {
      case 8 -> { // Context
      }
      case 25 -> readSaveClick();
      case 26 -> openForgeEditMenu();
      default -> e.setCancelled(false);
    }
  }

  /**
   * Views a recipe category.
   */
  private void viewRecipeCategory() {
    Map<PlayerMeta, String> playerMeta = PluginData.pluginSystem.getPlayerMetadata().get(userUUID);
    ForgeMenuAction action = ForgeMenuAction.asEnum(playerMeta.get(PlayerMeta.FUTURE));
    String item = ChatColor.stripColor(ItemReader.readName(e.getCurrentItem()));
    int requestedPage = Integer.parseInt(playerMeta.get(PlayerMeta.PAGE));

    playerMeta.put(PlayerMeta.CATEGORY, item);
    user.openInventory(new RecipeMenu(user, action).openCategoryPage(item, requestedPage));
    playerMeta.put(PlayerMeta.INVENTORY, "forge." + ForgeMenuAction.asString(action));
  }

  /**
   * Opens the previous recipe category page.
   *
   * @param action type of interaction
   */
  private void previousRecipePage(ForgeMenuAction action) {
    Map<PlayerMeta, String> playerMeta = PluginData.pluginSystem.getPlayerMetadata().get(userUUID);
    String category = playerMeta.get(PlayerMeta.CATEGORY);
    int requestedPage = Integer.parseInt(playerMeta.get(PlayerMeta.PAGE));
    user.openInventory(new RecipeMenu(user, action).openCategoryPage(category, requestedPage - 1));
    playerMeta.put(PlayerMeta.INVENTORY, "forge." + ForgeMenuAction.asString(action));
  }

  /**
   * Opens the Recipe menu with the intent to edit recipes.
   * <p>
   * The player can return to either the main menu or a recipe category.
   * </p>
   */
  private void openForgeEditMenu() {
    Map<PlayerMeta, String> playerMeta = PluginData.pluginSystem.getPlayerMetadata().get(userUUID);
    String category = playerMeta.get(PlayerMeta.CATEGORY);
    if (category.equals("")) {
      user.openInventory(new RecipeMenu(user, ForgeMenuAction.EDIT).openMainMenu());
      playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.FORGE_CATEGORY.getMeta());
    } else {
      int requestedPage = Integer.parseInt(playerMeta.get(PlayerMeta.PAGE));
      user.openInventory(new RecipeMenu(user, ForgeMenuAction.EDIT).openCategoryPage(category, requestedPage));
      playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.FORGE_EDIT.getMeta());
    }
  }

  /**
   * Opens the Recipe menu with the intent to remove recipes.
   */
  private void openForgeRemoveMenu() {
    Map<PlayerMeta, String> playerMeta = PluginData.pluginSystem.getPlayerMetadata().get(userUUID);
    String category = playerMeta.get(PlayerMeta.CATEGORY);
    int requestedPage = Integer.parseInt(playerMeta.get(PlayerMeta.PAGE));
    user.openInventory(new RecipeMenu(user, ForgeMenuAction.REMOVE).openCategoryPage(category, requestedPage));
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.FORGE_REMOVE.getMeta());
  }

  /**
   * Opens the Recipe menu with the future action in mind.
   */
  private void returnToMainMenu() {
    Map<PlayerMeta, String> playerMeta = PluginData.pluginSystem.getPlayerMetadata().get(userUUID);
    ForgeMenuAction action = ForgeMenuAction.asEnum(playerMeta.get(PlayerMeta.FUTURE));
    playerMeta.put(PlayerMeta.CATEGORY, "");
    user.openInventory(new RecipeMenu(user, action).openMainMenu());
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.FORGE_CATEGORY.getMeta());
    playerMeta.put(PlayerMeta.PAGE, "0");
  }

  /**
   * Opens the Recipe menu with the intent to craft recipes.
   */
  private void openForgeCraftMenu() {
    Map<PlayerMeta, String> playerMeta = PluginData.pluginSystem.getPlayerMetadata().get(userUUID);
    String category = playerMeta.get(PlayerMeta.CATEGORY);
    int requestedPage = Integer.parseInt(playerMeta.get(PlayerMeta.PAGE));
    user.openInventory(new RecipeMenu(user, ForgeMenuAction.CRAFT).openCategoryPage(category, requestedPage));
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.FORGE_CRAFT.getMeta());
  }

  /**
   * Opens the next recipe category page.
   *
   * @param action type of interaction
   */
  private void nextRecipePage(ForgeMenuAction action) {
    Map<PlayerMeta, String> playerMeta = PluginData.pluginSystem.getPlayerMetadata().get(userUUID);
    String category = playerMeta.get(PlayerMeta.CATEGORY);
    int requestedPage = Integer.parseInt(playerMeta.get(PlayerMeta.PAGE));
    user.openInventory(new RecipeMenu(user, action).openCategoryPage(category, requestedPage + 1));
    playerMeta.put(PlayerMeta.INVENTORY, "forge." + ForgeMenuAction.asString(action));
  }

  /**
   * Either crafts, edits, or removes a recipe.
   *
   * @param action type of interaction
   */
  private void interpretContextualClick(ForgeMenuAction action) {
    switch (action) {
      case CRAFT -> new RecipeDetailsMenu(user, RecipeDetailsMenu.RecipeDetailsType.CRAFT, e.getCurrentItem()).craftRecipeDetails();
      case EDIT -> new RecipeDetailsMenu(user, RecipeDetailsMenu.RecipeDetailsType.SAVE, e.getCurrentItem()).editRecipeDetails();
      case REMOVE -> removeRecipe();
    }
  }

  /**
   * Checks if the recipe's details were formatted correctly before saving the recipe.
   */
  private void readSaveClick() {
    ItemStack[] menuContents = e.getInventory().getContents();
    String file = nameFile(menuContents);
    if (file != null) {
      String encodedRecipe = encodeRecipe(menuContents);
      if (encodedRecipe != null) {
        try {
          FileWriter fw = new FileWriter(PluginDirectory.FORGE.getFile().getPath() + "/" + file + "_rcp.txt");
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
   * Removes an existing recipe.
   */
  private void removeRecipe() {
    PersistentRecipe recipe = PluginData.recipeRegistry.getRecipeMap().get(ItemReader.readName(e.getCurrentItem()));
    if (recipe.delete()) {
      user.sendMessage(ChatColor.RED + "[Removed Recipe] " + ChatColor.WHITE + recipe.getName());
    } else {
      user.sendMessage(ChatColor.WHITE + recipe.getName() + ChatColor.RED + " has already been removed.");
    }
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
