package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.interfaces.Menu;
import me.dannynguyen.aethel.systems.plugin.KeyHeader;
import me.dannynguyen.aethel.systems.plugin.PlayerHead;
import me.dannynguyen.aethel.systems.plugin.PluginNamespacedKey;
import me.dannynguyen.aethel.systems.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.systems.rpg.ability.PassiveAbilityType;
import me.dannynguyen.aethel.systems.rpg.ability.Trigger;
import me.dannynguyen.aethel.utility.InventoryPages;
import me.dannynguyen.aethel.utility.ItemCreator;
import me.dannynguyen.aethel.utility.TextFormatter;
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
 * Represents a menu that allows the user to edit an item's passive abilities.
 *
 * @author Danny Nguyen
 * @version 1.17.6
 * @since 1.15.1
 */
public class PassiveMenu implements Menu {
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
   * GUI trigger type.
   */
  private final Trigger trigger;

  /**
   * GUI equipment slot.
   */
  private final RpgEquipmentSlot eSlot;

  /**
   * ItemStack data container.
   */
  private final PersistentDataContainer dataContainer;

  /**
   * ItemStack passive abilities.
   */
  private final Map<String, List<SlotCondition>> existingPassives;

  /**
   * Associates a new Passive menu with its user and item.
   *
   * @param user    user
   * @param trigger trigger type
   * @param eSlot    equipment slot
   */
  public PassiveMenu(@NotNull Player user, @NotNull RpgEquipmentSlot eSlot, @NotNull Trigger trigger) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.eSlot = Objects.requireNonNull(eSlot, "Null slot");
    this.trigger = Objects.requireNonNull(trigger, "Null trigger");
    this.item = Plugin.getData().getEditedItemCache().getEditedItems().get(user.getUniqueId());
    this.dataContainer = item.getItemMeta().getPersistentDataContainer();
    this.existingPassives = mapPassives();
    this.menu = createMenu();
  }

  /**
   * Creates and names a new Passive menu with its action.
   *
   * @return Passive menu
   */
  private Inventory createMenu() {
    Inventory inv = Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "Passives " + ChatColor.DARK_AQUA + eSlot.getProperName() + " " + ChatColor.YELLOW + trigger.getProperName());
    inv.setItem(1, item);
    return inv;
  }

  /**
   * Sets the menu to display interactions with passive abilities.
   *
   * @return Passive menu
   */
  public Inventory getMainMenu() {
    addPassives();
    addContext();
    addActions();
    addTriggers();
    InventoryPages.addBackButton(menu, 2);
    return menu;
  }

  /**
   * Adds passive abilities.
   */
  private void addPassives() {
    int invSlot = 18;
    if (existingPassives != null) {
      for (PassiveAbilityType passiveType : PassiveAbilityType.values()) {
        String passiveName = passiveType.getProperName();
        String passiveId = passiveType.getId();
        boolean enabled = existingPassives.containsKey(passiveId);
        if (enabled) {
          List<String> lore = new ArrayList<>();
          for (SlotCondition slotCondition : existingPassives.get(passiveId)) {
            NamespacedKey passiveKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.PASSIVE.getHeader() + slotCondition.getSlot() + "." + slotCondition.getCondition() + "." + passiveId);
            String passiveValue = dataContainer.get(passiveKey, PersistentDataType.STRING);
            lore.add(ChatColor.WHITE + TextFormatter.capitalizePhrase(slotCondition.getSlot() + " " + slotCondition.getCondition() + ": " + passiveValue));
          }
          menu.setItem(invSlot, ItemCreator.createItem(Material.IRON_INGOT, ChatColor.AQUA + passiveName, lore));
        } else {
          menu.setItem(invSlot, ItemCreator.createItem(Material.RAW_IRON, ChatColor.AQUA + passiveName));
        }
        invSlot++;
      }
    } else {
      for (PassiveAbilityType passiveType : PassiveAbilityType.values()) {
        menu.setItem(invSlot, ItemCreator.createItem(Material.RAW_IRON, ChatColor.AQUA + passiveType.getProperName()));
        invSlot++;
      }
    }
  }

  /**
   * Adds contextual help.
   */
  private void addContext() {
    menu.setItem(0, ItemCreator.createPluginPlayerHead(PlayerHead.QUESTION_MARK_WHITE.getHead(), ChatColor.GREEN + "Help", List.of(ChatColor.WHITE + "To remove a passive ability, input \"-\".")));
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
    menu.setItem(16, ItemCreator.createItem(Material.IRON_NUGGET, ChatColor.AQUA + "Necklace"));
    menu.setItem(17, ItemCreator.createItem(Material.GOLD_NUGGET, ChatColor.AQUA + "Ring"));
  }

  /**
   * Adds trigger type buttons.
   */
  private void addTriggers() {
    menu.setItem(9, ItemCreator.createItem(Material.BEETROOT_SOUP, ChatColor.AQUA + "Below % HP"));
    menu.setItem(10, ItemCreator.createItem(Material.RED_DYE, ChatColor.AQUA + "Damage Dealt"));
    menu.setItem(11, ItemCreator.createItem(Material.GRAY_DYE, ChatColor.AQUA + "Damage Taken"));
    menu.setItem(12, ItemCreator.createItem(Material.BONE, ChatColor.AQUA + "Kill"));
  }

  /**
   * Maps an item's passive abilities.
   *
   * @return item's passives map
   */
  private Map<String, List<SlotCondition>> mapPassives() {
    NamespacedKey listKey = PluginNamespacedKey.PASSIVE_LIST.getNamespacedKey();
    boolean hasPassives = dataContainer.has(listKey, PersistentDataType.STRING);
    if (hasPassives) {
      Map<String, List<SlotCondition>> existingPassives = new HashMap<>();
      List<String> passives = new ArrayList<>(List.of(dataContainer.get(listKey, PersistentDataType.STRING).split(" ")));
      for (String passive : passives) {
        String[] passiveMeta = passive.split("\\.");
        String slot = passiveMeta[0];
        String condition = passiveMeta[1];
        String passiveType = passiveMeta[2];
        if (existingPassives.containsKey(passiveType)) {
          existingPassives.get(passiveType).add(new SlotCondition(slot, condition));
        } else {
          existingPassives.put(passiveType, new ArrayList<>(List.of(new SlotCondition(slot, condition))));
        }
      }
      return existingPassives;
    } else {
      return null;
    }
  }
}
