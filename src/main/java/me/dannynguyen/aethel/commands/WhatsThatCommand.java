package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType;
import me.dannynguyen.aethel.utils.TextFormatter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command invocation that gets a key word's description.
 * <p>
 * Registered through {@link Plugin}.
 * <p>
 * Parameters:
 * <ul>
 *  <li>keyWord: gets the key word's description
 * </ul>
 *
 * @author Danny Nguyen
 * @version 1.22.1
 * @since 1.22.1
 */
public class WhatsThatCommand implements CommandExecutor {
  /**
   * No parameter constructor.
   */
  public WhatsThatCommand() {
  }

  /**
   * Executes the WhatsThatFeature command.
   *
   * @param sender  command source
   * @param command executed command
   * @param label   command alias used
   * @param args    command parameters
   * @return true if a valid command
   */
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
    if (sender instanceof Player user) {
      if (user.hasPermission("aethel.whatsthatfeature")) {
        readRequest(user, args);
      } else {
        user.sendMessage(Message.INSUFFICIENT_PERMISSION.getMessage());
      }
    } else {
      sender.sendMessage(Message.PLAYER_ONLY_COMMAND.getMessage());
    }
    return true;
  }

  /**
   * Checks if the command request was formatted correctly before sending a key word's description.
   *
   * @param user user
   * @param args user provided parameters
   */
  private void readRequest(Player user, String[] args) {
    switch (args.length) {
      case 0 -> sendKeyWordDescription(user, KeyWord.HELP);
      default -> interpretKeyWord(user, args);
    }
  }

  /**
   * Either does something or another thing.
   *
   * @param user user
   * @param args user provided parameters
   */
  private void interpretKeyWord(Player user, String[] args) {
    StringBuilder userInput = new StringBuilder();
    for (String word : args) {
      userInput.append(word).append(" ");
    }

    KeyWord keyWord;
    try {
      keyWord = KeyWord.valueOf(TextFormatter.formatEnum(userInput.toString().trim()));
    } catch (IllegalArgumentException ex) {
      user.sendMessage(ChatColor.RED + "Invalid key word.");
      return;
    }

    switch (keyWord) {
      case COMMANDS -> sendKeyWordDescription(user, KeyWord.COMMANDS);
      case WORLD_TYPES -> sendKeyWordDescription(user, KeyWord.WORLD_TYPES);
      case ENCHANTMENTS -> sendKeyWordDescription(user, KeyWord.ENCHANTMENTS);
      case POTION_EFFECTS -> sendKeyWordDescription(user, KeyWord.POTION_EFFECTS);
      case ATTRIBUTES -> sendKeyWordDescription(user, KeyWord.ATTRIBUTES);
      case ABILITIES -> sendKeyWordDescription(user, KeyWord.ABILITIES);
      case PASSIVE_TRIGGERS -> sendKeyWordDescription(user, KeyWord.PASSIVE_TRIGGERS);
      case PASSIVE_ABILITIES -> sendKeyWordDescription(user, KeyWord.PASSIVE_ABILITIES);
      case ACTIVE_ABILITIES -> sendKeyWordDescription(user, KeyWord.ACTIVE_ABILITIES);
      case STATUSES -> sendKeyWordDescription(user, KeyWord.STATUSES);
      case CUMULATIVE -> sendKeyWordDescription(user, KeyWord.CUMULATIVE);
      case HIGHEST_INSTANCE -> sendKeyWordDescription(user, KeyWord.HIGHEST_INSTANCE);
      case CHARACTER -> sendKeyWordDescription(user, KeyWord.CHARACTER);
      case FORGE -> sendKeyWordDescription(user, KeyWord.FORGE);
      case PING -> sendKeyWordDescription(user, KeyWord.PING);
      case PLAYERSTAT -> sendKeyWordDescription(user, KeyWord.PLAYERSTAT);
      case SHOWITEM -> sendKeyWordDescription(user, KeyWord.SHOWITEM);
      case OVERWORLD -> sendKeyWordDescription(user, KeyWord.OVERWORLD);
      case NETHER -> sendKeyWordDescription(user, KeyWord.NETHER);
      case END -> sendKeyWordDescription(user, KeyWord.END);
      case RESOURCE -> sendKeyWordDescription(user, KeyWord.RESOURCE);
      case EVENT -> sendKeyWordDescription(user, KeyWord.EVENT);
      case RPG -> sendKeyWordDescription(user, KeyWord.RPG);
      case PROTECTION -> sendKeyWordDescription(user, KeyWord.PROTECTION);
      case BLAST_PROTECTION -> sendKeyWordDescription(user, KeyWord.BLAST_PROTECTION);
      case FIRE_PROTECTION -> sendKeyWordDescription(user, KeyWord.FIRE_PROTECTION);
      case PROJECTILE_PROTECTION -> sendKeyWordDescription(user, KeyWord.PROJECTILE_PROTECTION);
      case FEATHER_FALLING -> sendKeyWordDescription(user, KeyWord.FEATHER_FALLING);
      case ABSORPTION -> sendKeyWordDescription(user, KeyWord.ABSORPTION);
      case RESISTANCE -> sendKeyWordDescription(user, KeyWord.RESISTANCE);
      case MAX_HEALTH -> sendKeyWordDescription(user, KeyWord.MAX_HEALTH);
      case COUNTER_CHANCE -> sendKeyWordDescription(user, KeyWord.COUNTER_CHANCE);
      case DODGE_CHANCE -> sendKeyWordDescription(user, KeyWord.DODGE_CHANCE);
      case ARMOR_TOUGHNESS -> sendKeyWordDescription(user, KeyWord.ARMOR_TOUGHNESS);
      case ARMOR -> sendKeyWordDescription(user, KeyWord.ARMOR);
      case ITEM_DAMAGE -> sendKeyWordDescription(user, KeyWord.ITEM_DAMAGE);
      case ITEM_COOLDOWN -> sendKeyWordDescription(user, KeyWord.ITEM_COOLDOWN);
      case TENACITY -> sendKeyWordDescription(user, KeyWord.TENACITY);
      case BELOW_HEALTH -> sendKeyWordDescription(user, KeyWord.BELOW_HEALTH);
      case DAMAGE_DEALT -> sendKeyWordDescription(user, KeyWord.DAMAGE_DEALT);
      case DAMAGE_TAKEN -> sendKeyWordDescription(user, KeyWord.DAMAGE_TAKEN);
      case ON_KILL -> sendKeyWordDescription(user, KeyWord.ON_KILL);
      case BUFF -> sendKeyWordDescription(user, KeyWord.BUFF);
      case CHAIN_DAMAGE -> sendKeyWordDescription(user, KeyWord.CHAIN_DAMAGE);
      case SPARK -> sendKeyWordDescription(user, KeyWord.SPARK);
      case EFFECT -> sendKeyWordDescription(user, KeyWord.EFFECT);
      case STACK_INSTANCE -> sendKeyWordDescription(user, KeyWord.STACK_INSTANCE);
      case CLEAR_STATUS -> sendKeyWordDescription(user, KeyWord.CLEAR_STATUS);
      case DISMISS -> sendKeyWordDescription(user, KeyWord.DISMISS);
      case DISREGARD -> sendKeyWordDescription(user, KeyWord.DISREGARD);
      case DISTANCE_DAMAGE -> sendKeyWordDescription(user, KeyWord.DISTANCE_DAMAGE);
      case EXPLODE -> sendKeyWordDescription(user, KeyWord.EXPLODE);
      case FORCE_SWEEP -> sendKeyWordDescription(user, KeyWord.FORCE_SWEEP);
      case FORCE_WAVE -> sendKeyWordDescription(user, KeyWord.FORCE_WAVE);
      case QUAKE -> sendKeyWordDescription(user, KeyWord.QUAKE);
      case MOVEMENT -> sendKeyWordDescription(user, KeyWord.MOVEMENT);
      case DASH -> sendKeyWordDescription(user, KeyWord.DASH);
      case LEAP -> sendKeyWordDescription(user, KeyWord.LEAP);
      case SPRING -> sendKeyWordDescription(user, KeyWord.SPRING);
      case WITHDRAW -> sendKeyWordDescription(user, KeyWord.WITHDRAW);
      case SHATTER -> sendKeyWordDescription(user, KeyWord.SHATTER);
      case TELEPORT -> sendKeyWordDescription(user, KeyWord.TELEPORT);
      case BLINK -> sendKeyWordDescription(user, KeyWord.BLINK);
      case PROJECTION -> sendKeyWordDescription(user, KeyWord.PROJECTION);
      case BLEED -> sendKeyWordDescription(user, KeyWord.BLEED);
      case BRITTLE -> sendKeyWordDescription(user, KeyWord.BRITTLE);
      case ELECTROCUTE -> sendKeyWordDescription(user, KeyWord.ELECTROCUTE);
      case SOAKED -> sendKeyWordDescription(user, KeyWord.SOAKED);
      case FRACTURE -> sendKeyWordDescription(user, KeyWord.FRACTURE);
      case VULNERABLE -> sendKeyWordDescription(user, KeyWord.VULNERABLE);
    }
  }

  /**
   * Sends the user the key word's description.
   *
   * @param user    user
   * @param keyWord key word
   */
  private void sendKeyWordDescription(Player user, KeyWord keyWord) {
    for (String line : keyWord.getDescription()) {
      user.sendMessage(line);
    }
  }

  /**
   * Key word that describes a gameplay feature.
   */
  enum KeyWord {
    /**
     * Key word sections.
     */
    HELP(new String[]{
        ChatColor.GREEN + "Help",
        ChatColor.GRAY + "Search a related key word below to get started.",
        "Related: " + ChatColor.AQUA + "commands, world types, enchantments, potion effects, attributes, abilities, statuses"}),

    /**
     * Default-access commands.
     */
    COMMANDS(new String[]{
        ChatColor.GREEN + "Commands",
        ChatColor.GRAY + "These are commands you are able to use by default.",
        "Related: " + ChatColor.AQUA + "character, forge, ping, playerstat, showitem"}),

    /**
     * World types.
     */
    WORLD_TYPES(new String[]{
        ChatColor.GREEN + "World Types",
        ChatColor.GRAY + "There are six different world types.",
        "Related: " + ChatColor.AQUA + "overworld, nether, end, resource, event, rpg"
    }),

    /**
     * Modified enchantments.
     */
    ENCHANTMENTS(new String[]{
        ChatColor.GREEN + "Enchantments",
        ChatColor.GRAY + "Modified enchantments are listed below.",
        "Related: " + ChatColor.AQUA + "protection, blast protection, fire protection, projectile protection, feather falling"
    }),

    /**
     * Modified potion effects.
     */
    POTION_EFFECTS(new String[]{
        ChatColor.GREEN + "Potion Effects",
        ChatColor.GRAY + "Modified potion effects are listed below.",
        "Related: " + ChatColor.AQUA + "absorption, resistance"
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.AethelAttribute}
     */
    ATTRIBUTES(new String[]{
        ChatColor.GREEN + "Attributes",
        ChatColor.GRAY + "Custom attributes are listed below.",
        "Related: " + ChatColor.AQUA + "max health, counter chance, dodge chance, armor toughness, armor, item damage, item cooldown, tenacity"
    }),

    /**
     * {@link me.dannynguyen.aethel.rpg.abilities.Abilities}
     */
    ABILITIES(new String[]{
        ChatColor.GREEN + "Abilities",
        ChatColor.GRAY + "Abilities are obtained from wearing equipment.",
        "Related: " + ChatColor.AQUA + "passive triggers, passive abilities, active abilities"
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType}
     */
    PASSIVE_TRIGGERS(new String[]{
        ChatColor.GREEN + "Passive Triggers",
        "Related: " + ChatColor.AQUA + "below health, damage dealt, damage taken, on kill"
    }),

    /**
     * {@link me.dannynguyen.aethel.rpg.abilities.PassiveAbility}
     */
    PASSIVE_ABILITIES(new String[]{
        ChatColor.GREEN + "Passive Abilities",
        "Related: " + ChatColor.AQUA + "buff, chain damage, effect, stack instance"
    }),

    /**
     * {@link me.dannynguyen.aethel.rpg.abilities.ActiveAbility}
     */
    ACTIVE_ABILITIES(new String[]{
        ChatColor.GREEN + "Active Abilities",
        ChatColor.GRAY + "Must be bound through the character sheet menu settings first before being able to be used.",
        ChatColor.GRAY + "To activate an active ability, select the hotbar slot the ability slot is bound to and crouch.",
        "Related: " + ChatColor.AQUA + "buff, clear status, distance damage, movement, potion effect, shatter, teleport"
    }),

    /**
     * {@link me.dannynguyen.aethel.rpg.Status}
     */
    STATUSES(new String[]{
        ChatColor.GREEN + "Statuses",
        ChatColor.GRAY + "Statuses are like potion effects, except that statuses are measured in stacks that have individual durations.",
        "Related: " + ChatColor.AQUA + "cumulative, highest instance"
    }),

    /**
     * {@link me.dannynguyen.aethel.rpg.Status}
     */
    CUMULATIVE(new String[]{
        ChatColor.GREEN + "Cumulative",
        ChatColor.GRAY + "Stacks are represented together.",
        "Related: " + ChatColor.AQUA + "bleed, brittle, electrocute, soaked"
    }),

    /**
     * {@link me.dannynguyen.aethel.rpg.Status}
     */
    HIGHEST_INSTANCE(new String[]{
        ChatColor.GREEN + "Highest Instance",
        ChatColor.GRAY + "Stacks are represented by their highest stack application.",
        "Related: " + ChatColor.AQUA + "fracture, vulnerable"
    }),

    /**
     * {@link me.dannynguyen.aethel.commands.character.CharacterCommand}
     */
    CHARACTER(new String[]{
        ChatColor.GREEN + "Character",
        ChatColor.GRAY + "/character, /char, /c",
        ChatColor.GRAY + "RPG character sheet.",
        ChatColor.GRAY + ": Opens a character sheet belonging to the user.",
        ChatColor.GRAY + "<playerName>: Opens a character sheet belonging to the player."}),

    /**
     * {@link me.dannynguyen.aethel.commands.forge.ForgeCommand}
     */
    FORGE(new String[]{
        ChatColor.GREEN + "Forge",
        ChatColor.GRAY + "/forge, /f",
        ChatColor.GRAY + "Craft Forge recipes.",
        ChatColor.GRAY + ": Opens the Forge crafting menu."}),

    /**
     * {@link PingCommand}
     */
    PING(new String[]{
        ChatColor.GREEN + "Ping",
        ChatColor.GRAY + "/ping, /p",
        ChatColor.GRAY + "Ping.",
        ChatColor.GRAY + ": Responds with server latency."}),

    /**
     * {@link me.dannynguyen.aethel.commands.playerstat.StatCommand}
     */
    PLAYERSTAT(new String[]{
        ChatColor.GREEN + "PlayerStat",
        ChatColor.GRAY + "/playerstat, /stat, /ps",
        ChatColor.GRAY + "Gets player statistics.",
        ChatColor.GRAY + ": Opens a player statistics menu belonging to the user.",
        ChatColor.GRAY + "<playerName>: Opens a player statistics menu belonging to the player."
    }),

    /**
     * {@link me.dannynguyen.aethel.commands.showitem.ShowItemCommand}
     */
    SHOWITEM(new String[]{
        ChatColor.GREEN + "ShowItem",
        ChatColor.GRAY + "/showitem, /show, /si",
        ChatColor.GRAY + "Shows main hand item to chat.",
        ChatColor.GRAY + ": Shows your main hand item to chat.",
        ChatColor.GRAY + "past: Opens a past shared items menu."
    }),

    /**
     * Overworld dimension.
     */
    OVERWORLD(new String[]{
        ChatColor.GREEN + "Overworld",
        ChatColor.GRAY + "Seasonal, bordered at 10k.",
        ChatColor.GRAY + "Primary SMP world.",
    }),

    /**
     * Nether dimension.
     */
    NETHER(new String[]{
        ChatColor.GREEN + "Nether",
        ChatColor.GRAY + "Seasonal, bordered at 10k.",
        ChatColor.GRAY + "Border expanded as necessary."
    }),

    /**
     * End dimension.
     */
    END(new String[]{
        ChatColor.GREEN + "End",
        ChatColor.GRAY + "Renewable, no world border.",
        ChatColor.GRAY + "Regenerated as necessary."
    }),

    /**
     * Resource world.
     */
    RESOURCE(new String[]{
        ChatColor.GREEN + "Resource",
        ChatColor.GRAY + "Renewable, no world border.",
        ChatColor.GRAY + "Regenerated as necessary."
    }),

    /**
     * Event world.
     */
    EVENT(new String[]{
        ChatColor.GREEN + "Event",
        ChatColor.GRAY + "Permanent.",
        ChatColor.GRAY + "Minigames and events."
    }),

    /**
     * RPG world.
     */
    RPG(new String[]{
        ChatColor.GREEN + "RPG",
        ChatColor.GRAY + "Permanent.",
        ChatColor.GRAY + "Handmade adventure world."
    }),

    /**
     * Protection enchantment.
     */
    PROTECTION(new String[]{
        ChatColor.GREEN + "Protection",
        ChatColor.GRAY + "Each level of Protection mitigates non-environmental damage by 1%, up to a maximum of 20%."
    }),

    /**
     * Blast protection enchantment.
     */
    BLAST_PROTECTION(new String[]{
        ChatColor.GREEN + "Blast Protection",
        ChatColor.GRAY + "Each level of Blast Protection mitigates explosion damage by 10%.",
        ChatColor.GRAY + "At 10 levels of Blast Protection across all equipment, the wearer heals 20% of the explosion's damage and fills their hunger."
    }),

    /**
     * Fire protection enchantment.
     */
    FIRE_PROTECTION(new String[]{
        ChatColor.GREEN + "Fire Protection",
        ChatColor.GRAY + "Each level of fire protection mitigates fire damage by 10%.",
        ChatColor.GRAY + "At 10 levels of Fire Protection across all equipment, the wearer gains permanent Fire Resistance."
    }),

    /**
     * Projectile protection enchantment.
     */
    PROJECTILE_PROTECTION(new String[]{
        ChatColor.GREEN + "Projectile Protection",
        ChatColor.GRAY + "Each level of Projectile Protection mitigates projectile protection by 5%, up to a maximum of 50%.",
        ChatColor.GRAY + "At 10 levels of Projectile Protection across all equipment, the wearer gains the ability to catch all types of arrows, fireballs, and snowballs."
    }),

    /**
     * Feather falling enchantment.
     */
    FEATHER_FALLING(new String[]{
        ChatColor.GREEN + "Feather Falling",
        ChatColor.GRAY + "Each level of Feather Falling mitigates fall damage by 20%.",
        ChatColor.GRAY + "At 5 levels of Feather Falling across all equipment, the wearer gains permanent Slow Falling."
    }),

    /**
     * Absorption potion effect.
     */
    ABSORPTION(new String[]{
        ChatColor.GREEN + "Absorption",
        ChatColor.GRAY + "Absorption is immediately added to health.",
        ChatColor.GRAY + "Health above maximum health is called Overshield. Overshield begins to decay above x1.2 maximum health."
    }),

    /**
     * Resistance potion effect.
     */
    RESISTANCE(new String[]{
        ChatColor.GREEN + "Resistance",
        ChatColor.GRAY + "Each level of resistance mitigates all forms of damage by 5%."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.AethelAttribute#MAX_HEALTH}
     */
    MAX_HEALTH(new String[]{
        ChatColor.GREEN + "Max Health",
        ChatColor.GRAY + "Adds max health without visually displaying additional heart containers."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.AethelAttribute#COUNTER_CHANCE}
     */
    COUNTER_CHANCE(new String[]{
        ChatColor.GREEN + "Counter Chance",
        ChatColor.GRAY + "Chance to deal counter attacks.",
        ChatColor.GRAY + "The number of counter attacks dealt is based on the defender's attack speed, at least 1, with the attack speed's whole number rounded down.",
        ChatColor.GRAY + "The damage dealt per counterattack is based on the defender's attack damage, with no chance to deal critical damage.",
        ChatColor.GRAY + "If the attacker dies from counter attacks, no damage is dealt to the defender."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.AethelAttribute#DODGE_CHANCE}
     */
    DODGE_CHANCE(new String[]{
        ChatColor.GREEN + "Dodge Chance",
        ChatColor.GRAY + "Chance to ignore damage from attacking entities or explosions."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.AethelAttribute#ARMOR_TOUGHNESS}
     */
    ARMOR_TOUGHNESS(new String[]{
        ChatColor.GREEN + "Armor Toughness",
        ChatColor.GRAY + "Flat damage mitigation from non-magical and non-environmental sources.",
        ChatColor.GRAY + "Flat damage blocked is based on armor toughness divided by 2.",
        ChatColor.GRAY + "If the damage blocked is higher than the incoming damage, then no damage is dealt to the defender."
    }),

    /**
     * Armor attribute.
     */
    ARMOR(new String[]{
        ChatColor.GREEN + "Armor",
        ChatColor.GRAY + "Percentage damage mitigation from non-magical and non-environmental sources.",
        ChatColor.GRAY + "Each point of armor mitigates incoming damage by 2%, to a maximum of 40%."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.AethelAttribute#ITEM_DAMAGE}
     */
    ITEM_DAMAGE(new String[]{
        ChatColor.GREEN + "Item Damage",
        ChatColor.GRAY + "Item ability damage modifier."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.AethelAttribute#ITEM_COOLDOWN}
     */
    ITEM_COOLDOWN(new String[]{
        ChatColor.GREEN + "Item Cooldown",
        ChatColor.GRAY + "Item ability cooldown modifier."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.AethelAttribute#TENACITY}
     */
    TENACITY(new String[]{
        ChatColor.GREEN + "Tenacity",
        ChatColor.GRAY + "Reduces the duration of negative statuses."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType#BELOW_HEALTH}
     */
    BELOW_HEALTH(new String[]{
        ChatColor.GREEN + "Below Health",
        ChatColor.GRAY + "Activates below a certain % of health."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType#DAMAGE_DEALT}
     */
    DAMAGE_DEALT(new String[]{
        ChatColor.GREEN + "Damage Dealt",
        ChatColor.GRAY + "Activated on damage dealt.",
        ChatColor.GRAY + "The attack must be at least 75% charged to activate damage dealt passive abilities."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType#DAMAGE_TAKEN}
     */
    DAMAGE_TAKEN(new String[]{
        ChatColor.GREEN + "Damage Taken",
        ChatColor.GRAY + "Activated when taking damage."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType#ON_KILL}
     */
    ON_KILL(new String[]{
        ChatColor.GREEN + "On Kill",
        ChatColor.GRAY + "Activated on killing any living entity."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType#AETHEL_ATTRIBUTE}
     * {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType#ATTRIBUTE}
     * {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType#AETHEL_ATTRIBUTE}
     * {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType#ATTRIBUTE}
     */
    BUFF(new String[]{
        ChatColor.GREEN + "Buff",
        ChatColor.GRAY + "Applies a buff."
    }),

    /**
     * {@link PassiveAbilityType.Effect#CHAIN_DAMAGE}
     */
    CHAIN_DAMAGE(new String[]{
        ChatColor.GREEN + "Chain Damage",
        ChatColor.GRAY + "Deals chain damage.",
        "Related: " + ChatColor.AQUA + "spark"
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType#SPARK}
     */
    SPARK(new String[]{
        ChatColor.GREEN + "Spark",
        ChatColor.GRAY + "Attacks chain to entities with soaked status."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType#POTION_EFFECT}
     * {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType#POTION_EFFECT}
     */
    EFFECT(new String[]{
        ChatColor.GREEN + "Effect",
        ChatColor.GRAY + "Applies potion effects."
    }),

    /**
     * {@link PassiveAbilityType.Effect#STACK_INSTANCE}
     */
    STACK_INSTANCE(new String[]{
        ChatColor.GREEN + "Stack Instance",
        ChatColor.GRAY + "Applies stacks of Statuses.",
        "Related: " + ChatColor.AQUA + "bleed, brittle, electrocute, fracture, soaked, vulnerable"
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType.Effect#CLEAR_STATUS}
     */
    CLEAR_STATUS(new String[]{
        ChatColor.GREEN + "Clear Status",
        ChatColor.GRAY + "Clears statuses to be removed.",
        "Related: " + ChatColor.AQUA + "dismiss, disregard"
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType#DISMISS}
     */
    DISMISS(new String[]{
        ChatColor.GREEN + "Dismiss",
        ChatColor.GRAY + "Clears all non-damaging potion effects and statuses."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType#DISREGARD}
     */
    DISREGARD(new String[]{
        ChatColor.GREEN + "Disregard",
        ChatColor.GRAY + "Clears all damaging potion effects and statuses."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType.Effect#DISTANCE_DAMAGE}
     */
    DISTANCE_DAMAGE(new String[]{
        ChatColor.GREEN + "Distance Damage",
        ChatColor.GRAY + "Causes damage at a distance",
        "Related: " + ChatColor.AQUA + "explode, force sweep, force wave, quake"
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType#EXPLODE}
     */
    EXPLODE(new String[]{
        ChatColor.GREEN + "Explode",
        ChatColor.GRAY + "Spherical shaped attack."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType#FORCE_SWEEP}
     */
    FORCE_SWEEP(new String[]{
        ChatColor.GREEN + "Force Sweep",
        ChatColor.GRAY + "Forward facing triangular arc shaped attack."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType#FORCE_WAVE}
     */
    FORCE_WAVE(new String[]{
        ChatColor.GREEN + "Force Wave",
        ChatColor.GRAY + "Omnidirectional forward-facing line-shaped attack."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType#QUAKE}
     */
    QUAKE(new String[]{
        ChatColor.GREEN + "Quake",
        ChatColor.GRAY + "Circular shaped attack."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType.Effect#MOVEMENT}
     */
    MOVEMENT(new String[]{
        ChatColor.GREEN + "Movement",
        ChatColor.GRAY + "Causes movement with velocity.",
        "Related: " + ChatColor.AQUA + "dash, leap, spring, withdraw"
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType#DASH}
     */
    DASH(new String[]{
        ChatColor.GREEN + "Dash",
        ChatColor.GRAY + "Forward facing movement."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType#LEAP}
     */
    LEAP(new String[]{
        ChatColor.GREEN + "Leap",
        ChatColor.GRAY + "Omnidirectional forward-facing angular movement."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType#SPRING}
     */
    SPRING(new String[]{
        ChatColor.GREEN + "Spring",
        ChatColor.GRAY + "Upwards facing movement."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType#WITHDRAW}
     */
    WITHDRAW(new String[]{
        ChatColor.GREEN + "Withdraw",
        ChatColor.GRAY + "Backwards facing movement."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType#SHATTER}
     */
    SHATTER(new String[]{
        ChatColor.GREEN + "Shatter",
        ChatColor.GRAY + "Immediately consumes all stacks of Brittle from nearby enemies to deal an instance of damage."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType.Effect#TELEPORT}
     */
    TELEPORT(new String[]{
        ChatColor.GREEN + "Teleport",
        "Causes instant movement.",
        "Section: " + ChatColor.AQUA + "blink, projection"
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType#BLINK}
     */
    BLINK(new String[]{
        ChatColor.GREEN + "Blink",
        ChatColor.GRAY + "Omnidirectional forward-facing teleport."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType#PROJECTION}
     */
    PROJECTION(new String[]{
        ChatColor.GREEN + "Projection",
        ChatColor.GRAY + "Omnidirectional forward-facing teleport that after a delay, teleports the user back to their original location."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.StatusType#BLEED}}
     */
    BLEED(new String[]{
        ChatColor.GREEN + "Bleed",
        ChatColor.GRAY + "Damage over time."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.StatusType#BRITTLE}
     */
    BRITTLE(new String[]{
        ChatColor.GREEN + "Brittle",
        ChatColor.GRAY + "Can be triggered by Shatter to deal an instance of damage."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.StatusType#ELECTROCUTE}
     */
    ELECTROCUTE(new String[]{
        ChatColor.GREEN + "Electrocute",
        ChatColor.GRAY + "Damage over time that spreads its remaining stacks to nearby entities on death."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.StatusType#SOAKED}
     */
    SOAKED(new String[]{
        ChatColor.GREEN + "Soaked",
        ChatColor.GRAY + "Allows and increases chain damage between entities."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.StatusType#FRACTURE}
     */
    FRACTURE(new String[]{
        ChatColor.GREEN + "Fracture",
        ChatColor.GRAY + "Reduces an entity's effective armor value in damage calculations by 1 per stack."
    }),

    /**
     * {@link me.dannynguyen.aethel.enums.rpg.StatusType#VULNERABLE}
     */
    VULNERABLE(new String[]{
        ChatColor.GREEN + "Vulnerable",
        ChatColor.GRAY + "Increases the damage taken by the entity by 2.5% per stack."
    });

    /**
     * Description.
     */
    private final String[] description;

    /**
     * Associates a key word with its description.
     *
     * @param description description
     */
    KeyWord(String[] description) {
      this.description = description;
    }

    /**
     * Gets the key word's description.
     *
     * @return key word's description
     */
    private String[] getDescription() {
      return this.description;
    }
  }
}
