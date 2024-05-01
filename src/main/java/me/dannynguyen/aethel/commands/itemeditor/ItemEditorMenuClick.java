package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.plugin.KeyHeader;
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
import me.dannynguyen.aethel.utils.item.ItemCreator;
import me.dannynguyen.aethel.utils.item.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
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

import java.text.DecimalFormat;
import java.util.*;

/**
 * Inventory click event listener for {@link ItemEditorCommand} menus.
 * <p>
 * Called with {@link MenuListener}.
 *
 * @author Danny Nguyen
 * @version 1.24.9
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
      case 25 -> new MenuChange().openTag();
      case 36 -> { // Lore Context
      }
      case 37 -> new CosmeticChange().setLore();
      case 38 -> new CosmeticChange().clearLore();
      case 45 -> new CosmeticChange().addLore();
      case 46 -> new CosmeticChange().editLore();
      case 47 -> new CosmeticChange().removeLore();
      case 48 -> new LoreGeneration().generateLore();
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
   * Sets an item's {@link Key#ACTIVE_LIST active ability}.
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
   * @version 1.23.11
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
      menuInput.setMenu(MenuListener.Menu.ITEMEDITOR_ACTIVE);
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
   * @version 1.23.11
   * @since 1.23.11
   */
  private class CosmeticChange {
    /**
     * No parameter constructor.
     */
    CosmeticChange() {
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
      if (!meta.isUnbreakable()) {
        meta.setUnbreakable(true);
        user.sendMessage(ChatColor.GREEN + "[Set Unbreakable]");
      } else {
        meta.setUnbreakable(false);
        user.sendMessage(ChatColor.RED + "[Set Unbreakable]");
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
        user.sendMessage(ChatColor.GREEN + "[Hide " + itemFlagName + "]");
      } else {
        meta.removeItemFlags(itemFlag);
        user.sendMessage(ChatColor.RED + "[Hide " + itemFlagName + "]");
      }
      item.setItemMeta(meta);
      switch (itemFlag) {
        case HIDE_ARMOR_TRIM -> CosmeticMenu.addHideArmorTrim(menu, meta);
        case HIDE_ATTRIBUTES -> CosmeticMenu.addHideAttributes(menu, meta);
        case HIDE_DESTROYS -> CosmeticMenu.addHideDestroys(menu, meta);
        case HIDE_DYE -> CosmeticMenu.addHideDye(menu, meta);
        case HIDE_ENCHANTS -> CosmeticMenu.addHideEnchants(menu, meta);
        case HIDE_PLACED_ON -> CosmeticMenu.addHidePlacedOn(menu, meta);
        case HIDE_POTION_EFFECTS -> CosmeticMenu.addHidePotionEffects(menu, meta);
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
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + TextFormatter.capitalizePhrase(potionEffect) + ChatColor.WHITE + " duration, amplifier, and particle visibility");
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
     * for {@link Key#ACTIVE_LIST active abilities}.
     *
     * @param eSlot {@link RpgEquipmentSlot}
     */
    private void setSlot(RpgEquipmentSlot eSlot) {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      menuInput.setSlot(eSlot);
      user.openInventory(new ActiveMenu(user, eSlot).getMainMenu());
      menuInput.setMenu(MenuListener.Menu.ITEMEDITOR_ACTIVE);
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
      awaitMessageInput(MessageListener.Type.ITEMEDITOR_ACTIVE_ABILITY);
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

  /**
   * Represents an item's Minecraft and {@link Key#ATTRIBUTE_LIST Aethel attribute},
   * {@link Key#PASSIVE_LIST passive ability}, and
   * {@link Key#ACTIVE_LIST active ability} lore generation.
   *
   * @author Danny Nguyen
   * @version 1.24.9
   * @since 1.17.13
   */
  private class LoreGeneration {
    /**
     * Order of headers by {@link RpgEquipmentSlot}.
     */
    private static final List<String> headerOrder = List.of(
        RpgEquipmentSlot.HEAD.getId(), RpgEquipmentSlot.CHEST.getId(),
        RpgEquipmentSlot.LEGS.getId(), RpgEquipmentSlot.FEET.getId(),
        RpgEquipmentSlot.NECKLACE.getId(), RpgEquipmentSlot.RING.getId(),
        RpgEquipmentSlot.HAND.getId(), RpgEquipmentSlot.OFF_HAND.getId());

    /**
     * ItemStack's persistent tags.
     */
    private final PersistentDataContainer itemTags = meta.getPersistentDataContainer();

    /**
     * ItemStack's lore.
     */
    private final List<String> lore;

    /**
     * ItemStack's total Minecraft and {@link Key#ATTRIBUTE_LIST Aethel attribute}
     * values categorized by {@link RpgEquipmentSlot}.
     */
    private Map<String, Map<String, Double>> attributeValues;

    /**
     * ItemStack's {@link Key#PASSIVE_LIST passive abilities}
     * categorized by {@link RpgEquipmentSlot}.
     */
    private Map<String, List<String>> passiveAbilities;

    /**
     * ItemStack's {@link Key#ACTIVE_LIST active abilities}
     * categorized by {@link RpgEquipmentSlot}.
     */
    private Map<String, List<String>> activeAbilities;

    /**
     * If the item's lore was generated.
     */
    private boolean generatedLore = false;

    /**
     * No parameter constructor.
     */
    private LoreGeneration() {
      if (meta.hasLore()) {
        this.lore = meta.getLore();
      } else {
        this.lore = new ArrayList<>();
      }
    }

    /**
     * Generates an item's lore based on its {@link Key plugin-related data}.
     */
    private void generateLore() {
      if (itemTags.has(Key.ATTRIBUTE_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
        generatedLore = true;
        this.attributeValues = totalAttributeValues();
        addAttributeHeaders();
        menu.setItem(42, ItemCreator.createItem(Material.GREEN_DYE, ChatColor.AQUA + "Hide Attributes", List.of(ChatColor.GREEN + "True")));
      }

      if (itemTags.has(Key.PASSIVE_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
        generatedLore = true;
        this.passiveAbilities = sortPassiveAbilities();
        addPassiveHeaders();
      }

      if (itemTags.has(Key.ACTIVE_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
        generatedLore = true;
        this.activeAbilities = sortActiveAbilities();
        addActiveHeaders();
      }

      if (itemTags.has(Key.RECIPE_FORGE_ID.getNamespacedKey(), PersistentDataType.STRING)) {
        if (generatedLore) {
          lore.add("");
        }
        generatedLore = true;
        lore.add(ChatColor.DARK_GRAY + "aethel:" + itemTags.get(Key.RECIPE_FORGE_ID.getNamespacedKey(), PersistentDataType.STRING));
      }

      boolean hasReinforcementTags = itemTags.has(Key.RPG_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER) && itemTags.has(Key.RPG_MAX_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER);
      if (hasReinforcementTags) {
        if (generatedLore) {
          lore.add("");
        }
        generatedLore = true;
        int reinforcement = itemTags.get(Key.RPG_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER);
        int maxReinforcement = itemTags.get(Key.RPG_MAX_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER);
        lore.add(ChatColor.WHITE + "Reinforcement: " + reinforcement + " / " + maxReinforcement);
      }

      if (generatedLore) {
        meta.setLore(lore);
        item.setItemMeta(meta);
        user.sendMessage(ChatColor.GREEN + "[Generated Lore]");
      } else {
        user.sendMessage(ChatColor.RED + "Not modified by plugin.");
      }
    }

    /**
     * Totals the item's Minecraft and {@link Key#ATTRIBUTE_LIST Aethel attributes} together.
     *
     * @return ItemStack's total attribute values
     */
    private Map<String, Map<String, Double>> totalAttributeValues() {
      Map<String, Map<String, Double>> attributeValues = new HashMap<>();
      if (meta.hasAttributeModifiers()) {
        sortMinecraftAttributes(attributeValues);
      }
      sortAethelAttributes(attributeValues);
      return attributeValues;
    }

    /**
     * Adds attribute {@link RpgEquipmentSlot} headers to the item's lore.
     */
    private void addAttributeHeaders() {
      for (String eSlot : headerOrder) {
        if (attributeValues.containsKey(eSlot)) {
          List<String> header = new ArrayList<>(List.of(""));
          switch (eSlot) {
            case "head" -> header.add(ChatColor.GRAY + "When on Head:");
            case "chest" -> header.add(ChatColor.GRAY + "When on Chest:");
            case "legs" -> header.add(ChatColor.GRAY + "When on Legs:");
            case "feet" -> header.add(ChatColor.GRAY + "When on Feet:");
            case "necklace" -> header.add(ChatColor.GRAY + "When on Necklace:");
            case "ring" -> header.add(ChatColor.GRAY + "When on Ring:");
            case "hand" -> header.add(ChatColor.GRAY + "When in Main Hand:");
            case "off_hand" -> header.add(ChatColor.GRAY + "When in Off Hand:");
          }
          DecimalFormat df3 = new DecimalFormat();
          df3.setMaximumFractionDigits(3);
          for (String attribute : attributeValues.get(eSlot).keySet()) {
            switch (attribute) {
              case "critical_chance", "counter_chance", "dodge_chance", "critical_damage", "item_damage", "item_cooldown", "tenacity" -> header.add(ChatColor.BLUE + "+" + df3.format(attributeValues.get(eSlot).get(attribute)) + "% " + TextFormatter.capitalizePhrase(attribute));
              default -> header.add(ChatColor.BLUE + "+" + df3.format(attributeValues.get(eSlot).get(attribute)) + " " + TextFormatter.capitalizePhrase(attribute));
            }
          }
          lore.addAll(header);
        }
      }
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    }

    /**
     * Sorts {@link Key#PASSIVE_LIST passive abilities}
     * by their {@link RpgEquipmentSlot}.
     *
     * @return {@link RpgEquipmentSlot} : {@link Key#PASSIVE_LIST passive ability}
     */
    private Map<String, List<String>> sortPassiveAbilities() {
      Map<String, List<String>> passiveAbilities = new HashMap<>();
      for (String passive : itemTags.get(Key.PASSIVE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ")) {
        String[] passiveMeta = passive.split("\\.");
        String slot = passiveMeta[0];
        String condition = passiveMeta[1];
        String type = passiveMeta[2];

        PassiveAbilityType abilityType = PassiveAbilityType.valueOf(TextFormatter.formatEnum(type));
        PassiveTriggerType triggerType = PassiveTriggerType.valueOf(TextFormatter.formatEnum(condition));
        PassiveAbilityType.Effect effect = PassiveAbilityType.valueOf(TextFormatter.formatEnum(type)).getEffect();

        String[] abilityData = itemTags.get(new NamespacedKey(Plugin.getInstance(), KeyHeader.PASSIVE.getHeader() + slot + "." + condition + "." + type), PersistentDataType.STRING).split(" ");
        StringBuilder abilityLore = new StringBuilder();

        abilityLore.append(ChatColor.DARK_AQUA);
        switch (triggerType.getCondition()) {
          case COOLDOWN -> {
            addTriggerLore(abilityLore, triggerType);
            // Cooldown
            if (!abilityData[0].equals("0")) {
              abilityLore.append(ChatColor.WHITE).append("(").append(ticksToSeconds(abilityData[0])).append("s) ");
            }
            switch (effect) {
              case BUFF -> {
                String attributeName = abilityData[2];
                if (attributeName.startsWith("generic_")) {
                  attributeName = attributeName.substring(attributeName.indexOf("_") + 1);
                }
                abilityLore.append(ChatColor.WHITE).append("Gain ").append(TextFormatter.capitalizePhrase(attributeName)).append(" (").append(abilityData[3]).append(") ").append(ChatColor.AQUA).append("Buff ").append(ChatColor.WHITE).append("(").append(ticksToSeconds(abilityData[4])).append("s) [").append(abilityData[1].equals("true") ? "Self]" : "Target]");
              }
              case STACK_INSTANCE -> abilityLore.append(ChatColor.WHITE).append("Apply ").append(abilityData[2]).append(" ").append(ChatColor.AQUA).append(abilityType.getProperName()).append(ChatColor.WHITE).append(" [").append(abilityData[1].equals("true") ? "Self] (" : "Target] (").append(ticksToSeconds(abilityData[3])).append("s)");
              case CHAIN_DAMAGE -> abilityLore.append(ChatColor.WHITE).append("Deal ").append(abilityData[2]).append(" ").append(ChatColor.AQUA).append(abilityType.getProperName()).append(ChatColor.WHITE).append(" [").append(abilityData[1].equals("true") ? "Self] (" : "Target] (").append(abilityData[3]).append("m)");
              case POTION_EFFECT -> {
                int amplifier = Integer.parseInt(abilityData[3]) + 1;
                abilityLore.append(ChatColor.WHITE).append("Gain ").append(TextFormatter.capitalizePhrase(getPotionEffectTypeAsId(abilityData[2]))).append(" ").append(amplifier).append(ChatColor.AQUA).append(" Effect ").append(ChatColor.WHITE).append("(").append(ticksToSeconds(abilityData[4])).append("s) [").append(abilityData[1].equals("true") ? "Self]" : "Target]");
              }
            }
          }
          case CHANCE_COOLDOWN -> {
            addTriggerLore(abilityLore, triggerType);
            // Chance
            if (!abilityData[0].equals("100.0")) {
              abilityLore.append(ChatColor.WHITE).append(abilityData[0]).append("% ");
            }
            // Cooldown
            if (!abilityData[1].equals("0")) {
              abilityLore.append(ChatColor.WHITE).append("(").append(ticksToSeconds(abilityData[1])).append("s) ");
            }
            switch (effect) {
              case BUFF -> {
                String attributeName = abilityData[3];
                if (attributeName.startsWith("generic_")) {
                  attributeName = attributeName.substring(attributeName.indexOf("_") + 1);
                }
                abilityLore.append(ChatColor.WHITE).append("Gain ").append(TextFormatter.capitalizePhrase(attributeName)).append(" (").append(abilityData[4]).append(") ").append(ChatColor.AQUA).append("Buff ").append(ChatColor.WHITE).append("(").append(ticksToSeconds(abilityData[5])).append("s) [").append(abilityData[2].equals("true") ? "Self]" : "Target]");
              }
              case STACK_INSTANCE -> abilityLore.append(ChatColor.WHITE).append("Apply ").append(abilityData[3]).append(" ").append(ChatColor.AQUA).append(abilityType.getProperName()).append(ChatColor.WHITE).append(" [").append(abilityData[2].equals("true") ? "Self] (" : "Target] (").append(ticksToSeconds(abilityData[4])).append("s)");
              case CHAIN_DAMAGE -> abilityLore.append(ChatColor.WHITE).append("Deal ").append(abilityData[3]).append(" ").append(ChatColor.AQUA).append(abilityType.getProperName()).append(ChatColor.WHITE).append(" [").append(abilityData[2].equals("true") ? "Self] (" : "Target] (").append(abilityData[4]).append("m)");
              case POTION_EFFECT -> {
                int amplifier = Integer.parseInt(abilityData[4]) + 1;
                abilityLore.append(ChatColor.WHITE).append("Gain ").append(TextFormatter.capitalizePhrase(getPotionEffectTypeAsId(abilityData[3]))).append(" ").append(amplifier).append(ChatColor.AQUA).append(" Effect ").append(ChatColor.WHITE).append("(").append(ticksToSeconds(abilityData[5])).append("s) [").append(abilityData[2].equals("true") ? "Self]" : "Target]");
              }
            }
          }
          case HEALTH_COOLDOWN -> {
            abilityLore.append("Below ").append(abilityData[0]).append("% HP: ");
            addTriggerLore(abilityLore, triggerType);
            // Cooldown
            if (!abilityData[1].equals("0")) {
              abilityLore.append(ChatColor.WHITE).append("(").append(ticksToSeconds(abilityData[1])).append("s) ");
            }
            switch (effect) {
              case BUFF -> {
                String attributeName = abilityData[3];
                if (attributeName.startsWith("generic_")) {
                  attributeName = attributeName.substring(attributeName.indexOf("_") + 1);
                }
                abilityLore.append(ChatColor.WHITE).append("Gain ").append(TextFormatter.capitalizePhrase(attributeName)).append(" (").append(abilityData[4]).append(") ").append(ChatColor.AQUA).append("Buff ").append(ChatColor.WHITE).append("(").append(ticksToSeconds(abilityData[5])).append("s) [").append(abilityData[2].equals("true") ? "Self]" : "Target]");
              }
              case STACK_INSTANCE -> abilityLore.append(ChatColor.WHITE).append("Apply ").append(abilityData[3]).append(" ").append(ChatColor.AQUA).append(abilityType.getProperName()).append(ChatColor.WHITE).append(" [").append(abilityData[2].equals("true") ? "Self] (" : "Target] (").append(ticksToSeconds(abilityData[4])).append("s)");
              case CHAIN_DAMAGE -> abilityLore.append(ChatColor.WHITE).append("Deal ").append(abilityData[3]).append(" ").append(ChatColor.AQUA).append(abilityType.getProperName()).append(ChatColor.WHITE).append(" [").append(abilityData[2].equals("true") ? "Self] (" : "Target] (").append(abilityData[4]).append("m)");
              case POTION_EFFECT -> {
                int amplifier = Integer.parseInt(abilityData[4]) + 1;
                abilityLore.append(ChatColor.WHITE).append("Gain ").append(TextFormatter.capitalizePhrase(getPotionEffectTypeAsId(abilityData[3]))).append(" ").append(amplifier).append(ChatColor.AQUA).append(" Effect ").append(ChatColor.WHITE).append("(").append(ticksToSeconds(abilityData[5])).append("s) [").append(abilityData[2].equals("true") ? "Self]" : "Target]");
              }
            }
          }
        }
        if (passiveAbilities.containsKey(slot)) {
          passiveAbilities.get(slot).add(abilityLore.toString());
        } else {
          passiveAbilities.put(slot, new ArrayList<>(List.of(abilityLore.toString())));
        }
      }
      return passiveAbilities;
    }

    /**
     * Adds passive ability {@link RpgEquipmentSlot} headers to the item's lore.
     */
    private void addPassiveHeaders() {
      for (String eSlot : headerOrder) {
        if (passiveAbilities.containsKey(eSlot)) {
          List<String> header = new ArrayList<>(List.of(""));
          String tag = ChatColor.GREEN + "Passives";
          switch (eSlot) {
            case "head" -> header.add(ChatColor.GRAY + "Head " + tag);
            case "chest" -> header.add(ChatColor.GRAY + "Chest " + tag);
            case "legs" -> header.add(ChatColor.GRAY + "Legs " + tag);
            case "feet" -> header.add(ChatColor.GRAY + "Feet " + tag);
            case "necklace" -> header.add(ChatColor.GRAY + "Necklace " + tag);
            case "ring" -> header.add(ChatColor.GRAY + "Ring " + tag);
            case "hand" -> header.add(ChatColor.GRAY + "Main Hand " + tag);
            case "off_hand" -> header.add(ChatColor.GRAY + "Off Hand " + tag);
          }
          header.addAll(passiveAbilities.get(eSlot));
          lore.addAll(header);
        }
      }
    }

    /**
     * Sorts {@link Key#ACTIVE_LIST active abilities}
     * by their {@link RpgEquipmentSlot}.
     *
     * @return {@link RpgEquipmentSlot} : {@link Key#ACTIVE_LIST}
     */
    private Map<String, List<String>> sortActiveAbilities() {
      Map<String, List<String>> activeAbilities = new HashMap<>();
      for (String active : itemTags.get(Key.ACTIVE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ")) {
        String slot = active.substring(0, active.indexOf("."));
        String type = active.substring(active.indexOf(".") + 1);

        ActiveAbilityType abilityType = ActiveAbilityType.valueOf(TextFormatter.formatEnum(type));
        ActiveAbilityType.Effect abilityEffect = abilityType.getEffect();

        String[] abilityData = itemTags.get(new NamespacedKey(Plugin.getInstance(), KeyHeader.ACTIVE.getHeader() + slot + "." + type), PersistentDataType.STRING).split(" ");
        StringBuilder activeLore = new StringBuilder();

        activeLore.append(ChatColor.WHITE).append("(").append(ticksToSeconds(abilityData[0])).append("s) ");
        switch (abilityEffect) {
          case BUFF -> {
            String attributeName = abilityData[1];
            if (attributeName.startsWith("generic_")) {
              attributeName = attributeName.substring(attributeName.indexOf("_") + 1);
            }
            activeLore.append("Gain ").append(TextFormatter.capitalizePhrase(attributeName)).append(" (").append(abilityData[2]).append(") ").append(ChatColor.AQUA).append("Buff ").append(ChatColor.WHITE).append("(").append(ticksToSeconds(abilityData[3])).append("s)");
          }
          case CLEAR_STATUS -> activeLore.append(ChatColor.AQUA).append(abilityType.getProperName());
          case DISPLACEMENT -> activeLore.append(ChatColor.AQUA).append(abilityType.getProperName()).append(ChatColor.WHITE).append(" ").append(abilityData[1]).append("% (").append(abilityData[2]).append("m)");
          case DISTANCE_DAMAGE -> activeLore.append("Deal ").append(abilityData[1]).append(" ").append(ChatColor.AQUA).append(abilityType.getProperName()).append(ChatColor.WHITE).append(" Damage").append(" (").append(abilityData[2]).append("m)");
          case MOVEMENT -> activeLore.append(ChatColor.AQUA).append(abilityType.getProperName()).append(ChatColor.WHITE).append(" (").append(abilityData[1]).append("%)");
          case POTION_EFFECT -> {
            int amplifier = Integer.parseInt(abilityData[2]) + 1;
            activeLore.append("Gain ").append(TextFormatter.capitalizePhrase(getPotionEffectTypeAsId(abilityData[1]))).append(" ").append(amplifier).append(ChatColor.AQUA).append(" Effect").append(ChatColor.WHITE).append(" (").append(ticksToSeconds(abilityData[3])).append("s)");
          }
          case PROJECTION -> activeLore.append(ChatColor.AQUA).append(abilityType.getProperName()).append(ChatColor.WHITE).append(" (").append(abilityData[1]).append("m) Return after (").append(ticksToSeconds(abilityData[2])).append("s)");
          case SHATTER, TELEPORT -> activeLore.append(ChatColor.AQUA).append(abilityType.getProperName()).append(ChatColor.WHITE).append(" (").append(abilityData[1]).append("m)");
        }
        if (activeAbilities.containsKey(slot)) {
          activeAbilities.get(slot).add(activeLore.toString());
        } else {
          activeAbilities.put(slot, new ArrayList<>(List.of(activeLore.toString())));
        }
      }
      return activeAbilities;
    }

    /**
     * Adds active ability {@link RpgEquipmentSlot} headers to the item's lore.
     */
    private void addActiveHeaders() {
      for (String eSlot : headerOrder) {
        if (activeAbilities.containsKey(eSlot)) {
          List<String> header = new ArrayList<>(List.of(""));
          String tag = ChatColor.YELLOW + "Actives";
          switch (eSlot) {
            case "head" -> header.add(ChatColor.GRAY + "Head " + tag);
            case "chest" -> header.add(ChatColor.GRAY + "Chest " + tag);
            case "legs" -> header.add(ChatColor.GRAY + "Legs " + tag);
            case "feet" -> header.add(ChatColor.GRAY + "Feet " + tag);
            case "necklace" -> header.add(ChatColor.GRAY + "Necklace " + tag);
            case "ring" -> header.add(ChatColor.GRAY + "Ring " + tag);
            case "hand" -> header.add(ChatColor.GRAY + "Main Hand " + tag);
            case "off_hand" -> header.add(ChatColor.GRAY + "Off Hand " + tag);
          }
          header.addAll(activeAbilities.get(eSlot));
          lore.addAll(header);
        }
      }
    }

    /**
     * Sorts Minecraft attributes by their {@link RpgEquipmentSlot}.
     *
     * @param attributeValues {@link RpgEquipmentSlot} : (attribute : value)
     */
    private void sortMinecraftAttributes(Map<String, Map<String, Double>> attributeValues) {
      for (Attribute attribute : meta.getAttributeModifiers().keySet()) {
        for (AttributeModifier attributeModifier : meta.getAttributeModifiers(attribute)) {
          String slot = attributeModifier.getSlot().name().toLowerCase();
          String name;
          switch (attribute) {
            case GENERIC_ARMOR -> name = "armor";
            case GENERIC_MAX_HEALTH -> name = "max_health";
            case GENERIC_ARMOR_TOUGHNESS -> name = "armor_toughness";
            default -> name = attribute.name().substring(8).toLowerCase();
          }
          if (attributeValues.containsKey(slot)) {
            if (attributeValues.get(slot).containsKey(name)) {
              attributeValues.get(slot).put(name, attributeValues.get(slot).get(name) + attributeModifier.getAmount());
            } else {
              attributeValues.get(slot).put(name, attributeModifier.getAmount());
            }
          } else {
            attributeValues.put(slot, new HashMap<>(Map.of(name, attributeModifier.getAmount())));
          }
        }
      }
    }

    /**
     * Sorts {@link Key#ATTRIBUTE_LIST Aethel attributes}
     * by their {@link RpgEquipmentSlot}.
     *
     * @param attributeValues {@link RpgEquipmentSlot} : (attribute : value)
     */
    private void sortAethelAttributes(Map<String, Map<String, Double>> attributeValues) {
      PersistentDataContainer itemTags = meta.getPersistentDataContainer();
      for (String attribute : meta.getPersistentDataContainer().get(Key.ATTRIBUTE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ")) {
        String slot = attribute.substring(0, attribute.indexOf("."));
        String name = attribute.substring(attribute.indexOf(".") + 1);
        NamespacedKey key = new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + attribute);
        if (attributeValues.containsKey(slot)) {
          if (attributeValues.get(slot).containsKey(name)) {
            attributeValues.get(slot).put(name, attributeValues.get(slot).get(name) + itemTags.get(key, PersistentDataType.DOUBLE));
          } else {
            attributeValues.get(slot).put(name, itemTags.get(key, PersistentDataType.DOUBLE));
          }
        } else {
          attributeValues.put(slot, new HashMap<>(Map.of(name, itemTags.get(key, PersistentDataType.DOUBLE))));
        }
      }
    }

    /**
     * Adds ability {@link PassiveTriggerType} lore.
     *
     * @param abilityLore        ability lore
     * @param passiveTriggerType {@link PassiveTriggerType}
     */
    private void addTriggerLore(StringBuilder abilityLore, PassiveTriggerType passiveTriggerType) {
      switch (passiveTriggerType) {
        case DAMAGE_DEALT -> abilityLore.append("Damage Dealt: ");
        case DAMAGE_TAKEN -> abilityLore.append("Damage Taken: ");
        case INTERVAL -> abilityLore.append("Interval: ");
        case ON_KILL -> abilityLore.append("On Kill: ");
      }
    }

    /**
     * Gets a time duration in ticks and converts it to seconds.
     *
     * @param ticks ticks
     * @return seconds
     */
    private String ticksToSeconds(String ticks) {
      return String.valueOf(Double.parseDouble(ticks) / 20);
    }

    /**
     * Gets the potion effect type as an ID.
     *
     * @param potionEffect potion effect name
     * @return potion effect ID
     */
    private String getPotionEffectTypeAsId(String potionEffect) {
      potionEffect = potionEffect.toLowerCase();
      switch (potionEffect) {
        case "confusion" -> potionEffect = "nausea";
        case "damage_resistance" -> potionEffect = "resistance";
        case "fast_digging" -> potionEffect = "haste";
        case "harm" -> potionEffect = "instant_damage";
        case "heal" -> potionEffect = "instant_health";
        case "increase_damage" -> potionEffect = "strength";
        case "jump" -> potionEffect = "leap_boost";
        case "slow" -> potionEffect = "slowness";
        case "slow_digging" -> potionEffect = "mining_fatigue";
      }
      return potionEffect;
    }
  }
}
