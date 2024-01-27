package me.dannynguyen.aethel.commands.character;

import me.dannynguyen.aethel.creators.ItemCreator;
import me.dannynguyen.aethel.formatters.TextFormatter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.potion.PotionEffect;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * CharacterSheet is an inventory that shows the user's
 * equipment and attributes within the RPG context.
 *
 * @author Danny Nguyen
 * @version 1.7.10
 * @since 1.6.3
 */
public class CharacterSheet {
  /**
   * Creates a CharacterSheet with its equipment and attributes.
   *
   * @param user user
   * @return CharacterSheet with equipment and attributes
   */
  public static Inventory openCharacterSheet(Player user) {
    Inventory inv = createInventory(user);
    addActionButtons(inv);
    addEquipment(user, inv);
    addAttributes(user, inv);
    addStatusEffects(user, inv);
    addExtras(user, inv);
    return inv;
  }

  /**
   * Creates and names a CharacterSheet inventory to its user.
   *
   * @param user user
   * @return CharacterSheet inventory
   */
  private static Inventory createInventory(Player user) {
    return Bukkit.createInventory(user, 54,
        ChatColor.DARK_GRAY + "Character " + ChatColor.DARK_PURPLE + user.getName());
  }

  /**
   * Adds quests, collectibles, and settings buttons.
   *
   * @param inv interacting inv
   */
  private static void addActionButtons(Inventory inv) {
    inv.setItem(25, ItemCreator.createItem(Material.WRITABLE_BOOK, ChatColor.AQUA + "Quests"));
    inv.setItem(34, ItemCreator.createItem(Material.ENDER_CHEST, ChatColor.AQUA + "Collectibles"));
    inv.setItem(43, ItemCreator.createItem(Material.COMMAND_BLOCK, ChatColor.AQUA + "Settings"));
  }

  /**
   * Adds the user's equipped items.
   *
   * @param user user
   * @param inv  interacting inv
   */
  private static void addEquipment(Player user, Inventory inv) {
    inv.setItem(9, ItemCreator.createLoadedPlayerHead("WHITE_QUESTION_MARK",
        ChatColor.WHITE + "Equipment",
        List.of(ChatColor.GRAY + "Head" + ChatColor.WHITE + "  | "
                + ChatColor.GRAY + "Main Hand" + ChatColor.WHITE + " | " + ChatColor.GRAY + "Off Hand",
            ChatColor.GRAY + "Chest" + ChatColor.WHITE + " | " + ChatColor.GRAY + "Necklace",
            ChatColor.GRAY + "Legs" + ChatColor.WHITE + "  | " + ChatColor.GRAY + "Ring",
            ChatColor.GRAY + "Boots" + ChatColor.WHITE + " | " + ChatColor.GRAY + "Ring")));
    inv.setItem(10, user.getInventory().getHelmet());
    inv.setItem(19, user.getInventory().getChestplate());
    inv.setItem(28, user.getInventory().getLeggings());
    inv.setItem(37, user.getInventory().getBoots());
    inv.setItem(11, user.getInventory().getItemInMainHand());
    inv.setItem(12, user.getInventory().getItemInOffHand());
  }

  /**
   * Adds the user's attributes.
   *
   * @param user user
   * @param inv  interacting inv
   */
  private static void addAttributes(Player user, Inventory inv) {
    DecimalFormat tenths = new DecimalFormat();
    DecimalFormat hundredths = new DecimalFormat();
    DecimalFormat thousandths = new DecimalFormat();

    tenths.setMaximumFractionDigits(1);
    hundredths.setMaximumFractionDigits(2);
    thousandths.setMaximumFractionDigits(3);

    addOffenseAttributes(user, inv, hundredths);
    addDefenseAttributes(user, inv, tenths, hundredths, thousandths);
    addOtherAttributes(user, inv, hundredths);
  }

  /**
   * Adds the user's level and currency balance.
   *
   * @param user user
   * @param inv  interacting inv
   */
  private static void addExtras(Player user, Inventory inv) {
    String level = ChatColor.DARK_GREEN + "" + user.getLevel() + " LVL";
    String exp = ChatColor.GREEN + "" + user.getTotalExperience() + " EXP";
    inv.setItem(4, ItemCreator.createPlayerHead(user, List.of(level, exp, ChatColor.WHITE + "0 Silver")));
  }

