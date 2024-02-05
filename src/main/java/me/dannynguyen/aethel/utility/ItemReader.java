package me.dannynguyen.aethel.utility;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
 * @version 1.9.4
 * @since 1.1.4
 */
public class ItemReader {
  private ItemReader() {
  }

  /**
   * Checks if the item is not null or air.
   *
   * @param item interacting item
   * @return item is not null or air
   */
  public static boolean isNotNullOrAir(ItemStack item) {
    return item != null && item.getType() != Material.AIR;
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
        if (!keyName.startsWith("aethel.attribute.")) {
          aethelTags.append(ChatColor.AQUA).append(keyName.substring(7)).append(" ").append(ChatColor.WHITE).
              append(dataContainer.get(key, PersistentDataType.STRING)).append(" ");
        } else {
          if (keyName.matches("aethel.attribute.list")) {
            aethelTags.append(ChatColor.AQUA).append(keyName.substring(7)).append(" ").append(ChatColor.WHITE).
                append(dataContainer.get(key, PersistentDataType.STRING)).append(" ");
          } else {
            aethelTags.append(ChatColor.AQUA).append(keyName.substring(7)).append(" ").append(ChatColor.WHITE).
                append(dataContainer.get(key, PersistentDataType.DOUBLE)).append(" ");
          }
        }
      }
    }
    return aethelTags.toString();
  }

  /**
   * Deserializes an item.
   *
   * @param data   serialized item string
   * @param action type of interaction
   * @return item
   * @throws IOException            file not found
   * @throws ClassNotFoundException item could not be decoded
   */
  public static ItemStack decodeItem(String data, String action) {
    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(data));
      BukkitObjectInputStream bois = new BukkitObjectInputStream(bais);
      return (ItemStack) bois.readObject();
    } catch (IOException | ClassNotFoundException ex) {
      switch (action) {
        case "aethelitems" -> Bukkit.getLogger().warning(
            Failure.INVALID_AETHEL_ITEM_FILE.message + data);
        case "forge" -> Bukkit.getLogger().warning(
            Failure.INVALID_FORGE_RECIPE_FILE.message + data);
        default -> Bukkit.getLogger().warning(
            Failure.INVALID_GENERIC_ITEM_DATA.message + data);
      }
      return null;
    }
  }

  private enum Failure {
    INVALID_GENERIC_ITEM_DATA("[Aethel] Invalid item data: "),
    INVALID_AETHEL_ITEM_FILE("[Aethel] Invalid item file: "),
    INVALID_FORGE_RECIPE_FILE("[Aethel] Invalid forge recipe file: ");

    public final String message;

    Failure(String message) {
      this.message = message;
    }
  }
}