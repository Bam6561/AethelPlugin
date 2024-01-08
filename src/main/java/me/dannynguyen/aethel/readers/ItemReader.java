package me.dannynguyen.aethel.readers;

import me.dannynguyen.aethel.AethelPlugin;
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
 * ItemReader:
 * - reads metadata from ItemStacks
 * - decodes serialized ItemStacks
 *
 * @author Danny Nguyen
 * @version 1.4.12
 * @since 1.1.4
 */
public class ItemReader {
  /**
   * Returns either an item's renamed value or its material.
   *
   * @param item interacting item
   * @return effective item name
   */
  public static String readItemName(ItemStack item) {
    if (item.getItemMeta().hasDisplayName()) {
      return item.getItemMeta().getDisplayName();
    } else {
      return item.getType().name();
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

    for (NamespacedKey dataKey : dataContainer.getKeys()) {
      String keyName = dataKey.getKey();
      if (keyName.startsWith("aethel.")) {
        NamespacedKey namespacedKey = new NamespacedKey(AethelPlugin.getInstance(), keyName);
        aethelTags.append(ChatColor.AQUA + keyName.substring(7) + " " + ChatColor.WHITE
            + dataContainer.get(namespacedKey, PersistentDataType.STRING) + " ");
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
      ItemStack item = (ItemStack) bois.readObject();
      return item;
    } catch (IOException | ClassNotFoundException ex) {
      return null;
    }
  }
}
