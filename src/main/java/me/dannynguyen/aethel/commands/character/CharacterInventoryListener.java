package me.dannynguyen.aethel.commands.character;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginItems;
import me.dannynguyen.aethel.systems.object.RpgPlayer;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * CharacterInventoryListener is an inventory listener for the Character inventories.
 *
 * @author Danny Nguyen
 * @version 1.9.3
 * @since 1.9.2
 */
public class CharacterInventoryListener {
  /**
   * Checks if the user's action is allowed based on the clicked inventory.
   * <p>
   * Additional Parameters:
   * - CharacterSheet: prevent adding new items to the
   * inventory outside of the intended equipment slots
   * - Player: prevent shift-clicks adding items to the CharacterSheet inventory
   * and remove the main hand item from the menu when the user clicks on it
   * </p>
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void readMainClick(InventoryClickEvent e, Player user) {
    if (!e.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) { // Prevents duplication
      Inventory clickedInv = e.getClickedInventory();
      if (clickedInv != null && !clickedInv.getType().equals(InventoryType.PLAYER)) {
        if (ItemReader.isNotNullOrAir(e.getCurrentItem())) {
          interpretItemClick(e, user);
        } else {
          int slot = e.getSlot();
          switch (slot) {
            case 10, 11, 12, 19, 20, 28, 29, 37 -> interpretEquipItem(e, user, slot);
            default -> e.setCancelled(true);
          }
        }
      } else {
        if (e.getClick().isShiftClick()) {
          e.setCancelled(true);
        } else if (ItemReader.isNotNullOrAir(e.getCurrentItem()) &&
            (e.getCurrentItem().equals(e.getClickedInventory().getItem(e.getSlot())))) {
          e.getInventory().setItem(11, new ItemStack(Material.AIR));
        } else if (e.getSlot() == user.getInventory().getHeldItemSlot()) {
          e.getInventory().setItem(11, e.getCursor());
        }
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
      case 10, 11, 12, 19, 28, 37 -> unequipItem(user, e.getClickedInventory(), slot); // Armor & Hands
      case 20, 29 -> unequipJewelryItem(user, e.getClickedInventory(), slot); // Necklace & Ring
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

    if (!itemType.equals("AIR")) {
      switch (slot) {
        case 11 -> equipMainHandItem(e, user, e.getClickedInventory(), user.getInventory().getHeldItemSlot());
        case 12 -> updateOffHandAttributes(e, user, item);
        default -> {
          switch (slot) {
            case 10 -> equipIfValidHeadItem(e, user, item, itemType);
            case 19 -> equipIfValidChestItem(e, user, item, itemType);
            case 28 -> equipIfValidLegsItem(e, user, item, itemType);
            case 37 -> equipIfValidFeetItem(e, user, item, itemType);
            case 20 -> equipIfValidNecklaceItem(e, user, itemType);
            case 29 -> equipIfValidRingItem(e, user, itemType, slot);
          }
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
        () -> updateArmorHandAttributes(user, menu, invSlot), 1);
  }

  /**
   * Removes a jewelry item from the user.
   *
   * @param user user
   * @param menu CharacterSheet inventory
   * @param slot slot type
   */
  private static void unequipJewelryItem(Player user, Inventory menu, int slot) {
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(),
        () -> updateJewelryAttributes(user, menu, slot), 1);
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
      updateArmorHandAttributes(user, menu, slot);
    } else if (inv.firstEmpty() != -1) {
      int emptySlot = inv.firstEmpty();
      inv.setItem(emptySlot, item);
      updateArmorHandAttributes(user, menu, emptySlot);
    } else if (ItemReader.isNotNullOrAir(item)) {
      user.getWorld().dropItem(user.getLocation(), item);

      RpgPlayer rpgPlayer = PluginData.rpgData.getRpgPlayers().get(user);
      PluginData.rpgData.readEquipmentSlot(
          rpgPlayer.getEquipmentAttributes(),
          rpgPlayer.getAethelAttributes(),
          null, "hand");
    }
  }

  /**
   * Update's the user's offhand attributes.
   *
   * @param e    inventory click event
   * @param user user
   * @param item interacting item
   */
  private static void updateOffHandAttributes(InventoryClickEvent e, Player user, ItemStack item) {
    user.getInventory().setItem(40, item);
    updateArmorHandAttributes(user, e.getClickedInventory(), 40);
  }

  /**
   * Equips the head item to the user if it is valid for its corresponding slot.
   *
   * @param e        inventory click event
   * @param user     user
   * @param item     interacting item
   * @param itemType item type
   */
  private static void equipIfValidHeadItem(InventoryClickEvent e, Player user,
                                           ItemStack item, String itemType) {
    if (PluginItems.WornItems.HEAD.items.contains(itemType)) {
      user.getInventory().setItem(39, item);
      updateArmorHandAttributes(user, e.getClickedInventory(), 39);
    } else {
      user.sendMessage(Failure.UNABLE_TO_EQUIP_HEAD.message);
      e.setCancelled(true);
    }
  }

  /**
   * Equips the chest item to the user if it is valid for its corresponding slot.
   *
   * @param e        inventory click event
   * @param user     user
   * @param item     interacting item
   * @param itemType item type
   */
  private static void equipIfValidChestItem(InventoryClickEvent e, Player user,
                                            ItemStack item, String itemType) {
    if (PluginItems.WornItems.CHEST.items.contains(itemType)) {
      user.getInventory().setItem(38, item);
      updateArmorHandAttributes(user, e.getClickedInventory(), 38);
    } else {
      user.sendMessage(Failure.UNABLE_TO_EQUIP_CHEST.message);
      e.setCancelled(true);
    }
  }

  /**
   * Equips the legs item to the user if it is valid for its corresponding slot.
   *
   * @param e        inventory click event
   * @param user     user
   * @param item     interacting item
   * @param itemType item type
   */
  private static void equipIfValidLegsItem(InventoryClickEvent e, Player user,
                                           ItemStack item, String itemType) {
    if (PluginItems.WornItems.LEGS.items.contains(itemType)) {
      user.getInventory().setItem(37, item);
      updateArmorHandAttributes(user, e.getClickedInventory(), 37);
    } else {
      user.sendMessage(Failure.UNABLE_TO_EQUIP_LEGS.message);
      e.setCancelled(true);
    }
  }

  /**
   * Equips the feet item to the user if it is valid for its corresponding slot.
   *
   * @param e        inventory click event
   * @param user     user
   * @param item     interacting item
   * @param itemType item type
   */
  private static void equipIfValidFeetItem(InventoryClickEvent e, Player user,
                                           ItemStack item, String itemType) {
    if (PluginItems.WornItems.FEET.items.contains(itemType)) {
      user.getInventory().setItem(36, item);
      updateArmorHandAttributes(user, e.getClickedInventory(), 36);
    } else {
      user.sendMessage(Failure.UNABLE_TO_EQUIP_FEET.message);
      e.setCancelled(true);
    }
  }

  /**
   * Equips the necklace to the user if it is valid for its corresponding slot.
   *
   * @param e        inventory click event
   * @param user     user
   * @param itemType item type
   */
  private static void equipIfValidNecklaceItem(InventoryClickEvent e, Player user, String itemType) {
    if (itemType.equals("IRON_NUGGET")) {
      updateJewelryAttributes(user, e.getClickedInventory(), 20);
    } else {
      user.sendMessage(Failure.UNABLE_TO_EQUIP_NECKLACE.message);
      e.setCancelled(true);
    }
  }

  /**
   * Equips the ring to the user if it is valid for its corresponding slot.
   *
   * @param e        inventory click event
   * @param user     user
   * @param itemType item type
   * @param slot     slot type
   */
  private static void equipIfValidRingItem(InventoryClickEvent e, Player user, String itemType, int slot) {
    if (itemType.equals("GOLD_NUGGET")) {
      updateJewelryAttributes(user, e.getClickedInventory(), slot);
    } else {
      user.sendMessage(Failure.UNABLE_TO_EQUIP_RING.message);
      e.setCancelled(true);
    }
  }

  /**
   * Updates the user's displayed attributes for the armor and main hand slots.
   * <p>
   * A 1 tick delay is used because only the item that exists in the
   * corresponding slot after the interaction happens should be read.
   * </p>
   *
   * @param user user
   * @param menu CharacterSheet inventory
   * @param slot user's item slot
   */
  private static void updateArmorHandAttributes(Player user, Inventory menu, int slot) {
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

  /**
   * Updates the user's displayed attributes for the jewelry slots.
   * <p>
   * A 1 tick delay is used because only the item that exists in the
   * corresponding slot after the interaction happens should be read.
   * </p>
   *
   * @param user user
   * @param menu CharacterSheet inventory
   * @param slot user's item slot
   */
  private static void updateJewelryAttributes(Player user, Inventory menu, int slot) {
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
      RpgPlayer rpgPlayer = PluginData.rpgData.getRpgPlayers().get(user);
      ItemStack wornItem = menu.getItem(slot);

      switch (slot) {
        case 20 -> PluginData.rpgData.readEquipmentSlot(
            rpgPlayer.getEquipmentAttributes(),
            rpgPlayer.getAethelAttributes(),
            wornItem, "necklace");
        case 29 -> PluginData.rpgData.readEquipmentSlot(
            rpgPlayer.getEquipmentAttributes(),
            rpgPlayer.getAethelAttributes(),
            wornItem, "ring");
      }

      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(),
          () -> CharacterSheet.addAttributes(user, menu), 1);
    }, 1);
  }

  private enum Failure {
    UNABLE_TO_EQUIP_HEAD(ChatColor.RED + "Unable to equip item to head slot."),
    UNABLE_TO_EQUIP_CHEST(ChatColor.RED + "Unable to equip item to chest slot."),
    UNABLE_TO_EQUIP_LEGS(ChatColor.RED + "Unable to equip item to legs slot."),
    UNABLE_TO_EQUIP_FEET(ChatColor.RED + "Unable to equip item to feet slot."),
    UNABLE_TO_EQUIP_NECKLACE(ChatColor.RED + "Unable to equip item to necklace slot."),
    UNABLE_TO_EQUIP_RING(ChatColor.RED + "Unable to equip item to ring slot.");

    public String message;

    Failure(String message) {
      this.message = message;
    }
  }
}
