package me.dannynguyen.aethel.systems.rpg;

import me.dannynguyen.aethel.Plugin;
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
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an RPG player's health.
 *
 * @author Danny Nguyen
 * @version 1.14.0
 * @since 1.13.4
 */
public class RpgHealth {
  /**
   * Player's UUID.
   */
  private final UUID uuid;

  /**
   * Total Aethel attributes.
   */
  private final Map<AethelAttribute, Double> aethelAttributes;

  /**
   * Health bar display.
   */
  private final BossBar healthBar = Bukkit.createBossBar("Health", BarColor.RED, BarStyle.SEGMENTED_10);

  /**
   * Whether to display health in the action bar.
   */
  private boolean healthActionVisible;

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
   * @param player           interacting player
   * @param aethelAttributes total Aethel attributes
   */
  public RpgHealth(@NotNull Player player, @NotNull Map<AethelAttribute, Double> aethelAttributes) {
    this.uuid = Objects.requireNonNull(player, "Null player").getUniqueId();
    this.aethelAttributes = Objects.requireNonNull(aethelAttributes, "Null Aethel Attributes");
    this.healthActionVisible = true;
    this.currentHealth = player.getHealth();
    this.maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + aethelAttributes.get(AethelAttribute.MAX_HP);
    initializeHealth(player);
  }

  /**
   * Initializes the player's health.
   *
   * @param player interacting player
   */
  private void initializeHealth(Player player) {
    double minecraftMaxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
    double healthScale = (aethelAttributes.get(AethelAttribute.MAX_HP) + minecraftMaxHealth) / minecraftMaxHealth;
    setCurrentHealth(currentHealth * healthScale);
    updateDisplays();
    healthBar.addPlayer(player);
  }

  /**
   * Updates the max health.
   */
  public void updateMaxHealth() {
    Player player = Bukkit.getPlayer(uuid);
    setMaxHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + aethelAttributes.get(AethelAttribute.MAX_HP));
    updateDisplays();
  }

  /**
   * Updates the current health from an absorption effect.
   */
  public void updateOvershield() {
    Player player = Bukkit.getPlayer(uuid);
    setCurrentHealth(currentHealth + player.getAbsorptionAmount());
    player.setAbsorptionAmount(0);
    updateDisplays();
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
      setCurrentHealth(Math.min(maxHealth, currentHealth + heal));
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
   * Decays current health.
   * <p>
   * This method should only be used when an overshield
   * (current health > max health) exceeds x1.2 max health.
   * </p>
   */
  public void decayOvershield() {
    double overshieldCap = maxHealth * 1.2;
    double decayRate = Math.max((currentHealth - overshieldCap) / 40, 0.25);
    setCurrentHealth(Math.max(overshieldCap, currentHealth - decayRate));
    updateDisplays();
  }

  /**
   * Toggles the visibility of the health bar.
   */
  public void toggleBarVisibility() {
    if (healthBar.isVisible()) {
      healthBar.setVisible(false);
    } else {
      healthBar.setVisible(true);
      updateDisplays();
    }
  }

  /**
   * Toggles the visibility of health in the action bar.
   */
  public void toggleActionVisibility() {
    if (healthActionVisible) {
      healthActionVisible = false;
    } else {
      healthActionVisible = true;
    }
  }

  /**
   * Updates health bar displays.
   */
  private void updateDisplays() {
    Player player = Bukkit.getPlayer(uuid);
    double maxHealthScale = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
    if (currentHealth < maxHealth) {
      double lifeRatio = currentHealth / maxHealth;
      player.setHealth(lifeRatio * maxHealthScale);
      updateActionDisplay(RpgHealthCondition.WOUNDED);
      updateBarDisplay(RpgHealthCondition.WOUNDED, lifeRatio);
    } else if (currentHealth == maxHealth) {
      player.setHealth(maxHealthScale);
      updateActionDisplay(RpgHealthCondition.NORMAL);
      updateBarDisplay(RpgHealthCondition.NORMAL, 1.0);
    } else if (currentHealth > maxHealth) {
      player.setHealth(maxHealthScale);
      updateActionDisplay(RpgHealthCondition.OVERSHIELD);
      updateBarDisplay(RpgHealthCondition.OVERSHIELD, 1.0);
    }
  }

  /**
   * Updates the action bar display.
   */
  public void updateActionDisplay(@NotNull RpgHealthCondition condition) {
    if (healthActionVisible) {
      Objects.requireNonNull(condition, "Null condition");
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
   * @param condition health condition
   * @param lifeRatio hearts displayed : true max health
   */
  private void updateBarDisplay(RpgHealthCondition condition, Double lifeRatio) {
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
   * Gets the health bar.
   *
   * @return health bar
   */
  @NotNull
  public BossBar getBar() {
    return this.healthBar;
  }

  /**
   * Gets if health in the action bar is displayed.
   *
   * @return if health in the action bar displayed
   */
  public boolean isHealthActionVisible() {
    return this.healthActionVisible;
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
   * Gets the max health.
   *
   * @return max health
   */
  public double getMaxHealth() {
    return this.maxHealth;
  }

  /**
   * Sets the current health.
   *
   * @param currentHealth new current health value
   */
  private void setCurrentHealth(Double currentHealth) {
    this.currentHealth = currentHealth;
  }

  /**
   * Sets the max health.
   *
   * @param maxHealth new max health value
   */
  private void setMaxHealth(Double maxHealth) {
    this.maxHealth = maxHealth;
  }
}
