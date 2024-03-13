package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.systems.plugin.KeyHeader;
import me.dannynguyen.aethel.systems.plugin.PlayerHead;
import me.dannynguyen.aethel.systems.plugin.PluginNamespacedKey;
import me.dannynguyen.aethel.systems.rpg.ActiveAbility;
import me.dannynguyen.aethel.utility.InventoryPages;
import me.dannynguyen.aethel.utility.ItemCreator;
import me.dannynguyen.aethel.utility.TextFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents a menu that allows the user to edit an item's active abilities.
 *
 * @author Danny Nguyen
 * @version 1.15.10
 * @since 1.15.1
 */
class ActiveMenu {
  /**
   * Active GUI.
   */
  private final Inventory menu;

  /**
   * GUI user.
   */
  private final Player user;

  /**
   * ItemStack being edited.
   */
  private final ItemStack item;

  /**
   * GUI action.
   */
  private final EquipmentSlot slot;

  /**
   * ItemStack data container.
   */
  private final PersistentDataContainer dataContainer;

  /**
   * ItemStack active abilities.
   */
  private final Map<String, List<String>> activesMap;

  /**
   * Associates a new Active menu with its user and item.
   *
   * @param user user
   * @param slot equipment slot
   */
  protected ActiveMenu(@NotNull Player user, @NotNull EquipmentSlot slot) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.slot = Objects.requireNonNull(slot, "Null slot");
    this.item = Plugin.getData().getEditedItemCache().getEditedItemMap().get(user.getUniqueId());
    this.dataContainer = item.getItemMeta().getPersistentDataContainer();
    this.activesMap = mapActives();
    this.menu = createMenu();
  }

  /**
   * Creates and names a new Active menu with its action.
   *
   * @return Active menu
   */
  private Inventory createMenu() {
    String actionString = "";
    switch (slot) {
      case HEAD, CHEST, LEGS, FEET, HAND -> actionString = TextFormatter.capitalizeWord(slot.name());
      case OFF_HAND -> actionString = "Off Hand";
    }
    Inventory inv = Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "ItemEditor " + ChatColor.DARK_AQUA + "Active " + ChatColor.YELLOW + actionString);
    inv.setItem(1, item);
    return inv;
  }

  /**
   * Sets the menu to display interactions with active abilities.
   *
   * @return Active menu
   */
  protected Inventory openMenu() {
    addActives();
    addContext();
    addActions();
    InventoryPages.addBackButton(menu, 2);
    return menu;
  }

  /**
   * Adds active abilities.
   */
  private void addActives() {
    int invSlot = 18;
    if (activesMap != null) {
      for (ActiveAbility active : ActiveAbility.values()) {
        String activeName = active.getProperName();
        String activeMapKey = active.getId();
        boolean enabled = activesMap.containsKey(activeMapKey);
        if (enabled) {
          List<String> lore = new ArrayList<>();
          for (String slot : activesMap.get(activeMapKey)) {
            NamespacedKey activeKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.ACTIVE.getHeader() + slot + "." + activeMapKey);
            String activeValue = dataContainer.get(activeKey, PersistentDataType.STRING);
            lore.add(ChatColor.WHITE + TextFormatter.capitalizePhrase(slot + ": " + activeValue));
          }
          menu.setItem(invSlot, ItemCreator.createItem(Material.GOLD_INGOT, ChatColor.AQUA + activeName, lore));
        } else {
          menu.setItem(invSlot, ItemCreator.createItem(Material.RAW_GOLD, ChatColor.AQUA + activeName));
        }
        invSlot++;
      }
    } else {
      for (ActiveAbility active : ActiveAbility.values()) {
        menu.setItem(invSlot, ItemCreator.createItem(Material.RAW_GOLD, ChatColor.AQUA + active.getProperName()));
        invSlot++;
      }
    }
  }

  /**
   * Adds contextual help.
   */
  private void addContext() {
    menu.setItem(0, ItemCreator.createPluginPlayerHead(PlayerHead.QUESTION_MARK_WHITE.getHead(), ChatColor.GREEN + "Help", List.of(ChatColor.WHITE + "To remove an active ability, input \"-\".")));
  }

  /**
   * Adds equipment slot buttons.
   */
  private void addActions() {
    menu.setItem(5, ItemCreator.createItem(Material.IRON_HELMET, ChatColor.AQUA + "Head", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(6, ItemCreator.createItem(Material.IRON_CHESTPLATE, ChatColor.AQUA + "Chest", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(7, ItemCreator.createItem(Material.IRON_LEGGINGS, ChatColor.AQUA + "Legs", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(8, ItemCreator.createItem(Material.IRON_BOOTS, ChatColor.AQUA + "Feet", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(14, ItemCreator.createItem(Material.IRON_SWORD, ChatColor.AQUA + "Hand", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(15, ItemCreator.createItem(Material.SHIELD, ChatColor.AQUA + "Off Hand", ItemFlag.HIDE_ATTRIBUTES));
  }

  /**
   * Maps an item's active abilities.
   *
   * @return item's actives map
   */
  private Map<String, List<String>> mapActives() {
    NamespacedKey listKey = PluginNamespacedKey.ACTIVE_LIST.getNamespacedKey();
    boolean hasActives = dataContainer.has(listKey, PersistentDataType.STRING);
    if (hasActives) {
      Map<String, List<String>> activesMap = new HashMap<>();
      List<String> actives = new ArrayList<>(List.of(dataContainer.get(listKey, PersistentDataType.STRING).split(" ")));
      for (String active : actives) {
        String activeType = active.substring(active.indexOf(".") + 1);
        if (activesMap.containsKey(activeType)) {
          activesMap.get(activeType).add(active.substring(0, active.indexOf(".")));
        } else {
          activesMap.put(activeType, new ArrayList<>(List.of(active.substring(0, active.indexOf(".")))));
        }
      }
      return activesMap;
    } else {
      return null;
    }
  }
}
