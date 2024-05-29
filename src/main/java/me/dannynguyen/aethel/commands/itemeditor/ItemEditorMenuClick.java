package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.enums.rpg.AethelAttribute;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType;
import me.dannynguyen.aethel.interfaces.MenuClick;
import me.dannynguyen.aethel.listeners.MenuListener;
import me.dannynguyen.aethel.listeners.MessageListener;
import me.dannynguyen.aethel.plugin.MenuInput;
import me.dannynguyen.aethel.utils.TextFormatter;
import me.dannynguyen.aethel.utils.item.ItemReader;
import me.dannynguyen.aethel.utils.item.LoreGeneration;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

/**
 * Inventory click event listener for {@link ItemEditorCommand} menus.
 * <p>
 * Called with {@link MenuListener}.
 *
 * @author Danny Nguyen
 * @version 1.25.6
 * @since 1.6.7
 */
public class ItemEditorMenuClick implements MenuClick {
  /**
   * Inventory click event.
   */
  private final InventoryClickEvent e;

  /**
   * GUI.
   */
  private final Inventory menu;

  /**
   * Player who clicked.
   */
  private final Player user;

  /**
   * User's UUID.
   */
  private final UUID uuid;

  /**
   * ItemStack being edited.
   */
  private final ItemStack item;

  /**
   * ItemStack's meta.
   */
  private final ItemMeta meta;

  /**
   * Slot clicked.
   */
  private final int slot;

  /**
   * Associates an inventory click event with its user in
   * the context of an open {@link ItemEditorCommand} menu.
   *
   * @param e inventory click event
   */
  public ItemEditorMenuClick(@NotNull InventoryClickEvent e) {
    this.e = Objects.requireNonNull(e, "Null inventory click event");
    this.menu = e.getInventory();
    this.user = (Player) e.getWhoClicked();
    this.uuid = user.getUniqueId();
    this.item = Plugin.getData().getEditedItemCache().getEditedItems().get(user.getUniqueId());
    this.meta = item.getItemMeta();
    this.slot = e.getSlot();
  }

  /**
   * Sets an item's cosmetic metadata or opens a gameplay metadata editor menu.
   */
  public void interpretMenuClick() {
    switch (slot) {
      case 0, 1 -> { // Color Code Context
      }
      case 4 -> { // Item
      }
      case 6 -> new CosmeticChange().toggleUsable();
      case 7 -> new CosmeticChange().togglePlaceable();
      case 8 -> new CosmeticChange().toggleEdible();
      case 9 -> new CosmeticChange().setDisplayName();
      case 10 -> new CosmeticChange().setCustomModelData();
      case 11 -> new CosmeticChange().setDurability();
      case 12 -> new CosmeticChange().setRepairCost();
      case 14 -> new MenuChange().openAttribute();
      case 15 -> new MenuChange().openAethelAttribute();
      case 16 -> new MenuChange().openEnchantment();
      case 17 -> new MenuChange().openPotion();
      case 19 -> new CosmeticChange().setReinforcement();
      case 20 -> new CosmeticChange().setMaxReinforcement();
      case 21 -> new CosmeticChange().toggleUnbreakable();
      case 23 -> new MenuChange().openPassive();
      case 24 -> new MenuChange().openActive();
      case 26 -> new MenuChange().openEdible();
      case 35 -> new MenuChange().openTag();
      case 36 -> { // Lore Context
      }
      case 37 -> new CosmeticChange().setLore();
      case 38 -> new CosmeticChange().clearLore();
      case 45 -> new CosmeticChange().addLore();
      case 46 -> new CosmeticChange().editLore();
      case 47 -> new CosmeticChange().removeLore();
      case 48 -> new LoreGeneration(user, item, menu).generateLore();
      case 41, 42, 43, 44, 50, 51, 52, 53 -> new CosmeticChange().toggleItemFlag();
    }
  }

