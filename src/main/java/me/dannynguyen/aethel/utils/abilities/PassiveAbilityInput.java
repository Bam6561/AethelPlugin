package me.dannynguyen.aethel.utils.abilities;

import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.enums.rpg.AethelAttribute;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType;
import me.dannynguyen.aethel.utils.TextFormatter;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Validates inputs for {@link me.dannynguyen.aethel.enums.plugin.Key#PASSIVE_LIST passive ability} tags.
 *
 * @author Danny Nguyen
 * @version 1.20.11
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
   * {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType.Effect#BUFF}.
   *
   * @param user interacting user
   * @param args user provided parameters
   * @return ability data if set correctly, otherwise null
   */
  public static String chanceCooldownBuff(@NotNull Player user, @NotNull String[] args) {
    Objects.requireNonNull(user, "Null user");
    if (Objects.requireNonNull(args, "Null arguments").length != 6) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return null;
    }
    double chance;
    try {
      chance = Double.parseDouble(args[0]);
      if (chance < 0 || 100 < chance) {
        user.sendMessage(Message.INVALID_CHANCE.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_CHANCE.getMessage());
      return null;
    }
    int cooldown;
    try {
      cooldown = Integer.parseInt(args[1]);
      if (cooldown < 0) {
        user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
        return null;
      }
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
    String attribute = args[3];
    try {
      Attribute.valueOf(args[3].toUpperCase());
    } catch (IllegalArgumentException ex1) {
      try {
        AethelAttribute.valueOf(args[3].toUpperCase());
      } catch (IllegalArgumentException ex2) {
        user.sendMessage(Message.INVALID_ATTRIBUTE.getMessage());
        return null;
      }
    }
    double value;
    try {
      value = Double.parseDouble(args[4]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_VALUE.getMessage());
      return null;
    }
    int duration;
    try {
      duration = Integer.parseInt(args[5]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_DURATION.getMessage());
      return null;
    }
    return chance + " " + cooldown + " " + self + " " + attribute + " " + value + " " + duration;
  }

  /**
   * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType.Condition#HEALTH_COOLDOWN}
   * {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType.Effect#BUFF}.
   *
   * @param user interacting user
   * @param args user provided parameters
   * @return ability data if set correctly, otherwise null
   */
  public static String healthCooldownBuff(@NotNull Player user, @NotNull String[] args) {
    Objects.requireNonNull(user, "Null user");
    if (Objects.requireNonNull(args, "Null arguments").length != 6) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return null;
    }
    double percentHealth;
    try {
      percentHealth = Double.parseDouble(args[0]);
      if (percentHealth < 0) {
        user.sendMessage(Message.INVALID_HEALTH.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_HEALTH.getMessage());
      return null;
    }
    int cooldown;
    try {
      cooldown = Integer.parseInt(args[1]);
      if (cooldown < 0) {
        user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
      return null;
    }
    // Self
    if (!args[2].equals("true")) {
      user.sendMessage(Message.TRUE_ONLY.getMessage());
      return null;
    }
    String attribute = args[3];
    try {
      Attribute.valueOf(args[3].toUpperCase());
    } catch (IllegalArgumentException ex1) {
      try {
        AethelAttribute.valueOf(args[3].toUpperCase());
      } catch (IllegalArgumentException ex2) {
        user.sendMessage(Message.INVALID_ATTRIBUTE.getMessage());
        return null;
      }
    }
    double value;
    try {
      value = Double.parseDouble(args[4]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_VALUE.getMessage());
      return null;
    }
    int duration;
    try {
      duration = Integer.parseInt(args[5]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_DURATION.getMessage());
      return null;
    }
    return percentHealth + " " + cooldown + " " + true + " " + attribute + " " + value + " " + duration;
  }

  /**
   * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType.Condition#CHANCE_COOLDOWN}
   * {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType.Effect#CHAIN_DAMAGE}.
   *
   * @param user interacting user
   * @param args user provided parameters
   * @return ability data if set correctly, otherwise null
   */
  public static String chanceCooldownChainDamage(@NotNull Player user, @NotNull String[] args) {
    Objects.requireNonNull(user, "Null user");
    if (Objects.requireNonNull(args, "Null arguments").length != 5) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return null;
    }
    double chance;
    try {
      chance = Double.parseDouble(args[0]);
      if (chance < 0 || 100 < chance) {
        user.sendMessage(Message.INVALID_CHANCE.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_CHANCE.getMessage());
      return null;
    }
    int cooldown;
    try {
      cooldown = Integer.parseInt(args[1]);
      if (cooldown < 0) {
        user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
        return null;
      }
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
      if (radius < 0 || 64 < radius) {
        user.sendMessage(Message.INVALID_RADIUS.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_RADIUS.getMessage());
      return null;
    }
    return chance + " " + cooldown + " " + self + " " + damage + " " + radius;
  }

  /**
   * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType.Condition#HEALTH_COOLDOWN}
   * {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType.Effect#CHAIN_DAMAGE}.
   *
   * @param user interacting user
   * @param args user provided parameters
   * @return ability data if set correctly, otherwise null
   */
  public static String healthCooldownChainDamage(@NotNull Player user, @NotNull String[] args) {
    Objects.requireNonNull(user, "Null user");
    if (Objects.requireNonNull(args, "Null arguments").length != 5) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return null;
    }
    double percentHealth;
    try {
      percentHealth = Double.parseDouble(args[0]);
      if (percentHealth < 0) {
        user.sendMessage(Message.INVALID_HEALTH.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_HEALTH.getMessage());
      return null;
    }
    int cooldown;
    try {
      cooldown = Integer.parseInt(args[1]);
      if (cooldown < 0) {
        user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
      return null;
    }
    // Self
    if (!args[2].equals("true")) {
      user.sendMessage(Message.TRUE_ONLY.getMessage());
      return null;
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
      if (radius < 0 || 64 < radius) {
        user.sendMessage(Message.INVALID_RADIUS.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_RADIUS.getMessage());
      return null;
    }
    return percentHealth + " " + cooldown + " " + true + " " + damage + " " + radius;
  }

  /**
   * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType.Condition#CHANCE_COOLDOWN}
   * {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType.Effect#STACK_INSTANCE}.
   *
   * @param user    interacting user
   * @param args    user provided parameters
   * @param trigger {@link PassiveTriggerType}
   * @return ability data if set correctly, otherwise null
   */
  public static String chanceCooldownStackInstance(@NotNull Player user, @NotNull String[] args, PassiveTriggerType trigger) {
    Objects.requireNonNull(user, "Null user");
    if (Objects.requireNonNull(args, "Null arguments").length != 5) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return null;
    }
    double chance;
    try {
      chance = Double.parseDouble(args[0]);
      if (chance < 0 || 100 < chance) {
        user.sendMessage(Message.INVALID_CHANCE.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_CHANCE.getMessage());
      return null;
    }
    int cooldown;
    try {
      cooldown = Integer.parseInt(args[1]);
      if (cooldown < 0) {
        user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
      return null;
    }
    boolean self;
    if (trigger == PassiveTriggerType.ON_KILL) {
      if (args[2].equals("true")) {
        self = true;
      } else {
        user.sendMessage(Message.TRUE_ONLY.getMessage());
        return null;
      }
    } else {
      switch (args[2]) {
        case "true", "false" -> self = Boolean.parseBoolean(args[2]);
        default -> {
          user.sendMessage(Message.INVALID_BOOLEAN.getMessage());
          return null;
        }
      }
    }
    int stacks;
    try {
      stacks = Integer.parseInt(args[3]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_STACKS.getMessage());
      return null;
    }
    int duration;
    try {
      duration = Integer.parseInt(args[4]);
      if (duration < 0) {
        user.sendMessage(Message.INVALID_DURATION.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_DURATION.getMessage());
      return null;
    }
    return chance + " " + cooldown + " " + self + " " + stacks + " " + duration;
  }

  /**
   * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType.Condition#HEALTH_COOLDOWN}
   * {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType.Effect#STACK_INSTANCE}.
   *
   * @param user interacting user
   * @param args user provided parameters
   * @return ability data if set correctly, otherwise null
   */
  public static String healthCooldownStackInstance(@NotNull Player user, @NotNull String[] args) {
    Objects.requireNonNull(user, "Null user");
    if (Objects.requireNonNull(args, "Null arguments").length != 5) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return null;
    }
    double percentHealth;
    try {
      percentHealth = Double.parseDouble(args[0]);
      if (percentHealth < 0) {
        user.sendMessage(Message.INVALID_HEALTH.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_HEALTH.getMessage());
      return null;
    }
    int cooldown;
    try {
      cooldown = Integer.parseInt(args[1]);
      if (cooldown < 0) {
        user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
      return null;
    }
    // Self
    if (!args[2].equals("true")) {
      user.sendMessage(Message.TRUE_ONLY.getMessage());
      return null;
    }
    int stacks;
    try {
      stacks = Integer.parseInt(args[3]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_STACKS.getMessage());
      return null;
    }
    int duration;
    try {
      duration = Integer.parseInt(args[4]);
      if (duration < 0) {
        user.sendMessage(Message.INVALID_DURATION.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_DURATION.getMessage());
      return null;
    }
    return percentHealth + " " + cooldown + " " + true + " " + stacks + " " + duration;
  }

  /**
   * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType.Condition#CHANCE_COOLDOWN}
   * {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType.Effect#POTION_EFFECT}.
   *
   * @param user    interacting user
   * @param args    user provided parameters
   * @param trigger {@link PassiveTriggerType}
   * @return ability data if set correctly, otherwise null
   */
  public static String chanceCooldownPotionEffect(@NotNull Player user, @NotNull String[] args, PassiveTriggerType trigger) {
    Objects.requireNonNull(user, "Null user");
    if (Objects.requireNonNull(args, "Null arguments").length != 7) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return null;
    }
    double chance;
    try {
      chance = Double.parseDouble(args[0]);
      if (chance < 0 || 100 < chance) {
        user.sendMessage(Message.INVALID_CHANCE.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_CHANCE.getMessage());
      return null;
    }
    int cooldown;
    try {
      cooldown = Integer.parseInt(args[1]);
      if (cooldown < 0) {
        user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
      return null;
    }
    boolean self;
    if (trigger == PassiveTriggerType.ON_KILL) {
      if (args[2].equals("true")) {
        self = true;
      } else {
        user.sendMessage(Message.TRUE_ONLY.getMessage());
        return null;
      }
    } else {
      switch (args[2]) {
        case "true", "false" -> self = Boolean.parseBoolean(args[2]);
        default -> {
          user.sendMessage(Message.INVALID_BOOLEAN.getMessage());
          return null;
        }
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
      if (amplifier < 0 || 255 < amplifier) {
        user.sendMessage(Message.INVALID_AMPLIFIER.getMessage());
        return null;
      }
    } catch (NullPointerException ex) {
      user.sendMessage(Message.INVALID_AMPLIFIER.getMessage());
      return null;
    }
    int duration;
    try {
      duration = Integer.parseInt(args[5]);
      if (duration < 0) {
        user.sendMessage(Message.INVALID_DURATION.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_DURATION.getMessage());
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
    return chance + " " + cooldown + " " + self + " " + TextFormatter.formatId(potionEffectType.getName()) + " " + amplifier + " " + duration + " " + ambient;
  }

  /**
   * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType.Condition#HEALTH_COOLDOWN}
   * {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType.Effect#POTION_EFFECT}.
   *
   * @param user interacting user
   * @param args user provided parameters
   * @return ability data if set correctly, otherwise null
   */
  public static String healthCooldownPotionEffect(@NotNull Player user, @NotNull String[] args) {
    Objects.requireNonNull(user, "Null user");
    if (Objects.requireNonNull(args, "Null arguments").length != 7) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return null;
    }
    double percentHealth;
    try {
      percentHealth = Double.parseDouble(args[0]);
      if (percentHealth < 0) {
        user.sendMessage(Message.INVALID_HEALTH.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_HEALTH.getMessage());
      return null;
    }
    int cooldown;
    try {
      cooldown = Integer.parseInt(args[1]);
      if (cooldown < 0) {
        user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
      return null;
    }
    // Self
    if (!args[2].equals("true")) {
      user.sendMessage(Message.TRUE_ONLY.getMessage());
      return null;
    }
    PotionEffectType potionEffectType = PotionEffectType.getByName(args[3]);
    if (potionEffectType == null) {
      user.sendMessage(Message.INVALID_TYPE.getMessage());
      return null;
    }
    int amplifier;
    try {
      amplifier = Integer.parseInt(args[4]);
      if (amplifier < 0 || 255 < amplifier) {
        user.sendMessage(Message.INVALID_AMPLIFIER.getMessage());
        return null;
      }
    } catch (NullPointerException ex) {
      user.sendMessage(Message.INVALID_AMPLIFIER.getMessage());
      return null;
    }
    int duration;
    try {
      duration = Integer.parseInt(args[5]);
      if (duration < 0) {
        user.sendMessage(Message.INVALID_DURATION.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_DURATION.getMessage());
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
    return percentHealth + " " + cooldown + " " + true + " " + TextFormatter.formatId(potionEffectType.getName()) + " " + amplifier + " " + duration + " " + ambient;
  }
}
