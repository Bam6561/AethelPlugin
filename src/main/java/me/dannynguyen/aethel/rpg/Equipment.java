package me.dannynguyen.aethel.rpg;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Directory;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.rpg.abilities.Abilities;
import me.dannynguyen.aethel.utils.item.ItemCreator;
import me.dannynguyen.aethel.utils.item.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Represents an {@link RpgPlayer}'s equipment.
 *
 * @author Danny Nguyen
 * @version 1.19.6
 * @since 1.13.4
 */
public class Equipment {
  /**
   * Player's UUID.
   */
  private final UUID uuid;

  /**
   * {@link AethelAttributes}
   */
  private final AethelAttributes attributes;

  /**
   * {@link Enchantments}
   */
  private final Enchantments enchantments;

  /**
   * {@link Abilities}
   */
  private final Abilities abilities;

  /**
   * Jewelry slots.
   */
  private final ItemStack[] jewelry = new ItemStack[2];

  /**
   * Held item.
   */
  private ItemStack heldItem;

  /**
   * Associates RPG equipment with a player.
   *
   * @param player       interacting player
   * @param attributes   {@link AethelAttributes}
   * @param enchantments {@link Enchantments}
   * @param abilities    {@link Abilities}
   */
  public Equipment(@NotNull Player player, @NotNull AethelAttributes attributes, @NotNull Enchantments enchantments, @NotNull Abilities abilities) {
    this.uuid = Objects.requireNonNull(player, "Null player").getUniqueId();
    this.attributes = Objects.requireNonNull(attributes, "Null Aethel attributes");
    this.enchantments = Objects.requireNonNull(enchantments, "Null enchantments");
    this.abilities = Objects.requireNonNull(abilities, "Null abilities");
    this.heldItem = player.getInventory().getItemInMainHand();
    initializeJewelrySlots();
    initializeEquipment(player);
  }

  /**
   * Initializes the player's equipped jewelry from a file if it exists.
   */
  private void initializeJewelrySlots() {
    File file = new File(Directory.JEWELRY.getFile().getPath() + "/" + uuid.toString() + "_jwl.txt");
    if (file.exists()) {
      try {
        Scanner scanner = new Scanner(file);
        jewelry[0] = ItemReader.decodeItem(scanner.nextLine());
        jewelry[1] = ItemReader.decodeItem(scanner.nextLine());
        scanner.close();
      } catch (IOException ex) {
        Bukkit.getLogger().warning(Message.UNABLE_TO_READ_FILE.getMessage() + file.getName());
      }
    }
  }

  /**
   * Initializes the player's equipment-related {@link Key Aethel tags}.
   *
   * @param player interacting player
   */
  private void initializeEquipment(Player player) {
    PlayerInventory pInv = player.getInventory();
    readSlot(pInv.getItemInMainHand(), RpgEquipmentSlot.HAND, false);
    readSlot(pInv.getItemInOffHand(), RpgEquipmentSlot.OFF_HAND, false);
    readSlot(pInv.getHelmet(), RpgEquipmentSlot.HEAD, false);
    readSlot(pInv.getChestplate(), RpgEquipmentSlot.CHEST, false);
    readSlot(pInv.getLeggings(), RpgEquipmentSlot.LEGS, false);
    readSlot(pInv.getBoots(), RpgEquipmentSlot.FEET, false);
    readSlot(jewelry[0], RpgEquipmentSlot.NECKLACE, false);
    readSlot(jewelry[1], RpgEquipmentSlot.RING, false);
  }

