package me.dannynguyen.aethel.rpg;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.rpg.AethelAttribute;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an {@link RpgPlayer}'s health.
 *
 * @author Danny Nguyen
 * @version 1.20.1
 * @since 1.13.4
 */
public class Health {
  /**
   * Player's UUID.
   */
  private final UUID uuid;

  /**
   * {@link AethelAttributes}
   */
  private final AethelAttributes attributes;

  /**
   * {@link Settings}
   */
  private final Settings settings;

  /**
   * Health bar display.
   */
  private final BossBar healthBar = Bukkit.createBossBar("Health", BarColor.RED, BarStyle.SEGMENTED_10);

  /**
   * Player's health.
   */
  private double currentHealth;

  /**
   * Player's max health.
   */
  private double maxHealth;

  /**
   * Associates RPG health with a player.
   *
   * @param player     interacting player
   * @param attributes {@link AethelAttributes}
   * @param settings   {@link Settings}
   */
  public Health(@NotNull Player player, @NotNull AethelAttributes attributes, @NotNull Settings settings) {
    this.uuid = Objects.requireNonNull(player, "Null player").getUniqueId();
    this.attributes = Objects.requireNonNull(attributes, "Null Aethel attributes");
    this.settings = Objects.requireNonNull(settings, "Null settings");
    healthBar.setVisible(settings.isHealthBarVisible());
    this.currentHealth = player.getHealth();
    this.maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + attributes.getAttributes().get(AethelAttribute.MAX_HEALTH);
    initializeHealth(player);
  }

  /**
   * Initializes the player's health.
   *
   * @param player interacting player
   */
  private void initializeHealth(Player player) {
    double minecraftMaxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
    double healthScale = (attributes.getAttributes().get(AethelAttribute.MAX_HEALTH) + minecraftMaxHealth) / minecraftMaxHealth;
    setCurrentHealth(currentHealth * healthScale);
    updateDisplays();
    healthBar.addPlayer(player);
  }

  /**
   * Updates the max health.
   */
  public void updateMaxHealth() {
    setMaxHealth(Bukkit.getPlayer(uuid).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + attributes.getAttributes().get(AethelAttribute.MAX_HEALTH));
    updateDisplays();
  }

  /**
   * Updates the current health from an absorption effect.
   */
  public void updateOvershield() {
    Player player = Bukkit.getPlayer(uuid);
    if (player != null) {
      setCurrentHealth(currentHealth + player.getAbsorptionAmount());
      player.setAbsorptionAmount(0);
      updateDisplays();
    }
  }

  /**
   * Damages the player by an amount.
   *
   * @param damage damage amount
   */
  public void damage(double damage) {
    setCurrentHealth(currentHealth - damage);
    if (currentHealth > 0) {
      updateDisplays();
    } else {
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
        setCurrentHealth(0.0);
        Bukkit.getPlayer(uuid).setHealth(currentHealth);
        if (healthBar.isVisible()) {
          DecimalFormat df2 = new DecimalFormat();
          df2.setMaximumFractionDigits(2);
          healthBar.setProgress(0.0);
          healthBar.setTitle(0 + " / " + df2.format(maxHealth) + " HP");
        }
      }, 1);
    }
  }

  /**
   * Heals the player by an amount.
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
   * Resets the health.
   */
  public void reset() {
    setCurrentHealth(20.0);
    setMaxHealth(20.0);
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
   * Updates health bar displays.
   */
  void updateDisplays() {
    Player player = Bukkit.getPlayer(uuid);
    double maxHealthScale = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
    if (currentHealth < maxHealth) {
      double lifeRatio = currentHealth / maxHealth;
      player.setHealth(lifeRatio * maxHealthScale);
      updateActionDisplay(Condition.WOUNDED);
      updateBarDisplay(Condition.WOUNDED, lifeRatio);
    } else if (currentHealth == maxHealth) {
      player.setHealth(maxHealthScale);
      updateActionDisplay(Condition.NORMAL);
      updateBarDisplay(Condition.NORMAL, 1.0);
    } else if (currentHealth > maxHealth) {
      player.setHealth(maxHealthScale);
      updateActionDisplay(Condition.OVERSHIELD);
      updateBarDisplay(Condition.OVERSHIELD, 1.0);
    }
  }

  /**
   * Updates the action bar display.
   *
   * @param condition {@link Condition}
   */
  public void updateActionDisplay(@NotNull Health.Condition condition) {
    if (settings.isHealthActionVisible()) {
      DecimalFormat df2 = new DecimalFormat();
      df2.setMaximumFractionDigits(2);
      switch (Objects.requireNonNull(condition, "Null condition")) {
        case WOUNDED, NORMAL -> Bukkit.getPlayer(uuid).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + df2.format(currentHealth) + " / " + df2.format(maxHealth) + " ❤"));
        case OVERSHIELD -> Bukkit.getPlayer(uuid).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.YELLOW + df2.format(currentHealth) + " / " + df2.format(maxHealth) + " ❤"));
      }
    }
  }

  /**
   * Updates the action bar display.
   */
  public void updateActionDisplay() {
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
   * Updates the health bar display.
   *
   * @param condition {@link Condition}
   * @param lifeRatio hearts displayed : true max health
   */
  private void updateBarDisplay(Condition condition, double lifeRatio) {
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
   * Gets remaining health out of a 100% scale.
   *
   * @return remaining health
   */
  public double getHealthPercent() {
    return (currentHealth / maxHealth) * 100;
  }

  /**
   * Gets the health bar.
   *
   * @return health bar
   */
  @NotNull
  public BossBar getBar() {
    return this.healthBar;
  }

  /**
   * Gets the current health.
   *
   * @return current health
   */
  public double getCurrentHealth() {
    return this.currentHealth;
  }

  /**
   * Sets the current health.
   *
   * @param currentHealth new current health value
   */
  private void setCurrentHealth(double currentHealth) {
    this.currentHealth = currentHealth;
  }

  /**
   * Gets the max health.
   *
   * @return max health
   */
  public double getMaxHealth() {
    return this.maxHealth;
  }

  /**
   * Sets the max health.
   *
   * @param maxHealth new max health value
   */
  private void setMaxHealth(double maxHealth) {
    this.maxHealth = maxHealth;
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
