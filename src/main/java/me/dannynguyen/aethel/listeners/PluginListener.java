package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.plugin.PluginPlayer;
import me.dannynguyen.aethel.plugin.PluginSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import java.util.UUID;

/**
 * Collection of {@link PluginSystem} listeners.
 *
 * @author Danny Nguyen
 * @version 1.24.12
 * @since 1.10.1
 */
public class PluginListener implements Listener {
  /**
   * No parameter constructor.
   */
  public PluginListener() {
  }

  /**
   * Associates a {@link PluginPlayer} to a player upon joining the server.
   *
   * @param e player join event
   */
  @EventHandler
  private void onJoin(PlayerJoinEvent e) {
    Player player = e.getPlayer();
    UUID playerUUID = player.getUniqueId();
    Map<UUID, PluginPlayer> pluginPlayers = Plugin.getData().getPluginSystem().getPluginPlayers();
    if (!pluginPlayers.containsKey(playerUUID)) {
      pluginPlayers.put(playerUUID, new PluginPlayer(player));
    }
  }

  /**
   * Prevents non-placeable blocks from being placed.
   *
   * @param e block place event
   */
  @EventHandler
  private void onBlockPlace(BlockPlaceEvent e) {
    ItemStack item = e.getItemInHand();
    if (item.getItemMeta().getPersistentDataContainer().has(Key.NON_PLACEABLE.getNamespacedKey(), PersistentDataType.BOOLEAN)) {
      e.setCancelled(true);
    }
  }

  /**
   * Prevents non-consumable items from being consumed.
   *
   * @param e player item consume vent
   */
  @EventHandler
  private void onPlayerItemConsume(PlayerItemConsumeEvent e) {
    ItemStack item = e.getItem();
    if (item.getItemMeta().getPersistentDataContainer().has(Key.NON_EDIBLE.getNamespacedKey(), PersistentDataType.BOOLEAN)) {
      e.setCancelled(true);
    }
  }
}
