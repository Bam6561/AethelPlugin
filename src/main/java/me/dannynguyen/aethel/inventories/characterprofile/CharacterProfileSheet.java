package me.dannynguyen.aethel.inventories.characterprofile;

import me.dannynguyen.aethel.creators.ItemCreator;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

/**
 * CharacterSheet is an inventory under the CharacterProfile command that
 * shows the player's equipment and attributes within the rpg context.
 *
 * @author Danny Nguyen
 * @version 1.6.3
 * @since 1.6.3
 */
public class CharacterProfileSheet {
  /**
   * Creates a CharacterSheet with its equipment and attributes.
   *
   * @param player interacting player
   * @return CharacterSheet with equipment and attributes
   */
  public static Inventory openCharacterSheet(Player player) {
    Inventory inv = createInventory(player);
    addActionButtons(player, inv);
    addEquipment(player, inv);
    addAttributes(player, inv);
    addExtras(player, inv);
    return inv;
  }

  /**
   * Creates and names a CharacterSheet inventory to its player.
   *
   * @param player interacting player
   * @return CharacterSheet inventory
   */
  private static Inventory createInventory(Player player) {
    return Bukkit.createInventory(player, 54, ChatColor.DARK_PURPLE + player.getName());
  }

  /**
   * Adds the quests, collectibles, and settings buttons.
   *
   * @param player interacting player
   * @param inv    interacting inv
   */
  private static void addActionButtons(Player player, Inventory inv) {
    inv.setItem(3, ItemCreator.createItem(Material.BOOK, ChatColor.AQUA + "Quests"));
    inv.setItem(4, ItemCreator.createItem(Material.CHEST, ChatColor.AQUA + "Collectibles"));
    inv.setItem(5, ItemCreator.createItem(Material.COMMAND_BLOCK, ChatColor.AQUA + "Settings"));
  }

  /**
   * Adds the player's equipped items.
   *
   * @param player interacting player
   * @param inv    interacting inv
   */
  private static void addEquipment(Player player, Inventory inv) {
    inv.setItem(9, ItemCreator.createPlayerHead("WHITE_QUESTION_MARK",
        ChatColor.WHITE + "Equipment Slots",
        List.of(ChatColor.AQUA + "Head" + ChatColor.WHITE + " | "
                + ChatColor.AQUA + "Main Hand" + ChatColor.WHITE + " | " + ChatColor.AQUA + "Off Hand",
            ChatColor.AQUA + "Chest" + ChatColor.WHITE + " | " + ChatColor.AQUA + "Necklace",
            ChatColor.AQUA + "Legs" + ChatColor.WHITE + " | " + ChatColor.AQUA + "Ring",
            ChatColor.AQUA + "Boots" + ChatColor.WHITE + " | " + ChatColor.AQUA + "Ring")));
  }

  /**
   * Adds the player's attributes.
   *
   * @param player interacting player
   * @param inv    interacting inv
   */
  private static void addAttributes(Player player, Inventory inv) {
    inv.setItem(14, ItemCreator.createItem(Material.IRON_SWORD,
        ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Offense",
        List.of(ChatColor.RED + "Damage",
            ChatColor.GOLD + "Attack Speed",
            ChatColor.GREEN + "Crit Chance",
            ChatColor.DARK_PURPLE + "Crit Damage")));
    inv.setItem(15, ItemCreator.createItem(Material.SHIELD,
        ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Defense",
        List.of(ChatColor.LIGHT_PURPLE + "Max Health",
            ChatColor.GRAY + "Armor",
            ChatColor.GRAY + "Armor Toughness",
            ChatColor.AQUA + "Speed",
            ChatColor.BLUE + "Block",
            ChatColor.DARK_RED + "Parry",
            ChatColor.DARK_AQUA + "Dodge")));
    inv.setItem(16, ItemCreator.createItem(Material.NETHER_STAR,
        ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Miscellaneous",
        List.of(ChatColor.LIGHT_PURPLE + "Ability Damage",
            ChatColor.DARK_GREEN + "Ability Cooldown",
            ChatColor.YELLOW + "Status",
            ChatColor.DARK_GRAY + "Knockback Resistance")));
  }

  /**
   * Adds the player's currency.
   *
   * @param player interacting player
   * @param inv    interacting inv
   */
  private static void addExtras(Player player, Inventory inv) {
    inv.setItem(49, ItemCreator.createItem(Material.SUNFLOWER, ChatColor.YELLOW + "Currency"));
  }
}
