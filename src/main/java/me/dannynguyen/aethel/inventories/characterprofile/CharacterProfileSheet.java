package me.dannynguyen.aethel.inventories.characterprofile;

import me.dannynguyen.aethel.creators.ItemCreator;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.text.DecimalFormat;
import java.util.List;

/**
 * CharacterSheet is an inventory under the CharacterProfile command that
 * shows the player's equipment and attributes within the rpg context.
 *
 * @author Danny Nguyen
 * @version 1.6.5
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
    addStatusEffects(player, inv);
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
    inv.setItem(3, ItemCreator.createItem(Material.WRITABLE_BOOK, ChatColor.AQUA + "Quests"));
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
        ChatColor.WHITE + "Equipment",
        List.of(ChatColor.GRAY + "Head" + ChatColor.WHITE + "  | "
                + ChatColor.GRAY + "Main Hand" + ChatColor.WHITE + " | " + ChatColor.GRAY + "Off Hand",
            ChatColor.GRAY + "Chest" + ChatColor.WHITE + " | " + ChatColor.GRAY + "Necklace",
            ChatColor.GRAY + "Legs" + ChatColor.WHITE + "  | " + ChatColor.GRAY + "Ring",
            ChatColor.GRAY + "Boots" + ChatColor.WHITE + " | " + ChatColor.GRAY + "Ring")));
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
    DecimalFormat tenths = new DecimalFormat();
    DecimalFormat hundredths = new DecimalFormat();
    DecimalFormat thousandths = new DecimalFormat();

    tenths.setMaximumFractionDigits(1);
    hundredths.setMaximumFractionDigits(2);
    thousandths.setMaximumFractionDigits(3);

    addOffenseAttributes(player, inv, hundredths);
    addDefenseAttributes(player, inv, tenths, hundredths, thousandths);
    addOtherAttributes(player, inv, hundredths);
  }

  /**
   * Adds status effects.
   *
   * @param player interacting player
   * @param inv    interacting inventory
   */
  private static void addStatusEffects(Player player, Inventory inv) {
    StringBuilder potionEffects = new StringBuilder();
    for (PotionEffect potionEffect : player.getActivePotionEffects()) {
      String effectDuration = ChatColor.WHITE + tickTimeConversion(potionEffect.getDuration());
      String effectType = ChatColor.AQUA + capitalizeProperly(potionEffect.getType().getName());
      String effectAmplifier = (potionEffect.getAmplifier() == 0 ? "" :
          String.valueOf(potionEffect.getAmplifier() + 1));

      potionEffects.append(effectDuration).append(" ").
          append(effectType).append(" ").
          append(effectAmplifier).append(",");
    }

    ItemStack potion = new ItemStack(Material.POTION);
    ItemMeta meta = potion.getItemMeta();
    meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Status Effects");
    meta.setLore(List.of(potionEffects.toString().split(",")));
    meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
    potion.setItemMeta(meta);

    inv.setItem(42, potion);
  }

  /**
   * Adds the player's level and currency.
   *
   * @param player interacting player
   * @param inv    interacting inv
   */
  private static void addExtras(Player player, Inventory inv) {
    String level = ChatColor.DARK_GREEN + "" + player.getLevel() + " LVL";
    String exp = ChatColor.GREEN + "" + player.getTotalExperience() + " EXP";

    inv.setItem(49, ItemCreator.createItem(Material.NAME_TAG,
        ChatColor.DARK_PURPLE + player.getName(),
        List.of(level,
            exp,
            ChatColor.WHITE + "0 Silver")));
  }

  /**
   * Adds the player's offense attributes.
   *
   * @param player     interacting player
   * @param inv        interacting inventory
   * @param hundredths 0.00
   */
  private static void addOffenseAttributes(Player player, Inventory inv, DecimalFormat hundredths) {
    String damage = ChatColor.RED + "" +
        hundredths.format(player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue()) + " DMG";
    String attackSpeed = ChatColor.GOLD +
        hundredths.format(player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getValue()) + " ATK SPD";
    String criticalChance = ChatColor.GREEN +
        hundredths.format(0) + "% CRIT";
    String criticalDamage = ChatColor.DARK_GREEN +
        hundredths.format(1) + "x CRIT DMG";

    inv.setItem(15, ItemCreator.createItem(Material.IRON_SWORD,
        ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Offense",
        List.of(damage,
            attackSpeed,
            criticalChance,
            criticalDamage)));
  }

  /**
   * Adds the player's defense attributes.
   *
   * @param player      interacting player
   * @param inv         interacting inventory
   * @param tenths      0.0
   * @param hundredths  0.00
   * @param thousandths 0.000
   */
  private static void addDefenseAttributes(Player player, Inventory inv,
                                           DecimalFormat tenths, DecimalFormat hundredths,
                                           DecimalFormat thousandths) {
    String maxHealth = ChatColor.WHITE + "" +
        (int) player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + " MAX HP";
    String armor = ChatColor.GRAY + "" +
        tenths.format(player.getAttribute(Attribute.GENERIC_ARMOR).getValue()) + " ARMOR";
    String armorToughness = ChatColor.GRAY + "" +
        tenths.format(player.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue()) + " TOUGH";
    String speed = ChatColor.AQUA + "" +
        thousandths.format(player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue() * 20) + " SPD";
    String block = ChatColor.BLUE + "" +
        "0" + " BLOCK";
    String parryChance = ChatColor.RED + "" +
        hundredths.format(0) + "% PARRY";
    String dodgeChance = ChatColor.DARK_AQUA + "" +
        hundredths.format(0) + "% DODGE";

    inv.setItem(24, ItemCreator.createItem(Material.SHIELD,
        ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Defense",
        List.of(maxHealth,
            armor,
            armorToughness,
            speed,
            block,
            parryChance,
            dodgeChance)));
  }

  /**
   * Adds the player's other attributes.
   *
   * @param player     interacting player
   * @param inv        interacting inventory
   * @param hundredths 0.00
   */
  private static void addOtherAttributes(Player player, Inventory inv, DecimalFormat hundredths) {
    String abilityDamage = ChatColor.LIGHT_PURPLE + "" +
        hundredths.format(1) + "x ABILITY DMG";
    String abilityCooldown = ChatColor.DARK_PURPLE + "-" +
        hundredths.format(0) + "% ABILITY CD";
    String applyStatusEffect = ChatColor.YELLOW + "" +
        hundredths.format(0) + "% APPLY STATUS";
    String knockbackResistance = ChatColor.GRAY + "" +
        hundredths.format(player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).getValue() * 100) + "% KB RESIST";
    String luck = ChatColor.GREEN + "" +
        hundredths.format(player.getAttribute(Attribute.GENERIC_LUCK).getValue()) + " LUCK";

    inv.setItem(33, ItemCreator.createItem(Material.NETHER_STAR,
        ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Other",
        List.of(abilityDamage,
            abilityCooldown,
            applyStatusEffect,
            knockbackResistance,
            luck)));
  }

  /**
   * Capitalizes the first character of every word.
   *
   * @param phrase phrase
   * @return proper phrase
   */
  private static String capitalizeProperly(String phrase) {
    phrase = phrase.replace("_", " ");
    String[] words = phrase.split(" ");

    StringBuilder properPhrase = new StringBuilder();
    for (String word : words) {
      properPhrase.append(word.replace(word.substring(1), word.substring(1).toLowerCase())).append(" ");
    }
    return properPhrase.toString().trim();
  }

  /**
   * Gets a time duration in ticks and converts to readable conventional time.
   *
   * @return conventional time duration
   */
  private static String tickTimeConversion(int ticks) {
    int minutes = ticks / 1200 % 60;
    int seconds = ticks / 20 % 60;
    return (minutes == 0 ? "0:" : minutes + ":") + (seconds > 10 ? seconds : "0" + seconds);
  }
}
