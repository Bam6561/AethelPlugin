package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.rpg.Equipment;
import me.dannynguyen.aethel.rpg.RpgPlayer;
import me.dannynguyen.aethel.utils.item.ItemDurability;
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
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Collection of {@link Equipment} held, equipped, and unequipped listeners.
 *
 * @author Danny Nguyen
 * @version 1.23.3
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
        new EquipmentUpdate(player, e.getCurrentItem(), true).updateIfWornItem();
      } else {
        int slot = e.getSlot();
        switch (slot) {
          case 36, 37, 38, 39, 40 -> {
            if (ItemReader.isNotNullOrAir(e.getCursor()) || ItemReader.isNotNullOrAir(e.getCurrentItem())) {
              new EquipmentUpdate(player).updateEquipmentSlot(slot);
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
        new EquipmentUpdate(e.getPlayer(), e.getItem(), true).updateIfWornItem();
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
      new EquipmentUpdate(player, e.getItem(), false).updateIfWornItem();
    }
  }

  /**
   * Damages a player's item durability.
   *
   * @param e player item damage event
   */
  @EventHandler
  private void onItemDamage(PlayerItemDamageEvent e) {
    e.setCancelled(true);

    Player player = e.getPlayer();
    PlayerInventory pInv = player.getInventory();
    ItemStack item = e.getItem();
    EquipmentSlot eSlot = null;

    for (EquipmentSlot slot : EquipmentSlot.values()) {
      if (pInv.getItem(slot).equals(item)) {
        eSlot = slot;
        break;
      }
    }

    ItemDurability.increaseDamage(player, player.getEquipment(), eSlot, e.getDamage());
  }

  /**
   * Updates a player's {@link Equipment} when a wearable item is broken.
   *
   * @param e player item break event
   */
  @EventHandler
  private void onBreak(PlayerItemBreakEvent e) {
    new EquipmentUpdate(e.getPlayer(), e.getBrokenItem(), false).updateIfWornItem();
  }

  /**
   * Updates the player's equipment when wearable items are involved.
   *
   * @author Danny Nguyen
   * @version 1.23.3
   * @since 1.23.3
   */
  private static class EquipmentUpdate {
    /**
     * Interacting player.
     */
    private final Player player;

    /**
     * Interacting item.
     */
    private final ItemStack item;

    /**
     * If to update the main hand slot after the interaction.
     */
    private final boolean updateMainHandSlot;

    /**
     * Associates the equipment update with its player if the item slot is already known.
     * <p>
     * Only used in {@link #onInteract(PlayerInteractEvent)}.
     *
     * @param player interacting player
     */
    EquipmentUpdate(@NotNull Player player) {
      this.player = Objects.requireNonNull(player, "Null player");
      this.item = null;
      this.updateMainHandSlot = false;
    }

    /**
     * Associates the equipment update with its player, item, and if to update the main hand slot.
     * <p>
     * Used when the item slot is not already known.
     *
     * @param player             interacting player
     * @param item               interacting item
     * @param updateMainHandSlot if to update the main hand slot
     */
    EquipmentUpdate(@NotNull Player player, @NotNull ItemStack item, boolean updateMainHandSlot) {
      this.player = Objects.requireNonNull(player, "Null player");
      this.item = Objects.requireNonNull(item, "Null item");
      this.updateMainHandSlot = updateMainHandSlot;
    }

    /**
     * Updates the player's {@link Equipment} if the item is a worn item.
     */
    private void updateIfWornItem() {
      switch (item.getType()) {
        case LEATHER_HELMET, CHAINMAIL_HELMET, IRON_HELMET, GOLDEN_HELMET, DIAMOND_HELMET, NETHERITE_HELMET,
            CREEPER_HEAD, ZOMBIE_HEAD, SKELETON_SKULL, WITHER_SKELETON_SKULL, PLAYER_HEAD, DRAGON_HEAD,
            TURTLE_HELMET, PUMPKIN -> updateEquipmentSlot(39);
        case LEATHER_CHESTPLATE, CHAINMAIL_CHESTPLATE, IRON_CHESTPLATE,
            GOLDEN_CHESTPLATE, DIAMOND_CHESTPLATE, NETHERITE_CHESTPLATE,
            ELYTRA -> updateEquipmentSlot(38);
        case LEATHER_LEGGINGS, CHAINMAIL_LEGGINGS, IRON_LEGGINGS,
            GOLDEN_LEGGINGS, DIAMOND_LEGGINGS, NETHERITE_LEGGINGS -> updateEquipmentSlot(37);
        case LEATHER_BOOTS, CHAINMAIL_BOOTS, IRON_BOOTS,
            GOLDEN_BOOTS, DIAMOND_BOOTS, NETHERITE_BOOTS -> updateEquipmentSlot(36);
        case SHIELD -> updateEquipmentSlot(40);
      }
    }

    /**
     * Updates the player's {@link Equipment} at the slot they interacted with.
     * <p>
     * A 1 tick delay is used for because only the item that exists in the
     * corresponding slot after the interaction happens should be read.
     */
    private void updateEquipmentSlot(int slot) {
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
        Equipment equipment = Plugin.getData().getRpgSystem().getRpgPlayers().get(player.getUniqueId()).getEquipment();
        PlayerInventory pInv = player.getInventory();
        ItemStack wornItem = pInv.getItem(slot);

        switch (slot) {
          case 36 -> equipment.readSlot(wornItem, RpgEquipmentSlot.FEET);
          case 37 -> equipment.readSlot(wornItem, RpgEquipmentSlot.LEGS);
          case 38 -> equipment.readSlot(wornItem, RpgEquipmentSlot.CHEST);
          case 39 -> equipment.readSlot(wornItem, RpgEquipmentSlot.HEAD);
          case 40 -> equipment.readSlot(wornItem, RpgEquipmentSlot.OFF_HAND);
        }

        if (updateMainHandSlot) {
          equipment.readSlot(pInv.getItem(EquipmentSlot.HAND), RpgEquipmentSlot.HAND);
        }
      }, 1);
    }
  }
}
