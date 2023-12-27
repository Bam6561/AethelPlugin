package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.inventories.forge.ForgeCraft;
import me.dannynguyen.aethel.inventories.forge.ForgeDelete;
import me.dannynguyen.aethel.inventories.forge.ForgeMain;
import me.dannynguyen.aethel.inventories.forge.ForgeModify;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * ForgeListener is an inventory listener for the Forge command invocation.
 *
 * @author Danny Nguyen
 * @version 1.1.2
 * @since 1.0.9
 */
public class ForgeListener {
  /**
   * Either increments or decrements a recipe page or contextualizes the click based on the menu type.
   *
   * @param e inventory click event
   */
  public void interpretMainClick(InventoryClickEvent e, String menuType) {
    Player player = (Player) e.getWhoClicked();
    int pageRequested = Integer.parseInt(player.getMetadata("page").get(0).asString());
    switch (e.getSlot()) {
      case 0 -> {
        player.openInventory(new ForgeMain().populateView(player, pageRequested - 1));
        player.setMetadata("menu", new FixedMetadataValue(AethelPlugin.getInstance(), menuType));
      }
      case 8 -> {
        player.openInventory(new ForgeMain().populateView(player, pageRequested + 1));
        player.setMetadata("menu", new FixedMetadataValue(AethelPlugin.getInstance(), menuType));
      }
      default -> {
        if (e.getSlot() > 8 && e.getCurrentItem() != null) {
          interpretMenuContextualClick(e, menuType, player);
        }
      }
    }
    e.setCancelled(true);
  }

  /**
   * Either crafts or modifies a forge recipe.
   *
   * @param e        inventory click event
   * @param menuType menu type (craft or modify)
   * @param player   interacting player
   */
  private void interpretMenuContextualClick(InventoryClickEvent e, String menuType, Player player) {
    switch (menuType) {
      case "forge-craft" -> {
        new ForgeCraft().craftRecipe(e, player);
        player.setMetadata("menu", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-craft"));
      }
      case "forge-modify" -> interpretClickType(e, player);
    }
  }

  /**
   * Either modifies or deletes a forge recipe file.
   */
  private void interpretClickType(InventoryClickEvent e, Player player) {
    ClickType clickType = e.getClick();
    if (clickType.isLeftClick()) {
      new ForgeModify().modifyRecipeFile(e);
      player.setMetadata("menu", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-create"));
    } else if (clickType.isRightClick()) {
      new ForgeDelete().deleteRecipeFile(e);
    }
  }
}
