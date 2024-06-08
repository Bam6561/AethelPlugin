package me.bam6561.aethelplugin.utils.item;

import me.bam6561.aethelplugin.utils.TextFormatter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
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
 * @version 1.23.9
 * @since 1.1.4
 */
public class ItemReader {
  /**
   * Utility methods only.
   */
  private ItemReader() {
  }

  /**
   * Checks if the item is null or air.
   *
   * @param item interacting item
   * @return item is null or air
   */
  public static boolean isNullOrAir(@Nullable ItemStack item) {
    return item == null || item.getType() == Material.AIR;
  }

  /**
   * Checks if the item is not null and not air.
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
  @NotNull
  public static String readName(@NotNull ItemStack item) {
    if (Objects.requireNonNull(item, "Null item").hasItemMeta() && item.getItemMeta().hasDisplayName()) {
      return item.getItemMeta().getDisplayName();
    } else {
      return TextFormatter.capitalizePhrase(item.getType().name());
    }
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
