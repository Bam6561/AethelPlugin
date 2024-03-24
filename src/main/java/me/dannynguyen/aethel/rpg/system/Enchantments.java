package me.dannynguyen.aethel.rpg.system;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents an {@link RpgPlayer}'s enchantments.
 *
 * @author Danny Nguyen
 * @version 1.17.9
 * @since 1.17.9
 */
public class Enchantments {
  /**
   * Tracked enchantments.
   */
  private static final Set<Enchantment> trackedEnchantments = Set.of(
      Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_EXPLOSIONS,
      Enchantment.PROTECTION_FALL, Enchantment.PROTECTION_FIRE, Enchantment.PROTECTION_PROJECTILE);

  /**
   * Total enchantments.
   */
  private final Map<Enchantment, Integer> totalEnchantments = createBlankTotalEnchantments();

  /**
   * Enchantments on {@link RpgEquipmentSlot}.
   */
  private final Map<RpgEquipmentSlot, Map<Enchantment, Integer>> slotEnchantments = new HashMap<>();

  /**
   * Player's UUID.
   */
  private final UUID uuid;

  /**
   * Associates enchantments with a player.
   *
   * @param uuid player's UUID
   */
  public Enchantments(@NotNull UUID uuid) {
    this.uuid = Objects.requireNonNull(uuid, "Null uuid");
  }

  /**
   * Creates a blank map of total enchantment levels.
   *
   * @return blank total enchantment levels
   */
  private Map<Enchantment, Integer> createBlankTotalEnchantments() {
    Map<Enchantment, Integer> enchantments = new HashMap<>();
    for (Enchantment enchantment : trackedEnchantments) {
      enchantments.put(enchantment, 0);
    }
    return enchantments;
  }

  /**
   * Adds new {@link Equipment} enchantments.
   *
   * @param eSlot {@link RpgEquipmentSlot}
   * @param item  interacting item
   */
  public void addEnchantments(@NotNull RpgEquipmentSlot eSlot, @NotNull ItemStack item) {
    Objects.requireNonNull(eSlot, "Null slot");
    Objects.requireNonNull(item, "Null item");
    for (Enchantment enchantment : trackedEnchantments) {
      slotEnchantments.get(eSlot).put(enchantment, item.getEnchantmentLevel(enchantment));
      totalEnchantments.put(enchantment, totalEnchantments.get(enchantment) + item.getEnchantmentLevel(enchantment));
    }
    readEnchantmentLevel(Enchantment.PROTECTION_FALL, 5);
    readEnchantmentLevel(Enchantment.PROTECTION_FIRE, 10);
  }

  /**
   * Removes existing {@link Equipment} enchantments at an {@link RpgEquipmentSlot}.
   *
   * @param eSlot {@link RpgEquipmentSlot}
   */
  public void removeEnchantments(@NotNull RpgEquipmentSlot eSlot) {
    for (Enchantment enchantment : slotEnchantments.get(Objects.requireNonNull(eSlot, "Null slot")).keySet()) {
      totalEnchantments.put(enchantment, totalEnchantments.get(enchantment) - slotEnchantments.get(eSlot).get(enchantment));
      slotEnchantments.get(eSlot).put(enchantment, 0);
    }
    readEnchantmentLevel(Enchantment.PROTECTION_FALL, 5);
    readEnchantmentLevel(Enchantment.PROTECTION_FIRE, 10);
  }

  /**
   * Checks if the player has met a certain enchantment level.
   *
   * @param enchantment enchantment to be checked
   * @param requirement required level to be sufficient
   */
  private void readEnchantmentLevel(Enchantment enchantment, int requirement) {
    if (totalEnchantments.get(enchantment) >= requirement) {
      Plugin.getData().getRpgSystem().getSufficientEnchantments().get(enchantment).add(uuid);
    } else {
      Plugin.getData().getRpgSystem().getSufficientEnchantments().get(enchantment).remove(uuid);
    }
  }

  /**
   * Gets the player's total enchantments.
   *
   * @return total enchantments
   */
  @NotNull
  public Map<Enchantment, Integer> getTotalEnchantments() {
    return this.totalEnchantments;
  }

  /**
   * Gets the player's {@link Equipment} enchantments on {@link RpgEquipmentSlot}.
   *
   * @return enchantments on {@link RpgEquipmentSlot}
   */
  @NotNull
  public Map<RpgEquipmentSlot, Map<Enchantment, Integer>> getSlotEnchantments() {
    return this.slotEnchantments;
  }
}
