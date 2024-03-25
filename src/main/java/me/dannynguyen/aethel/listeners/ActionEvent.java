package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.commands.character.SettingsMenu;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.plugin.PluginPlayer;
import me.dannynguyen.aethel.rpg.RpgPlayer;
import me.dannynguyen.aethel.rpg.Settings;
import me.dannynguyen.aethel.rpg.abilities.ActiveAbility;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.UUID;

/**
 * Collection of player action listeners.
 *
 * @author Danny Nguyen
 * @version 1.18.7
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
      UUID uuid = player.getUniqueId();
      PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
      if (pluginPlayer.getActionInput() == Input.CROUCH_BIND_ACTIVE_ABILITY) {
        Settings settings = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getSettings();
        RpgEquipmentSlot slot = pluginPlayer.getSlot();
        int heldSlot = player.getInventory().getHeldItemSlot();

        settings.setActiveAbilityCrouchBind(slot, heldSlot);
        player.sendMessage(ChatColor.GREEN + "[Set " + ChatColor.AQUA + slot.getProperName() + " Active Ability " + ChatColor.GREEN + "Crouch Bind] " + ChatColor.WHITE + heldSlot + 1);
        pluginPlayer.setActionInput(null);
        player.openInventory(new SettingsMenu(player).getMainMenu());
        pluginPlayer.setMenu(MenuEvent.Menu.CHARACTER_SETTINGS);
      } else {
        RpgPlayer rpgPlayer = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid);
        RpgEquipmentSlot eSlot = rpgPlayer.getSettings().getAbilityBoundHotbar().get(player.getInventory().getHeldItemSlot());
        if (eSlot != null) {
          for (ActiveAbility ability : rpgPlayer.getAbilities().getTriggerActives().get(eSlot)) {
            if (!ability.isOnCooldown()) {
              ability.doEffect(player);
            }
          }
        }
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
