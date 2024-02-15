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
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents a menu that edits an item's attributes.
 *
 * @author Danny Nguyen
 * @version 1.9.20
 * @since 1.7.0
 */
class AttributeEditorMenu {
  /**
   * AttributeEditor GUI.
   */
  private final Inventory menu;

  /**
   * GUI user.
   */
  private final Player user;

  /**
   * ItemStack being edited.
   */
  private final ItemStack item;

  /**
   * GUI action.
   */
  private final AttributeEditorAction action;

  /**
   * ItemStack data container.
   */
  private final PersistentDataContainer dataContainer;

  /**
   * ItemStack Minecraft attributes.
   */
  private final Multimap<Attribute, AttributeModifier> attributeModifiers;

  /**
   * ItemStack Aethel attributes.
   */
  private final Map<String, List<String>> aethelAttributesMap;

  /**
   * Associates a new AttributeEditor menu with its user and editing item.
   *
   * @param user user
   */
  protected AttributeEditorMenu(@NotNull Player user, @NotNull AttributeEditorAction action) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.item = PluginData.editedItemCache.getEditedItemMap().get(user);
    this.action = action;
    this.dataContainer = item.getItemMeta().getPersistentDataContainer();
    this.attributeModifiers = item.getItemMeta().getAttributeModifiers();
    this.aethelAttributesMap = mapAethelAttributes();
    this.menu = createMenu();
  }

  /**
   * Creates and names a new AttributeEditor menu with its action.
   *
   * @return AttributeEditor menu
   */
  private Inventory createMenu() {
    String actionString = AttributeEditorAction.asString(action);
    switch (actionString) {
      case "head", "chest", "legs", "feet", "hand", "necklace", "ring" -> actionString = TextFormatter.capitalizeWord(actionString);
      case "off_hand" -> actionString = "Off Hand";
    }
    Inventory inv = Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "ItemEditor " + ChatColor.DARK_AQUA + "Attributes " + ChatColor.YELLOW + actionString);
    inv.setItem(1, item);
    return inv;
  }

  /**
   * Sets the menu to display interactions with attributes.
   *
   * @return AttributeEditor menu
   */
  @NotNull
  protected Inventory openMenu() {
    addAttributes();
    addContext();
    addActions();
    InventoryPages.addBackButton(menu, 2);
    return menu;
  }

  /**
   * Adds attributes.
   */
  private void addAttributes() {
    addAttributeCategory("offense", 19);
    addAttributeCategory("defense", 28);
    addAttributeCategory("other", 37);
  }

  /**
   * Adds contextual help.
   */
  private void addContext() {
    menu.setItem(0, ItemCreator.createPluginPlayerHead(PluginPlayerHead.QUESTION_MARK_WHITE.head, ChatColor.GREEN + "Help", List.of(ChatColor.WHITE + "To remove a attribute, input \"0\".")));
    menu.setItem(18, ItemCreator.createItem(Material.IRON_SWORD, ChatColor.GREEN + "Offense", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(27, ItemCreator.createItem(Material.IRON_CHESTPLATE, ChatColor.GREEN + "Defense", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(36, ItemCreator.createItem(Material.SPYGLASS, ChatColor.GREEN + "Other"));
  }

  /**
   * Adds equipment slot buttons.
   */
  private void addActions() {
    menu.setItem(3, ItemCreator.createItem(Material.IRON_HELMET, ChatColor.AQUA + "Head", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(4, ItemCreator.createItem(Material.IRON_CHESTPLATE, ChatColor.AQUA + "Chest", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(5, ItemCreator.createItem(Material.IRON_LEGGINGS, ChatColor.AQUA + "Legs", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(6, ItemCreator.createItem(Material.IRON_BOOTS, ChatColor.AQUA + "Feet", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(7, ItemCreator.createItem(Material.IRON_SWORD, ChatColor.AQUA + "Hand", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(8, ItemCreator.createItem(Material.SHIELD, ChatColor.AQUA + "Off Hand", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(16, ItemCreator.createItem(Material.IRON_NUGGET, ChatColor.AQUA + "Necklace"));
    menu.setItem(17, ItemCreator.createItem(Material.GOLD_NUGGET, ChatColor.AQUA + "Ring"));
  }

  /**
   * Maps an item's Aethel attributes.
   *
   * @return item's Aethel attributes map
   */
  private Map<String, List<String>> mapAethelAttributes() {
    NamespacedKey listKey = PluginNamespacedKey.AETHEL_ATTRIBUTE_LIST.getNamespacedKey();
    boolean hasAttributes = dataContainer.has(listKey, PersistentDataType.STRING);
    if (hasAttributes) {
      Map<String, List<String>> attributesMap = new HashMap<>();
      List<String> attributes = new ArrayList<>(List.of(dataContainer.get(listKey, PersistentDataType.STRING).split(" ")));
      for (String attribute : attributes) {
        String attributeType = attribute.substring(0, attribute.indexOf("."));
        if (attributesMap.containsKey(attributeType)) {
          attributesMap.get(attributeType).add(attribute.substring(attribute.indexOf(".") + 1));
        } else {
          attributesMap.put(attributeType, new ArrayList<>(List.of(attribute.substring(attribute.indexOf(".") + 1))));
        }
      }
      return attributesMap;
    } else {
      return null;
    }
  }

  /**
   * Adds a category of attributes.
   * <p>
   * There are two types of attributes:
   * - Minecraft
   * - Aethel
   * </p>
   *
   * @param category attribute category
   * @param invSlot  inventory slot
   */
  private void addAttributeCategory(String category, int invSlot) {
    if (aethelAttributesMap != null) { // Read both Minecraft & Aethel attributes
      for (String attributeName : PluginConstant.aethelAttributesMap.get(category)) {
        if (PluginConstant.minecraftAttributes.contains(attributeName)) {
          addMinecraftAttribute(attributeName, invSlot);
        } else {
          addAethelAttribute(attributeName, invSlot);
        }
        invSlot++;
      }
    } else {  // Read Minecraft attributes only
      for (String attributeName : PluginConstant.aethelAttributesMap.get(category)) {
        if (PluginConstant.minecraftAttributes.contains(attributeName)) {
          addMinecraftAttribute(attributeName, invSlot);
        } else {
          menu.setItem(invSlot, ItemCreator.createItem(Material.ITEM_FRAME, ChatColor.AQUA + attributeName));
        }
        invSlot++;
      }
    }
  }

  /**
   * Adds a Minecraft attribute with its slot attribute modifiers.
   *
   * @param attributeName attribute name
   * @param invSlot       inventory slot
   */
  private void addMinecraftAttribute(String attributeName, int invSlot) {
    Attribute attribute = Attribute.valueOf("GENERIC_" + TextFormatter.formatEnum(attributeName));
    boolean disabled = attributeModifiers == null || attributeModifiers.get(attribute).isEmpty();
    if (disabled) {
      menu.setItem(invSlot, ItemCreator.createItem(Material.ITEM_FRAME, ChatColor.AQUA + attributeName));
    } else {
      List<String> lore = new ArrayList<>();
      for (AttributeModifier attributeModifier : attributeModifiers.get(attribute)) {
        lore.add(ChatColor.WHITE + "" + TextFormatter.capitalizePhrase(attributeModifier.getSlot().name()) + ": " + attributeModifier.getAmount());
      }
      menu.setItem(invSlot, ItemCreator.createItem(Material.GLOW_ITEM_FRAME, ChatColor.AQUA + attributeName, lore));
    }
  }

  /**
   * Adds an Aethel attribute with its slot attribute modifiers.
   *
   * @param attributeName attribute name
   * @param invSlot       inventory slot
   */
  private void addAethelAttribute(String attributeName, int invSlot) {
    String attributeMapKey = TextFormatter.formatId(attributeName);
    boolean enabled = aethelAttributesMap.containsKey(attributeMapKey);
    if (enabled) {
      List<String> lore = new ArrayList<>();
      for (String attribute : aethelAttributesMap.get(attributeMapKey)) {
        NamespacedKey attributeKey = new NamespacedKey(Plugin.getInstance(), "aethel.attribute." + attributeMapKey + "." + attribute);
        Double attributeValue = dataContainer.get(attributeKey, PersistentDataType.DOUBLE);
        lore.add(ChatColor.WHITE + TextFormatter.capitalizePhrase(attribute + ": " + attributeValue));
      }
      menu.setItem(invSlot, ItemCreator.createItem(Material.GLOW_ITEM_FRAME, ChatColor.AQUA + attributeName, lore));
    } else {
      menu.setItem(invSlot, ItemCreator.createItem(Material.ITEM_FRAME, ChatColor.AQUA + attributeName));
    }
  }
}
