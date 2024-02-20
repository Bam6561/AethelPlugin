package me.dannynguyen.aethel.commands.character;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.systems.EquipmentSlot;
import me.dannynguyen.aethel.systems.MenuMeta;
import me.dannynguyen.aethel.systems.PlayerMeta;
import me.dannynguyen.aethel.systems.RpgProfile;
import me.dannynguyen.aethel.utility.ItemCreator;
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
 * <p>
 * 1 tick delays are used because only the item that exists in the
 * corresponding slot after the interaction happens should be read.
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.11.6
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
  public void interpretSheetClick() {
    if (ItemReader.isNotNullOrAir(e.getCurrentItem())) {
      switch (slotClicked) {
        case 4, 9, 15, 24, 33, 42 -> { // Player Head & Attributes
        }
        case 25 -> { // Quests
        }
        case 34 -> { // Collectibles
        }
        case 43 -> openSettings();
        case 10, 11, 12, 19, 28, 37 -> {
          e.setCancelled(false);
          unequipArmorHands(); // Armor & Hands
        }
        case 20, 29 -> {
          e.setCancelled(false);
          updateJewelryAttributes(); // Necklace & Ring
        }
      }
    } else {
      switch (slotClicked) {
        case 10, 11, 12, 19, 20, 28, 29, 37 -> {
          e.setCancelled(false);
          interpretEquipItem();
        }
      }
    }
  }

  /**
   * Toggles the player's health bar.
   */
  public void interpretSettingsClick() {
    if (ItemReader.isNotNullOrAir(e.getCurrentItem())) {
      switch (slotClicked) {
        case 4 -> { // Player Head
        }
        case 6 -> returnToSheet();
        case 9 -> toggleHealthBar();
      }
    }
  }

  /**
   * Updates the user's main hand item in the menu when
   * it is interacted with from the user's inventory.
   */
  public void interpretPlayerInventoryClick() {
    if (e.getSlot() == user.getInventory().getHeldItemSlot()) {
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
        e.getInventory().setItem(11, user.getInventory().getItem(e.getSlot()));
        Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
          RpgProfile rpgProfile = PluginData.rpgSystem.getRpgProfiles().get(user);
          ItemStack item = user.getInventory().getItem(user.getInventory().getHeldItemSlot());
          rpgProfile.readEquipmentSlot(item, EquipmentSlot.HAND);
          Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> new SheetMenu(user, e.getInventory()).addAttributes(), 1);
        }, 1);
      }, 1);
    }
  }

  /**
   * Opens a Settings menu.
   */
  private void openSettings() {
    user.openInventory(new SettingsMenu(user).openMenu());
    PluginData.pluginSystem.getPlayerMetadata().get(user).put(PlayerMeta.INVENTORY, MenuMeta.CHARACTER_SETTINGS.getMeta());
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
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
      user.getInventory().setItem(invSlot, e.getInventory().getItem(slotClicked));
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> updateArmorHandsAttributes(invSlot), 1);
    }, 1);
  }

  /**
   * Equips an item to the user.
   */
  private void interpretEquipItem() {
    if (ItemReader.isNotNullOrAir(e.getCursor())) {
      switch (slotClicked) {
        case 11 -> equipMainHandItem();
        case 10, 12, 19, 28, 37 -> equipOffHandArmorItem();
        case 20, 29 -> updateJewelryAttributes();
      }
    }
  }

  /**
   * Equips the item to the user's main hand.
   */
  private void equipMainHandItem() {
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
      PlayerInventory pInv = user.getInventory();
      int slot = pInv.getHeldItemSlot();
      ItemStack item = e.getInventory().getItem(11);

      if (pInv.getItem(slot) == null) { // Main hand slot is empty
        pInv.setItem(slot, item);
        updateArmorHandsAttributes(slot);
      } else if (pInv.firstEmpty() != -1) { // Main hand slot is full
        user.setItemOnCursor(null);
        pInv.setItem(pInv.firstEmpty(), item);
        user.sendMessage(ChatColor.RED + "Main hand occupied.");
      } else if (ItemReader.isNotNullOrAir(item)) { // Inventory is full
        user.setItemOnCursor(null);
        user.getWorld().dropItem(user.getLocation(), item);
        user.sendMessage(ChatColor.RED + "Inventory full.");
      }
    }, 1);
  }


  /**
   * Equips the item to the user's off hand or armor slot.
   */
  private void equipOffHandArmorItem() {
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
      int slot;
      switch (slotClicked) {
        case 12 -> slot = 40;
        case 10 -> slot = 39;
        case 19 -> slot = 38;
        case 28 -> slot = 37;
        case 37 -> slot = 36;
        default -> slot = -1;
      }
      user.getInventory().setItem(slot, e.getInventory().getItem(slotClicked));
      updateArmorHandsAttributes(slot);
    }, 1);
  }

  /**
   * Updates the user's displayed attributes for the armor and main hand slots.
   *
   * @param slot user's item slot
   */
  private void updateArmorHandsAttributes(int slot) {
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
      RpgProfile rpgProfile = PluginData.rpgSystem.getRpgProfiles().get(user);
      ItemStack wornItem = user.getInventory().getItem(slot);

      switch (slot) {
        case 39 -> rpgProfile.readEquipmentSlot(wornItem, EquipmentSlot.HEAD);
        case 38 -> rpgProfile.readEquipmentSlot(wornItem, EquipmentSlot.CHEST);
        case 37 -> rpgProfile.readEquipmentSlot(wornItem, EquipmentSlot.LEGS);
        case 36 -> rpgProfile.readEquipmentSlot(wornItem, EquipmentSlot.FEET);
        case 40 -> rpgProfile.readEquipmentSlot(wornItem, EquipmentSlot.OFF_HAND);
        default -> rpgProfile.readEquipmentSlot(wornItem, EquipmentSlot.HAND);
      }
      PluginData.rpgSystem.getRpgProfiles().get(user).updateHealthBar();
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> new SheetMenu(user, e.getClickedInventory()).addAttributes(), 1);
    }, 1);
  }

  /**
   * Updates the user's displayed attributes for the jewelry slots.
   */
  private void updateJewelryAttributes() {
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
      RpgProfile rpgProfile = PluginData.rpgSystem.getRpgProfiles().get(user);
      Inventory menu = e.getClickedInventory();
      ItemStack wornItem = menu.getItem(slotClicked);

      switch (slotClicked) {
        case 20 -> {
          rpgProfile.getJewelrySlots()[0] = wornItem;
          rpgProfile.readEquipmentSlot(wornItem, EquipmentSlot.NECKLACE);
        }
        case 29 -> {
          rpgProfile.getJewelrySlots()[1] = wornItem;
          rpgProfile.readEquipmentSlot(wornItem, EquipmentSlot.RING);
        }
      }
      PluginData.rpgSystem.getRpgProfiles().get(user).updateHealthBar();
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> new SheetMenu(user, menu).addAttributes(), 1);
    }, 1);
  }

  /**
   * Returns to the Sheet menu.
   */
  private void returnToSheet() {
    user.openInventory(new SheetMenu(user, user).openMenu());
    PluginData.pluginSystem.getPlayerMetadata().get(user).put(PlayerMeta.INVENTORY, MenuMeta.CHARACTER_SHEET.getMeta());
  }

  /**
   * Toggles the player's health bar.
   */
  private void toggleHealthBar() {
    Inventory menu = e.getInventory();
    if (PluginData.rpgSystem.getRpgProfiles().get(user).getHealthBar().isVisible()) {
      menu.setItem(9, ItemCreator.createItem(Material.RED_WOOL, ChatColor.WHITE + "Display Health Bar"));
      user.sendMessage(ChatColor.RED + "[Display Health Bar]");
    } else {
      menu.setItem(9, ItemCreator.createItem(Material.LIME_WOOL, ChatColor.WHITE + "Display Health Bar"));
      user.sendMessage(ChatColor.GREEN + "[Display Health Bar]");
    }
    PluginData.rpgSystem.getRpgProfiles().get(user).toggleHealthBar();
  }
}
