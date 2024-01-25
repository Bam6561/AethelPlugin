package me.dannynguyen.aethel.listeners.inventory;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.inventories.forge.ForgeCraft;
import me.dannynguyen.aethel.inventories.forge.ForgeMain;
import me.dannynguyen.aethel.inventories.forge.ForgeSave;
import me.dannynguyen.aethel.inventories.forge.utility.ForgeEdit;
import me.dannynguyen.aethel.inventories.forge.utility.ForgeRemove;
import me.dannynguyen.aethel.objects.forge.ForgeCraftOperation;
import me.dannynguyen.aethel.readers.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * ForgeInventory is an inventory listener for the Forge command.
 *
 * @author Danny Nguyen
 * @version 1.7.3
 * @since 1.0.9
 */
public class ForgeInventory {
  /**
   * Opens a recipe category page.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public static void interpretMainClick(InventoryClickEvent e, Player player) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 2, 4 -> { // Help Context
        }
        case 3 -> openForgeSave(player);
        default -> {
          String action = player.getMetadata("future").get(0).asString();
          String itemName = ChatColor.stripColor(ItemReader.readItemName(e.getCurrentItem()));
          player.setMetadata("category", new FixedMetadataValue(AethelPlugin.getInstance(), itemName));
          int pageRequest = player.getMetadata("page").get(0).asInt();

          player.openInventory(ForgeMain.openForgeCategoryPage(player, action, itemName, pageRequest));
          player.setMetadata("inventory",
              new FixedMetadataValue(AethelPlugin.getInstance(), "forge." + action));
        }
      }
    }
    e.setCancelled(true);
  }

  /**
   * Either:
   * - increments or decrements a recipe page
   * - changes the interaction type
   * - opens a ForgeSave inventory
   * - contextualizes the click to expand, edit, or remove recipes
   *
   * @param e      inventory click event
   * @param player interacting player
   * @param action type of interaction
   */
  public static void interpretCategoryClick(InventoryClickEvent e, Player player, String action) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 0 -> previousRecipePage(player, action);
        case 2 -> { // Help Context
        }
        case 3 -> openForgeSave(player);
        case 4 -> {
          if (player.getMetadata("future").get(0).asString().equals("edit")) {
            openForgeEdit(player);
          }
        }
        case 5 -> openForgeRemove(player);
        case 6 -> returnToMainPage(player);
        case 8 -> nextRecipePage(player, action);
        default -> interpretContextualClick(e, action, player);
      }
    }
    e.setCancelled(true);
  }

  /**
   * Either crafts a recipe or goes back to the ForgeMain inventory with the intent to craft recipes.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public static void interpretCraftConfirmClick(InventoryClickEvent e, Player player) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 25 -> new ForgeCraftOperation().craftRecipe(e, player);
        case 26 -> openForgeCraft(player);
      }
    }
    e.setCancelled(true);
  }

  /**
   * Either saves a recipe or goes back to the ForgeMain inventory with the intent to edit recipes.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public static void interpretSaveClick(InventoryClickEvent e, Player player) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 8 -> e.setCancelled(true);
        case 25 -> ForgeSave.readSaveClick(e, player);
        case 26 -> {
          openForgeEdit(player);
          e.setCancelled(true);
        }
      }
    }
  }

  /**
   * Opens the previous recipe page.
   *
   * @param player interacting player
   * @param action type of interaction
   */
  private static void previousRecipePage(Player player, String action) {
    String categoryName = player.getMetadata("category").get(0).asString();
    int pageRequest = player.getMetadata("page").get(0).asInt();

    player.openInventory(ForgeMain.openForgeCategoryPage(player, action, categoryName,
        pageRequest - 1));
    player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "forge." + action));
  }

  /**
   * Opens a ForgeSave inventory.
   *
   * @param player interacting player
   */
  private static void openForgeSave(Player player) {
    player.openInventory(ForgeSave.createInventory(player));
    player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "forge.save"));
  }

  /**
   * Opens a ForgeMain inventory with the intent to edit recipes.
   * <p>
   * Since the player can either be returning to the main page or
   * a recipe category from here, both scenarios are supported.
   * </p>
   *
   * @param player interacting player
   */
  private static void openForgeEdit(Player player) {
    String categoryName = player.getMetadata("category").get(0).asString();
    if (categoryName.equals("")) {
      player.openInventory(ForgeMain.openForgeMainPage(player, "edit"));
      player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "forge.category"));
    } else {
      categoryName = player.getMetadata("category").get(0).asString();

      player.openInventory(ForgeMain.openForgeCategoryPage(player, "edit",
          categoryName, player.getMetadata("page").get(0).asInt()));
      player.setMetadata("inventory",
          new FixedMetadataValue(AethelPlugin.getInstance(), "forge.edit"));
    }
  }

  /**
   * Opens a ForgeMain inventory with the intent to remove recipes.
   *
   * @param player interacting player
   */
  private static void openForgeRemove(Player player) {
    String categoryName = player.getMetadata("category").get(0).asString();

    player.openInventory(ForgeMain.openForgeCategoryPage(player, "remove",
        categoryName, player.getMetadata("page").get(0).asInt()));
    player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "forge.remove"));
  }

  /**
   * Opens a ForgeMain inventory with the future action in mind.
   *
   * @param player interacting player
   */
  private static void returnToMainPage(Player player) {
    String action = player.getMetadata("future").get(0).asString();
    player.setMetadata("category", new FixedMetadataValue(AethelPlugin.getInstance(), ""));

    player.openInventory(ForgeMain.openForgeMainPage(player, action));
    player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "forge.category"));
    player.setMetadata("page", new FixedMetadataValue(AethelPlugin.getInstance(), "0"));
  }

  /**
   * Opens a ForgeMain inventory with the intent to craft recipes.
   *
   * @param player interacting player
   */
  private static void openForgeCraft(Player player) {
    String categoryName = player.getMetadata("category").get(0).asString();

    player.openInventory(ForgeMain.openForgeCategoryPage(player, "craft", categoryName,
        player.getMetadata("page").get(0).asInt()));
    player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "forge.craft"));
  }

  /**
   * Opens the next recipe page.
   *
   * @param player interacting player
   * @param action type of interaction
   */
  private static void nextRecipePage(Player player, String action) {
    String categoryName = player.getMetadata("category").get(0).asString();
    int pageRequest = player.getMetadata("page").get(0).asInt();

    player.openInventory(ForgeMain.openForgeCategoryPage(player, action, categoryName,
        pageRequest + 1));
    player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "forge." + action));
  }

  /**
   * Either crafts, edits, or removes a recipe.
   *
   * @param e      inventory click event
   * @param action interaction type
   * @param player interacting player
   */
  private static void interpretContextualClick(InventoryClickEvent e, String action, Player player) {
    switch (action) {
      case "craft" -> ForgeCraft.expandRecipeDetails(e, player);
      case "edit" -> ForgeEdit.editRecipe(e, player);
      case "remove" -> ForgeRemove.removeRecipe(e, player);
    }
  }
}
