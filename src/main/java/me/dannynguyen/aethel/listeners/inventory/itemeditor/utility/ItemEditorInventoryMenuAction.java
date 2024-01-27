package me.dannynguyen.aethel.listeners.inventory.itemeditor.utility;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.inventories.itemeditor.ItemEditorAttributes;
import me.dannynguyen.aethel.inventories.itemeditor.ItemEditorEnchants;
import me.dannynguyen.aethel.inventories.itemeditor.ItemEditorI;
import me.dannynguyen.aethel.inventories.itemeditor.ItemEditorTags;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * ItemEditorInventoryMenuAction is a utility class that
 * handles shared actions across ItemEditor's inventories.
 *
 * @author Danny Nguyen
 * @version 1.7.2
 * @since 1.7.0
 */
public class ItemEditorInventoryMenuAction {
  /**
   * Uses the user's next message as the field's input.
   *
   * @param player   interacting player
   * @param metadata metadata field
   */
  public static void awaitMessageResponse(Player player, String metadata) {
    player.closeInventory();
    player.setMetadata("message",
        new FixedMetadataValue(Plugin.getInstance(), "itemeditor." + metadata));
  }

  /**
   * Opens a ItemEditorAttributes inventory.
   *
   * @param player interacting player
   */
  public static void openAttributesMenu(Player player) {
    if (player.hasMetadata("message")) {
      player.removeMetadata("message", Plugin.getInstance());
    }

    player.setMetadata("slot", new FixedMetadataValue(Plugin.getInstance(), "head"));

    player.openInventory(ItemEditorAttributes.openAttributesMenu(player, "Head"));
    player.setMetadata("inventory",
        new FixedMetadataValue(Plugin.getInstance(), "itemeditor.attributes"));
  }

  /**
   * Opens a ItemEditorEnchants inventory.
   *
   * @param player interacting player
   */
  public static void openEnchantsMenu(Player player) {
    if (player.hasMetadata("message")) {
      player.removeMetadata("message", Plugin.getInstance());
    }

    player.openInventory(ItemEditorEnchants.openEnchantsMenu(player));
    player.setMetadata("inventory",
        new FixedMetadataValue(Plugin.getInstance(), "itemeditor.enchants"));
  }

  /**
   * Opens a ItemEditorTags inventory.
   *
   * @param player interacting player
   */
  public static void openTagsMenu(Player player) {
    if (player.hasMetadata("message")) {
      player.removeMetadata("message", Plugin.getInstance());
    }

    player.openInventory(ItemEditorTags.openTagsMenu(player));
    player.setMetadata("inventory",
        new FixedMetadataValue(Plugin.getInstance(), "itemeditor.tags"));
  }


  /**
   * Opens a ItemEditorMenu.
   *
   * @param player interacting player
   */
  public static void returnToMainMenu(Player player) {
    player.openInventory(ItemEditorI.openCosmeticMenu(player,
        PluginData.itemEditorData.getEditedItemMap().get(player)));
    player.setMetadata("inventory",
        new FixedMetadataValue(Plugin.getInstance(), "itemeditor.menu"));
  }
}
