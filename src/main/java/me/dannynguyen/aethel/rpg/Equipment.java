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
import me.dannynguyen.aethel.utils.entity.HealthChange;
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
 * @version 1.26.10
 * @since 1.13.4
 */
public class Equipment {
  /**
   * Player's UUID.
   */
  private final UUID uuid;

  /**
   * Player's persistent tags.
   */
  private final PersistentDataContainer entityTags;

  /**
   * {@link AethelAttributes}
   */
  private final AethelAttributes attributes = new AethelAttributes();

  /**
   * {@link Enchantments}
   */
  private final Enchantments enchantments = new Enchantments();

  /**
   * {@link Abilities}
   */
  private final Abilities abilities = new Abilities();

  /**
   * Jewelry slots.
   * <ul>
   *   <li>Necklace
   *   <li>Ring
   *   <li>Trinket
   * </ul>
   */
  private final ItemStack[] jewelry = new ItemStack[3];

  /**
   * Held itemstack.
   * <p>
   * Updated when items are held to determine
   * if to update equipment when items are dropped.
   */
  private ItemStack heldItem;

  /**
   * Associates RPG equipment with a player.
   *
   * @param player interacting player
   */
  public Equipment(@NotNull Player player) {
    this.uuid = Objects.requireNonNull(player, "Null player").getUniqueId();
    this.entityTags = player.getPersistentDataContainer();
    this.heldItem = player.getInventory().getItemInMainHand();
    loadJewelrySlots();
    loadEquipment(player);
  }

  /**
   * Loads the player's equipped jewelry from a file if it exists.
   */
  private void loadJewelrySlots() {
    File file = new File(Directory.JEWELRY.getFile().getPath() + "/" + uuid.toString() + "_jwl.txt");
    if (!file.exists()) {
      return;
    }

    try {
      Scanner scanner = new Scanner(file);
      jewelry[0] = ItemReader.decodeItem(scanner.nextLine());
      jewelry[1] = ItemReader.decodeItem(scanner.nextLine());
      jewelry[2] = ItemReader.decodeItem(scanner.nextLine());
      scanner.close();
    } catch (IOException ex) {
      Bukkit.getLogger().warning(Message.UNABLE_TO_READ_FILE.getMessage() + file.getName());
    }
  }

  /**
   * Loads the player's equipment-related {@link Key Aethel tags}.
   *
   * @param player interacting player
   */
  public void loadEquipment(@NotNull Player player) {
    Objects.requireNonNull(player, "Null player");

    // TODO: Temporary fix for old stuck data
    List<NamespacedKey> tagsToRemove = new ArrayList<>();
    for (NamespacedKey entityTag : player.getPersistentDataContainer().getKeys()) {
      if (entityTag.getKey().startsWith("aethel.attribute.") || entityTag.getKey().startsWith("aethel.enchantment.")) {
        tagsToRemove.add(entityTag);
      }
    }
    for (NamespacedKey entityTag : tagsToRemove) {
      player.getPersistentDataContainer().remove(entityTag);
    }

    PlayerInventory pInv = player.getInventory();
    loadSlot(pInv.getHelmet(), RpgEquipmentSlot.HEAD);
    loadSlot(pInv.getChestplate(), RpgEquipmentSlot.CHEST);
    loadSlot(pInv.getLeggings(), RpgEquipmentSlot.LEGS);
    loadSlot(pInv.getBoots(), RpgEquipmentSlot.FEET);
    loadSlot(pInv.getItemInMainHand(), RpgEquipmentSlot.HAND);
    loadSlot(pInv.getItemInOffHand(), RpgEquipmentSlot.OFF_HAND);
    loadSlot(jewelry[0], RpgEquipmentSlot.NECKLACE);
    loadSlot(jewelry[1], RpgEquipmentSlot.RING);
  }

