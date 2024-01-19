package me.dannynguyen.aethel.inventories.characterprofile;

import me.dannynguyen.aethel.creators.ItemCreator;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.text.DecimalFormat;
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
        ChatColor.WHITE + "Equipment Slots " + ChatColor.GREEN + "->",
        List.of(ChatColor.AQUA + "Head" + ChatColor.WHITE + " | "
                + ChatColor.AQUA + "Main Hand" + ChatColor.WHITE + " | " + ChatColor.AQUA + "Off Hand",
            ChatColor.AQUA + "Chest" + ChatColor.WHITE + " | " + ChatColor.AQUA + "Necklace",
            ChatColor.AQUA + "Legs" + ChatColor.WHITE + " | " + ChatColor.AQUA + "Ring",
            ChatColor.AQUA + "Boots" + ChatColor.WHITE + " | " + ChatColor.AQUA + "Ring")));
    inv.setItem(10, player.getInventory().getHelmet());
    inv.setItem(19, player.getInventory().getChestplate());
    inv.setItem(28, player.getInventory().getLeggings());
    inv.setItem(37, player.getInventory().getBoots());
    inv.setItem(11, player.getInventory().getItemInMainHand());
    inv.setItem(12, player.getInventory().getItemInOffHand());
  }

  /**
   * Adds the player's attributes.
   *
   * @param player interacting player
   * @param inv    interacting inv
   */
  private static void addAttributes(Player player, Inventory inv) {
    DecimalFormat thousandth = new DecimalFormat(("0.000"));

    inv.setItem(14, ItemCreator.createItem(Material.IRON_SWORD,
        ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Offense",
        List.of(ChatColor.RED + "" + player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue() + " Damage",
            ChatColor.GOLD + "" + thousandth.format(player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getValue()) + " Attack Speed",
            ChatColor.GREEN + "Crit Chance",
            ChatColor.DARK_GREEN + "Crit Damage")));
    inv.setItem(15, ItemCreator.createItem(Material.SHIELD,
        ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Defense",
        List.of(ChatColor.WHITE + "" + player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + " Max Health",
            ChatColor.GRAY + "" + player.getAttribute(Attribute.GENERIC_ARMOR).getValue() + " Armor",
            ChatColor.GRAY + "" + player.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue() + " Armor Toughness",
            ChatColor.AQUA + "" + thousandth.format(player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue() * 20) + " Speed",
            ChatColor.BLUE + "Block",
            ChatColor.RED + "Parry",
            ChatColor.DARK_AQUA + "Dodge")));
    inv.setItem(16, ItemCreator.createItem(Material.NETHER_STAR,
        ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Miscellaneous",
        List.of(ChatColor.LIGHT_PURPLE + "Ability Damage",
            ChatColor.DARK_PURPLE + "Ability Cooldown",
            ChatColor.YELLOW + "Status",
            ChatColor.DARK_GRAY + "" + player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).getValue() + " Knockback Resistance",
            ChatColor.GREEN + "" + player.getAttribute(Attribute.GENERIC_LUCK).getValue() + " Luck")));
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
