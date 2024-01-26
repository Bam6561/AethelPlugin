package me.dannynguyen.aethel.listeners.message.itemeditor;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.formatters.TextFormatter;
import me.dannynguyen.aethel.inventories.itemeditor.ItemEditorAttributes;
import me.dannynguyen.aethel.listeners.inventory.itemeditor.utility.ItemEditorInventoryMenuAction;
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
import java.util.Arrays;
import java.util.UUID;

/**
 * ItemEditorMessageFunctional is a utility class that edits an item's gameplay-related metadata.
 *
 * @author Danny Nguyen
 * @version 1.7.5
 * @since 1.7.0
 */
public class ItemEditorMessageFunctional {
  /**
   * Sets or removes an item's attribute modifier.
   *
   * @param e      message event
   * @param player interacting player
   * @param item   interacting item
   * @throws NumberFormatException not a number
   */
  public static void setAttribute(AsyncPlayerChatEvent e, Player player, ItemStack item) {
    ItemMeta meta = AethelResources.itemEditorData.getEditedItemMap().get(player).getItemMeta();
    String type = player.getMetadata("type").get(0).asString();
    if (!type.contains("aethel.")) {
      setMinecraftAttribute(e, player, item, meta, type);
    } else {
      setAethelAttribute(e, player, item, meta, type);
    }
    Bukkit.getScheduler().runTask(AethelPlugin.getInstance(), () -> returnToAttributesMenu(player));
  }

  /**
   * Sets or removes an item's enchant.
   *
   * @param e      message event
   * @param player interacting player
   * @param item   interacting item
   */
  public static void setEnchant(AsyncPlayerChatEvent e, Player player, ItemStack item) {
    NamespacedKey enchant = NamespacedKey.minecraft(player.getMetadata("type").get(0).asString());

    if (!e.getMessage().equals("0")) {
      setEnchantLevel(e, player, item, enchant);
    } else {
      item.removeEnchantment(Enchantment.getByKey(enchant));
      player.sendMessage(
          ChatColor.RED + "[Removed " + TextFormatter.capitalizeProperly(enchant.getKey()) + "]");
    }
    Bukkit.getScheduler().runTask(AethelPlugin.getInstance(),
        () -> ItemEditorInventoryMenuAction.openEnchantsMenu(player));
  }

  /**
   * Sets or removes an item's Aethel tag.
   *
   * @param e      message event
   * @param player interacting player
   * @param item   interacting item
   * @param meta   item meta
   */
  public static void setTag(AsyncPlayerChatEvent e, Player player,
                            ItemStack item, ItemMeta meta) {
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
    String tagType = player.getMetadata("type").get(0).asString();
    NamespacedKey aethelTagKey = new NamespacedKey(AethelPlugin.getInstance(), "aethel." + tagType);

    if (!e.getMessage().equals("-")) {
      dataContainer.set(aethelTagKey, PersistentDataType.STRING, e.getMessage());
      player.sendMessage(ChatColor.GREEN + "[Set " + tagType + "]");
    } else {
      dataContainer.remove(aethelTagKey);
      player.sendMessage(ChatColor.RED + "[Removed " + tagType + "]");
    }
    item.setItemMeta(meta);
    Bukkit.getScheduler().runTask(AethelPlugin.getInstance(), () ->
        ItemEditorInventoryMenuAction.openTagsMenu(player));
  }

