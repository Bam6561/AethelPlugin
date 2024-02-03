package me.dannynguyen.aethel.commands.character;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginItems;
import me.dannynguyen.aethel.systems.object.RpgPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * CharacterInventoryListener is an inventory listener for the Character inventories.
 *
 * @author Danny Nguyen
 * @version 1.9.2
 * @since 1.9.2
 */
public class CharacterInventoryListener {
  /**
   * Checks if the user's action is allowed based on the clicked inventory.
   * <p>
   * Additional Parameters:
   * - CharacterSheet: prevent adding new items to the inventory outside of the intended equipment slots
   * - Player: prevent shift-clicks adding items to the CharacterSheet inventory
   * </p>
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void readMainClick(InventoryClickEvent e, Player user) {
    Inventory clickedInv = e.getClickedInventory();
    if (clickedInv != null && !clickedInv.getType().equals(InventoryType.PLAYER)) {
      if (e.getCurrentItem() != null) {
        interpretItemClick(e, user);
      } else {
        int slot = e.getSlot();
        switch (slot) {
          case 10, 11, 12, 19, 20, 28, 29, 37, 38 -> interpretEquipItem(e, user, slot);
          default -> e.setCancelled(true);
        }
      }
    } else {
      if (e.getClick().isShiftClick()) {
        e.setCancelled(true);
      }
    }
  }

  /**
   * Either:
   * - opens the quest inventory
   * - opens the collectibles inventory
   * - opens the settings inventory
   * - unequips an item from the user
   *
   * @param e    inventory click event
   * @param user user
   */
  private static void interpretItemClick(InventoryClickEvent e, Player user) {
    int slot = e.getSlot();
    switch (slot) {
      case 4, 9, 15, 24, 33, 42 -> e.setCancelled(true); // Player Head & Attributes
      case 25 -> e.setCancelled(true); // Quests
      case 34 -> e.setCancelled(true); // Collectibles
      case 43 -> e.setCancelled(true); // Settings
      case 10, 11, 12, 19, 28, 37 -> unequipItem(user, e.getClickedInventory(), slot);
    }
  }

  /**
   * Equips an item to the user if the item is valid for its corresponding slot.
   *
   * @param e    inventory click event
   * @param user user
   * @param slot slot type
   */
  private static void interpretEquipItem(InventoryClickEvent e, Player user, int slot) {
    ItemStack item = e.getCursor();
    String itemType = item.getType().name();

    if (slot == 11) {
      equipMainHandItem(e, user, e.getClickedInventory(), user.getInventory().getHeldItemSlot());
    } else {
      if (slot == 12) {
        updateAttributesIfShield(e, user, item, itemType);
      } else {
        if (PluginItems.WornItems.ALL.items.contains(itemType)) {
          switch (slot) {
            case 10, 19, 28, 37 -> equipIfValidArmorSlot(e, user, slot, item, itemType);
            case 20 -> {
              // Necklace
            }
            case 29, 38 -> {
              // Rings
            }
          }
        } else {
          e.setCancelled(true);
        }
      }
    }
  }

  /**
   * Removes an equipped item from the user.
   *
   * @param user user
   * @param menu CharacterSheet inventory
   * @param slot slot type
   */
  private static void unequipItem(Player user, Inventory menu, int slot) {
    switch (slot) {
      case 10 -> slot = 39;
      case 19 -> slot = 38;
      case 28 -> slot = 37;
      case 37 -> slot = 36;
      case 11 -> slot = user.getInventory().getHeldItemSlot();
      case 12 -> slot = 40;
    }
    int invSlot = slot;

    user.getInventory().setItem(invSlot, new ItemStack(Material.AIR));
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(),
        () -> updateAttributes(user, menu, invSlot), 1);
  }

  /**
   * Equips the item to the user's main hand.
   *
   * @param e    inventory click event
   * @param user user
   * @param menu character sheet inventory
   * @param slot main hand slot
   */
  private static void equipMainHandItem(InventoryClickEvent e, Player user,
                                        Inventory menu, int slot) {
    Inventory inv = user.getInventory();
    ItemStack item = e.getCursor();

    if (inv.getItem(slot) == null) {
      inv.setItem(slot, item);
      updateAttributes(user, menu, slot);
    } else if (inv.firstEmpty() != -1) {
      int emptySlot = inv.firstEmpty();
      inv.setItem(emptySlot, item);
      updateAttributes(user, menu, emptySlot);
    } else {
      if (item != null) {
        user.getWorld().dropItem(user.getLocation(), item);
      }
    }
  }

  /**
   * Update's the user's attribute if a shield is equipped to the off-hand.
   *
   * @param e        inventory click event
   * @param user     user
   * @param item     interacting item
   * @param itemType item type
   */
  private static void updateAttributesIfShield(InventoryClickEvent e, Player user, ItemStack item, String itemType) {
    user.getInventory().setItem(40, item);
    if (itemType.equals("SHIELD")) {
      updateAttributes(user, e.getClickedInventory(), 40);
    }
  }

  /**
   * Equips the armor piece to the user if it is valid for its corresponding slot.
   *
   * @param e        inventory click event
   * @param user     user
   * @param slot     slot type
   * @param item     interacting item
   * @param itemType item type
   */
  private static void equipIfValidArmorSlot(InventoryClickEvent e, Player user, int slot,
                                            ItemStack item, String itemType) {
    boolean validSlot = false;
    int armorSlot = 0;
    switch (slot) {
      case 10 -> {
        if (PluginItems.WornItems.HEAD.items.contains(itemType)) {
          validSlot = true;
          armorSlot = 39;
        }
      }
      case 19 -> {
        if (PluginItems.WornItems.CHEST.items.contains(itemType)) {
          validSlot = true;
          armorSlot = 38;
        }
      }
      case 28 -> {
        if (PluginItems.WornItems.LEGS.items.contains(itemType)) {
          validSlot = true;
          armorSlot = 37;
        }
      }
      case 37 -> {
        if (PluginItems.WornItems.FEET.items.contains(itemType)) {
          validSlot = true;
          armorSlot = 36;
        }
      }
    }

    if (validSlot) {
      user.getInventory().setItem(armorSlot, item);
      updateAttributes(user, e.getClickedInventory(), armorSlot);
    } else {
      e.setCancelled(true);
    }
  }

  /**
   * Updates the user's displayed attributes.
   * <p>
   * A 1 tick delay is used because only the item that exists in the
   * corresponding slot after the interaction happens should be read.
   * </p>
   *
   * @param user user
   * @param menu CharacterSheet inventory
   * @param slot user's item slot
   */
  private static void updateAttributes(Player user, Inventory menu, int slot) {
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
      RpgPlayer rpgPlayer = PluginData.rpgData.getRpgPlayers().get(user);
      ItemStack wornItem = user.getInventory().getItem(slot);

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
        default -> PluginData.rpgData.readEquipmentSlot(
            rpgPlayer.getEquipmentAttributes(),
            rpgPlayer.getAethelAttributes(),
            wornItem, "hand");
      }

      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(),
          () -> CharacterSheet.addAttributes(user, menu), 1);
    }, 1);
  }
}
