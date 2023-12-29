package me.dannynguyen.aethel.data;

import me.dannynguyen.aethel.creators.ItemCreator;
import me.dannynguyen.aethel.objects.PlayerHead;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * PlayerHeadData contains information about player heads loaded in memory.
 *
 * @author Danny Nguyen
 * @version 1.2.1
 * @since 1.2.1
 */
public class PlayerHeadData {
  HashMap<String, ItemStack> headsMap = new HashMap<>();

  /**
   * Loads player heads into memory.
   */
  public void loadPlayerHeads() {
    for (PlayerHead head : PlayerHead.values()) {
      headsMap.put(head.getName(), new ItemCreator().createPlayerHead(head.getName(), head.getTextureData()));
    }
  }

  public HashMap<String, ItemStack> getHeadsMap() {
    return this.headsMap;
  }
}
