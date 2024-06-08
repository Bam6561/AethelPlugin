package me.bam6561.aethelplugin.commands.itemeditor;

import com.google.common.collect.Multimap;
import me.bam6561.aethelplugin.Plugin;
import me.bam6561.aethelplugin.enums.plugin.PlayerHead;
import me.bam6561.aethelplugin.interfaces.Menu;
import me.bam6561.aethelplugin.utils.InventoryPages;
import me.bam6561.aethelplugin.utils.TextFormatter;
import me.bam6561.aethelplugin.utils.item.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a menu that edits an item's Minecraft attributes.
 *
 * @author Danny Nguyen
 * @version 1.27.1
 * @since 1.7.0
 */
public class AttributeMenu implements Menu {
  /**
   * Categorized Minecraft attributes.
   */
  private static final Map<Category, Attribute[]> attributeCategories = Map.of(
      Category.OFFENSE, new Attribute[]{Attribute.PLAYER_BLOCK_INTERACTION_RANGE, Attribute.PLAYER_ENTITY_INTERACTION_RANGE, Attribute.GENERIC_ATTACK_DAMAGE, Attribute.GENERIC_ATTACK_SPEED},
      Category.DEFENSE, new Attribute[]{Attribute.GENERIC_MAX_HEALTH, Attribute.GENERIC_ARMOR_TOUGHNESS, Attribute.GENERIC_ARMOR, Attribute.GENERIC_KNOCKBACK_RESISTANCE},
      Category.MISCELLANEOUS, new Attribute[]{Attribute.GENERIC_MOVEMENT_SPEED, Attribute.GENERIC_LUCK, Attribute.GENERIC_JUMP_STRENGTH, Attribute.GENERIC_SCALE});

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
   * GUI equipment slot.
   */
  private final EquipmentSlot eSlot;

  /**
   * ItemStack's Minecraft attributes.
   */
  private final Multimap<Attribute, AttributeModifier> existingAttributes;

  /**
   * Associates a new Attribute menu with its user and editing item.
   *
   * @param user  user
   * @param eSlot equipment slot
   */
  public AttributeMenu(@NotNull Player user, @NotNull EquipmentSlot eSlot) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.eSlot = Objects.requireNonNull(eSlot, "Null slot");
    this.item = Plugin.getData().getEditedItemCache().getEditedItems().get(user.getUniqueId());
    this.existingAttributes = item.getItemMeta().getAttributeModifiers();
    this.menu = createMenu();
  }

  /**
   * Creates and names a new Attribute menu with its equipment slot.
   *
   * @return Attribute menu
   */
  private Inventory createMenu() {
    String action = null;
    switch (eSlot) {
      case HEAD, CHEST, LEGS, FEET, HAND -> action = TextFormatter.capitalizeWord(eSlot.name());
      case OFF_HAND -> action = "Off Hand";
    }
    Inventory inv = Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "Attributes " + ChatColor.DARK_AQUA + action);
    inv.setItem(4, item);
    return inv;
  }

  /**
   * Sets the menu to display interactions with attributes.
   *
   * @return Attribute menu
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
   * Adds attributes.
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
   * Adds equipment slot buttons.
   */
  private void addActions() {
    menu.setItem(9, ItemCreator.createItem(Material.IRON_HELMET, ChatColor.AQUA + "Head", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(10, ItemCreator.createItem(Material.IRON_CHESTPLATE, ChatColor.AQUA + "Chest", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(11, ItemCreator.createItem(Material.IRON_LEGGINGS, ChatColor.AQUA + "Legs", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(12, ItemCreator.createItem(Material.IRON_BOOTS, ChatColor.AQUA + "Feet", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(14, ItemCreator.createItem(Material.IRON_SWORD, ChatColor.AQUA + "Hand", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(15, ItemCreator.createItem(Material.SHIELD, ChatColor.AQUA + "Off Hand", ItemFlag.HIDE_ATTRIBUTES));
  }

  /**
   * Adds a category of attributes.
   *
   * @param category attribute category
   * @param invSlot  inventory slot
   */
  private void addAttributeCategory(Category category, int invSlot) {
    for (Attribute attribute : attributeCategories.get(category)) {
      String attributeName = TextFormatter.capitalizePhrase(attribute.name());
      boolean disabled = existingAttributes == null || existingAttributes.get(attribute).isEmpty();
      if (disabled) {
        menu.setItem(invSlot, ItemCreator.createItem(Material.ITEM_FRAME, ChatColor.AQUA + attributeName));
      } else {
        List<String> lore = new ArrayList<>();
        for (AttributeModifier attributeModifier : existingAttributes.get(attribute)) {
          if (attributeModifier.getSlot() != null) {
            lore.add(ChatColor.WHITE + TextFormatter.capitalizePhrase(attributeModifier.getSlot().name()) + ": " + attributeModifier.getAmount());
          } else {
            lore.add(ChatColor.WHITE + "All: " + attributeModifier.getAmount());
          }
        }
        menu.setItem(invSlot, ItemCreator.createItem(Material.GLOW_ITEM_FRAME, ChatColor.AQUA + attributeName, lore));
      }
      invSlot++;
    }
  }

  /**
   * Minecraft attribute categories.
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
