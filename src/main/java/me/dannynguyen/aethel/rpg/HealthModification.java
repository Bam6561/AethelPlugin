package me.dannynguyen.aethel.rpg;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.rpg.AethelAttribute;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents entity health modification.
 *
 * @author Danny Nguyen
 * @version 1.22.20
 * @since 1.22.20
 */
public class HealthModification {
  /**
   * Defending entity.
   */
  private final LivingEntity defender;

  /**
   * Entity's UUID.
   */
  private final UUID uuid;

  /**
   * Entity's persistent tags.
   */
  private final PersistentDataContainer entityTags;

  /**
   * Current health.
   */
  private double currentHealth;

  /**
   * Max health.
   */
  private final double maxHealth;

  /**
   * Associates the health modification with an entity.
   *
   * @param defender defending entity
   */
  public HealthModification(@NotNull LivingEntity defender) {
    this.defender = Objects.requireNonNull(defender, "Null UUID");
    this.uuid = defender.getUniqueId();
    this.entityTags = defender.getPersistentDataContainer();
    this.currentHealth = entityTags.getOrDefault(Key.RPG_CURRENT_HEALTH.getNamespacedKey(), PersistentDataType.DOUBLE, defender.getHealth());

    Buffs buffs = Plugin.getData().getRpgSystem().getBuffs().get(uuid);
    double genericMaxHealthBuff = 0.0;
    double maxHealthBuff = 0.0;
    if (buffs != null) {
      genericMaxHealthBuff = buffs.getAttribute(Attribute.GENERIC_MAX_HEALTH);
      maxHealthBuff = buffs.getAethelAttribute(AethelAttribute.MAX_HEALTH);
    }

    this.maxHealth = defender.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + genericMaxHealthBuff + maxHealthBuff;
  }

