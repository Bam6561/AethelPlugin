package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.rpg.Equipment;
import me.dannynguyen.aethel.rpg.RpgPlayer;
import me.dannynguyen.aethel.utils.item.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Collection of {@link Equipment} held, equipped, and unequipped listeners.
 *
 * @author Danny Nguyen
 * @version 1.22.17
 * @since 1.9.0
 */
public class EquipmentListener implements Listener {
  /**
   * No parameter constructor.
   */
  public EquipmentListener() {
  }

  /**
   * Checks clicks within player inventories to determine
   * whether to update a player's {@link Equipment}.
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
              updateEquipmentSlot(player, slot);
              Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
              }, 1);
            }
          }
        }
      }
    }
  }

  /**
   * Updates a player's {@link Equipment} when items are held.
   *
   * @param e player held item event
   */
  @EventHandler
  private void onItemHeld(PlayerItemHeldEvent e) {
    Player player = e.getPlayer();
    Equipment equipment = Plugin.getData().getRpgSystem().getRpgPlayers().get(player.getUniqueId()).getEquipment();
    ItemStack heldItem = player.getInventory().getItem(e.getNewSlot());
    if (heldItem == null) {
      heldItem = new ItemStack(Material.AIR);
    }
    equipment.setHeldItem(heldItem);
    equipment.readSlot(heldItem, RpgEquipmentSlot.HAND);
  }

  /**
   * Updates a player's {@link Equipment} when items are swapped.
   *
   * @param e player swap hand items event
   */
  @EventHandler
  private void onSwapHandItem(PlayerSwapHandItemsEvent e) {
    RpgPlayer rpgPlayer = Plugin.getData().getRpgSystem().getRpgPlayers().get(e.getPlayer().getUniqueId());
    rpgPlayer.getEquipment().readSlot(e.getOffHandItem(), RpgEquipmentSlot.OFF_HAND);
  }

  /**
   * Updates a player's {@link Equipment} when a wearable item is interacted with.
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
   * Updates a player's {@link Equipment} when a dispenser equips them with armor.
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
   * Updates a player's {@link Equipment} when a wearable item is broken.
   *
   * @param e player item break event
   */
  @EventHandler
  private void onBreak(PlayerItemBreakEvent e) {
    updateIfWornItem(e.getPlayer(), e.getBrokenItem());
  }

  /**
   * Updates the player's {@link Equipment} if the item is a worn item.
   *
   * @param player interacting player
   * @param item   interacting item
   */
  private void updateIfWornItem(Player player, ItemStack item) {
    switch (item.getType()) {
      case LEATHER_HELMET, CHAINMAIL_HELMET, IRON_HELMET, GOLDEN_HELMET, DIAMOND_HELMET, NETHERITE_HELMET,
          CREEPER_HEAD, ZOMBIE_HEAD, SKELETON_SKULL, WITHER_SKELETON_SKULL, PLAYER_HEAD, DRAGON_HEAD,
          TURTLE_HELMET, PUMPKIN -> updateEquipmentSlot(player, 39);
      case LEATHER_CHESTPLATE, CHAINMAIL_CHESTPLATE, IRON_CHESTPLATE, GOLDEN_CHESTPLATE,
          DIAMOND_CHESTPLATE, NETHERITE_CHESTPLATE, ELYTRA -> updateEquipmentSlot(player, 38);
      case LEATHER_LEGGINGS, CHAINMAIL_LEGGINGS, IRON_LEGGINGS, GOLDEN_LEGGINGS,
          DIAMOND_LEGGINGS, NETHERITE_LEGGINGS -> updateEquipmentSlot(player, 37);
      case LEATHER_BOOTS, CHAINMAIL_BOOTS, IRON_BOOTS, GOLDEN_BOOTS,
          DIAMOND_BOOTS, NETHERITE_BOOTS -> updateEquipmentSlot(player, 36);
      case SHIELD -> updateEquipmentSlot(player, 40);
    }
  }

  /**
   * Updates the player's {@link Equipment} at the slot they interacted with.
   * <p>
   * A 1 tick delay is used for because only the item that exists in the
   * corresponding slot after the interaction happens should be read.
   *
   * @param player interacting player
   * @param slot   slot type
   */
  private void updateEquipmentSlot(Player player, int slot) {
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
      RpgPlayer rpgPlayer = Plugin.getData().getRpgSystem().getRpgPlayers().get(player.getUniqueId());
      Equipment equipment = rpgPlayer.getEquipment();
      final ItemStack wornItem = player.getInventory().getItem(slot);
      switch (slot) {
        case 36 -> equipment.readSlot(wornItem, RpgEquipmentSlot.FEET);
        case 37 -> equipment.readSlot(wornItem, RpgEquipmentSlot.LEGS);
        case 38 -> equipment.readSlot(wornItem, RpgEquipmentSlot.CHEST);
        case 39 -> equipment.readSlot(wornItem, RpgEquipmentSlot.HEAD);
        case 40 -> equipment.readSlot(wornItem, RpgEquipmentSlot.OFF_HAND);
      }
    }, 1);
  }
}
