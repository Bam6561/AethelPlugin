package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.PlayerHead;
import me.dannynguyen.aethel.interfaces.Menu;
import me.dannynguyen.aethel.utils.InventoryPages;
import me.dannynguyen.aethel.utils.TextFormatter;
import me.dannynguyen.aethel.utils.item.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents a menu that edits an item's potion effects.
 *
 * @author Danny Nguyen
 * @version 1.17.6
 * @since 1.14.0
 */
public class PotionMenu implements Menu {
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
   * Associates a new Potion menu with its user and editing item.
   *
   * @param user user
   */
  public PotionMenu(@NotNull Player user) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.item = Plugin.getData().getEditedItemCache().getEditedItems().get(user.getUniqueId());
    this.menu = createMenu();
  }

  /**
   * Creates and names a Potion menu.
   *
   * @return Potion menu
   */
  private Inventory createMenu() {
    Inventory inv = Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "Potion Effects");
    inv.setItem(4, item);
    return inv;
  }

  /**
   * Sets the menu to display interactions with potion effects.
   *
   * @return Potion menu
   */
  @NotNull
  public Inventory getMainMenu() {
    addColor();
    addPotionEffects();
    addContext();
    InventoryPages.addBackButton(menu, 6);
    return menu;
  }

  /**
   * Adds potion color.
   */
  private void addColor() {
    if (item.getItemMeta() instanceof PotionMeta) {
      menu.setItem(5, ItemCreator.createItem(Material.NETHER_WART, ChatColor.AQUA + "Set Color"));
    }
  }

  /**
   * Adds potion effects.
   */
  private void addPotionEffects() {
    if (item.getItemMeta() instanceof PotionMeta potion) {
      Map<PotionEffectType, PotionEffect> potionEffects = getPotionEffects(potion);
      int invSlot = 9;
      for (PotionEffectType potionEffectType : PotionEffectType.values()) {
        if (potionEffects.containsKey(potionEffectType)) {
          PotionEffect potionEffect = potionEffects.get(potionEffectType);
          String duration = ChatColor.WHITE + String.valueOf(potionEffect.getDuration());
          String amplifier = ChatColor.YELLOW + (potionEffect.getAmplifier() == 0 ? "" : String.valueOf(potionEffect.getAmplifier() + 1));
          menu.setItem(invSlot, ItemCreator.createItem(Material.POTION, ChatColor.AQUA + TextFormatter.capitalizePhrase(potionEffectType.getName()), List.of(duration + " " + amplifier), ItemFlag.HIDE_POTION_EFFECTS));
        } else {
          menu.setItem(invSlot, ItemCreator.createItem(Material.GLASS_BOTTLE, ChatColor.AQUA + TextFormatter.capitalizePhrase(potionEffectType.getName())));
        }
        invSlot++;
      }
    }
  }

  /**
   * Adds contextual help.
   */
  private void addContext() {
    List<String> lore = new ArrayList<>(List.of(
        ChatColor.WHITE + "Base potion effects cannot be modified.",
        ChatColor.WHITE + "To add an effect, input the duration in",
        ChatColor.WHITE + "ticks, amplifier, and ambient (true/false).",
        ChatColor.WHITE + "To remove an effect, input \"-\"."
    ));
    menu.setItem(2, ItemCreator.createPluginPlayerHead(PlayerHead.QUESTION_MARK_WHITE.getHead(), ChatColor.GREEN + "Help", lore));
  }

  /**
   * Gets an item's potion effects.
   *
   * @param potion potion meta
   * @return item's potion effects
   */
  private Map<PotionEffectType, PotionEffect> getPotionEffects(PotionMeta potion) {
    Map<PotionEffectType, PotionEffect> potionEffects = new HashMap<>();
    for (PotionEffect basePotionEffect : potion.getBasePotionType().getPotionEffects()) {
      potionEffects.put(basePotionEffect.getType(), basePotionEffect);
    }
    if (potion.hasCustomEffects()) {
      for (PotionEffect customPotionEffect : potion.getCustomEffects()) {
        potionEffects.put(customPotionEffect.getType(), customPotionEffect);
      }
    }
    return potionEffects;
  }
}
