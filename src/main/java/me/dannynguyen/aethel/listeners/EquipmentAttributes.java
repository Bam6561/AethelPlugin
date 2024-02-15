package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.systems.object.RpgPlayer;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Collection of equipment attribute update listeners.
 *
 * @author Danny Nguyen
 * @version 1.9.22
 * @since 1.9.0
 */
public class EquipmentAttributes implements Listener {
  /**
   * All wearable items.
   */
  private final static HashSet<String> wornAll = new HashSet<>(List.of(
      "LEATHER_HELMET", "LEATHER_CHESTPLATE", "LEATHER_LEGGINGS", "LEATHER_BOOTS",
      "CHAINMAIL_HELMET", "CHAINMAIL_CHESTPLATE", "CHAINMAIL_LEGGINGS", "CHAINMAIL_BOOTS",
      "IRON_HELMET", "IRON_CHESTPLATE", "IRON_LEGGINGS", "IRON_BOOTS",
      "GOLDEN_HELMET", "GOLDEN_CHESTPLATE", "GOLDEN_LEGGINGS", "GOLDEN_BOOTS",
      "DIAMOND_HELMET", "DIAMOND_CHESTPLATE", "DIAMOND_LEGGINGS", "DIAMOND_BOOTS",
      "NETHERITE_HELMET", "NETHERITE_CHESTPLATE", "NETHERITE_LEGGINGS", "NETHERITE_BOOTS",
      "CREEPER_HEAD", "ZOMBIE_HEAD", "SKELETON_SKULL", "WITHER_SKELETON_SKULL", "PLAYER_HEAD",
      "DRAGON_HEAD", "TURTLE_HELMET", "PUMPKIN", "ELYTRA", "SHIELD"));

  /**
   * Items worn on the head slot.
   */
  private final static HashSet<String> wornHead = new HashSet<>(List.of(
      "LEATHER_HELMET", "CHAINMAIL_HELMET", "IRON_HELMET",
      "GOLDEN_HELMET", "DIAMOND_HELMET", "NETHERITE_HELMET",
      "CREEPER_HEAD", "ZOMBIE_HEAD", "SKELETON_SKULL", "WITHER_SKELETON_SKULL",
      "PLAYER_HEAD", "DRAGON_HEAD", "TURTLE_HELMET", "PUMPKIN"));

  /**
   * Items worn on the chest slot.
   */
  private final static HashSet<String> wornChest = new HashSet<>(List.of(
      "LEATHER_CHESTPLATE", "CHAINMAIL_CHESTPLATE", "IRON_CHESTPLATE",
      "GOLDEN_CHESTPLATE", "DIAMOND_CHESTPLATE", "NETHERITE_CHESTPLATE",
      "ELYTRA"));

  /**
   * Items worn on the leg slot.
   */
  private final static HashSet<String> wornLegs = new HashSet<>(List.of(
      "LEATHER_LEGGINGS", "CHAINMAIL_LEGGINGS", "IRON_LEGGINGS",
      "GOLDEN_LEGGINGS", "DIAMOND_LEGGINGS", "NETHERITE_LEGGINGS"));

  /**
   * Items worn on the feet slot.
   */
  private final static HashSet<String> wornFeet = new HashSet<>(List.of(
      "LEATHER_BOOTS", "CHAINMAIL_BOOTS", "IRON_BOOTS",
      "GOLDEN_BOOTS", "DIAMOND_BOOTS", "NETHERITE_BOOTS"));

