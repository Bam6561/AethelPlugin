package me.dannynguyen.aethel.commands.character;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.plugin.enums.Message;
import me.dannynguyen.aethel.plugin.interfaces.MenuClick;
import me.dannynguyen.aethel.listeners.MenuEvent;
import me.dannynguyen.aethel.plugin.system.PluginPlayer;
import me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot;
import me.dannynguyen.aethel.listeners.ActionEvent;
import me.dannynguyen.aethel.rpg.system.Equipment;
import me.dannynguyen.aethel.rpg.system.Settings;
import me.dannynguyen.aethel.util.ItemCreator;
import me.dannynguyen.aethel.util.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Inventory click event listener for {@link CharacterCommand} menus.
 * <p>
 * 1 tick delays are used because only the item that exists in the
 * corresponding slot after the interaction happens should be read.
 * <p>
 * Called through {@link MenuEvent}.
 *
 * @author Danny Nguyen
 * @version 1.17.19
 * @since 1.9.2
 */
public class CharacterMenuClick implements MenuClick {
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
  private final UUID uuid;

  /**
   * Slot clicked.
   */
  private final int slot;

  /**
   * Associates an inventory click event with its user in the context of an open {@link CharacterCommand} menu.
   *
   * @param e inventory click event
   */
  public CharacterMenuClick(@NotNull InventoryClickEvent e) {
    this.e = Objects.requireNonNull(e, "Null inventory click event");
    this.user = (Player) e.getWhoClicked();
    this.uuid = user.getUniqueId();
    this.slot = e.getSlot();
  }

