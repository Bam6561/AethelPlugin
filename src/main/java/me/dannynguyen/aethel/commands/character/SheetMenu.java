package me.dannynguyen.aethel.commands.character;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.plugin.PlayerHead;
import me.dannynguyen.aethel.enums.rpg.AethelAttribute;
import me.dannynguyen.aethel.enums.rpg.StatusType;
import me.dannynguyen.aethel.interfaces.Menu;
import me.dannynguyen.aethel.rpg.Buffs;
import me.dannynguyen.aethel.rpg.Equipment;
import me.dannynguyen.aethel.rpg.Health;
import me.dannynguyen.aethel.rpg.Status;
import me.dannynguyen.aethel.utils.item.ItemCreator;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Represents a menu that shows the player's
 * {@link Equipment equipment},
 * Minecraft attributes, {@link Equipment.AethelAttributes Aethel attributes},
 * {@link Equipment.Enchantments enchantments}, and {@link Status statuses}.
 *
 * @author Danny Nguyen
 * @version 1.22.16
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
   * Minecraft attributes, {@link Equipment.AethelAttributes Aethel attributes},
   * {@link Equipment.Enchantments enchantments}, and {@link Status statuses}.
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
    PersistentDataContainer entityTags = Bukkit.getPlayer(uuid).getPersistentDataContainer();
    Buffs buffs = Plugin.getData().getRpgSystem().getBuffs().get(uuid);

    DecimalFormat df2 = new DecimalFormat();
    df2.setMaximumFractionDigits(2);

    addOffenseAttributes(entityTags, buffs, df2);
    addDefenseAttributes(entityTags, buffs, df2);
    addOtherAttributes(entityTags, buffs, df2);
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
   * @param entityTags owner's persistent tags
   * @param buffs      {@link Buffs}
   * @param df2        0.00 decimal format
   */
  private void addOffenseAttributes(PersistentDataContainer entityTags, Buffs buffs, DecimalFormat df2) {
    double criticalChanceBase = entityTags.getOrDefault(Key.ATTRIBUTE_CRITICAL_CHANCE.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
    double criticalDamageBase = entityTags.getOrDefault(Key.ATTRIBUTE_CRITICAL_DAMAGE.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
    double feintSkillBase = entityTags.getOrDefault(Key.ATTRIBUTE_FEINT_SKILL.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
    double accuracySkillBase = entityTags.getOrDefault(Key.ATTRIBUTE_ACCURACY_SKILL.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);


    double damageBuff = 0.0;
    double attackSpeedBuff = 0.0;
    double criticalChanceBuff = 0.0;
    double criticalDamageBuff = 0.0;
    double feintSkillBuff = 0.0;
    double accuracySkillBuff = 0.0;
    if (buffs != null) {
      damageBuff = buffs.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
      attackSpeedBuff = buffs.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
      criticalChanceBuff = buffs.getAethelAttribute(AethelAttribute.CRITICAL_CHANCE);
      criticalDamageBuff = buffs.getAethelAttribute(AethelAttribute.CRITICAL_DAMAGE);
      feintSkillBuff = buffs.getAethelAttribute(AethelAttribute.FEINT_SKILL);
      accuracySkillBuff = buffs.getAethelAttribute(AethelAttribute.ACCURACY_SKILL);
    }

    String damage = ChatColor.RED + df2.format(owner.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue()) + " ATK DMG" + (damageBuff != 0.0 ? " [" + df2.format(damageBuff) + "]" : "");
    String attackSpeed = ChatColor.GOLD + df2.format(owner.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getValue()) + " ATK SPD" + (attackSpeedBuff != 0.0 ? " [" + df2.format(attackSpeedBuff) + "]" : "");
    String criticalChance = ChatColor.GREEN + df2.format(criticalChanceBase + criticalChanceBuff) + "% CRIT" + (criticalChanceBuff != 0.0 ? " [" + df2.format(criticalChanceBuff) + "]" : "");
    String criticalDamage = ChatColor.DARK_GREEN + df2.format(1.25 + (criticalDamageBase + criticalDamageBuff) / 100) + "x CRIT DMG" + (criticalDamageBuff != 0.0 ? " [" + df2.format(criticalDamageBuff) + "]" : "");
    String feintSkill = ChatColor.DARK_AQUA + df2.format(feintSkillBase + feintSkillBuff) + " FEINT" + (feintSkillBuff != 0.0 ? " [" + df2.format(feintSkillBuff) + "]" : "");
    String accuracySkill = ChatColor.DARK_PURPLE + df2.format(accuracySkillBase + accuracySkillBuff) + " ACC" + (accuracySkillBuff != 0.0 ? " [" + df2.format(accuracySkillBuff) + "]" : "");

    menu.setItem(15, ItemCreator.createItem(Material.IRON_SWORD, ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Offense", List.of(damage, attackSpeed, criticalChance, criticalDamage, feintSkill, accuracySkill), ItemFlag.HIDE_ATTRIBUTES));
  }

  /**
   * Adds the player's defense attributes.
   *
   * @param entityTags owner's persistent tags
   * @param buffs      {@link Buffs}
   * @param df2        0.00 decimal format
   */
  private void addDefenseAttributes(PersistentDataContainer entityTags, Buffs buffs, DecimalFormat df2) {
    double counterChanceBase = entityTags.getOrDefault(Key.ATTRIBUTE_COUNTER_CHANCE.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
    double dodgeChanceBase = entityTags.getOrDefault(Key.ATTRIBUTE_DODGE_CHANCE.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
    double armorToughnessBase = entityTags.getOrDefault(Key.ATTRIBUTE_ARMOR_TOUGHNESS.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);

    double genericMaxHealthBuff = 0.0;
    double maxHealthBuff = 0.0;
    double counterChanceBuff = 0.0;
    double dodgeChanceBuff = 0.0;
    double genericArmorToughnessBuff = 0.0;
    double armorToughnessBuff = 0.0;
    double armorBuff = 0.0;
    if (buffs != null) {
      genericMaxHealthBuff = buffs.getAttribute(Attribute.GENERIC_MAX_HEALTH);
      maxHealthBuff = buffs.getAethelAttribute(AethelAttribute.MAX_HEALTH);
      counterChanceBuff = buffs.getAethelAttribute(AethelAttribute.COUNTER_CHANCE);
      dodgeChanceBuff = buffs.getAethelAttribute(AethelAttribute.DODGE_CHANCE);
      genericArmorToughnessBuff = buffs.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS);
      armorToughnessBuff = buffs.getAethelAttribute(AethelAttribute.ARMOR_TOUGHNESS);
      armorBuff = buffs.getAttribute(Attribute.GENERIC_ARMOR);
    }

    double featherFallingBase = entityTags.getOrDefault(Key.ENCHANTMENT_FEATHER_FALLING.getNamespacedKey(), PersistentDataType.INTEGER, 0);
    double protectionBase = entityTags.getOrDefault(Key.ENCHANTMENT_PROTECTION.getNamespacedKey(), PersistentDataType.INTEGER, 0);
    double blastProtectionBase = entityTags.getOrDefault(Key.ENCHANTMENT_BLAST_PROTECTION.getNamespacedKey(), PersistentDataType.INTEGER, 0);
    double fireProtectionBase = entityTags.getOrDefault(Key.ENCHANTMENT_FIRE_PROTECTION.getNamespacedKey(), PersistentDataType.INTEGER, 0);
    double projectileProtectionBase = entityTags.getOrDefault(Key.ENCHANTMENT_PROJECTILE_PROTECTION.getNamespacedKey(), PersistentDataType.INTEGER, 0);

    Health health = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getHealth();

    String maxHealth = ChatColor.RED + df2.format(health.getCurrentHealth()) + " / " + df2.format(health.getMaxHealth()) + " HP" + (genericMaxHealthBuff + maxHealthBuff != 0.0 ? " [" + df2.format(genericMaxHealthBuff + maxHealthBuff) + "]" : "");
    String counterChance = ChatColor.YELLOW + df2.format(counterChanceBase + counterChanceBuff) + "% COUNTER" + (counterChanceBuff != 0.0 ? " [" + df2.format(counterChanceBuff) + "]" : "");
    String dodgeChance = ChatColor.BLUE + df2.format(dodgeChanceBase + dodgeChanceBuff) + "% DODGE" + (dodgeChanceBuff != 0.0 ? " [" + df2.format(dodgeChanceBuff) + "]" : "");
    String armorToughness = ChatColor.GRAY + df2.format(owner.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue() + armorToughnessBase + armorToughnessBuff) + " TOUGH" + (genericArmorToughnessBuff + armorToughnessBuff != 0.0 ? " [" + df2.format(genericArmorToughnessBuff + armorToughnessBuff) + "]" : "");
    String armor = ChatColor.GRAY + df2.format(owner.getAttribute(Attribute.GENERIC_ARMOR).getValue()) + " ARMOR" + (armorBuff != 0.0 ? " [" + df2.format(armorBuff) + "]" : "");

    String featherFalling = ChatColor.GRAY + "" + featherFallingBase + " FEATHER FALL";
    String protection = ChatColor.GRAY + "" + protectionBase + " PROT";
    String blastProtection = ChatColor.GRAY + "" + blastProtectionBase + " BLAST PROT";
    String fireProtection = ChatColor.GRAY + "" + fireProtectionBase + " FIRE PROT";
    String projectileProtection = ChatColor.GRAY + "" + projectileProtectionBase + " PROJ PROT";

    menu.setItem(24, ItemCreator.createItem(Material.IRON_CHESTPLATE, ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Defense", List.of(maxHealth, counterChance, dodgeChance, armorToughness, armor, "", featherFalling, protection, blastProtection, fireProtection, projectileProtection), ItemFlag.HIDE_ATTRIBUTES));
  }

  /**
   * Adds the player's other attributes.
   *
   * @param entityTags owner's persistent tags
   * @param buffs      {@link Buffs}
   * @param df2        0.00 decimal format
   */
  private void addOtherAttributes(PersistentDataContainer entityTags, Buffs buffs, DecimalFormat df2) {
    DecimalFormat df3 = new DecimalFormat();
    df3.setMaximumFractionDigits(3);

    double itemDamageBase = entityTags.getOrDefault(Key.ATTRIBUTE_ITEM_DAMAGE.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
    double itemCooldownBase = entityTags.getOrDefault(Key.ATTRIBUTE_ITEM_COOLDOWN.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);
    double tenacityBase = entityTags.getOrDefault(Key.ATTRIBUTE_TENACITY.getNamespacedKey(), PersistentDataType.DOUBLE, 0.0);

    double itemDamageBuff = 0.0;
    double itemCooldownBuff = 0.0;
    double speedBuff = 0.0;
    double luckBuff = 0.0;
    double knockbackResistanceBuff = 0.0;
    double tenacityBuff = 0.0;
    if (buffs != null) {
      itemDamageBuff = buffs.getAethelAttribute(AethelAttribute.ITEM_DAMAGE);
      itemCooldownBuff = buffs.getAethelAttribute(AethelAttribute.ITEM_COOLDOWN);
      speedBuff = buffs.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
      luckBuff = buffs.getAttribute(Attribute.GENERIC_LUCK);
      knockbackResistanceBuff = buffs.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
      tenacityBuff = buffs.getAethelAttribute(AethelAttribute.TENACITY);
    }

    String itemDamage = ChatColor.LIGHT_PURPLE + df2.format(1.0 + (itemDamageBase + itemDamageBuff) / 100) + "x ITEM DMG" + (itemDamageBuff != 0.0 ? " [" + df2.format(itemDamageBuff) + "]" : "");
    String itemCooldown = ChatColor.DARK_PURPLE + "-" + df2.format(itemCooldownBase + itemCooldownBuff) + "% ITEM CD" + (itemCooldownBuff != 0.0 ? " [" + df2.format(itemCooldownBuff) + "]" : "");
    String speed = ChatColor.DARK_AQUA + df3.format(owner.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue()) + " SPEED" + (speedBuff != 0.0 ? " [" + df2.format(speedBuff) + "]" : "");
    String luck = ChatColor.GREEN + df2.format(owner.getAttribute(Attribute.GENERIC_LUCK).getValue()) + " LUCK" + (luckBuff != 0.0 ? " [" + df2.format(luckBuff) + "]" : "");
    String knockbackResistance = ChatColor.GRAY + "-" + df2.format(owner.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).getValue() * 100) + "% KNOCKBACK" + (knockbackResistanceBuff != 0.0 ? " [" + df2.format(knockbackResistanceBuff) + "]" : "");
    String tenacity = ChatColor.DARK_GREEN + df2.format(tenacityBase + tenacityBuff) + "% TENACITY" + (tenacityBuff != 0.0 ? " [" + df2.format(tenacityBuff) + "]" : "");

    menu.setItem(33, ItemCreator.createItem(Material.SPYGLASS, ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Other", List.of(itemDamage, itemCooldown, speed, luck, knockbackResistance, tenacity)));
  }
}
