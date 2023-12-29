package me.dannynguyen.aethel.creators;

import me.dannynguyen.aethel.AethelPlugin;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * ItemCreator creates ItemStacks with metadata.
 *
 * @author Danny Nguyen
 * @version 1.2.2
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
  public ItemStack createItem(Material material, String displayName) {
    ItemStack item = new ItemStack(material, 1);
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(displayName);
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
  public ItemStack createPlayerHead(String headName, String displayName) {
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
}
