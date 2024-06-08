package me.bam6561.aethelplugin.commands.character;

import me.bam6561.aethelplugin.Plugin;
import me.bam6561.aethelplugin.enums.plugin.Message;
import me.bam6561.aethelplugin.enums.rpg.RpgEquipmentSlot;
import me.bam6561.aethelplugin.interfaces.MenuClick;
import me.bam6561.aethelplugin.listeners.MenuListener;
import me.bam6561.aethelplugin.listeners.MessageListener;
import me.bam6561.aethelplugin.plugin.MenuInput;
import me.bam6561.aethelplugin.rpg.Equipment;
import me.bam6561.aethelplugin.rpg.Settings;
import me.bam6561.aethelplugin.rpg.abilities.ActiveAbility;
import me.bam6561.aethelplugin.utils.item.ItemCreator;
import me.bam6561.aethelplugin.utils.item.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Inventory click event listener for {@link CharacterCommand} menus.
 * <p>
 * 1 tick delays are used because only the item that exists in the
 * corresponding slot after the interaction happens should be read.
 * <p>
 * Called through {@link MenuListener}.
 *
 * @author Danny Nguyen
 * @version 1.26.10
 * @since 1.9.2
 */
public class CharacterMenuClick implements MenuClick {
  /**
   * Shulker boxes.
   * <p>
   * Cannot be put in equipment slots.
   */
  private static final Set<Material> shulkerBoxes = Set.of(Material.SHULKER_BOX,
      Material.BLACK_SHULKER_BOX, Material.BLUE_SHULKER_BOX, Material.BROWN_SHULKER_BOX, Material.CYAN_SHULKER_BOX,
      Material.GRAY_SHULKER_BOX, Material.GREEN_SHULKER_BOX, Material.LIGHT_BLUE_SHULKER_BOX, Material.LIGHT_GRAY_SHULKER_BOX,
      Material.LIME_SHULKER_BOX, Material.MAGENTA_SHULKER_BOX, Material.ORANGE_SHULKER_BOX, Material.PINK_SHULKER_BOX,
      Material.PURPLE_SHULKER_BOX, Material.RED_SHULKER_BOX, Material.WHITE_SHULKER_BOX, Material.YELLOW_SHULKER_BOX);

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
    if (e.getAction() == InventoryAction.HOTBAR_SWAP) {
      return;
    }

