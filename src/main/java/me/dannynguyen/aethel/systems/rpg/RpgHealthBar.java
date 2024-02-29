package me.dannynguyen.aethel.systems.rpg;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an RPG player's health bar.
 *
 * @author Danny Nguyen
 * @version 1.13.4
 * @since 1.13.4
 */
public class RpgHealthBar {
  /**
   * Player's UUID.
   */
  private final UUID uuid;

  /**
   * Total Aethel attributes.
   */
  private final Map<AethelAttribute, Double> aethelAttributes;

  /**
   * Health bar.
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
   * Associates RPG health bar with a player.
   *
   * @param player           interacting player
   * @param aethelAttributes total Aethel attributes
   */
  public RpgHealthBar(@NotNull Player player, @NotNull Map<AethelAttribute, Double> aethelAttributes) {
    this.uuid = Objects.requireNonNull(player, "Null player").getUniqueId();
    this.aethelAttributes = Objects.requireNonNull(aethelAttributes, "Null Aethel Attributes");
    this.currentHealth = player.getHealth();
    this.maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + aethelAttributes.get(AethelAttribute.MAX_HP);
    initializeHealthBar(player);
  }

  /**
   * Initializes the player's health bar.
   *
   * @param player interacting player
   */
  private void initializeHealthBar(Player player) {
    double minecraftMaxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
    double healthScale = (aethelAttributes.get(AethelAttribute.MAX_HP) + minecraftMaxHealth) / minecraftMaxHealth;
    setCurrentHealth(currentHealth * healthScale);
    updateProgress();
    healthBar.addPlayer(player);
  }

  /**
   * Updates the health bar and display.
   */
  public void update() {
    Player player = Bukkit.getPlayer(uuid);
    setCurrentHealth(currentHealth + player.getAbsorptionAmount());
    player.setAbsorptionAmount(0);
    setMaxHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + aethelAttributes.get(AethelAttribute.MAX_HP));
    updateProgress();
  }

  /**
   * Damages the player by an amount.
   *
   * @param damage damage amount
   */
  public void damage(double damage) {
    setCurrentHealth(currentHealth - damage);
    if (currentHealth > 0) {
      updateProgress();
    } else {
      DecimalFormat df2 = new DecimalFormat();
      df2.setMaximumFractionDigits(2);
      setCurrentHealth(0.0);
      Bukkit.getPlayer(uuid).setHealth(0.0);
      healthBar.setProgress(0.0);
      healthBar.setTitle(0 + " / " + df2.format(maxHealth) + " HP");
    }
  }

  /**
   * Heals the player by an amount.
   *
   * @param heal heal amount
   */
  public void heal(double heal) {
    if (!(currentHealth > maxHealth)) {
      setCurrentHealth(Math.min(maxHealth, currentHealth + heal));
      updateProgress();
    }
  }

  /**
   * Resets the health bar.
   */
  public void reset() {
    setCurrentHealth(20.0);
    setMaxHealth(20.0);
    updateProgress();
  }

  /**
   * Decays a player's current health.
   * <p>
   * This method should only be used when a player's overshield
   * (current health > max health) exceeds x1.2 their max health.
   * </p>
   */
  public void decayOvershield() {
    double overshieldCap = maxHealth * 1.2;
    double decayRate = Math.max((currentHealth - overshieldCap) / 40, 0.25);
    setCurrentHealth(Math.max(overshieldCap, currentHealth - decayRate));
    updateProgress();
  }

  /**
   * Toggles the visibility of the health bar.
   */
  public void toggleVisibility() {
    healthBar.setVisible(!healthBar.isVisible());
  }

  /**
   * Sets the progress of the health bar based on the current health : max health.
   */
  private void updateProgress() {
    Player player = Bukkit.getPlayer(uuid);
    double maxHealthScale = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
    if (currentHealth < maxHealth) {
      double lifeRatio = currentHealth / maxHealth;
      player.setHealth(lifeRatio * maxHealthScale);
      healthBar.setProgress(lifeRatio);
      healthBar.setColor(BarColor.RED);
    } else if (currentHealth == maxHealth) {
      player.setHealth(maxHealthScale);
      healthBar.setProgress(1.0);
      healthBar.setColor(BarColor.RED);
    } else if (currentHealth > maxHealth) {
      player.setHealth(maxHealthScale);
      healthBar.setProgress(1.0);
      healthBar.setColor(BarColor.YELLOW);
    }
    DecimalFormat df2 = new DecimalFormat();
    df2.setMaximumFractionDigits(2);
    healthBar.setTitle(df2.format(currentHealth) + " / " + df2.format(maxHealth) + " HP");
  }

  /**
   * Gets the health bar.
   *
   * @return player's health bar
   */
  @NotNull
  public BossBar getHealthBar() {
    return this.healthBar;
  }

  /**
   * Gets the current health.
   *
   * @return player's current health
   */
  public double getCurrentHealth() {
    return this.currentHealth;
  }

  /**
   * Gets the max health.
   *
   * @return player's max health
   */
  public double getMaxHealth() {
    return this.maxHealth;
  }


  /**
   * Sets the player's current health.
   *
   * @param currentHealth new current health value
   */
  private void setCurrentHealth(Double currentHealth) {
    this.currentHealth = currentHealth;
  }

  /**
   * Sets the player's max health.
   *
   * @param maxHealth new max health value
   */
  private void setMaxHealth(Double maxHealth) {
    this.maxHealth = maxHealth;
  }
}
