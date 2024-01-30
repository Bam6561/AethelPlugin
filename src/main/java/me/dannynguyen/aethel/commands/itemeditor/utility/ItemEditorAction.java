package me.dannynguyen.aethel.commands.itemeditor.utility;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.commands.itemeditor.inventory.ItemEditorAttributes;
import me.dannynguyen.aethel.commands.itemeditor.inventory.ItemEditorEnchants;
import me.dannynguyen.aethel.commands.itemeditor.inventory.ItemEditorInventory;
import me.dannynguyen.aethel.commands.itemeditor.inventory.ItemEditorTags;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.listeners.InventoryListener;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * ItemEditorAction is a utility class that handles shared actions across ItemEditor's inventories.
 *
 * @author Danny Nguyen
 * @version 1.8.4
 * @since 1.7.0
 */
public class ItemEditorAction {
  /**
   * Uses the user's next message as the field's input.
   *
   * @param user     user
   * @param metadata metadata field
   */
  public static void awaitMessageResponse(Player user, String metadata) {
    user.closeInventory();
    user.setMetadata(PluginPlayerMeta.Namespace.MESSAGE.namespace,
        new FixedMetadataValue(Plugin.getInstance(), "itemeditor." + metadata));
  }

  /**
   * Opens a ItemEditorAttributes menu.
   *
   * @param user user
   */
  public static void openAttributesMenu(Player user) {
    if (user.hasMetadata(PluginPlayerMeta.Namespace.MESSAGE.namespace)) {
      user.removeMetadata(PluginPlayerMeta.Namespace.MESSAGE.namespace, Plugin.getInstance());
    }

    user.setMetadata(PluginPlayerMeta.Namespace.SLOT.namespace,
        new FixedMetadataValue(Plugin.getInstance(), "head"));

    user.openInventory(ItemEditorAttributes.openAttributesMenu(user, "Head"));
    user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(),
            InventoryListener.Inventory.ITEMEDITOR_ATTRIBUTES.inventory));
  }

  /**
   * Opens a ItemEditorEnchants menu.
   *
   * @param user user
   */
  public static void openEnchantsMenu(Player user) {
    if (user.hasMetadata(PluginPlayerMeta.Namespace.MESSAGE.namespace)) {
      user.removeMetadata(PluginPlayerMeta.Namespace.MESSAGE.namespace, Plugin.getInstance());
    }

    user.openInventory(ItemEditorEnchants.openMenu(user));
    user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(), InventoryListener.Inventory.ITEMEDITOR_ENCHANTS.inventory));
  }

  /**
   * Opens a ItemEditorTags menu.
   *
   * @param user user
   */
  public static void openTagsMenu(Player user) {
    if (user.hasMetadata(PluginPlayerMeta.Namespace.MESSAGE.namespace)) {
      user.removeMetadata(PluginPlayerMeta.Namespace.MESSAGE.namespace, Plugin.getInstance());
    }

    user.openInventory(ItemEditorTags.openTagsMenu(user));
    user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(), InventoryListener.Inventory.ITEMEDITOR_TAGS.inventory));
  }


  /**
   * Opens an ItemEditor main menu,
   *
   * @param user user
   */
  public static void returnToMainMenu(Player user) {
    user.openInventory(ItemEditorInventory.openMainMenu(user,
        PluginData.itemEditorData.getEditedItemMap().get(user)));
    user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(), InventoryListener.Inventory.ITEMEDITOR_COSMETICS.inventory));
  }
}
