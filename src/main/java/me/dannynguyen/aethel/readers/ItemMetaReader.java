package me.dannynguyen.aethel.readers;

import me.dannynguyen.aethel.AethelPlugin;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

/**
 * ItemMetaReader reads metadata from ItemStacks.
 *
 * @author Danny Nguyen
 * @version 1.2.6
 * @since 1.1.4
 */
public class ItemMetaReader {
  /**
   * Returns either an item's renamed value or its material.
   *
   * @param item interacting item
   * @return effective item name
   */
  public String readItemName(ItemStack item) {
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
  public String readAethelTags(ItemStack item) {
    PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
    StringBuilder aethelTags = new StringBuilder();

    for (NamespacedKey dataKey : dataContainer.getKeys()) {
      String keyName = dataKey.getKey();
      if (keyName.startsWith("aethel.")) {
        NamespacedKey namespacedKey = new NamespacedKey(AethelPlugin.getInstance(), keyName);
        aethelTags.append(ChatColor.AQUA + "[" + keyName.substring(7) + "] " + ChatColor.WHITE
            + dataContainer.get(namespacedKey, PersistentDataType.STRING) + "\n");
      }
    }
    return aethelTags.toString();
  }
}
