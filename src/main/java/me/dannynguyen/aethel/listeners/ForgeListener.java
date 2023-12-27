package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.inventories.forge.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * ForgeListener is an inventory listener for the Forge command invocation.
 *
 * @author Danny Nguyen
 * @version 1.1.5
 * @since 1.0.9
 */
public class ForgeListener {
  /**
   * Either:
   * - increments or decrements a recipe page
   * - changes the interaction type
   * - contextualizes the click
   *
   * @param e      inventory click event
   * @param action type of interaction
   */
  public void interpretForgeMainClick(InventoryClickEvent e, String action) {
    if (e.getCurrentItem() != null) {
      Player player = (Player) e.getWhoClicked();
      switch (e.getSlot()) {
        case 0 -> previousRecipePage(player, action);
        case 3 -> openForgeCreateInventory(player);
        case 4 -> openForgeModifyInventory(player);
        case 5 -> openForgeDeleteInventory(player);
        case 8 -> nextRecipePage(player, action);
        default -> interpretContextualClick(e, action, player);
      }
    }
    e.setCancelled(true);
  }

  /**
   * Either crafts, modifies, or deletes a recipe.
   *
   * @param e      inventory click event
   * @param action interaction type
   * @param player interacting player
   */
  private void interpretContextualClick(InventoryClickEvent e, String action, Player player) {
    switch (action) {
      case "craft" -> {
        new ForgeCraft().craftRecipe(e, player);
        player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-craft"));
      }
      case "modify" -> {
        new ForgeModify().modifyRecipeFile(e, player);
        player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-create"));
      }
      case "delete" -> {
        new ForgeDelete().deleteRecipeFile(e, player);
        player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-delete"));
      }
    }
  }

  /**
   * Either saves a recipe or goes back to the ForgeMain inventory with the intent to modify recipes.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public void interpretForgeCreateClick(InventoryClickEvent e, Player player) {
    switch (e.getSlot()) {
      case 25 -> new ForgeCreate().readSaveClick(e, player);
      case 26 -> {
        openForgeModifyInventory(player);
        e.setCancelled(true);
      }
    }
  }

  /**
   * Opens the previous recipe page.
   *
   * @param player interacting player
   * @param action type of interaction
   */
  private void previousRecipePage(Player player, String action) {
    int pageRequest = player.getMetadata("page").get(0).asInt();
    player.openInventory(new ForgeMain().processPageToDisplay(player, action,
        pageRequest - 1));
    player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-" + action));
  }

  /**
   * Opens a ForgeCreate inventory.
   *
   * @param player interacting player
   */

  private void openForgeCreateInventory(Player player) {
    player.openInventory(new ForgeCreate().createInventory(player));
    player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-create"));
  }

  /**
   * Opens a ForgeMain inventory with the intent to modify recipes.
   *
   * @param player interacting player
   */
  private void openForgeModifyInventory(Player player) {
    player.openInventory(new ForgeMain().processPageToDisplay(player, "modify",
        player.getMetadata("page").get(0).asInt()));
    player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-modify"));
  }

  /**
   * Opens a ForgeMain inventory with the intent to delete recipes.
   *
   * @param player interacting player
   */
  private void openForgeDeleteInventory(Player player) {
    player.openInventory(new ForgeMain().processPageToDisplay(player, "delete",
        player.getMetadata("page").get(0).asInt()));
    player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-delete"));
  }

  /**
   * Opens the next recipe page.
   *
   * @param player interacting player
   * @param action type of interaction
   */
  private void nextRecipePage(Player player, String action) {
    int pageRequest = player.getMetadata("page").get(0).asInt();
    player.openInventory(new ForgeMain().processPageToDisplay(player, action,
        pageRequest + 1));
    player.setMetadata("inventory", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-" + action));
  }
}