  /**
   * Either changes the {@link RpgEquipmentSlot} mode or sets an item's Minecraft attribute.
   */
  public void interpretAttributeClick() {
    switch (e.getSlot()) {
      case 2, 4 -> { // Context, Item
      }
      case 6 -> new MenuChange().returnToCosmetic();
      case 9 -> new AttributeChange().setSlot(EquipmentSlot.HEAD);
      case 10 -> new AttributeChange().setSlot(EquipmentSlot.CHEST);
      case 11 -> new AttributeChange().setSlot(EquipmentSlot.LEGS);
      case 12 -> new AttributeChange().setSlot(EquipmentSlot.FEET);
      case 14 -> new AttributeChange().setSlot(EquipmentSlot.HAND);
      case 15 -> new AttributeChange().setSlot(EquipmentSlot.OFF_HAND);
      case 18, 27, 36 -> { // Context
      }
      default -> new AttributeChange().readAttribute();
    }
  }

  /**
   * Either changes the {@link RpgEquipmentSlot} mode or sets an
   * item's {@link Key#ATTRIBUTE_LIST Aethel attribute}.
   */
  public void interpretAethelAttributeClick() {
    switch (e.getSlot()) {
      case 2, 4 -> { // Context, Item
      }
      case 6 -> new MenuChange().returnToCosmetic();
      case 9 -> new AethelAttributeChange().setSlot(RpgEquipmentSlot.HEAD);
      case 10 -> new AethelAttributeChange().setSlot(RpgEquipmentSlot.CHEST);
      case 11 -> new AethelAttributeChange().setSlot(RpgEquipmentSlot.LEGS);
      case 12 -> new AethelAttributeChange().setSlot(RpgEquipmentSlot.FEET);
      case 14 -> new AethelAttributeChange().setSlot(RpgEquipmentSlot.HAND);
      case 15 -> new AethelAttributeChange().setSlot(RpgEquipmentSlot.OFF_HAND);
      case 16 -> new AethelAttributeChange().setSlot(RpgEquipmentSlot.NECKLACE);
      case 17 -> new AethelAttributeChange().setSlot(RpgEquipmentSlot.RING);
      case 18, 27, 36 -> { // Context
      }
      default -> new AethelAttributeChange().readAttribute();
    }
  }

  /**
   * Sets an item's enchant.
   */
  public void interpretEnchantmentClick() {
    switch (e.getSlot()) {
      case 2, 4 -> { // Context, Item
      }
      case 6 -> new MenuChange().returnToCosmetic();
      default -> new EnchantmentChange().readEnchantment();
    }
  }

  /**
   * Sets an item's potion effect.
   */
  public void interpretPotionClick() {
    switch (e.getSlot()) {
      case 2, 4 -> { // Context, Item
      }
      case 5 -> new PotionChange().setColor();
      case 6 -> new MenuChange().returnToCosmetic();
      default -> new PotionChange().readEffect();
    }
  }

  /**
   * Sets an item's {@link Key#PASSIVE_LIST passive ability}.
   */
  public void interpretPassiveClick() {
    switch (e.getSlot()) {
      case 2, 4 -> { // Context, Item
      }
      case 6 -> new MenuChange().returnToCosmetic();
      case 9 -> new PassiveChange().setSlot(RpgEquipmentSlot.HEAD);
      case 10 -> new PassiveChange().setSlot(RpgEquipmentSlot.CHEST);
      case 11 -> new PassiveChange().setSlot(RpgEquipmentSlot.LEGS);
      case 12 -> new PassiveChange().setSlot(RpgEquipmentSlot.FEET);
      case 14 -> new PassiveChange().setSlot(RpgEquipmentSlot.HAND);
      case 15 -> new PassiveChange().setSlot(RpgEquipmentSlot.OFF_HAND);
      case 16 -> new PassiveChange().setSlot(RpgEquipmentSlot.NECKLACE);
      case 17 -> new PassiveChange().setSlot(RpgEquipmentSlot.RING);
      case 18 -> new PassiveChange().setTrigger(PassiveTriggerType.BELOW_HEALTH);
      case 19 -> new PassiveChange().setTrigger(PassiveTriggerType.DAMAGE_DEALT);
      case 20 -> new PassiveChange().setTrigger(PassiveTriggerType.DAMAGE_TAKEN);
      case 21 -> new PassiveChange().setTrigger(PassiveTriggerType.INTERVAL);
      case 22 -> new PassiveChange().setTrigger(PassiveTriggerType.ON_KILL);
      default -> new PassiveChange().readPassive();
    }
  }

