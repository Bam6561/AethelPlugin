package me.dannynguyen.aethel.listeners.rpg;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.systems.plugin.PluginData;
import me.dannynguyen.aethel.systems.rpg.AethelAttribute;
import me.dannynguyen.aethel.systems.rpg.EquipmentSlot;
import me.dannynguyen.aethel.systems.rpg.RpgProfile;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.Bukkit;
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
import java.util.Set;

/**
 * Collection of equipment attribute update listeners.
 *
 * @author Danny Nguyen
 * @version 1.11.6
 * @since 1.9.0
 */
public class EquipmentAttributes implements Listener {
  /**
   * All wearable items.
   */
  private static final Set<String> wornAll = Set.of(
      "LEATHER_HELMET", "LEATHER_CHESTPLATE", "LEATHER_LEGGINGS", "LEATHER_BOOTS",
      "CHAINMAIL_HELMET", "CHAINMAIL_CHESTPLATE", "CHAINMAIL_LEGGINGS", "CHAINMAIL_BOOTS",
      "IRON_HELMET", "IRON_CHESTPLATE", "IRON_LEGGINGS", "IRON_BOOTS",
      "GOLDEN_HELMET", "GOLDEN_CHESTPLATE", "GOLDEN_LEGGINGS", "GOLDEN_BOOTS",
      "DIAMOND_HELMET", "DIAMOND_CHESTPLATE", "DIAMOND_LEGGINGS", "DIAMOND_BOOTS",
      "NETHERITE_HELMET", "NETHERITE_CHESTPLATE", "NETHERITE_LEGGINGS", "NETHERITE_BOOTS",
      "CREEPER_HEAD", "ZOMBIE_HEAD", "SKELETON_SKULL", "WITHER_SKELETON_SKULL", "PLAYER_HEAD",
      "DRAGON_HEAD", "TURTLE_HELMET", "PUMPKIN", "ELYTRA", "SHIELD");

  /**
   * Items worn on the head slot.
   */
  private static final Set<String> wornHead = Set.of(
      "LEATHER_HELMET", "CHAINMAIL_HELMET", "IRON_HELMET",
      "GOLDEN_HELMET", "DIAMOND_HELMET", "NETHERITE_HELMET",
      "CREEPER_HEAD", "ZOMBIE_HEAD", "SKELETON_SKULL", "WITHER_SKELETON_SKULL",
      "PLAYER_HEAD", "DRAGON_HEAD", "TURTLE_HELMET", "PUMPKIN");

  /**
   * Items worn on the chest slot.
   */
  private static final Set<String> wornChest = Set.of(
      "LEATHER_CHESTPLATE", "CHAINMAIL_CHESTPLATE", "IRON_CHESTPLATE",
      "GOLDEN_CHESTPLATE", "DIAMOND_CHESTPLATE", "NETHERITE_CHESTPLATE",
      "ELYTRA");

  /**
   * Items worn on the leg slot.
   */
  private static final Set<String> wornLegs = Set.of(
      "LEATHER_LEGGINGS", "CHAINMAIL_LEGGINGS", "IRON_LEGGINGS",
      "GOLDEN_LEGGINGS", "DIAMOND_LEGGINGS", "NETHERITE_LEGGINGS");

  /**
   * Items worn on the feet slot.
   */
  private static final Set<String> wornFeet = Set.of(
      "LEATHER_BOOTS", "CHAINMAIL_BOOTS", "IRON_BOOTS",
      "GOLDEN_BOOTS", "DIAMOND_BOOTS", "NETHERITE_BOOTS");

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
    RpgProfile rpgProfile = PluginData.rpgSystem.getRpgProfiles().get(player);
    rpgProfile.readEquipmentSlot(player.getInventory().getItem(e.getNewSlot()), EquipmentSlot.HAND);
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), rpgProfile::updateHealthBar, 2);
  }

  /**
   * Updates a player's equipment attributes when items are swapped.
   *
   * @param e player swap hand items event
   */
  @EventHandler
  private void onSwapHandItem(PlayerSwapHandItemsEvent e) {
    RpgProfile rpgProfile = PluginData.rpgSystem.getRpgProfiles().get(e.getPlayer());
    rpgProfile.readEquipmentSlot(e.getOffHandItem(), EquipmentSlot.OFF_HAND);
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), rpgProfile::updateHealthBar, 2);
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
      RpgProfile rpgProfile = PluginData.rpgSystem.getRpgProfiles().get(e.getEntity());
      Map<EquipmentSlot, Map<AethelAttribute, Double>> equipment = rpgProfile.getEquipmentAttributes();

      dropJewelryItems(e.getEntity(), rpgProfile.getJewelrySlots());

      for (EquipmentSlot slot : equipment.keySet()) {
        rpgProfile.removeEquipmentAttributes(slot);
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
    String itemType = item.getType().name();
    if (wornAll.contains(itemType)) {
      if (wornHead.contains(itemType)) {
        updateEquipmentAttributesAtSlot(player, 39);
      } else if (wornChest.contains(itemType)) {
        updateEquipmentAttributesAtSlot(player, 38);
      } else if (wornLegs.contains(itemType)) {
        updateEquipmentAttributesAtSlot(player, 37);
      } else if (wornFeet.contains(itemType)) {
        updateEquipmentAttributesAtSlot(player, 36);
      } else if (itemType.equals("SHIELD")) {
        updateEquipmentAttributesAtSlot(player, 40);
      }
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
      RpgProfile rpgProfile = PluginData.rpgSystem.getRpgProfiles().get(player);
      final ItemStack wornItem = player.getInventory().getItem(slot);
      switch (slot) {
        case 36 -> rpgProfile.readEquipmentSlot(wornItem, EquipmentSlot.FEET);
        case 37 -> rpgProfile.readEquipmentSlot(wornItem, EquipmentSlot.LEGS);
        case 38 -> rpgProfile.readEquipmentSlot(wornItem, EquipmentSlot.CHEST);
        case 39 -> rpgProfile.readEquipmentSlot(wornItem, EquipmentSlot.HEAD);
        case 40 -> rpgProfile.readEquipmentSlot(wornItem, EquipmentSlot.OFF_HAND);
      }
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), rpgProfile::updateHealthBar, 1);
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
