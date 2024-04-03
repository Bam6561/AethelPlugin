package me.dannynguyen.aethel.utils.abilities;

import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.utils.TextFormatter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * Validates inputs for {@link me.dannynguyen.aethel.enums.plugin.Key#PASSIVE_LIST passive ability} tags.
 *
 * @author Danny Nguyen
 * @version 1.20.5
 * @since 1.20.5
 */
public class PassiveAbilityInput {
  /**
   * Static methods only.
   */
  private PassiveAbilityInput() {
  }

  /**
   * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType.Condition#CHANCE_COOLDOWN}
   * {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType.Effect#CHAIN_DAMAGE}.
   *
   * @param user interacting user
   * @param args user provided parameters
   * @return ability data if set correctly, otherwise null
   */
  public static String chanceCooldownChainDamage(Player user, String[] args) {
    if (args.length != 5) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return null;
    }
    double chance;
    try {
      chance = Double.parseDouble(args[0]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_CHANCE.getMessage());
      return null;
    }
    int cooldown;
    try {
      cooldown = Integer.parseInt(args[1]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
      return null;
    }
    boolean self;
    switch (args[2]) {
      case "true", "false" -> self = Boolean.parseBoolean(args[2]);
      default -> {
        user.sendMessage(Message.INVALID_BOOLEAN.getMessage());
        return null;
      }
    }
    double damage;
    try {
      damage = Integer.parseInt(args[3]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_DAMAGE.getMessage());
      return null;
    }
    double distance;
    try {
      distance = Double.parseDouble(args[4]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_RADIUS.getMessage());
      return null;
    }
    return chance + " " + cooldown + " " + self + " " + damage + " " + distance;
  }

  /**
   * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType.Condition#HEALTH_COOLDOWN}
   * {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType.Effect#CHAIN_DAMAGE}.
   *
   * @param user interacting user
   * @param args user provided parameters
   * @return ability data if set correctly, otherwise null
   */
  public static String healthCooldownChainDamage(Player user, String[] args) {
    if (args.length != 5) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return null;
    }
    double percentHealth;
    try {
      percentHealth = Double.parseDouble(args[0]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_HEALTH.getMessage());
      return null;
    }
    int cooldown;
    try {
      cooldown = Integer.parseInt(args[1]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
      return null;
    }
    boolean self;
    switch (args[2]) {
      case "true", "false" -> self = Boolean.parseBoolean(args[2]);
      default -> {
        user.sendMessage(Message.INVALID_BOOLEAN.getMessage());
        return null;
      }
    }
    double damage;
    try {
      damage = Integer.parseInt(args[3]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_DAMAGE.getMessage());
      return null;
    }
    double radius;
    try {
      radius = Double.parseDouble(args[4]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_RADIUS.getMessage());
      return null;
    }
    return percentHealth + " " + cooldown + " " + self + " " + damage + " " + radius;
  }

  /**
   * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType.Condition#CHANCE_COOLDOWN}
   * {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType.Effect#STACK_INSTANCE}.
   *
   * @param user interacting user
   * @param args user provided parameters
   * @return ability data if set correctly, otherwise null
   */
  public static String chanceCooldownStackInstance(Player user, String[] args) {
    if (args.length != 5) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return null;
    }
    double chance;
    try {
      chance = Double.parseDouble(args[0]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_CHANCE.getMessage());
      return null;
    }
    int cooldown;
    try {
      cooldown = Integer.parseInt(args[1]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
      return null;
    }
    boolean self;
    switch (args[2]) {
      case "true", "false" -> self = Boolean.parseBoolean(args[2]);
      default -> {
        user.sendMessage(Message.INVALID_BOOLEAN.getMessage());
        return null;
      }
    }
    int stacks;
    try {
      stacks = Integer.parseInt(args[3]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_STACKS.getMessage());
      return null;
    }
    int ticks;
    try {
      ticks = Integer.parseInt(args[4]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_TICKS.getMessage());
      return null;
    }
    return chance + " " + cooldown + " " + self + " " + stacks + " " + ticks;
  }

  /**
   * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType.Condition#HEALTH_COOLDOWN}
   * {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType.Effect#STACK_INSTANCE}.
   *
   * @param user interacting user
   * @param args user provided parameters
   * @return ability data if set correctly, otherwise null
   */
  public static String healthCooldownStackInstance(Player user, String[] args) {
    if (args.length != 5) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return null;
    }
    double percentHealth;
    try {
      percentHealth = Double.parseDouble(args[0]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_HEALTH.getMessage());
      return null;
    }
    int cooldown;
    try {
      cooldown = Integer.parseInt(args[1]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
      return null;
    }
    boolean self;
    switch (args[2]) {
      case "true", "false" -> self = Boolean.parseBoolean(args[2]);
      default -> {
        user.sendMessage(Message.INVALID_BOOLEAN.getMessage());
        return null;
      }
    }
    int stacks;
    try {
      stacks = Integer.parseInt(args[3]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_STACKS.getMessage());
      return null;
    }
    int ticks;
    try {
      ticks = Integer.parseInt(args[4]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_TICKS.getMessage());
      return null;
    }
    return percentHealth + " " + cooldown + " " + self + " " + stacks + " " + ticks;
  }

  /**
   * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType.Condition#CHANCE_COOLDOWN}
   * {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType.Effect#POTION_EFFECT}.
   *
   * @param user interacting user
   * @param args user provided parameters
   * @return ability data if set correctly, otherwise null
   */
  public static String chanceCooldownPotionEffect(Player user, String[] args) {
    if (args.length != 7) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return null;
    }
    double chance;
    try {
      chance = Double.parseDouble(args[0]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_CHANCE.getMessage());
      return null;
    }
    int cooldown;
    try {
      cooldown = Integer.parseInt(args[1]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
      return null;
    }
    boolean self;
    switch (args[2]) {
      case "true", "false" -> self = Boolean.parseBoolean(args[2]);
      default -> {
        user.sendMessage(Message.INVALID_BOOLEAN.getMessage());
        return null;
      }
    }
    PotionEffectType potionEffectType = PotionEffectType.getByName(args[3]);
    if (potionEffectType == null) {
      user.sendMessage(Message.INVALID_TYPE.getMessage());
      return null;
    }
    int amplifier;
    try {
      amplifier = Integer.parseInt(args[4]);
    } catch (NullPointerException ex) {
      user.sendMessage(Message.INVALID_AMPLIFIER.getMessage());
      return null;
    }
    int ticks;
    try {
      ticks = Integer.parseInt(args[5]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_TICKS.getMessage());
      return null;
    }
    boolean ambient;
    switch (args[6]) {
      case "true", "false" -> ambient = Boolean.parseBoolean(args[6]);
      default -> {
        user.sendMessage(Message.INVALID_BOOLEAN.getMessage());
        return null;
      }
    }
    return chance + " " + cooldown + " " + self + " " + TextFormatter.formatId(potionEffectType.getName()) + " " + amplifier + " " + ticks + " " + ambient;
  }

  /**
   * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType.Condition#HEALTH_COOLDOWN}
   * {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType.Effect#POTION_EFFECT}.
   *
   * @param user interacting user
   * @param args user provided parameters
   * @return ability data if set correctly, otherwise null
   */
  public static String healthCooldownPotionEffect(Player user, String[] args) {
    if (args.length != 7) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return null;
    }
    double percentHealth;
    try {
      percentHealth = Double.parseDouble(args[0]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_HEALTH.getMessage());
      return null;
    }
    int cooldown;
    try {
      cooldown = Integer.parseInt(args[1]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
      return null;
    }
    boolean self;
    switch (args[2]) {
      case "true", "false" -> self = Boolean.parseBoolean(args[2]);
      default -> {
        user.sendMessage(Message.INVALID_BOOLEAN.getMessage());
        return null;
      }
    }
    PotionEffectType potionEffectType = PotionEffectType.getByName(args[3]);
    if (potionEffectType == null) {
      user.sendMessage(Message.INVALID_TYPE.getMessage());
      return null;
    }
    int amplifier;
    try {
      amplifier = Integer.parseInt(args[4]);
    } catch (NullPointerException ex) {
      user.sendMessage(Message.INVALID_AMPLIFIER.getMessage());
      return null;
    }
    int ticks;
    try {
      ticks = Integer.parseInt(args[5]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_TICKS.getMessage());
      return null;
    }
    boolean ambient;
    switch (args[6]) {
      case "true", "false" -> ambient = Boolean.parseBoolean(args[6]);
      default -> {
        user.sendMessage(Message.INVALID_BOOLEAN.getMessage());
        return null;
      }
    }
    return percentHealth + " " + cooldown + " " + self + " " + TextFormatter.formatId(potionEffectType.getName()) + " " + amplifier + " " + ticks + " " + ambient;
  }
}
