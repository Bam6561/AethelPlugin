package me.dannynguyen.aethel.commands.character;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.systems.RpgProfile;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Inventory click event listener for Character menus.
 *
 * @author Danny Nguyen
 * @version 1.9.23
 * @since 1.9.2
 */
public class CharacterMenuClick {
  /**
   * Inventory click event.
   */
  private final InventoryClickEvent e;

  /**
   * Player who clicked.
   */
  private final Player user;

  /**
   * Slot clicked.
   */
  private final int slotClicked;

  /**
   * Associates an inventory click event with its user in the context of an open Character menu.
   *
   * @param e inventory click event
   */
  public CharacterMenuClick(@NotNull InventoryClickEvent e) {
    this.e = Objects.requireNonNull(e, "Null inventory click event");
    this.user = (Player) e.getWhoClicked();
    this.slotClicked = e.getSlot();
  }

  /**
   * Checks if the user is interacting with an item/button or attempting to wear equipment.
   */
  public void interpretCharacterSheetClick() {
    if (ItemReader.isNotNullOrAir(e.getCurrentItem())) {
      switch (slotClicked) {
        case 4, 9, 15, 24, 33, 42 -> e.setCancelled(true); // Player Head & Attributes
        case 25 -> e.setCancelled(true); // Quests
        case 34 -> e.setCancelled(true); // Collectibles
        case 43 -> e.setCancelled(true); // Settings
        case 10, 11, 12, 19, 28, 37 -> unequipArmorHands(); // Armor & Hands
        case 20, 29 -> updateJewelryAttributes(); // Necklace & Ring
      }
    } else {
      switch (slotClicked) {
        case 10, 11, 12, 19, 20, 28, 29, 37 -> interpretEquipItem();
        default -> e.setCancelled(true);
      }
    }
  }

  /**
   * Disables shift clicks and updates the user's main hand item in
   * the menu when it is interacted with from the user's inventory.
   */
  public void interpretPlayerInventoryClick() {
    if (e.getClick().isShiftClick()) {
      e.setCancelled(true);

      // User removes main hand item
    } else if (ItemReader.isNotNullOrAir(e.getCurrentItem()) && (e.getCurrentItem().equals(e.getClickedInventory().getItem(e.getSlot())))) {
      RpgProfile rpgProfile = PluginData.rpgSystem.getRpgProfiles().get(user);
      rpgProfile.readEquipmentSlot(null, "hand");

      e.getInventory().setItem(11, new ItemStack(Material.AIR));
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> new CharacterSheet(user, e.getInventory()).addAttributes(), 1);

      // User sets new main hand item
    } else if (e.getSlot() == user.getInventory().getHeldItemSlot()) {
      e.getInventory().setItem(11, e.getCursor());

      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
        RpgProfile rpgProfile = PluginData.rpgSystem.getRpgProfiles().get(user);
        ItemStack wornItem = user.getInventory().getItem(user.getInventory().getHeldItemSlot());
        rpgProfile.readEquipmentSlot(wornItem, "hand");
        Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> new CharacterSheet(user, e.getInventory()).addAttributes(), 1);
      }, 1);
    }
  }

  /**
   * Removes an equipped armor or hand item from the user.
   */
  private void unequipArmorHands() {
    int invSlot;
    switch (slotClicked) {
      case 10 -> invSlot = 39;
      case 11 -> invSlot = user.getInventory().getHeldItemSlot();
      case 12 -> invSlot = 40;
      case 19 -> invSlot = 38;
      case 28 -> invSlot = 37;
      case 37 -> invSlot = 36;
      default -> invSlot = -1; // Unreachable
    }
    user.getInventory().setItem(invSlot, new ItemStack(Material.AIR));
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> updateArmorHandsAttributes(invSlot), 1);
  }

  /**
   * Equips an item to the user.
   */
  private void interpretEquipItem() {
    ItemStack item = e.getCursor();
    if (item.getType() != Material.AIR) {
      switch (slotClicked) {
        case 11 -> equipMainHandItem();
        case 12 -> {
          user.getInventory().setItem(40, item);
          updateArmorHandsAttributes(40);
        }
        case 10 -> {
          user.getInventory().setItem(39, item);
          updateArmorHandsAttributes(39);
        }
        case 19 -> {
          user.getInventory().setItem(38, item);
          updateArmorHandsAttributes(38);
        }
        case 28 -> {
          user.getInventory().setItem(37, item);
          updateArmorHandsAttributes(37);
        }
        case 37 -> {
          user.getInventory().setItem(36, item);
          updateArmorHandsAttributes(36);
        }
        case 20, 29 -> updateJewelryAttributes();
      }
    }
  }

  /**
   * Equips the item to the user's main hand.
   */
  private void equipMainHandItem() {
    PlayerInventory pInv = user.getInventory();
    int slot = pInv.getHeldItemSlot();
    ItemStack item = e.getCursor();

    if (pInv.getItem(slot) == null) { // Main hand slot is empty
      pInv.setItem(slot, item);
      updateArmorHandsAttributes(slot);
    } else if (pInv.firstEmpty() != -1) { // Main hand slot is full
      user.setItemOnCursor(new ItemStack(Material.AIR));
      pInv.setItem(pInv.firstEmpty(), item);
      user.sendMessage(ChatColor.RED + "Main hand occupied.");
      e.setCancelled(true);
    } else if (ItemReader.isNotNullOrAir(item)) { // Inventory is full
      user.setItemOnCursor(new ItemStack(Material.AIR));
      user.getWorld().dropItem(user.getLocation(), item);
      user.sendMessage(ChatColor.RED + "Inventory full.");
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
   * @param slot user's item slot
   */
  private void updateArmorHandsAttributes(int slot) {
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
      RpgProfile rpgProfile = PluginData.rpgSystem.getRpgProfiles().get(user);
      ItemStack wornItem = user.getInventory().getItem(slot);

      switch (slot) {
        case 39 -> rpgProfile.readEquipmentSlot(wornItem, "head");
        case 38 -> rpgProfile.readEquipmentSlot(wornItem, "chest");
        case 37 -> rpgProfile.readEquipmentSlot(wornItem, "legs");
        case 36 -> rpgProfile.readEquipmentSlot(wornItem, "feet");
        case 40 -> rpgProfile.readEquipmentSlot(wornItem, "off_hand");
        default -> rpgProfile.readEquipmentSlot(wornItem, "hand");
      }
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> new CharacterSheet(user, e.getClickedInventory()).addAttributes(), 1);
    }, 1);
  }

  /**
   * Updates the user's displayed attributes for the jewelry slots.
   * <p>
   * A 1 tick delay is used because only the item that exists in the
   * corresponding slot after the interaction happens should be read.
   * </p>
   */
  private void updateJewelryAttributes() {
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
      RpgProfile rpgProfile = PluginData.rpgSystem.getRpgProfiles().get(user);
      Inventory menu = e.getClickedInventory();
      ItemStack wornItem = menu.getItem(slotClicked);

      switch (slotClicked) {
        case 20 -> {
          rpgProfile.getJewelrySlots()[0] = wornItem;
          rpgProfile.readEquipmentSlot(wornItem, "necklace");
        }
        case 29 -> {
          rpgProfile.getJewelrySlots()[1] = wornItem;
          rpgProfile.readEquipmentSlot(wornItem, "ring");
        }
      }
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> new CharacterSheet(user, menu).addAttributes(), 1);
    }, 1);
  }
}