  /**
   * Sets an item's {@link Key#ACTIVE_EQUIPMENT_LIST equipment active ability}.
   */
  public void interpretActiveClick() {
    switch (e.getSlot()) {
      case 2, 4 -> { // Context, Item
      }
      case 6 -> new MenuChange().returnToCosmetic();
      case 9 -> new ActiveChange().setSlot(RpgEquipmentSlot.HEAD);
      case 10 -> new ActiveChange().setSlot(RpgEquipmentSlot.CHEST);
      case 11 -> new ActiveChange().setSlot(RpgEquipmentSlot.LEGS);
      case 12 -> new ActiveChange().setSlot(RpgEquipmentSlot.FEET);
      case 14 -> new ActiveChange().setSlot(RpgEquipmentSlot.HAND);
      case 15 -> new ActiveChange().setSlot(RpgEquipmentSlot.OFF_HAND);
      case 16 -> new ActiveChange().setSlot(RpgEquipmentSlot.NECKLACE);
      case 17 -> new ActiveChange().setSlot(RpgEquipmentSlot.RING);
      default -> new ActiveChange().readActive();
    }
  }

  /**
   * Sets an item's {@link Key#ACTIVE_EDIBLE_LIST edible active ability}.
   */
  public void interpretEdibleClick() {
    switch (e.getSlot()) {
      case 2, 4 -> { // Context, Item
      }
      case 6 -> new MenuChange().returnToCosmetic();
      default -> new EdibleChange().readActive();
    }
  }

  /**
   * Sets an item's {@link Key Aethel tag}.
   */
  public void interpretTagClick() {
    switch (e.getSlot()) {
      case 2, 4 -> { // Context, Item
      }
      case 6 -> new MenuChange().returnToCosmetic();
      default -> new TagChange().readTag();
    }
  }

  /**
   * Uses the user's next message as the field's input.
   *
   * @param messageType {@link MessageListener.Type}
   */
  private void awaitMessageInput(MessageListener.Type messageType) {
    user.closeInventory();
    Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput().setMessageInput(messageType);
  }

  /**
   * Represents a menu change operation.
   *
   * @author Danny Nguyen
   * @version 1.25.0
   * @since 1.23.11
   */
  private class MenuChange {
    /**
     * No parameter constructor.
     */
    MenuChange() {
    }

    /**
     * Opens an {@link AttributeMenu}.
     */
    private void openAttribute() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      menuInput.setSlot(RpgEquipmentSlot.HAND);
      user.openInventory(new AttributeMenu(user, EquipmentSlot.HAND).getMainMenu());
      menuInput.setMenu(MenuListener.Menu.ITEMEDITOR_MINECRAFT_ATTRIBUTE);
    }

