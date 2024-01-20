package me.dannynguyen.aethel.listeners.inventory;

import me.dannynguyen.aethel.AethelPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * ItemEditorInventoryListener is an inventory listener for the ItemEditor command.
 *
 * @author Danny Nguyen
 * @version 1.6.8
 * @since 1.6.7
 */
public class ItemEditorInventoryListener {
  /**
   * Edits an item's metadata field.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public static void interpretMenuClick(InventoryClickEvent e, Player player) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 9 -> awaitMessageResponse(player, "display_name");
        case 10 -> awaitMessageResponse(player, "custom_model_data");
        case 11 -> awaitMessageResponse(player, "lore");
      }
    }
    e.setCancelled(true);
  }

  /**
   * Uses the user's next message as the field's input.
   *
   * @param player   interacting player
   * @param metadata metadata field
   */
  private static void awaitMessageResponse(Player player, String metadata) {
    player.closeInventory();
    player.setMetadata("message",
        new FixedMetadataValue(AethelPlugin.getInstance(), "itemeditor." + metadata));
  }
}
