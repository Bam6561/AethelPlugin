package me.dannynguyen.aethel.listeners.rpg;

import me.dannynguyen.aethel.systems.plugin.PluginData;
import me.dannynguyen.aethel.systems.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Random;

/**
 * Lowers an item's durability and breaks it accordingly.
 *
 * @author Danny Nguyen
 * @version 1.12.10
 * @since 1.12.10
 */
class DurabilityDamage {
  /**
   * Player taking damage.
   */
  private final Player damagee;

  /**
   * Player's inventory.
   */
  private final PlayerInventory pInv;

  /**
   * Random number generator.
   */
  private final Random random;

  /**
   * Durability loss.
   */
  private final int durabilityLoss;

  /**
   * Associates a damagee with their damage taken.
   *
   * @param damagee player taking damage
   * @param damage  damage taken
   */
  protected DurabilityDamage(@NotNull Player damagee, double damage) {
    this.damagee = Objects.requireNonNull(damagee, "Null player");
    this.pInv = damagee.getInventory();
    this.random = new Random();
    this.durabilityLoss = (int) Math.max(damage / 4, 1);
  }

  /**
   * Damages an item's durability.
   * <p>
   * If the item's damage exceeds the item's max durability, it is broken.
   * </p>
   *
   * @param slot equipment slot
   */
  protected void damageDurability(EquipmentSlot slot) {
    ItemStack item = pInv.getItem(slot);
    if (ItemReader.isNotNullOrAir(item)) {
      int unbreaking = item.getEnchantmentLevel(Enchantment.DURABILITY);
      if (unbreaking > 0) {
        double damageChance = 1.0 / (unbreaking + 1);
        if (damageChance < random.nextDouble()) {
          return;
        }
      }
      Damageable durability = (Damageable) item.getItemMeta();
      durability.setDamage(durability.getDamage() + durabilityLoss);
      if (durability.getDamage() > item.getType().getMaxDurability()) {
        pInv.setItem(slot, new ItemStack(Material.AIR));
        damagee.playSound(damagee.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
        PluginData.rpgSystem.getRpgPlayers().get(damagee.getUniqueId()).readEquipmentSlot(null, RpgEquipmentSlot.asEnum(slot.name()));
      } else {
        item.setItemMeta(durability);
      }
    }
  }
}

