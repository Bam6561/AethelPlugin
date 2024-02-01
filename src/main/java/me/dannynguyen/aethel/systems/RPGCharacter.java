package me.dannynguyen.aethel.systems;

import org.bukkit.entity.Player;

import java.util.Map;

/**
 * RPGCharacter is an object relating a player with their RPG data.
 *
 * @author Danny Nguyen
 * @version 1.8.9
 * @since 1.8.9
 */
public class RPGCharacter {
  private final Player player;
  private Map aethelAttributeModifiers;

  public RPGCharacter(Player player, Map aethelAttributeModifiers) {
    this.player = player;
    this.aethelAttributeModifiers = aethelAttributeModifiers;
  }

  public Player getPlayer() {
    return this.player;
  }

  public Map getAethelAttributeModifiers() {
    return this.aethelAttributeModifiers;
  }
}
