package me.dannynguyen.aethel.listeners.plugin;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.commands.character.SettingsMenu;
import me.dannynguyen.aethel.systems.plugin.MenuMeta;
import me.dannynguyen.aethel.systems.plugin.PlayerMeta;
import me.dannynguyen.aethel.systems.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.systems.rpg.Settings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.Map;

/**
 * Player crouch listener for the plugin system.
 *
 * @author Danny Nguyen
 * @version 1.17.4
 * @since 1.17.3
 */
public class Crouch implements Listener {
  /**
   * No parameter constructor.
   */
  public Crouch() {

  }

  /**
   * Routes interactions for crouches.
   *
   * @param e player crouch event
   */
  @EventHandler
  private void onCrouch(PlayerToggleSneakEvent e) {
    if (e.isSneaking()) {
      Player player = e.getPlayer();
      Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(player.getUniqueId());
      if (playerMeta.containsKey(PlayerMeta.ACTION) && playerMeta.get(PlayerMeta.ACTION).equals("crouch.bind-active_ability")) {
        Settings settings = Plugin.getData().getRpgSystem().getRpgPlayers().get(player.getUniqueId()).getSettings();
        RpgEquipmentSlot equipmentSlot = RpgEquipmentSlot.valueOf(playerMeta.get(PlayerMeta.SLOT).toUpperCase());
        int hotbarSlot = player.getInventory().getHeldItemSlot();

        settings.setActiveAbilityCrouchBind(equipmentSlot, hotbarSlot);
        player.sendMessage(ChatColor.GREEN + "[Set " + ChatColor.AQUA + equipmentSlot.getProperName() + " Active Ability " + ChatColor.GREEN + "Crouch Bind] " + ChatColor.WHITE + hotbarSlot);
        playerMeta.remove(PlayerMeta.ACTION);
        player.openInventory(new SettingsMenu(player).openMenu());
        playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.CHARACTER_SETTINGS.getMeta());
      }
    }
  }
}
