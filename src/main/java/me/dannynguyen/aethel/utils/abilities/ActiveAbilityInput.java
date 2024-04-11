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
 * @version 1.21.10
 * @since 1.20.5
 */
public class ActiveAbilityInput {
  /**
   * Static methods only.
   */
  private ActiveAbilityInput() {
  }

  /**
   * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType.Effect#BUFF}.
   *
   * @param user interacting user
   * @param args user provided parameters
   * @return ability data if set correctly, otherwise null
   */
  @Nullable
  public static String buff(@NotNull Player user, @NotNull String[] args) {
    Objects.requireNonNull(user, "Null user");
    if (Objects.requireNonNull(args, "Null arguments").length != 4) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return null;
    }
    int cooldown;
    try {
      cooldown = Integer.parseInt(args[0]);
      if (cooldown < 0) {
        user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
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
   * @param user interacting user
   * @param args user provided parameters
   * @return ability data if set correctly, otherwise null
   */
  @Nullable
  public static String clearStatus(@NotNull Player user, @NotNull String[] args) {
    Objects.requireNonNull(user, "Null user");
    if (Objects.requireNonNull(args, "Null arguments").length != 1) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return null;
    }
    int cooldown;
    try {
      cooldown = Integer.parseInt(args[0]);
      if (cooldown < 0) {
        user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
      return null;
    }
    return String.valueOf(cooldown);
  }

  /**
   * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType.Effect#DISTANCE_DAMAGE}.
   *
   * @param user interacting user
   * @param args user provided parameters
   * @return ability data if set correctly, otherwise null
   */
  @Nullable
  public static String distanceDamage(@NotNull Player user, @NotNull String[] args) {
    Objects.requireNonNull(user, "Null user");
    if (Objects.requireNonNull(args, "Null arguments").length != 3) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return null;
    }
    int cooldown;
    try {
      cooldown = Integer.parseInt(args[0]);
      if (cooldown < 0) {
        user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
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
   * @param user interacting user
   * @param args user provided parameters
   * @return ability data if set correctly, otherwise null
   */
  @Nullable
  public static String movement(@NotNull Player user, @NotNull String[] args) {
    Objects.requireNonNull(user, "Null user");
    if (Objects.requireNonNull(args, "Null arguments").length != 2) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return null;
    }
    int cooldown;
    try {
      cooldown = Integer.parseInt(args[0]);
      if (cooldown < 0) {
        user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
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
   * @param user interacting user
   * @param args user provided parameters
   * @return ability data if set correctly, otherwise null
   */
  @Nullable
  public static String potionEffect(@NotNull Player user, @NotNull String[] args) {
    Objects.requireNonNull(user, "Null user");
    if (Objects.requireNonNull(args, "Null arguments").length != 5) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return null;
    }
    int cooldown;
    try {
      cooldown = Integer.parseInt(args[0]);
      if (cooldown < 0) {
        user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
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
    boolean ambient;
    switch (args[4]) {
      case "true", "false" -> ambient = Boolean.parseBoolean(args[4]);
      default -> {
        user.sendMessage(Message.INVALID_BOOLEAN.getMessage());
        return null;
      }
    }
    return cooldown + " " + TextFormatter.formatId(potionEffectType.getName()) + " " + amplifier + " " + duration + " " + ambient;
  }

  /**
   * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType.Effect#PROJECTION}.
   *
   * @param user interacting user
   * @param args user provided parameters
   * @return ability data if set correctly, otherwise null
   */
  @Nullable
  public static String projection(@NotNull Player user, @NotNull String[] args) {
    Objects.requireNonNull(user, "Null user");
    if (Objects.requireNonNull(args, "Null arguments").length != 3) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return null;
    }
    int cooldown;
    try {
      cooldown = Integer.parseInt(args[0]);
      if (cooldown < 0) {
        user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
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
   * @param user interacting user
   * @param args user provided parameters
   * @return ability data if set correctly, otherwise null
   */
  @Nullable
  public static String shatter(@NotNull Player user, @NotNull String[] args) {
    Objects.requireNonNull(user, "Null user");
    if (Objects.requireNonNull(args, "Null arguments").length != 2) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return null;
    }
    int cooldown;
    try {
      cooldown = Integer.parseInt(args[0]);
      if (cooldown < 0) {
        user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
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
   * @param user interacting user
   * @param args user provided parameters
   * @return ability data if set correctly, otherwise null
   */
  @Nullable
  public static String teleport(@NotNull Player user, @NotNull String[] args) {
    Objects.requireNonNull(user, "Null user");
    if (Objects.requireNonNull(args, "Null arguments").length != 2) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return null;
    }
    int cooldown;
    try {
      cooldown = Integer.parseInt(args[0]);
      if (cooldown < 0) {
        user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
        return null;
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
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