    if (ItemReader.isNotNullOrAir(e.getCurrentItem())) {
      switch (slot) {
        case 4, 9, 15, 24, 33, 42 -> { // Player Head & Attributes
        }
        case 25 -> new MenuChange().openQuests();
        case 34 -> new MenuChange().openCollectibles();
        case 43 -> new MenuChange().openSettings();
        case 10, 11, 12, 19, 28, 37 -> new EquipmentChange().unequipArmorHands();
        case 20 -> new EquipmentChange().unequipNecklace();
        case 29 -> new EquipmentChange().unequipRing();
        case 38 -> new EquipmentChange().unequipTrinket();
      }
    } else {
      switch (slot) {
        case 11, 12 -> new EquipmentChange().interpretEquipItem();
        case 10, 19, 28, 37 -> new EquipmentChange().equipArmor();
        case 20 -> new EquipmentChange().equipNecklace();
        case 29 -> new EquipmentChange().equipRing();
        case 38 -> new EquipmentChange().equipTrinket();
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
        case 6 -> new MenuChange().returnToSheet();
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
        case 6 -> new MenuChange().returnToSheet();
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
        case 6 -> new MenuChange().returnToSheet();
        case 9 -> new SettingsChange().resetActiveAbilityRightClickBinds();
        case 10 -> new SettingsChange().setActiveAbilityRightClickBind(RpgEquipmentSlot.HAND);
        case 11 -> new SettingsChange().setActiveAbilityRightClickBind(RpgEquipmentSlot.OFF_HAND);
        case 12 -> new SettingsChange().setActiveAbilityRightClickBind(RpgEquipmentSlot.HEAD);
        case 13 -> new SettingsChange().setActiveAbilityRightClickBind(RpgEquipmentSlot.CHEST);
        case 14 -> new SettingsChange().setActiveAbilityRightClickBind(RpgEquipmentSlot.LEGS);
        case 15 -> new SettingsChange().setActiveAbilityRightClickBind(RpgEquipmentSlot.FEET);
        case 16 -> new SettingsChange().setActiveAbilityRightClickBind(RpgEquipmentSlot.NECKLACE);
        case 17 -> new SettingsChange().setActiveAbilityRightClickBind(RpgEquipmentSlot.RING);
        case 18 -> new SettingsChange().resetActiveAbilityCrouchBinds();
        case 19 -> new SettingsChange().setActiveAbilityCrouchBind(RpgEquipmentSlot.HAND);
        case 20 -> new SettingsChange().setActiveAbilityCrouchBind(RpgEquipmentSlot.OFF_HAND);
        case 21 -> new SettingsChange().setActiveAbilityCrouchBind(RpgEquipmentSlot.HEAD);
        case 22 -> new SettingsChange().setActiveAbilityCrouchBind(RpgEquipmentSlot.CHEST);
        case 23 -> new SettingsChange().setActiveAbilityCrouchBind(RpgEquipmentSlot.LEGS);
        case 24 -> new SettingsChange().setActiveAbilityCrouchBind(RpgEquipmentSlot.FEET);
        case 25 -> new SettingsChange().setActiveAbilityCrouchBind(RpgEquipmentSlot.NECKLACE);
        case 26 -> new SettingsChange().setActiveAbilityCrouchBind(RpgEquipmentSlot.RING);
        case 27 -> new SettingsChange().toggleHealthBar();
        case 28 -> new SettingsChange().toggleHealthAction();
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
          StringBuilder logEntry = new StringBuilder();
          String time = ZonedDateTime.now(ZoneId.of("America/New_York")).format(DateTimeFormatter.ofPattern("hh:mm"));
          logEntry.append(time).append(" ").append(user.getName()).append("CM/IPIC");
          Plugin.getData().getPluginLogger().addEntry(logEntry.toString());
          Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getEquipment().readSlot(item, RpgEquipmentSlot.HAND);
          Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), new SheetMenu(user, e.getInventory())::addAttributes, 3);
        }, 1);
      }, 1);
    }
  }

  /**
   * Represents a menu change operation.
   *
   * @author Danny Nguyen
   * @version 1.26.9
   * @since 1.23.10
   */
  private class MenuChange {
    /**
     * No parameter constructor.
     */
    MenuChange() {
    }

    /**
     * Opens a {@link QuestsMenu}.
     */
    private void openQuests() {
      user.openInventory(new QuestsMenu(user).getMainMenu());
      Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput().setMenu(MenuListener.Menu.CHARACTER_QUESTS);
    }

    /**
     * Opens a {@link CollectiblesMenu}.
     */
    private void openCollectibles() {
      user.openInventory(new CollectiblesMenu(user).getMainMenu());
      Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput().setMenu(MenuListener.Menu.CHARACTER_COLLECTIBLES);
    }

    /**
     * Opens a {@link SettingsMenu}.
     */
    private void openSettings() {
      user.openInventory(new SettingsMenu(user).getMainMenu());
      Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput().setMenu(MenuListener.Menu.CHARACTER_SETTINGS);
    }

    /**
     * Returns to the {@link SheetMenu}.
     */
    private void returnToSheet() {
      user.openInventory(new SheetMenu(user, user).getMainMenu());
      Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput().setMenu(MenuListener.Menu.CHARACTER_SHEET);
    }
  }

  /**
   * Represents an equipment change operation.
   *
   * @author Danny Nguyen
   * @version 1.26.10
   * @since 1.23.10
   */
  private class EquipmentChange {
    /**
     * No parameter constructor.
     */
    EquipmentChange() {
    }

    /**
     * Removes an equipped armor or hand item from the user.
     */
    private void unequipArmorHands() {
      e.setCancelled(false);
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
     * Unequips the user's necklace.
     */
    private void unequipNecklace() {
      if (e.getCursor() == null || e.getCursor().getType() == Material.AIR || (e.getCursor() != null && e.getCursor().getType() == Material.IRON_NUGGET)) {
        updateJewelryAttributes();
      } else {
        user.sendMessage(ChatColor.RED + "Necklace-only slot.");
      }
    }

    /**
     * Unequips the user's ring.
     */
    private void unequipRing() {
      if (e.getCursor() == null || e.getCursor().getType() == Material.AIR || (e.getCursor() != null && e.getCursor().getType() == Material.GOLD_NUGGET)) {
        updateJewelryAttributes();
      } else {
        user.sendMessage(ChatColor.RED + "Ring-only slot.");
      }
    }

    /**
     * Unequips the user's trinket.
     */
    private void unequipTrinket() {
      e.setCancelled(false);
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
        ItemStack[] jewelry = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getEquipment().getJewelry();
        Inventory menu = e.getClickedInventory();
        jewelry[2] = menu.getItem(38);
      }, 1);
    }

    /**
     * Equips an item to the user.
     */
    private void interpretEquipItem() {
      e.setCancelled(false);
      if (ItemReader.isNotNullOrAir(e.getCursor())) {
        switch (slot) {
          case 11 -> equipMainHand();
          case 10, 12, 19, 28, 37 -> equipOffHandArmor();
          case 20, 29 -> updateJewelryAttributes();
        }
      }
    }

    /**
     * Equips the armor onto the user.
     */
    private void equipArmor() {
      if (e.getCursor() == null) {
        return;
      }
      if (shulkerBoxes.contains(e.getCursor().getType())) {
        user.sendMessage(ChatColor.RED + "Cannot equip shulker boxes.");
        return;
      }
      interpretEquipItem();
    }

    /**
     * Equips a necklace onto the user.
     */
    private void equipNecklace() {
      if (e.getCursor() == null) {
        return;
      }
      if (e.getCursor().getType() != Material.IRON_NUGGET) {
        user.sendMessage(ChatColor.RED + "Necklace-only slot.");
        return;
      }
      interpretEquipItem();
    }

    /**
     * Equips a ring onto the user.
     */
    private void equipRing() {
      if (e.getCursor() == null) {
        return;
      }
      if (e.getCursor().getType() != Material.GOLD_NUGGET) {
        user.sendMessage(ChatColor.RED + "Ring-only slot.");
        return;
      }
      interpretEquipItem();
    }

    /**
     * Equips a trinket onto the user.
     */
    private void equipTrinket() {
      e.setCancelled(false);
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
        ItemStack[] jewelry = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getEquipment().getJewelry();
        Inventory menu = e.getClickedInventory();
        jewelry[2] = menu.getItem(38);
      }, 1);
    }

    /**
     * Equips the item to the user's main hand.
     */
    private void equipMainHand() {
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
    private void equipOffHandArmor() {
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
        StringBuilder logEntry = new StringBuilder();
        String time = ZonedDateTime.now(ZoneId.of("America/New_York")).format(DateTimeFormatter.ofPattern("hh:mm"));
        logEntry.append(time).append(" ").append(user.getName()).append("CM/UAHA");
        Plugin.getData().getPluginLogger().addEntry(logEntry.toString());
        switch (iSlot) {
          case 39 -> equipment.readSlot(wornItem, RpgEquipmentSlot.HEAD);
          case 38 -> equipment.readSlot(wornItem, RpgEquipmentSlot.CHEST);
          case 37 -> equipment.readSlot(wornItem, RpgEquipmentSlot.LEGS);
          case 36 -> equipment.readSlot(wornItem, RpgEquipmentSlot.FEET);
          case 40 -> equipment.readSlot(wornItem, RpgEquipmentSlot.OFF_HAND);
          default -> equipment.readSlot(wornItem, RpgEquipmentSlot.HAND);
        }
        Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), new SheetMenu(user, e.getClickedInventory())::addAttributes, 3);
      }, 1);
    }

    /**
     * Updates the user's displayed attributes for the {@link Equipment jewelry} slots.
     */
    private void updateJewelryAttributes() {
      e.setCancelled(false);
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
        Equipment equipment = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getEquipment();
        Inventory menu = e.getClickedInventory();
        ItemStack wornItem = menu.getItem(slot);
        StringBuilder logEntry = new StringBuilder();
        String time = ZonedDateTime.now(ZoneId.of("America/New_York")).format(DateTimeFormatter.ofPattern("hh:mm"));
        logEntry.append(time).append(" ").append(user.getName()).append("CM/UJA");
        Plugin.getData().getPluginLogger().addEntry(logEntry.toString());
        switch (slot) {
          case 20 -> {
            equipment.getJewelry()[0] = wornItem;
            equipment.readSlot(wornItem, RpgEquipmentSlot.NECKLACE);
          }
          case 29 -> {
            equipment.getJewelry()[1] = wornItem;
            equipment.readSlot(wornItem, RpgEquipmentSlot.RING);
          }
        }
        Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), new SheetMenu(user, menu)::addAttributes, 3);
      }, 1);
    }
  }

  /**
   * Represents a settings change operation.
   *
   * @author Danny Nguyen
   * @version 1.25.8
   * @since 1.23.10
   */
  private class SettingsChange {
    /**
     * No parameter constructor.
     */
    SettingsChange() {
    }

    /**
     * Resets all {@link Equipment} {@link ActiveAbility} right click binds.
     */
    private void resetActiveAbilityRightClickBinds() {
      user.sendMessage(ChatColor.GREEN + "[Reset Active Ability Right Click Binds]");
      Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getSettings().resetActiveAbilityRightClickBinds();
      Inventory menu = e.getInventory();
      menu.setItem(10, ItemCreator.createItem(Material.IRON_SWORD, ChatColor.AQUA + "Main Hand", ItemFlag.HIDE_ATTRIBUTES));
      menu.setItem(11, ItemCreator.createItem(Material.SHIELD, ChatColor.AQUA + "Off Hand", ItemFlag.HIDE_ATTRIBUTES));
      menu.setItem(12, ItemCreator.createItem(Material.IRON_HELMET, ChatColor.AQUA + "Head", ItemFlag.HIDE_ATTRIBUTES));
      menu.setItem(13, ItemCreator.createItem(Material.IRON_CHESTPLATE, ChatColor.AQUA + "Chest", ItemFlag.HIDE_ATTRIBUTES));
      menu.setItem(14, ItemCreator.createItem(Material.IRON_LEGGINGS, ChatColor.AQUA + "Legs", ItemFlag.HIDE_ATTRIBUTES));
      menu.setItem(15, ItemCreator.createItem(Material.IRON_BOOTS, ChatColor.AQUA + "Feet", ItemFlag.HIDE_ATTRIBUTES));
      menu.setItem(16, ItemCreator.createItem(Material.IRON_NUGGET, ChatColor.AQUA + "Necklace"));
      menu.setItem(17, ItemCreator.createItem(Material.GOLD_NUGGET, ChatColor.AQUA + "Ring"));
    }

    /**
     * Sets the right click bind to activate {@link RpgEquipmentSlot} {@link ActiveAbility abilities}.
     *
     * @param eSlot {@link RpgEquipmentSlot}
     */
    private void setActiveAbilityRightClickBind(RpgEquipmentSlot eSlot) {
      user.closeInventory();
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + eSlot.getProperName() + " Active Ability " + ChatColor.WHITE + "Right Click Binds:");
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Hotbar (Slot #'s)");
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      menuInput.setMessageInput(MessageListener.Type.CHARACTER_RIGHT_CLICK_BIND_ACTIVE_ABILITY);
      menuInput.setSlot(eSlot);
    }

    /**
     * Resets all {@link Equipment} {@link ActiveAbility} crouch binds.
     */
    private void resetActiveAbilityCrouchBinds() {
      user.sendMessage(ChatColor.GREEN + "[Reset Active Ability Crouch Binds]");
      Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getSettings().resetActiveAbilityCrouchBinds();
      Inventory menu = e.getInventory();
      menu.setItem(19, ItemCreator.createItem(Material.IRON_SWORD, ChatColor.AQUA + "Main Hand", ItemFlag.HIDE_ATTRIBUTES));
      menu.setItem(20, ItemCreator.createItem(Material.SHIELD, ChatColor.AQUA + "Off Hand", ItemFlag.HIDE_ATTRIBUTES));
      menu.setItem(21, ItemCreator.createItem(Material.IRON_HELMET, ChatColor.AQUA + "Head", ItemFlag.HIDE_ATTRIBUTES));
      menu.setItem(22, ItemCreator.createItem(Material.IRON_CHESTPLATE, ChatColor.AQUA + "Chest", ItemFlag.HIDE_ATTRIBUTES));
      menu.setItem(23, ItemCreator.createItem(Material.IRON_LEGGINGS, ChatColor.AQUA + "Legs", ItemFlag.HIDE_ATTRIBUTES));
      menu.setItem(24, ItemCreator.createItem(Material.IRON_BOOTS, ChatColor.AQUA + "Feet", ItemFlag.HIDE_ATTRIBUTES));
      menu.setItem(25, ItemCreator.createItem(Material.IRON_NUGGET, ChatColor.AQUA + "Necklace"));
      menu.setItem(26, ItemCreator.createItem(Material.GOLD_NUGGET, ChatColor.AQUA + "Ring"));
    }

    /**
     * Sets the crouch bind to activate {@link RpgEquipmentSlot} {@link ActiveAbility abilities}.
     *
     * @param eSlot {@link RpgEquipmentSlot}
     */
    private void setActiveAbilityCrouchBind(RpgEquipmentSlot eSlot) {
      user.closeInventory();
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + eSlot.getProperName() + " Active Ability " + ChatColor.WHITE + "Crouch Binds:");
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Hotbar (Slot #'s)");
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      menuInput.setMessageInput(MessageListener.Type.CHARACTER_CROUCH_BIND_ACTIVE_ABILITY);
      menuInput.setSlot(eSlot);
    }

    /**
     * Toggles the player's {@link Settings#isHealthBarVisible() health bar}.
     */
    private void toggleHealthBar() {
      Settings settings = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getSettings();
      Inventory menu = e.getInventory();
      if (settings.isHealthBarVisible()) {
        menu.setItem(27, ItemCreator.createItem(Material.RED_CONCRETE_POWDER, ChatColor.AQUA + "Display Health Bar"));
        user.sendMessage(ChatColor.RED + "[Display Health Boss Bar]");
      } else {
        menu.setItem(27, ItemCreator.createItem(Material.GREEN_CONCRETE_POWDER, ChatColor.AQUA + "Display Health Bar"));
        user.sendMessage(ChatColor.GREEN + "[Display Health Boss Bar]");
      }
      settings.toggleHealthBarVisibility();
    }

    /**
     * Toggles the player's {@link Settings#isHealthActionVisible() health in action bar}.
     */
    private void toggleHealthAction() {
      Settings settings = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getSettings();
      Inventory menu = e.getInventory();
      if (settings.isHealthActionVisible()) {
        menu.setItem(28, ItemCreator.createItem(Material.RED_CONCRETE_POWDER, ChatColor.AQUA + "Display Health Action Bar"));
        user.sendMessage(ChatColor.RED + "[Display Health Action Bar]");
      } else {
        menu.setItem(28, ItemCreator.createItem(Material.GREEN_CONCRETE_POWDER, ChatColor.AQUA + "Display Health Action Bar"));
        user.sendMessage(ChatColor.GREEN + "[Display Health Action Bar]");
      }
      settings.toggleHealthActionVisibility();
    }
  }
}
