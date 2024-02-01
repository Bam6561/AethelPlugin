package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginNamespacedKey;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.listeners.InventoryListener;
import me.dannynguyen.aethel.utility.TextFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ItemEditorMessageListener is a message listener for the ItemEditor text inputs.
 *
 * @author Danny Nguyen
 * @version 1.8.10
 * @since 1.7.0
 */
public class ItemEditorMessageListener {
  /**
   * Sets the item's display name.
   *
   * @param e    message event
   * @param user user
   * @param item interacting item
   * @param meta item meta
   */
  public static void setDisplayName(AsyncPlayerChatEvent e, Player user,
                                    ItemStack item, ItemMeta meta) {
    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
    item.setItemMeta(meta);
    user.sendMessage(Success.NAME_ITEM.message + ChatColor.WHITE + e.getMessage());
    returnToMainMenu(user, item);
  }

  /**
   * Sets the item's custom model data.
   *
   * @param e    message event
   * @param user user
   * @param item interacting item
   * @param meta item meta
   * @throws NumberFormatException not an integer
   */
  public static void setCustomModelData(AsyncPlayerChatEvent e, Player user,
                                        ItemStack item, ItemMeta meta) {
    try {
      meta.setCustomModelData(Integer.parseInt(e.getMessage()));
      item.setItemMeta(meta);
      user.sendMessage(Success.SET_CUSTOMMODELDATA.message +
          ChatColor.WHITE + e.getMessage());
    } catch (NumberFormatException ex) {
      user.sendMessage(Failure.INVALID_CUSTOMMODELDATA.message);
    }
    returnToMainMenu(user, item);
  }

  /**
   * Sets the lore.
   *
   * @param e    message event
   * @param user user
   * @param item interacting item
   * @param meta item meta
   */
  public static void setLore(AsyncPlayerChatEvent e, Player user,
                             ItemStack item, ItemMeta meta) {
    meta.setLore(List.of(e.getMessage().split(",, ")));
    item.setItemMeta(meta);
    user.sendMessage(Success.SET_LORE.message);
    returnToMainMenu(user, item);
  }

  /**
   * Clears the lore.
   *
   * @param user user
   * @param item interacting item
   * @param meta item meta
   */
  public static void clearLore(Player user, ItemStack item, ItemMeta meta) {
    meta.setLore(new ArrayList<>());
    item.setItemMeta(meta);
    user.sendMessage(Success.CLEAR_LORE.message);
    returnToMainMenu(user, item);
  }

  /**
   * Adds a line of lore.
   *
   * @param e    message event
   * @param user user
   * @param item interacting item
   * @param meta item meta
   */
  public static void addLore(AsyncPlayerChatEvent e, Player user,
                             ItemStack item, ItemMeta meta) {
    if (meta.hasLore()) {
      List<String> lore = meta.getLore();
      lore.add(e.getMessage());
      meta.setLore(lore);
    } else {
      meta.setLore(List.of(e.getMessage()));
    }
    item.setItemMeta(meta);
    user.sendMessage(Success.ADD_LORE.message);
    returnToMainMenu(user, item);
  }

  /**
   * Edits a line of lore.
   *
   * @param e    message event
   * @param user user
   * @param item interacting item
   * @param meta item meta
   * @throws NumberFormatException     not a number
   * @throws IndexOutOfBoundsException invalid index
   */
  public static void editLore(AsyncPlayerChatEvent e, Player user,
                              ItemStack item, ItemMeta meta) {
    String[] input = e.getMessage().split(" ", 2);
    try {
      List<String> lore = meta.getLore();
      lore.set(Integer.parseInt(input[0]) - 1, input[1]);
      meta.setLore(lore);
      item.setItemMeta(meta);
      user.sendMessage(Success.EDIT_LORE.message);
    } catch (NumberFormatException ex) {
      user.sendMessage(Failure.INVALID_LINE.message);
    } catch (IndexOutOfBoundsException ex) {
      user.sendMessage(Failure.NONEXISTENT_LINE.message);
    }
    returnToMainMenu(user, item);
  }

  /**
   * Removes a line of lore.
   *
   * @param e    message event
   * @param user user
   * @param item interacting item
   * @param meta item meta
   * @throws NumberFormatException     not a number
   * @throws IndexOutOfBoundsException invalid index
   */
  public static void removeLore(AsyncPlayerChatEvent e, Player user,
                                ItemStack item, ItemMeta meta) {
    try {
      List<String> lore = meta.getLore();
      lore.remove(Integer.parseInt(e.getMessage()) - 1);
      meta.setLore(lore);
      item.setItemMeta(meta);
      user.sendMessage(Success.REMOVE_LORE.message);
    } catch (NumberFormatException ex) {
      user.sendMessage(Failure.INVALID_LINE.message);
    } catch (IndexOutOfBoundsException ex) {
      user.sendMessage(Failure.NONEXISTENT_LINE.message);
    }
    returnToMainMenu(user, item);
  }