    /**
     * Opens an {@link AethelAttributeMenu}.
     */
    private void openAethelAttribute() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      menuInput.setSlot(RpgEquipmentSlot.HAND);
      user.openInventory(new AethelAttributeMenu(user, RpgEquipmentSlot.HAND).getMainMenu());
      menuInput.setMenu(MenuListener.Menu.ITEMEDITOR_AETHEL_ATTRIBUTE);
    }

    /**
     * Opens an {@link EnchantmentMenu}.
     */
    private void openEnchantment() {
      user.openInventory(new EnchantmentMenu(user).getMainMenu());
      Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput().setMenu(MenuListener.Menu.ITEMEDITOR_ENCHANTMENT);
    }

    /**
     * Opens a {@link PotionMenu}.
     */
    private void openPotion() {
      user.openInventory(new PotionMenu(user).getMainMenu());
      Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput().setMenu(MenuListener.Menu.ITEMEDITOR_POTION);
    }

    /**
     * Opens a {@link PassiveMenu}.
     */
    private void openPassive() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      menuInput.setSlot(RpgEquipmentSlot.HAND);
      menuInput.setTrigger(PassiveTriggerType.DAMAGE_DEALT);
      user.openInventory(new PassiveMenu(user, RpgEquipmentSlot.HAND, PassiveTriggerType.DAMAGE_DEALT).getMainMenu());
      menuInput.setMenu(MenuListener.Menu.ITEMEDITOR_PASSIVE);
    }

    /**
     * Opens an {@link ActiveMenu}.
     */
    private void openActive() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      menuInput.setSlot(RpgEquipmentSlot.HAND);
      user.openInventory(new ActiveMenu(user, RpgEquipmentSlot.HAND).getMainMenu());
      menuInput.setMenu(MenuListener.Menu.ITEMEDITOR_ACTIVE_EQUIPMENT);
    }

    /**
     * Opens a {@link EdibleMenu}.
     */
    private void openEdible() {
      user.openInventory(new EdibleMenu(user).getMainMenu());
      Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput().setMenu(MenuListener.Menu.ITEMEDITOR_ACTIVE_EDIBLE);
    }

    /**
     * Opens a {@link TagMenu}.
     */
    private void openTag() {
      user.openInventory(new TagMenu(user).getMainMenu());
      Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput().setMenu(MenuListener.Menu.ITEMEDITOR_TAG);
    }

    /**
     * Returns to the {@link CosmeticMenu}.
     */
    private void returnToCosmetic() {
      user.openInventory(new CosmeticMenu(user).getMainMenu());
      Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput().setMenu(MenuListener.Menu.ITEMEDITOR_COSMETIC);
    }
  }

  /**
   * Represents an item's {@link CosmeticMenu} metadata change operation.
   *
   * @author Danny Nguyen
   * @version 1.27.0
   * @since 1.23.11
   */
  private class CosmeticChange {
    /**
     * No parameter constructor.
     */
    CosmeticChange() {
    }

    /**
     * Toggles an item's ability to be used.
     */
    private void toggleUsable() {
      PersistentDataContainer itemTags = meta.getPersistentDataContainer();
      if (itemTags.has(Key.UNUSABLE.getNamespacedKey(), PersistentDataType.BOOLEAN)) {
        itemTags.remove(Key.UNUSABLE.getNamespacedKey());
        user.sendMessage(ChatColor.GREEN + "[Usable]");
      } else {
        itemTags.set(Key.UNUSABLE.getNamespacedKey(), PersistentDataType.BOOLEAN, true);
        user.sendMessage(ChatColor.RED + "[Unusable]");
      }
      item.setItemMeta(meta);
      CosmeticMenu.addUnusable(menu, item);
    }

    /**
     * Toggles an item's ability to be placed.
     */
    private void togglePlaceable() {
      PersistentDataContainer itemTags = meta.getPersistentDataContainer();
      if (itemTags.has(Key.NON_PLACEABLE.getNamespacedKey(), PersistentDataType.BOOLEAN)) {
        itemTags.remove(Key.NON_PLACEABLE.getNamespacedKey());
        user.sendMessage(ChatColor.GREEN + "[Placeable]");
      } else {
        itemTags.set(Key.NON_PLACEABLE.getNamespacedKey(), PersistentDataType.BOOLEAN, true);
        user.sendMessage(ChatColor.RED + "[Non-Placeable]");
      }
      item.setItemMeta(meta);
      CosmeticMenu.addNonPlaceable(menu, item);
    }

    /**
     * Toggles an item's ability to be eaten.
     */
    private void toggleEdible() {
      PersistentDataContainer itemTags = meta.getPersistentDataContainer();
      if (itemTags.has(Key.NON_EDIBLE.getNamespacedKey(), PersistentDataType.BOOLEAN)) {
        itemTags.remove(Key.NON_EDIBLE.getNamespacedKey());
        user.sendMessage(ChatColor.GREEN + "[Edible]");
      } else {
        itemTags.set(Key.NON_EDIBLE.getNamespacedKey(), PersistentDataType.BOOLEAN, true);
        user.sendMessage(ChatColor.RED + "[Non-Edible]");
      }
      item.setItemMeta(meta);
      CosmeticMenu.addNonEdible(menu, item);
    }

    /**
     * Sets an item's display name.
     */
    private void setDisplayName() {
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input display name.");
      awaitMessageInput(MessageListener.Type.ITEMEDITOR_DISPLAY_NAME);
    }

    /**
     * Sets an item's custom model data.
     */
    private void setCustomModelData() {
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input custom model data value.");
      awaitMessageInput(MessageListener.Type.ITEMEDITOR_CUSTOM_MODEL_DATA);
    }

    /**
     * Sets an item's durability.
     */
    private void setDurability() {
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input durability (+) or damage (-) value.");
      awaitMessageInput(MessageListener.Type.ITEMEDITOR_DURABILITY);
    }

    /**
     * Sets an item's repair cost.
     */
    private void setRepairCost() {
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input repair cost value.");
      awaitMessageInput(MessageListener.Type.ITEMEDITOR_REPAIR_COST);
    }

    /**
     * Sets an item's reinforcement.
     */
    private void setReinforcement() {
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input reinforcement.");
      awaitMessageInput(MessageListener.Type.ITEMEDITOR_RPG_DURABILITY);
    }

    /**
     * Sets an item's max reinforcement.
     */
    private void setMaxReinforcement() {
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input max reinforcement.");
      awaitMessageInput(MessageListener.Type.ITEMEDITOR_MAX_RPG_DURABILITY);
    }

    /**
     * Toggles an item's ability to be broken.
     */
    private void toggleUnbreakable() {
      if (meta.isUnbreakable()) {
        meta.setUnbreakable(false);
        user.sendMessage(ChatColor.RED + "[Set Unbreakable]");
      } else {
        meta.setUnbreakable(true);
        user.sendMessage(ChatColor.GREEN + "[Set Unbreakable]");
      }
      item.setItemMeta(meta);
      CosmeticMenu.addUnbreakable(menu, meta);
    }

    /**
     * Sets an item's lore.
     */
    private void setLore() {
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input lore to set.");
      awaitMessageInput(MessageListener.Type.ITEMEDITOR_LORE_SET);
    }

    /**
     * Clears an item's lore.
     */
    private void clearLore() {
      if (meta.hasLore()) {
        meta.setLore(new ArrayList<>());
        item.setItemMeta(meta);
        user.sendMessage(ChatColor.GREEN + "[Cleared Lore]");
      } else {
        user.sendMessage(Message.LORE_DOES_NOT_EXIST.getMessage());
      }
    }

    /**
     * Adds a line of text to an item's lore.
     */
    private void addLore() {
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input lore to add.");
      awaitMessageInput(MessageListener.Type.ITEMEDITOR_LORE_ADD);
    }

    /**
     * Edits a line of text from an item's lore.
     */
    private void editLore() {
      if (meta.hasLore()) {
        user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input line number and new lore.");
        awaitMessageInput(MessageListener.Type.ITEMEDITOR_LORE_EDIT);
      } else {
        user.sendMessage(Message.LORE_DOES_NOT_EXIST.getMessage());
      }
    }

    /**
     * Removes a line of text from an item's lore.
     */
    private void removeLore() {
      if (meta.hasLore()) {
        user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input line number to remove.");
        awaitMessageInput(MessageListener.Type.ITEMEDITOR_LORE_REMOVE);
      } else {
        user.sendMessage(Message.LORE_DOES_NOT_EXIST.getMessage());
      }
    }

    /**
     * Toggles an item's item flag.
     */
    private void toggleItemFlag() {
      String itemFlagName = ChatColor.stripColor(ItemReader.readName(e.getCurrentItem()));
      ItemFlag itemFlag = ItemFlag.valueOf(TextFormatter.formatEnum(itemFlagName));
      if (!meta.hasItemFlag(itemFlag)) {
        meta.addItemFlags(itemFlag);
        user.sendMessage(ChatColor.GREEN + "[" + itemFlagName + "]");
      } else {
        meta.removeItemFlags(itemFlag);
        user.sendMessage(ChatColor.RED + "[" + itemFlagName + "]");
      }
      item.setItemMeta(meta);
      switch (itemFlag) {
        case HIDE_ARMOR_TRIM -> CosmeticMenu.addHideArmorTrim(menu, meta);
        case HIDE_ATTRIBUTES -> CosmeticMenu.addHideAttributes(menu, meta);
        case HIDE_DESTROYS -> CosmeticMenu.addHideDestroys(menu, meta);
        case HIDE_DYE -> CosmeticMenu.addHideDye(menu, meta);
        case HIDE_ENCHANTS -> CosmeticMenu.addHideEnchants(menu, meta);
        case HIDE_PLACED_ON -> CosmeticMenu.addHidePlacedOn(menu, meta);
        case HIDE_ADDITIONAL_TOOLTIP -> CosmeticMenu.addHidePotionEffects(menu, meta);
        case HIDE_UNBREAKABLE -> CosmeticMenu.addHideUnbreakable(menu, meta);
      }
    }
  }

  /**
   * Represents an item's {@link AttributeMenu} metadata change operation.
   *
   * @author Danny Nguyen
   * @version 1.23.11
   * @since 1.23.11
   */
  private class AttributeChange {
    /**
     * No parameter constructor.
     */
    AttributeChange() {
    }

    /**
     * Sets the user's interacting equipment slot for attributes.
     *
     * @param eSlot equipment slot
     */
    private void setSlot(EquipmentSlot eSlot) {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      menuInput.setSlot(RpgEquipmentSlot.valueOf(eSlot.name()));
      user.openInventory(new AttributeMenu(user, eSlot).getMainMenu());
      menuInput.setMenu(MenuListener.Menu.ITEMEDITOR_MINECRAFT_ATTRIBUTE);
    }

    /**
     * Determines the Minecraft attribute to be set and prompts the user for an input.
     */
    private void readAttribute() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      EquipmentSlot slot = EquipmentSlot.valueOf(menuInput.getSlot().name());
      String attribute = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + TextFormatter.capitalizePhrase(slot.name()) + " " + attribute + ChatColor.WHITE + " value.");
      user.sendMessage(getAttributeContext(attribute));
      menuInput.setObjectType(attribute);
      awaitMessageInput(MessageListener.Type.ITEMEDITOR_MINECRAFT_ATTRIBUTE);
    }

    /**
     * Sends a contextual base value for the Minecraft attribute being edited.
     *
     * @param attribute attribute name
     * @return base attribute value
     */
    private String getAttributeContext(String attribute) {
      String context = Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Base: ";
      switch (Attribute.valueOf(TextFormatter.formatEnum(attribute))) {
        case GENERIC_MAX_HEALTH -> {
          return context + "20.0";
        }
        case GENERIC_ATTACK_DAMAGE -> {
          return context + "1.0";
        }
        case GENERIC_ATTACK_SPEED -> {
          return context + "4.0";
        }
        case GENERIC_ARMOR -> {
          return context + "0.0 [Max: 30.0]";
        }
        case GENERIC_ARMOR_TOUGHNESS -> {
          return context + "0.0 [Max: 20.0]";
        }
        case GENERIC_KNOCKBACK_RESISTANCE -> {
          return context + "0.0 [Max: 1.0]";
        }
        case GENERIC_MOVEMENT_SPEED -> {
          return context + "0.1";
        }
        case GENERIC_LUCK -> {
          return context + "0.0";
        }
        default -> {
          return null;
        }
      }
    }
  }

  /**
   * Represents an item's {@link AethelAttributeMenu} metadata change operation.
   *
   * @author Danny Nguyen
   * @version 1.23.11
   * @since 1.23.11
   */
  private class AethelAttributeChange {
    /**
     * No parameter constructor.
     */
    AethelAttributeChange() {
    }

    /**
     * Sets the user's interacting {@link RpgEquipmentSlot} for
     * {@link Key#ATTRIBUTE_LIST Aethel attributes}.
     *
     * @param eSlot {@link RpgEquipmentSlot}
     */
    private void setSlot(RpgEquipmentSlot eSlot) {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      menuInput.setSlot(eSlot);
      user.openInventory(new AethelAttributeMenu(user, eSlot).getMainMenu());
      menuInput.setMenu(MenuListener.Menu.ITEMEDITOR_AETHEL_ATTRIBUTE);
    }

    /**
     * Determines the {@link AethelAttribute} to be set and prompts the user for an input.
     */
    private void readAttribute() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      RpgEquipmentSlot eSlot = menuInput.getSlot();
      String attribute = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + eSlot.getProperName() + " " + attribute + ChatColor.WHITE + " value.");
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Base: " + AethelAttribute.valueOf(TextFormatter.formatEnum(attribute)).getBaseValue());
      menuInput.setObjectType(TextFormatter.formatId(attribute));
      awaitMessageInput(MessageListener.Type.ITEMEDITOR_AETHEL_ATTRIBUTE);
    }
  }

  /**
   * Represents an item's {@link EnchantmentMenu} metadata change operation.
   *
   * @author Danny Nguyen
   * @version 1.23.11
   * @since 1.23.11
   */
  private class EnchantmentChange {
    /**
     * No parameter constructor.
     */
    EnchantmentChange() {
    }

    /**
     * Determines the enchantment to be set and prompts the user for an input.
     */
    private void readEnchantment() {
      String enchantment = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + TextFormatter.capitalizePhrase(enchantment) + ChatColor.WHITE + " value.");
      Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput().setObjectType(TextFormatter.formatId(enchantment));
      awaitMessageInput(MessageListener.Type.ITEMEDITOR_ENCHANTMENT);
    }
  }

  /**
   * Represents an item's {@link PotionMenu} metadata change operation.
   *
   * @author Danny Nguyen
   * @version 1.23.11
   * @since 1.23.11
   */
  private class PotionChange {
    /**
     * No parameter constructor.
     */
    PotionChange() {
    }

    /**
     * Sets the potion's color.
     */
    private void setColor() {
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input RGB value.");
      awaitMessageInput(MessageListener.Type.ITEMEDITOR_POTION_COLOR);
    }

    /**
     * Determines the potion effect to be set and prompts the user for an input.
     */
    private void readEffect() {
      String potionEffect = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + TextFormatter.capitalizePhrase(potionEffect) + ChatColor.WHITE + " duration, amplifier, and particle visibility.");
      Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput().setObjectType(TextFormatter.formatId(potionEffect));
      awaitMessageInput(MessageListener.Type.ITEMEDITOR_POTION_EFFECT);
    }
  }

  /**
   * Represents an item's {@link PassiveMenu} metadata change operation.
   *
   * @author Danny Nguyen
   * @version 1.23.11
   * @since 1.23.11
   */
  private class PassiveChange {
    /**
     * No parameter constructor.
     */
    PassiveChange() {
    }

    /**
     * Sets the user's interacting {@link RpgEquipmentSlot} for
     * {@link Key#PASSIVE_LIST passive abilities}.
     *
     * @param eSlot {@link RpgEquipmentSlot}
     */
    private void setSlot(RpgEquipmentSlot eSlot) {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      menuInput.setSlot(eSlot);
      user.openInventory(new PassiveMenu(user, eSlot, menuInput.getTrigger()).getMainMenu());
      menuInput.setMenu(MenuListener.Menu.ITEMEDITOR_PASSIVE);
    }

    /**
     * Sets the user's interacting {@link PassiveTriggerType} for
     * {@link Key#PASSIVE_LIST passive abilities}.
     *
     * @param passiveTriggerType {@link PassiveTriggerType}
     */
    private void setTrigger(PassiveTriggerType passiveTriggerType) {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      menuInput.setTrigger(passiveTriggerType);
      user.openInventory(new PassiveMenu(user, menuInput.getSlot(), passiveTriggerType).getMainMenu());
      menuInput.setMenu(MenuListener.Menu.ITEMEDITOR_PASSIVE);
    }

    /**
     * Determines the {@link PassiveAbilityType} to be set and prompts the user for an input.
     */
    private void readPassive() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      RpgEquipmentSlot eSlot = menuInput.getSlot();
      PassiveTriggerType passiveTriggerType = menuInput.getTrigger();
      String passive = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + eSlot.getProperName() + " " + passiveTriggerType.getProperName() + " " + passive + ChatColor.WHITE + " ability values:");
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + passiveTriggerType.getCondition().getData() + " " + PassiveAbilityType.valueOf(TextFormatter.formatEnum(passive)).getEffect().getData());
      menuInput.setObjectType(TextFormatter.formatId(passive));
      awaitMessageInput(MessageListener.Type.ITEMEDITOR_PASSIVE_ABILITY);
    }
  }

  /**
   * Represents an item's {@link ActiveMenu} metadata change operation.
   *
   * @author Danny Nguyen
   * @version 1.23.11
   * @since 1.23.11
   */
  private class ActiveChange {
    /**
     * No parameter constructor.
     */
    ActiveChange() {
    }

    /**
     * Sets the user's interacting {@link RpgEquipmentSlot}
     * for {@link Key#ACTIVE_EQUIPMENT_LIST active abilities}.
     *
     * @param eSlot {@link RpgEquipmentSlot}
     */
    private void setSlot(RpgEquipmentSlot eSlot) {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      menuInput.setSlot(eSlot);
      user.openInventory(new ActiveMenu(user, eSlot).getMainMenu());
      menuInput.setMenu(MenuListener.Menu.ITEMEDITOR_ACTIVE_EQUIPMENT);
    }

    /**
     * Determines the {@link ActiveAbilityType} to be set and prompts the user for an input.
     */
    private void readActive() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      RpgEquipmentSlot eSlot = menuInput.getSlot();
      String active = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + eSlot.getProperName() + " " + active + ChatColor.WHITE + " ability values:");
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + ActiveAbilityType.valueOf(TextFormatter.formatEnum(active)).getEffect().getData());
      menuInput.setObjectType(TextFormatter.formatId(active));
      awaitMessageInput(MessageListener.Type.ITEMEDITOR_ACTIVE_ABILITY_EQUIPMENT);
    }
  }

  /**
   * Represents an item's {@link EdibleMenu} metadata change operation.
   *
   * @author Danny Nguyen
   * @version 1.25.0
   * @since 1.25.0
   */
  private class EdibleChange {
    /**
     * No parameter constructor.
     */
    EdibleChange() {
    }

    /**
     * Determines the {@link ActiveAbilityType} to be set and prompts the user for an input.
     */
    private void readActive() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      String active = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + active + ChatColor.WHITE + " ability values:");
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + ActiveAbilityType.valueOf(TextFormatter.formatEnum(active)).getEffect().getData());
      menuInput.setObjectType(TextFormatter.formatId(active));
      awaitMessageInput(MessageListener.Type.ITEMEDITOR_ACTIVE_ABILITY_EDIBLE);
    }
  }

  /**
   * Represents an item's {@link TagMenu} metadata change operation.
   *
   * @author Danny Nguyen
   * @version 1.23.11
   * @since 1.23.11
   */
  private class TagChange {
    /**
     * No parameter constructor.
     */
    TagChange() {
    }

    /**
     * Determines the {@link Key Aethel tag} to be set and prompts the user for an input.
     */
    private void readTag() {
      String tag = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + tag + ChatColor.WHITE + " value.");
      Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput().setObjectType(tag);
      awaitMessageInput(MessageListener.Type.ITEMEDITOR_AETHEL_TAG);
    }
  }
}
