package me.dannynguyen.aethel.utility;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

/**
 * Creates and serializes ItemStacks with metadata.
 *
 * @author Danny Nguyen
 * @version 1.13.8
 * @since 1.1.5
 */
public class ItemCreator {
  /**
   * Utility methods only.
   */
  private ItemCreator() {
  }

  /**
   * Creates a named item.
   *
   * @param material item material
   * @param name     item name
   * @return named item
   */
  public static ItemStack createItem(@NotNull Material material, @NotNull String name) {
    ItemStack item = new ItemStack(Objects.requireNonNull(material, "Null material"), 1);
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(Objects.requireNonNull(name, "Null name"));
    item.setItemMeta(meta);
    return item;
  }

  /**
   * Creates a named item with lore.
   *
   * @param material item material
   * @param name     item name
   * @param lore     item lore
   * @return named item with lore
   */
  public static ItemStack createItem(@NotNull Material material, @NotNull String name, @NotNull List<String> lore) {
    ItemStack item = new ItemStack(Objects.requireNonNull(material, "Null material"), 1);
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(Objects.requireNonNull(name, "Null name"));
    meta.setLore(Objects.requireNonNull(lore, "Null lore"));
    item.setItemMeta(meta);
    return item;
  }

  /**
   * Creates a named item with lore and an item flag.
   *
   * @param material item material
   * @param name     item name
   * @param lore     item lore
   * @param itemFlag item flag
   * @return named item with an item flag
   */
  public static ItemStack createItem(@NotNull Material material, @NotNull String name,
                                     @NotNull List<String> lore, @NotNull ItemFlag itemFlag) {
    ItemStack item = new ItemStack(Objects.requireNonNull(material, "Null material"), 1);
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(Objects.requireNonNull(name, "Null name"));
    meta.setLore(Objects.requireNonNull(lore, "Null lore"));
    meta.addItemFlags(Objects.requireNonNull(itemFlag, "Null item flag"));
    item.setItemMeta(meta);
    return item;
  }

  /**
   * Creates a named item with an item flag.
   *
   * @param material item material
   * @param name     item name
   * @param itemFlag item flag
   * @return named item with an item flag disabled
   */
  public static ItemStack createItem(@NotNull Material material, @NotNull String name, @NotNull ItemFlag itemFlag) {
    ItemStack item = new ItemStack(Objects.requireNonNull(material, "Null material"), 1);
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(Objects.requireNonNull(name, "Null name"));
    meta.addItemFlags(Objects.requireNonNull(itemFlag, "Null item flag"));
    item.setItemMeta(meta);
    return item;
  }

  /**
   * Creates a named player head.
   *
   * @param player interacting player
   * @return named player head
   */
  public static ItemStack createPlayerHead(@NotNull Player player) {
    ItemStack item = new ItemStack(Material.PLAYER_HEAD);
    SkullMeta meta = (SkullMeta) item.getItemMeta();
    meta.setOwningPlayer(Objects.requireNonNull(player, "Null player"));
    meta.setDisplayName(ChatColor.DARK_PURPLE + player.getName());
    item.setItemMeta(meta);
    return item;
  }

  /**
   * Creates a named player head with lore.
   *
   * @param player interacting player
   * @param lore   item lore
   * @return named player head with lore
   */
  public static ItemStack createPlayerHead(@NotNull Player player, @NotNull List<String> lore) {
    ItemStack item = new ItemStack(Material.PLAYER_HEAD);
    SkullMeta meta = (SkullMeta) item.getItemMeta();
    meta.setOwningPlayer(Objects.requireNonNull(player, "Null player"));
    meta.setDisplayName(ChatColor.DARK_PURPLE + player.getName());
    meta.setLore(Objects.requireNonNull(lore, "Null lore"));
    item.setItemMeta(meta);
    return item;
  }

  /**
   * Creates a named player head from loaded textures.
   *
   * @param head interacting player head
   * @param name item name
   * @return named player head texture
   */
  public static ItemStack createPluginPlayerHead(@NotNull ItemStack head, @NotNull String name) {
    ItemStack item = Objects.requireNonNull(head, "Null head").clone();
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(Objects.requireNonNull(name, "Null name"));
    item.setItemMeta(meta);
    return item;
  }

  /**
   * Creates a named player head from loaded textures with lore.
   *
   * @param head interacting player head
   * @param name item name
   * @param lore item lore
   * @return named player head texture with lore
   */
  public static ItemStack createPluginPlayerHead(@NotNull ItemStack head, @NotNull String name, @NotNull List<String> lore) {
    ItemStack item = Objects.requireNonNull(head, "Null head").clone();
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(Objects.requireNonNull(name, "Null name"));
    meta.setLore(Objects.requireNonNull(lore, "Null lore"));
    item.setItemMeta(meta);
    return item;
  }

  /**
   * Encodes an item into bytes.
   *
   * @param item item to encode
   * @return encoded item string
   */
  public static String encodeItem(@NotNull ItemStack item) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      BukkitObjectOutputStream boos = new BukkitObjectOutputStream(baos);
      boos.writeObject(Objects.requireNonNull(item, "Null item"));
      boos.flush();
      return Base64.getEncoder().encodeToString(baos.toByteArray());
    } catch (IOException ex) {
      return null;
    }
  }
}
