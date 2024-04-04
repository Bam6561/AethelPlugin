package me.dannynguyen.aethel.commands.character;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.PlayerHead;
import me.dannynguyen.aethel.enums.rpg.AethelAttribute;
import me.dannynguyen.aethel.enums.rpg.StatusType;
import me.dannynguyen.aethel.interfaces.Menu;
import me.dannynguyen.aethel.rpg.*;
import me.dannynguyen.aethel.utils.item.ItemCreator;
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
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Represents a menu that shows the player's
 * {@link Equipment equipment},
 * Minecraft attributes, {@link AethelAttributes Aethel attributes},
 * {@link Enchantments enchantments}, and {@link Status statuses}.
 *
 * @author Danny Nguyen
 * @version 1.21.1
 * @since 1.6.3
 */
public class SheetMenu implements Menu {
  /**
   * GUI.
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
  private final UUID uuid;

  /**
   * Associates a new Sheet menu with its user and target player.
   *
   * @param user  user
   * @param owner requested player
   */
  public SheetMenu(@NotNull Player user, @NotNull Player owner) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.owner = Objects.requireNonNull(owner, "Null owner");
    this.uuid = owner.getUniqueId();
    this.menu = createMenu();
  }

  /**
   * Associates an existing Sheet menu with its user.
   *
   * @param user user
   * @param menu existing Sheet menu
   */
  public SheetMenu(@NotNull Player user, @NotNull Inventory menu) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.menu = Objects.requireNonNull(menu, "Null menu");
    this.owner = user;
    this.uuid = owner.getUniqueId();
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
   * Sets the menu to view {@link Equipment equipment},
   * Minecraft attributes, {@link AethelAttributes Aethel attributes},
   * {@link Enchantments enchantments}, and {@link Status statuses}.
   *
   * @return Sheet menu
   */
  @NotNull
  public Inventory getMainMenu() {
    addContext();
    addActions();
    addEquipment();
    addAttributes();
    addStatuses();
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

    menu.setItem(9, ItemCreator.createPluginPlayerHead(PlayerHead.QUESTION_MARK_WHITE.getHead(), ChatColor.WHITE + "Equipment", context));
  }

  /**
   * Adds {@link QuestsMenu}, {@link CollectiblesMenu}, and {@link SettingsMenu} buttons.
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
    ItemStack[] jewelry = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getEquipment().getJewelry();

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
    RpgPlayer rpgPlayer = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid);
    Buffs buffs = rpgPlayer.getBuffs();
    Map<Attribute, Double> attributeBuffs = buffs.getAttributes();
    Map<AethelAttribute, Double> aethelAttributeBuffs = buffs.getAethelAttributes();
    Map<AethelAttribute, Double> aethelAttributes = rpgPlayer.getAethelAttributes().getAttributes();

    DecimalFormat df2 = new DecimalFormat();
    df2.setMaximumFractionDigits(2);

    addOffenseAttributes(attributeBuffs, aethelAttributeBuffs, aethelAttributes, df2);
    addDefenseAttributes(attributeBuffs, aethelAttributeBuffs, aethelAttributes, df2);
    addOtherAttributes(attributeBuffs, aethelAttributeBuffs, aethelAttributes, df2);
  }

  /**
   * Adds the player's {@link Status statuses}.
   */
  private void addStatuses() {
    List<String> lore = new ArrayList<>();
    Map<UUID, Map<StatusType, Status>> entityStatuses = Plugin.getData().getRpgSystem().getStatuses();
    if (entityStatuses.containsKey(uuid)) {
      Map<StatusType, Status> statuses = entityStatuses.get(uuid);
      for (StatusType statusType : statuses.keySet()) {
        Status status = statuses.get(statusType);
        StringBuilder instancesBuilder = new StringBuilder();
        instancesBuilder.append("[");
        for (int instance : status.getStackInstances().values()) {
          instancesBuilder.append(instance).append(", ");
        }
        instancesBuilder.delete(instancesBuilder.length() - 2, instancesBuilder.length());
        instancesBuilder.append("]");
        lore.add(ChatColor.AQUA + statusType.getProperName() + " " + ChatColor.WHITE + status.getStackAmount() + " " + instancesBuilder);
      }
    }
    menu.setItem(42, ItemCreator.createItem(Material.POTION, ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Statuses", lore, ItemFlag.HIDE_POTION_EFFECTS));
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
   * @param attributeBuffs       {@link Buffs#getAttributes()}
   * @param aethelAttributeBuffs {@link Buffs#getAethelAttributes()}
   * @param aethelAttributes     owner's {@link AethelAttributes}
   * @param df2                  0.00 decimal format
   */
  private void addOffenseAttributes(Map<Attribute, Double> attributeBuffs, Map<AethelAttribute, Double> aethelAttributeBuffs, Map<AethelAttribute, Double> aethelAttributes, DecimalFormat df2) {
    double damageBuff = attributeBuffs.get(Attribute.GENERIC_ATTACK_DAMAGE);
    double attackSpeedBuff = attributeBuffs.get(Attribute.GENERIC_ATTACK_SPEED);
    double criticalChanceBuff = aethelAttributeBuffs.get(AethelAttribute.CRITICAL_CHANCE);
    double criticalDamageBuff = aethelAttributeBuffs.get(AethelAttribute.CRITICAL_DAMAGE);

    String damage = ChatColor.RED + "" + df2.format(owner.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue()) + " ATK DMG" + (damageBuff != 0.0 ? " [" + df2.format(damageBuff) + "]" : "");
    String attackSpeed = ChatColor.GOLD + df2.format(owner.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getValue()) + " ATK SPD" + (attackSpeedBuff != 0.0 ? " [" + df2.format(attackSpeedBuff) + "]" : "");
    String criticalChance = ChatColor.GREEN + df2.format(aethelAttributes.get(AethelAttribute.CRITICAL_CHANCE) + aethelAttributeBuffs.get(AethelAttribute.CRITICAL_CHANCE)) + "% CRIT" + (criticalChanceBuff != 0.0 ? " [" + df2.format(criticalChanceBuff) + "]" : "");
    String criticalDamage = ChatColor.DARK_GREEN + df2.format(1.25 + (aethelAttributes.get(AethelAttribute.CRITICAL_DAMAGE) + aethelAttributeBuffs.get(AethelAttribute.CRITICAL_DAMAGE)) / 100) + "x CRIT DMG" + (criticalDamageBuff != 0.0 ? " [" + df2.format(criticalDamageBuff) + "]" : "");

    menu.setItem(15, ItemCreator.createItem(Material.IRON_SWORD, ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Offense", List.of(damage, attackSpeed, criticalChance, criticalDamage), ItemFlag.HIDE_ATTRIBUTES));
  }

  /**
   * Adds the player's defense attributes.
   *
   * @param attributeBuffs       {@link Buffs#getAttributes()}
   * @param aethelAttributeBuffs {@link Buffs#getAethelAttributes()}
   * @param aethelAttributes     owner's {@link AethelAttributes}
   * @param df2                  0.00 decimal format
   */
  private void addDefenseAttributes(Map<Attribute, Double> attributeBuffs, Map<AethelAttribute, Double> aethelAttributeBuffs, Map<AethelAttribute, Double> aethelAttributes, DecimalFormat df2) {
    RpgPlayer rpgPlayer = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid);
    Health health = rpgPlayer.getHealth();
    Map<Enchantment, Integer> enchantments = rpgPlayer.getEnchantments().getTotalEnchantments();

    double maxHealthBuff = attributeBuffs.get(Attribute.GENERIC_MAX_HEALTH) + aethelAttributeBuffs.get(AethelAttribute.MAX_HEALTH);
    double counterChanceBuff = aethelAttributeBuffs.get(AethelAttribute.COUNTER_CHANCE);
    double dodgeChanceBuff = aethelAttributeBuffs.get(AethelAttribute.DODGE_CHANCE);
    double armorToughnessBuff = attributeBuffs.get(Attribute.GENERIC_ARMOR_TOUGHNESS) + aethelAttributeBuffs.get(AethelAttribute.ARMOR_TOUGHNESS);
    double armorBuff = attributeBuffs.get(Attribute.GENERIC_ARMOR);

    String maxHealth = ChatColor.RED + "" + df2.format(health.getCurrentHealth()) + " / " + df2.format(owner.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + aethelAttributes.get(AethelAttribute.MAX_HEALTH) + aethelAttributeBuffs.get(AethelAttribute.MAX_HEALTH)) + " HP" + (maxHealthBuff != 0.0 ? " [" + df2.format(maxHealthBuff) + "]" : "");
    String counterChance = ChatColor.YELLOW + "" + df2.format(aethelAttributes.get(AethelAttribute.COUNTER_CHANCE) + aethelAttributeBuffs.get(AethelAttribute.COUNTER_CHANCE)) + "% COUNTER" + (counterChanceBuff != 0.0 ? " [" + df2.format(counterChanceBuff) + "]" : "");
    String dodgeChance = ChatColor.BLUE + "" + df2.format(aethelAttributes.get(AethelAttribute.DODGE_CHANCE) + aethelAttributeBuffs.get(AethelAttribute.DODGE_CHANCE)) + "% DODGE" + (dodgeChanceBuff != 0.0 ? " [" + df2.format(dodgeChanceBuff) + "]" : "");
    String armorToughness = ChatColor.GRAY + "" + df2.format(owner.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue() + aethelAttributes.get(AethelAttribute.ARMOR_TOUGHNESS) + aethelAttributeBuffs.get(AethelAttribute.ARMOR_TOUGHNESS)) + " TOUGH" + (armorToughnessBuff != 0.0 ? " [" + df2.format(armorToughnessBuff) + "]" : "");
    String armor = ChatColor.GRAY + "" + df2.format(owner.getAttribute(Attribute.GENERIC_ARMOR).getValue()) + " ARMOR" + (armorBuff != 0.0 ? " [" + df2.format(armorBuff) + "]" : "");

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
   * @param attributeBuffs       {@link Buffs#getAttributes()}
   * @param aethelAttributeBuffs {@link Buffs#getAethelAttributes()}
   * @param aethelAttributes     owner's {@link AethelAttributes}
   * @param df2                  0.00 decimal format
   */
  private void addOtherAttributes(Map<Attribute, Double> attributeBuffs, Map<AethelAttribute, Double> aethelAttributeBuffs, Map<AethelAttribute, Double> aethelAttributes, DecimalFormat df2) {
    DecimalFormat df3 = new DecimalFormat();
    df3.setMaximumFractionDigits(3);

    double itemDamageBuff = aethelAttributeBuffs.get(AethelAttribute.ITEM_DAMAGE);
    double itemCooldownBuff = aethelAttributeBuffs.get(AethelAttribute.ITEM_COOLDOWN);
    double speedBuff = attributeBuffs.get(Attribute.GENERIC_MOVEMENT_SPEED);
    double luckBuff = attributeBuffs.get(Attribute.GENERIC_LUCK);
    double knockbackResistanceBuff = attributeBuffs.get(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
    double tenacityBuff = aethelAttributeBuffs.get(AethelAttribute.TENACITY);

    String itemDamage = ChatColor.LIGHT_PURPLE + "" + df2.format(1.0 + (aethelAttributes.get(AethelAttribute.ITEM_DAMAGE) + aethelAttributeBuffs.get(AethelAttribute.ITEM_DAMAGE)) / 100) + "x ITEM DMG" + (itemDamageBuff != 0.0 ? " [" + df2.format(itemDamageBuff) + "]" : "");
    String itemCooldown = ChatColor.DARK_PURPLE + "-" + df2.format(aethelAttributes.get(AethelAttribute.ITEM_COOLDOWN) + aethelAttributeBuffs.get(AethelAttribute.ITEM_COOLDOWN)) + "% ITEM CD" + (itemCooldownBuff != 0.0 ? " [" + df2.format(itemCooldownBuff) + "]" : "");
    String speed = ChatColor.DARK_AQUA + "" + df3.format(owner.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue()) + " SPEED" + (speedBuff != 0.0 ? " [" + df2.format(speedBuff) + "]" : "");
    String luck = ChatColor.GREEN + "" + df2.format(owner.getAttribute(Attribute.GENERIC_LUCK).getValue()) + " LUCK" + (luckBuff != 0.0 ? " [" + df2.format(luckBuff) + "]" : "");
    String knockbackResistance = ChatColor.GRAY + "-" + df2.format(owner.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).getValue() * 100) + "% KNOCKBACK" + (knockbackResistanceBuff != 0.0 ? " [" + df2.format(knockbackResistanceBuff) + "]" : "");
    String tenacity = ChatColor.DARK_GREEN + df2.format(aethelAttributes.get(AethelAttribute.TENACITY) + aethelAttributeBuffs.get(AethelAttribute.TENACITY)) + "% TENACITY" + (tenacityBuff != 0.0 ? " [" + df2.format(tenacityBuff) + "]" : "");

    menu.setItem(33, ItemCreator.createItem(Material.SPYGLASS, ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Other", List.of(itemDamage, itemCooldown, speed, luck, knockbackResistance, tenacity)));
  }
}
