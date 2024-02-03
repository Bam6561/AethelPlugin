package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.systems.object.RpgCharacter;
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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * EquipmentAttributeListener is a collection of listeners for
 * events related to changing a player's equipment attributes.
 *
 * @author Danny Nguyen
 * @version 1.9.1
 * @since 1.9.0
 */
public class EquipmentAttributeListener implements Listener {
  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    Player player = e.getPlayer();
    if (PluginData.rpgData.getRpgCharacters().get(player) == null) {
      PluginData.rpgData.loadRpgCharacter(player);
    }
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    Player player = (Player) e.getWhoClicked();
    Inventory inv = e.getClickedInventory();

    if (inv != null && inv.getType().equals(InventoryType.PLAYER)) {
      if (e.getClick().isShiftClick() && e.getCurrentItem() != null) {
        updateIfWornItem(player, inv, e.getCurrentItem().getType().name());
      } else {
        int slot = e.getSlot();
        switch (slot) {
          case 36, 37, 38, 39, 40 -> updateEquipmentAttributesAtSlot(player, inv, slot);
        }
      }
    }
  }

  @EventHandler
  public void onSwapHandItem(PlayerSwapHandItemsEvent e) {
    RpgCharacter rpgCharacter = PluginData.rpgData.getRpgCharacters().get(e.getPlayer());
    PluginData.rpgData.readEquipmentSlot(
        rpgCharacter.getEquipmentAttributes(),
        rpgCharacter.getAethelAttributes(),
        e.getOffHandItem(), "off_hand");
  }

  @EventHandler
  public void onInteract(PlayerInteractEvent e) {
    Action action = e.getAction();

    if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
      if (e.getItem() != null) {
        updateIfWornItem(e.getPlayer(), e.getPlayer().getInventory(), e.getItem().getType().name());
      }
    }
  }

  @EventHandler
  public void onDispense(BlockDispenseArmorEvent e) {
    if (e.getTargetEntity() instanceof Player player) {
      updateIfWornItem(player, player.getInventory(), e.getItem().getType().name());
    }
  }

  @EventHandler
  public void onBreak(PlayerItemBreakEvent e) {
    updateIfWornItem(e.getPlayer(), e.getPlayer().getInventory(), e.getBrokenItem().getType().name());
  }

  @EventHandler
  public void onDeath(PlayerDeathEvent e) {
    if (!e.getKeepInventory()) {
      RpgCharacter rpgCharacter = PluginData.rpgData.getRpgCharacters().get(e.getEntity());
      Map<String, Map<String, Double>> equipment = rpgCharacter.getEquipmentAttributes();
      Map<String, Double> aethelAttributes = rpgCharacter.getAethelAttributes();

      for (String slot : equipment.keySet()) {
        PluginData.rpgData.removeExistingEquipmentAttributes(equipment, aethelAttributes, slot);
      }
    }
  }

  /**
   * Updates the player's equipment attributes if the item is a worn item.
   *
   * @param player   interacting player
   * @param inv      interacting inventory
   * @param itemName item name
   */
  private void updateIfWornItem(Player player, Inventory inv, String itemName) {
    if (WornItems.ALL.items.contains(itemName)) {
      if (WornItems.HEAD.items.contains(itemName)) {
        updateEquipmentAttributesAtSlot(player, inv, 39);
      } else if (WornItems.CHEST.items.contains(itemName)) {
        updateEquipmentAttributesAtSlot(player, inv, 38);
      } else if (WornItems.LEGS.items.contains(itemName)) {
        updateEquipmentAttributesAtSlot(player, inv, 37);
      } else if (WornItems.FEET.items.contains(itemName)) {
        updateEquipmentAttributesAtSlot(player, inv, 36);
      } else if (itemName.equals("SHIELD")) {
        updateEquipmentAttributesAtSlot(player, inv, 40);
      }
    }
  }

  /**
   * Updates the player's equipment attributes at the slot they interacted with.
   *
   * @param player interacting player
   * @param inv    interacting inventory
   * @param slot   slot type
   */
  private void updateEquipmentAttributesAtSlot(Player player, Inventory inv, int slot) {
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
      RpgCharacter rpgCharacter = PluginData.rpgData.getRpgCharacters().get(player);
      ItemStack item = inv.getItem(slot);

      switch (slot) {
        case 36 -> PluginData.rpgData.readEquipmentSlot(
            rpgCharacter.getEquipmentAttributes(),
            rpgCharacter.getAethelAttributes(),
            item, "feet");
        case 37 -> PluginData.rpgData.readEquipmentSlot(
            rpgCharacter.getEquipmentAttributes(),
            rpgCharacter.getAethelAttributes(),
            item, "legs");
        case 38 -> PluginData.rpgData.readEquipmentSlot(
            rpgCharacter.getEquipmentAttributes(),
            rpgCharacter.getAethelAttributes(),
            item, "chest");
        case 39 -> PluginData.rpgData.readEquipmentSlot(
            rpgCharacter.getEquipmentAttributes(),
            rpgCharacter.getAethelAttributes(),
            item, "head");
        case 40 -> PluginData.rpgData.readEquipmentSlot(
            rpgCharacter.getEquipmentAttributes(),
            rpgCharacter.getAethelAttributes(),
            item, "off_hand");
      }
    }, 1);
  }

  private enum WornItems {
    ALL(new HashSet<>(List.of(
        "LEATHER_HELMET", "LEATHER_CHESTPLATE", "LEATHER_LEGGINGS", "LEATHER_BOOTS",
        "CHAINMAIL_HELMET", "CHAINMAIL_CHESTPLATE", "CHAINMAIL_LEGGINGS", "CHAINMAIL_BOOTS",
        "IRON_HELMET", "IRON_CHESTPLATE", "IRON_LEGGINGS", "IRON_BOOTS",
        "GOLDEN_HELMET", "GOLDEN_CHESTPLATE", "GOLDEN_LEGGINGS", "GOLDEN_BOOTS",
        "DIAMOND_HELMET", "DIAMOND_CHESTPLATE", "DIAMOND_LEGGINGS", "DIAMOND_BOOTS",
        "NETHERITE_HELMET", "NETHERITE_CHESTPLATE", "NETHERITE_LEGGINGS", "NETHERITE_BOOTS",
        "CREEPER_HEAD", "ZOMBIE_HEAD", "SKELETON_SKULL", "WITHER_SKELETON_SKULL", "PLAYER_HEAD",
        "DRAGON_HEAD", "TURTLE_HELMET", "PUMPKIN", "ELYTRA", "SHIELD"))),
    HEAD(new HashSet<>(List.of(
        "LEATHER_HELMET", "CHAINMAIL_HELMET", "IRON_HELMET",
        "GOLDEN_HELMET", "DIAMOND_HELMET", "NETHERITE_HELMET",
        "CREEPER_HEAD", "ZOMBIE_HEAD", "SKELETON_SKULL", "WITHER_SKELETON_SKULL", "PLAYER_HEAD",
        "DRAGON_HEAD", "TURTLE_HELMET", "PUMPKIN"))),
    CHEST(new HashSet<>(List.of(
        "LEATHER_CHESTPLATE", "CHAINMAIL_CHESTPLATE", "IRON_CHESTPLATE",
        "GOLDEN_CHESTPLATE", "DIAMOND_CHESTPLATE", "NETHERITE_CHESTPLATE",
        "ELYTRA"))),
    LEGS(new HashSet<>(List.of(
        "LEATHER_LEGGINGS", "CHAINMAIL_LEGGINGS", "IRON_LEGGINGS",
        "GOLDEN_LEGGINGS", "DIAMOND_LEGGINGS", "NETHERITE_LEGGINGS"))),
    FEET(new HashSet<>(List.of(
        "LEATHER_BOOTS", "CHAINMAIL_BOOTS", "IRON_BOOTS",
        "GOLDEN_BOOTS", "DIAMOND_BOOTS", "NETHERITE_BOOTS"
    )));

    public Set<String> items;

    WornItems(Set<String> items) {
      this.items = items;
    }
  }
}
