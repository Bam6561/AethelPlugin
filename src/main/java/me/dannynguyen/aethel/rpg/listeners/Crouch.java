package me.dannynguyen.aethel.rpg.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.commands.character.SettingsMenu;
import me.dannynguyen.aethel.plugin.enums.MenuMeta;
import me.dannynguyen.aethel.plugin.enums.PlayerMeta;
import me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot;
import me.dannynguyen.aethel.rpg.system.Settings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.Map;

/**
 * Player crouch listener.
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
        int heldSlot = player.getInventory().getHeldItemSlot();

        settings.setActiveAbilityCrouchBind(equipmentSlot, heldSlot);
        player.sendMessage(ChatColor.GREEN + "[Set " + ChatColor.AQUA + equipmentSlot.getProperName() + " Active Ability " + ChatColor.GREEN + "Crouch Bind] " + ChatColor.WHITE + heldSlot);
        playerMeta.remove(PlayerMeta.ACTION);
        player.openInventory(new SettingsMenu(player).getMainMenu());
        playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.CHARACTER_SETTINGS.getMeta());
      }
    }
  }
}
