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

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Changes damage on existing items' durabilities.
 *
 * @author Danny Nguyen
 * @version 1.27.0
 * @since 1.13.0
 */
public class DurabilityChange {
  /**
   * Utility methods only.
   */
  private DurabilityChange() {
  }

  /**
   * Damages an item's durability.
   * <p>
   * If the item has {@link Key#RPG_DURABILITY}, the reinforcement value is damaged first.
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

    int unbreaking = item.getEnchantmentLevel(Enchantment.UNBREAKING);
    if (unbreaking > 0) {
      double damageChance = 1.0 / (unbreaking + 1);
      if (damageChance < new Random().nextDouble()) {
        return;
      }
    }

    PersistentDataContainer itemTags = durability.getPersistentDataContainer();
    boolean hasReinforcementTags = itemTags.has(Key.RPG_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER) && itemTags.has(Key.RPG_MAX_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER);
    if (hasReinforcementTags && itemTags.get(Key.RPG_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER) != 0) {
      List<String> lore;
      if (durability.hasLore()) {
        lore = durability.getLore();
      } else {
        lore = new ArrayList<>();
      }

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
        StringBuilder logEntry = new StringBuilder();
        String time = ZonedDateTime.now(ZoneId.of("America/New_York")).format(DateTimeFormatter.ofPattern("hh:mm"));
        logEntry.append(time).append(" ").append(defender.getName()).append("DC/IC");
        Plugin.getData().getPluginLogger().addEntry(logEntry.toString());
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
   * Repairs an item's durability.
   * <p>
   * If the item has {@link Key#RPG_DURABILITY}, the reinforcement value is repaired first.
   *
   * @param defender  defending entity
   * @param equipment entity equipment
   * @param eSlot     equipment slot
   * @param repair    durability repair
   */
  public static void decreaseDamage(@NotNull LivingEntity defender, @NotNull EntityEquipment equipment, @NotNull EquipmentSlot eSlot, int repair) {
    Objects.requireNonNull(defender, "Null defender");
    Objects.requireNonNull(equipment, "Null equipment");
    Objects.requireNonNull(eSlot, "Null slot");

    ItemStack item = equipment.getItem(eSlot);
    if (ItemReader.isNullOrAir(item) || !(item.getItemMeta() instanceof Damageable durability)) {
      return;
    }

    PersistentDataContainer itemTags = durability.getPersistentDataContainer();
    boolean hasReinforcementTags = itemTags.has(Key.RPG_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER) && itemTags.has(Key.RPG_MAX_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER);
    if (hasReinforcementTags && itemTags.get(Key.RPG_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER) != itemTags.get(Key.RPG_MAX_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER)) {
      List<String> lore;
      if (durability.hasLore()) {
        lore = durability.getLore();
      } else {
        lore = new ArrayList<>();
      }

      int reinforcement = itemTags.get(Key.RPG_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER);
      int maxReinforcement = itemTags.get(Key.RPG_MAX_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER);

      reinforcement = reinforcement + repair;
      if (reinforcement < maxReinforcement) {
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
        itemTags.set(Key.RPG_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER, maxReinforcement);

        if (lore.isEmpty()) {
          lore.add(ChatColor.WHITE + "Reinforcement: " + maxReinforcement + " / " + maxReinforcement);
        } else {
          lore.set(lore.size() - 1, ChatColor.WHITE + "Reinforcement: " + maxReinforcement + " / " + maxReinforcement);
        }

        durability.setLore(lore);
        repair = reinforcement - maxReinforcement;
      }
    }

    durability.setDamage(Math.max(0, durability.getDamage() - repair));
    item.setItemMeta(durability);

    if (!(defender instanceof Player)) {
      equipment.setItem(eSlot, item);
    }
  }
}
