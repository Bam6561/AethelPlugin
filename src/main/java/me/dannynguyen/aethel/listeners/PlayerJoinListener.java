package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.PluginData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * PlayerJoinListener is a general usage player join listener.
 *
 * @author Danny Nguyen
 * @version 1.8.10
 * @since 1.8.10
 */
public class PlayerJoinListener implements Listener {
  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    Player player = e.getPlayer();

    if (PluginData.rpgData.getRpgCharacters().get(player) == null) {
      PluginData.rpgData.loadRpgCharacter(player);
    }
  }
}
