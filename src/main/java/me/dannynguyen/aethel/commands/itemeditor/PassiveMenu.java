package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.plugin.KeyHeader;
import me.dannynguyen.aethel.enums.plugin.PlayerHead;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType;
import me.dannynguyen.aethel.interfaces.Menu;
import me.dannynguyen.aethel.utils.InventoryPages;
import me.dannynguyen.aethel.utils.TextFormatter;
import me.dannynguyen.aethel.utils.item.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents a menu that allows the user to edit an item's
 * {@link Key#PASSIVE_LIST passive abilities}.
 *
 * @author Danny Nguyen
 * @version 1.19.9
 * @since 1.15.1
 */
public class PassiveMenu implements Menu {
  /**
   * GUI.
   */
  private final Inventory menu;

  /**
   * Passive menu {@link Mode}.
   */
  private final Mode mode;

  /**
   * GUI user.
   */
  private final Player user;

  /**
   * ItemStack being edited.
   */
  private final ItemStack item;

  /**
   * {@link RpgEquipmentSlot}
   */
  private final RpgEquipmentSlot eSlot;

  /**
   * {@link PassiveTriggerType}
   */
  private final PassiveTriggerType trigger;

  /**
   * ItemStack data container.
   */
  private final PersistentDataContainer dataContainer;

  /**
   * ItemStack {@link Key#PASSIVE_LIST passive abilities}.
   */
  private final Map<String, List<SlotCondition>> existingPassives;

  /**
   * Associates a new Passive menu with its user and item.
   *
   * @param user    user
   * @param mode    passive menu mode
   * @param trigger {@link PassiveTriggerType}
   * @param eSlot   {@link RpgEquipmentSlot}
   */
  public PassiveMenu(@NotNull Player user, @NotNull PassiveMenu.Mode mode, @NotNull RpgEquipmentSlot eSlot, @NotNull PassiveTriggerType trigger) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.mode = Objects.requireNonNull(mode, "Null mode");
    this.eSlot = Objects.requireNonNull(eSlot, "Null slot");
    this.trigger = Objects.requireNonNull(trigger, "Null trigger");
    this.item = Plugin.getData().getEditedItemCache().getEditedItems().get(user.getUniqueId());
    this.dataContainer = item.getItemMeta().getPersistentDataContainer();
    this.existingPassives = mapPassives();
    this.menu = createMenu();
  }

  /**
   * Creates and names a new Passive menu with its {@link RpgEquipmentSlot}.
   *
   * @return Passive menu
   */
  private Inventory createMenu() {
    Inventory inv = Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "Passives " + ChatColor.DARK_AQUA + eSlot.getProperName() + " " + ChatColor.YELLOW + trigger.getProperName());
    inv.setItem(1, item);
    return inv;
  }

  /**
   * Sets the menu to display interactions with {@link PassiveAbilityType}.
   *
   * @return Passive menu
   */
  @NotNull
  public Inventory getMainMenu() {
    switch (mode) {
      case ABILITIES -> addAbilities();
      case POTION_EFFECTS -> addPotionEffects();
    }
    addContext();
    addModes();
    addSlots();
    addTriggers();
    InventoryPages.addBackButton(menu, 2);
    return menu;
  }

  /**
   * Adds {@link PassiveAbilityType passive abilities}.
   */
  private void addAbilities() {
    int invSlot = 27;
    if (existingPassives != null) {
      for (PassiveAbilityType passiveAbilityType : PassiveAbilityType.values()) {
        String passiveName = passiveAbilityType.getProperName();
        String passiveId = passiveAbilityType.getId();
        boolean enabled = existingPassives.containsKey(passiveId);
        if (enabled) {
          List<String> lore = new ArrayList<>();
          for (SlotCondition slotCondition : existingPassives.get(passiveId)) {
            NamespacedKey passiveKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.PASSIVE.getHeader() + slotCondition.getSlot() + "." + slotCondition.getCondition() + "." + passiveId);
            String passiveValue = dataContainer.get(passiveKey, PersistentDataType.STRING);
            lore.add(ChatColor.WHITE + TextFormatter.capitalizePhrase(slotCondition.getSlot() + " " + slotCondition.getCondition() + ": " + passiveValue));
          }
          menu.setItem(invSlot, ItemCreator.createItem(Material.IRON_INGOT, ChatColor.AQUA + passiveName, lore));
        } else {
          menu.setItem(invSlot, ItemCreator.createItem(Material.RAW_IRON, ChatColor.AQUA + passiveName));
        }
        invSlot++;
      }
    } else {
      for (PassiveAbilityType passiveAbilityType : PassiveAbilityType.values()) {
        menu.setItem(invSlot, ItemCreator.createItem(Material.RAW_IRON, ChatColor.AQUA + passiveAbilityType.getProperName()));
        invSlot++;
      }
    }
  }

  /**
   * Adds potion effects.
   */
  private void addPotionEffects() {
    int invSlot = 27;
    for (PotionEffectType potionEffectType : PotionEffectType.values()) {
      menu.setItem(invSlot, ItemCreator.createItem(Material.GLASS_BOTTLE, ChatColor.AQUA + TextFormatter.capitalizePhrase(potionEffectType.getName())));
      invSlot++;
    }
  }

  /**
   * Adds contextual help.
   */
  private void addContext() {
    menu.setItem(0, ItemCreator.createPluginPlayerHead(PlayerHead.QUESTION_MARK_WHITE.getHead(), ChatColor.GREEN + "Help", List.of(ChatColor.WHITE + "To remove a passive ability, input \"-\".")));
  }

  /**
   * Adds menu {@link Mode} buttons.
   */
  private void addModes() {
    switch (mode) {
      case ABILITIES -> menu.setItem(8, ItemCreator.createItem(Material.POTION, ChatColor.AQUA + "Potion Effects", ItemFlag.HIDE_POTION_EFFECTS));
      case POTION_EFFECTS -> menu.setItem(6, ItemCreator.createItem(Material.SUGAR, ChatColor.AQUA + "Abilities"));
    }
  }

  /**
   * Adds {@link RpgEquipmentSlot} buttons.
   */
  private void addSlots() {
    menu.setItem(9, ItemCreator.createItem(Material.IRON_HELMET, ChatColor.AQUA + "Head", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(10, ItemCreator.createItem(Material.IRON_CHESTPLATE, ChatColor.AQUA + "Chest", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(11, ItemCreator.createItem(Material.IRON_LEGGINGS, ChatColor.AQUA + "Legs", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(12, ItemCreator.createItem(Material.IRON_BOOTS, ChatColor.AQUA + "Feet", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(14, ItemCreator.createItem(Material.IRON_SWORD, ChatColor.AQUA + "Hand", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(15, ItemCreator.createItem(Material.SHIELD, ChatColor.AQUA + "Off Hand", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(16, ItemCreator.createItem(Material.IRON_NUGGET, ChatColor.AQUA + "Necklace"));
    menu.setItem(17, ItemCreator.createItem(Material.GOLD_NUGGET, ChatColor.AQUA + "Ring"));
  }

  /**
   * Adds {@link PassiveTriggerType} buttons.
   */
  private void addTriggers() {
    menu.setItem(18, ItemCreator.createItem(Material.BEETROOT_SOUP, ChatColor.AQUA + "Below % HP"));
    menu.setItem(19, ItemCreator.createItem(Material.RED_DYE, ChatColor.AQUA + "Damage Dealt"));
    menu.setItem(20, ItemCreator.createItem(Material.GRAY_DYE, ChatColor.AQUA + "Damage Taken"));
    menu.setItem(21, ItemCreator.createItem(Material.BONE, ChatColor.AQUA + "Kill"));
  }

  /**
   * Maps an item's {@link Key#PASSIVE_LIST passive abilities}.
   *
   * @return item's {@link Key#PASSIVE_LIST passives} map
   */
  private Map<String, List<SlotCondition>> mapPassives() {
    NamespacedKey listKey = Key.PASSIVE_LIST.getNamespacedKey();
    boolean hasPassives = dataContainer.has(listKey, PersistentDataType.STRING);
    if (hasPassives) {
      Map<String, List<SlotCondition>> existingPassives = new HashMap<>();
      List<String> passives = new ArrayList<>(List.of(dataContainer.get(listKey, PersistentDataType.STRING).split(" ")));
      for (String passive : passives) {
        String[] passiveMeta = passive.split("\\.");
        String slot = passiveMeta[0];
        String condition = passiveMeta[1];
        String passiveType = passiveMeta[2];
        if (existingPassives.containsKey(passiveType)) {
          existingPassives.get(passiveType).add(new SlotCondition(slot, condition));
        } else {
          existingPassives.put(passiveType, new ArrayList<>(List.of(new SlotCondition(slot, condition))));
        }
      }
      return existingPassives;
    } else {
      return null;
    }
  }

  /**
   * Passive menu modes.
   */
  protected enum Mode {
    /**
     * Passive abilities.
     */
    ABILITIES,

    /**
     * Passive potion effects.
     */
    POTION_EFFECTS
  }

  /**
   * Represents a {@link Key#PASSIVE_LIST passive ability's}
   * {@link RpgEquipmentSlot} and {@link PassiveTriggerType.Condition}.
   *
   * @author Danny Nguyen
   * @version 1.17.12
   * @since 1.15.12
   */
  private static class SlotCondition {
    /**
     * {@link RpgEquipmentSlot}
     */
    private final String slot;

    /**
     * {@link PassiveTriggerType.Condition}
     */
    private final String condition;

    /**
     * Associates an {@link RpgEquipmentSlot}
     * with its {@link PassiveTriggerType.Condition}.
     *
     * @param eSlot     {@link RpgEquipmentSlot}
     * @param condition {@link PassiveTriggerType.Condition}.
     */
    SlotCondition(@NotNull String eSlot, @NotNull String condition) {
      this.slot = Objects.requireNonNull(eSlot, "Null slot");
      this.condition = Objects.requireNonNull(condition, "Null condition");
    }

    /**
     * Gets the {@link RpgEquipmentSlot}.
     *
     * @return {@link RpgEquipmentSlot}
     */
    @NotNull
    private String getSlot() {
      return this.slot;
    }

    /**
     * Gets the {@link PassiveTriggerType.Condition}.
     *
     * @return {@link PassiveTriggerType.Condition}
     */
    @NotNull
    private String getCondition() {
      return this.condition;
    }
  }
}
