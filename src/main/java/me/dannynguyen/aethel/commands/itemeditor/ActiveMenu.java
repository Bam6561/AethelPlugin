package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.plugin.enums.KeyHeader;
import me.dannynguyen.aethel.plugin.enums.PlayerHead;
import me.dannynguyen.aethel.plugin.enums.PluginNamespacedKey;
import me.dannynguyen.aethel.plugin.interfaces.Menu;
import me.dannynguyen.aethel.rpg.enums.ActiveAbilityType;
import me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot;
import me.dannynguyen.aethel.util.InventoryPages;
import me.dannynguyen.aethel.util.TextFormatter;
import me.dannynguyen.aethel.util.item.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents a menu that allows the user to edit an item's
 * {@link ActiveAbilityType active abilities}.
 *
 * @author Danny Nguyen
 * @version 1.17.6
 * @since 1.15.1
 */
public class ActiveMenu implements Menu {
  /**
   * GUI.
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
   * GUI {@link RpgEquipmentSlot equipment slot}.
   */
  private final RpgEquipmentSlot eSlot;

  /**
   * ItemStack data container.
   */
  private final PersistentDataContainer dataContainer;

  /**
   * ItemStack {@link ActiveAbilityType active abilities}.
   */
  private final Map<String, List<String>> existingActives;

  /**
   * Associates a new Active menu with its user and item.
   *
   * @param user  user
   * @param eSlot {@link RpgEquipmentSlot equipment slot}
   */
  public ActiveMenu(@NotNull Player user, @NotNull RpgEquipmentSlot eSlot) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.eSlot = Objects.requireNonNull(eSlot, "Null slot");
    this.item = Plugin.getData().getEditedItemCache().getEditedItems().get(user.getUniqueId());
    this.dataContainer = item.getItemMeta().getPersistentDataContainer();
    this.existingActives = mapActives();
    this.menu = createMenu();
  }

  /**
   * Creates and names a new Active menu with its action.
   *
   * @return Active menu
   */
  private Inventory createMenu() {
    Inventory inv = Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "Actives " + ChatColor.DARK_AQUA + eSlot.getProperName());
    inv.setItem(1, item);
    return inv;
  }

  /**
   * Sets the menu to display interactions with {@link ActiveAbilityType active abilities}.
   *
   * @return Active menu
   */
  @NotNull
  public Inventory getMainMenu() {
    addActives();
    addContext();
    addActions();
    InventoryPages.addBackButton(menu, 2);
    return menu;
  }

  /**
   * Adds {@link ActiveAbilityType active abilities}.
   */
  private void addActives() {
    int invSlot = 18;
    if (existingActives != null) {
      for (ActiveAbilityType activeType : ActiveAbilityType.values()) {
        String activeName = activeType.getProperName();
        String activeId = activeType.getId();
        boolean enabled = existingActives.containsKey(activeId);
        if (enabled) {
          List<String> lore = new ArrayList<>();
          for (String slot : existingActives.get(activeId)) {
            NamespacedKey activeKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.ACTIVE.getHeader() + slot + "." + activeId);
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
      for (ActiveAbilityType activeType : ActiveAbilityType.values()) {
        menu.setItem(invSlot, ItemCreator.createItem(Material.RAW_GOLD, ChatColor.AQUA + activeType.getProperName()));
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
   * Adds {@link RpgEquipmentSlot equipment slot} buttons.
   */
  private void addActions() {
    menu.setItem(5, ItemCreator.createItem(Material.IRON_HELMET, ChatColor.AQUA + "Head", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(6, ItemCreator.createItem(Material.IRON_CHESTPLATE, ChatColor.AQUA + "Chest", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(7, ItemCreator.createItem(Material.IRON_LEGGINGS, ChatColor.AQUA + "Legs", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(8, ItemCreator.createItem(Material.IRON_BOOTS, ChatColor.AQUA + "Feet", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(14, ItemCreator.createItem(Material.IRON_SWORD, ChatColor.AQUA + "Hand", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(15, ItemCreator.createItem(Material.SHIELD, ChatColor.AQUA + "Off Hand", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(16, ItemCreator.createItem(Material.IRON_NUGGET, ChatColor.AQUA + "Necklace"));
    menu.setItem(17, ItemCreator.createItem(Material.GOLD_NUGGET, ChatColor.AQUA + "Ring"));
  }

  /**
   * Maps an item's {@link ActiveAbilityType active abilities}.
   *
   * @return item's {@link ActiveAbilityType actives} map
   */
  private Map<String, List<String>> mapActives() {
    NamespacedKey listKey = PluginNamespacedKey.ACTIVE_LIST.getNamespacedKey();
    boolean hasActives = dataContainer.has(listKey, PersistentDataType.STRING);
    if (hasActives) {
      Map<String, List<String>> existingActives = new HashMap<>();
      List<String> actives = new ArrayList<>(List.of(dataContainer.get(listKey, PersistentDataType.STRING).split(" ")));
      for (String active : actives) {
        String activeType = active.substring(active.indexOf(".") + 1);
        if (existingActives.containsKey(activeType)) {
          existingActives.get(activeType).add(active.substring(0, active.indexOf(".")));
        } else {
          existingActives.put(activeType, new ArrayList<>(List.of(active.substring(0, active.indexOf(".")))));
        }
      }
      return existingActives;
    } else {
      return null;
    }
  }
}
