package me.dannynguyen.aethel.utils.abilities;

import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.utils.TextFormatter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * Validates inputs for {@link me.dannynguyen.aethel.enums.plugin.Key#ACTIVE_LIST active ability} tags.
 *
 * @author Danny Nguyen
 * @version 1.20.5
 * @since 1.20.5
 */
public class ActiveAbilityInput {
  /**
   * Static methods only.
   */
  private ActiveAbilityInput() {
  }

  /**
   * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType.Effect#CLEAR_STATUS}.
   *
   * @param user interacting user
   * @param args user provided parameters
   * @return ability data if set correctly, otherwise null
   */
  public static String clearStatus(Player user, String[] args) {
    if (args.length != 1) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return null;
    }
    int cooldown;
    try {
      cooldown = Integer.parseInt(args[0]);
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
  public static String distanceDamage(Player user, String[] args) {
    if (args.length != 3) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return null;
    }
    int cooldown;
    try {
      cooldown = Integer.parseInt(args[0]);
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
    double distance;
    try {
      distance = Double.parseDouble(args[2]);
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
  public static String movement(Player user, String[] args) {
    if (args.length != 2) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return null;
    }
    int cooldown;
    try {
      cooldown = Integer.parseInt(args[0]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
      return null;
    }
    double modifier;
    try {
      modifier = Double.parseDouble(args[1]);
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
  public static String potionEffect(Player user, String[] args) {
    if (args.length != 5) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return null;
    }
    int cooldown;
    try {
      cooldown = Integer.parseInt(args[0]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
      return null;
    }
    PotionEffectType potionEffectType = PotionEffectType.getByName(args[1]);
    if (potionEffectType == null) {
    } else {
      user.sendMessage(Message.INVALID_TYPE.getMessage());
      return null;
    }
    int amplifier;
    try {
      amplifier = Integer.parseInt(args[2]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_AMPLIFIER.getMessage());
      return null;
    }
    int ticks;
    try {
      ticks = Integer.parseInt(args[3]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_TICKS.getMessage());
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
    return cooldown + " " + TextFormatter.formatId(potionEffectType.getName()) + " " + amplifier + " " + ticks + " " + ambient;
  }

  /**
   * Sets {@link me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType.Effect#PROJECTION}.
   *
   * @param user interacting user
   * @param args user provided parameters
   * @return ability data if set correctly, otherwise null
   */
  public static String projection(Player user, String[] args) {
    if (args.length != 3) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return null;
    }
    int cooldown;
    try {
      cooldown = Integer.parseInt(args[0]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
      return null;
    }
    int distance;
    try {
      distance = Integer.parseInt(args[1]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_DISTANCE.getMessage());
      return null;
    }
    int delay;
    try {
      delay = Integer.parseInt(args[2]);
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
  public static String shatter(Player user, String[] args) {
    if (args.length != 2) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return null;
    }
    int cooldown;
    try {
      cooldown = Integer.parseInt(args[0]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
      return null;
    }
    double radius;
    try {
      radius = Double.parseDouble(args[1]);
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
  public static String teleport(Player user, String[] args) {
    if (args.length != 2) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return null;
    }
    int cooldown;
    try {
      cooldown = Integer.parseInt(args[0]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
      return null;
    }
    int distance;
    try {
      distance = Integer.parseInt(args[1]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_DISTANCE.getMessage());
      return null;
    }
    return cooldown + " " + distance;
  }
}
