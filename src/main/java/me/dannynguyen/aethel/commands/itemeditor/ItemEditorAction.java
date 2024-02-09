package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.listeners.InventoryMenuListener;
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
    user.setMetadata(PluginPlayerMeta.MESSAGE.getMeta(),
        new FixedMetadataValue(Plugin.getInstance(), "itemeditor." + metadata));
  }

  /**
   * Opens a ItemEditorAttributes menu.
   *
   * @param user user
   */
  public static void openAttributesMenu(Player user) {
    if (user.hasMetadata(PluginPlayerMeta.MESSAGE.getMeta())) {
      user.removeMetadata(PluginPlayerMeta.MESSAGE.getMeta(), Plugin.getInstance());
    }

    user.setMetadata(PluginPlayerMeta.SLOT.getMeta(),
        new FixedMetadataValue(Plugin.getInstance(), "head"));

    user.openInventory(ItemEditorAttributes.openAttributesMenu(user, "Head"));
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(),
        new FixedMetadataValue(Plugin.getInstance(),
            InventoryMenuListener.Menu.ITEMEDITOR_ATTRIBUTES.menu));
  }

  /**
   * Opens a ItemEditorEnchants menu.
   *
   * @param user user
   */
  public static void openEnchantsMenu(Player user) {
    if (user.hasMetadata(PluginPlayerMeta.MESSAGE.getMeta())) {
      user.removeMetadata(PluginPlayerMeta.MESSAGE.getMeta(), Plugin.getInstance());
    }

    user.openInventory(ItemEditorEnchants.openMenu(user));
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(),
        new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.ITEMEDITOR_ENCHANTS.menu));
  }

  /**
   * Opens a ItemEditorTags menu.
   *
   * @param user user
   */
  public static void openTagsMenu(Player user) {
    if (user.hasMetadata(PluginPlayerMeta.MESSAGE.getMeta())) {
      user.removeMetadata(PluginPlayerMeta.MESSAGE.getMeta(), Plugin.getInstance());
    }

    user.openInventory(ItemEditorTags.openTagsMenu(user));
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(),
        new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.ITEMEDITOR_TAGS.menu));
  }


  /**
   * Opens an ItemEditor main menu.
   *
   * @param user user
   */
  public static void returnToMainMenu(Player user) {
    user.openInventory(ItemEditorInventory.openMainMenu(user,
        PluginData.itemEditorData.getEditedItemMap().get(user)));
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(),
        new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.ITEMEDITOR_COSMETICS.menu));
  }
}
