package me.bam6561.aethelplugin.commands.itemeditor;

import me.bam6561.aethelplugin.Plugin;
import me.bam6561.aethelplugin.enums.plugin.Key;
import me.bam6561.aethelplugin.enums.plugin.KeyHeader;
import me.bam6561.aethelplugin.enums.plugin.PlayerHead;
import me.bam6561.aethelplugin.enums.rpg.RpgEquipmentSlot;
import me.bam6561.aethelplugin.enums.rpg.abilities.ActiveAbilityType;
import me.bam6561.aethelplugin.interfaces.Menu;
import me.bam6561.aethelplugin.utils.InventoryPages;
import me.bam6561.aethelplugin.utils.TextFormatter;
import me.bam6561.aethelplugin.utils.item.ItemCreator;
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
 * {@link Key#ACTIVE_EQUIPMENT_LIST active abilities}.
 *
 * @author Danny Nguyen
 * @version 1.24.9
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
   * {@link RpgEquipmentSlot}
   */
  private final RpgEquipmentSlot eSlot;

  /**
   * ItemStack's persistent tags.
   */
  private final PersistentDataContainer itemTags;

  /**
   * ItemStack {@link Key#ACTIVE_EQUIPMENT_LIST active abilities}.
   */
  private final Map<String, List<String>> existingActives;

  /**
   * Associates a new Active menu with its user and item.
   *
   * @param user  user
   * @param eSlot {@link RpgEquipmentSlot}
   */
  public ActiveMenu(@NotNull Player user, @NotNull RpgEquipmentSlot eSlot) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.eSlot = Objects.requireNonNull(eSlot, "Null slot");
    this.item = Plugin.getData().getEditedItemCache().getEditedItems().get(user.getUniqueId());
    this.itemTags = item.getItemMeta().getPersistentDataContainer();
    this.existingActives = mapActives();
    this.menu = createMenu();
  }

  /**
   * Creates and names a new Active menu with its {@link RpgEquipmentSlot}.
   *
   * @return Active menu
   */
  private Inventory createMenu() {
    Inventory inv = Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "Actives " + ChatColor.DARK_AQUA + eSlot.getProperName());
    inv.setItem(4, item);
    return inv;
  }

  /**
   * Sets the menu to display interactions with {@link ActiveAbilityType}.
   *
   * @return Active menu
   */
  @NotNull
  public Inventory getMainMenu() {
    addActives();
    addActions();
    addContext();
    InventoryPages.addBackButton(menu, 6);
    return menu;
  }

  /**
   * Adds {@link ActiveAbilityType active abilities}.
   */
  private void addActives() {
    int invSlot = 18;
    if (existingActives != null) {
      for (ActiveAbilityType activeAbilityType : ActiveAbilityType.values()) {
        String activeName = activeAbilityType.getProperName();
        String activeId = activeAbilityType.getId();
        boolean enabled = existingActives.containsKey(activeId);
        if (enabled) {
          List<String> lore = new ArrayList<>();
          for (String slot : existingActives.get(activeId)) {
            NamespacedKey activeKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.ACTIVE_EQUIPMENT.getHeader() + slot + "." + activeId);
            String activeValue = itemTags.get(activeKey, PersistentDataType.STRING);
            lore.add(ChatColor.WHITE + TextFormatter.capitalizePhrase(slot + ": " + activeValue));
          }
          menu.setItem(invSlot, ItemCreator.createItem(Material.TORCH, ChatColor.AQUA + activeName, lore));
        } else {
          menu.setItem(invSlot, ItemCreator.createItem(Material.GLOWSTONE_DUST, ChatColor.AQUA + activeName));
        }
        invSlot++;
      }
    } else {
      for (ActiveAbilityType activeAbilityType : ActiveAbilityType.values()) {
        menu.setItem(invSlot, ItemCreator.createItem(Material.GLOWSTONE_DUST, ChatColor.AQUA + activeAbilityType.getProperName()));
        invSlot++;
      }
    }
  }

  /**
   * Adds contextual help.
   */
  private void addContext() {
    menu.setItem(2, ItemCreator.createPluginPlayerHead(PlayerHead.QUESTION_MARK_WHITE.getHead(), ChatColor.GREEN + "Help", List.of(ChatColor.WHITE + "To remove an active ability, input \"-\".")));
  }

  /**
   * Adds {@link RpgEquipmentSlot} buttons.
   */
  private void addActions() {
    menu.setItem(9, ItemCreator.createItem(Material.IRON_HELMET, ChatColor.AQUA + "Head", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(10, ItemCreator.createItem(Material.IRON_CHESTPLATE, ChatColor.AQUA + "Chest", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(11, ItemCreator.createItem(Material.IRON_LEGGINGS, ChatColor.AQUA + "Legs", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(12, ItemCreator.createItem(Material.IRON_BOOTS, ChatColor.AQUA + "Feet", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(14, ItemCreator.createItem(Material.IRON_SWORD, ChatColor.AQUA + "Hand", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(15, ItemCreator.createItem(Material.SHIELD, ChatColor.AQUA + "Off Hand", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(16, ItemCreator.createItem(Material.IRON_NUGGET, ChatColor.AQUA + "Necklace"));
    menu.setItem(17, ItemCreator.createItem(Material.GOLD_NUGGET, ChatColor.AQUA + "Ring"));
  }

  /**
   * Maps an item's {@link Key#ACTIVE_EQUIPMENT_LIST active abilities}.
   *
   * @return item's {@link Key#ACTIVE_EQUIPMENT_LIST actives} map
   */
  private Map<String, List<String>> mapActives() {
    NamespacedKey listKey = Key.ACTIVE_EQUIPMENT_LIST.getNamespacedKey();
    boolean hasActives = itemTags.has(listKey, PersistentDataType.STRING);
    if (hasActives) {
      Map<String, List<String>> existingActives = new HashMap<>();
      List<String> actives = new ArrayList<>(List.of(itemTags.get(listKey, PersistentDataType.STRING).split(" ")));
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
