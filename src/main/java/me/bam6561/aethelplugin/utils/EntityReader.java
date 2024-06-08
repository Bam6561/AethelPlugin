package me.bam6561.aethelplugin.utils;

import me.bam6561.aethelplugin.Plugin;
import me.bam6561.aethelplugin.utils.item.ItemReader;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Reads entities with metadata.
 *
 * @author Danny Nguyen
 * @version 1.23.9
 * @since 1.22.20
 */
public class EntityReader {
  /**
   * Static methods only.
   */
  private EntityReader() {
  }

  /**
   * If the player is in developer mode or has the matching trinket material.
   *
   * @param player   interacting player
   * @param material required trinket material
   * @return if the player has the matching trinket material
   */
  public static boolean hasTrinket(@NotNull Player player, @NotNull Material material) {
    UUID uuid = player.getUniqueId();
    if (Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput().isDeveloper()) {
      return true;
    }
    PlayerInventory pInv = player.getInventory();
    ItemStack mainHand = pInv.getItemInMainHand();
    if (ItemReader.isNotNullOrAir(mainHand) && mainHand.getType() == material) {
      return true;
    }
    ItemStack offHand = pInv.getItemInOffHand();
    if (ItemReader.isNotNullOrAir(offHand) && offHand.getType() == material) {
      return true;
    }
    ItemStack[] jewelry = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getEquipment().getJewelry();
    if (ItemReader.isNotNullOrAir(jewelry[2]) && jewelry[2].getType() == material) {
      return true;
    }
    return false;
  }
}
