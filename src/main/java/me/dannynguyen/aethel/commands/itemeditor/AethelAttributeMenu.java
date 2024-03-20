package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.plugin.enums.KeyHeader;
import me.dannynguyen.aethel.plugin.enums.PlayerHead;
import me.dannynguyen.aethel.plugin.enums.PluginNamespacedKey;
import me.dannynguyen.aethel.plugin.interfaces.Menu;
import me.dannynguyen.aethel.rpg.enums.AethelAttributeType;
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
 * Represents a menu that edits an item's Aethel attributes.
 *
 * @author Danny Nguyen
 * @version 1.17.6
 * @since 1.14.1
 */
public class AethelAttributeMenu implements Menu {
  /**
   * Categorized Aethel attributes.
   */
  private static final Map<String, AethelAttributeType[]> attributeCategories = Map.of(
      "offense", new AethelAttributeType[]{AethelAttributeType.CRITICAL_CHANCE, AethelAttributeType.CRITICAL_DAMAGE},
      "defense", new AethelAttributeType[]{AethelAttributeType.MAX_HEALTH, AethelAttributeType.COUNTER_CHANCE, AethelAttributeType.DODGE_CHANCE, AethelAttributeType.ARMOR_TOUGHNESS},
      "other", new AethelAttributeType[]{AethelAttributeType.ITEM_DAMAGE, AethelAttributeType.ITEM_COOLDOWN});

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
   * GUI action.
   */
  private final RpgEquipmentSlot eSlot;

  /**
   * ItemStack data container.
   */
  private final PersistentDataContainer dataContainer;

  /**
   * ItemStack Aethel attributes.
   */
  private final Map<String, List<String>> existingAethelAttributes;

  /**
   * Associates a new AethelAttribute menu with its user and editing item.
   *
   * @param user  user
   * @param eSlot type of interaction
   */
  public AethelAttributeMenu(@NotNull Player user, @NotNull RpgEquipmentSlot eSlot) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.eSlot = Objects.requireNonNull(eSlot, "Null slot");
    this.item = Plugin.getData().getEditedItemCache().getEditedItems().get(user.getUniqueId());
    this.dataContainer = item.getItemMeta().getPersistentDataContainer();
    this.existingAethelAttributes = mapAethelAttributes();
    this.menu = createMenu();
  }

  /**
   * Creates and names a new AethelAttribute menu with its action.
   *
   * @return AethelAttribute menu
   */
  private Inventory createMenu() {
    Inventory inv = Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "Aethel Attributes " + ChatColor.DARK_AQUA + eSlot.getProperName());
    inv.setItem(1, item);
    return inv;
  }

  /**
   * Sets the menu to display interactions with attributes.
   *
   * @return AethelAttribute menu
   */
  @NotNull
  public Inventory getMainMenu() {
    addAttributes();
    addContext();
    addActions();
    InventoryPages.addBackButton(menu, 2);
    return menu;
  }

  /**
   * Adds attributes.
   */
  private void addAttributes() {
    addAttributeCategory("offense", 19);
    addAttributeCategory("defense", 28);
    addAttributeCategory("other", 37);
  }

  /**
   * Adds contextual help.
   */
  private void addContext() {
    menu.setItem(0, ItemCreator.createPluginPlayerHead(PlayerHead.QUESTION_MARK_WHITE.getHead(), ChatColor.GREEN + "Help", List.of(ChatColor.WHITE + "To remove a attribute, input \"-\".")));
    menu.setItem(18, ItemCreator.createItem(Material.IRON_SWORD, ChatColor.GREEN + "Offense", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(27, ItemCreator.createItem(Material.IRON_CHESTPLATE, ChatColor.GREEN + "Defense", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(36, ItemCreator.createItem(Material.SPYGLASS, ChatColor.GREEN + "Other"));
  }

  /**
   * Adds RPG equipment slot buttons.
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
   * Maps an item's Aethel attributes.
   *
   * @return item's Aethel attributes map
   */
  private Map<String, List<String>> mapAethelAttributes() {
    NamespacedKey listKey = PluginNamespacedKey.ATTRIBUTE_LIST.getNamespacedKey();
    boolean hasAttributes = dataContainer.has(listKey, PersistentDataType.STRING);
    if (hasAttributes) {
      Map<String, List<String>> existingAethelAttributes = new HashMap<>();
      List<String> attributes = new ArrayList<>(List.of(dataContainer.get(listKey, PersistentDataType.STRING).split(" ")));
      for (String attribute : attributes) {
        String attributeType = attribute.substring(attribute.indexOf(".") + 1);
        if (existingAethelAttributes.containsKey(attributeType)) {
          existingAethelAttributes.get(attributeType).add(attribute.substring(0, attribute.indexOf(".")));
        } else {
          existingAethelAttributes.put(attributeType, new ArrayList<>(List.of(attribute.substring(0, attribute.indexOf(".")))));
        }
      }
      return existingAethelAttributes;
    } else {
      return null;
    }
  }

  /**
   * Adds a category of attributes.
   *
   * @param category attribute category
   * @param invSlot  inventory slot
   */
  private void addAttributeCategory(String category, int invSlot) {
    if (existingAethelAttributes != null) {
      for (AethelAttributeType attribute : attributeCategories.get(category)) {
        String attributeName = attribute.getProperName();
        String attributeId = attribute.getId();
        boolean enabled = existingAethelAttributes.containsKey(attributeId);
        if (enabled) {
          List<String> lore = new ArrayList<>();
          for (String slot : existingAethelAttributes.get(attributeId)) {
            NamespacedKey attributeKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + slot + "." + attributeId);
            double attributeValue = dataContainer.get(attributeKey, PersistentDataType.DOUBLE);
            lore.add(ChatColor.WHITE + TextFormatter.capitalizePhrase(slot + ": " + attributeValue));
          }
          menu.setItem(invSlot, ItemCreator.createItem(Material.GLOW_ITEM_FRAME, ChatColor.AQUA + attributeName, lore));
        } else {
          menu.setItem(invSlot, ItemCreator.createItem(Material.ITEM_FRAME, ChatColor.AQUA + attributeName));
        }
        invSlot++;
      }
    } else {
      for (AethelAttributeType attribute : attributeCategories.get(category)) {
        menu.setItem(invSlot, ItemCreator.createItem(Material.ITEM_FRAME, ChatColor.AQUA + attribute.getProperName()));
        invSlot++;
      }
    }
  }
}
