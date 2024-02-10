package me.dannynguyen.aethel.commands.character;

import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginPlayerHead;
import me.dannynguyen.aethel.utility.ItemCreator;
import me.dannynguyen.aethel.utility.TextFormatter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a menu that shows the player's equipment and attributes within the RPG context.
 *
 * @author Danny Nguyen
 * @version 1.9.10
 * @since 1.6.3
 */
public class CharacterMenu {
  /**
   * Character GUI.
   */
  private final Inventory menu;

  /**
   * GUI user.
   */
  private final Player user;

  /**
   * Associates a new Character menu with its user.
   *
   * @param user user
   */
  public CharacterMenu(@NotNull Player user) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.menu = createMenu();
  }

  /**
   * Associates an existing Character menu with its user.
   *
   * @param user user
   * @param menu existing Character menu
   */
  public CharacterMenu(@NotNull Player user, @NotNull Inventory menu) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.menu = Objects.requireNonNull(menu, "Null menu");
  }

  /**
   * Creates and names a Character menu with its user.
   *
   * @return Character menu
   */
  private Inventory createMenu() {
    return Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "Character " + ChatColor.DARK_PURPLE + user.getName());
  }

  /**
   * Sets the menu to view equipment and attributes.
   *
   * @return Character main menu
   */
  @NotNull
  public Inventory openMainMenu() {
    addContext();
    addActions();
    addEquipment();
    addAttributes();
    addStatusEffects();
    addExtras();
    return menu;
  }

  /**
   * Adds contextual help.
   */
  private void addContext() {
    List<String> context = List.of(
        ChatColor.GRAY + "Head" + ChatColor.WHITE + "  | " + ChatColor.GRAY + "Main Hand" + ChatColor.WHITE + " | " + ChatColor.GRAY + "Off Hand",
        ChatColor.GRAY + "Chest" + ChatColor.WHITE + " | " + ChatColor.GRAY + "Necklace",
        ChatColor.GRAY + "Legs" + ChatColor.WHITE + "  | " + ChatColor.GRAY + "Ring",
        ChatColor.GRAY + "Boots");

    menu.setItem(9, ItemCreator.createPluginPlayerHead(PluginPlayerHead.QUESTION_MARK_WHITE.head, ChatColor.WHITE + "Equipment", context));
  }

  /**
   * Adds quests, collectibles, and settings buttons.
   */
  private void addActions() {
    menu.setItem(25, ItemCreator.createItem(Material.WRITABLE_BOOK, ChatColor.AQUA + "Quests"));
    menu.setItem(34, ItemCreator.createItem(Material.ENDER_CHEST, ChatColor.AQUA + "Collectibles"));
    menu.setItem(43, ItemCreator.createItem(Material.COMMAND_BLOCK, ChatColor.AQUA + "Settings"));
  }

  /**
   * Adds the player's equipped items.
   */
  private void addEquipment() {
    PlayerInventory pInv = user.getInventory();
    ItemStack[] jewelry = PluginData.rpgData.getRpgPlayers().get(user).getJewelrySlots();

    menu.setItem(10, pInv.getHelmet());
    menu.setItem(19, pInv.getChestplate());
    menu.setItem(28, pInv.getLeggings());
    menu.setItem(37, pInv.getBoots());
    menu.setItem(11, pInv.getItemInMainHand());
    menu.setItem(12, pInv.getItemInOffHand());
    menu.setItem(20, jewelry[0]);
    menu.setItem(29, jewelry[1]);
  }

  /**
   * Adds the player's attributes.
   */
  public void addAttributes() {
    Map<String, Double> attributes = PluginData.rpgData.getRpgPlayers().get(user).getAethelAttributes();

    DecimalFormat hundredths = new DecimalFormat();
    hundredths.setMaximumFractionDigits(2);

    addOffenseAttributes(attributes, hundredths);
    addDefenseAttributes(attributes, hundredths);
    addOtherAttributes(attributes, hundredths);
  }

  /**
   * Adds the player's status effects.
   */
  private void addStatusEffects() {
    List<String> lore = new ArrayList<>();
    for (PotionEffect potionEffect : user.getActivePotionEffects()) {
      String effectDuration = ChatColor.WHITE + tickTimeConversion(potionEffect.getDuration());
      String effectType = ChatColor.AQUA + TextFormatter.capitalizePhrase(potionEffect.getType().getName());
      String effectAmplifier = (potionEffect.getAmplifier() == 0 ? "" : String.valueOf(potionEffect.getAmplifier() + 1));

      lore.add(effectDuration + " " + effectType + " " + effectAmplifier);
    }
    menu.setItem(42, ItemCreator.createItem(Material.POTION, ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Status Effects", lore, ItemFlag.HIDE_POTION_EFFECTS));
  }

  /**
   * Adds the player's level and currency balance.
   */
  private void addExtras() {
    String level = ChatColor.DARK_GREEN + "" + user.getLevel() + " LVL";
    String exp = ChatColor.GREEN + "" + user.getTotalExperience() + " EXP";
    menu.setItem(4, ItemCreator.createPlayerHead(user, List.of(level, exp, ChatColor.WHITE + "0 Silver")));
  }

  /**
   * Adds the player's offense attributes.
   *
   * @param attributes user's Aethel attributes
   * @param hundredths 0.00
   */
  private void addOffenseAttributes(Map<String, Double> attributes, DecimalFormat hundredths) {
    String damage = ChatColor.RED + "" + hundredths.format(user.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue()) + " ATK DMG";
    String attackSpeed = ChatColor.GOLD + hundredths.format(user.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getValue()) + " ATK SPD";
    String criticalChance = ChatColor.GREEN + hundredths.format(attributes.get(AethelAttribute.CRITICAL_CHANCE.getId())) + "% CRIT";
    String criticalDamage = ChatColor.DARK_GREEN + hundredths.format(1.25 + attributes.get(AethelAttribute.CRITICAL_DAMAGE.getId()) / 100) + "x CRIT DMG";

    menu.setItem(15, ItemCreator.createItem(Material.IRON_SWORD, ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Offense", List.of(damage, attackSpeed, criticalChance, criticalDamage), ItemFlag.HIDE_ATTRIBUTES));
  }

  /**
   * Adds the player's defense attributes.
   *
   * @param attributes user's Aethel attributes
   * @param hundredths 0.00
   */
  private void addDefenseAttributes(Map<String, Double> attributes, DecimalFormat hundredths) {
    DecimalFormat thousandths = new DecimalFormat();
    thousandths.setMaximumFractionDigits(3);

    String maxHealth = ChatColor.WHITE + "" + hundredths.format(user.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) + " MAX HP";
    String armor = ChatColor.GRAY + "" + hundredths.format(user.getAttribute(Attribute.GENERIC_ARMOR).getValue()) + " ARMOR";
    String armorToughness = ChatColor.GRAY + "" + hundredths.format(user.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue()) + " ARMOR TOUGH";
    String speed = ChatColor.AQUA + "" + thousandths.format(user.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue() * 20) + " SPD";
    String block = ChatColor.BLUE + "" + hundredths.format(attributes.get(AethelAttribute.BLOCK.getId())) + " BLOCK";
    String parryChance = ChatColor.RED + "" + hundredths.format(attributes.get(AethelAttribute.PARRY_CHANCE.getId())) + "% PARRY";
    String parryReflect = ChatColor.DARK_RED + "" + hundredths.format(attributes.get(AethelAttribute.PARRY_DEFLECT.getId())) + "% PARRY DEFLECT";
    String dodgeChance = ChatColor.DARK_AQUA + "" + hundredths.format(attributes.get(AethelAttribute.DODGE_CHANCE.getId())) + "% DODGE";

    menu.setItem(24, ItemCreator.createItem(Material.IRON_CHESTPLATE, ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Defense", List.of(maxHealth, armor, armorToughness, speed, block, parryChance, parryReflect, dodgeChance), ItemFlag.HIDE_ATTRIBUTES));
  }

  /**
   * Adds the player's other attributes.
   *
   * @param attributes user's Aethel attributes
   * @param hundredths 0.00
   */
  private void addOtherAttributes(Map<String, Double> attributes, DecimalFormat hundredths) {
    String abilityDamage = ChatColor.LIGHT_PURPLE + "" + hundredths.format(1 + attributes.get(AethelAttribute.ABILITY_DAMAGE.getId()) / 100) + "x ABILITY DMG";
    String abilityCooldown = ChatColor.DARK_PURPLE + "-" + hundredths.format(attributes.get(AethelAttribute.ABILITY_COOLDOWN.getId())) + "% ABILITY CD";
    String applyStatusEffect = ChatColor.YELLOW + "" + hundredths.format(attributes.get(AethelAttribute.APPLY_STATUS.getId())) + "% APPLY STATUS";
    String knockbackResistance = ChatColor.GRAY + "" + hundredths.format(user.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).getValue() * 100) + "% KB RESIST";
    String luck = ChatColor.GREEN + "" + hundredths.format(user.getAttribute(Attribute.GENERIC_LUCK).getValue()) + " LUCK";

    menu.setItem(33, ItemCreator.createItem(Material.SPYGLASS, ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Other", List.of(abilityDamage, abilityCooldown, applyStatusEffect, knockbackResistance, luck)));
  }

  /**
   * Gets a time duration in ticks and converts it to readable conventional time.
   *
   * @return conventional time duration
   */
  private String tickTimeConversion(int ticks) {
    int minutes = ticks / 1200 % 60;
    int seconds = ticks / 20 % 60;
    return (minutes == 0 ? "0:" : minutes + ":") + (seconds > 10 ? seconds : "0" + seconds);
  }
}
