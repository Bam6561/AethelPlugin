package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.systems.plugin.PluginData;
import me.dannynguyen.aethel.systems.plugin.PluginEnum;
import me.dannynguyen.aethel.utility.InventoryPages;
import me.dannynguyen.aethel.utility.ItemCreator;
import me.dannynguyen.aethel.utility.TextFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents a menu that edits an item's enchantments.
 *
 * @author Danny Nguyen
 * @version 1.9.21
 * @since 1.6.16
 */
class EnchantmentEditorMenu {
  /**
   * List of sorted enchantments by name.
   */
  private static final List<Enchantment> enchantments = sortEnchantments();

  /**
   * EnchantmentEditor GUI.
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
   * Associates a new EnchantmentEditor menu with its user and editing item.
   *
   * @param user user
   */
  protected EnchantmentEditorMenu(@NotNull Player user) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.item = PluginData.editedItemCache.getEditedItemMap().get(user);
    this.menu = createMenu();
  }

  /**
   * Creates and names an EnchantmentEditor menu.
   *
   * @return EnchantmentEditor menu
   */
  private Inventory createMenu() {
    Inventory inv = Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "ItemEditor " + ChatColor.DARK_AQUA + "Enchantments");
    inv.setItem(4, item);
    return inv;
  }

  /**
   * Sets the menu to display interactions with Minecraft enchantments.
   *
   * @return EnchantmentEditor menu
   */
  @NotNull
  protected Inventory openMenu() {
    addEnchantments();
    addContext();
    InventoryPages.addBackButton(menu, 6);
    return menu;
  }

  /**
   * Adds enchantments.
   */
  private void addEnchantments() {
    Map<Enchantment, Integer> metaEnchantments = item.getItemMeta().getEnchants();
    int invSlot = 9;
    for (Enchantment enchantment : enchantments) {
      String enchantmentName = ChatColor.AQUA + TextFormatter.capitalizePhrase(enchantment.getKey().getKey());
      boolean disabled = metaEnchantments.get(enchantment) == null;
      menu.setItem(invSlot, disabled ?
          ItemCreator.createItem(Material.BOOK, enchantmentName) :
          ItemCreator.createItem(Material.ENCHANTED_BOOK, enchantmentName, List.of(ChatColor.WHITE + String.valueOf(metaEnchantments.get(enchantment)))));
      invSlot++;
    }
  }

  /**
   * Adds contextual help.
   */
  private void addContext() {
    menu.setItem(2, ItemCreator.createPluginPlayerHead(PluginEnum.PlayerHead.QUESTION_MARK_WHITE.getHead(), ChatColor.GREEN + "Help", List.of(ChatColor.WHITE + "To remove an enchant, input \"0\".")));
  }

  /**
   * Sorts enchantments by name.
   *
   * @return sorted enchantments
   */
  private static List<Enchantment> sortEnchantments() {
    List<Enchantment> enchantments = new ArrayList<>(List.of(Enchantment.values()));
    Comparator<Enchantment> enchantmentComparator = Comparator.comparing(e -> e.getKey().getKey());
    enchantments.sort(enchantmentComparator);
    return enchantments;
  }
}
