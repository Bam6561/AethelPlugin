package me.dannynguyen.aethel.utils.abilities;

import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.enums.rpg.AethelAttribute;
import me.dannynguyen.aethel.utils.TextFormatter;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Validates inputs for {@link me.dannynguyen.aethel.enums.plugin.Key#ACTIVE_LIST active ability} tags.
 *
 * @author Danny Nguyen
 * @version 1.24.14
 * @since 1.20.5
 */
public class ActiveAbilityInput {
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
   * Associates the active ability input with its user and parameters.
   */
  public ActiveAbilityInput(@NotNull Player user, @NotNull String[] args) {
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
   * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType.Effect#BUFF}.
   *
   * @return ability data if set correctly, otherwise null
   */
  @Nullable
  public String buff() {
    if (invalidParameters(4)) {
      return null;
    }
    if (invalidCooldown(0)) {
      return null;
    }
    String attribute = args[1];
    try {
      Attribute.valueOf(args[1].toUpperCase());
    } catch (IllegalArgumentException ex1) {
      try {
        AethelAttribute.valueOf(args[1].toUpperCase());
      } catch (IllegalArgumentException ex2) {
        user.sendMessage(Message.INVALID_ATTRIBUTE.getMessage());
        return null;
      }
    }
    double value;
    try {
      value = Double.parseDouble(args[2]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_VALUE.getMessage());
      return null;
    }
    int duration;
    try {
      duration = Integer.parseInt(args[3]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_DURATION.getMessage());
      return null;
    }
    return cooldown + " " + attribute + " " + value + " " + duration;
  }

  /**
   * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType.Effect#CLEAR_STATUS}.
   *
   * @return ability data if set correctly, otherwise null
   */
  @Nullable
  public String clearStatus() {
    if (invalidParameters(1)) {
      return null;
    }
    if (invalidCooldown(0)) {
      return null;
    }
    return String.valueOf(cooldown);
  }

  /**
   * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType.Effect#DISPLACEMENT}.
   *
   * @return ability data if set correctly, otherwise null
   */
  @Nullable
  public String displacement() {
    if (invalidParameters(3)) {
      return null;
    }
    if (invalidCooldown(0)) {
      return null;
    }
    double modifier;
    try {
      modifier = Double.parseDouble(args[1]);
      if (modifier < 0 || 1000 < modifier) {
        user.sendMessage(Message.INVALID_MODIFIER.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_MODIFIER.getMessage());
      return null;
    }
    int distance;
    try {
      distance = Integer.parseInt(args[2]);
      if (distance < 0 || 64 < distance) {
        user.sendMessage(Message.INVALID_DISTANCE.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_DISTANCE.getMessage());
      return null;
    }
    return cooldown + " " + modifier + " " + distance;
  }

  /**
   * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType.Effect#DISTANCE_DAMAGE}.
   *
   * @return ability data if set correctly, otherwise null
   */
  @Nullable
  public String distanceDamage() {
    if (invalidParameters(3)) {
      return null;
    }
    if (invalidCooldown(0)) {
      return null;
    }
    double damage;
    try {
      damage = Double.parseDouble(args[1]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_DAMAGE.getMessage());
      return null;
    }
    int distance;
    try {
      distance = Integer.parseInt(args[2]);
      if (distance < 0 || 64 < distance) {
        user.sendMessage(Message.INVALID_DISTANCE.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_DISTANCE.getMessage());
      return null;
    }
    return cooldown + " " + damage + " " + distance;
  }

  /**
   * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType.Effect#MOVEMENT}.
   *
   * @return ability data if set correctly, otherwise null
   */
  @Nullable
  public String movement() {
    if (invalidParameters(2)) {
      return null;
    }
    if (invalidCooldown(0)) {
      return null;
    }
    double modifier;
    try {
      modifier = Double.parseDouble(args[1]);
      if (modifier < 0 || 1000 < modifier) {
        user.sendMessage(Message.INVALID_MODIFIER.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_MODIFIER.getMessage());
      return null;
    }
    return cooldown + " " + modifier;
  }

  /**
   * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType.Effect#POTION_EFFECT}.
   *
   * @return ability data if set correctly, otherwise null
   */
  @Nullable
  public String potionEffect() {
    if (invalidParameters(5)) {
      return null;
    }
    if (invalidCooldown(0)) {
      return null;
    }
    PotionEffectType potionEffectType = PotionEffectType.getByName(args[1]);
    if (potionEffectType == null) {
      user.sendMessage(Message.INVALID_TYPE.getMessage());
      return null;
    }
    int amplifier;
    try {
      amplifier = Integer.parseInt(args[2]);
      if (amplifier < 0 || 255 < amplifier) {
        user.sendMessage(Message.INVALID_AMPLIFIER.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_AMPLIFIER.getMessage());
      return null;
    }
    int duration;
    try {
      duration = Integer.parseInt(args[3]);
      if (duration < 0) {
        user.sendMessage(Message.INVALID_DURATION.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_DURATION.getMessage());
      return null;
    }
    boolean particles;
    switch (args[4]) {
      case "true", "false" -> particles = Boolean.parseBoolean(args[4]);
      default -> {
        user.sendMessage(Message.INVALID_BOOLEAN.getMessage());
        return null;
      }
    }
    return cooldown + " " + TextFormatter.formatId(potionEffectType.getName()) + " " + amplifier + " " + duration + " " + particles;
  }

  /**
   * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType.Effect#PROJECTION}.
   *
   * @return ability data if set correctly, otherwise null
   */
  @Nullable
  public String projection() {
    if (invalidParameters(3)) {
      return null;
    }
    if (invalidCooldown(0)) {
      return null;
    }
    int distance;
    try {
      distance = Integer.parseInt(args[1]);
      if (distance < 0 || 64 < distance) {
        user.sendMessage(Message.INVALID_DISTANCE.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_DISTANCE.getMessage());
      return null;
    }
    int delay;
    try {
      delay = Integer.parseInt(args[2]);
      if (delay < 0) {
        user.sendMessage(Message.INVALID_DELAY.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_DELAY.getMessage());
      return null;
    }
    return cooldown + " " + distance + " " + delay;
  }

  /**
   * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType.Effect#SHATTER}.
   *
   * @return ability data if set correctly, otherwise null
   */
  @Nullable
  public String shatter() {
    if (invalidParameters(2)) {
      return null;
    }
    if (invalidCooldown(0)) {
      return null;
    }
    double radius;
    try {
      radius = Double.parseDouble(args[1]);
      if (radius < 0 || 64 < radius) {
        user.sendMessage(Message.INVALID_RADIUS.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_RADIUS.getMessage());
      return null;
    }
    return cooldown + " " + radius;
  }

  /**
   * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType.Effect#TELEPORT}.
   *
   * @return ability data if set correctly, otherwise null
   */
  @Nullable
  public String teleport() {
    if (invalidParameters(2)) {
      return null;
    }
    if (invalidCooldown(0)) {
      return null;
    }
    int distance;
    try {
      distance = Integer.parseInt(args[1]);
      if (distance < 0 || 64 < distance) {
        user.sendMessage(Message.INVALID_DISTANCE.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_DISTANCE.getMessage());
      return null;
    }
    return cooldown + " " + distance;
  }
}
