package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.rpg.Equipment;
import me.dannynguyen.aethel.utils.item.DurabilityChange;
import me.dannynguyen.aethel.utils.item.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Collection of {@link Equipment} held, equipped, and unequipped listeners.
 *
 * @author Danny Nguyen
 * @version 1.26.4
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
    if (inv == null || inv.getType() != InventoryType.PLAYER) {
      return;
    }

    Player player = (Player) e.getWhoClicked();

    int slot = e.getSlot();
    switch (slot) {
      case 36, 37, 38, 39, 40 -> {
        if (ItemReader.isNotNullOrAir(e.getCursor()) || ItemReader.isNotNullOrAir(e.getCurrentItem()) || e.getAction() == InventoryAction.HOTBAR_SWAP) {
          switch (slot) {
            case 36 -> readEquipmentSlot(player, RpgEquipmentSlot.FEET);
            case 37 -> readEquipmentSlot(player, RpgEquipmentSlot.LEGS);
            case 38 -> readEquipmentSlot(player, RpgEquipmentSlot.CHEST);
            case 39 -> readEquipmentSlot(player, RpgEquipmentSlot.HEAD);
            case 40 -> readEquipmentSlot(player, RpgEquipmentSlot.OFF_HAND);
          }
        }
        return;
      }
    }

    ItemStack item = e.getCurrentItem();
    if (e.getClick().isShiftClick() && ItemReader.isNotNullOrAir(item)) {
      RpgEquipmentSlot eSlot = getEquipmentSlot(item);
      if (eSlot != null) {
        readEquipmentSlot(player, eSlot);
      }
      PlayerInventory pInv = player.getInventory();
      int heldItemSlot = pInv.getHeldItemSlot();
      if (e.getSlot() == heldItemSlot || pInv.firstEmpty() == heldItemSlot) {
        readHandSlot(player);
      }
      return;
    }

    if (e.getSlot() == player.getInventory().getHeldItemSlot()) {
      readHandSlot(player);
      return;
    }

    if (e.getAction() == InventoryAction.HOTBAR_SWAP && e.getHotbarButton() == player.getInventory().getHeldItemSlot()) {
      readHandSlot(player);
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
    equipment.setHeldMaterial(heldItem);
    StringBuilder logEntry = new StringBuilder();
    String time = ZonedDateTime.now(ZoneId.of("America/New_York")).format(DateTimeFormatter.ofPattern("hh:mm"));
    logEntry.append(time).append(" ").append(e.getPlayer().getName()).append("EL/IH");
    Plugin.getData().getPluginLogger().addEntry(logEntry.toString());
    equipment.readSlot(heldItem, RpgEquipmentSlot.HAND);
  }

  /**
   * Updates a player's {@link Equipment} when items are dropped.
   *
   * @param e player dropped item event
   */
  @EventHandler
  private void onItemDrop(PlayerDropItemEvent e) {
    Player player = e.getPlayer();
    if (ItemReader.isNotNullOrAir(player.getInventory().getItemInMainHand())) {
      return;
    }

    Equipment equipment = Plugin.getData().getRpgSystem().getRpgPlayers().get(player.getUniqueId()).getEquipment();
    Material material = e.getItemDrop().getItemStack().getType();

    if (material == equipment.getHeldMaterial()) {
      ItemStack heldItem = player.getInventory().getItemInMainHand();
      equipment.setHeldMaterial(heldItem);
      StringBuilder logEntry = new StringBuilder();
      String time = ZonedDateTime.now(ZoneId.of("America/New_York")).format(DateTimeFormatter.ofPattern("hh:mm"));
      logEntry.append(time).append(" ").append(e.getPlayer().getName()).append("EL/ID");
      Plugin.getData().getPluginLogger().addEntry(logEntry.toString());
      equipment.readSlot(heldItem, RpgEquipmentSlot.HAND);
    }
  }

  /**
   * Updates a player's {@link Equipment} when items are swapped.
   *
   * @param e player swap hand items event
   */
  @EventHandler
  private void onSwapHandItems(PlayerSwapHandItemsEvent e) {
    Equipment equipment = Plugin.getData().getRpgSystem().getRpgPlayers().get(e.getPlayer().getUniqueId()).getEquipment();
    StringBuilder logEntry = new StringBuilder();
    String time = ZonedDateTime.now(ZoneId.of("America/New_York")).format(DateTimeFormatter.ofPattern("hh:mm"));
    logEntry.append(time).append(" ").append(e.getPlayer().getName()).append("EL/SHI");
    Plugin.getData().getPluginLogger().addEntry(logEntry.toString());
    equipment.readSlot(e.getMainHandItem(), RpgEquipmentSlot.HAND);
    equipment.readSlot(e.getOffHandItem(), RpgEquipmentSlot.OFF_HAND);
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
      ItemStack item = e.getItem();
      if (ItemReader.isNullOrAir(item)) {
        return;
      }
      RpgEquipmentSlot eSlot = getEquipmentSlot(item);
      if (eSlot != null && eSlot != RpgEquipmentSlot.OFF_HAND) {
        Player player = e.getPlayer();
        readEquipmentSlot(player, eSlot);
        readHandSlot(player);
      }
    }
  }

  /**
   * Updates a player's {@link Equipment} when a dispenser equips them with armor.
   *
   * @param e block dispense armor event
   */
  @EventHandler
  private void onDispenseArmor(BlockDispenseArmorEvent e) {
    if (e.getTargetEntity() instanceof Player player) {
      readEquipmentSlot(player, getEquipmentSlot(e.getItem()));
    }
  }

  /**
   * Updates a player's {@link Equipment} when they interact with an armor stand.
   *
   * @param e manipulate armor stand event
   */
  @EventHandler
  private void onManipulateArmorStand(PlayerArmorStandManipulateEvent e) {
    readHandSlot(e.getPlayer());
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

    DurabilityChange.increaseDamage(player, player.getEquipment(), eSlot, e.getDamage());
  }

  /**
   * Updates a player's {@link Equipment} when a wearable item is broken.
   *
   * @param e player item break event
   */
  @EventHandler
  private void onItemBreak(PlayerItemBreakEvent e) {
    ItemStack item = e.getBrokenItem();
    RpgEquipmentSlot eSlot = getEquipmentSlot(item);
    if (eSlot != null) {
      readEquipmentSlot(e.getPlayer(), eSlot);
    } else {
      readHandSlot(e.getPlayer());
    }
  }

  /**
   * Mends a player's item durability.
   *
   * @param e player item mend event
   */
  @EventHandler
  private void onItemMend(PlayerItemMendEvent e) {
    e.setCancelled(true);
    Player player = e.getPlayer();
    DurabilityChange.decreaseDamage(player, player.getEquipment(), e.getSlot(), e.getRepairAmount());
  }

  /**
   * Gets the worn item {@link RpgEquipmentSlot}.
   *
   * @param item interacting item
   * @return slot the equipment is worn on
   */
  private RpgEquipmentSlot getEquipmentSlot(ItemStack item) {
    switch (item.getType()) {
      case LEATHER_HELMET, CHAINMAIL_HELMET, IRON_HELMET, GOLDEN_HELMET, DIAMOND_HELMET, NETHERITE_HELMET,
          CREEPER_HEAD, ZOMBIE_HEAD, SKELETON_SKULL, WITHER_SKELETON_SKULL, PLAYER_HEAD, DRAGON_HEAD,
          TURTLE_HELMET, PUMPKIN -> {
        return RpgEquipmentSlot.HEAD;
      }
      case LEATHER_CHESTPLATE, CHAINMAIL_CHESTPLATE, IRON_CHESTPLATE,
          GOLDEN_CHESTPLATE, DIAMOND_CHESTPLATE, NETHERITE_CHESTPLATE,
          ELYTRA -> {
        return RpgEquipmentSlot.CHEST;
      }
      case LEATHER_LEGGINGS, CHAINMAIL_LEGGINGS, IRON_LEGGINGS,
          GOLDEN_LEGGINGS, DIAMOND_LEGGINGS, NETHERITE_LEGGINGS -> {
        return RpgEquipmentSlot.LEGS;
      }
      case LEATHER_BOOTS, CHAINMAIL_BOOTS, IRON_BOOTS,
          GOLDEN_BOOTS, DIAMOND_BOOTS, NETHERITE_BOOTS -> {
        return RpgEquipmentSlot.FEET;
      }
      case SHIELD -> {
        return RpgEquipmentSlot.OFF_HAND;
      }
      default -> {
        return null;
      }
    }
  }

  /**
   * Reads the item at the slot they interacted with.
   * <p>
   * A 1 tick delay is used for because only the item that exists in the
   * corresponding slot after the interaction happens should be read.
   *
   * @param player interacting player
   * @param eSlot  {@link RpgEquipmentSlot}
   */
  private void readEquipmentSlot(Player player, RpgEquipmentSlot eSlot) {
    int slot;
    switch (eSlot) {
      case HEAD -> slot = 39;
      case CHEST -> slot = 38;
      case LEGS -> slot = 37;
      case FEET -> slot = 36;
      case OFF_HAND -> slot = 40;
      default -> slot = -1;
    }
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
      Equipment equipment = Plugin.getData().getRpgSystem().getRpgPlayers().get(player.getUniqueId()).getEquipment();
      ItemStack wornItem = player.getInventory().getItem(slot);
      StringBuilder logEntry = new StringBuilder();
      String time = ZonedDateTime.now(ZoneId.of("America/New_York")).format(DateTimeFormatter.ofPattern("hh:mm"));
      logEntry.append(time).append(" ").append(player.getName()).append("EL/RES");
      Plugin.getData().getPluginLogger().addEntry(logEntry.toString());
      equipment.readSlot(wornItem, eSlot);
    }, 1);
  }

  /**
   * Reads the item in the hand slot.
   * <p>
   * A 1 tick delay is used for because only the item that exists in the
   * corresponding slot after the interaction happens should be read.
   *
   * @param player interacting player
   */
  private void readHandSlot(Player player) {
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
      Equipment equipment = Plugin.getData().getRpgSystem().getRpgPlayers().get(player.getUniqueId()).getEquipment();
      PlayerInventory pInv = player.getInventory();
      StringBuilder logEntry = new StringBuilder();
      String time = ZonedDateTime.now(ZoneId.of("America/New_York")).format(DateTimeFormatter.ofPattern("hh:mm"));
      logEntry.append(time).append(" ").append(player.getName()).append("EL/RHS");
      Plugin.getData().getPluginLogger().addEntry(logEntry.toString());
      equipment.readSlot(pInv.getItemInMainHand(), RpgEquipmentSlot.HAND);
    }, 1);
  }
}
