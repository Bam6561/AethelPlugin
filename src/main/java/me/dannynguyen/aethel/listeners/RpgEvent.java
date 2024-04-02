package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType;
import me.dannynguyen.aethel.rpg.Health;
import me.dannynguyen.aethel.rpg.RpgPlayer;
import me.dannynguyen.aethel.rpg.RpgSystem;
import me.dannynguyen.aethel.rpg.abilities.Abilities;
import me.dannynguyen.aethel.rpg.abilities.PassiveAbility;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Collection of {@link RpgSystem} listeners.
 *
 * @author Danny Nguyen
 * @version 1.20.3
 * @since 1.10.6
 */
public class RpgEvent implements Listener {
  /**
   * No parameter constructor.
   */
  public RpgEvent() {
  }

  /**
   * Assigns an {@link RpgPlayer} to a player upon joining the server.
   *
   * @param e player join event
   */
  @EventHandler
  private void onJoin(PlayerJoinEvent e) {
    RpgSystem rpgSystem = Plugin.getData().getRpgSystem();
    Player player = e.getPlayer();
    UUID uuid = player.getUniqueId();

    if (rpgSystem.getRpgPlayers().get(uuid) == null) {
      rpgSystem.loadRpgPlayer(player);
      rpgSystem.getRpgPlayers().get(uuid).getEquipment().setHeldItem(player.getInventory().getItemInMainHand());
    } else {
      BossBar healthBar = rpgSystem.getRpgPlayers().get(uuid).getHealth().getBar();
      healthBar.removeAll();
      healthBar.addPlayer(player);
    }
  }

  /**
   * Updates the player's {@link Health}
   * to account for absorption and health boost status effects.
   *
   * @param e entity potion effect event
   */
  @EventHandler
  private void onPotionEffect(EntityPotionEffectEvent e) {
    if (e.getEntity() instanceof Player player) {
      switch (e.getModifiedType().getName()) {
        case "ABSORPTION" -> {
          if (e.getAction() == EntityPotionEffectEvent.Action.ADDED || e.getAction() == EntityPotionEffectEvent.Action.CHANGED) {
            Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Plugin.getData().getRpgSystem().getRpgPlayers().get(player.getUniqueId()).getHealth().updateOvershield(), 1);
          }
        }
        case "HEALTH_BOOST" -> Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Plugin.getData().getRpgSystem().getRpgPlayers().get(player.getUniqueId()).getHealth().updateMaxHealth(), 1);
      }
    }
  }

  /**
   * Resets the player's {@link Health}.
   *
   * @param e player respawn event
   */
  @EventHandler
  private void onRespawn(PlayerRespawnEvent e) {
    Plugin.getData().getRpgSystem().getRpgPlayers().get(e.getPlayer().getUniqueId()).getHealth().reset();
  }

  /**
   * Triggers {@link PassiveTriggerType#ON_KILL} {@link PassiveAbility passive abilities} if the player has any.
   *
   * @param e entity death event
   */
  @EventHandler
  private void onEntityDeath(EntityDeathEvent e) {
    if (e.getEntity().getKiller() != null) {
      triggerPassivesOnKill(e.getEntity().getUniqueId(), e.getEntity().getKiller().getUniqueId());
    }
  }

  /**
   * Triggers {@link PassiveTriggerType#ON_KILL} {@link PassiveAbility passive abilities}.
   * <p>
   * {@link PassiveTriggerType#ON_KILL}
   * {@link PassiveAbilityType.Effect#STACK_INSTANCE}
   * can only be triggered on self.
   *
   * @param killedUUID killed entity UUID
   * @param selfUUID   self UUID
   */
  private void triggerPassivesOnKill(UUID killedUUID, UUID selfUUID) {
    RpgPlayer rpgPlayer = Plugin.getData().getRpgSystem().getRpgPlayers().get(selfUUID);
    Map<Abilities.SlotPassive, PassiveAbility> killTriggers = rpgPlayer.getAbilities().getTriggerPassives().get(PassiveTriggerType.ON_KILL);
    if (killTriggers.isEmpty()) {
      return;
    }
    Random random = new Random();
    for (PassiveAbility ability : killTriggers.values()) {
      if (ability.isOnCooldown()) {
        continue;
      }
      double chance = Double.parseDouble(ability.getConditionData().get(0));
      if (chance > random.nextDouble() * 100) {
        boolean self = Boolean.parseBoolean(ability.getEffectData().get(0));
        UUID targetUUID;
        switch (ability.getType().getEffect()) {
          case STACK_INSTANCE, POTION_EFFECT -> {
            if (self) {
              ability.doEffect(rpgPlayer, selfUUID);
            }
          }
          case CHAIN_DAMAGE -> {
            if (self) {
              targetUUID = selfUUID;
            } else {
              targetUUID = killedUUID;
            }
            ability.doEffect(rpgPlayer, targetUUID);
          }
        }
      }
    }
  }
}
