package me.dannynguyen.aethel.utility;

import me.dannynguyen.aethel.systems.plugin.PluginData;
import me.dannynguyen.aethel.systems.rpg.RpgEquipmentSlot;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;

import java.util.Random;

/**
 * Modifies existing items' durabilities.
 *
 * @author Danny Nguyen
 * @version 1.13.4
 * @since 1.13.0
 */
public class ItemDurability {
  /**
   * Utility methods only.
   */
  private ItemDurability() {
  }

  /**
   * Damages an item's durability.
   * <p>
   * An item is broken when its damage exceeds its material type's durability.
   * </p>
   *
   * @param player interacting player
   * @param slot   slot type
   * @param damage durability damage
   */
  public static void increaseDamage(Player player, EquipmentSlot slot, int damage) {
    PlayerInventory pInv = player.getInventory();
    ItemStack item = pInv.getItem(slot);
    if (ItemReader.isNotNullOrAir(item)) {
      int unbreaking = item.getEnchantmentLevel(Enchantment.DURABILITY);
      if (unbreaking > 0) {
        double damageChance = 1.0 / (unbreaking + 1);
        if (damageChance < new Random().nextDouble()) {
          return;
        }
      }
      Damageable durability = (Damageable) item.getItemMeta();
      durability.setDamage(durability.getDamage() + damage);
      if (durability.getDamage() > item.getType().getMaxDurability()) {
        pInv.setItem(slot, new ItemStack(Material.AIR));
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
        PluginData.rpgSystem.getRpgPlayers().get(player.getUniqueId()).getEquipment().readSlot(null, RpgEquipmentSlot.valueOf(slot.name().toUpperCase()));
      } else {
        item.setItemMeta(durability);
      }
    }
  }

  /**
   * Sets an item's durability.
   * <p>
   * If the durability to be set exceeds the material type's max durability,
   * the durability is set to match the material type's max durability instead.
   * </p>
   *
   * @param item                interacting item
   * @param requestedDurability requested durability
   */
  public static void setDurability(ItemStack item, int requestedDurability) {
    Damageable durability = (Damageable) item.getItemMeta();
    if (requestedDurability > item.getType().getMaxDurability()) {
      durability.setDamage(0);
    } else {
      durability.setDamage(Math.abs(requestedDurability - item.getType().getMaxDurability()));
    }
    item.setItemMeta(durability);
  }

  /**
   * Sets an item's damage.
   * <p>
   * If the damage to be set exceeds the material type's max durability,
   * the damage is set to match the material type's max durability instead.
   * </p>
   *
   * @param item   interacting item
   * @param damage durability damage
   */
  public static void setDamage(ItemStack item, int damage) {
    Damageable durability = (Damageable) item.getItemMeta();
    durability.setDamage(Math.min(damage, item.getType().getMaxDurability()));
    item.setItemMeta(durability);
  }
}
