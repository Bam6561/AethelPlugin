package me.dannynguyen.aethel.listeners.rpg;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.systems.rpg.RpgSystem;
import me.dannynguyen.aethel.systems.rpg.Status;
import me.dannynguyen.aethel.systems.rpg.StatusType;
import me.dannynguyen.aethel.systems.rpg.ability.PassiveAbility;
import me.dannynguyen.aethel.systems.rpg.ability.SlotAbility;
import me.dannynguyen.aethel.systems.rpg.ability.Trigger;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.*;

/**
 * Collection of listeners for RPG system functionality.
 *
 * @author Danny Nguyen
 * @version 1.16.11
 * @since 1.10.6
 */
public class RpgEvent implements Listener {
  /**
   * Assigns an RPG player to a player upon joining the server.
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
   * Updates the player's health to account for absorption and health boost status effects.
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
   * Resets the player's health.
   *
   * @param e player respawn event
   */
  @EventHandler
  private void onRespawn(PlayerRespawnEvent e) {
    Plugin.getData().getRpgSystem().getRpgPlayers().get(e.getPlayer().getUniqueId()).getHealth().reset();
  }

  /**
   * Triggers on kill passives if the player has any.
   *
   * @param e entity death event
   */
  @EventHandler
  private void onEntityDeath(EntityDeathEvent e) {
    if (e.getEntity().getKiller() != null) {
      triggerOnKillPassives(e.getEntity().getKiller());
    }
  }

  /**
   * Triggers on kill passive abilities.
   *
   * @param killer killer
   */
  private void triggerOnKillPassives(Player killer) {
    Map<SlotAbility, PassiveAbility> killTriggers = Plugin.getData().getRpgSystem().getRpgPlayers().get(killer.getUniqueId()).getEquipment().getTriggerPassives().get(Trigger.ON_KILL);
    if (!killTriggers.isEmpty()) {
      Map<UUID, Map<StatusType, Status>> entityStatuses = Plugin.getData().getRpgSystem().getStatuses();
      Random random = new Random();
      for (PassiveAbility ability : killTriggers.values()) {
        if (!ability.isOnCooldown()) {
          switch (ability.getAbility().getEffect()) {
            case STACK_INSTANCE -> applyStackInstanceEffect(entityStatuses, random, ability, killer.getUniqueId());
          }
        }
      }
    }
  }

  /**
   * Applies stack instances by chance.
   *
   * @param entityStatuses entity statuses
   * @param random         rng
   * @param ability        passive ability
   * @param selfUUID       self UUID
   */
  private void applyStackInstanceEffect(Map<UUID, Map<StatusType, Status>> entityStatuses, Random random, PassiveAbility ability, UUID selfUUID) {
    List<String> triggerData = ability.getTriggerData();
    double chance = Double.parseDouble(triggerData.get(0));

    if (chance > random.nextDouble() * 100) {
      List<String> effectData = ability.getEffectData();
      boolean self = Boolean.parseBoolean(effectData.get(0));
      if (self) {
        Map<StatusType, Status> statuses;
        if (!entityStatuses.containsKey(selfUUID)) {
          entityStatuses.put(selfUUID, new HashMap<>());
        }
        statuses = entityStatuses.get(selfUUID);

        StatusType statusType = StatusType.valueOf(ability.getAbility().toString());
        int stacks = Integer.parseInt(effectData.get(1));
        int ticks = Integer.parseInt(effectData.get(2));
        if (statuses.containsKey(statusType)) {
          statuses.get(statusType).addStacks(stacks, ticks);
        } else {
          statuses.put(statusType, new Status(selfUUID, statusType, stacks, ticks));
        }

        int cooldown = Integer.parseInt(triggerData.get(1));
        if (cooldown > 0) {
          ability.setOnCooldown(true);
          Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> ability.setOnCooldown(false), cooldown);
        }
      }
    }
  }
}
