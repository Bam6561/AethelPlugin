package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.plugin.enums.KeyHeader;
import me.dannynguyen.aethel.plugin.enums.PlayerHead;
import me.dannynguyen.aethel.plugin.enums.PluginNamespacedKey;
import me.dannynguyen.aethel.plugin.interfaces.Menu;
import me.dannynguyen.aethel.rpg.enums.PassiveAbilityType;
import me.dannynguyen.aethel.rpg.enums.RpgEquipmentSlot;
import me.dannynguyen.aethel.rpg.enums.Trigger;
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
 * {@link PluginNamespacedKey#PASSIVE_LIST passive abilities}.
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
   * {@link RpgEquipmentSlot}
   */
  private final RpgEquipmentSlot eSlot;

  /**
   * {@link Trigger}
   */
  private final Trigger trigger;

  /**
   * ItemStack data container.
   */
  private final PersistentDataContainer dataContainer;

  /**
   * ItemStack {@link PluginNamespacedKey#PASSIVE_LIST passive abilities}.
   */
  private final Map<String, List<PassiveLoreIdentifier>> existingPassives;

  /**
   * Associates a new Passive menu with its user and item.
   *
   * @param user    user
   * @param trigger {@link Trigger}
   * @param eSlot   {@link RpgEquipmentSlot}
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
   * Creates and names a new Passive menu with its {@link RpgEquipmentSlot}.
   *
   * @return Passive menu
   */
  private Inventory createMenu() {
    Inventory inv = Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "Passives " + ChatColor.DARK_AQUA + eSlot.getProperName() + " " + ChatColor.YELLOW + trigger.getProperName());
    inv.setItem(1, item);
    return inv;
  }

  /**
   * Sets the menu to display interactions with {@link PassiveAbilityType}.
   *
   * @return Passive menu
   */
  @NotNull
  public Inventory getMainMenu() {
    addPassives();
    addContext();
    addActions();
    addTriggers();
    InventoryPages.addBackButton(menu, 2);
    return menu;
  }

  /**
   * Adds {@link PassiveAbilityType passive abilities}.
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
          for (PassiveLoreIdentifier passiveLoreIdentifier : existingPassives.get(passiveId)) {
            NamespacedKey passiveKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.PASSIVE.getHeader() + passiveLoreIdentifier.getSlot() + "." + passiveLoreIdentifier.getCondition() + "." + passiveId);
            String passiveValue = dataContainer.get(passiveKey, PersistentDataType.STRING);
            lore.add(ChatColor.WHITE + TextFormatter.capitalizePhrase(passiveLoreIdentifier.getSlot() + " " + passiveLoreIdentifier.getCondition() + ": " + passiveValue));
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
   * Adds {@link RpgEquipmentSlot} buttons.
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
   * Adds {@link Trigger} buttons.
   */
  private void addTriggers() {
    menu.setItem(9, ItemCreator.createItem(Material.BEETROOT_SOUP, ChatColor.AQUA + "Below % HP"));
    menu.setItem(10, ItemCreator.createItem(Material.RED_DYE, ChatColor.AQUA + "Damage Dealt"));
    menu.setItem(11, ItemCreator.createItem(Material.GRAY_DYE, ChatColor.AQUA + "Damage Taken"));
    menu.setItem(12, ItemCreator.createItem(Material.BONE, ChatColor.AQUA + "Kill"));
  }

  /**
   * Maps an item's {@link PluginNamespacedKey#PASSIVE_LIST passive abilities}.
   *
   * @return item's {@link PluginNamespacedKey#PASSIVE_LIST passives} map
   */
  private Map<String, List<PassiveLoreIdentifier>> mapPassives() {
    NamespacedKey listKey = PluginNamespacedKey.PASSIVE_LIST.getNamespacedKey();
    boolean hasPassives = dataContainer.has(listKey, PersistentDataType.STRING);
    if (hasPassives) {
      Map<String, List<PassiveLoreIdentifier>> existingPassives = new HashMap<>();
      List<String> passives = new ArrayList<>(List.of(dataContainer.get(listKey, PersistentDataType.STRING).split(" ")));
      for (String passive : passives) {
        String[] passiveMeta = passive.split("\\.");
        String slot = passiveMeta[0];
        String condition = passiveMeta[1];
        String passiveType = passiveMeta[2];
        if (existingPassives.containsKey(passiveType)) {
          existingPassives.get(passiveType).add(new PassiveLoreIdentifier(slot, condition));
        } else {
          existingPassives.put(passiveType, new ArrayList<>(List.of(new PassiveLoreIdentifier(slot, condition))));
        }
      }
      return existingPassives;
    } else {
      return null;
    }
  }
}
