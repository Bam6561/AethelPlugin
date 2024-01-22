package me.dannynguyen.aethel.data;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * ItemEditorData stores items currently being edited into memory.
 *
 * @author Danny Nguyen
 * @version 1.6.15
 * @since 1.6.7
 */
public class ItemEditorData {
  private final HashMap<Player, ItemStack> editedItemMap = new HashMap<>();
  private final ArrayList<String> aethelTags = new ArrayList<>(
      Arrays.asList("aitem_cat", "forge_cat", "forge_id"));

  public HashMap<Player, ItemStack> getEditedItemMap() {
    return this.editedItemMap;
  }

  public ArrayList<String> getAethelTags() {
    return this.aethelTags;
  }
}
