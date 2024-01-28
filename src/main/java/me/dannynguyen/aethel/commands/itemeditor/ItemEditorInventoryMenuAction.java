package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
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
   * @param user     interacting user
   * @param metadata metadata field
   */
  public static void awaitMessageResponse(Player user, String metadata) {
    user.closeInventory();
    user.setMetadata(PluginPlayerMeta.Namespace.MESSAGE.namespace,
        new FixedMetadataValue(Plugin.getInstance(), "itemeditor." + metadata));
  }

  /**
   * Opens a ItemEditorAttributes inventory.
   *
   * @param user interacting user
   */
  public static void openAttributesMenu(Player user) {
    if (user.hasMetadata(PluginPlayerMeta.Namespace.MESSAGE.namespace)) {
      user.removeMetadata(PluginPlayerMeta.Namespace.MESSAGE.namespace, Plugin.getInstance());
    }

    user.setMetadata(PluginPlayerMeta.Namespace.SLOT.namespace,
        new FixedMetadataValue(Plugin.getInstance(), "head"));

    user.openInventory(ItemEditorAttributes.openAttributesMenu(user, "Head"));
    user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(), PluginPlayerMeta.Inventory.ITEMEDITOR_ATTRIBUTES.inventory));
  }

  /**
   * Opens a ItemEditorEnchants inventory.
   *
   * @param user interacting user
   */
  public static void openEnchantsMenu(Player user) {
    if (user.hasMetadata(PluginPlayerMeta.Namespace.MESSAGE.namespace)) {
      user.removeMetadata(PluginPlayerMeta.Namespace.MESSAGE.namespace, Plugin.getInstance());
    }

    user.openInventory(ItemEditorEnchants.openEnchantsMenu(user));
    user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(), PluginPlayerMeta.Inventory.ITEMEDITOR_ENCHANTS.inventory));
  }

  /**
   * Opens a ItemEditorTags inventory.
   *
   * @param user interacting user
   */
  public static void openTagsMenu(Player user) {
    if (user.hasMetadata(PluginPlayerMeta.Namespace.MESSAGE.namespace)) {
      user.removeMetadata(PluginPlayerMeta.Namespace.MESSAGE.namespace, Plugin.getInstance());
    }

    user.openInventory(ItemEditorTags.openTagsMenu(user));
    user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(), PluginPlayerMeta.Inventory.ITEMEDITOR_TAGS.inventory));
  }


  /**
   * Opens a ItemEditorMenu.
   *
   * @param user interacting user
   */
  public static void returnToMainMenu(Player user) {
    user.openInventory(ItemEditorI.openCosmeticMenu(user,
        PluginData.itemEditorData.getEditedItemMap().get(user)));
    user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(), PluginPlayerMeta.Inventory.ITEMEDITOR_COSMETICS));
  }
}
