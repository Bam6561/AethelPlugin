package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.utility.TextFormatter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * ItemEditorInventoryFunctional is an inventory listener for the ItemEditor
 * command pertaining to its gameplay-related metadata inventories.
 *
 * @author Danny Nguyen
 * @version 1.7.5
 * @since 1.7.0
 */
public class ItemEditorInventoryFunctional {
  /**
   * Either changes the equipment slot mode or sets an item's attribute.
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void interpretAttributesClick(InventoryClickEvent e, Player user) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 0, 1 -> { // Context, Item
        }
        case 2 -> ItemEditorInventoryMenuAction.returnToMainMenu(user);
        case 3 -> setEquipmentSlotMode(user, "head");
        case 4 -> setEquipmentSlotMode(user, "chest");
        case 5 -> setEquipmentSlotMode(user, "legs");
        case 6 -> setEquipmentSlotMode(user, "feet");
        case 7 -> setEquipmentSlotMode(user, "hand");
        case 8 -> setEquipmentSlotMode(user, "off_hand");
        default -> readAttribute(e, user);
      }
    }
    e.setCancelled(true);
  }

  /**
   * Sets an item's enchant.
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void interpretEnchantsClick(InventoryClickEvent e, Player user) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 2, 4 -> { // Context, Item
        }
        case 6 -> ItemEditorInventoryMenuAction.returnToMainMenu(user);
        default -> readEnchant(e, user);
      }
    }
    e.setCancelled(true);
  }

  /**
   * Edits an item's Aethel tag.
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void interpretTagsClick(InventoryClickEvent e, Player user) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 2, 4 -> { // Context, Item
        }
        case 6 -> ItemEditorInventoryMenuAction.returnToMainMenu(user);
        default -> readTag(e, user);
      }
    }
    e.setCancelled(true);
  }

  /**
   * Sets the user's interacting equipment slot.
   *
   * @param user          user
   * @param equipmentSlot interacting equipment slot.
   */
  private static void setEquipmentSlotMode(Player user, String equipmentSlot) {
    user.setMetadata(PluginPlayerMeta.Namespace.SLOT.namespace, new FixedMetadataValue(Plugin.getInstance(), equipmentSlot));

    user.openInventory(ItemEditorAttributes.openAttributesMenu(user, equipmentSlot));
    user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(), PluginPlayerMeta.Inventory.ITEMEDITOR_ATTRIBUTES.inventory));
  }

  /**
   * Determines the attribute to be set and prompts the user for an input.
   *
   * @param e    inventory click event
   * @param user user
   */
  private static void readAttribute(InventoryClickEvent e, Player user) {
    String attributeName = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
    String attribute = "";
    switch (attributeName) {
      case "Attack Damage", "Attack Speed", "Max Health", "Armor", "Armor Toughness",
          "Movement Speed", "Knockback Resistance", "Luck" -> attribute = "GENERIC_"
          + attributeName.replace(" ", "_").toUpperCase();
      case "Critical Chance", "Critical Damage", "Block", "Parry",
          "Dodge", "Ability Damage", "Ability Cooldown", "Apply Status" -> attribute = "aethel.attribute."
          + attributeName.replace(" ", "_").toLowerCase();
    }
    user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message +
        ChatColor.WHITE + "Input " + ChatColor.AQUA + attributeName + ChatColor.WHITE + " value.");

    user.setMetadata(PluginPlayerMeta.Namespace.TYPE.namespace, new FixedMetadataValue(Plugin.getInstance(), attribute));
    ItemEditorInventoryMenuAction.awaitMessageResponse(user, "attributes");
  }

  /**
   * Determines the enchant to be set and prompts the user for an input.
   *
   * @param e    inventory click event
   * @param user user
   */
  private static void readEnchant(InventoryClickEvent e, Player user) {
    String enchant = ChatColor.stripColor(e.getCurrentItem().
        getItemMeta().getDisplayName().replace(" ", "_").toLowerCase());

    user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message
        + ChatColor.WHITE + "Input " + ChatColor.AQUA
        + TextFormatter.capitalizePhrase(enchant) + ChatColor.WHITE + " value.");

    user.setMetadata(PluginPlayerMeta.Namespace.TYPE.namespace, new FixedMetadataValue(Plugin.getInstance(), enchant));
    ItemEditorInventoryMenuAction.awaitMessageResponse(user, "enchants");
  }

  /**
   * Determines the Aethel tag to be set and prompts the user for an input.
   *
   * @param e    inventory click event
   * @param user user
   */
  private static void readTag(InventoryClickEvent e, Player user) {
    String aethelTag = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());

    user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message +
        ChatColor.WHITE + "Input " + ChatColor.AQUA + aethelTag + ChatColor.WHITE + " value.");

    user.setMetadata(PluginPlayerMeta.Namespace.TYPE.namespace, new FixedMetadataValue(Plugin.getInstance(), aethelTag));
    ItemEditorInventoryMenuAction.awaitMessageResponse(user, "tags");
  }
}
