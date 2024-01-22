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
 * @version 1.7.0
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
    Attribute attribute = Attribute.valueOf(player.getMetadata("input").get(0).asString());
    EquipmentSlot equipmentSlot = EquipmentSlot.valueOf(
        player.getMetadata("slot").get(0).asString().toUpperCase());

    try {
      AttributeModifier attributeModifier = new AttributeModifier(
          UUID.randomUUID(), "attribute",
          Double.parseDouble(e.getMessage()),
          AttributeModifier.Operation.ADD_NUMBER,
          equipmentSlot);

      if (!e.getMessage().equals("0")) {
        setAttributeModifier(player, item, meta, attribute, attributeModifier);
      } else {
        removeAttributeModifier(player, item, meta, attribute, equipmentSlot);
      }
    } catch (NumberFormatException ex) {
      player.sendMessage(ChatColor.RED + "Invalid value.");
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
   * Sets an item's attribute modifier based on the equipment slot mode.
   *
   * @param player            interacting player
   * @param item              interacting item
   * @param meta              item's meta
   * @param attribute         attribute
   * @param attributeModifier attribute modifier
   */
  private static void setAttributeModifier(Player player, ItemStack item, ItemMeta meta,
                                           Attribute attribute, AttributeModifier attributeModifier) {
    meta.addAttributeModifier(attribute, attributeModifier);
    item.setItemMeta(meta);
    player.sendMessage(
        ChatColor.GREEN + "[Set " +
            TextFormatter.capitalizeProperly(attribute.getKey().getKey(), ".") + "]");
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
  private static void removeAttributeModifier(Player player, ItemStack item, ItemMeta meta,
                                              Attribute attribute, EquipmentSlot equipmentSlot) {
    for (AttributeModifier setAttributeModifier : meta.getAttributeModifiers().get(attribute)) {
      if (setAttributeModifier.getSlot().equals(equipmentSlot)) {
        meta.removeAttributeModifier(attribute, setAttributeModifier);
      }
    }
    item.setItemMeta(meta);
    player.sendMessage(ChatColor.RED +
        "[Removed " + TextFormatter.capitalizeProperly(attribute.getKey().getKey(), ".") + "]");
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
    player.openInventory(ItemEditorAttributes.
        openAttributesMenu(player, player.getMetadata("slot").get(0).asString()));
    player.setMetadata("inventory",
        new FixedMetadataValue(AethelPlugin.getInstance(), "itemeditor.attributes"));
  }
}