  /**
   * Damages the entity by an amount.
   *
   * @param damage damage amount
   */
  public void damage(double damage) {
    defender.damage(0.1);
    double remainingHealth = currentHealth - damage;
    setCurrentHealth(remainingHealth);

    if (remainingHealth > 0) {
      updateDisplays();
    } else {
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
        defender.setHealth(0.0);
        if (defender instanceof Player) {
          BossBar healthBar = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getDisplays().getBar();
          if (healthBar.isVisible()) {
            DecimalFormat df2 = new DecimalFormat();
            df2.setMaximumFractionDigits(2);
            healthBar.setProgress(0.0);
            healthBar.setTitle(0 + " / " + df2.format(maxHealth) + " HP");
          }
        }
      }, 1);
    }
  }

  /**
   * Heals the entity by an amount.
   *
   * @param heal heal amount
   */
  public void heal(double heal) {
    if (!(currentHealth > maxHealth)) {
      setCurrentHealth(Math.min(currentHealth + heal, maxHealth));
      updateDisplays();
    }
  }

  /**
   * Adds the entity's absorption amount to their current health.
   */
  public void shield() {
    setCurrentHealth(currentHealth + defender.getAbsorptionAmount());
    defender.setAbsorptionAmount(0);
    updateDisplays();
  }

  /**
   * Resets the current health.
   */
  public void reset() {
    setCurrentHealth(maxHealth);
    updateDisplays();
  }

  /**
   * Decays current health based on the overshield amount.
   */
  public void decayOvershield() {
    double overshieldCap = maxHealth * 1.2;
    if (currentHealth > overshieldCap) {
      double decayRate = Math.max((currentHealth - overshieldCap) / 40, 0.25);
      setCurrentHealth(Math.max(overshieldCap, currentHealth - decayRate));
      updateDisplays();
    }
  }

  /**
   * Updates health displays.
   */
  public void updateDisplays() {
    double maxHealthScale = defender.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
    if (currentHealth < maxHealth) {
      double lifeRatio = currentHealth / maxHealth;
      defender.setHealth(Math.max(0, lifeRatio * maxHealthScale));

      if (defender instanceof Player) {
        updateActionDisplay(Condition.WOUNDED);
        updateBarDisplay(Condition.WOUNDED, lifeRatio);
      }
    } else if (currentHealth == maxHealth) {
      defender.setHealth(maxHealthScale);

      if (defender instanceof Player) {
        updateActionDisplay(Condition.NORMAL);
        updateBarDisplay(Condition.NORMAL, 1.0);
      }
    } else if (currentHealth > maxHealth) {
      defender.setHealth(maxHealthScale);

      if (defender instanceof Player) {
        updateActionDisplay(Condition.OVERSHIELD);
        updateBarDisplay(Condition.OVERSHIELD, 1.0);
      }
    }
  }

  /**
   * Updates the action bar display.
   */
  public void updateActionDisplay() {
    Settings settings = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getSettings();
    if (settings.isHealthActionVisible()) {
      DecimalFormat df2 = new DecimalFormat();
      df2.setMaximumFractionDigits(2);
      switch (getCondition()) {
        case WOUNDED, NORMAL -> Bukkit.getPlayer(uuid).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + df2.format(currentHealth) + " / " + df2.format(maxHealth) + " ❤"));
        case OVERSHIELD -> Bukkit.getPlayer(uuid).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.YELLOW + df2.format(currentHealth) + " / " + df2.format(maxHealth) + " ❤"));
      }
    }
  }

  /**
   * Updates the action bar display.
   *
   * @param condition {@link Condition}
   */
  private void updateActionDisplay(Condition condition) {
    Settings settings = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getSettings();
    if (settings.isHealthActionVisible()) {
      DecimalFormat df2 = new DecimalFormat();
      df2.setMaximumFractionDigits(2);
      switch (condition) {
        case WOUNDED, NORMAL -> Bukkit.getPlayer(uuid).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + df2.format(currentHealth) + " / " + df2.format(maxHealth) + " ❤"));
        case OVERSHIELD -> Bukkit.getPlayer(uuid).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.YELLOW + df2.format(currentHealth) + " / " + df2.format(maxHealth) + " ❤"));
      }
    }
  }

  /**
   * Updates the health bar display.
   *
   * @param condition {@link Condition}
   * @param lifeRatio hearts displayed : true max health
   */
  private void updateBarDisplay(Condition condition, double lifeRatio) {
    BossBar healthBar = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getDisplays().getBar();
    if (healthBar.isVisible()) {
      DecimalFormat df2 = new DecimalFormat();
      df2.setMaximumFractionDigits(2);
      switch (condition) {
        case WOUNDED -> {
          healthBar.setProgress(lifeRatio);
          healthBar.setColor(BarColor.RED);
          healthBar.setTitle(ChatColor.RED + df2.format(currentHealth) + " / " + df2.format(maxHealth) + " ❤");
        }
        case NORMAL -> {
          healthBar.setProgress(1.0);
          healthBar.setColor(BarColor.RED);
          healthBar.setTitle(ChatColor.RED + df2.format(currentHealth) + " / " + df2.format(maxHealth) + " ❤");
        }
        case OVERSHIELD -> {
          healthBar.setProgress(1.0);
          healthBar.setColor(BarColor.YELLOW);
          healthBar.setTitle(ChatColor.YELLOW + df2.format(currentHealth) + " / " + df2.format(maxHealth) + " ❤");
        }
      }
    }
  }

  /**
   * Sets the entity's current health.
   *
   * @param currentHealth current health
   */
  private void setCurrentHealth(double currentHealth) {
    this.currentHealth = currentHealth;
    entityTags.set(Key.RPG_CURRENT_HEALTH.getNamespacedKey(), PersistentDataType.DOUBLE, currentHealth);
  }

  /**
   * Gets the {@link Condition}.
   *
   * @return {@link Condition}
   */
  private Condition getCondition() {
    if (currentHealth < maxHealth) {
      return Condition.WOUNDED;
    } else if (currentHealth == maxHealth) {
      return Condition.NORMAL;
    } else if (currentHealth > maxHealth) {
      return Condition.OVERSHIELD;
    }
    return null;
  }

  /**
   * Types of health conditions.
   */
  private enum Condition {
    /**
     * Below max health.
     */
    WOUNDED,

    /**
     * At max health.
     */
    NORMAL,

    /**
     * Above max health.
     */
    OVERSHIELD
  }
}
