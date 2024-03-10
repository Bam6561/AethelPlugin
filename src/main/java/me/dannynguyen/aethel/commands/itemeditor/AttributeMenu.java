package me.dannynguyen.aethel.commands.itemeditor;

import com.google.common.collect.Multimap;
import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.systems.plugin.PlayerHead;
import me.dannynguyen.aethel.utility.InventoryPages;
import me.dannynguyen.aethel.utility.ItemCreator;
import me.dannynguyen.aethel.utility.TextFormatter;
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
 * @version 1.15.5
 * @since 1.7.0
 */
class AttributeMenu {
  /**
   * Categorized Minecraft attributes.
   */
  private static final Map<String, Attribute[]> attributeCategories = Map.of(
      "offense", new Attribute[]{Attribute.GENERIC_ATTACK_DAMAGE, Attribute.GENERIC_ATTACK_SPEED},
      "defense", new Attribute[]{Attribute.GENERIC_MAX_HEALTH, Attribute.GENERIC_ARMOR, Attribute.GENERIC_ARMOR_TOUGHNESS, Attribute.GENERIC_KNOCKBACK_RESISTANCE},
      "other", new Attribute[]{Attribute.GENERIC_MOVEMENT_SPEED, Attribute.GENERIC_LUCK});

  /**
   * Attribute GUI.
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
   * ItemStack's Minecraft attributes.
   */
  private final Multimap<Attribute, AttributeModifier> attributeModifiers;

  /**
   * Associates a new Attribute menu with its user and editing item.
   *
   * @param user user
   * @param slot type of interaction
   */
  protected AttributeMenu(@NotNull Player user, @NotNull EquipmentSlot slot) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.slot = Objects.requireNonNull(slot, "Null slot");
    this.item = Plugin.getData().getEditedItemCache().getEditedItemMap().get(user.getUniqueId());
    this.attributeModifiers = item.getItemMeta().getAttributeModifiers();
    this.menu = createMenu();
  }

  /**
   * Creates and names a new Attribute menu with its action.
   *
   * @return Attribute menu
   */
  private Inventory createMenu() {
    String actionString = "";
    switch (slot) {
      case HEAD, CHEST, LEGS, FEET, HAND -> actionString = TextFormatter.capitalizeWord(slot.name());
      case OFF_HAND -> actionString = "Off Hand";
    }
    Inventory inv = Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "ItemEditor " + ChatColor.DARK_AQUA + "Attributes " + ChatColor.YELLOW + actionString);
    inv.setItem(1, item);
    return inv;
  }

  /**
   * Sets the menu to display interactions with attributes.
   *
   * @return Attribute menu
   */
  @NotNull
  protected Inventory openMenu() {
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
   * Adds a category of attributes.
   *
   * @param category attribute category
   * @param invSlot  inventory slot
   */
  private void addAttributeCategory(String category, int invSlot) {
    for (Attribute attribute : attributeCategories.get(category)) {
      String attributeName = TextFormatter.capitalizePhrase(attribute.name());
      boolean disabled = attributeModifiers == null || attributeModifiers.get(attribute).isEmpty();
      if (disabled) {
        menu.setItem(invSlot, ItemCreator.createItem(Material.ITEM_FRAME, ChatColor.AQUA + attributeName));
      } else {
        List<String> lore = new ArrayList<>();
        for (AttributeModifier attributeModifier : attributeModifiers.get(attribute)) {
          lore.add(ChatColor.WHITE + "" + TextFormatter.capitalizePhrase(attributeModifier.getSlot().name()) + ": " + attributeModifier.getAmount());
        }
        menu.setItem(invSlot, ItemCreator.createItem(Material.GLOW_ITEM_FRAME, ChatColor.AQUA + attributeName, lore));
      }
      invSlot++;
    }
  }
}
