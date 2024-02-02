package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.systems.object.RpgCharacter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * EquipmentAttributeListener is a collection of listeners for
 * events related to changing a player's equipment attributes.
 *
 * @author Danny Nguyen
 * @version 1.9.0
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
    Inventory clickedInv = e.getClickedInventory();
    if (clickedInv != null && clickedInv.getType().equals(InventoryType.PLAYER)) {
      int slot = e.getSlot();
      switch (slot) {
        case 36, 37, 38, 39, 40 -> updateEquipmentAttributesAtSlot(e, slot);
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

  /**
   * Updates the player's equipment attributes at the slot they interacted with.
   *
   * @param e    inventory click event
   * @param slot slot type
   */
  private void updateEquipmentAttributesAtSlot(InventoryClickEvent e, int slot) {
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
      RpgCharacter rpgCharacter = PluginData.rpgData.getRpgCharacters().get(e.getWhoClicked());
      ItemStack item = e.getClickedInventory().getItem(slot);

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
}