  /**
   * Returns to the editor menu.
   *
   * @param user user
   * @param item interacting item
   */
  private static void returnToMainMenu(Player user, ItemStack item) {
    user.removeMetadata(PluginPlayerMeta.Namespace.MESSAGE.namespace, Plugin.getInstance());
    Bukkit.getScheduler().runTask(Plugin.getInstance(),
        () -> {
          user.openInventory(ItemEditorInventory.openMainMenu(user, item));
          user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
              new FixedMetadataValue(Plugin.getInstance(),
                  InventoryListener.Inventory.ITEMEDITOR_COSMETICS.inventory));
        });
  }

  /**
   * Sets or removes an item's attribute modifier.
   *
   * @param e    message event
   * @param user user
   * @param item interacting item
   * @throws NumberFormatException not a number
   */
  public static void setAttribute(AsyncPlayerChatEvent e, Player user, ItemStack item) {
    ItemMeta meta = PluginData.itemEditorData.getEditedItemMap().get(user).getItemMeta();
    String type = user.getMetadata(PluginPlayerMeta.Namespace.TYPE.namespace).get(0).asString();
    if (!type.contains("aethel.")) {
      setMinecraftAttribute(e, user, item, meta, type);
    } else {
      setAethelAttribute(e, user, item, meta, type);
    }
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> returnToAttributesMenu(user));
  }

  /**
   * Sets or removes an item's enchant.
   *
   * @param e    message event
   * @param user user
   * @param item interacting item
   */
  public static void setEnchant(AsyncPlayerChatEvent e, Player user, ItemStack item) {
    NamespacedKey enchant = NamespacedKey.minecraft(user.getMetadata(
        PluginPlayerMeta.Namespace.TYPE.namespace).get(0).asString());

    if (!e.getMessage().equals("0")) {
      setEnchantLevel(e, user, item, enchant);
    } else {
      item.removeEnchantment(Enchantment.getByKey(enchant));
      user.sendMessage(
          ChatColor.RED + "[Removed " + TextFormatter.capitalizePhrase(enchant.getKey()) + "]");
    }
    Bukkit.getScheduler().runTask(Plugin.getInstance(),
        () -> ItemEditorAction.openEnchantsMenu(user));
  }

  /**
   * Sets or removes an item's Aethel tag.
   *
   * @param e    message event
   * @param user user
   * @param item interacting item
   * @param meta item meta
   */
  public static void setTag(AsyncPlayerChatEvent e, Player user,
                            ItemStack item, ItemMeta meta) {
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
    String tagType = user.getMetadata(PluginPlayerMeta.Namespace.TYPE.namespace).get(0).asString();
    NamespacedKey aethelTagKey = new NamespacedKey(Plugin.getInstance(), "aethel." + tagType);

    if (!e.getMessage().equals("-")) {
      dataContainer.set(aethelTagKey, PersistentDataType.STRING, e.getMessage());
      user.sendMessage(ChatColor.GREEN + "[Set " + tagType + "]");
    } else {
      dataContainer.remove(aethelTagKey);
      user.sendMessage(ChatColor.RED + "[Removed " + tagType + "]");
    }
    item.setItemMeta(meta);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () ->
        ItemEditorAction.openTagsMenu(user));
  }

  /**
   * Sets a Minecraft attribute.
   *
   * @param e    message event
   * @param user user
   * @param item interacting item
   * @param meta item meta
   * @param type attribute derived from inventory click
   * @throws NumberFormatException not a number
   */
  private static void setMinecraftAttribute(AsyncPlayerChatEvent e,
                                            Player user, ItemStack item,
                                            ItemMeta meta, String type) {
    Attribute attribute = Attribute.valueOf(type);
    EquipmentSlot equipmentSlot = EquipmentSlot.valueOf(
        user.getMetadata(PluginPlayerMeta.Namespace.SLOT.namespace).get(0).asString().toUpperCase());

    try {
      AttributeModifier attributeModifier = new AttributeModifier(
          UUID.randomUUID(), "attribute",
          Double.parseDouble(e.getMessage()),
          AttributeModifier.Operation.ADD_NUMBER,
          equipmentSlot);

      if (!e.getMessage().equals("0")) {
        setMinecraftAttributeModifier(user, meta, attribute, attributeModifier, equipmentSlot);
      } else {
        removeMinecraftAttributeModifier(user, meta, attribute, equipmentSlot);
      }
      item.setItemMeta(meta);
    } catch (NumberFormatException ex) {
      user.sendMessage(Failure.INVALID_VALUE.message);
    }
  }

  /**
   * Sets an Aethel attribute.
   *
   * @param e    message event
   * @param user user
   * @param item interacting item
   * @param meta item meta
   * @param type attribute derived from inventory click
   * @throws NumberFormatException not a number
   */
  private static void setAethelAttribute(AsyncPlayerChatEvent e,
                                         Player user, ItemStack item,
                                         ItemMeta meta, String type) {
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
    NamespacedKey attributesKey = PluginNamespacedKey.AETHEL_ATTRIBUTE_LIST.namespacedKey;

    String equipmentSlot = user.getMetadata(PluginPlayerMeta.Namespace.SLOT.namespace).get(0).asString();
    String attributeName = type + "." + equipmentSlot;
    NamespacedKey attributeKey = new NamespacedKey(Plugin.getInstance(), attributeName);

    // Remove "aethel.attribute."
    attributeName = attributeName.substring(17);

    try {
      String attributeValue = String.valueOf(Double.parseDouble(e.getMessage()));
      if (!e.getMessage().equals("0")) {
        setAethelAttributeModifier(user, type, dataContainer,
            attributesKey, attributeName, attributeKey, attributeValue);
      } else {
        removeAethelAttributeModifier(user, type, dataContainer,
            attributesKey, attributeName, attributeKey);
      }
      item.setItemMeta(meta);
    } catch (NumberFormatException ex) {
      user.sendMessage(Failure.INVALID_VALUE.message);
    }
  }

  /**
   * Sets an item's attribute modifier based on the equipment slot mode.
   *
   * @param user              user
   * @param meta              item's meta
   * @param attribute         attribute
   * @param attributeModifier attribute modifier
   */
  private static void setMinecraftAttributeModifier(Player user, ItemMeta meta,
                                                    Attribute attribute, AttributeModifier attributeModifier,
                                                    EquipmentSlot equipmentSlot) {
    removeExistingAttributeModifiers(meta, attribute, equipmentSlot);
    meta.addAttributeModifier(attribute, attributeModifier);
    user.sendMessage(
        ChatColor.GREEN + "[Set " +
            TextFormatter.capitalizePhrase(attribute.getKey().getKey(), ".").substring(8) + "]");
  }

  /**
   * Removes an item's attribute modifier based on the equipment slot mode.
   *
   * @param user          user
   * @param meta          item's meta
   * @param attribute     attribute
   * @param equipmentSlot equipment slot
   */
  private static void removeMinecraftAttributeModifier(Player user, ItemMeta meta,
                                                       Attribute attribute, EquipmentSlot equipmentSlot) {
    removeExistingAttributeModifiers(meta, attribute, equipmentSlot);
    user.sendMessage(ChatColor.RED + "[Removed " +
        TextFormatter.capitalizePhrase(attribute.getKey().getKey(), ".").substring(8) + "]");
  }

  /**
   * Sets an item's Aethel attribute modifier based on the equipment slot mode.
   *
   * @param user           user
   * @param type           attribute derived from inventory click
   * @param dataContainer  item's persistent tags
   * @param attributesKey  attributes list key
   * @param attributeName  attribute name
   * @param attributeKey   attribute key
   * @param attributeValue attribute value
   */
  private static void setAethelAttributeModifier(Player user, String type,
                                                 PersistentDataContainer dataContainer,
                                                 NamespacedKey attributesKey,
                                                 String attributeName, NamespacedKey attributeKey,
                                                 String attributeValue) {
    if (dataContainer.has(attributesKey, PersistentDataType.STRING)) {
      List<String> attributes = new ArrayList<>(
          List.of(dataContainer.get(attributesKey, PersistentDataType.STRING).split(" ")));

      StringBuilder newAttributes = new StringBuilder();
      for (String attribute : attributes) {
        if (!attribute.equals(attributeName)) {
          newAttributes.append(attribute).append(" ");
        }
      }

      dataContainer.set(attributesKey, PersistentDataType.STRING, newAttributes + attributeName);
    } else {
      dataContainer.set(attributesKey, PersistentDataType.STRING, attributeName);
    }

    dataContainer.set(attributeKey, PersistentDataType.DOUBLE, Double.parseDouble(attributeValue));
    user.sendMessage(ChatColor.GREEN +
        "[Set " + TextFormatter.capitalizePhrase(type.substring(17)) + "]");
  }

  /**
   * Removes an item's Aethel attribute modifier based on the equipment slot mode.
   *
   * @param user          user
   * @param type          attribute derived from inventory click
   * @param dataContainer item's persistent tags
   * @param attributesKey attributes list key
   * @param attributeName attribute name
   * @param attributeKey  attribute key
   */
  private static void removeAethelAttributeModifier(Player user, String type,
                                                    PersistentDataContainer dataContainer,
                                                    NamespacedKey attributesKey,
                                                    String attributeName, NamespacedKey attributeKey) {
    List<String> attributes = new ArrayList<>(
        List.of(dataContainer.get(attributesKey, PersistentDataType.STRING).split(" ")));

    StringBuilder newAttributes = new StringBuilder();
    for (String attribute : attributes) {
      if (!attribute.equals(attributeName)) {
        newAttributes.append(attribute).append(" ");
      }
    }

    if (!newAttributes.isEmpty()) {
      dataContainer.set(attributesKey, PersistentDataType.STRING, newAttributes.toString().trim());
    } else {
      dataContainer.remove(attributesKey);
    }
    dataContainer.remove(attributeKey);
    user.sendMessage(ChatColor.RED +
        "[Removed " + TextFormatter.capitalizePhrase(type.substring(17)) + "]");
  }

  /**
   * Sets an item's enchant level.
   *
   * @param e       message event
   * @param user    user
   * @param item    interacting item
   * @param enchant enchant type
   * @throws NumberFormatException not a number
   */
  private static void setEnchantLevel(AsyncPlayerChatEvent e, Player user,
                                      ItemStack item, NamespacedKey enchant) {
    try {
      int level = Integer.parseInt(e.getMessage());
      if (level > 0 && level < 32768) {
        item.addUnsafeEnchantment(Enchantment.getByKey(enchant), level);
        user.sendMessage(ChatColor.GREEN + "[Set " +
            TextFormatter.capitalizePhrase(enchant.getKey()) + "]");
      } else {
        user.sendMessage(Failure.INVALID_ENCHANT_LEVEL.message);
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Failure.INVALID_VALUE.message);
    }
  }

  /**
   * Removes existing attribute modifiers in the same slot.
   *
   * @param meta          item meta
   * @param attribute     attribute
   * @param equipmentSlot equipment slot
   */
  private static void removeExistingAttributeModifiers(ItemMeta meta, Attribute attribute,
                                                       EquipmentSlot equipmentSlot) {
    if (meta.getAttributeModifiers() != null) {
      for (AttributeModifier existingAttributeModifier : meta.getAttributeModifiers().get(attribute)) {
        if (existingAttributeModifier.getSlot().equals(equipmentSlot)) {
          meta.removeAttributeModifier(attribute, existingAttributeModifier);
        }
      }
    }
  }

  /**
   * Opens a ItemEditorAttributes inventory.
   *
   * @param user user
   */
  private static void returnToAttributesMenu(Player user) {
    user.removeMetadata(PluginPlayerMeta.Namespace.MESSAGE.namespace, Plugin.getInstance());

    user.openInventory(ItemEditorAttributes.
        openAttributesMenu(user,
            user.getMetadata(PluginPlayerMeta.Namespace.SLOT.namespace).get(0).asString()));
    user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(),
            InventoryListener.Inventory.ITEMEDITOR_ATTRIBUTES.inventory));
  }

  private enum Success {
    NAME_ITEM(ChatColor.GREEN + "[Named Item] "),
    SET_CUSTOMMODELDATA(ChatColor.GREEN + "[Set Custom Model Data] "),
    SET_LORE(ChatColor.GREEN + "[Set Lore]"),
    CLEAR_LORE(ChatColor.GREEN + "[Cleared Lore]"),
    ADD_LORE(ChatColor.GREEN + "[Added Lore]"),
    EDIT_LORE(ChatColor.GREEN + "[Edited Lore]"),
    REMOVE_LORE(ChatColor.GREEN + "[Removed Lore]");

    public final String message;

    Success(String message) {
      this.message = message;
    }
  }

  private enum Failure {
    INVALID_CUSTOMMODELDATA(ChatColor.RED + "Invalid custom model data."),
    INVALID_LINE(ChatColor.RED + "Invalid line number."),
    NONEXISTENT_LINE(ChatColor.RED + "Line does not exist."),
    INVALID_VALUE(ChatColor.RED + "Invalid value."),
    INVALID_ENCHANT_LEVEL(ChatColor.RED + "Specify a level between 0 - 32767.");

    public final String message;

    Failure(String message) {
      this.message = message;
    }
  }
}
