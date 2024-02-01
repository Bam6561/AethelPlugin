package me.dannynguyen.aethel.systems.object;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * RpgCharacter is an object relating a player
 * with their equipment and Aethel attribute values.
 *
 * @author Danny Nguyen
 * @version 1.8.10
 * @since 1.8.9
 */
public class RpgCharacter {
  private final Player player;
  private Map<String, ItemStack> equipment;
  private Map<String, Double> aethelAttributes;

  public RpgCharacter(Player player, Map<String, ItemStack> equipment, Map<String, Double> aethelAttributes) {
    this.player = player;
    this.equipment = equipment;
    this.aethelAttributes = aethelAttributes;
  }

  public Player getPlayer() {
    return this.player;
  }

  public Map<String, ItemStack> getEquipment() {
    return this.equipment;
  }

  public Map<String, Double> getAethelAttributes() {
    return this.aethelAttributes;
  }
}
