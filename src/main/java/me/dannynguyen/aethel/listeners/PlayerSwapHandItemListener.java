package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.systems.object.RpgCharacter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

/**
 * PlayerSwapHandItemListener is a general usage swap hand item listener.
 * <p>
 * Equipment Attributes:
 * Only reads the item being swapped to the off hand because the
 * main hand item is already being tracked on a set interval.
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.8.13
 * @since 1.8.13
 */
public class PlayerSwapHandItemListener implements Listener {
  @EventHandler
  public void onSwap(PlayerSwapHandItemsEvent e) {
    Player player = e.getPlayer();
    RpgCharacter rpgCharacter = PluginData.rpgData.getRpgCharacters().get(player);

    PluginData.rpgData.readEquipmentSlot(
        rpgCharacter.getEquipmentAttributes(),
        rpgCharacter.getAethelAttributes(),
        e.getOffHandItem(), "off_hand");
  }
}