  /**
   * Checks if the item has {@link Key#ATTRIBUTE_LIST Aethel attributes},
   * enchantments, {@link Key#PASSIVE_LIST passive abilities},
   * and {@link Key#ACTIVE_EQUIPMENT_LIST active abilities} before checking
   * whether the item is in the correct equipment slot.
   *
   * @param item  interacting item
   * @param eSlot {@link RpgEquipmentSlot}
   */
  private void loadSlot(ItemStack item, RpgEquipmentSlot eSlot) {
    if (ItemReader.isNullOrAir(item)) {
      return;
    }

    PersistentDataContainer itemTags = item.getItemMeta().getPersistentDataContainer();
    if (itemTags.has(Key.ATTRIBUTE_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
      attributes.getSlotAttributes().put(eSlot, new HashMap<>());
      attributes.readAttributes(eSlot, itemTags, false);
    }
    if (item.getItemMeta().hasEnchants()) {
      enchantments.getSlotEnchantments().put(eSlot, new HashMap<>());
      enchantments.addEnchantments(eSlot, item, false);
    }
    if (itemTags.has(Key.PASSIVE_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
      abilities.getSlotPassives().put(eSlot, new ArrayList<>());
      abilities.readPassives(eSlot, itemTags);
    }
    if (itemTags.has(Key.ACTIVE_EQUIPMENT_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
      abilities.getTriggerActives().put(eSlot, new ArrayList<>());
      abilities.readActives(eSlot, itemTags);
    }
  }

  /**
   * Checks if the item has {@link Key#ATTRIBUTE_LIST Aethel attributes},
   * enchantments, {@link Key#PASSIVE_LIST passive abilities},
   * and {@link Key#ACTIVE_EQUIPMENT_LIST active abilities} before checking
   * whether the item is in the correct equipment slot.
   * <p>
   * A 2 tick delay is added to max health updates due to items containing
   * Minecraft's Generic Health attribute requiring 1 tick to update.
   *
   * @param item  interacting item
   * @param eSlot {@link RpgEquipmentSlot}
   */
  public void readSlot(@Nullable ItemStack item, @NotNull RpgEquipmentSlot eSlot) {
    Objects.requireNonNull(eSlot, "Null RPG equipment slot");
    if (ItemReader.isNotNullOrAir(item)) {
      removeSlotData(eSlot);
      setSlotData(eSlot, item);
    } else {
      removeSlotData(eSlot);
    }
    Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> new HealthChange(Bukkit.getPlayer(uuid)).updateDisplays(), 2);
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
      attributes.readAttributes(eSlot, itemTags, false);
    }
    if (item.getItemMeta().hasEnchants()) {
      enchantments.getSlotEnchantments().put(eSlot, new HashMap<>());
      enchantments.addEnchantments(eSlot, item, false);
    }
    if (itemTags.has(Key.PASSIVE_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
      abilities.getSlotPassives().put(eSlot, new ArrayList<>());
      abilities.readPassives(eSlot, itemTags);
    }
    if (itemTags.has(Key.ACTIVE_EQUIPMENT_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
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
   * Gets the held item material.
   *
   * @return held item material
   */
  @NotNull
  public ItemStack getHeldItem() {
    return this.heldItem;
  }

  /**
   * Sets the held item.
   *
   * @param item held item
   */
  public void setHeldItem(@NotNull ItemStack item) {
    this.heldItem = Objects.requireNonNull(item, "Null item");
  }

  /**
   * Represents an {@link RpgPlayer}'s {@link Equipment} {@link AethelAttribute} values.
   *
   * @author Danny Nguyen
   * @version 1.25.9
   * @since 1.17.9
   */
  public class AethelAttributes {
    /**
     * {@link AethelAttribute} values on {@link RpgEquipmentSlot}.
     */
    private final Map<RpgEquipmentSlot, Map<AethelAttribute, Double>> slotAttributes = new HashMap<>();

    /**
     * No parameter constructor.
     */
    private AethelAttributes() {
    }

    /**
     * Checks if the item is in the correct {@link RpgEquipmentSlot}
     * before updating the player's {@link AethelAttribute} values.
     *
     * @param eSlot      {@link RpgEquipmentSlot}
     * @param itemTags   item's persistent tags
     * @param alreadySet if the attribute has already been set to tags, true only on equipment initialization
     */
    private void readAttributes(RpgEquipmentSlot eSlot, PersistentDataContainer itemTags, boolean alreadySet) {
      String[] slotAttributes = itemTags.get(Key.ATTRIBUTE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ");
      for (String slotAttribute : slotAttributes) {
        RpgEquipmentSlot slot = RpgEquipmentSlot.valueOf(TextFormatter.formatEnum(slotAttribute.substring(0, slotAttribute.indexOf("."))));
        if (slot == eSlot) {
          addAttributes(eSlot, itemTags, slotAttribute, alreadySet);
        }
      }
    }

    /**
     * Adds new {@link Equipment} {@link AethelAttribute} modifiers.
     *
     * @param eSlot         {@link RpgEquipmentSlot}
     * @param itemContainer item's persistent tags
     * @param slotAttribute attribute modifier
     * @param alreadySet    if the attribute has already been set to tags, true only on equipment initialization
     */
    private void addAttributes(RpgEquipmentSlot eSlot, PersistentDataContainer itemContainer, String slotAttribute, boolean alreadySet) {
      NamespacedKey itemAttributeKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + slotAttribute);
      String attributeId = slotAttribute.substring(slotAttribute.indexOf(".") + 1);
      AethelAttribute attributeType = AethelAttribute.valueOf(TextFormatter.formatEnum(attributeId));
      double itemAttributeValue = itemContainer.get(itemAttributeKey, PersistentDataType.DOUBLE);
      slotAttributes.get(eSlot).put(attributeType, itemAttributeValue);

      if (!alreadySet) {
        NamespacedKey entityAttributeKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + attributeId);
        double entityAttributeValue = entityTags.getOrDefault(entityAttributeKey, PersistentDataType.DOUBLE, 0.0);
        entityTags.set(entityAttributeKey, PersistentDataType.DOUBLE, entityAttributeValue + itemAttributeValue);
      }
    }

    /**
     * Removes existing {@link Equipment} {@link AethelAttribute} modifiers at a {@link RpgEquipmentSlot}.
     *
     * @param eSlot {@link RpgEquipmentSlot}
     */
    public void removeAttributes(@NotNull RpgEquipmentSlot eSlot) {
      Objects.requireNonNull(eSlot, "Null slot");
      for (AethelAttribute attribute : slotAttributes.get(eSlot).keySet()) {
        NamespacedKey entityAttributeKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + attribute.getId());
        double entityAttributeValue = entityTags.getOrDefault(entityAttributeKey, PersistentDataType.DOUBLE, 0.0);

        if (entityAttributeValue - slotAttributes.get(eSlot).get(attribute) != 0.0) {
          entityTags.set(entityAttributeKey, PersistentDataType.DOUBLE, entityAttributeValue - slotAttributes.get(eSlot).get(attribute));
        } else {
          entityTags.remove(entityAttributeKey);
        }
      }
      slotAttributes.get(eSlot).clear();
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
   * @version 1.25.9
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
     * Enchantments on {@link RpgEquipmentSlot}.
     */
    private final Map<RpgEquipmentSlot, Map<Enchantment, Integer>> slotEnchantments = new HashMap<>();

    /**
     * No parameter constructor.
     */
    private Enchantments() {
    }

    /**
     * Adds new {@link Equipment} enchantments.
     *
     * @param eSlot      {@link RpgEquipmentSlot}
     * @param item       interacting item
     * @param alreadySet if the attribute has already been set to tags, true only on equipment initialization
     */
    private void addEnchantments(RpgEquipmentSlot eSlot, ItemStack item, boolean alreadySet) {
      for (Enchantment enchantment : trackedEnchantments) {
        int itemEnchantmentValue = item.getEnchantmentLevel(enchantment);
        if (itemEnchantmentValue == 0) {
          continue;
        }

        slotEnchantments.get(eSlot).put(enchantment, itemEnchantmentValue);

        if (!alreadySet) {
          NamespacedKey entityEnchantmentKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.ENCHANTMENT.getHeader() + TextFormatter.formatId(enchantment.getKey().getKey()));
          int entityEnchantmentValue = entityTags.getOrDefault(entityEnchantmentKey, PersistentDataType.INTEGER, 0);
          entityTags.set(entityEnchantmentKey, PersistentDataType.INTEGER, entityEnchantmentValue + itemEnchantmentValue);
        }
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
      Objects.requireNonNull(eSlot, "Null slot");
      for (Enchantment enchantment : slotEnchantments.get(eSlot).keySet()) {
        NamespacedKey entityEnchantmentKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.ENCHANTMENT.getHeader() + TextFormatter.formatId(enchantment.getKey().getKey()));
        int entityEnchantmentValue = entityTags.getOrDefault(entityEnchantmentKey, PersistentDataType.INTEGER, 0);

        if (entityEnchantmentValue - slotEnchantments.get(eSlot).get(enchantment) != 0.0) {
          entityTags.set(entityEnchantmentKey, PersistentDataType.INTEGER, entityEnchantmentValue - slotEnchantments.get(eSlot).get(enchantment));
        } else {
          entityTags.remove(entityEnchantmentKey);
        }
      }
      slotEnchantments.get(eSlot).clear();

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
      NamespacedKey entityEnchantmentKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.ENCHANTMENT.getHeader() + TextFormatter.formatEnum(enchantment.getKey().getKey()));
      int entityEnchantmentValue = entityTags.getOrDefault(entityEnchantmentKey, PersistentDataType.INTEGER, 0);

      if (entityEnchantmentValue >= requirement) {
        Plugin.getData().getRpgSystem().getSufficientEnchantments().get(enchantment).add(uuid);
      } else {
        Plugin.getData().getRpgSystem().getSufficientEnchantments().get(enchantment).remove(uuid);
      }
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
   * @version 1.25.9
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
    private void readPassives(RpgEquipmentSlot eSlot, PersistentDataContainer itemTags) {
      String[] passives = itemTags.get(Key.PASSIVE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ");
      for (String passive : passives) {
        RpgEquipmentSlot slot = RpgEquipmentSlot.valueOf(TextFormatter.formatEnum(passive.substring(0, passive.indexOf("."))));
        if (slot == eSlot) {
          addPassives(eSlot, itemTags, passive);
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
     * Removes existing {@link Equipment} {@link PassiveAbility passive abilities} at an {@link RpgEquipmentSlot}.
     *
     * @param eSlot {@link RpgEquipmentSlot}
     */
    public void removePassives(@NotNull RpgEquipmentSlot eSlot) {
      Objects.requireNonNull(eSlot, "Null slot");
      for (TriggerPassive triggerPassive : slotPassives.get(eSlot)) {
        triggerPassives.get(triggerPassive.trigger()).remove(new SlotPassive(eSlot, triggerPassive.ability()));
      }
      slotPassives.get(eSlot).clear();
    }

    /**
     * Checks if the item is in the correct {@link RpgEquipmentSlot}
     * before updating the player's {@link ActiveAbility active abilities}.
     *
     * @param eSlot    {@link RpgEquipmentSlot}
     * @param itemTags item's persistent tags
     */
    private void readActives(RpgEquipmentSlot eSlot, PersistentDataContainer itemTags) {
      String[] actives = itemTags.get(Key.ACTIVE_EQUIPMENT_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ");
      for (String active : actives) {
        RpgEquipmentSlot slot = RpgEquipmentSlot.valueOf(TextFormatter.formatEnum(active.substring(0, active.indexOf("."))));
        if (slot == eSlot) {
          addActives(eSlot, itemTags, active);
        }
      }
    }

    /**
     * Adds new {@link Equipment} {@link ActiveAbility active abilities}.
     *
     * @param eSlot    {@link RpgEquipmentSlot}
     * @param itemTags item's persistent tags
     * @param active   {@link ActiveAbility} data
     */
    private void addActives(RpgEquipmentSlot eSlot, PersistentDataContainer itemTags, String active) {
      NamespacedKey activeKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.ACTIVE_EQUIPMENT.getHeader() + active);
      String[] abilityMeta = active.split("\\.");
      ActiveAbilityType activeAbilityType = ActiveAbilityType.valueOf(TextFormatter.formatEnum(abilityMeta[1]));
      triggerActives.get(eSlot).add(new ActiveAbility(onCooldownActives, eSlot, activeAbilityType, itemTags.get(activeKey, PersistentDataType.STRING).split(" ")));
    }

    /**
     * Removes existing {@link Equipment} {@link ActiveAbility active abilities} at an {@link RpgEquipmentSlot}.
     *
     * @param eSlot {@link RpgEquipmentSlot}
     */
    public void removeActives(@NotNull RpgEquipmentSlot eSlot) {
      Objects.requireNonNull(eSlot, "Null slot");
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
     * @version 1.24.7
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
     * @version 1.24.7
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
    }
  }
}
