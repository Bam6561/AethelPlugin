package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.rpg.RpgPlayer;
import me.dannynguyen.aethel.rpg.abilities.ActiveAbility;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.Set;

/**
 * Collection of player action listeners.
 *
 * @author Danny Nguyen
 * @version 1.23.4
 * @since 1.17.3
 */
public class ActionListener implements Listener {
  /**
   * No parameter constructor.
   */
  public ActionListener() {
  }

  /**
   * Routes interactions for crouches.
   *
   * @param e player crouch event
   */
  @EventHandler
  private void onCrouch(PlayerToggleSneakEvent e) {
    if (!e.isSneaking()) {
      return;
    }
    Player player = e.getPlayer();
    if (player.getGameMode() == GameMode.SPECTATOR) {
      return;
    }
    RpgPlayer rpgPlayer = Plugin.getData().getRpgSystem().getRpgPlayers().get(player.getUniqueId());
    Set<RpgEquipmentSlot> eSlots = rpgPlayer.getSettings().getAbilityBoundHotbar().get(player.getInventory().getHeldItemSlot());
    if (eSlots == null) {
      return;
    }

    for (RpgEquipmentSlot eSlot : eSlots) {
      for (ActiveAbility ability : rpgPlayer.getEquipment().getAbilities().getTriggerActives().get(eSlot)) {
        if (!ability.isOnCooldown()) {
          ability.doEffect(player);
        }
      }
    }
  }
}
