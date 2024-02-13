package me.dannynguyen.aethel.commands.itemeditor;

import com.google.common.collect.Multimap;
import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginConstant;
import me.dannynguyen.aethel.enums.PluginNamespacedKey;
import me.dannynguyen.aethel.enums.PluginPlayerHead;
import me.dannynguyen.aethel.utility.InventoryPages;
import me.dannynguyen.aethel.utility.ItemCreator;
import me.dannynguyen.aethel.utility.TextFormatter;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ItemEditorAttributes is an inventory that edits an item's attributes.
 *
 * @author Danny Nguyen
 * @version 1.9.15
 * @since 1.7.0
 */
public class ItemEditorAttributes {
  /**
   * Opens an ItemEditorAttributes menu with attributes.
   *
   * @param user   user
   * @param action type of interaction
   * @return ItemEditorAttributes menu
   */
  public static Inventory openAttributesMenu(Player user, String action) {
    Inventory inv = createInventory(user, action);
    addAttributes(inv, user);
    addContext(inv);
    addActions(inv);
    InventoryPages.addBackButton(inv, 2);
    return inv;
  }

  /**
   * Creates and names an ItemEditorAttributes
   * inventory with its action and item being edited.
   *
   * @param user   user
   * @param action type of interaction
   * @return ItemEditorAttributes inventory
   */
  private static Inventory createInventory(Player user, String action) {
    switch (action) {
      case "head", "chest", "legs", "feet",
          "hand", "necklace", "ring" -> action = TextFormatter.capitalizeWord(action);
      case "off_hand" -> action = "Off Hand";
    }
    Inventory inv = Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "ItemEditor " + ChatColor.DARK_AQUA + "Attributes " + ChatColor.YELLOW + action);
    inv.setItem(1, PluginData.editedItemCache.getEditedItemMap().get(user));
    return inv;
  }

  /**
   * Adds attributes.
   *
   * @param inv  interacting inventory
   * @param user user
   */
  private static void addAttributes(Inventory inv, Player user) {
    ItemMeta meta = PluginData.editedItemCache.getEditedItemMap().get(user).getItemMeta();
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
    Multimap<Attribute, AttributeModifier> metaAttributes = meta.getAttributeModifiers();
    Map<String, List<AethelAttributeModifierSlot>> aethelAttributesMap = mapAethelAttributes(dataContainer);

    addAttributeCategory(inv, dataContainer, metaAttributes, aethelAttributesMap, "offense", 19);
    addAttributeCategory(inv, dataContainer, metaAttributes, aethelAttributesMap, "defense", 28);
    addAttributeCategory(inv, dataContainer, metaAttributes, aethelAttributesMap, "other", 37);
  }

  /**
   * Adds help contexts to the attributes editor.
   *
   * @param inv interacting inventory
   */
  private static void addContext(Inventory inv) {
    List<String> lore = List.of(ChatColor.WHITE + "To remove a attribute, input \"0\".");
    inv.setItem(0, ItemCreator.createPluginPlayerHead(PluginPlayerHead.QUESTION_MARK_WHITE.head, ChatColor.GREEN + "Help", lore));
    inv.setItem(18, ItemCreator.createItem(Material.IRON_SWORD, ChatColor.GREEN + "Offense", ItemFlag.HIDE_ATTRIBUTES));
    inv.setItem(27, ItemCreator.createItem(Material.IRON_CHESTPLATE, ChatColor.GREEN + "Defense", ItemFlag.HIDE_ATTRIBUTES));
    inv.setItem(36, ItemCreator.createItem(Material.SPYGLASS, ChatColor.GREEN + "Other"));
  }

  /**
   * Adds equipment slot buttons.
   *
   * @param inv interacting inventory
   */
  private static void addActions(Inventory inv) {
    inv.setItem(3, ItemCreator.createItem(Material.IRON_HELMET, ChatColor.AQUA + "Head", ItemFlag.HIDE_ATTRIBUTES));
    inv.setItem(4, ItemCreator.createItem(Material.IRON_CHESTPLATE, ChatColor.AQUA + "Chest", ItemFlag.HIDE_ATTRIBUTES));
    inv.setItem(5, ItemCreator.createItem(Material.IRON_LEGGINGS, ChatColor.AQUA + "Legs", ItemFlag.HIDE_ATTRIBUTES));
    inv.setItem(6, ItemCreator.createItem(Material.IRON_BOOTS, ChatColor.AQUA + "Feet", ItemFlag.HIDE_ATTRIBUTES));
    inv.setItem(7, ItemCreator.createItem(Material.IRON_SWORD, ChatColor.AQUA + "Hand", ItemFlag.HIDE_ATTRIBUTES));
    inv.setItem(8, ItemCreator.createItem(Material.SHIELD, ChatColor.AQUA + "Off Hand", ItemFlag.HIDE_ATTRIBUTES));
    inv.setItem(16, ItemCreator.createItem(Material.IRON_NUGGET, ChatColor.AQUA + "Necklace"));
    inv.setItem(17, ItemCreator.createItem(Material.GOLD_NUGGET, ChatColor.AQUA + "Ring"));
  }

  /**
   * Maps an item's Aethel attributes.
   *
   * @param dataContainer item's persistent data
   * @return item's Aethel attributes map
   */
  private static Map<String, List<AethelAttributeModifierSlot>> mapAethelAttributes(PersistentDataContainer dataContainer) {
    NamespacedKey listKey = PluginNamespacedKey.AETHEL_ATTRIBUTE_LIST.getNamespacedKey();
    boolean hasAttributes = dataContainer.has(listKey, PersistentDataType.STRING);
    if (hasAttributes) {
      Map<String, List<AethelAttributeModifierSlot>> attributesMap = new HashMap<>();
      List<String> attributes = new ArrayList<>(List.of(dataContainer.get(listKey, PersistentDataType.STRING).split(" ")));
      for (String attribute : attributes) {
        String attributeType = attribute.substring(0, attribute.indexOf("."));
        String attributeSlot = attribute.substring(attribute.indexOf(".") + 1);
        if (attributesMap.containsKey(attributeType)) {
          attributesMap.get(attributeType).add(new AethelAttributeModifierSlot(attributeType, attributeSlot));
        } else {
          attributesMap.put(attributeType, new ArrayList<>(List.of(new AethelAttributeModifierSlot(attributeType, attributeSlot))));
        }
      }
      return attributesMap;
    } else {
      return new HashMap<>();
    }
  }

  /**
   * Adds a category of attributes.
   * <p>
   * There are two types of attributes:
   * - Built-in (Minecraft)
   * - Custom (Aethel)
   * </p>
   *
   * @param inv                 interacting inventory
   * @param dataContainer       item's persistent tags
   * @param metaAttributes      attribute modifiers
   * @param aethelAttributesMap Aethel attributes map
   * @param category            attribute category
   * @param invSlot             inventory slot
   */
  private static void addAttributeCategory(Inventory inv, PersistentDataContainer dataContainer,
                                           Multimap<Attribute, AttributeModifier> metaAttributes,
                                           Map<String, List<AethelAttributeModifierSlot>> aethelAttributesMap,
                                           String category, int invSlot) {
    if (!aethelAttributesMap.isEmpty()) { // Read both Minecraft & Aethel attributes
      for (String attributeName : PluginConstant.aethelAttributesMap.get(category)) {
        if (PluginConstant.minecraftAttributes.contains(attributeName)) {
          addMinecraftAttribute(inv, metaAttributes, attributeName, invSlot);
        } else {
          createAethelAttribute(inv, dataContainer, aethelAttributesMap, attributeName, invSlot);
        }
        invSlot++;
      }
    } else {  // Read Minecraft attributes only
      for (String attributeName : PluginConstant.aethelAttributesMap.get(category)) {
        if (PluginConstant.minecraftAttributes.contains(attributeName)) {
          addMinecraftAttribute(inv, metaAttributes, attributeName, invSlot);
        } else {
          inv.setItem(invSlot, ItemCreator.createItem(Material.ITEM_FRAME, ChatColor.AQUA + attributeName));
        }
        invSlot++;
      }
    }
  }

  /**
   * Adds a Minecraft attribute with its slot attribute modifiers.
   *
   * @param inv            interacting inventory
   * @param metaAttributes item's attributes
   * @param attributeName  attribute name
   * @param invSlot        inventory slot
   */
  private static void addMinecraftAttribute(Inventory inv,
                                            Multimap<Attribute, AttributeModifier> metaAttributes,
                                            String attributeName, int invSlot) {
    Attribute attribute = Attribute.valueOf("GENERIC_" + TextFormatter.formatEnum(attributeName));
    boolean disabled = metaAttributes == null || metaAttributes.get(attribute).isEmpty();
    if (disabled) {
      inv.setItem(invSlot, ItemCreator.createItem(Material.ITEM_FRAME, ChatColor.AQUA + attributeName));
    } else {
      List<String> lore = new ArrayList<>();
      for (AttributeModifier attributeModifier : metaAttributes.get(attribute)) {
        lore.add(ChatColor.WHITE + "" + TextFormatter.capitalizePhrase(attributeModifier.getSlot().name()) + ": " + attributeModifier.getAmount());
      }
      inv.setItem(invSlot, ItemCreator.createItem(Material.GLOW_ITEM_FRAME, ChatColor.AQUA + attributeName, lore));
    }
  }

  /**
   * Adds an Aethel attribute with its slot attribute modifiers.
   *
   * @param inv                 interacting inventory
   * @param dataContainer       item's persistent tags
   * @param aethelAttributesMap Aethel attributes map
   * @param attributeName       attribute name
   * @param invSlot             inventory slot
   */
  private static void createAethelAttribute(Inventory inv,
                                            PersistentDataContainer dataContainer,
                                            Map<String, List<AethelAttributeModifierSlot>> aethelAttributesMap,
                                            String attributeName, int invSlot) {
    String attributeMapKey = TextFormatter.formatId(attributeName.toLowerCase());
    boolean enabled = aethelAttributesMap.containsKey(attributeMapKey);
    if (enabled) {
      List<String> lore = new ArrayList<>();
      for (AethelAttributeModifierSlot aethelAttribute : aethelAttributesMap.get(attributeMapKey)) {
        NamespacedKey attributeKey = new NamespacedKey(Plugin.getInstance(), "aethel.attribute." + aethelAttribute.getType() + "." + aethelAttribute.getSlot());
        String attributeValue = String.valueOf(dataContainer.get(attributeKey, PersistentDataType.DOUBLE));
        lore.add(ChatColor.WHITE + TextFormatter.capitalizePhrase(aethelAttribute.getSlot()) + ": " + attributeValue);
      }
      inv.setItem(invSlot, ItemCreator.createItem(Material.GLOW_ITEM_FRAME, ChatColor.AQUA + attributeName, lore));
    } else {
      inv.setItem(invSlot, ItemCreator.createItem(Material.ITEM_FRAME, ChatColor.AQUA + attributeName));
    }
  }
}
