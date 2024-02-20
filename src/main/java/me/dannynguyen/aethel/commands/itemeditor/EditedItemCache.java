package me.dannynguyen.aethel.commands.itemeditor;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents items currently being edited in memory.
 *
 * @author Danny Nguyen
 * @version 1.12.0
 * @since 1.6.7
 */
public class EditedItemCache {
  /**
   * Items currently being edited by what player.
   */
  private final Map<UUID, ItemStack> editedItemMap = new HashMap<>();

  /**
   * Gets the player:item map.
   *
   * @return player:item map
   */
  protected Map<UUID, ItemStack> getEditedItemMap() {
    return this.editedItemMap;
  }
}
