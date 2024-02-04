package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginItems;
import me.dannynguyen.aethel.systems.object.RpgPlayer;
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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * EquipmentAttributeListener is a collection of listeners for
 * events related to changing a player's equipment attributes.
 *
 * @author Danny Nguyen
 * @version 1.9.3
 * @since 1.9.0
 */
public class EquipmentAttributeListener implements Listener {
  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    Player player = e.getPlayer();
    if (PluginData.rpgData.getRpgPlayers().get(player) == null) {
      PluginData.rpgData.loadRpgPlayer(player);
    }
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    Player player = (Player) e.getWhoClicked();
    Inventory inv = e.getClickedInventory();

    if (inv != null && inv.getType().equals(InventoryType.PLAYER)) {
      if (e.getClick().isShiftClick() && ItemReader.isNotNullOrAir(e.getCurrentItem())) {
        updateIfWornItem(player, e.getCurrentItem(), "shift");
      } else {
        int slot = e.getSlot();
        switch (slot) {
          case 36, 37, 38, 39, 40 -> {
            if (ItemReader.isNotNullOrAir(e.getCursor()) ||
                ItemReader.isNotNullOrAir(e.getCurrentItem())) {
              updateEquipmentAttributesAtSlot(player, null, slot, "click");
            }
          }
        }
      }
    }
  }

  @EventHandler
  public void onSwapHandItem(PlayerSwapHandItemsEvent e) {
    RpgPlayer rpgPlayer = PluginData.rpgData.getRpgPlayers().get(e.getPlayer());
    PluginData.rpgData.readEquipmentSlot(
        rpgPlayer.getEquipmentAttributes(),
        rpgPlayer.getAethelAttributes(),
        e.getOffHandItem(), "off_hand");
  }

  @EventHandler
  public void onInteract(PlayerInteractEvent e) {
    Action action = e.getAction();

    if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
      if (ItemReader.isNotNullOrAir(e.getItem())) {
        updateIfWornItem(e.getPlayer(), e.getItem(), "interact");
      }
    }
  }

  @EventHandler
  public void onDispense(BlockDispenseArmorEvent e) {
    if (e.getTargetEntity() instanceof Player player) {
      updateIfWornItem(player, e.getItem(), "dispense");
    }
  }

  @EventHandler
  public void onBreak(PlayerItemBreakEvent e) {
    updateIfWornItem(e.getPlayer(), e.getBrokenItem(), "break");
  }

  @EventHandler
  public void onDeath(PlayerDeathEvent e) {
    if (!e.getKeepInventory()) {
      RpgPlayer rpgPlayer = PluginData.rpgData.getRpgPlayers().get(e.getEntity());
      Map<String, Map<String, Double>> equipment = rpgPlayer.getEquipmentAttributes();
      Map<String, Double> aethelAttributes = rpgPlayer.getAethelAttributes();

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

    if (PluginItems.WornItems.ALL.items.contains(itemType)) {
      if (PluginItems.WornItems.HEAD.items.contains(itemType)) {
        updateEquipmentAttributesAtSlot(player, item, 39, action);
      } else if (PluginItems.WornItems.CHEST.items.contains(itemType)) {
        updateEquipmentAttributesAtSlot(player, item, 38, action);
      } else if (PluginItems.WornItems.LEGS.items.contains(itemType)) {
        updateEquipmentAttributesAtSlot(player, item, 37, action);
      } else if (PluginItems.WornItems.FEET.items.contains(itemType)) {
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
        case 36 -> PluginData.rpgData.readEquipmentSlot(
            rpgPlayer.getEquipmentAttributes(),
            rpgPlayer.getAethelAttributes(),
            item, "feet");
        case 37 -> PluginData.rpgData.readEquipmentSlot(
            rpgPlayer.getEquipmentAttributes(),
            rpgPlayer.getAethelAttributes(),
            item, "legs");
        case 38 -> PluginData.rpgData.readEquipmentSlot(
            rpgPlayer.getEquipmentAttributes(),
            rpgPlayer.getAethelAttributes(),
            item, "chest");
        case 39 -> PluginData.rpgData.readEquipmentSlot(
            rpgPlayer.getEquipmentAttributes(),
            rpgPlayer.getAethelAttributes(),
            item, "head");
        case 40 -> PluginData.rpgData.readEquipmentSlot(
            rpgPlayer.getEquipmentAttributes(),
            rpgPlayer.getAethelAttributes(),
            item, "off_hand");
      }
    } else {
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
        RpgPlayer rpgPlayer = PluginData.rpgData.getRpgPlayers().get(player);
        final ItemStack wornItem = player.getInventory().getItem(slot);

        switch (slot) {
          case 36 -> PluginData.rpgData.readEquipmentSlot(
              rpgPlayer.getEquipmentAttributes(),
              rpgPlayer.getAethelAttributes(),
              wornItem, "feet");
          case 37 -> PluginData.rpgData.readEquipmentSlot(
              rpgPlayer.getEquipmentAttributes(),
              rpgPlayer.getAethelAttributes(),
              wornItem, "legs");
          case 38 -> PluginData.rpgData.readEquipmentSlot(
              rpgPlayer.getEquipmentAttributes(),
              rpgPlayer.getAethelAttributes(),
              wornItem, "chest");
          case 39 -> PluginData.rpgData.readEquipmentSlot(
              rpgPlayer.getEquipmentAttributes(),
              rpgPlayer.getAethelAttributes(),
              wornItem, "head");
          case 40 -> PluginData.rpgData.readEquipmentSlot(
              rpgPlayer.getEquipmentAttributes(),
              rpgPlayer.getAethelAttributes(),
              wornItem, "off_hand");
        }
      }, 1);
    }
  }
}
