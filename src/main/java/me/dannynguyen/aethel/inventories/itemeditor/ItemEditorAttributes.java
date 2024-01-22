package me.dannynguyen.aethel.inventories.itemeditor;

import com.google.common.collect.Multimap;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.creators.ItemCreator;
import me.dannynguyen.aethel.formatters.TextFormatter;
import me.dannynguyen.aethel.inventories.utility.Pagination;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ItemEditorAttributes is an inventory under the ItemEditor command that edits an item's attributes.
 *
 * @author Danny Nguyen
 * @version 1.7.0
 * @since 1.7.0
 */
public class ItemEditorAttributes {
  /**
   * Opens an ItemEditorAttributes inventory with attributes.
   *
   * @param player interacting player
   * @param action type of interaction
   * @return ItemEditorAttributes inventory with attributes
   */
  public static Inventory openAttributesMenu(Player player, String action) {
    ItemStack item = AethelResources.itemEditorData.getEditedItemMap().get(player);
    Inventory inv = createInventory(player, item, action);
    addAttributes(inv, player);
    addAttributesContext(inv);
    addEquipmentSlotButtons(inv);
    Pagination.addBackButton(inv, 2);
    return inv;
  }

  /**
   * Creates and names an ItemEditorAttributes inventory.
   *
   * @param player interacting player
   * @param item   interacting item
   * @param action type of interaction
   * @return ItemEditorAttributes inventory
   */
  private static Inventory createInventory(Player player, ItemStack item, String action) {
    switch (action) {
      case "head" -> action = "Head";
      case "chest" -> action = "Chest";
      case "legs" -> action = "Feet";
      case "feet" -> action = "Legs";
      case "hand" -> action = "Hand";
      case "off_hand" -> action = "Off Hand";
    }
    Inventory inv = Bukkit.createInventory(player, 54,
        ChatColor.DARK_GRAY + "ItemEditor " +
            ChatColor.DARK_AQUA + "Attributes " +
            ChatColor.YELLOW + action);
    inv.setItem(1, item);
    return inv;
  }

  /**
   * Adds attributes.
   *
   * @param inv    interacting inventory
   * @param player interacting player
   */
  private static void addAttributes(Inventory inv, Player player) {
    ItemMeta meta = AethelResources.itemEditorData.getEditedItemMap().get(player).getItemMeta();
    Multimap<Attribute, AttributeModifier> metaAttributes = meta.getAttributeModifiers();

    int invSlot = 9;
    for (Attribute attribute : AethelResources.itemEditorData.getAttributes()) {
      String attributeName =
          ChatColor.AQUA + TextFormatter.capitalizeProperly(attribute.getKey().getKey(), ".");

      boolean disabled = metaAttributes == null || metaAttributes.get(attribute).isEmpty();
      if (disabled) {
        inv.setItem(invSlot, ItemCreator.createItem(Material.ITEM_FRAME, attributeName));
      } else {
        ArrayList<String> lore = new ArrayList<>();
        for (AttributeModifier attributeModifier : metaAttributes.get(attribute)) {
          lore.add(ChatColor.WHITE + "" +
              TextFormatter.capitalizeProperly(attributeModifier.getSlot().name()) +
              ": " + attributeModifier.getAmount());
        }
        inv.setItem(invSlot, ItemCreator.createItem(Material.GLOW_ITEM_FRAME, attributeName, lore));
      }
      invSlot++;
    }
  }

  /**
   * Adds a help context to the attributes editor.
   *
   * @param inv interacting inventory
   */
  private static void addAttributesContext(Inventory inv) {
    List<String> helpLore = Arrays.asList(
        ChatColor.WHITE + "To remove a attribute, input \"0\".");
    inv.setItem(0, ItemCreator.createLoadedPlayerHead("WHITE_QUESTION_MARK",
        ChatColor.GREEN + "Help", helpLore));
  }

  /**
   * Adds equipment slot buttons.
   *
   * @param inv interacting inventory
   */
  private static void addEquipmentSlotButtons(Inventory inv) {
    inv.setItem(3, ItemCreator.createItem(Material.IRON_HELMET, ChatColor.AQUA + "Head"));
    inv.setItem(4, ItemCreator.createItem(Material.IRON_CHESTPLATE, ChatColor.AQUA + "Chest"));
    inv.setItem(5, ItemCreator.createItem(Material.IRON_LEGGINGS, ChatColor.AQUA + "Legs"));
    inv.setItem(6, ItemCreator.createItem(Material.IRON_BOOTS, ChatColor.AQUA + "Feet"));
    inv.setItem(7, ItemCreator.createItem(Material.IRON_SWORD, ChatColor.AQUA + "Hand"));
    inv.setItem(8, ItemCreator.createItem(Material.SHIELD, ChatColor.AQUA + "Off Hand"));
  }
}