  /**
   * Assigns an RPG profile to a player upon joining the server.
   *
   * @param e player join event
   */
  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    Player player = e.getPlayer();
    if (PluginData.rpgData.getRpgPlayers().get(player) == null) {
      PluginData.rpgData.loadRpgPlayer(player);
    }
  }

  /**
   * Checks clicks within player inventories to determine whether to update a player's equipment attributes.
   *
   * @param e inventory click event
   */
  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    Inventory inv = e.getClickedInventory();
    if (inv != null && inv.getType().equals(InventoryType.PLAYER)) {
      Player player = (Player) e.getWhoClicked();
      if (e.getClick().isShiftClick() && ItemReader.isNotNullOrAir(e.getCurrentItem())) {
        updateIfWornItem(player, e.getCurrentItem(), "shift");
      } else {
        int slot = e.getSlot();
        switch (slot) {
          case 36, 37, 38, 39, 40 -> {
            if (ItemReader.isNotNullOrAir(e.getCursor()) || ItemReader.isNotNullOrAir(e.getCurrentItem())) {
              updateEquipmentAttributesAtSlot(player, null, slot, "click");
            }
          }
        }
      }
    }
  }

  /**
   * Updates a player's equipment attributes when items are swapped.
   *
   * @param e player swap hand items event
   */
  @EventHandler
  public void onSwapHandItem(PlayerSwapHandItemsEvent e) {
    RpgPlayer rpgPlayer = PluginData.rpgData.getRpgPlayers().get(e.getPlayer());
    PluginData.rpgData.readEquipmentSlot(rpgPlayer.getEquipmentAttributes(), rpgPlayer.getAethelAttributes(), e.getOffHandItem(), "off_hand");
  }

  /**
   * Updates a player's equipment attributes when a wearable item is interacted with.
   *
   * @param e player interact event
   */
  @EventHandler
  public void onInteract(PlayerInteractEvent e) {
    Action action = e.getAction();
    if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
      if (ItemReader.isNotNullOrAir(e.getItem())) {
        updateIfWornItem(e.getPlayer(), e.getItem(), "interact");
      }
    }
  }

  /**
   * Updates a player's equipment attributes when a dispenser equips them with armor.
   *
   * @param e block dispense armor event
   */
  @EventHandler
  public void onDispense(BlockDispenseArmorEvent e) {
    if (e.getTargetEntity() instanceof Player player) {
      updateIfWornItem(player, e.getItem(), "dispense");
    }
  }

  /**
   * Updates a player's equipment attributes when a wearable item is broken.
   *
   * @param e player item break event
   */
  @EventHandler
  public void onBreak(PlayerItemBreakEvent e) {
    updateIfWornItem(e.getPlayer(), e.getBrokenItem(), "break");
  }

  /**
   * Updates a player's equipment attributes when they die unless they have keep inventory on.
   *
   * @param e player death event
   */
  @EventHandler
  public void onDeath(PlayerDeathEvent e) {
    if (!e.getKeepInventory()) {
      RpgPlayer rpgPlayer = PluginData.rpgData.getRpgPlayers().get(e.getEntity());
      Map<String, Map<String, Double>> equipment = rpgPlayer.getEquipmentAttributes();
      Map<String, Double> aethelAttributes = rpgPlayer.getAethelAttributes();

      dropJewelryItems(e.getEntity(), rpgPlayer.getJewelrySlots());

      for (String slot : equipment.keySet()) {
        PluginData.rpgData.removeExistingEquipmentAttributes(equipment, aethelAttributes, slot);
      }
    }
  }

  /**
   * Updates the player's equipment attributes if the item is a worn item.
   *
   * @param player interacting player
   * @param item   interacting item
   * @param action type of interaction
   */
  private void updateIfWornItem(Player player, ItemStack item, String action) {
    String itemType = item.getType().name();
    if (wornAll.contains(itemType)) {
      if (wornHead.contains(itemType)) {
        updateEquipmentAttributesAtSlot(player, item, 39, action);
      } else if (wornChest.contains(itemType)) {
        updateEquipmentAttributesAtSlot(player, item, 38, action);
      } else if (wornLegs.contains(itemType)) {
        updateEquipmentAttributesAtSlot(player, item, 37, action);
      } else if (wornFeet.contains(itemType)) {
        updateEquipmentAttributesAtSlot(player, item, 36, action);
      } else if (itemType.equals("SHIELD")) {
        updateEquipmentAttributesAtSlot(player, item, 40, action);
      }
    }
  }

  /**
   * Updates the player's equipment attributes at the slot they interacted with.
   * <p>
   * A 1 tick delay is used for inventory clicks and item break interactions because only the
   * item that exists in the corresponding slot after the interaction happens should be read.
   * </p>
   *
   * @param player interacting player
   * @param item   interacting item
   * @param slot   slot type
   * @param action type of interaction
   */
  private void updateEquipmentAttributesAtSlot(Player player, ItemStack item, int slot, String action) {
    if (!(action.equals("shift") || action.equals("click") || action.equals("break"))) {
      RpgPlayer rpgPlayer = PluginData.rpgData.getRpgPlayers().get(player);
      switch (slot) {
        case 36 -> PluginData.rpgData.readEquipmentSlot(rpgPlayer.getEquipmentAttributes(), rpgPlayer.getAethelAttributes(), item, "feet");
        case 37 -> PluginData.rpgData.readEquipmentSlot(rpgPlayer.getEquipmentAttributes(), rpgPlayer.getAethelAttributes(), item, "legs");
        case 38 -> PluginData.rpgData.readEquipmentSlot(rpgPlayer.getEquipmentAttributes(), rpgPlayer.getAethelAttributes(), item, "chest");
        case 39 -> PluginData.rpgData.readEquipmentSlot(rpgPlayer.getEquipmentAttributes(), rpgPlayer.getAethelAttributes(), item, "head");
        case 40 -> PluginData.rpgData.readEquipmentSlot(rpgPlayer.getEquipmentAttributes(), rpgPlayer.getAethelAttributes(), item, "off_hand");
      }
    } else {
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
        RpgPlayer rpgPlayer = PluginData.rpgData.getRpgPlayers().get(player);
        final ItemStack wornItem = player.getInventory().getItem(slot);
        switch (slot) {
          case 36 -> PluginData.rpgData.readEquipmentSlot(rpgPlayer.getEquipmentAttributes(), rpgPlayer.getAethelAttributes(), wornItem, "feet");
          case 37 -> PluginData.rpgData.readEquipmentSlot(rpgPlayer.getEquipmentAttributes(), rpgPlayer.getAethelAttributes(), wornItem, "legs");
          case 38 -> PluginData.rpgData.readEquipmentSlot(rpgPlayer.getEquipmentAttributes(), rpgPlayer.getAethelAttributes(), wornItem, "chest");
          case 39 -> PluginData.rpgData.readEquipmentSlot(rpgPlayer.getEquipmentAttributes(), rpgPlayer.getAethelAttributes(), wornItem, "head");
          case 40 -> PluginData.rpgData.readEquipmentSlot(rpgPlayer.getEquipmentAttributes(), rpgPlayer.getAethelAttributes(), wornItem, "off_hand");
        }
      }, 1);
    }
  }

  /**
   * Drops the player's jewelry items.
   *
   * @param player       interacting player
   * @param jewelrySlots player's jewelry slots
   */
  private void dropJewelryItems(Player player, ItemStack[] jewelrySlots) {
    player.getWorld().dropItem(player.getLocation(), jewelrySlots[0]);
    player.getWorld().dropItem(player.getLocation(), jewelrySlots[1]);
    jewelrySlots[0] = new ItemStack(Material.AIR);
    jewelrySlots[1] = new ItemStack(Material.AIR);
  }
}
