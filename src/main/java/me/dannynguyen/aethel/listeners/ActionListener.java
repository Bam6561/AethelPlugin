package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.plugin.KeyHeader;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType;
import me.dannynguyen.aethel.rpg.RpgPlayer;
import me.dannynguyen.aethel.rpg.abilities.ActiveAbility;
import me.dannynguyen.aethel.utils.TextFormatter;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Set;

/**
 * Collection of player action listeners.
 *
 * @author Danny Nguyen
 * @version 1.25.9
 * @since 1.17.3
 */
public class ActionListener implements Listener {
  /**
   * No parameter constructor.
   */
  public ActionListener() {
  }

  /**
   * Routes interactions for player interactions.
   *
   * @param e player interaction event
   */
  @EventHandler
  private void onInteract(PlayerInteractEvent e) {
    Player player = e.getPlayer();
    switch (e.getAction()) {
      case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK -> {
      }
      default -> {
        return;
      }
    }
    if (player.getGameMode() == GameMode.SPECTATOR) {
      return;
    }
    RpgPlayer rpgPlayer = Plugin.getData().getRpgSystem().getRpgPlayers().get(player.getUniqueId());
    Set<RpgEquipmentSlot> eSlots = rpgPlayer.getSettings().getActiveAbilityRightClickBoundHotbar().get(player.getInventory().getHeldItemSlot());
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

  /**
   * Routes interactions for items consumed.
   *
   * @param e player item consume event
   */
  @EventHandler
  private void onPlayerItemConsume(PlayerItemConsumeEvent e) {
    ItemStack item = e.getItem();
    PersistentDataContainer itemTags = item.getItemMeta().getPersistentDataContainer();
    if (!itemTags.has(Key.ACTIVE_EDIBLE_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
      return;
    }
    Player caster = e.getPlayer();
    Set<String> onCooldownEdibles = Plugin.getData().getRpgSystem().getRpgPlayers().get(caster.getUniqueId()).getOnCooldownEdibles();
    String id;
    if (itemTags.has(Key.RECIPE_FORGE_ID.getNamespacedKey(), PersistentDataType.STRING)) {
      id = itemTags.get(Key.RECIPE_FORGE_ID.getNamespacedKey(), PersistentDataType.STRING);
    } else {
      id = item.getType().name();
    }
    if (onCooldownEdibles.contains(id)) {
      return;
    }

    String[] actives = itemTags.get(Key.ACTIVE_EDIBLE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ");
    for (String active : actives) {
      NamespacedKey activeKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.ACTIVE_EDIBLE.getHeader() + active);
      String[] abilityMeta = active.split("\\.");
      ActiveAbilityType activeAbilityType = ActiveAbilityType.valueOf(TextFormatter.formatEnum(abilityMeta[0]));
      ActiveAbility ability = new ActiveAbility(onCooldownEdibles, id, activeAbilityType, itemTags.get(activeKey, PersistentDataType.STRING).split(" "));
      ability.doEffect(caster);
    }
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
    Set<RpgEquipmentSlot> eSlots = rpgPlayer.getSettings().getActiveAbilityCrouchBoundHotbar().get(player.getInventory().getHeldItemSlot());
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