  /**
   * Checks if the item has {@link Key#ATTRIBUTE_LIST Aethel attributes},
   * enchantments, {@link Key#PASSIVE_LIST passive abilities},
   * and {@link Key#ACTIVE_LIST active abilities}
   * before checking whether the item is in the correct equipment slot.
   * <p>
   * A 2 tick delay is added to max health updates due to items containing
   * Minecraft's Generic Health attribute requiring 1 tick to update.
   *
   * @param item            interacting item
   * @param eSlot           {@link RpgEquipmentSlot}
   * @param maxHealthUpdate whether to update the player's max health
   */
  public void readSlot(@Nullable ItemStack item, @NotNull RpgEquipmentSlot eSlot, boolean maxHealthUpdate) {
    Objects.requireNonNull(eSlot, "Null RPG equipment slot");
    if (ItemReader.isNotNullOrAir(item)) {
      removeSlotData(eSlot);
      setSlotData(eSlot, item);
    } else {
      removeSlotData(eSlot);
    }
    if (maxHealthUpdate) {
      Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getHealth().updateMaxHealth(), 2);
    }
  }

  /**
   * Saves the player's jewelry items to a file.
   */
  public void saveJewelry() {
    File file = new File(Directory.JEWELRY.getFile().getPath() + "/" + uuid + "_jwl.txt");
    String encodedJewelry = encodeJewelry();
    try {
      FileWriter fw = new FileWriter(file);
      fw.write(encodedJewelry);
      fw.close();
    } catch (IOException ex) {
      Bukkit.getLogger().warning("[Aethel] Failed to write " + uuid + "'s jewelry items to file.");
    }
  }

  /**
   * Sets new data stored about an {@link RpgEquipmentSlot}.
   *
   * @param eSlot {@link RpgEquipmentSlot}
   * @param item  interacting item
   */
  private void setSlotData(RpgEquipmentSlot eSlot, ItemStack item) {
    PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
    if (dataContainer.has(Key.ATTRIBUTE_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
      attributes.getSlotAttributes().put(eSlot, new HashMap<>());
      attributes.readAttributes(eSlot, dataContainer);
    }
    if (item.getItemMeta().hasEnchants()) {
      enchantments.getSlotEnchantments().put(eSlot, new HashMap<>());
      enchantments.addEnchantments(eSlot, item);
    }
    if (dataContainer.has(Key.PASSIVE_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
      abilities.getSlotPassives().put(eSlot, new ArrayList<>());
      abilities.readPassives(eSlot, dataContainer);
    }
    if (dataContainer.has(Key.ACTIVE_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
      abilities.getTriggerActives().put(eSlot, new ArrayList<>());
      abilities.readActives(eSlot, dataContainer);
    }
  }

  /**
   * Removes all data stored about an {@link RpgEquipmentSlot}.
   *
   * @param eSlot {@link RpgEquipmentSlot}
   */
  private void removeSlotData(RpgEquipmentSlot eSlot) {
    if (attributes.getSlotAttributes().containsKey(eSlot)) {
      attributes.removeAttributes(eSlot);
    }
    if (enchantments.getSlotEnchantments().containsKey(eSlot)) {
      enchantments.removeEnchantments(eSlot);
    }
    if (abilities.getSlotPassives().containsKey(eSlot)) {
      abilities.removePassives(eSlot);
    }
    if (abilities.getTriggerActives().containsKey(eSlot)) {
      abilities.removeActives(eSlot);
    }
  }

  /**
   * Encodes the player's jewelry items.
   *
   * @return encoded jewelry string
   */
  private String encodeJewelry() {
    StringBuilder encodedJewelry = new StringBuilder();
    for (ItemStack jewelrySlot : jewelry) {
      if (ItemReader.isNotNullOrAir(jewelrySlot)) {
        encodedJewelry.append(ItemCreator.encodeItem(jewelrySlot));
      } else {
        encodedJewelry.append("NULL");
      }
      encodedJewelry.append("\n");
    }
    return encodedJewelry.toString();
  }

  /**
   * Gets the player's jewelry.
   *
   * @return jewelry slots
   */
  @Nullable
  public ItemStack[] getJewelry() {
    return this.jewelry;
  }

  /**
   * Gets the player's held item tracked by the {@link RpgSystem}.
   * <p>
   * Held item is not synchronous with in-game events,
   * as it is only updated every 10 tick interval.
   *
   * @return held item tracked by the {@link RpgSystem}
   */
  @NotNull
  public ItemStack getHeldItem() {
    return this.heldItem;
  }

  /**
   * Sets the player's held item tracked by {@link RpgSystem}.
   *
   * @param item item to be set
   */
  public void setHeldItem(@Nullable ItemStack item) {
    this.heldItem = item;
  }
}
