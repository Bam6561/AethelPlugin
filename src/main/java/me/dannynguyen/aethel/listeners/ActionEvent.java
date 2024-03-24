package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.commands.character.SettingsMenu;
import me.dannynguyen.aethel.plugin.system.PluginPlayer;
import me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot;
import me.dannynguyen.aethel.rpg.system.Settings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

/**
 * Collection of player action listeners.
 *
 * @author Danny Nguyen
 * @version 1.17.17
 * @since 1.17.3
 */
public class ActionEvent implements Listener {
  /**
   * No parameter constructor.
   */
  public ActionEvent() {
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
      PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(player.getUniqueId());
      if (pluginPlayer.getActionInput() == Input.CROUCH_BIND_ACTIVE_ABILITY) {
        Settings settings = Plugin.getData().getRpgSystem().getRpgPlayers().get(player.getUniqueId()).getSettings();
        RpgEquipmentSlot slot = pluginPlayer.getSlot();
        int heldSlot = player.getInventory().getHeldItemSlot();

        settings.setActiveAbilityCrouchBind(slot, heldSlot);
        player.sendMessage(ChatColor.GREEN + "[Set " + ChatColor.AQUA + slot.getProperName() + " Active Ability " + ChatColor.GREEN + "Crouch Bind] " + ChatColor.WHITE + heldSlot);
        pluginPlayer.setActionInput(null);
        player.openInventory(new SettingsMenu(player).getMainMenu());
        pluginPlayer.setMenu(MenuEvent.Menu.CHARACTER_SETTINGS);
      }
    }
  }

  /**
   * Action input types.
   */
  public enum Input {
    /**
     * Binds active abilities by crouching.
     */
    CROUCH_BIND_ACTIVE_ABILITY
  }
}
