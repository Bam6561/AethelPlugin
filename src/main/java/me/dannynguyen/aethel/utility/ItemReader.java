package me.dannynguyen.aethel.utility;

import me.dannynguyen.aethel.systems.plugin.KeyHeader;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

/**
 * Reads and decodes ItemStacks with metadata.
 *
 * @author Danny Nguyen
 * @version 1.15.3
 * @since 1.1.4
 */
public class ItemReader {
  /**
   * Utility methods only.
   */
  private ItemReader() {
  }

  /**
   * Checks if the item is not null or air.
   *
   * @param item interacting item
   * @return item is not null or air
   */
  public static boolean isNotNullOrAir(@Nullable ItemStack item) {
    return item != null && item.getType() != Material.AIR;
  }

  /**
   * Returns either an item's display name or its material.
   *
   * @param item interacting item
   * @return effective item name
   */
  public static String readName(@NotNull ItemStack item) {
    if (Objects.requireNonNull(item, "Null item").hasItemMeta() && item.getItemMeta().hasDisplayName()) {
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
  public static String readAethelTags(@NotNull ItemStack item) {
    PersistentDataContainer dataContainer = Objects.requireNonNull(item, "Null item").getItemMeta().getPersistentDataContainer();
    StringBuilder aethelTags = new StringBuilder();
    for (NamespacedKey key : dataContainer.getKeys()) {
      String keyName = key.getKey();
      if (keyName.startsWith(KeyHeader.AETHEL.getHeader())) {
        keyName = keyName.substring(7);
        if (keyName.startsWith("attribute.")) {
          if (keyName.matches("attribute.list")) {
            aethelTags.append(ChatColor.AQUA).append(keyName).append(" ").append(ChatColor.WHITE).append(dataContainer.get(key, PersistentDataType.STRING)).append(" ");
          } else {
            aethelTags.append(ChatColor.AQUA).append(keyName).append(" ").append(ChatColor.WHITE).append(dataContainer.get(key, PersistentDataType.DOUBLE)).append(" ");
          }
        } else {
          aethelTags.append(ChatColor.AQUA).append(keyName).append(" ").append(ChatColor.WHITE).append(dataContainer.get(key, PersistentDataType.STRING)).append(" ");
        }
      }
    }
    return aethelTags.toString();
  }

  /**
   * Deserializes an ItemStack.
   *
   * @param data serialized item string
   * @return item
   */
  @Nullable
  public static ItemStack decodeItem(@NotNull String data) {
    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(Objects.requireNonNull(data, "Null data")));
      BukkitObjectInputStream bois = new BukkitObjectInputStream(bais);
      return (ItemStack) bois.readObject();
    } catch (IOException | ClassNotFoundException ex) {
      return null;
    }
  }
}
