package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginDirectory;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.listeners.InventoryMenuListener;
import me.dannynguyen.aethel.utility.ItemCreator;
import me.dannynguyen.aethel.utility.ItemReader;
import me.dannynguyen.aethel.utility.TextFormatter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

/**
 * Inventory click event listener for Forge menus.
 *
 * @author Danny Nguyen
 * @version 1.9.15
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
    this.slotClicked = e.getSlot();
  }

  /**
   * Either saves a recipe or opens a recipe category page.
   */
  public void interpretMainMenuClick() {
    switch (slotClicked) {
      case 2, 4 -> { // Help Context
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
   */
  public void interpretCategoryClick(ForgeMenuAction action) {
    switch (slotClicked) {
      case 0 -> previousRecipePage(action);
      case 2 -> { // Help Context
      }
      case 3 -> new RecipeDetailsMenu(user, RecipeDetailsMenu.RecipeDetailsType.SAVE).saveRecipeDetails();
      case 4 -> {
        if (user.getMetadata(PluginPlayerMeta.FUTURE.getMeta()).get(0).asString().equals("edit")) {
          openForgeEditMenu();
        }
      }
      case 5 -> openForgeRemoveMenu();
      case 6 -> returnToMainMenu();
      case 8 -> nextRecipePage(action);
      default -> interpretContextualClick(action);
    }
  }

  /**
   * Either crafts a recipe or returns to a category page with the intent to craft recipes.
   */
  public void interpretCraftConfirmClick() {
    switch (e.getSlot()) {
      case 25 -> new RecipeCraftOperation(user, e.getClickedInventory().getItem(0)).craftRecipe();
      case 26 -> openForgeCraftMenu();
    }
  }

  /**
   * Either saves a recipe or returns to a category page with the intent to edit recipes.
   */
  public void interpretSaveClick() {
    switch (slotClicked) {
      case 8 -> e.setCancelled(true);
      case 25 -> {
        readSaveClick();
        e.setCancelled(true);
      }
      case 26 -> {
        openForgeEditMenu();
        e.setCancelled(true);
      }
    }
  }

  /**
   * Views a recipe category.
   */
  private void viewRecipeCategory() {
    ForgeMenuAction action = ForgeMenuAction.asAction(user.getMetadata(PluginPlayerMeta.FUTURE.getMeta()).get(0).asString());
    String itemName = ChatColor.stripColor(ItemReader.readName(e.getCurrentItem()));
    int requestedPage = user.getMetadata(PluginPlayerMeta.PAGE.getMeta()).get(0).asInt();

    user.setMetadata(PluginPlayerMeta.CATEGORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), itemName));
    user.openInventory(new ForgeMenu(user, action).openCategoryPage(itemName, requestedPage));
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), "forge." + ForgeMenuAction.asString(action)));
  }

  /**
   * Opens the previous recipe category page.
   */
  private void previousRecipePage(ForgeMenuAction action) {
    String categoryName = user.getMetadata(PluginPlayerMeta.CATEGORY.getMeta()).get(0).asString();
    int requestedPage = user.getMetadata(PluginPlayerMeta.PAGE.getMeta()).get(0).asInt();
    user.openInventory(new ForgeMenu(user, action).openCategoryPage(categoryName, requestedPage - 1));
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), "forge." + ForgeMenuAction.asString(action)));
  }

  /**
   * Opens the Forge main menu with the intent to edit recipes.
   * <p>
   * The player can return to either the main menu or a recipe category.
   * </p>
   */
  private void openForgeEditMenu() {
    String category = user.getMetadata(PluginPlayerMeta.CATEGORY.getMeta()).get(0).asString();
    if (category.equals("")) {
      user.openInventory(new ForgeMenu(user, ForgeMenuAction.EDIT).openMainMenu());
      user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.FORGE_CATEGORY.menu));
    } else {
      int requestedPage = user.getMetadata(PluginPlayerMeta.PAGE.getMeta()).get(0).asInt();
      user.openInventory(new ForgeMenu(user, ForgeMenuAction.EDIT).openCategoryPage(category, requestedPage));
      user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.FORGE_EDIT.menu));
    }
  }

  /**
   * Opens the Forge main menu with the intent to remove recipes.
   */
  private void openForgeRemoveMenu() {
    String categoryName = user.getMetadata(PluginPlayerMeta.CATEGORY.getMeta()).get(0).asString();
    int requestedPage = user.getMetadata(PluginPlayerMeta.PAGE.getMeta()).get(0).asInt();
    user.openInventory(new ForgeMenu(user, ForgeMenuAction.REMOVE).openCategoryPage(categoryName, requestedPage));
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.FORGE_REMOVE.menu));
  }

  /**
   * Opens the Forge main menu with the future action in mind.
   */
  private void returnToMainMenu() {
    ForgeMenuAction action = ForgeMenuAction.asAction(user.getMetadata(PluginPlayerMeta.FUTURE.getMeta()).get(0).asString());
    user.setMetadata(PluginPlayerMeta.CATEGORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), ""));
    user.openInventory(new ForgeMenu(user, action).openMainMenu());
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.FORGE_CATEGORY.menu));
    user.setMetadata(PluginPlayerMeta.PAGE.getMeta(), new FixedMetadataValue(Plugin.getInstance(), "0"));
  }

  /**
   * Opens the Forge main menu with the intent to craft recipes.
   */
  private void openForgeCraftMenu() {
    String categoryName = user.getMetadata(PluginPlayerMeta.CATEGORY.getMeta()).get(0).asString();
    int requestedPage = user.getMetadata(PluginPlayerMeta.PAGE.getMeta()).get(0).asInt();
    user.openInventory(new ForgeMenu(user, ForgeMenuAction.CRAFT).openCategoryPage(categoryName, requestedPage));
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.FORGE_CRAFT.menu));
  }

  /**
   * Opens the next recipe category page.
   */
  private void nextRecipePage(ForgeMenuAction action) {
    String categoryName = user.getMetadata(PluginPlayerMeta.CATEGORY.getMeta()).get(0).asString();
    int requestedPage = user.getMetadata(PluginPlayerMeta.PAGE.getMeta()).get(0).asInt();
    user.openInventory(new ForgeMenu(user, action).openCategoryPage(categoryName, requestedPage + 1));
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), "forge." + ForgeMenuAction.asString(action)));
  }

  /**
   * Either crafts, edits, or removes a recipe.
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
    String fileName = nameFile(menuContents);
    if (fileName != null) {
      String encodedRecipe = encodeRecipe(menuContents);
      if (encodedRecipe != null) {
        try {
          FileWriter fw = new FileWriter(PluginDirectory.FORGE.file.getPath() + "/" + fileName + "_rcp.txt");
          fw.write(encodedRecipe);
          fw.close();
          user.sendMessage(ChatColor.GREEN + "[Saved Recipe] " + ChatColor.WHITE + TextFormatter.capitalizePhrase(fileName));
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
    PersistentRecipe recipe = PluginData.forgeData.getRecipeMap().get(ItemReader.readName(e.getCurrentItem()));
    recipe.delete();
    user.sendMessage(ChatColor.RED + "[Removed Recipe] " + ChatColor.WHITE + recipe.getName());
  }

  /**
   * Names a recipe by the first item in the results row.
   *
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

    if (materials.toString().equals("")) {
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
