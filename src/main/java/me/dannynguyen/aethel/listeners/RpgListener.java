package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.rpg.AethelAttribute;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType;
import me.dannynguyen.aethel.rpg.Equipment;
import me.dannynguyen.aethel.rpg.RpgPlayer;
import me.dannynguyen.aethel.rpg.RpgSystem;
import me.dannynguyen.aethel.rpg.abilities.PassiveAbility;
import me.dannynguyen.aethel.utils.entity.HealthChange;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Collection of {@link RpgSystem} listeners.
 *
 * @author Danny Nguyen
 * @version 1.26.2
 * @since 1.10.6
 */
public class RpgListener implements Listener {
  /**
   * No parameter constructor.
   */
  public RpgListener() {
  }

  /**
   * Assigns an {@link RpgPlayer} to a player upon joining the server.
   *
   * @param e player join event
   */
  @EventHandler
  private void onJoin(PlayerJoinEvent e) {
    Player player = e.getPlayer();
    UUID uuid = player.getUniqueId();

    Map<UUID, RpgPlayer> rpgPlayers = Plugin.getData().getRpgSystem().getRpgPlayers();
    RpgPlayer rpgPlayer = rpgPlayers.get(uuid);

    if (rpgPlayer == null) {
      rpgPlayer = new RpgPlayer(player);
      rpgPlayers.put(uuid, rpgPlayer);
    } else {
      BossBar healthBar = rpgPlayer.getDisplays().getBar();
      healthBar.removeAll();
      healthBar.addPlayer(player);
    }
  }

  /**
   * Updates the entity's health to account for absorption and health boost status effects.
   *
   * @param e entity potion effect event
   */
  @EventHandler
  private void onPotionEffect(EntityPotionEffectEvent e) {
    if (e.getEntity() instanceof LivingEntity livingEntity) {
      switch (e.getModifiedType().getName()) {
        case "ABSORPTION" -> {
          if (e.getAction() == EntityPotionEffectEvent.Action.ADDED || e.getAction() == EntityPotionEffectEvent.Action.CHANGED) {
            Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> new HealthChange(livingEntity).shield(), 1);
          }
        }
        case "HEALTH_BOOST" -> Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> new HealthChange(livingEntity).updateDisplays(), 1);
      }
    }
  }

  /**
   * Updates a player's {@link Equipment} when they die unless they have keep inventory on.
   *
   * @param e player death event
   */
  @EventHandler
  private void onPlayerDeath(PlayerDeathEvent e) {
    RpgSystem rpgSystem = Plugin.getData().getRpgSystem();
    Player player = e.getEntity();
    UUID uuid = player.getUniqueId();
    RpgPlayer rpgPlayer = rpgSystem.getRpgPlayers().get(uuid);
    BukkitScheduler scheduler = Bukkit.getScheduler();

    rpgSystem.getStatuses().remove(uuid);
    for (int taskId : rpgPlayer.getProjections()) {
      scheduler.cancelTask(taskId);
    }

    if (!e.getKeepInventory()) {
      Equipment equipment = rpgPlayer.getEquipment();

      Equipment.AethelAttributes attributes = equipment.getAttributes();
      Map<RpgEquipmentSlot, Map<AethelAttribute, Double>> slotAttributes = attributes.getSlotAttributes();
      for (RpgEquipmentSlot eSlot : slotAttributes.keySet()) {
        attributes.removeAttributes(eSlot);
      }

      Equipment.Enchantments enchantments = equipment.getEnchantments();
      Map<RpgEquipmentSlot, Map<Enchantment, Integer>> slotEnchantments = enchantments.getSlotEnchantments();
      for (RpgEquipmentSlot eSlot : slotEnchantments.keySet()) {
        enchantments.removeEnchantments(eSlot);
      }

      Equipment.Abilities abilities = equipment.getAbilities();
      Map<RpgEquipmentSlot, List<Equipment.Abilities.TriggerPassive>> slotPassives = abilities.getSlotPassives();
      for (RpgEquipmentSlot eSlot : slotPassives.keySet()) {
        abilities.removePassives(eSlot);
      }
      for (RpgEquipmentSlot eSlot : RpgEquipmentSlot.values()) {
        abilities.removeActives(eSlot);
      }

      ItemStack[] jewelrySlots = equipment.getJewelry();
      for (int i = 0; i < jewelrySlots.length; i++) {
        if (jewelrySlots[i] != null) {
          player.getWorld().dropItem(player.getLocation(), jewelrySlots[i]);
          jewelrySlots[i] = null;
        }
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
    new HealthChange(e.getPlayer()).reset();
  }

  /**
   * Triggers {@link PassiveTriggerType#ON_KILL} {@link PassiveAbility passive abilities} if the player has any.
   *
   * @param e entity death event
   */
  @EventHandler
  private void onEntityDeath(EntityDeathEvent e) {
    LivingEntity killed = e.getEntity();
    Player killer = killed.getKiller();
    if (killer == null) {
      return;
    }

    triggerRaidCaptainBadOmen(killed, killer);
    triggerOnKillPassives(killed.getUniqueId(), killer.getUniqueId());
  }

  /**
   * Gives the player Bad Omen effect if the killed entity was a raid captain.
   *
   * @param killed killed entity
   * @param killer player
   */
  private void triggerRaidCaptainBadOmen(LivingEntity killed, Player killer) {
    if (killed.getCategory() != EntityCategory.ILLAGER) {
      return;
    }
    ItemStack head = killed.getEquipment().getHelmet();
    if (head == null || head.getType() != Material.WHITE_BANNER) {
      return;
    }

    if (killer.hasPotionEffect(PotionEffectType.BAD_OMEN)) {
      int amplifier = killer.getPotionEffect(PotionEffectType.BAD_OMEN).getAmplifier();
      if (amplifier < 5) {
        killer.removePotionEffect(PotionEffectType.BAD_OMEN);
        killer.addPotionEffect(new PotionEffect(PotionEffectType.BAD_OMEN, 120000, amplifier + 1));
      }
    } else {
      killer.addPotionEffect(new PotionEffect(PotionEffectType.BAD_OMEN, 120000, 0));
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
  private void triggerOnKillPassives(UUID killedUUID, UUID selfUUID) {
    RpgPlayer rpgPlayer = Plugin.getData().getRpgSystem().getRpgPlayers().get(selfUUID);
    Map<Equipment.Abilities.SlotPassive, PassiveAbility> killTriggers = rpgPlayer.getEquipment().getAbilities().getTriggerPassives().get(PassiveTriggerType.ON_KILL);
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
          case BUFF, STACK_INSTANCE, POTION_EFFECT -> {
            if (self) {
              ability.doEffect(rpgPlayer.getUUID(), selfUUID);
            }
          }
          case CHAIN_DAMAGE -> {
            if (self) {
              targetUUID = selfUUID;
            } else {
              targetUUID = killedUUID;
            }
            ability.doEffect(rpgPlayer.getUUID(), targetUUID);
          }
        }
      }
    }
  }
}
