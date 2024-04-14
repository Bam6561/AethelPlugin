package me.dannynguyen.aethel.utils.item;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.utils.TextFormatter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Gets or modifies existing items' durabilities.
 *
 * @author Danny Nguyen
 * @version 1.23.2
 * @since 1.13.0
 */
public class ItemDurability {
  /**
   * Utility methods only.
   */
  private ItemDurability() {
  }

  /**
   * Gets the item's durability.
   * <p>
   * Returns -1 if the item's meta cannot be type cast to a Damageable item meta.
   *
   * @param item interacting item
   * @return item durability
   */
  public static int getDurability(@NotNull ItemStack item) {
    if (Objects.requireNonNull(item, "Null item").getItemMeta() instanceof Damageable durability) {
      return item.getType().getMaxDurability() - durability.getDamage();
    } else {
      return -1;
    }
  }

  /**
   * Gets the item's damage.
   * <p>
   * Returns -1 if the item's meta cannot be type cast to a Damageable item meta.
   *
   * @param item interacting item
   * @return item damage
   */
  public static int getDamage(@NotNull ItemStack item) {
    if (Objects.requireNonNull(item, "Null item").getItemMeta() instanceof Damageable durability) {
      return durability.getDamage();
    } else {
      return -1;
    }
  }

  /**
   * Displays the item's remaining to maximum durability.
   *
   * @param item interacting item
   * @return remaining : maximum durability
   */
  @NotNull
  public static String displayDurability(@NotNull ItemStack item) {
    if (Objects.requireNonNull(item, "Null item").getItemMeta() instanceof Damageable durability) {
      short maxDurability = item.getType().getMaxDurability();
      int durabilityValue = maxDurability - durability.getDamage();
      return durabilityValue + " / " + maxDurability;
    }
    return "";
  }

  /**
   * Damages an item's durability.
   * <p>
   * An item is broken when its damage exceeds its material type's durability.
   *
   * @param defender  defending entity
   * @param equipment entity equipment
   * @param eSlot     equipment slot
   * @param damage    durability damage
   */
  public static void increaseDamage(@NotNull LivingEntity defender, @NotNull EntityEquipment equipment, @NotNull EquipmentSlot eSlot, int damage) {
    Objects.requireNonNull(defender, "Null defender");
    Objects.requireNonNull(equipment, "Null equipment");
    Objects.requireNonNull(eSlot, "Null slot");

    ItemStack item = equipment.getItem(eSlot);
    if (ItemReader.isNullOrAir(item) || !(item.getItemMeta() instanceof Damageable durability)) {
      return;
    }

    int unbreaking = item.getEnchantmentLevel(Enchantment.DURABILITY);
    if (unbreaking > 0) {
      double damageChance = 1.0 / (unbreaking + 1);
      if (damageChance < new Random().nextDouble()) {
        return;
      }
    }

    PersistentDataContainer itemTags = durability.getPersistentDataContainer();
    boolean hasReinforcementTags = itemTags.has(Key.RPG_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER) && itemTags.has(Key.RPG_MAX_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER);
    if (hasReinforcementTags && itemTags.get(Key.RPG_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER) != 0) {
      List<String> lore = durability.getLore();
      int reinforcement = itemTags.get(Key.RPG_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER);
      int maxReinforcement = itemTags.get(Key.RPG_MAX_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER);

      reinforcement = reinforcement - damage;
      if (reinforcement > 0) {
        itemTags.set(Key.RPG_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER, reinforcement);

        if (lore.isEmpty()) {
          lore.add(ChatColor.WHITE + "Reinforcement: " + reinforcement + " / " + maxReinforcement);
        } else {
          lore.set(lore.size() - 1, ChatColor.WHITE + "Reinforcement: " + reinforcement + " / " + maxReinforcement);
        }

        durability.setLore(lore);
        item.setItemMeta(durability);

        if (!(defender instanceof Player)) {
          equipment.setItem(eSlot, item);
        }
        return;
      } else {
        itemTags.set(Key.RPG_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER, 0);

        if (lore.isEmpty()) {
          lore.add(ChatColor.WHITE + "Reinforcement: 0 / " + maxReinforcement);
        } else {
          lore.set(lore.size() - 1, ChatColor.WHITE + "Reinforcement: 0 / " + maxReinforcement);
        }

        durability.setLore(lore);
        damage = Math.abs(reinforcement);
      }
    }

    durability.setDamage(durability.getDamage() + damage);

    if (durability.getDamage() > item.getType().getMaxDurability()) {
      equipment.setItem(eSlot, new ItemStack(Material.AIR));
      defender.getWorld().playSound(defender.getEyeLocation(), Sound.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1, 1);
      if (defender instanceof Player) {
        Plugin.getData().getRpgSystem().getRpgPlayers().get(defender.getUniqueId()).getEquipment().readSlot(null, RpgEquipmentSlot.valueOf(TextFormatter.formatEnum(eSlot.name())));
      }
      return;
    }

    item.setItemMeta(durability);

    if (!(defender instanceof Player)) {
      equipment.setItem(eSlot, item);
    }
  }

  /**
   * Sets an item's durability.
   * <p>
   * If the durability to be set exceeds the material type's max durability,
   * the durability is set to match the material type's max durability instead.
   *
   * @param item                interacting item
   * @param requestedDurability requested durability
   */
  public static void setDurability(@NotNull ItemStack item, int requestedDurability) {
    if (Objects.requireNonNull(item, "Null item").getItemMeta() instanceof Damageable durability) {
      if (requestedDurability > item.getType().getMaxDurability()) {
        durability.setDamage(0);
      } else {
        durability.setDamage(Math.abs(requestedDurability - item.getType().getMaxDurability()));
      }
      item.setItemMeta(durability);
    }
  }

  /**
   * Sets an item's damage.
   * <p>
   * If the damage to be set exceeds the material type's max durability,
   * the damage is set to match the material type's max durability instead.
   *
   * @param item   interacting item
   * @param damage durability damage
   */
  public static void setDamage(@NotNull ItemStack item, int damage) {
    if (Objects.requireNonNull(item, "Null item").getItemMeta() instanceof Damageable durability) {
      durability.setDamage(Math.min(damage, item.getType().getMaxDurability()));
      item.setItemMeta(durability);
    }
  }
}
