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

import java.util.UUID;

/**
 * ItemEditorEditCosmetic is a utility class that edits an item's gameplay-related metadata.
 *
 * @author Danny Nguyen
 * @version 1.7.2
 * @since 1.7.0
 */
public class ItemEditorMessageGameplay {
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
    String input = player.getMetadata("input").get(0).asString();
    if (!input.contains("aethel.")) {
      setMinecraftAttribute(e, player, item, meta, input);
    } else {
      setAethelAttribute(e, player, item, meta, input);
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
    NamespacedKey enchant = NamespacedKey.minecraft(player.getMetadata("input").get(0).asString());

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
    String editTag = player.getMetadata("input").get(0).asString();
    NamespacedKey aethelTagKey = new NamespacedKey(AethelPlugin.getInstance(), "aethel." + editTag);

    if (!e.getMessage().equals("-")) {
      dataContainer.set(aethelTagKey, PersistentDataType.STRING, e.getMessage());
      item.setItemMeta(meta);
      player.sendMessage(ChatColor.GREEN + "[Set " + editTag + "]");
    } else {
      dataContainer.remove(aethelTagKey);
      item.setItemMeta(meta);
      player.sendMessage(ChatColor.RED + "[Removed " + editTag + "]");
    }
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
   * @param input  attribute derived from inventory click
   * @throws NumberFormatException not a number
   */
  private static void setMinecraftAttribute(AsyncPlayerChatEvent e,
                                            Player player, ItemStack item,
                                            ItemMeta meta, String input) {
    Attribute attribute = Attribute.valueOf(input);
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
   * @param input  attribute derived from inventory click
   * @throws NumberFormatException not a number
   */
  private static void setAethelAttribute(AsyncPlayerChatEvent e,
                                         Player player, ItemStack item,
                                         ItemMeta meta, String input) {
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
    String equipmentSlot = player.getMetadata("slot").get(0).asString();
    NamespacedKey aethelAttribute =
        new NamespacedKey(AethelPlugin.getInstance(), input + "." + equipmentSlot);
    try {
      String attributeValue = String.valueOf(Double.parseDouble(e.getMessage()));
      if (!e.getMessage().equals("0")) {
        dataContainer.set(aethelAttribute, PersistentDataType.STRING, attributeValue);
        item.setItemMeta(meta);
        player.sendMessage(ChatColor.GREEN +
            "[Set " + TextFormatter.capitalizeProperly(input.substring(17)) + "]");
      } else {
        dataContainer.remove(aethelAttribute);
        item.setItemMeta(meta);
        player.sendMessage(ChatColor.RED +
            "[Removed " + TextFormatter.capitalizeProperly(input.substring(17)) + "]");
      }
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
    item.setItemMeta(meta);
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
    item.setItemMeta(meta);
    player.sendMessage(ChatColor.RED + "[Removed " +
        TextFormatter.capitalizeProperly(attribute.getKey().getKey(), ".").substring(8) + "]");
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
      for (AttributeModifier setAttributeModifier : meta.getAttributeModifiers().get(attribute)) {
        if (setAttributeModifier.getSlot().equals(equipmentSlot)) {
          meta.removeAttributeModifier(attribute, setAttributeModifier);
        }
      }
    }
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