  /**
   * Sets a Minecraft attribute.
   *
   * @param e      message event
   * @param player interacting player
   * @param item   interacting item
   * @param meta   item meta
   * @param type   attribute derived from inventory click
   * @throws NumberFormatException not a number
   */
  private static void setMinecraftAttribute(AsyncPlayerChatEvent e,
                                            Player player, ItemStack item,
                                            ItemMeta meta, String type) {
    Attribute attribute = Attribute.valueOf(type);
    EquipmentSlot equipmentSlot = EquipmentSlot.valueOf(
        player.getMetadata("slot").get(0).asString().toUpperCase());

    try {
      AttributeModifier attributeModifier = new AttributeModifier(
          UUID.randomUUID(), "attribute",
          Double.parseDouble(e.getMessage()),
          AttributeModifier.Operation.ADD_NUMBER,
          equipmentSlot);

      if (!e.getMessage().equals("0")) {
        setMinecraftAttributeModifier(player, item, meta, attribute, attributeModifier, equipmentSlot);
      } else {
        removeMinecraftAttributeModifier(player, item, meta, attribute, equipmentSlot);
      }
      item.setItemMeta(meta);
    } catch (NumberFormatException ex) {
      player.sendMessage(ChatColor.RED + "Invalid value.");
    }
  }

  /**
   * Sets an Aethel attribute.
   *
   * @param e      message event
   * @param player interacting player
   * @param item   interacting item
   * @param meta   item meta
   * @param type   attribute derived from inventory click
   * @throws NumberFormatException not a number
   */
  private static void setAethelAttribute(AsyncPlayerChatEvent e,
                                         Player player, ItemStack item,
                                         ItemMeta meta, String type) {
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
    NamespacedKey attributesKey =
        new NamespacedKey(AethelPlugin.getInstance(), "aethel.attribute.list");

    String equipmentSlot = player.getMetadata("slot").get(0).asString();
    String attributeName = type + "." + equipmentSlot;
    NamespacedKey attributeKey = new NamespacedKey(AethelPlugin.getInstance(), attributeName);

    // Remove "aethel.attribute."
    attributeName = attributeName.substring(17);

    try {
      String attributeValue = String.valueOf(Double.parseDouble(e.getMessage()));
      if (!e.getMessage().equals("0")) {
        setAethelAttributeModifier(player, type, dataContainer,
            attributesKey, attributeName, attributeKey, attributeValue);
      } else {
        removeAethelAttributeModifier(player, type, dataContainer,
            attributesKey, attributeName, attributeKey);
      }
      item.setItemMeta(meta);
    } catch (NumberFormatException ex) {
      player.sendMessage(ChatColor.RED + "Invalid value.");
    }
  }

  /**
   * Sets an item's attribute modifier based on the equipment slot mode.
   *
   * @param player            interacting player
   * @param item              interacting item
   * @param meta              item's meta
   * @param attribute         attribute
   * @param attributeModifier attribute modifier
   */
  private static void setMinecraftAttributeModifier(Player player, ItemStack item, ItemMeta meta,
                                                    Attribute attribute, AttributeModifier attributeModifier,
                                                    EquipmentSlot equipmentSlot) {
    removeExistingAttributeModifiers(meta, attribute, equipmentSlot);
    meta.addAttributeModifier(attribute, attributeModifier);
    player.sendMessage(
        ChatColor.GREEN + "[Set " +
            TextFormatter.capitalizeProperly(attribute.getKey().getKey(), ".").substring(8) + "]");
  }

  /**
   * Removes an item's attribute modifier based on the equipment slot mode.
   *
   * @param player        interacting player
   * @param item          interacting item
   * @param meta          item's meta
   * @param attribute     attribute
   * @param equipmentSlot equipment slot
   */
  private static void removeMinecraftAttributeModifier(Player player, ItemStack item, ItemMeta meta,
                                                       Attribute attribute, EquipmentSlot equipmentSlot) {
    removeExistingAttributeModifiers(meta, attribute, equipmentSlot);
    player.sendMessage(ChatColor.RED + "[Removed " +
        TextFormatter.capitalizeProperly(attribute.getKey().getKey(), ".").substring(8) + "]");
  }

