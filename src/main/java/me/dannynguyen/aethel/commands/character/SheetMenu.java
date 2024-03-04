package me.dannynguyen.aethel.commands.character;

import me.dannynguyen.aethel.systems.plugin.PluginData;
import me.dannynguyen.aethel.systems.plugin.enums.PluginPlayerHead;
import me.dannynguyen.aethel.systems.rpg.AethelAttribute;
import me.dannynguyen.aethel.systems.rpg.RpgHealth;
import me.dannynguyen.aethel.systems.rpg.RpgPlayer;
import me.dannynguyen.aethel.utility.ItemCreator;
import me.dannynguyen.aethel.utility.TextFormatter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Represents a menu that shows the player's equipment and attributes within the RPG context.
 *
 * @author Danny Nguyen
 * @version 1.14.0
 * @since 1.6.3
 */
class SheetMenu {
  /**
   * Sheet GUI.
   */
  private final Inventory menu;

  /**
   * GUI user.
   */
  private final Player user;

  /**
   * Owner of the character sheet.
   */
  private final Player owner;

  /**
   * Owner's UUID.
   */
  private final UUID ownerUUID;

  /**
   * Associates a new Sheet menu with its user and target player.
   *
   * @param user  user
   * @param owner requested player
   */
  protected SheetMenu(@NotNull Player user, @NotNull Player owner) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.owner = Objects.requireNonNull(owner, "Null owner");
    this.ownerUUID = owner.getUniqueId();
    this.menu = createMenu();
  }

  /**
   * Associates an existing Sheet menu with its user.
   *
   * @param user user
   * @param menu existing Sheet menu
   */
  protected SheetMenu(@NotNull Player user, @NotNull Inventory menu) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.owner = user;
    this.ownerUUID = owner.getUniqueId();
    this.menu = Objects.requireNonNull(menu, "Null menu");
  }

  /**
   * Creates and names a Sheet menu with its user.
   *
   * @return Sheet menu
   */
  private Inventory createMenu() {
    return Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "Character " + ChatColor.DARK_PURPLE + owner.getName());
  }

  /**
   * Sets the menu to view equipment and attributes.
   *
   * @return Sheet menu
   */
  @NotNull
  protected Inventory openMenu() {
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

    menu.setItem(9, ItemCreator.createPluginPlayerHead(PluginPlayerHead.QUESTION_MARK_WHITE.getHead(), ChatColor.WHITE + "Equipment", context));
  }

  /**
   * Adds quests, collectibles, and settings buttons.
   */
  private void addActions() {
    menu.setItem(25, ItemCreator.createItem(Material.WRITABLE_BOOK, ChatColor.AQUA + "Quests"));
    menu.setItem(34, ItemCreator.createItem(Material.ENDER_CHEST, ChatColor.AQUA + "Collectibles"));
    if (user.equals(owner)) {
      menu.setItem(43, ItemCreator.createItem(Material.COMMAND_BLOCK, ChatColor.AQUA + "Settings"));
    }
  }

  /**
   * Adds the player's equipped items.
   */
  private void addEquipment() {
    PlayerInventory pInv = owner.getInventory();
    ItemStack[] jewelry = PluginData.rpgSystem.getRpgPlayers().get(ownerUUID).getEquipment().getJewelry();

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
  protected void addAttributes() {
    Map<AethelAttribute, Double> attributes = PluginData.rpgSystem.getRpgPlayers().get(ownerUUID).getAethelAttributes();

    DecimalFormat df2 = new DecimalFormat();
    df2.setMaximumFractionDigits(2);

    addOffenseAttributes(attributes, df2);
    addDefenseAttributes(attributes, df2);
    addOtherAttributes(attributes, df2);
  }

  /**
   * Adds the player's status effects.
   */
  private void addStatusEffects() {
    List<String> lore = new ArrayList<>();
    for (PotionEffect potionEffect : owner.getActivePotionEffects()) {
      String duration = ChatColor.WHITE + tickTimeConversion(potionEffect.getDuration());
      String type = ChatColor.AQUA + TextFormatter.capitalizePhrase(potionEffect.getType().getName());
      String amplifier = ChatColor.YELLOW + (potionEffect.getAmplifier() == 0 ? "" : String.valueOf(potionEffect.getAmplifier() + 1));
      lore.add(duration + " " + type + " " + amplifier);
    }
    menu.setItem(42, ItemCreator.createItem(Material.POTION, ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Status Effects", lore, ItemFlag.HIDE_POTION_EFFECTS));
  }

  /**
   * Adds the player's level and currency balance.
   */
  private void addExtras() {
    String level = ChatColor.DARK_GREEN + "" + owner.getLevel() + " LVL";
    String exp = ChatColor.GREEN + "" + owner.getTotalExperience() + " EXP";
    menu.setItem(4, ItemCreator.createPlayerHead(owner, List.of(level, exp, ChatColor.WHITE + "0 Silver")));
  }

  /**
   * Adds the player's offense attributes.
   *
   * @param attributes owner's Aethel attributes
   * @param df2        0.00 decimal format
   */
  private void addOffenseAttributes(Map<AethelAttribute, Double> attributes, DecimalFormat df2) {
    String damage = ChatColor.RED + "" + df2.format(owner.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue()) + " ATK DMG";
    String attackSpeed = ChatColor.GOLD + df2.format(owner.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getValue()) + " ATK SPD";
    String criticalChance = ChatColor.GREEN + df2.format(attributes.get(AethelAttribute.CRITICAL_CHANCE)) + "% CRIT";
    String criticalDamage = ChatColor.DARK_GREEN + df2.format(1.25 + attributes.get(AethelAttribute.CRITICAL_DAMAGE) / 100) + "x CRIT DMG";

    menu.setItem(15, ItemCreator.createItem(Material.IRON_SWORD, ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Offense", List.of(damage, attackSpeed, criticalChance, criticalDamage), ItemFlag.HIDE_ATTRIBUTES));
  }

  /**
   * Adds the player's defense attributes.
   *
   * @param attributes owner's Aethel attributes
   * @param df2        0.00 decimal format
   */
  private void addDefenseAttributes(Map<AethelAttribute, Double> attributes, DecimalFormat df2) {
    RpgPlayer rpgPlayer = PluginData.rpgSystem.getRpgPlayers().get(ownerUUID);
    RpgHealth rpgHealth = rpgPlayer.getHealth();
    Map<Enchantment, Integer> enchantments = rpgPlayer.getEquipment().getTotalEnchantments();

    String maxHealth = ChatColor.RED + "" + df2.format(rpgHealth.getCurrentHealth()) + " / " + df2.format(owner.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + attributes.get(AethelAttribute.MAX_HEALTH)) + " HP";
    String counterChance = ChatColor.YELLOW + "" + df2.format(attributes.get(AethelAttribute.COUNTER_CHANCE)) + "% COUNTER";
    String dodgeChance = ChatColor.BLUE + "" + df2.format(attributes.get(AethelAttribute.DODGE_CHANCE)) + "% DODGE";
    String armorToughness = ChatColor.GRAY + "" + df2.format(owner.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue() + attributes.get(AethelAttribute.ARMOR_TOUGHNESS)) + " TOUGH";
    String armor = ChatColor.GRAY + "" + df2.format(owner.getAttribute(Attribute.GENERIC_ARMOR).getValue()) + " ARMOR";

    String featherFalling = ChatColor.GRAY + "" + enchantments.get(Enchantment.PROTECTION_FALL) + " FEATHER FALL";
    String protection = ChatColor.GRAY + "" + enchantments.get(Enchantment.PROTECTION_ENVIRONMENTAL) + " PROT";
    String blastProtection = ChatColor.GRAY + "" + enchantments.get(Enchantment.PROTECTION_EXPLOSIONS) + " BLAST PROT";
    String fireProtection = ChatColor.GRAY + "" + enchantments.get(Enchantment.PROTECTION_FIRE) + " FIRE PROT";
    String projectileProtection = ChatColor.GRAY + "" + enchantments.get(Enchantment.PROTECTION_PROJECTILE) + " PROJ PROT";

    menu.setItem(24, ItemCreator.createItem(Material.IRON_CHESTPLATE, ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Defense", List.of(maxHealth, counterChance, dodgeChance, armorToughness, armor, "", featherFalling, protection, blastProtection, fireProtection, projectileProtection), ItemFlag.HIDE_ATTRIBUTES));
  }

  /**
   * Adds the player's other attributes.
   *
   * @param attributes owner's Aethel attributes
   * @param df2        0.00 decimal format
   */
  private void addOtherAttributes(Map<AethelAttribute, Double> attributes, DecimalFormat df2) {
    DecimalFormat df3 = new DecimalFormat();
    df3.setMaximumFractionDigits(3);

    String abilityDamage = ChatColor.LIGHT_PURPLE + "" + df2.format(1.0 + attributes.get(AethelAttribute.ITEM_DAMAGE) / 100) + "x ITEM DMG";
    String abilityCooldown = ChatColor.DARK_PURPLE + "-" + df2.format(attributes.get(AethelAttribute.ITEM_COOLDOWN)) + "% ITEM CD";
    String speed = ChatColor.DARK_AQUA + "" + df3.format(owner.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue() * 20) + " SPEED";
    String luck = ChatColor.GREEN + "" + df2.format(owner.getAttribute(Attribute.GENERIC_LUCK).getValue()) + " LUCK";
    String knockbackResistance = ChatColor.GRAY + "-" + df2.format(owner.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).getValue() * 100) + "% KNOCKBACK";

    menu.setItem(33, ItemCreator.createItem(Material.SPYGLASS, ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Other", List.of(abilityDamage, abilityCooldown, speed, luck, knockbackResistance)));
  }

  /**
   * Gets a time duration in ticks and converts it to readable conventional time.
   *
   * @param ticks ticks
   * @return conventional time duration
   */
  private String tickTimeConversion(int ticks) {
    int minutes = ticks / 1200 % 60;
    int seconds = ticks / 20 % 60;
    return (minutes == 0 ? "0:" : minutes + ":") + (seconds > 10 ? seconds : "0" + seconds);
  }
}
