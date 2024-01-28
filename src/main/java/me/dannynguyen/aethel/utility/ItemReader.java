package me.dannynguyen.aethel.utility;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * ItemReader is a utility class that:
 * - reads metadata from ItemStacks
 * - decodes serialized ItemStacks
 *
 * @author Danny Nguyen
 * @version 1.7.11
 * @since 1.1.4
 */
public final class ItemReader {
  private ItemReader() {
  }

  /**
   * Returns either an item's display name or its material.
   *
   * @param item interacting item
   * @return effective item name
   */
  public static String readName(ItemStack item) {
    if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
      return item.getItemMeta().getDisplayName();
    } else {
      return TextFormatter.capitalizePhrase(item.getType().name());
    }
  }

  /**
   * Returns the item's Aethel tags.
   *
   * @param item interacting item
   * @return item's Aethel tags
   */
  public static String readAethelTags(ItemStack item) {
    PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();

    StringBuilder aethelTags = new StringBuilder();
    for (NamespacedKey key : dataContainer.getKeys()) {
      String keyName = key.getKey();
      if (keyName.startsWith("aethel.")) {
        aethelTags.append(ChatColor.AQUA).append(keyName.substring(7)).append(" ").append(ChatColor.WHITE).
            append(dataContainer.get(key, PersistentDataType.STRING)).append(" ");
      }
    }
    return aethelTags.toString();
  }

  /**
   * Deserializes an item.
   *
   * @param data serialized item string
   * @return item
   * @throws IOException            file not found
   * @throws ClassNotFoundException item could not be decoded
   */
  public static ItemStack decodeItem(String data) {
    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(data));
      BukkitObjectInputStream bois = new BukkitObjectInputStream(bais);
      return (ItemStack) bois.readObject();
    } catch (IOException | ClassNotFoundException ex) {
      return null;
    }
  }
}