  /**
   * Adds the user's offense attributes.
   *
   * @param user       interacting user
   * @param inv        interacting inventory
   * @param hundredths 0.00
   */
  private static void addOffenseAttributes(Player user, Inventory inv, DecimalFormat hundredths) {
    String damage = ChatColor.RED + "" +
        hundredths.format(user.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue()) + " DMG";
    String attackSpeed = ChatColor.GOLD +
        hundredths.format(user.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getValue()) + " ATK SPD";
    String criticalChance = ChatColor.GREEN +
        hundredths.format(0) + "% CRIT";
    String criticalDamage = ChatColor.DARK_GREEN +
        hundredths.format(1) + "x CRIT DMG";

    inv.setItem(15, ItemCreator.createItem(
        Material.IRON_SWORD,
        ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Offense",
        List.of(damage, attackSpeed, criticalChance, criticalDamage),
        ItemFlag.HIDE_ATTRIBUTES));
  }

  /**
   * Adds the user's defense attributes.
   *
   * @param user        interacting user
   * @param inv         interacting inventory
   * @param tenths      0.0
   * @param hundredths  0.00
   * @param thousandths 0.000
   */
  private static void addDefenseAttributes(Player user, Inventory inv,
                                           DecimalFormat tenths, DecimalFormat hundredths,
                                           DecimalFormat thousandths) {
    String maxHealth = ChatColor.WHITE + "" +
        (int) user.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + " MAX HP";
    String armor = ChatColor.GRAY + "" +
        tenths.format(user.getAttribute(Attribute.GENERIC_ARMOR).getValue()) + " ARMOR";
    String armorToughness = ChatColor.GRAY + "" +
        tenths.format(user.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue()) + " TOUGH";
    String speed = ChatColor.AQUA + "" +
        thousandths.format(user.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue() * 20) + " SPD";
    String block = ChatColor.BLUE + "" +
        "0" + " BLOCK";
    String parryChance = ChatColor.RED + "" +
        hundredths.format(0) + "% PARRY";
    String dodgeChance = ChatColor.DARK_AQUA + "" +
        hundredths.format(0) + "% DODGE";

    inv.setItem(24, ItemCreator.createItem(
        Material.IRON_CHESTPLATE,
        ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Defense",
        List.of(maxHealth, armor, armorToughness, speed, block, parryChance, dodgeChance),
        ItemFlag.HIDE_ATTRIBUTES));
  }

  /**
   * Adds the user's other attributes.
   *
   * @param user       interacting user
   * @param inv        interacting inventory
   * @param hundredths 0.00
   */
  private static void addOtherAttributes(Player user, Inventory inv, DecimalFormat hundredths) {
    String abilityDamage = ChatColor.LIGHT_PURPLE + "" +
        hundredths.format(1) + "x ABILITY DMG";
    String abilityCooldown = ChatColor.DARK_PURPLE + "-" +
        hundredths.format(0) + "% ABILITY CD";
    String applyStatusEffect = ChatColor.YELLOW + "" +
        hundredths.format(0) + "% APPLY STATUS";
    String knockbackResistance = ChatColor.GRAY + "" +
        hundredths.format(user.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).getValue() * 100) + "% KB RESIST";
    String luck = ChatColor.GREEN + "" +
        hundredths.format(user.getAttribute(Attribute.GENERIC_LUCK).getValue()) + " LUCK";

    inv.setItem(33, ItemCreator.createItem(Material.SPYGLASS,
        ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Other",
        List.of(abilityDamage,
            abilityCooldown,
            applyStatusEffect,
            knockbackResistance,
            luck)));
  }

  /**
   * Adds status effects.
   *
   * @param user user
   * @param inv  interacting inventory
   */
  private static void addStatusEffects(Player user, Inventory inv) {
    ArrayList<String> lore = new ArrayList<>();
    for (PotionEffect potionEffect : user.getActivePotionEffects()) {
      String effectDuration = ChatColor.WHITE + tickTimeConversion(potionEffect.getDuration());
      String effectType = ChatColor.AQUA + TextFormatter.capitalizePhrase(potionEffect.getType().getName());
      String effectAmplifier = (potionEffect.getAmplifier() == 0 ? "" :
          String.valueOf(potionEffect.getAmplifier() + 1));

      lore.add(effectDuration + " " + effectType + " " + effectAmplifier);
    }

    inv.setItem(42, ItemCreator.createItem(Material.POTION,
        ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Status Effects",
        lore, ItemFlag.HIDE_POTION_EFFECTS));
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
