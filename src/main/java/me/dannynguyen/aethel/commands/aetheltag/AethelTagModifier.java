package me.dannynguyen.aethel.commands.aetheltag;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.systems.plugin.enums.PluginNamespacedKey;
import me.dannynguyen.aethel.systems.rpg.AethelAttribute;
import me.dannynguyen.aethel.systems.rpg.RpgEquipmentSlot;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a set or remove operation for an item's Aethel tag.
 *
 * @author Danny Nguyen
 * @version 1.14.0
 * @since 1.13.9
 */
class AethelTagModifier {
  /**
   * ItemStack being modified.
   */
  private final ItemStack item;

  /**
   * ItemStack's meta.
   */
  private final ItemMeta meta;

  /**
   * ItemStack's persistent data tags.
   */
  private final PersistentDataContainer dataContainer;

  /**
   * Associates an item with its tag to be modified.
   *
   * @param item interacting item
   */
  protected AethelTagModifier(@NotNull ItemStack item) {
    this.item = Objects.requireNonNull(item, "Null item");
    this.meta = item.getItemMeta();
    this.dataContainer = meta.getPersistentDataContainer();
  }

  /**
   * Removes the Aethel tag from the item.
   *
   * @param tag to be removed
   * @return if the tag was removed
   */
  protected boolean removeTag(String tag) {
    NamespacedKey tagKey = new NamespacedKey(Plugin.getInstance(), "aethel." + tag);
    if (dataContainer.has(tagKey, PersistentDataType.STRING) || dataContainer.has(tagKey, PersistentDataType.DOUBLE)) {
      dataContainer.remove(tagKey);
      if (tag.startsWith("attribute.")) {
        removeAttributeTag(tag);
      }
      item.setItemMeta(meta);
      return true;
    }
    return false;
  }

  /**
   * Sets the Aethel tag to the item.
   *
   * @param user  user
   * @param tag   tag to be set
   * @param value tag value
   */
  protected void setTag(Player user, String tag, String value) {
    if (!tag.startsWith("attribute.")) {
      dataContainer.set(new NamespacedKey(Plugin.getInstance(), "aethel." + tag), PersistentDataType.STRING, value);
      item.setItemMeta(meta);
      user.sendMessage(ChatColor.GREEN + "[Set Tag] " + ChatColor.AQUA + tag.toLowerCase() + " " + ChatColor.WHITE + value);
    } else {
      if (!tag.equals("attribute.list")) {
        try {
          readAttributeModifier(user, tag, Double.parseDouble(value));
        } catch (NumberFormatException ex) {
          user.sendMessage(ChatColor.RED + "Invalid attribute value.");
        }
      } else {
        user.sendMessage(ChatColor.RED + "Cannot set attribute.list directly.");
      }
    }
  }

  /**
   * Removes an item's attribute tag.
   *
   * @param tag tag to be removed
   */
  private void removeAttributeTag(String tag) {
    if (!tag.equals("attribute.list")) {
      NamespacedKey listKey = PluginNamespacedKey.ATTRIBUTE_LIST.getNamespacedKey();
      if (dataContainer.has(listKey, PersistentDataType.STRING)) {
        tag = tag.substring(10);
        List<String> attributes = new ArrayList<>(List.of(dataContainer.get(listKey, PersistentDataType.STRING).split(" ")));
        StringBuilder newAttributes = new StringBuilder();
        for (String attribute : attributes) {
          if (!attribute.equals(tag)) {
            newAttributes.append(attribute).append(" ");
          }
        }
        if (!newAttributes.isEmpty()) {
          dataContainer.set(listKey, PersistentDataType.STRING, newAttributes.toString().trim());
        } else {
          dataContainer.remove(listKey);
        }
      }
    } else {
      for (NamespacedKey key : dataContainer.getKeys()) {
        if (key.getKey().startsWith("aethel.attribute.")) {
          dataContainer.remove(key);
        }
      }
    }
  }

  /**
   * Checks whether the attribute modifier tag was formatted correctly before setting its tag and value.
   *
   * @param user  user
   * @param tag   tag to be set
   * @param value tag value
   */
  private void readAttributeModifier(Player user, String tag, Double value) {
    tag = tag.substring(10);
    String[] tagMeta = tag.split("\\.", 2);
    if (tagMeta.length == 2) {
      try {
        AethelAttribute.valueOf(tagMeta[0].toUpperCase());
        try {
          RpgEquipmentSlot.valueOf(tagMeta[1].toUpperCase());
          setAttributeTag(user, tag, value);
        } catch (IllegalArgumentException ex) {
          user.sendMessage(ChatColor.RED + "Equipment slot does not exist.");
        }
      } catch (IllegalArgumentException ex) {
        user.sendMessage(ChatColor.RED + "Aethel attribute does not exist.");
      }
    } else {
      user.sendMessage(ChatColor.RED + "Did not provide attribute and equipment slot.");
    }
  }

  /**
   * Sets an item's attribute tag.
   *
   * @param user  user
   * @param tag   tag to add
   * @param value tag value
   */
  private void setAttributeTag(Player user, String tag, Double value) {
    NamespacedKey tagKey = new NamespacedKey(Plugin.getInstance(), "aethel.attribute." + tag);
    NamespacedKey listKey = PluginNamespacedKey.ATTRIBUTE_LIST.getNamespacedKey();

    dataContainer.set(tagKey, PersistentDataType.DOUBLE, value);

    if (dataContainer.has(listKey, PersistentDataType.STRING)) {
      List<String> attributes = new ArrayList<>(List.of(dataContainer.get(listKey, PersistentDataType.STRING).split(" ")));
      StringBuilder newAttributes = new StringBuilder();
      for (String attribute : attributes) {
        if (!attribute.equals(tag)) {
          newAttributes.append(attribute).append(" ");
        }
      }
      dataContainer.set(listKey, PersistentDataType.STRING, newAttributes + tag);
    } else {
      dataContainer.set(listKey, PersistentDataType.STRING, tag);
    }
    item.setItemMeta(meta);
    user.sendMessage(ChatColor.GREEN + "[Set Tag] " + ChatColor.AQUA + tag.toLowerCase() + " " + ChatColor.WHITE + value);
  }
}
