package me.dannynguyen.aethel.rpg;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Directory;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.plugin.KeyHeader;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.enums.rpg.AethelAttribute;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType;
import me.dannynguyen.aethel.listeners.EquipmentListener;
import me.dannynguyen.aethel.rpg.abilities.ActiveAbility;
import me.dannynguyen.aethel.rpg.abilities.PassiveAbility;
import me.dannynguyen.aethel.utils.TextFormatter;
import me.dannynguyen.aethel.utils.item.ItemCreator;
import me.dannynguyen.aethel.utils.item.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
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
 * @version 1.22.11
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
   * @param player interacting player
   */
  public Equipment(@NotNull Player player) {
    this.uuid = Objects.requireNonNull(player, "Null player").getUniqueId();
    this.attributes = new AethelAttributes(player.getPersistentDataContainer());
    this.enchantments = new Enchantments();
    this.abilities = new Abilities();
    this.heldItem = player.getInventory().getItemInMainHand();
    loadJewelrySlots();
    loadEquipment(player);
  }

  /**
   * Loads the player's equipped jewelry from a file if it exists.
   */
  private void loadJewelrySlots() {
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
   * Loads the player's equipment-related {@link Key Aethel tags}.
   *
   * @param player interacting player
   */
  private void loadEquipment(Player player) {
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
    PersistentDataContainer itemTags = item.getItemMeta().getPersistentDataContainer();
    if (itemTags.has(Key.ATTRIBUTE_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
      attributes.getSlotAttributes().put(eSlot, new HashMap<>());
      attributes.readAttributes(eSlot, itemTags);
    }
    if (item.getItemMeta().hasEnchants()) {
      enchantments.getSlotEnchantments().put(eSlot, new HashMap<>());
      enchantments.addEnchantments(eSlot, item);
    }
    if (itemTags.has(Key.PASSIVE_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
      abilities.getSlotPassives().put(eSlot, new ArrayList<>());
      abilities.readPassives(eSlot, itemTags);
    }
    if (itemTags.has(Key.ACTIVE_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
      abilities.getTriggerActives().put(eSlot, new ArrayList<>());
      abilities.readActives(eSlot, itemTags);
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
   * Gets the {@link AethelAttributes}.
   *
   * @return {@link AethelAttributes}
   */
  @NotNull
  public AethelAttributes getAttributes() {
    return this.attributes;
  }

  /**
   * Gets the {@link Enchantments}.
   *
   * @return {@link Enchantments}
   */
  @NotNull
  public Enchantments getEnchantments() {
    return this.enchantments;
  }

  /**
   * Gets the {@link Abilities}.
   *
   * @return {@link Abilities}
   */
  @NotNull
  public Abilities getAbilities() {
    return this.abilities;
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

  /**
   * Represents an {@link RpgPlayer}'s equipment {@link AethelAttribute} values.
   *
   * @author Danny Nguyen
   * @version 1.22.7
   * @since 1.17.9
   */
  public static class AethelAttributes {
    /**
     * Player's persistent tags.
     */
    private final PersistentDataContainer playerTags;

    /**
     * {@link AethelAttribute} values on {@link RpgEquipmentSlot}.
     */
    private final Map<RpgEquipmentSlot, Map<AethelAttribute, Double>> slotAttributes = new HashMap<>();

    /**
     * Associates the player's persistent tags with Aethel attributes.
     *
     * @param playerTags player's persistent tags
     */
    private AethelAttributes(@NotNull PersistentDataContainer playerTags) {
      this.playerTags = playerTags;
    }

    /**
     * Checks if the item is in the correct {@link RpgEquipmentSlot}
     * before updating the player's {@link AethelAttribute} values.
     *
     * @param eSlot    {@link RpgEquipmentSlot}
     * @param itemTags item's persistent tags
     */
    private void readAttributes(@NotNull RpgEquipmentSlot eSlot, @NotNull PersistentDataContainer itemTags) {
      String[] slotAttributes = Objects.requireNonNull(itemTags, "Null data container").get(Key.ATTRIBUTE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ");
      for (String slotAttribute : slotAttributes) {
        RpgEquipmentSlot slot = RpgEquipmentSlot.valueOf(TextFormatter.formatEnum(slotAttribute.substring(0, slotAttribute.indexOf("."))));
        if (slot == Objects.requireNonNull(eSlot, "Null slot")) {
          addAttributes(eSlot, itemTags, slotAttribute);
        }
      }
    }

    /**
     * Adds new {@link Equipment} {@link AethelAttribute} modifiers.
     *
     * @param eSlot         {@link RpgEquipmentSlot}
     * @param itemContainer item's persistent tags
     * @param slotAttribute attribute modifier
     */
    private void addAttributes(RpgEquipmentSlot eSlot, PersistentDataContainer itemContainer, String slotAttribute) {
      NamespacedKey itemAttributeKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + slotAttribute);
      String attributeId = slotAttribute.substring(slotAttribute.indexOf(".") + 1);
      AethelAttribute attributeType = AethelAttribute.valueOf(TextFormatter.formatEnum(attributeId));
      double itemAttributeValue = itemContainer.get(itemAttributeKey, PersistentDataType.DOUBLE);
      slotAttributes.get(eSlot).put(attributeType, itemAttributeValue);

      NamespacedKey entityAttributeKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + attributeId);
      double entityAttributeValue = playerTags.getOrDefault(entityAttributeKey, PersistentDataType.DOUBLE, 0.0);
      playerTags.set(entityAttributeKey, PersistentDataType.DOUBLE, entityAttributeValue + itemAttributeValue);
    }

    /**
     * Removes existing {@link Equipment} {@link AethelAttribute} modifiers at a {@link RpgEquipmentSlot}.
     *
     * @param eSlot {@link RpgEquipmentSlot}
     */
    public void removeAttributes(@NotNull RpgEquipmentSlot eSlot) {
      for (AethelAttribute attribute : slotAttributes.get(Objects.requireNonNull(eSlot, "Null slot")).keySet()) {
        NamespacedKey entityAttributeKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + attribute.getId());
        double entityAttributeValue = playerTags.getOrDefault(entityAttributeKey, PersistentDataType.DOUBLE, 0.0);
        playerTags.set(entityAttributeKey, PersistentDataType.DOUBLE, entityAttributeValue - slotAttributes.get(eSlot).get(attribute));
        slotAttributes.get(eSlot).put(attribute, 0.0);
      }
    }

    /**
     * Gets {@link Equipment} {@link AethelAttribute} values on {@link RpgEquipmentSlot}.
     *
     * @return {@link Equipment} {@link AethelAttribute} values on {@link RpgEquipmentSlot}
     */
    @NotNull
    public Map<RpgEquipmentSlot, Map<AethelAttribute, Double>> getSlotAttributes() {
      return this.slotAttributes;
    }
  }

  /**
   * Represents an {@link RpgPlayer}'s equipment enchantments.
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
     * No parameter constructor.
     */
    private Enchantments() {
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
    private void addEnchantments(@NotNull RpgEquipmentSlot eSlot, @NotNull ItemStack item) {
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
     * Gets the total enchantments.
     *
     * @return total enchantments
     */
    @NotNull
    public Map<Enchantment, Integer> getTotalEnchantments() {
      return this.totalEnchantments;
    }

    /**
     * Gets the {@link Equipment} enchantments on {@link RpgEquipmentSlot}.
     *
     * @return enchantments on {@link RpgEquipmentSlot}
     */
    @NotNull
    public Map<RpgEquipmentSlot, Map<Enchantment, Integer>> getSlotEnchantments() {
      return this.slotEnchantments;
    }
  }

  /**
   * Represents an {@link RpgPlayer}'s {@link Equipment}
   * {@link PassiveAbility passive} and {@link ActiveAbility active} abilities.
   *
   * @author Danny Nguyen
   * @version 1.19.6
   * @since 1.17.9
   */
  public class Abilities {
    /**
     * Used to remove abilities upon {@link EquipmentListener}.
     */
    private final Map<RpgEquipmentSlot, List<TriggerPassive>> slotPassives = new HashMap<>();

    /**
     * Used to identify unique {@link PassiveAbility passive abilities}
     * after a {@link PassiveTriggerType} is called.
     */
    private final Map<PassiveTriggerType, Map<SlotPassive, PassiveAbility>> triggerPassives = createPassiveTriggers();

    /**
     * {@link SlotPassive Passive abilities} on cooldown.
     */
    private final Map<PassiveTriggerType, Set<SlotPassive>> onCooldownPassives = createPassiveCooldownTriggers();

    /**
     * {@link ActiveAbility Active abilities} identified by their {@link RpgEquipmentSlot} trigger.
     */
    private final Map<RpgEquipmentSlot, List<ActiveAbility>> triggerActives = createActiveTriggers();

    /**
     * {@link ActiveAbilityType Active abilities} on cooldown.
     */
    private final Map<RpgEquipmentSlot, Set<ActiveAbilityType>> onCooldownActives = createActiveCooldownTriggers();

    /**
     * No parameter constructor.
     */
    private Abilities() {
    }

    /**
     * Creates a blank map of {@link PassiveTriggerType triggerable} {@link PassiveAbility passive abilities}.
     *
     * @return blank map of {@link PassiveTriggerType triggerable} {@link PassiveAbility passive abilities}
     */
    private Map<PassiveTriggerType, Map<SlotPassive, PassiveAbility>> createPassiveTriggers() {
      Map<PassiveTriggerType, Map<SlotPassive, PassiveAbility>> triggers = new HashMap<>();
      for (PassiveTriggerType passiveTriggerType : PassiveTriggerType.values()) {
        triggers.put(passiveTriggerType, new HashMap<>());
      }
      return triggers;
    }

    /**
     * Creates a blank map of {@link PassiveTriggerType triggerable}
     * {@link SlotPassive passive abilities} on cooldown.
     *
     * @return blank map of {@link PassiveTriggerType triggerable} {@link SlotPassive passive abilities} on cooldown
     */
    private Map<PassiveTriggerType, Set<SlotPassive>> createPassiveCooldownTriggers() {
      Map<PassiveTriggerType, Set<SlotPassive>> triggers = new HashMap<>();
      for (PassiveTriggerType passiveTriggerType : PassiveTriggerType.values()) {
        triggers.put(passiveTriggerType, new HashSet<>());
      }
      return triggers;
    }

    /**
     * Creates a blank map of {@link RpgEquipmentSlot triggerable} {@link ActiveAbility active abilities}.
     *
     * @return blank map of {@link RpgEquipmentSlot triggerable} {@link ActiveAbility active abilities}
     */
    private Map<RpgEquipmentSlot, List<ActiveAbility>> createActiveTriggers() {
      Map<RpgEquipmentSlot, List<ActiveAbility>> triggers = new HashMap<>();
      for (RpgEquipmentSlot slot : RpgEquipmentSlot.values()) {
        triggers.put(slot, new ArrayList<>());
      }
      return triggers;
    }

    /**
     * Creates a blank map of {@link RpgEquipmentSlot triggerable}
     * {@link ActiveAbilityType active abilities} on cooldown.
     *
     * @return blank map of {@link RpgEquipmentSlot triggerable} {@link ActiveAbilityType active abilities} on cooldown
     */
    private Map<RpgEquipmentSlot, Set<ActiveAbilityType>> createActiveCooldownTriggers() {
      Map<RpgEquipmentSlot, Set<ActiveAbilityType>> triggers = new HashMap<>();
      for (RpgEquipmentSlot slot : RpgEquipmentSlot.values()) {
        triggers.put(slot, new HashSet<>());
      }
      return triggers;
    }

    /**
     * Checks if the item is in the correct {@link RpgEquipmentSlot}
     * before updating the player's {@link PassiveAbility passive abilities}.
     *
     * @param eSlot    {@link RpgEquipmentSlot}
     * @param itemTags item's persistent tags
     */
    private void readPassives(@NotNull RpgEquipmentSlot eSlot, @NotNull PersistentDataContainer itemTags) {
      String[] passives = Objects.requireNonNull(itemTags, "Null data container").get(Key.PASSIVE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ");
      for (String passive : passives) {
        RpgEquipmentSlot slot = RpgEquipmentSlot.valueOf(TextFormatter.formatEnum(passive.substring(0, passive.indexOf("."))));
        if (slot == Objects.requireNonNull(eSlot, "Null slot")) {
          addPassives(eSlot, itemTags, passive);
        }
      }
    }

    /**
     * Checks if the item is in the correct {@link RpgEquipmentSlot}
     * before updating the player's {@link ActiveAbility active abilities}.
     *
     * @param eSlot    {@link RpgEquipmentSlot}
     * @param itemTags item's persistent tags
     */
    private void readActives(@NotNull RpgEquipmentSlot eSlot, @NotNull PersistentDataContainer itemTags) {
      String[] actives = Objects.requireNonNull(itemTags, "Null data container").get(Key.ACTIVE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ");
      for (String active : actives) {
        RpgEquipmentSlot slot = RpgEquipmentSlot.valueOf(TextFormatter.formatEnum(active.substring(0, active.indexOf("."))));
        if (slot == Objects.requireNonNull(eSlot, "Null slot")) {
          addActives(eSlot, itemTags, active);
        }
      }
    }

    /**
     * Adds new {@link Equipment} {@link PassiveAbility passive abilities}.
     *
     * @param eSlot    {@link RpgEquipmentSlot}
     * @param itemTags item's persistent tags
     * @param passive  {@link PassiveAbility} data
     */
    private void addPassives(RpgEquipmentSlot eSlot, PersistentDataContainer itemTags, String passive) {
      NamespacedKey passiveKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.PASSIVE.getHeader() + passive);
      String[] abilityMeta = passive.split("\\.");
      PassiveTriggerType passiveTriggerType = PassiveTriggerType.valueOf(TextFormatter.formatEnum(abilityMeta[1]));
      PassiveAbilityType abilityType = PassiveAbilityType.valueOf(TextFormatter.formatEnum(abilityMeta[2]));
      slotPassives.get(eSlot).add(new TriggerPassive(passiveTriggerType, abilityType));
      triggerPassives.get(passiveTriggerType).put(new Abilities.SlotPassive(eSlot, abilityType), new PassiveAbility(onCooldownPassives, eSlot, passiveTriggerType, abilityType, itemTags.get(passiveKey, PersistentDataType.STRING).split(" ")));
    }

    /**
     * Adds new {@link Equipment} {@link ActiveAbility active abilities}.
     *
     * @param eSlot    {@link RpgEquipmentSlot}
     * @param itemTags item's persistent tags
     * @param active   {@link ActiveAbility} data
     */
    private void addActives(RpgEquipmentSlot eSlot, PersistentDataContainer itemTags, String active) {
      NamespacedKey activeKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.ACTIVE.getHeader() + active);
      String[] abilityMeta = active.split("\\.");
      ActiveAbilityType activeAbilityType = ActiveAbilityType.valueOf(TextFormatter.formatEnum(abilityMeta[1]));
      triggerActives.get(eSlot).add(new ActiveAbility(onCooldownActives, eSlot, activeAbilityType, itemTags.get(activeKey, PersistentDataType.STRING).split(" ")));
    }

    /**
     * Removes existing {@link Equipment} {@link PassiveAbility passive abilities} at an {@link RpgEquipmentSlot}.
     *
     * @param eSlot {@link RpgEquipmentSlot}
     */
    public void removePassives(@NotNull RpgEquipmentSlot eSlot) {
      List<TriggerPassive> abilitiesToRemove = new ArrayList<>();
      for (TriggerPassive triggerPassive : slotPassives.get(Objects.requireNonNull(eSlot, "Null slot"))) {
        triggerPassives.get(triggerPassive.trigger()).remove(new SlotPassive(eSlot, triggerPassive.ability()));
        abilitiesToRemove.add(triggerPassive);
      }
      slotPassives.get(eSlot).removeAll(abilitiesToRemove);
    }

    /**
     * Removes existing {@link Equipment} {@link ActiveAbility active abilities} at an {@link RpgEquipmentSlot}.
     *
     * @param eSlot {@link RpgEquipmentSlot}
     */
    public void removeActives(@NotNull RpgEquipmentSlot eSlot) {
      triggerActives.get(eSlot).clear();
    }

    /**
     * Gets the player's {@link TriggerPassive passive abilities}
     * that exist on each {@link RpgEquipmentSlot}.
     *
     * @return {@link TriggerPassive passive abilities} that exist on each {@link RpgEquipmentSlot}
     */
    @NotNull
    public Map<RpgEquipmentSlot, List<TriggerPassive>> getSlotPassives() {
      return this.slotPassives;
    }

    /**
     * Gets the player's {@link PassiveTriggerType triggerable} {@link PassiveAbility passive abilities}.
     *
     * @return {@link PassiveTriggerType triggerable} {@link PassiveAbility passive abilities}
     */
    @NotNull
    public Map<PassiveTriggerType, Map<SlotPassive, PassiveAbility>> getTriggerPassives() {
      return this.triggerPassives;
    }

    /**
     * Gets the player's {@link RpgEquipmentSlot} {@link ActiveAbility active abilities}
     * triggered by binds.
     *
     * @return {@link RpgEquipmentSlot} {@link ActiveAbility active abilities} triggered by binds
     */
    @NotNull
    public Map<RpgEquipmentSlot, List<ActiveAbility>> getTriggerActives() {
      return this.triggerActives;
    }

    /**
     * Represents an {@link RpgEquipmentSlot} {@link PassiveAbilityType} pair.
     * <p>
     * Used to identify unique {@link PassiveAbility passive abilities}
     * after a {@link PassiveTriggerType} is called.
     *
     * @param eSlot {@link RpgEquipmentSlot}
     * @param type  {@link PassiveAbilityType}
     * @author Danny Nguyen
     * @version 1.18.1
     * @since 1.16.3
     */
    public record SlotPassive(@NotNull RpgEquipmentSlot eSlot, @NotNull PassiveAbilityType type) {
      /**
       * Associates an {@link RpgEquipmentSlot} with an {@link PassiveAbilityType}.
       *
       * @param eSlot {@link RpgEquipmentSlot}
       * @param type  {@link PassiveAbilityType}
       */
      public SlotPassive(@NotNull RpgEquipmentSlot eSlot, @NotNull PassiveAbilityType type) {
        this.eSlot = Objects.requireNonNull(eSlot, "Null slot");
        this.type = Objects.requireNonNull(type, "Null ability");
      }

      /**
       * Gets the {@link RpgEquipmentSlot}.
       *
       * @return {@link RpgEquipmentSlot}
       */
      @Override
      @NotNull
      public RpgEquipmentSlot eSlot() {
        return this.eSlot;
      }

      /**
       * Gets the {@link PassiveAbilityType}.
       *
       * @return {@link PassiveAbilityType}
       */
      @NotNull
      public PassiveAbilityType type() {
        return this.type;
      }

      /**
       * Returns true if the slot ability has the same fields.
       *
       * @param o compared object
       * @return if the slot ability has the same fields
       */
      @Override
      public boolean equals(Object o) {
        if (o instanceof SlotPassive slotPassive) {
          return (slotPassive.eSlot() == eSlot && slotPassive.type() == type);
        }
        return false;
      }

      /**
       * Gets the hash value of the slot ability.
       *
       * @return hash value of the slot ability
       */
      @Override
      public int hashCode() {
        return Objects.hash(eSlot, type);
      }
    }

    /**
     * Represents a {@link PassiveTriggerType} {@link PassiveAbilityType} pair.
     * <p>
     * Used to remove abilities upon {@link EquipmentListener}.
     *
     * @param trigger {@link PassiveTriggerType}
     * @param ability {@link PassiveAbilityType}
     * @author Danny Nguyen
     * @version 1.18.1
     * @since 1.16.1
     */
    public record TriggerPassive(@NotNull PassiveTriggerType trigger, @NotNull PassiveAbilityType ability) {
      /**
       * Associates a {@link PassiveTriggerType} with a {@link PassiveAbilityType}.
       *
       * @param trigger {@link PassiveTriggerType}
       * @param ability {@link PassiveAbilityType}
       */
      public TriggerPassive(@NotNull PassiveTriggerType trigger, @NotNull PassiveAbilityType ability) {
        this.trigger = Objects.requireNonNull(trigger, "Null trigger");
        this.ability = Objects.requireNonNull(ability, "Null ability");
      }

      /**
       * Gets the {@link PassiveTriggerType}.
       *
       * @return {@link PassiveTriggerType}
       */
      @NotNull
      public PassiveTriggerType trigger() {
        return this.trigger;
      }

      /**
       * Gets the {@link PassiveAbilityType}.
       *
       * @return {@link PassiveAbilityType}
       */
      @NotNull
      public PassiveAbilityType ability() {
        return this.ability;
      }
    }
  }
}