  /**
   * Sets an item's Aethel attribute modifier based on the equipment slot mode.
   *
   * @param player         interacting player
   * @param type           attribute derived from inventory click
   * @param dataContainer  item's persistent tags
   * @param attributesKey  attributes list key
   * @param attributeName  attribute name
   * @param attributeKey   attribute key
   * @param attributeValue attribute value
   */
  private static void setAethelAttributeModifier(Player player, String type,
                                                 PersistentDataContainer dataContainer,
                                                 NamespacedKey attributesKey,
                                                 String attributeName, NamespacedKey attributeKey,
                                                 String attributeValue) {
    if (dataContainer.has(attributesKey, PersistentDataType.STRING)) {
      ArrayList<String> attributes = new ArrayList<>(
          Arrays.asList(dataContainer.get(attributesKey, PersistentDataType.STRING).split(" ")));

      StringBuilder newAttributes = new StringBuilder();
      for (String attribute : attributes) {
        if (!attribute.equals(attributeName)) {
          newAttributes.append(attribute + " ");
        }
      }

      dataContainer.set(attributesKey, PersistentDataType.STRING, newAttributes + attributeName);
    } else {
      dataContainer.set(attributesKey, PersistentDataType.STRING, attributeName);
    }

    dataContainer.set(attributeKey, PersistentDataType.STRING, attributeValue);
    player.sendMessage(ChatColor.GREEN +
        "[Set " + TextFormatter.capitalizeProperly(type.substring(17)) + "]");
  }

  /**
   * Removes an item's Aethel attribute modifier based on the equipment slot mode.
   *
   * @param player        interacting player
   * @param type          attribute derived from inventory click
   * @param dataContainer item's persistent tags
   * @param attributesKey attributes list key
   * @param attributeName attribute name
   * @param attributeKey  attribute key
   */
  private static void removeAethelAttributeModifier(Player player, String type,
                                                    PersistentDataContainer dataContainer,
                                                    NamespacedKey attributesKey,
                                                    String attributeName, NamespacedKey attributeKey) {
    ArrayList<String> attributes = new ArrayList<>(
        Arrays.asList(dataContainer.get(attributesKey, PersistentDataType.STRING).split(" ")));

    StringBuilder newAttributes = new StringBuilder();
    for (String attribute : attributes) {
      if (!attribute.equals(attributeName)) {
        newAttributes.append(attribute + " ");
      }
    }

    if (!newAttributes.isEmpty()) {
      dataContainer.set(attributesKey, PersistentDataType.STRING, newAttributes.toString().trim());
    } else {
      dataContainer.remove(attributesKey);
    }
    dataContainer.remove(attributeKey);
    player.sendMessage(ChatColor.RED +
        "[Removed " + TextFormatter.capitalizeProperly(type.substring(17)) + "]");
  }

  /**
   * Sets an item's enchant level.
   *
   * @param e       message event
   * @param player  interacting player
   * @param item    interacting item
   * @param enchant enchant type
   * @throws NumberFormatException not a number
   */
  private static void setEnchantLevel(AsyncPlayerChatEvent e, Player player,
                                      ItemStack item, NamespacedKey enchant) {
    try {
      int level = Integer.parseInt(e.getMessage());
      if (level > 0 && level < 32768) {
        item.addUnsafeEnchantment(Enchantment.getByKey(enchant), level);
        player.sendMessage(
            ChatColor.GREEN + "[Set " + TextFormatter.capitalizeProperly(enchant.getKey()) + "]");
      } else {
        player.sendMessage(ChatColor.RED + "Specify a level between 0 - 32767.");
      }
    } catch (NumberFormatException ex) {
      player.sendMessage(ChatColor.RED + "Invalid value.");
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
   * @param player interacting player
   */
  private static void returnToAttributesMenu(Player player) {
    player.removeMetadata("message", AethelPlugin.getInstance());

    player.openInventory(ItemEditorAttributes.
        openAttributesMenu(player, player.getMetadata("slot").get(0).asString()));
    player.setMetadata("inventory",
        new FixedMetadataValue(AethelPlugin.getInstance(), "itemeditor.attributes"));
  }
}
