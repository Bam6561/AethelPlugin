package me.bam6561.aethelplugin.commands.itemeditor;

import me.bam6561.aethelplugin.Plugin;
import me.bam6561.aethelplugin.enums.plugin.Key;
import me.bam6561.aethelplugin.enums.plugin.KeyHeader;
import me.bam6561.aethelplugin.enums.plugin.PlayerHead;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a menu that allows the user to edit an item's
 * {@link Key#ACTIVE_EDIBLE_LIST edible active abilities}.
 *
 * @author Danny Nguyen
 * @version 1.25.0
 * @since 1.25.0
 */
public class EdibleMenu implements Menu {
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
   * ItemStack's persistent tags.
   */
  private final PersistentDataContainer itemTags;

  /**
   * ItemStack {@link Key#ACTIVE_EDIBLE_LIST edible active abilities}.
   */
  private final Set<String> existingActives;

  /**
   * Associates a new Edible menu with its user and item.
   *
   * @param user user
   */
  public EdibleMenu(@NotNull Player user) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.item = Plugin.getData().getEditedItemCache().getEditedItems().get(user.getUniqueId());
    this.itemTags = item.getItemMeta().getPersistentDataContainer();
    this.existingActives = setOfActives();
    this.menu = createMenu();
  }

  /**
   * Creates and names a new Edible menu.
   *
   * @return Edible menu
   */
  private Inventory createMenu() {
    Inventory inv = Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "Edible Actives");
    inv.setItem(4, item);
    return inv;
  }

  /**
   * Sets the menu to display interactions with {@link ActiveAbilityType}.
   *
   * @return Edible menu
   */
  @NotNull
  public Inventory getMainMenu() {
    addActives();
    addContext();
    InventoryPages.addBackButton(menu, 6);
    return menu;
  }

  /**
   * Adds {@link ActiveAbilityType edible active abilities}.
   */
  private void addActives() {
    int invSlot = 18;
    if (existingActives != null) {
      for (ActiveAbilityType activeAbilityType : ActiveAbilityType.values()) {
        String activeName = activeAbilityType.getProperName();
        String activeId = activeAbilityType.getId();
        boolean enabled = existingActives.contains(activeId);
        if (enabled) {
          NamespacedKey activeKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.ACTIVE_EDIBLE.getHeader() + activeId);
          String activeValue = itemTags.get(activeKey, PersistentDataType.STRING);
          List<String> lore = List.of(ChatColor.WHITE + TextFormatter.capitalizePhrase(activeValue));
          menu.setItem(invSlot, ItemCreator.createItem(Material.GLOW_BERRIES, ChatColor.AQUA + activeName, lore));
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
   * Gets an item's {@link Key#ACTIVE_EDIBLE_LIST edible active abilities} set.
   *
   * @return item's {@link Key#ACTIVE_EDIBLE_LIST edible actives} set
   */
  private Set<String> setOfActives() {
    NamespacedKey listKey = Key.ACTIVE_EDIBLE_LIST.getNamespacedKey();
    boolean hasActives = itemTags.has(listKey, PersistentDataType.STRING);
    if (hasActives) {
      return Set.of(itemTags.get(listKey, PersistentDataType.STRING).split(" "));
    } else {
      return null;
    }
  }
}
