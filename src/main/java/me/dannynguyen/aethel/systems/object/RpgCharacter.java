package me.dannynguyen.aethel.systems.object;

import org.bukkit.entity.Player;

import java.util.Map;

/**
 * RpgCharacter is an object relating a player
 * with their equipment and Aethel attribute values.
 *
 * @author Danny Nguyen
 * @version 1.8.12
 * @since 1.8.9
 */
public class RpgCharacter {
  private final Player player;
  private Map<String, Map<String, Double>> equipmentAttributes;
  private Map<String, Double> aethelAttributes;

  public RpgCharacter(Player player,
                      Map<String, Map<String, Double>> equipmentAttributes,
                      Map<String, Double> aethelAttributes) {
    this.player = player;
    this.equipmentAttributes = equipmentAttributes;
    this.aethelAttributes = aethelAttributes;
  }

  public Player getPlayer() {
    return this.player;
  }

  public Map<String, Map<String, Double>> getEquipmentAttributes() {
    return this.equipmentAttributes;
  }

  public Map<String, Double> getAethelAttributes() {
    return this.aethelAttributes;
  }
}
