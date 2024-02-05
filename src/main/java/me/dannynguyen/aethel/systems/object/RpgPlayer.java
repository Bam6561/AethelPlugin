package me.dannynguyen.aethel.systems.object;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * RpgPlayer is an object relating a player with their equipment and Aethel attribute values.
 *
 * @author Danny Nguyen
 * @version 1.9.5
 * @since 1.8.9
 */
public record RpgPlayer(Player player,
                        Map<String, Map<String, Double>> equipmentAttributes,
                        Map<String, Double> aethelAttributes,
                        ItemStack[] jewelrySlots) {

  public Player getPlayer() {
    return this.player;
  }

  public Map<String, Map<String, Double>> getEquipmentAttributes() {
    return this.equipmentAttributes;
  }

  public Map<String, Double> getAethelAttributes() {
    return this.aethelAttributes;
  }

  public ItemStack[] getJewelrySlots() {
    return this.jewelrySlots;
  }
}
