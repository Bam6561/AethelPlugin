package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.inventories.forge.*;
import me.dannynguyen.aethel.objects.forge.ForgeCraftOperation;
import me.dannynguyen.aethel.readers.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * ForgeListener is an inventory listener for the Forge command.
 *
 * @author Danny Nguyen
 * @version 1.5.4
 * @since 1.0.9
 */
public class ForgeListener {
  public static void interpretForgeMainClickNew(InventoryClickEvent e, Player player) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 2 -> { // Help Context
        }
        case 3 -> openForgeSaveInventory(player);
        default -> {
          String itemName = ChatColor.stripColor(ItemReader.readItemName(e.getCurrentItem()));
          player.setMetadata("category",
              new FixedMetadataValue(AethelPlugin.getInstance(), itemName));
          String action = player.getMetadata("action").get(0).asString();
          int pageRequest = player.getMetadata("page").get(0).asInt();

          player.openInventory(ForgeMain.openForgeCategoryPage(player, action, itemName, pageRequest));
          player.setMetadata("inventory",
              new FixedMetadataValue(AethelPlugin.getInstance(), "forge-" + action));
        }
      }
      e.setCancelled(true);
    }
  }

  public static void interpretForgeCategoryClickNew(InventoryClickEvent e, Player player, String action) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 0 -> previousRecipePage(player, action);
        case 2 -> { // Help Context
        }
        case 3 -> openForgeSaveInventory(player);
        case 4 -> openForgeModifyInventory(player);
        case 5 -> openForgeDeleteInventory(player);
        case 6 -> returnToMainPage(player);
        case 8 -> nextRecipePage(player, action);
        default -> interpretContextualClick(e, action, player);
      }
      e.setCancelled(true);
    }
  }

  /**
   * Opens a AethelItemMain inventory.
   *
   * @param player interacting playert
   */
  private static void returnToMainPage(Player player) {
    player.setMetadata("category", new FixedMetadataValue(AethelPlugin.getInstance(), "categories"));
    player.openInventory(ForgeMain.openForgeMainPage(player, "categories", "modify"));
    player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-category"));
  }

  /**
   * Either:
   * - increments or decrements a recipe page
   * - expands a recipe's details
   * - changes the interaction type
   * - contextualizes the click to modify or delete recipes
   *
   * @param e      inventory click event
   * @param player interacting player
   * @param action type of interaction
   */
  public static void interpretForgeMainClick(InventoryClickEvent e, Player player, String action) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 0 -> previousRecipePage(player, action);
        case 2 -> { // Help Context
        }
        case 3 -> openForgeSaveInventory(player);
        case 4 -> {
          String itemName = e.getCurrentItem().getItemMeta().getDisplayName();
          if (!itemName.equals(ChatColor.GREEN + "Help")) {
            openForgeModifyInventory(player);
          }
        }
        case 5 -> openForgeDeleteInventory(player);
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
  public static void interpretForgeCraftConfirmClick(InventoryClickEvent e, Player player) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 25 -> new ForgeCraftOperation().craftRecipe(e, player);
        case 26 -> openForgeCraftInventory(player);
      }
    }
    e.setCancelled(true);
  }

  /**
   * Either saves a recipe or goes back to the ForgeMain inventory with the intent to modify recipes.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public static void interpretForgeSaveClick(InventoryClickEvent e, Player player) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 8 -> e.setCancelled(true);
        case 25 -> ForgeSave.readSaveClick(e, player);
        case 26 -> {
          openForgeModifyInventory(player);
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
    player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-" + action));
  }

  /**
   * Opens a ForgeSave inventory.
   *
   * @param player interacting player
   */

  private static void openForgeSaveInventory(Player player) {
    player.openInventory(ForgeSave.createInventory(player));
    player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-save"));
  }

  /**
   * Opens a ForgeMain inventory with the intent to modify recipes.
   *
   * @param player interacting player
   */
  private static void openForgeModifyInventory(Player player) {
    String categoryName = player.getMetadata("category").get(0).asString();
    player.openInventory(ForgeMain.openForgeCategoryPage(player, "modify",
        categoryName, player.getMetadata("page").get(0).asInt()));
    player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-modify"));
  }

  /**
   * Opens a ForgeMain inventory with the intent to delete recipes.
   *
   * @param player interacting player
   */
  private static void openForgeDeleteInventory(Player player) {
    String categoryName = player.getMetadata("category").get(0).asString();
    player.openInventory(ForgeMain.openForgeCategoryPage(player, "delete",
        categoryName, player.getMetadata("page").get(0).asInt()));
    player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-delete"));
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
    player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-" + action));
  }


  /**
   * Either crafts, modifies, or deletes a recipe.
   *
   * @param e      inventory click event
   * @param action interaction type
   * @param player interacting player
   */
  private static void interpretContextualClick(InventoryClickEvent e, String action, Player player) {
    switch (action) {
      case "craft" -> ForgeCraft.expandRecipeDetails(e, player);
      case "modify" -> ForgeModify.modifyRecipe(e, player);
      case "delete" -> ForgeDelete.deleteRecipe(e, player);
    }
  }

  /**
   * Opens a ForgeMain inventory with the intent to craft recipes.
   *
   * @param player interacting player
   */
  private static void openForgeCraftInventory(Player player) {
    String categoryName = player.getMetadata("category").get(0).asString();
    player.openInventory(ForgeMain.openForgeCategoryPage(player, "craft", categoryName,
        player.getMetadata("page").get(0).asInt()));
    player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-craft"));
  }
}
