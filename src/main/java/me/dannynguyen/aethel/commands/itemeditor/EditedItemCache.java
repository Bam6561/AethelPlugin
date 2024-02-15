package me.dannynguyen.aethel.commands.itemeditor;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents items currently being edited in memory.
 *
 * @author Danny Nguyen
 * @version 1.9.17
 * @since 1.6.7
 */
public class EditedItemCache {
  /**
   * Items currently being edited by what player.
   */
  private final Map<Player, ItemStack> editedItemMap = new HashMap<>();

  /**
   * Gets the player:item map.
   *
   * @return player:item map
   */
  protected Map<Player, ItemStack> getEditedItemMap() {
    return this.editedItemMap;
  }
}
