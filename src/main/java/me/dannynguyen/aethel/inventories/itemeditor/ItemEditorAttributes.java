package me.dannynguyen.aethel.inventories.itemeditor;

import com.google.common.collect.Multimap;
import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.creators.ItemCreator;
import me.dannynguyen.aethel.formatters.TextFormatter;
import me.dannynguyen.aethel.inventories.utility.InventoryPages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ItemEditorAttributes is an inventory under the ItemEditor command that edits an item's attributes.
 *
 * @author Danny Nguyen
 * @version 1.7.1
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
    Inventory inv = createInventory(player, action);
    addAttributes(inv, player);
    addAttributesContext(inv);
    addEquipmentSlotButtons(inv);
    InventoryPages.addBackButton(inv, 2);
    return inv;
  }

  /**
   * Creates and names an ItemEditorAttributes inventory.
   *
   * @param player interacting player
   * @param action type of interaction
   * @return ItemEditorAttributes inventory
   */
  private static Inventory createInventory(Player player, String action) {
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
    inv.setItem(1, AethelResources.itemEditorData.getEditedItemMap().get(player));
    return inv;
  }

  /**
   * Adds attributes.
   *
   * @param inv    interacting inventory
   * @param player interacting player
   */
  private static void addAttributes(Inventory inv, Player player) {
    addAttributeCategory(inv, player, "offense", 19);
    addAttributeCategory(inv, player, "defense", 28);
    addAttributeCategory(inv, player, "other", 37);
  }

  /**
   * Adds help contexts to the attributes editor.
   *
   * @param inv interacting inventory
   */
  private static void addAttributesContext(Inventory inv) {
    List<String> helpLore = Arrays.asList(
        ChatColor.WHITE + "To remove a attribute, input \"0\".");

    inv.setItem(0, ItemCreator.createLoadedPlayerHead("WHITE_QUESTION_MARK",
        ChatColor.GREEN + "Help", helpLore));
    inv.setItem(18, ItemCreator.createItem(Material.IRON_SWORD,
        ChatColor.GREEN + "Offense", ItemFlag.HIDE_ATTRIBUTES));
    inv.setItem(27, ItemCreator.createItem(Material.IRON_CHESTPLATE,
        ChatColor.GREEN + "Defense", ItemFlag.HIDE_ATTRIBUTES));
    inv.setItem(36, ItemCreator.createItem(Material.SPYGLASS,
        ChatColor.GREEN + "Other"));
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

  /**
   * Adds a category of attributes.
   * <p>
   * There are two types of attributes:
   * - Built-in (Minecraft)
   * - Custom (Aethel)
   * </p>
   *
   * @param inv      interacting inventory
   * @param player   interacting player
   * @param category attribute category
   * @param invSlot  inventory slot
   */
  private static void addAttributeCategory(Inventory inv, Player player,
                                           String category, int invSlot) {
    ItemMeta meta = AethelResources.itemEditorData.getEditedItemMap().get(player).getItemMeta();
    Multimap<Attribute, AttributeModifier> metaAttributes = meta.getAttributeModifiers();
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();

    for (String attributeName : AethelResources.itemEditorData.getAttributesMap().get(category)) {
      switch (attributeName) {
        case "Attack Damage", "Attack Speed", "Max Health", "Armor",
            "Armor Toughness", "Movement Speed", "Knockback Resistance",
            "Luck" -> addMinecraftAttribute(inv, metaAttributes, attributeName, invSlot);
        case "Critical Chance", "Critical Damage", "Block",
            "Parry", "Dodge", "Ability Damage", "Ability Cooldown",
            "Apply Status" -> createAethelAttribute(inv, dataContainer, attributeName, invSlot);
      }
      invSlot++;
    }
  }

  /**
   * Adds a Minecraft attribute.
   *
   * @param inv            interacting inventory
   * @param metaAttributes item's attributes
   * @param attributeName  attribute name
   * @param invSlot        inventory slot
   */
  private static void addMinecraftAttribute(Inventory inv,
                                            Multimap<Attribute, AttributeModifier> metaAttributes,
                                            String attributeName, int invSlot) {
    Attribute attribute = Attribute.valueOf("GENERIC_" +
        attributeName.toUpperCase().replace(" ", "_"));

    boolean disabled = metaAttributes == null || metaAttributes.get(attribute).isEmpty();
    if (disabled) {
      inv.setItem(invSlot, ItemCreator.createItem(Material.ITEM_FRAME,
          ChatColor.AQUA + attributeName));
    } else {
      ArrayList<String> lore = new ArrayList<>();
      for (AttributeModifier attributeModifier : metaAttributes.get(attribute)) {
        lore.add(ChatColor.WHITE + "" +
            TextFormatter.capitalizeProperly(attributeModifier.getSlot().name()) +
            ": " + attributeModifier.getAmount());
      }
      inv.setItem(invSlot, ItemCreator.createItem(Material.GLOW_ITEM_FRAME,
          ChatColor.AQUA + attributeName, lore));
    }
  }

  /**
   * Adds an Aethel attribute.
   *
   * @param inv           interacting inventory
   * @param dataContainer item's persistent tags
   * @param attributeName attribute name
   * @param invSlot       inventory slot
   */
  private static void createAethelAttribute(Inventory inv,
                                            PersistentDataContainer dataContainer,
                                            String attributeName, int invSlot) {
    ArrayList<String> lore = new ArrayList<>();
    ArrayList<String> slotTypes = new ArrayList<>(
        Arrays.asList("head", "chest", "legs", "feet", "hand", "off_hand"));

    for (int i = 0; i < slotTypes.size(); i++) {
      NamespacedKey attributeKey = new NamespacedKey(AethelPlugin.getInstance(), "aethel.attribute."
          + attributeName.toLowerCase().replace(" ", "_") + "." + slotTypes.get(i));

      boolean enabled = dataContainer.has(attributeKey, PersistentDataType.STRING);
      if (enabled) {
        String attributeValue = dataContainer.get(attributeKey, PersistentDataType.STRING);
        lore.add(ChatColor.WHITE + TextFormatter.capitalizeProperly(slotTypes.get(i)) + ": " + attributeValue);
      }
    }

    if (lore.isEmpty()) {
      inv.setItem(invSlot, ItemCreator.createItem(Material.ITEM_FRAME,
          ChatColor.AQUA + attributeName));
    } else {
      inv.setItem(invSlot, ItemCreator.createItem(Material.GLOW_ITEM_FRAME,
          ChatColor.AQUA + attributeName, lore));
    }
  }
}
