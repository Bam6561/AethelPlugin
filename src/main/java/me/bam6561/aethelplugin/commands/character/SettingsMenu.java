package me.bam6561.aethelplugin.commands.character;

import me.bam6561.aethelplugin.Plugin;
import me.bam6561.aethelplugin.enums.plugin.PlayerHead;
import me.bam6561.aethelplugin.enums.rpg.RpgEquipmentSlot;
import me.bam6561.aethelplugin.interfaces.Menu;
import me.bam6561.aethelplugin.rpg.Settings;
import me.bam6561.aethelplugin.rpg.abilities.ActiveAbility;
import me.bam6561.aethelplugin.utils.InventoryPages;
import me.bam6561.aethelplugin.utils.item.ItemCreator;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents a menu that shows the player's {@link Settings}.
 *
 * @author Danny Nguyen
 * @version 1.25.8
 * @since 1.11.5
 */
public class SettingsMenu implements Menu {
  /**
   * GUI.
   */
  private final Inventory menu;

  /**
   * GUI user.
   */
  private final Player user;

  /**
   * User's UUID.
   */
  private final UUID uuid;

  /**
   * Associates a new Settings menu with its user.
   *
   * @param user user
   */
  public SettingsMenu(@NotNull Player user) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.uuid = user.getUniqueId();
    this.menu = createMenu();
  }

  /**
   * Creates and names a Settings menu to its user.
   *
   * @return Settings menu
   */
  private Inventory createMenu() {
    return Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "Settings " + ChatColor.DARK_PURPLE + user.getName());
  }

  /**
   * Opens a Settings menu.
   *
   * @return Settings menu
   */
  @NotNull
  public Inventory getMainMenu() {
    addOwner();
    addSettings();
    InventoryPages.addBackButton(menu, 6);
    return menu;
  }

  /**
   * Adds the settings owner's head.
   */
  private void addOwner() {
    menu.setItem(4, ItemCreator.createPlayerHead(user));
  }

  /**
   * Adds settings.
   */
  private void addSettings() {
    addActiveAbilityRightClickBinds();
    addActiveAbilityCrouchBinds();
    addDisplayHealthBar();
    addDisplayHealthAction();
  }

  /**
   * Adds {@link me.bam6561.aethelplugin.rpg.Equipment} {@link ActiveAbility} right click binds.
   */
  private void addActiveAbilityRightClickBinds() {
    Map<RpgEquipmentSlot, Set<Integer>> boundEquipmentSlots = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getSettings().getActiveAbilityRightClickBoundEquipmentSlots();
    menu.setItem(9, ItemCreator.createPluginPlayerHead(PlayerHead.TRASH_CAN.getHead(), ChatColor.AQUA + "Reset Active Ability Right Click Binds"));
    menu.setItem(10, ItemCreator.createItem(Material.IRON_SWORD, ChatColor.AQUA + "Main Hand", List.of(ChatColor.WHITE + getHotbarSlotsAsString(boundEquipmentSlots, RpgEquipmentSlot.HAND)), ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(11, ItemCreator.createItem(Material.SHIELD, ChatColor.AQUA + "Off Hand", List.of(ChatColor.WHITE + getHotbarSlotsAsString(boundEquipmentSlots, RpgEquipmentSlot.OFF_HAND)), ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(12, ItemCreator.createItem(Material.IRON_HELMET, ChatColor.AQUA + "Head", List.of(ChatColor.WHITE + getHotbarSlotsAsString(boundEquipmentSlots, RpgEquipmentSlot.HEAD)), ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(13, ItemCreator.createItem(Material.IRON_CHESTPLATE, ChatColor.AQUA + "Chest", List.of(ChatColor.WHITE + getHotbarSlotsAsString(boundEquipmentSlots, RpgEquipmentSlot.CHEST)), ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(14, ItemCreator.createItem(Material.IRON_LEGGINGS, ChatColor.AQUA + "Legs", List.of(ChatColor.WHITE + getHotbarSlotsAsString(boundEquipmentSlots, RpgEquipmentSlot.LEGS)), ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(15, ItemCreator.createItem(Material.IRON_BOOTS, ChatColor.AQUA + "Feet", List.of(ChatColor.WHITE + getHotbarSlotsAsString(boundEquipmentSlots, RpgEquipmentSlot.FEET)), ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(16, ItemCreator.createItem(Material.IRON_NUGGET, ChatColor.AQUA + "Necklace", List.of(ChatColor.WHITE + getHotbarSlotsAsString(boundEquipmentSlots, RpgEquipmentSlot.NECKLACE))));
    menu.setItem(17, ItemCreator.createItem(Material.GOLD_NUGGET, ChatColor.AQUA + "Ring", List.of(ChatColor.WHITE + getHotbarSlotsAsString(boundEquipmentSlots, RpgEquipmentSlot.RING))));
  }

  /**
   * Adds {@link me.bam6561.aethelplugin.rpg.Equipment} {@link ActiveAbility} crouch binds.
   */
  private void addActiveAbilityCrouchBinds() {
    Map<RpgEquipmentSlot, Set<Integer>> boundEquipmentSlots = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getSettings().getActiveAbilityCrouchBoundEquipmentSlots();
    menu.setItem(18, ItemCreator.createPluginPlayerHead(PlayerHead.TRASH_CAN.getHead(), ChatColor.AQUA + "Reset Active Ability Crouch Binds"));
    menu.setItem(19, ItemCreator.createItem(Material.IRON_SWORD, ChatColor.AQUA + "Main Hand", List.of(ChatColor.WHITE + getHotbarSlotsAsString(boundEquipmentSlots, RpgEquipmentSlot.HAND)), ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(20, ItemCreator.createItem(Material.SHIELD, ChatColor.AQUA + "Off Hand", List.of(ChatColor.WHITE + getHotbarSlotsAsString(boundEquipmentSlots, RpgEquipmentSlot.OFF_HAND)), ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(21, ItemCreator.createItem(Material.IRON_HELMET, ChatColor.AQUA + "Head", List.of(ChatColor.WHITE + getHotbarSlotsAsString(boundEquipmentSlots, RpgEquipmentSlot.HEAD)), ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(22, ItemCreator.createItem(Material.IRON_CHESTPLATE, ChatColor.AQUA + "Chest", List.of(ChatColor.WHITE + getHotbarSlotsAsString(boundEquipmentSlots, RpgEquipmentSlot.CHEST)), ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(23, ItemCreator.createItem(Material.IRON_LEGGINGS, ChatColor.AQUA + "Legs", List.of(ChatColor.WHITE + getHotbarSlotsAsString(boundEquipmentSlots, RpgEquipmentSlot.LEGS)), ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(24, ItemCreator.createItem(Material.IRON_BOOTS, ChatColor.AQUA + "Feet", List.of(ChatColor.WHITE + getHotbarSlotsAsString(boundEquipmentSlots, RpgEquipmentSlot.FEET)), ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(25, ItemCreator.createItem(Material.IRON_NUGGET, ChatColor.AQUA + "Necklace", List.of(ChatColor.WHITE + getHotbarSlotsAsString(boundEquipmentSlots, RpgEquipmentSlot.NECKLACE))));
    menu.setItem(26, ItemCreator.createItem(Material.GOLD_NUGGET, ChatColor.AQUA + "Ring", List.of(ChatColor.WHITE + getHotbarSlotsAsString(boundEquipmentSlots, RpgEquipmentSlot.RING))));
  }

  /**
   * Toggles the visibility of the {@link Settings#isHealthBarVisible() health bar}.
   */
  private void addDisplayHealthBar() {
    if (Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getSettings().isHealthBarVisible()) {
      menu.setItem(27, ItemCreator.createItem(Material.GREEN_CONCRETE_POWDER, ChatColor.AQUA + "Display Health Boss Bar"));
    } else {
      menu.setItem(27, ItemCreator.createItem(Material.RED_CONCRETE_POWDER, ChatColor.AQUA + "Display Health Boss Bar"));
    }
  }

  /**
   * Toggles the visibility of {@link Settings#isHealthActionVisible() health in the action bar}.
   */
  private void addDisplayHealthAction() {
    if (Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getSettings().isHealthActionVisible()) {
      menu.setItem(28, ItemCreator.createItem(Material.GREEN_CONCRETE_POWDER, ChatColor.AQUA + "Display Health Action Bar"));
    } else {
      menu.setItem(28, ItemCreator.createItem(Material.RED_CONCRETE_POWDER, ChatColor.AQUA + "Display Health Action Bar"));
    }
  }

  /**
   * Returns a text display of hotbar slots.
   *
   * @param eSlot               {@link RpgEquipmentSlot}
   * @param boundEquipmentSlots assigned hotbar slots
   * @return text display of hotbar slots
   */
  private String getHotbarSlotsAsString(Map<RpgEquipmentSlot, Set<Integer>> boundEquipmentSlots, RpgEquipmentSlot eSlot) {
    StringBuilder hotbarBuilder = new StringBuilder();
    for (int hotbarSlot : boundEquipmentSlots.get(eSlot)) {
      hotbarBuilder.append(hotbarSlot + 1).append(" ");
    }
    return hotbarBuilder.toString().trim();
  }
}
