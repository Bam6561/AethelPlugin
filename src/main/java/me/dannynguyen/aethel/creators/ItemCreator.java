package me.dannynguyen.aethel.creators;

import me.dannynguyen.aethel.AethelPlugin;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

/**
 * ItemCreator:
 * - creates ItemStacks with metadata
 * - serializes ItemStacks
 *
 * @author Danny Nguyen
 * @version 1.4.12
 * @since 1.1.5
 */
public class ItemCreator {
  /**
   * Creates a named item.
   *
   * @param material    item material
   * @param displayName item name
   * @return named item
   */
  public static ItemStack createItem(Material material, String displayName) {
    ItemStack item = new ItemStack(material, 1);
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(displayName);
    item.setItemMeta(meta);
    return item;
  }

  /**
   * Creates a named item with lore.
   *
   * @param material    item material
   * @param displayName item name
   * @param lore        item lore
   * @return named item with lore
   */
  public static ItemStack createItem(Material material, String displayName, List<String> lore) {
    ItemStack item = new ItemStack(material, 1);
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(displayName);
    meta.setLore(lore);
    item.setItemMeta(meta);
    return item;
  }

  /**
   * Creates a named player head from loaded textures.
   *
   * @param headName    player head name
   * @param displayName item name
   * @return named custom player head
   */
  public static ItemStack createPlayerHead(String headName, String displayName) {
    ItemStack item = AethelPlugin.getInstance().getResources().getPlayerHeadData().getHeadsMap().get(headName);
    if (item != null) {
      ItemMeta meta = item.getItemMeta();
      meta.setDisplayName(displayName);
      item.setItemMeta(meta);
      return item;
    } else {
      return null;
    }
  }

  /**
   * Creates a named player head from loaded textures with lore.
   *
   * @param headName    player head name
   * @param displayName item name
   * @param lore        item lore
   * @return named custom player head with lore
   */
  public static ItemStack createPlayerHead(String headName, String displayName, List<String> lore) {
    ItemStack item = AethelPlugin.getInstance().getResources().getPlayerHeadData().getHeadsMap().get(headName);
    if (item != null) {
      ItemMeta meta = item.getItemMeta();
      meta.setDisplayName(displayName);
      meta.setLore(lore);
      item.setItemMeta(meta);
      return item;
    } else {
      return null;
    }
  }

  /**
   * Encodes an item into bytes.
   *
   * @param item item to encode
   * @return encoded item string
   * @throws IOException item could not be encoded
   */
  public static String encodeItem(ItemStack item) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      BukkitObjectOutputStream boos = new BukkitObjectOutputStream(baos);
      boos.writeObject(item);
      boos.flush();
      return Base64.getEncoder().encodeToString(baos.toByteArray());
    } catch (IOException ex) {
      return null;
    }
  }
}
