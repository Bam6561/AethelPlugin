package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.plugin.enums.PlayerHead;
import me.dannynguyen.aethel.plugin.interfaces.Menu;
import me.dannynguyen.aethel.util.InventoryPages;
import me.dannynguyen.aethel.util.ItemCreator;
import me.dannynguyen.aethel.util.TextFormatter;
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
 * @version 1.17.6
 * @since 1.6.16
 */
public class EnchantmentMenu implements Menu {
  /**
   * List of sorted enchantments by name.
   */
  private static final List<Enchantment> enchantments = sortEnchantments();

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
   * Associates a new Enchantment menu with its user and editing item.
   *
   * @param user user
   */
  public EnchantmentMenu(@NotNull Player user) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.item = Plugin.getData().getEditedItemCache().getEditedItems().get(user.getUniqueId());
    this.menu = createMenu();
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

  /**
   * Creates and names an Enchantment menu.
   *
   * @return Enchantment menu
   */
  private Inventory createMenu() {
    Inventory inv = Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "Enchantments");
    inv.setItem(4, item);
    return inv;
  }

  /**
   * Sets the menu to display interactions with Minecraft enchantments.
   *
   * @return Enchantment menu
   */
  @NotNull
  public Inventory getMainMenu() {
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
    menu.setItem(2, ItemCreator.createPluginPlayerHead(PlayerHead.QUESTION_MARK_WHITE.getHead(), ChatColor.GREEN + "Help", List.of(ChatColor.WHITE + "To remove an enchant, input \"-\".")));
  }
}
