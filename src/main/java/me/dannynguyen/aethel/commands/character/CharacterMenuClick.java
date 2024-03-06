package me.dannynguyen.aethel.commands.character;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.systems.plugin.MenuMeta;
import me.dannynguyen.aethel.systems.plugin.PlayerMeta;
import me.dannynguyen.aethel.systems.rpg.RpgEquipment;
import me.dannynguyen.aethel.systems.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.systems.rpg.RpgHealth;
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
import java.util.UUID;

/**
 * Inventory click event listener for Character menus.
 * <p>
 * 1 tick delays are used because only the item that exists in the
 * corresponding slot after the interaction happens should be read.
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.14.5
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
   * User's UUID.
   */
  private final UUID userUUID;

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
    this.userUUID = user.getUniqueId();
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
        case 25 -> openQuests();
        case 34 -> openCollectibles();
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
   * Views the player's quests.
   */
  public void interpretQuestsClick() {
    if (ItemReader.isNotNullOrAir(e.getCurrentItem())) {
      switch (slotClicked) {
        case 4 -> { // Player Head
        }
        case 6 -> returnToSheet();
      }
    }
  }

  /**
   * Views the player's collectibles.
   */
  public void interpretCollectiblesClick() {
    if (ItemReader.isNotNullOrAir(e.getCurrentItem())) {
      switch (slotClicked) {
        case 4 -> { // Player Head
        }
        case 6 -> returnToSheet();
      }
    }
  }

  /**
   * Toggles the player's settings.
   */
  public void interpretSettingsClick() {
    if (ItemReader.isNotNullOrAir(e.getCurrentItem())) {
      switch (slotClicked) {
        case 4 -> { // Player Head
        }
        case 6 -> returnToSheet();
        case 9 -> toggleHealthBar();
        case 10 -> toggleHealthAction();
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
          ItemStack item = user.getInventory().getItem(user.getInventory().getHeldItemSlot());
          Plugin.getData().getRpgSystem().getRpgPlayers().get(userUUID).getEquipment().readSlot(item, RpgEquipmentSlot.HAND, true);
          Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), new SheetMenu(user, e.getInventory())::addAttributes, 3);
        }, 1);
      }, 1);
    }
  }

  /**
   * Opens a Quests menu.
   */
  private void openQuests() {
    user.openInventory(new QuestsMenu(user).openMenu());
    Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).put(PlayerMeta.INVENTORY, MenuMeta.CHARACTER_QUESTS.getMeta());
  }

  /**
   * Opens a Collectibles menu.
   */
  private void openCollectibles() {
    user.openInventory(new CollectiblesMenu(user).openMenu());
    Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).put(PlayerMeta.INVENTORY, MenuMeta.CHARACTER_COLLECTIBLES.getMeta());
  }

  /**
   * Opens a Settings menu.
   */
  private void openSettings() {
    user.openInventory(new SettingsMenu(user).openMenu());
    Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).put(PlayerMeta.INVENTORY, MenuMeta.CHARACTER_SETTINGS.getMeta());
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
      RpgEquipment rpgEquipment = Plugin.getData().getRpgSystem().getRpgPlayers().get(userUUID).getEquipment();
      ItemStack wornItem = user.getInventory().getItem(slot);
      switch (slot) {
        case 39 -> rpgEquipment.readSlot(wornItem, RpgEquipmentSlot.HEAD, true);
        case 38 -> rpgEquipment.readSlot(wornItem, RpgEquipmentSlot.CHEST, true);
        case 37 -> rpgEquipment.readSlot(wornItem, RpgEquipmentSlot.LEGS, true);
        case 36 -> rpgEquipment.readSlot(wornItem, RpgEquipmentSlot.FEET, true);
        case 40 -> rpgEquipment.readSlot(wornItem, RpgEquipmentSlot.OFF_HAND, true);
        default -> rpgEquipment.readSlot(wornItem, RpgEquipmentSlot.HAND, true);
      }
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), new SheetMenu(user, e.getClickedInventory())::addAttributes, 3);
    }, 1);
  }

  /**
   * Updates the user's displayed attributes for the jewelry slots.
   */
  private void updateJewelryAttributes() {
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
      RpgEquipment rpgEquipment = Plugin.getData().getRpgSystem().getRpgPlayers().get(userUUID).getEquipment();
      Inventory menu = e.getClickedInventory();
      ItemStack wornItem = menu.getItem(slotClicked);
      switch (slotClicked) {
        case 20 -> {
          rpgEquipment.getJewelry()[0] = wornItem;
          rpgEquipment.readSlot(wornItem, RpgEquipmentSlot.NECKLACE, true);
        }
        case 29 -> {
          rpgEquipment.getJewelry()[1] = wornItem;
          rpgEquipment.readSlot(wornItem, RpgEquipmentSlot.RING, true);
        }
      }
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), new SheetMenu(user, menu)::addAttributes, 3);
    }, 1);
  }

  /**
   * Returns to the Sheet menu.
   */
  private void returnToSheet() {
    user.openInventory(new SheetMenu(user, user).openMenu());
    Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).put(PlayerMeta.INVENTORY, MenuMeta.CHARACTER_SHEET.getMeta());
  }

  /**
   * Toggles the player's health bar.
   */
  private void toggleHealthBar() {
    RpgHealth rpgHealth = Plugin.getData().getRpgSystem().getRpgPlayers().get(userUUID).getHealth();
    Inventory menu = e.getInventory();
    if (rpgHealth.getBar().isVisible()) {
      menu.setItem(9, ItemCreator.createItem(Material.RED_WOOL, ChatColor.AQUA + "Display Health Bar"));
      user.sendMessage(ChatColor.RED + "[Display Health Boss Bar]");
    } else {
      menu.setItem(9, ItemCreator.createItem(Material.LIME_WOOL, ChatColor.AQUA + "Display Health Bar"));
      user.sendMessage(ChatColor.GREEN + "[Display Health Boss Bar]");
    }
    rpgHealth.toggleBarVisibility();
  }

  /**
   * Toggles the player's health in action bar.
   */
  private void toggleHealthAction() {
    RpgHealth rpgHealth = Plugin.getData().getRpgSystem().getRpgPlayers().get(userUUID).getHealth();
    Inventory menu = e.getInventory();
    if (rpgHealth.isHealthActionVisible()) {
      menu.setItem(10, ItemCreator.createItem(Material.RED_WOOL, ChatColor.AQUA + "Display Health Action Bar"));
      user.sendMessage(ChatColor.RED + "[Display Health Action Bar]");
    } else {
      menu.setItem(10, ItemCreator.createItem(Material.LIME_WOOL, ChatColor.AQUA + "Display Health Action Bar"));
      user.sendMessage(ChatColor.GREEN + "[Display Health Action Bar]");
    }
    rpgHealth.toggleActionVisibility();
  }
}
