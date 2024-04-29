package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.plugin.KeyHeader;
import me.dannynguyen.aethel.enums.plugin.PlayerHead;
import me.dannynguyen.aethel.enums.rpg.AethelAttribute;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.interfaces.Menu;
import me.dannynguyen.aethel.utils.InventoryPages;
import me.dannynguyen.aethel.utils.TextFormatter;
import me.dannynguyen.aethel.utils.item.ItemCreator;
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
 * Represents a menu that edits an item's {@link Key#ATTRIBUTE_LIST Aethel attributes}.
 *
 * @author Danny Nguyen
 * @version 1.24.9
 * @since 1.14.1
 */
public class AethelAttributeMenu implements Menu {
  /**
   * Categorized {@link AethelAttribute}.
   */
  private static final Map<Category, AethelAttribute[]> attributeCategories = Map.of(
      Category.OFFENSE, new AethelAttribute[]{AethelAttribute.CRITICAL_CHANCE, AethelAttribute.CRITICAL_DAMAGE, AethelAttribute.FEINT_SKILL, AethelAttribute.ACCURACY_SKILL},
      Category.DEFENSE, new AethelAttribute[]{AethelAttribute.MAX_HEALTH, AethelAttribute.COUNTER_CHANCE, AethelAttribute.DODGE_CHANCE, AethelAttribute.ARMOR_TOUGHNESS, AethelAttribute.ARMOR},
      Category.MISCELLANEOUS, new AethelAttribute[]{AethelAttribute.ITEM_DAMAGE, AethelAttribute.ITEM_COOLDOWN, AethelAttribute.TENACITY});

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
   * ItemStack {@link Key#ATTRIBUTE_LIST Aethel attributes}.
   */
  private final Map<String, List<String>> existingAethelAttributes;

  /**
   * Associates a new AethelAttribute menu with its user and editing item.
   *
   * @param user  user
   * @param eSlot {@link RpgEquipmentSlot}
   */
  public AethelAttributeMenu(@NotNull Player user, @NotNull RpgEquipmentSlot eSlot) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.eSlot = Objects.requireNonNull(eSlot, "Null slot");
    this.item = Plugin.getData().getEditedItemCache().getEditedItems().get(user.getUniqueId());
    this.itemTags = item.getItemMeta().getPersistentDataContainer();
    this.existingAethelAttributes = mapAethelAttributes();
    this.menu = createMenu();
  }

  /**
   * Creates and names a new AethelAttribute menu with its {@link RpgEquipmentSlot}.
   *
   * @return AethelAttribute menu
   */
  private Inventory createMenu() {
    Inventory inv = Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "Aethel Attributes " + ChatColor.DARK_AQUA + eSlot.getProperName());
    inv.setItem(4, item);
    return inv;
  }

  /**
   * Sets the menu to display interactions with {@link AethelAttribute}.
   *
   * @return AethelAttribute menu
   */
  @NotNull
  public Inventory getMainMenu() {
    addAttributes();
    addActions();
    addContext();
    InventoryPages.addBackButton(menu, 6);
    return menu;
  }

  /**
   * Adds {@link AethelAttribute attributes}.
   */
  private void addAttributes() {
    addAttributeCategory(Category.OFFENSE, 19);
    addAttributeCategory(Category.DEFENSE, 28);
    addAttributeCategory(Category.MISCELLANEOUS, 37);
  }

  /**
   * Adds contextual help.
   */
  private void addContext() {
    menu.setItem(2, ItemCreator.createPluginPlayerHead(PlayerHead.QUESTION_MARK_WHITE.getHead(), ChatColor.GREEN + "Help", List.of(ChatColor.WHITE + "To remove a attribute, input \"-\".")));
    menu.setItem(18, ItemCreator.createItem(Material.IRON_SWORD, ChatColor.GREEN + "Offense", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(27, ItemCreator.createItem(Material.IRON_CHESTPLATE, ChatColor.GREEN + "Defense", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(36, ItemCreator.createItem(Material.TOTEM_OF_UNDYING, ChatColor.GREEN + "Other"));
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
   * Maps an item's {@link Key#ATTRIBUTE_LIST Aethel attributes}.
   *
   * @return item's {@link Key#ATTRIBUTE_LIST Aethel attributes} map
   */
  private Map<String, List<String>> mapAethelAttributes() {
    NamespacedKey listKey = Key.ATTRIBUTE_LIST.getNamespacedKey();
    boolean hasAttributes = itemTags.has(listKey, PersistentDataType.STRING);
    if (hasAttributes) {
      Map<String, List<String>> existingAethelAttributes = new HashMap<>();
      List<String> attributes = new ArrayList<>(List.of(itemTags.get(listKey, PersistentDataType.STRING).split(" ")));
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
   * Adds a category of {@link AethelAttribute attributes}.
   *
   * @param category {@link AethelAttribute attributes} category
   * @param invSlot  inventory slot
   */
  private void addAttributeCategory(Category category, int invSlot) {
    if (existingAethelAttributes != null) {
      for (AethelAttribute attribute : attributeCategories.get(category)) {
        String attributeName = attribute.getProperName();
        String attributeId = attribute.getId();
        boolean enabled = existingAethelAttributes.containsKey(attributeId);
        if (enabled) {
          List<String> lore = new ArrayList<>();
          for (String slot : existingAethelAttributes.get(attributeId)) {
            NamespacedKey attributeKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + slot + "." + attributeId);
            double attributeValue = itemTags.get(attributeKey, PersistentDataType.DOUBLE);
            lore.add(ChatColor.WHITE + TextFormatter.capitalizePhrase(slot + ": " + attributeValue));
          }
          menu.setItem(invSlot, ItemCreator.createItem(Material.GLOW_ITEM_FRAME, ChatColor.AQUA + attributeName, lore));
        } else {
          menu.setItem(invSlot, ItemCreator.createItem(Material.ITEM_FRAME, ChatColor.AQUA + attributeName));
        }
        invSlot++;
      }
    } else {
      for (AethelAttribute attribute : attributeCategories.get(category)) {
        menu.setItem(invSlot, ItemCreator.createItem(Material.ITEM_FRAME, ChatColor.AQUA + attribute.getProperName()));
        invSlot++;
      }
    }
  }

  /**
   * {@link AethelAttribute} categories.
   */
  private enum Category {
    /**
     * Offensive attributes.
     */
    OFFENSE,

    /**
     * Defensive attributes.
     */
    DEFENSE,

    /**
     * Miscellaneous attributes.
     */
    MISCELLANEOUS
  }
}
