package me.dannynguyen.aethel.utils;

import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.plugin.KeyHeader;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Reads entities with metadata.
 *
 * @author Danny Nguyen
 * @version 1.22.15
 * @since 1.22.15
 */
public class EntityReader {
  /**
   * Returns the entity's {@link Key Aethel tags}.
   *
   * @param entity interacting entity
   * @return entity's {@link Key Aethel tags}
   */
  @NotNull
  public static String readTags(@NotNull Entity entity) {
    PersistentDataContainer entityTags = Objects.requireNonNull(entity, "Null entity").getPersistentDataContainer();
    StringBuilder aethelTags = new StringBuilder();
    for (NamespacedKey key : entityTags.getKeys()) {
      String keyName = key.getKey();
      if (keyName.startsWith(KeyHeader.AETHEL.getHeader())) {
        keyName = keyName.substring(7);
        if (keyName.startsWith("attribute.")) {
          aethelTags.append(ChatColor.AQUA).append(keyName).append(" ").append(ChatColor.WHITE).append(entityTags.get(key, PersistentDataType.DOUBLE)).append(" ");
        } else if (keyName.startsWith("enchantment.")) {
          aethelTags.append(ChatColor.AQUA).append(keyName).append(" ").append(ChatColor.WHITE).append(entityTags.get(key, PersistentDataType.INTEGER)).append(" ");
        } else {
          aethelTags.append(ChatColor.AQUA).append(keyName).append(" ").append(ChatColor.WHITE).append(entityTags.get(key, PersistentDataType.STRING)).append(" ");
        }
      }
    }
    return aethelTags.toString();
  }
}
