package me.dannynguyen.aethel.listeners.rpg;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.systems.plugin.PluginData;
import me.dannynguyen.aethel.systems.rpg.*;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Collection of equipment attribute update listeners.
 *
 * @author Danny Nguyen
 * @version 1.13.6
 * @since 1.9.0
 */
public class EquipmentAttributes implements Listener {
  /**
   * Checks clicks within player inventories to determine whether to update a player's equipment attributes.
   *
   * @param e inventory click event
   */
  @EventHandler
  private void onInventoryClick(InventoryClickEvent e) {
    Inventory inv = e.getClickedInventory();
    if (inv != null && inv.getType() == InventoryType.PLAYER) {
      Player player = (Player) e.getWhoClicked();
      if (e.getClick().isShiftClick() && ItemReader.isNotNullOrAir(e.getCurrentItem())) {
        updateIfWornItem(player, e.getCurrentItem());
      } else {
        int slot = e.getSlot();
        switch (slot) {
          case 36, 37, 38, 39, 40 -> {
            if (ItemReader.isNotNullOrAir(e.getCursor()) || ItemReader.isNotNullOrAir(e.getCurrentItem())) {
              updateEquipmentAttributesAtSlot(player, slot);
              Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
              }, 1);
            }
          }
        }
      }
    }
  }

  /**
   * Updates a player's equipment attributes when items are held.
   *
   * @param e player held item event
   */
  @EventHandler
  private void onItemHeld(PlayerItemHeldEvent e) {
    Player player = e.getPlayer();
    RpgPlayer rpgPlayer = PluginData.rpgSystem.getRpgPlayers().get(player.getUniqueId());
    rpgPlayer.getEquipment().readSlot(player.getInventory().getItem(e.getNewSlot()), RpgEquipmentSlot.HAND);
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> rpgPlayer.getHealth().updateMaxHealth(), 2);
  }

  /**
   * Updates a player's equipment attributes when items are swapped.
   *
   * @param e player swap hand items event
   */
  @EventHandler
  private void onSwapHandItem(PlayerSwapHandItemsEvent e) {
    RpgPlayer rpgPlayer = PluginData.rpgSystem.getRpgPlayers().get(e.getPlayer().getUniqueId());
    rpgPlayer.getEquipment().readSlot(e.getOffHandItem(), RpgEquipmentSlot.OFF_HAND);
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> rpgPlayer.getHealth().updateMaxHealth(), 2);
  }

  /**
   * Updates a player's equipment attributes when a wearable item is interacted with.
   *
   * @param e player interact event
   */
  @EventHandler
  private void onInteract(PlayerInteractEvent e) {
    Action action = e.getAction();
    if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
      if (ItemReader.isNotNullOrAir(e.getItem())) {
        updateIfWornItem(e.getPlayer(), e.getItem());
      }
    }
  }

  /**
   * Updates a player's equipment attributes when a dispenser equips them with armor.
   *
   * @param e block dispense armor event
   */
  @EventHandler
  private void onDispense(BlockDispenseArmorEvent e) {
    if (e.getTargetEntity() instanceof Player player) {
      updateIfWornItem(player, e.getItem());
    }
  }

  /**
   * Updates a player's equipment attributes when a wearable item is broken.
   *
   * @param e player item break event
   */
  @EventHandler
  private void onBreak(PlayerItemBreakEvent e) {
    updateIfWornItem(e.getPlayer(), e.getBrokenItem());
  }

  /**
   * Updates a player's equipment attributes when they die unless they have keep inventory on.
   *
   * @param e player death event
   */
  @EventHandler
  private void onDeath(PlayerDeathEvent e) {
    if (!e.getKeepInventory()) {
      RpgEquipment rpgEquipment = PluginData.rpgSystem.getRpgPlayers().get(e.getEntity().getUniqueId()).getEquipment();
      Map<RpgEquipmentSlot, Map<Enchantment, Integer>> equipmentEnchantments = rpgEquipment.getEnchantments();
      Map<RpgEquipmentSlot, Map<AethelAttribute, Double>> equipmentAttributes = rpgEquipment.getAttributes();

      dropJewelryItems(e.getEntity(), rpgEquipment.getJewelry());

      for (RpgEquipmentSlot slot : equipmentEnchantments.keySet()) {
        rpgEquipment.removeEnchantments(slot);
      }

      for (RpgEquipmentSlot slot : equipmentAttributes.keySet()) {
        rpgEquipment.removeAttributes(slot);
      }
    }
  }

  /**
   * Updates the player's equipment attributes if the item is a worn item.
   *
   * @param player interacting player
   * @param item   interacting item
   */
  private void updateIfWornItem(Player player, ItemStack item) {
    switch (item.getType()) {
      case LEATHER_HELMET, CHAINMAIL_HELMET, IRON_HELMET, GOLDEN_HELMET, DIAMOND_HELMET, NETHERITE_HELMET,
          CREEPER_HEAD, ZOMBIE_HEAD, SKELETON_SKULL, WITHER_SKELETON_SKULL, PLAYER_HEAD, DRAGON_HEAD,
          TURTLE_HELMET, PUMPKIN -> updateEquipmentAttributesAtSlot(player, 39);
      case LEATHER_CHESTPLATE, CHAINMAIL_CHESTPLATE, IRON_CHESTPLATE, GOLDEN_CHESTPLATE,
          DIAMOND_CHESTPLATE, NETHERITE_CHESTPLATE, ELYTRA -> updateEquipmentAttributesAtSlot(player, 38);
      case LEATHER_LEGGINGS, CHAINMAIL_LEGGINGS, IRON_LEGGINGS, GOLDEN_LEGGINGS,
          DIAMOND_LEGGINGS, NETHERITE_LEGGINGS -> updateEquipmentAttributesAtSlot(player, 37);
      case LEATHER_BOOTS, CHAINMAIL_BOOTS, IRON_BOOTS, GOLDEN_BOOTS,
          DIAMOND_BOOTS, NETHERITE_BOOTS -> updateEquipmentAttributesAtSlot(player, 36);
      case SHIELD -> updateEquipmentAttributesAtSlot(player, 40);
    }
  }

  /**
   * Updates the player's equipment attributes at the slot they interacted with.
   * <p>
   * A 1 tick delay is used for because only the item that exists in the
   * corresponding slot after the interaction happens should be read.
   * </p>
   *
   * @param player interacting player
   * @param slot   slot type
   */
  private void updateEquipmentAttributesAtSlot(Player player, int slot) {
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
      RpgPlayer rpgPlayer = PluginData.rpgSystem.getRpgPlayers().get(player.getUniqueId());
      RpgEquipment rpgEquipment = rpgPlayer.getEquipment();
      RpgHealth rpgHealth = rpgPlayer.getHealth();
      final ItemStack wornItem = player.getInventory().getItem(slot);
      switch (slot) {
        case 36 -> rpgEquipment.readSlot(wornItem, RpgEquipmentSlot.FEET);
        case 37 -> rpgEquipment.readSlot(wornItem, RpgEquipmentSlot.LEGS);
        case 38 -> rpgEquipment.readSlot(wornItem, RpgEquipmentSlot.CHEST);
        case 39 -> rpgEquipment.readSlot(wornItem, RpgEquipmentSlot.HEAD);
        case 40 -> rpgEquipment.readSlot(wornItem, RpgEquipmentSlot.OFF_HAND);
      }
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> rpgHealth.updateMaxHealth(), 1);
    }, 1);
  }

  /**
   * Drops the player's jewelry items.
   *
   * @param player       interacting player
   * @param jewelrySlots player's jewelry slots
   */
  private void dropJewelryItems(Player player, ItemStack[] jewelrySlots) {
    for (int i = 0; i < jewelrySlots.length; i++) {
      if (jewelrySlots[i] != null) {
        player.getWorld().dropItem(player.getLocation(), jewelrySlots[i]);
        jewelrySlots[i] = null;
      }
    }
  }
}