  /**
   * Checks if the user is interacting with an item/button or attempting to wear {@link Equipment}.
   */
  public void interpretMenuClick() {
    if (ItemReader.isNotNullOrAir(e.getCurrentItem())) {
      switch (slot) {
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
      switch (slot) {
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
      switch (slot) {
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
      switch (slot) {
        case 4 -> { // Player Head
        }
        case 6 -> returnToSheet();
      }
    }
  }

  /**
   * Toggles the player's {@link Settings}.
   */
  public void interpretSettingsClick() {
    if (ItemReader.isNotNullOrAir(e.getCurrentItem())) {
      switch (slot) {
        case 4 -> { // Player Head
        }
        case 6 -> returnToSheet();
        case 9 -> resetActiveAbilityCrouchBinds();
        case 10 -> setActiveAbilityCrouchBind(RpgEquipmentSlot.HAND);
        case 11 -> setActiveAbilityCrouchBind(RpgEquipmentSlot.OFF_HAND);
        case 12 -> setActiveAbilityCrouchBind(RpgEquipmentSlot.HEAD);
        case 13 -> setActiveAbilityCrouchBind(RpgEquipmentSlot.CHEST);
        case 14 -> setActiveAbilityCrouchBind(RpgEquipmentSlot.LEGS);
        case 15 -> setActiveAbilityCrouchBind(RpgEquipmentSlot.FEET);
        case 16 -> setActiveAbilityCrouchBind(RpgEquipmentSlot.NECKLACE);
        case 17 -> setActiveAbilityCrouchBind(RpgEquipmentSlot.RING);
        case 18 -> toggleHealthBar();
        case 19 -> toggleHealthAction();
      }
    }
  }

  /**
   * Updates the user's main hand item in the {@link SheetMenu}
   * when it is interacted with from the user's inventory.
   */
  public void interpretPlayerInventoryClick() {
    if (e.getSlot() == user.getInventory().getHeldItemSlot()) {
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
        e.getInventory().setItem(11, user.getInventory().getItem(e.getSlot()));
        Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
          ItemStack item = user.getInventory().getItem(user.getInventory().getHeldItemSlot());
          Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getEquipment().readSlot(item, RpgEquipmentSlot.HAND, true);
          Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), new SheetMenu(user, e.getInventory())::addAttributes, 3);
        }, 1);
      }, 1);
    }
  }

  /**
   * Opens a {@link QuestsMenu}.
   */
  private void openQuests() {
    user.openInventory(new QuestsMenu(user).getMainMenu());
    Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).setMenu(MenuEvent.Menu.CHARACTER_QUESTS);
  }

  /**
   * Opens a {@link CollectiblesMenu}.
   */
  private void openCollectibles() {
    user.openInventory(new CollectiblesMenu(user).getMainMenu());
    Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).setMenu(MenuEvent.Menu.CHARACTER_COLLECTIBLES);
  }

  /**
   * Opens a {@link SettingsMenu}.
   */
  private void openSettings() {
    user.openInventory(new SettingsMenu(user).getMainMenu());
    Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).setMenu(MenuEvent.Menu.CHARACTER_SETTINGS);
  }

  /**
   * Removes an equipped armor or hand item from the user.
   */
  private void unequipArmorHands() {
    int invSlot;
    switch (slot) {
      case 10 -> invSlot = 39;
      case 11 -> invSlot = user.getInventory().getHeldItemSlot();
      case 12 -> invSlot = 40;
      case 19 -> invSlot = 38;
      case 28 -> invSlot = 37;
      case 37 -> invSlot = 36;
      default -> invSlot = -1; // Unreachable
    }
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
      user.getInventory().setItem(invSlot, e.getInventory().getItem(slot));
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> updateArmorHandsAttributes(invSlot), 1);
    }, 1);
  }

  /**
   * Equips an item to the user.
   */
  private void interpretEquipItem() {
    if (ItemReader.isNotNullOrAir(e.getCursor())) {
      switch (slot) {
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
      int heldSlot = pInv.getHeldItemSlot();
      ItemStack item = e.getInventory().getItem(11);

      if (pInv.getItem(heldSlot) == null) { // Main hand slot is empty
        pInv.setItem(heldSlot, item);
        updateArmorHandsAttributes(heldSlot);
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
      int iSlot;
      switch (slot) {
        case 12 -> iSlot = 40;
        case 10 -> iSlot = 39;
        case 19 -> iSlot = 38;
        case 28 -> iSlot = 37;
        case 37 -> iSlot = 36;
        default -> iSlot = -1;
      }
      user.getInventory().setItem(iSlot, e.getInventory().getItem(slot));
      updateArmorHandsAttributes(iSlot);
    }, 1);
  }

  /**
   * Updates the user's displayed attributes for the armor and main hand slots.
   *
   * @param iSlot user's item slot
   */
  private void updateArmorHandsAttributes(int iSlot) {
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
      Equipment equipment = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getEquipment();
      ItemStack wornItem = user.getInventory().getItem(iSlot);
      switch (iSlot) {
        case 39 -> equipment.readSlot(wornItem, RpgEquipmentSlot.HEAD, true);
        case 38 -> equipment.readSlot(wornItem, RpgEquipmentSlot.CHEST, true);
        case 37 -> equipment.readSlot(wornItem, RpgEquipmentSlot.LEGS, true);
        case 36 -> equipment.readSlot(wornItem, RpgEquipmentSlot.FEET, true);
        case 40 -> equipment.readSlot(wornItem, RpgEquipmentSlot.OFF_HAND, true);
        default -> equipment.readSlot(wornItem, RpgEquipmentSlot.HAND, true);
      }
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), new SheetMenu(user, e.getClickedInventory())::addAttributes, 3);
    }, 1);
  }

  /**
   * Updates the user's displayed attributes for the {@link Equipment jewelry} slots.
   */
  private void updateJewelryAttributes() {
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
      Equipment equipment = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getEquipment();
      Inventory menu = e.getClickedInventory();
      ItemStack wornItem = menu.getItem(slot);
      switch (slot) {
        case 20 -> {
          equipment.getJewelry()[0] = wornItem;
          equipment.readSlot(wornItem, RpgEquipmentSlot.NECKLACE, true);
        }
        case 29 -> {
          equipment.getJewelry()[1] = wornItem;
          equipment.readSlot(wornItem, RpgEquipmentSlot.RING, true);
        }
      }
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), new SheetMenu(user, menu)::addAttributes, 3);
    }, 1);
  }

  /**
   * Returns to the {@link SheetMenu}.
   */
  private void returnToSheet() {
    user.openInventory(new SheetMenu(user, user).getMainMenu());
    Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).setMenu(MenuEvent.Menu.CHARACTER_SHEET);
  }

  /**
   * Toggles the player's {@link me.dannynguyen.aethel.rpg.system.Health health bar}.
   */
  private void toggleHealthBar() {
    Settings settings = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getSettings();
    Inventory menu = e.getInventory();
    if (settings.isHealthBarVisible()) {
      menu.setItem(18, ItemCreator.createItem(Material.RED_WOOL, ChatColor.AQUA + "Display Health Bar"));
      user.sendMessage(ChatColor.RED + "[Display Health Boss Bar]");
    } else {
      menu.setItem(18, ItemCreator.createItem(Material.LIME_WOOL, ChatColor.AQUA + "Display Health Bar"));
      user.sendMessage(ChatColor.GREEN + "[Display Health Boss Bar]");
    }
    settings.toggleHealthBarVisibility();
  }

  /**
   * Toggles the player's {@link me.dannynguyen.aethel.rpg.system.Health health in action bar}.
   */
  private void toggleHealthAction() {
    Settings settings = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getSettings();
    Inventory menu = e.getInventory();
    if (settings.isHealthActionVisible()) {
      menu.setItem(19, ItemCreator.createItem(Material.RED_WOOL, ChatColor.AQUA + "Display Health Action Bar"));
      user.sendMessage(ChatColor.RED + "[Display Health Action Bar]");
    } else {
      menu.setItem(19, ItemCreator.createItem(Material.LIME_WOOL, ChatColor.AQUA + "Display Health Action Bar"));
      user.sendMessage(ChatColor.GREEN + "[Display Health Action Bar]");
    }
    settings.toggleHealthActionVisibility();
  }

  /**
   * Resets all {@link me.dannynguyen.aethel.rpg.ability.ActiveAbility} crouch binds.
   */
  private void resetActiveAbilityCrouchBinds() {
    user.sendMessage(ChatColor.GREEN + "[Reset Active Ability Crouch Binds]");
    Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getSettings().resetActiveAbilityCrouchBinds();
    Map<RpgEquipmentSlot, Integer> activeAbilityCrouchBinds = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getSettings().getActiveAbilityCrouchBinds();
    Inventory menu = e.getInventory();
    menu.setItem(10, ItemCreator.createItem(Material.IRON_SWORD, ChatColor.AQUA + "Main Hand", List.of(ChatColor.WHITE + activeAbilityCrouchBinds.get(RpgEquipmentSlot.HAND).toString()), ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(11, ItemCreator.createItem(Material.SHIELD, ChatColor.AQUA + "Off Hand", List.of(ChatColor.WHITE + activeAbilityCrouchBinds.get(RpgEquipmentSlot.OFF_HAND).toString()), ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(12, ItemCreator.createItem(Material.IRON_HELMET, ChatColor.AQUA + "Head", List.of(ChatColor.WHITE + activeAbilityCrouchBinds.get(RpgEquipmentSlot.HEAD).toString()), ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(13, ItemCreator.createItem(Material.IRON_CHESTPLATE, ChatColor.AQUA + "Chest", List.of(ChatColor.WHITE + activeAbilityCrouchBinds.get(RpgEquipmentSlot.CHEST).toString()), ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(14, ItemCreator.createItem(Material.IRON_LEGGINGS, ChatColor.AQUA + "Legs", List.of(ChatColor.WHITE + activeAbilityCrouchBinds.get(RpgEquipmentSlot.LEGS).toString()), ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(15, ItemCreator.createItem(Material.IRON_BOOTS, ChatColor.AQUA + "Feet", List.of(ChatColor.WHITE + activeAbilityCrouchBinds.get(RpgEquipmentSlot.FEET).toString()), ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(16, ItemCreator.createItem(Material.IRON_NUGGET, ChatColor.AQUA + "Necklace", List.of(ChatColor.WHITE + activeAbilityCrouchBinds.get(RpgEquipmentSlot.NECKLACE).toString())));
    menu.setItem(17, ItemCreator.createItem(Material.GOLD_NUGGET, ChatColor.AQUA + "Ring", List.of(ChatColor.WHITE + activeAbilityCrouchBinds.get(RpgEquipmentSlot.RING).toString())));
  }

  /**
   * Sets the crouch bind to activate {@link RpgEquipmentSlot}
   * {@link me.dannynguyen.aethel.rpg.ability.ActiveAbility abilities}.
   *
   * @param eSlot {@link RpgEquipmentSlot}
   */
  private void setActiveAbilityCrouchBind(RpgEquipmentSlot eSlot) {
    user.closeInventory();
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + eSlot.getProperName() + " Active Ability " + ChatColor.WHITE + "crouch bind:");
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Select a hotbar slot and crouch.");
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    pluginPlayer.setActionInput(ActionEvent.Input.CROUCH_BIND_ACTIVE_ABILITY);
    pluginPlayer.setSlot(eSlot);
  }
}
