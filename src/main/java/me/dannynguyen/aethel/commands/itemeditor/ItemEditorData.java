package me.dannynguyen.aethel.commands.itemeditor;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * ItemEditorData stores items currently being edited into memory.
 *
 * @author Danny Nguyen
 * @version 1.8.2
 * @since 1.6.7
 */
public class ItemEditorData {
  private final Map<Player, ItemStack> editedItemMap = new HashMap<>();

  public Map<Player, ItemStack> getEditedItemMap() {
    return this.editedItemMap;
  }
}
