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
 * @version 1.17.9
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
    Map<AethelAttribute, Double> attributes = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getAethelAttributes().getAttributes();

    DecimalFormat df2 = new DecimalFormat();
    df2.setMaximumFractionDigits(2);

    addOffenseAttributes(attributes, df2);
    addDefenseAttributes(attributes, df2);
    addOtherAttributes(attributes, df2);
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
   * @param attributes owner's attributes
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
   * @param attributes owner's attributes
   * @param df2        0.00 decimal format
   */
  private void addDefenseAttributes(Map<AethelAttribute, Double> attributes, DecimalFormat df2) {
    RpgPlayer rpgPlayer = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid);
    Health health = rpgPlayer.getHealth();
    Map<Enchantment, Integer> enchantments = rpgPlayer.getEnchantments().getTotalEnchantments();

    String maxHealth = ChatColor.RED + "" + df2.format(health.getCurrentHealth()) + " / " + df2.format(owner.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + attributes.get(AethelAttribute.MAX_HEALTH)) + " HP";
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
   * @param attributes owner's attributes
   * @param df2        0.00 decimal format
   */
  private void addOtherAttributes(Map<AethelAttribute, Double> attributes, DecimalFormat df2) {
    DecimalFormat df3 = new DecimalFormat();
    df3.setMaximumFractionDigits(3);

    String itemDamage = ChatColor.LIGHT_PURPLE + "" + df2.format(1.0 + attributes.get(AethelAttribute.ITEM_DAMAGE) / 100) + "x ITEM DMG";
    String itemCooldown = ChatColor.DARK_PURPLE + "-" + df2.format(attributes.get(AethelAttribute.ITEM_COOLDOWN)) + "% ITEM CD";
    String speed = ChatColor.DARK_AQUA + "" + df3.format(owner.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue()) + " SPEED";
    String luck = ChatColor.GREEN + "" + df2.format(owner.getAttribute(Attribute.GENERIC_LUCK).getValue()) + " LUCK";
    String knockbackResistance = ChatColor.GRAY + "-" + df2.format(owner.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).getValue() * 100) + "% KNOCKBACK";

    menu.setItem(33, ItemCreator.createItem(Material.SPYGLASS, ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Other", List.of(itemDamage, itemCooldown, speed, luck, knockbackResistance)));
  }
}
