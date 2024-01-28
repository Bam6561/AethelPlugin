package me.dannynguyen.aethel.commands.itemeditor;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * ItemEditorData stores items currently being edited into memory.
 *
 * @author Danny Nguyen
 * @version 1.8.0
 * @since 1.6.7
 */
public class ItemEditorData {
  private final HashMap<Player, ItemStack> editedItemMap = new HashMap<>();

  public HashMap<Player, ItemStack> getEditedItemMap() {
    return this.editedItemMap;
  }
}
