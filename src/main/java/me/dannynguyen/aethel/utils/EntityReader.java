package me.dannynguyen.aethel.utils;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.plugin.KeyHeader;
import me.dannynguyen.aethel.utils.item.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * Reads entities with metadata.
 *
 * @author Danny Nguyen
 * @version 1.23.6
 * @since 1.22.20
 */
public class EntityReader {
  /**
   * Static methods only.
   */
  private EntityReader() {
  }

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
        if (keyName.startsWith("attribute.") || keyName.startsWith("rpg.health")) {
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

  /**
   * If the player is in developer mode or has the matching trinket material.
   *
   * @param player   interacting player
   * @param material required trinket material
   * @return if the player has the matching trinket material
   */
  public static boolean hasTrinket(@NotNull Player player, @NotNull Material material) {
    UUID uuid = player.getUniqueId();
    if (Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput().isDeveloper()) {
      return true;
    }
    PlayerInventory pInv = player.getInventory();
    ItemStack mainHand = pInv.getItemInMainHand();
    if (ItemReader.isNotNullOrAir(mainHand) && mainHand.getType() == material) {
      return true;
    }
    ItemStack offHand = pInv.getItemInOffHand();
    if (ItemReader.isNotNullOrAir(offHand) && offHand.getType() == material) {
      return true;
    }
    ItemStack[] jewelry = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getEquipment().getJewelry();
    if (ItemReader.isNotNullOrAir(jewelry[2]) && jewelry[2].getType() == material) {
      return true;
    }
    return false;
  }
}
