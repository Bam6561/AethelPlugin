package me.dannynguyen.aethel.listeners.inventory.itemeditor;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.formatters.TextFormatter;
import me.dannynguyen.aethel.inventories.itemeditor.ItemEditorAttributes;
import me.dannynguyen.aethel.listeners.inventory.itemeditor.utility.ItemEditorInventoryMenuAction;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * ItemEditorInventoryGameplay is an inventory listener for the ItemEditor
 * command pertaining to its gameplay-related metadata inventories.
 *
 * @author Danny Nguyen
 * @version 1.7.0
 * @since 1.7.0
 */
public class ItemEditorInventoryGameplay {
  /**
   * Either changes the equipment slot mode or sets an item's attribute.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public static void interpretAttributesClick(InventoryClickEvent e, Player player) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 0, 1 -> { // Context, Item
        }
        case 2 -> ItemEditorInventoryMenuAction.returnToMainMenu(player);
        case 3 -> setEquipmentSlotMode(player, "head");
        case 4 -> setEquipmentSlotMode(player, "chest");
        case 5 -> setEquipmentSlotMode(player, "legs");
        case 6 -> setEquipmentSlotMode(player, "feet");
        case 7 -> setEquipmentSlotMode(player, "hand");
        case 8 -> setEquipmentSlotMode(player, "off_hand");
        default -> readAttribute(e, player);
      }
    }
    e.setCancelled(true);
  }

  /**
   * Sets an item's enchant.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public static void interpretEnchantsClick(InventoryClickEvent e, Player player) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 2, 4 -> { // Context, Item
        }
        case 6 -> ItemEditorInventoryMenuAction.returnToMainMenu(player);
        default -> readEnchant(e, player);
      }
    }
    e.setCancelled(true);
  }

  /**
   * Edits an item's Aethel tag.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public static void interpretTagsClick(InventoryClickEvent e, Player player) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 2, 4 -> { // Context, Item
        }
        case 6 -> ItemEditorInventoryMenuAction.returnToMainMenu(player);
        default -> readTag(e, player);
      }
    }
    e.setCancelled(true);
  }

  /**
   * Sets the player's interacting equipment slot.
   *
   * @param player        interacting player
   * @param equipmentSlot interacting equipment slot.
   */
  private static void setEquipmentSlotMode(Player player, String equipmentSlot) {
    player.setMetadata("slot", new FixedMetadataValue(AethelPlugin.getInstance(), equipmentSlot));

    player.openInventory(ItemEditorAttributes.openAttributesMenu(player, equipmentSlot));
    player.setMetadata("inventory",
        new FixedMetadataValue(AethelPlugin.getInstance(), "itemeditor.attributes"));
  }

  /**
   * Determines the attribute to be set and prompts the user for an input.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  private static void readAttribute(InventoryClickEvent e, Player player) {
    String attributeName = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
    String attribute = attributeName.replace(" ", "_").toUpperCase();

    player.sendMessage(ChatColor.GOLD + "[!] " +
        ChatColor.WHITE + "Input " + ChatColor.AQUA + attributeName + ChatColor.WHITE + " value.");

    player.setMetadata("input", new FixedMetadataValue(AethelPlugin.getInstance(), attribute));
    ItemEditorInventoryMenuAction.awaitMessageResponse(player, "attributes");
  }

  /**
   * Determines the enchant to be set and prompts the user for an input.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  private static void readEnchant(InventoryClickEvent e, Player player) {
    String enchant = ChatColor.stripColor(e.getCurrentItem().
        getItemMeta().getDisplayName().replace(" ", "_").toLowerCase());

    player.sendMessage(ChatColor.GOLD + "[!] "
        + ChatColor.WHITE + "Input " + ChatColor.AQUA
        + TextFormatter.capitalizeProperly(enchant) + ChatColor.WHITE + " value.");

    player.setMetadata("input", new FixedMetadataValue(AethelPlugin.getInstance(), enchant));
    ItemEditorInventoryMenuAction.awaitMessageResponse(player, "enchants");
  }

  /**
   * Determines the Aethel tag to be set and prompts the user for an input.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  private static void readTag(InventoryClickEvent e, Player player) {
    String aethelTag = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());

    player.sendMessage(ChatColor.GOLD + "[!] " +
        ChatColor.WHITE + "Input " + ChatColor.AQUA + aethelTag + ChatColor.WHITE + " value.");

    player.setMetadata("input", new FixedMetadataValue(AethelPlugin.getInstance(), aethelTag));
    ItemEditorInventoryMenuAction.awaitMessageResponse(player, "tags");
  }
}
