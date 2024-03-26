package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.rpg.RpgPlayer;
import me.dannynguyen.aethel.rpg.abilities.ActiveAbility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

/**
 * Collection of player action listeners.
 *
 * @author Danny Nguyen
 * @version 1.19.0
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
      RpgPlayer rpgPlayer = Plugin.getData().getRpgSystem().getRpgPlayers().get(player.getUniqueId());
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
