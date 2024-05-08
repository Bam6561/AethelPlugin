package me.dannynguyen.aethel.utils.abilities;

import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.enums.rpg.AethelAttribute;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType;
import me.dannynguyen.aethel.utils.TextFormatter;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents {@link me.dannynguyen.aethel.rpg.abilities.PassiveAbility passive ability}
 * tags input validation.
 *
 * @author Danny Nguyen
 * @version 1.24.13
 * @since 1.20.5
 */
public class PassiveAbilityInput {
  /**
   * Interacting user.
   */
  private final Player user;

  /**
   * User provided parameters.
   */
  private final String[] args;

  /**
   * Ability cooldown.
   */
  private int cooldown;

  /**
   * Ability chance.
   */
  private double chance;

  /**
   * Ability health percent.
   */
  private double healthPercent;

  /**
   * Ability target.
   */
  private boolean self;

  /**
   * Associates the passive ability input with its user and parameters.
   *
   * @param user interacting user
   * @param args user provided parameters
   */
  public PassiveAbilityInput(@NotNull Player user, @NotNull String[] args) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.args = Objects.requireNonNull(args, "Null args");
  }

  /**
   * Returns if the number of parameters is invalid.
   *
   * @param validLength correct number of parameters
   * @return if the number of parameters is invalid
   */
  private boolean invalidParameters(int validLength) {
    if (args.length != validLength) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return true;
    }
    return false;
  }

  /**
   * Sets the ability cooldown and returns if the ability cooldown is invalid.
   *
   * @param i parameter index
   * @return if the ability cooldown is invalid
   */
  private boolean invalidCooldown(int i) {
    try {
      cooldown = Integer.parseInt(args[i]);
      if (cooldown < 0) {
        user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
        return true;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
      return true;
    }
    return false;
  }

  /**
   * Sets the ability chance and returns if the ability chance is invalid.
   *
   * @param i parameter index
   * @return if the ability chance is invalid
   */
  private boolean invalidChance(int i) {
    try {
      chance = Double.parseDouble(args[i]);
      if (chance < 0 || 100 < chance) {
        user.sendMessage(Message.INVALID_CHANCE.getMessage());
        return true;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_CHANCE.getMessage());
      return true;
    }
    return false;
  }

  /**
   * Sets the ability health percent and returns if the ability health percent is invalid.
   *
   * @param i parameter index
   * @return if the health percent is invalid
   */
  private boolean invalidHealthPercent(int i) {
    try {
      healthPercent = Double.parseDouble(args[i]);
      if (healthPercent < 0) {
        user.sendMessage(Message.INVALID_HEALTH.getMessage());
        return true;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_HEALTH.getMessage());
      return true;
    }
    return false;
  }

  /**
   * Returns if the target is not true.
   *
   * @param i parameter index
   * @return if the target is not true
   */
  private boolean invalidSelf(int i) {
    if (!args[i].equals("true")) {
      user.sendMessage(Message.TRUE_ONLY.getMessage());
      return true;
    }
    return false;
  }

  /**
   * Sets the target and returns if the target is not boolean.
   *
   * @param i parameter index
   * @return if the target is not true
   */
  private boolean invalidBoolean(int i) {
    switch (args[i]) {
      case "true", "false" -> self = Boolean.parseBoolean(args[i]);
      default -> {
        user.sendMessage(Message.INVALID_BOOLEAN.getMessage());
        return true;
      }
    }
    return false;
  }

  /**
   * Represents {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType.Effect#BUFF}
   * ability inputs.
   *
   * @author Danny Nguyen
   * @version 1.24.13
   * @since 1.24.9
   */
  public class Buff {
    /**
     * Buff attribute.
     */
    private String attribute;

    /**
     * Buff value.
     */
    private double value;

    /**
     * Buff duration.
     */
    private int duration;

    /**
     * No parameter constructor.
     */
    public Buff() {
    }

    /**
     * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType.Condition#COOLDOWN}.
     *
     * @return ability data if set correctly, otherwise null
     */
    @Nullable
    public String cooldown() {
      if (invalidParameters(5)) {
        return null;
      }
      if (invalidCooldown(0)) {
        return null;
      }
      if (invalidSelf(1)) {
        return null;
      }
      if (invalidAttribute(2)) {
        return null;
      }
      if (invalidValue(3)) {
        return null;
      }
      if (invalidDuration(4)) {
        return null;
      }
      return cooldown + " " + true + " " + attribute + " " + value + " " + duration;
    }

    /**
     * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType.Condition#CHANCE_COOLDOWN}.
     *
     * @return ability data if set correctly, otherwise null
     */
    @Nullable
    public String chanceCooldown() {
      if (invalidParameters(6)) {
        return null;
      }
      if (invalidChance(0)) {
        return null;
      }
      if (invalidCooldown(1)) {
        return null;
      }
      if (invalidBoolean(2)) {
        return null;
      }
      if (invalidAttribute(3)) {
        return null;
      }
      if (invalidValue(4)) {
        return null;
      }
      if (invalidDuration(5)) {
        return null;
      }
      return chance + " " + cooldown + " " + self + " " + attribute + " " + value + " " + duration;
    }

    /**
     * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType.Condition#HEALTH_COOLDOWN}.
     *
     * @return ability data if set correctly, otherwise null
     */
    @Nullable
    public String healthCooldown() {
      if (invalidParameters(6)) {
        return null;
      }
      if (invalidHealthPercent(0)) {
        return null;
      }
      if (invalidCooldown(1)) {
        return null;
      }
      if (invalidSelf(2)) {
        return null;
      }
      if (invalidAttribute(3)) {
        return null;
      }
      if (invalidValue(4)) {
        return null;
      }
      if (invalidDuration(5)) {
        return null;
      }
      return healthPercent + " " + cooldown + " " + true + " " + attribute + " " + value + " " + duration;
    }

    /**
     * Returns if the attribute is invalid.
     *
     * @param i parameter index
     * @return if the attribute is invalid
     */
    private boolean invalidAttribute(int i) {
      attribute = args[i];
      try {
        Attribute.valueOf(args[i].toUpperCase());
      } catch (IllegalArgumentException ex1) {
        try {
          AethelAttribute.valueOf(args[i].toUpperCase());
        } catch (IllegalArgumentException ex2) {
          user.sendMessage(Message.INVALID_ATTRIBUTE.getMessage());
          return true;
        }
      }
      return false;
    }

    /**
     * Returns if the buff value is invalid.
     *
     * @param i parameter index
     * @return if the buff value is invalid
     */
    private boolean invalidValue(int i) {
      try {
        value = Double.parseDouble(args[i]);
      } catch (NumberFormatException ex) {
        user.sendMessage(Message.INVALID_VALUE.getMessage());
        return true;
      }
      return false;
    }

    /**
     * Returns if the buff duration is invalid.
     *
     * @param i parameter index
     * @return if the buff duration is invalid
     */
    private boolean invalidDuration(int i) {
      try {
        duration = Integer.parseInt(args[i]);
      } catch (NumberFormatException ex) {
        user.sendMessage(Message.INVALID_DURATION.getMessage());
        return true;
      }
      return false;
    }
  }

  /**
   * Represents {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType.Effect#CHAIN_DAMAGE}
   * ability input.
   *
   * @author Danny Nguyen
   * @version 1.24.13
   * @since 1.24.9
   */
  public class ChainDamage {
    /**
     * Chain damage.
     */
    private double damage;

    /**
     * Chain radius.
     */
    private double radius;

    /**
     * No parameter constructor.
     */
    public ChainDamage() {
    }

    /**
     * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType.Condition#COOLDOWN}.
     *
     * @return ability data if set correctly, otherwise null
     */
    @Nullable
    public String cooldown() {
      if (invalidParameters(4)) {
        return null;
      }
      if (invalidCooldown(0)) {
        return null;
      }
      if (invalidSelf(1)) {
        return null;
      }
      if (invalidDamage(2)) {
        return null;
      }
      if (invalidRadius(3)) {
        return null;
      }
      return cooldown + " " + true + " " + damage + " " + radius;
    }

    /**
     * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType.Condition#CHANCE_COOLDOWN}.
     *
     * @return ability data if set correctly, otherwise null
     */
    @Nullable
    public String chanceCooldown() {
      if (invalidParameters(5)) {
        return null;
      }
      if (invalidChance(0)) {
        return null;
      }
      if (invalidCooldown(1)) {
        return null;
      }
      if (invalidBoolean(2)) {
        return null;
      }
      if (invalidDamage(3)) {
        return null;
      }
      if (invalidRadius(4)) {
        return null;
      }
      return chance + " " + cooldown + " " + self + " " + damage + " " + radius;
    }

    /**
     * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType.Condition#HEALTH_COOLDOWN}.
     *
     * @return ability data if set correctly, otherwise null
     */
    @Nullable
    public String healthCooldown() {
      if (invalidParameters(5)) {
        return null;
      }
      if (invalidHealthPercent(0)) {
        return null;
      }
      if (invalidCooldown(1)) {
        return null;
      }
      if (invalidSelf(2)) {
        return null;
      }
      if (invalidDamage(3)) {
        return null;
      }
      if (invalidRadius(4)) {
        return null;
      }
      return healthPercent + " " + cooldown + " " + true + " " + damage + " " + radius;
    }

    /**
     * Returns if the damage is invalid.
     *
     * @param i parameter index
     * @return if the damage is invalid
     */
    private boolean invalidDamage(int i) {
      try {
        damage = Integer.parseInt(args[i]);
      } catch (NumberFormatException ex) {
        user.sendMessage(Message.INVALID_DAMAGE.getMessage());
        return true;
      }
      return false;
    }

    /**
     * Returns if the radius invalid.
     *
     * @param i parameter index
     * @return if the radius is invalid
     */
    private boolean invalidRadius(int i) {
      try {
        radius = Double.parseDouble(args[i]);
        if (radius < 0 || 64 < radius) {
          user.sendMessage(Message.INVALID_RADIUS.getMessage());
          return true;
        }
      } catch (NumberFormatException ex) {
        user.sendMessage(Message.INVALID_RADIUS.getMessage());
        return true;
      }
      return false;
    }
  }

  /**
   * Represents {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType.Effect#POTION_EFFECT}
   * ability input.
   *
   * @author Danny Nguyen
   * @version 1.24.13
   * @since 1.24.9
   */
  public class PotionEffect {
    /**
     * Potion effect type.
     */
    private PotionEffectType potionEffectType;

    /**
     * Potion effect amplifier.
     */
    private int amplifier;

    /**
     * Potion effect duration.
     */
    private int duration;

    /**
     * Potion effect particle visibility.
     */
    private boolean particles;

    /**
     * No parameter constructor.
     */
    public PotionEffect() {
    }

    /**
     * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType.Condition#COOLDOWN}
     *
     * @return ability data if set correctly, otherwise null
     */
    @Nullable
    public String cooldown() {
      if (invalidParameters(6)) {
        return null;
      }
      if (invalidCooldown(0)) {
        return null;
      }
      if (invalidSelf(1)) {
        return null;
      }
      if (invalidPotionEffectType(2)) {
        return null;
      }
      if (invalidAmplifier(3)) {
        return null;
      }
      if (invalidDuration(4)) {
        return null;
      }
      if (invalidParticles(5)) {
        return null;
      }
      return cooldown + " " + true + " " + TextFormatter.formatId(potionEffectType.getName()) + " " + amplifier + " " + duration + " " + particles;
    }

    /**
     * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType.Condition#CHANCE_COOLDOWN}.
     *
     * @param trigger {@link PassiveTriggerType}
     * @return ability data if set correctly, otherwise null
     */
    @Nullable
    public String chanceCooldown(@NotNull PassiveTriggerType trigger) {
      Objects.requireNonNull(trigger, "Null trigger");
      if (invalidParameters(7)) {
        return null;
      }
      if (invalidChance(0)) {
        return null;
      }
      if (invalidCooldown(1)) {
        return null;
      }
      if (trigger == PassiveTriggerType.ON_KILL) {
        if (invalidSelf(2)) {
          return null;
        } else {
          self = true;
        }
      } else {
        if (invalidBoolean(2)) {
          return null;
        }
      }
      if (invalidPotionEffectType(3)) {
        return null;
      }
      if (invalidAmplifier(4)) {
        return null;
      }
      if (invalidDuration(5)) {
        return null;
      }
      if (invalidParticles(6)) {
        return null;
      }
      return chance + " " + cooldown + " " + self + " " + TextFormatter.formatId(potionEffectType.getName()) + " " + amplifier + " " + duration + " " + particles;
    }

    /**
     * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType.Condition#HEALTH_COOLDOWN}.
     *
     * @return ability data if set correctly, otherwise null
     */
    @Nullable
    public String healthCooldown() {
      if (invalidParameters(7)) {
        return null;
      }
      if (invalidHealthPercent(0)) {
        return null;
      }
      if (invalidCooldown(1)) {
        return null;
      }
      if (invalidSelf(2)) {
        return null;
      }
      if (invalidPotionEffectType(3)) {
        return null;
      }
      if (invalidAmplifier(4)) {
        return null;
      }
      if (invalidDuration(5)) {
        return null;
      }
      if (invalidParticles(6)) {
        return null;
      }
      return healthPercent + " " + cooldown + " " + true + " " + TextFormatter.formatId(potionEffectType.getName()) + " " + amplifier + " " + duration + " " + particles;
    }

    /**
     * Returns if invalid potion effect type.
     *
     * @param i parameter index
     * @return if invalid potion effect type
     */
    private boolean invalidPotionEffectType(int i) {
      potionEffectType = PotionEffectType.getByName(args[i]);
      if (potionEffectType == null) {
        user.sendMessage(Message.INVALID_TYPE.getMessage());
        return true;
      }
      return false;
    }

    /**
     * Returns if invalid potion effect amplifier.
     *
     * @param i parameter index
     * @return if invalid potion effect amplifier
     */
    private boolean invalidAmplifier(int i) {
      try {
        amplifier = Integer.parseInt(args[i]);
        if (amplifier < 0 || 255 < amplifier) {
          user.sendMessage(Message.INVALID_AMPLIFIER.getMessage());
          return true;
        }
      } catch (NumberFormatException ex) {
        user.sendMessage(Message.INVALID_AMPLIFIER.getMessage());
        return true;
      }
      return false;
    }

    /**
     * Returns if invalid potion effect duration.
     *
     * @param i parameter index
     * @return if invalid potion effect duration
     */
    private boolean invalidDuration(int i) {
      try {
        duration = Integer.parseInt(args[i]);
        if (duration < 0) {
          user.sendMessage(Message.INVALID_DURATION.getMessage());
          return true;
        }
      } catch (NumberFormatException ex) {
        user.sendMessage(Message.INVALID_DURATION.getMessage());
        return true;
      }
      return false;
    }

    /**
     * Returns if invalid potion effect particle visibility.
     *
     * @param i parameter index
     * @return if invalid potion effect particle visibility
     */
    private boolean invalidParticles(int i) {
      switch (args[i]) {
        case "true", "false" -> particles = Boolean.parseBoolean(args[i]);
        default -> {
          user.sendMessage(Message.INVALID_BOOLEAN.getMessage());
          return true;
        }
      }
      return false;
    }
  }

  /**
   * Represents {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType.Effect#STACK_INSTANCE}
   * ability input.
   *
   * @author Danny Nguyen
   * @version 1.24.13
   * @since 1.24.9
   */
  public class StackInstance {
    /**
     * Amount of stacks.
     */
    private int stacks;

    /**
     * Stack instance duration.
     */
    private int duration;

    /**
     * No parameter constructor.
     */
    public StackInstance() {
    }

    /**
     * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType.Condition#COOLDOWN}.
     *
     * @return ability data if set correctly, otherwise null
     */
    @Nullable
    public String cooldown() {
      if (invalidParameters(4)) {
        return null;
      }
      if (invalidCooldown(0)) {
        return null;
      }
      if (invalidSelf(1)) {
        return null;
      }
      if (invalidStacks(2)) {
        return null;
      }
      if (invalidDuration(3)) {
        return null;
      }
      return cooldown + " " + true + " " + stacks + " " + duration;
    }

    /**
     * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType.Condition#CHANCE_COOLDOWN}.
     *
     * @param trigger {@link PassiveTriggerType}
     * @return ability data if set correctly, otherwise null
     */
    @Nullable
    public String chanceCooldown(@NotNull PassiveTriggerType trigger) {
      Objects.requireNonNull(trigger, "Null trigger");
      if (invalidParameters(5)) {
        return null;
      }
      if (invalidChance(0)) {
        return null;
      }
      if (invalidCooldown(1)) {
        return null;
      }
      if (trigger == PassiveTriggerType.ON_KILL) {
        if (invalidSelf(2)) {
          return null;
        } else {
          self = true;
        }
      } else {
        if (invalidBoolean(2)) {
          return null;
        }
      }
      if (invalidStacks(3)) {
        return null;
      }
      if (invalidDuration(4)) {
        return null;
      }
      return chance + " " + cooldown + " " + self + " " + stacks + " " + duration;
    }

    /**
     * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType.Condition#HEALTH_COOLDOWN}.
     *
     * @return ability data if set correctly, otherwise null
     */
    @Nullable
    public String healthCooldown() {
      if (invalidParameters(5)) {
        return null;
      }
      if (invalidHealthPercent(0)) {
        return null;
      }
      if (invalidCooldown(1)) {
        return null;
      }
      if (invalidSelf(2)) {
        return null;
      }
      if (invalidStacks(3)) {
        return null;
      }
      if (invalidDuration(4)) {
        return null;
      }
      return healthPercent + " " + cooldown + " " + true + " " + stacks + " " + duration;
    }

    /**
     * Returns if the number of stacks is invalid.
     *
     * @param i parameter index
     * @return if the number of stacks is invalid
     */
    private boolean invalidStacks(int i) {
      try {
        stacks = Integer.parseInt(args[i]);
      } catch (NumberFormatException ex) {
        user.sendMessage(Message.INVALID_STACKS.getMessage());
        return true;
      }
      return false;
    }

    /**
     * Returns if the stack instance duration is invalid.
     *
     * @param i parameter index
     * @return if the stack instance duration is invalid
     */
    private boolean invalidDuration(int i) {
      try {
        duration = Integer.parseInt(args[i]);
        if (duration < 0) {
          user.sendMessage(Message.INVALID_DURATION.getMessage());
          return true;
        }
      } catch (NumberFormatException ex) {
        user.sendMessage(Message.INVALID_DURATION.getMessage());
        return true;
      }
      return false;
    }
  }
}
