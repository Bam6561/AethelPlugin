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
import me.dannynguyen.aethel.listeners.MenuEvent;
import me.dannynguyen.aethel.listeners.MessageEvent;
import me.dannynguyen.aethel.plugin.PluginPlayer;
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
 * Called with {@link MenuEvent}.
 *
 * @author Danny Nguyen
 * @version 1.20.12
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
      case 9 -> setDisplayName();
      case 10 -> setCustomModelData();
      case 11 -> setDurability();
      case 12 -> setRepairCost();
      case 14 -> openAttribute();
      case 15 -> openAethelAttribute();
      case 16 -> openEnchantment();
      case 17 -> openPotion();
      case 20 -> toggleUnbreakable();
      case 23 -> openPassive();
      case 24 -> openActive();
      case 25 -> openTag();
      case 36 -> { // Lore Context
      }
      case 37 -> setLore();
      case 38 -> clearLore();
      case 45 -> addLore();
      case 46 -> editLore();
      case 47 -> removeLore();
      case 48 -> new LoreGeneration().generateLore();
      case 41, 42, 43, 44, 50, 51, 52, 53 -> toggleItemFlag();
    }
  }

  /**
   * Either changes the {@link RpgEquipmentSlot} mode or sets an item's Minecraft attribute.
   */
  public void interpretAttributeClick() {
    switch (e.getSlot()) {
      case 0, 1 -> { // Context, Item
      }
      case 2 -> returnToCosmetic();
      case 5 -> setAttributeSlot(EquipmentSlot.HEAD);
      case 6 -> setAttributeSlot(EquipmentSlot.CHEST);
      case 7 -> setAttributeSlot(EquipmentSlot.LEGS);
      case 8 -> setAttributeSlot(EquipmentSlot.FEET);
      case 14 -> setAttributeSlot(EquipmentSlot.HAND);
      case 15 -> setAttributeSlot(EquipmentSlot.OFF_HAND);
      case 18, 27, 36 -> { // Context
      }
      default -> readMinecraftAttribute();
    }
  }

  /**
   * Either changes the {@link RpgEquipmentSlot} mode or sets an
   * item's {@link Key#ATTRIBUTE_LIST Aethel attribute}.
   */
  public void interpretAethelAttributeClick() {
    switch (e.getSlot()) {
      case 0, 1 -> { // Context, Item
      }
      case 2 -> returnToCosmetic();
      case 5 -> setAethelAttributeSlot(RpgEquipmentSlot.HEAD);
      case 6 -> setAethelAttributeSlot(RpgEquipmentSlot.CHEST);
      case 7 -> setAethelAttributeSlot(RpgEquipmentSlot.LEGS);
      case 8 -> setAethelAttributeSlot(RpgEquipmentSlot.FEET);
      case 14 -> setAethelAttributeSlot(RpgEquipmentSlot.HAND);
      case 15 -> setAethelAttributeSlot(RpgEquipmentSlot.OFF_HAND);
      case 16 -> setAethelAttributeSlot(RpgEquipmentSlot.NECKLACE);
      case 17 -> setAethelAttributeSlot(RpgEquipmentSlot.RING);
      case 18, 27, 36 -> { // Context
      }
      default -> readAethelAttribute();
    }
  }

  /**
   * Sets an item's enchant.
   */
  public void interpretEnchantmentClick() {
    switch (e.getSlot()) {
      case 2, 4 -> { // Context, Item
      }
      case 6 -> returnToCosmetic();
      default -> readEnchantment();
    }
  }

  /**
   * Sets an item's potion effect.
   */
  public void interpretPotionClick() {
    switch (e.getSlot()) {
      case 2, 4 -> { // Context, Item
      }
      case 5 -> setPotionColor();
      case 6 -> returnToCosmetic();
      default -> readPotionEffect();
    }
  }

  /**
   * Sets an item's {@link Key#PASSIVE_LIST passive ability}.
   */
  public void interpretPassiveClick() {
    switch (e.getSlot()) {
      case 3, 4 -> { // Context, Item
      }
      case 5 -> returnToCosmetic();
      case 9 -> setPassiveSlot(RpgEquipmentSlot.HEAD);
      case 10 -> setPassiveSlot(RpgEquipmentSlot.CHEST);
      case 11 -> setPassiveSlot(RpgEquipmentSlot.LEGS);
      case 12 -> setPassiveSlot(RpgEquipmentSlot.FEET);
      case 14 -> setPassiveSlot(RpgEquipmentSlot.HAND);
      case 15 -> setPassiveSlot(RpgEquipmentSlot.OFF_HAND);
      case 16 -> setPassiveSlot(RpgEquipmentSlot.NECKLACE);
      case 17 -> setPassiveSlot(RpgEquipmentSlot.RING);
      case 18 -> setPassiveTrigger(PassiveTriggerType.BELOW_HEALTH);
      case 19 -> setPassiveTrigger(PassiveTriggerType.DAMAGE_DEALT);
      case 20 -> setPassiveTrigger(PassiveTriggerType.DAMAGE_TAKEN);
      case 21 -> setPassiveTrigger(PassiveTriggerType.ON_KILL);
      default -> readPassive();
    }
  }

  /**
   * Sets an item's {@link Key#ACTIVE_LIST active ability}.
   */
  public void interpretActiveClick() {
    switch (e.getSlot()) {
      case 0, 1 -> { // Context, Item
      }
      case 2 -> returnToCosmetic();
      case 5 -> setActiveMode(RpgEquipmentSlot.HEAD);
      case 6 -> setActiveMode(RpgEquipmentSlot.CHEST);
      case 7 -> setActiveMode(RpgEquipmentSlot.LEGS);
      case 8 -> setActiveMode(RpgEquipmentSlot.FEET);
      case 14 -> setActiveMode(RpgEquipmentSlot.HAND);
      case 15 -> setActiveMode(RpgEquipmentSlot.OFF_HAND);
      case 16 -> setActiveMode(RpgEquipmentSlot.NECKLACE);
      case 17 -> setActiveMode(RpgEquipmentSlot.RING);
      default -> readActive();
    }
  }

  /**
   * Sets an item's {@link Key Aethel tag}.
   */
  public void interpretTagClick() {
    switch (e.getSlot()) {
      case 2, 4 -> { // Context, Item
      }
      case 6 -> returnToCosmetic();
      default -> readTag();
    }
  }

  /**
   * Sets an item's display name.
   */
  private void setDisplayName() {
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input display name.");
    awaitMessageInput(MessageEvent.Type.ITEMEDITOR_DISPLAY_NAME);
  }

  /**
   * Sets an item's custom model data.
   */
  private void setCustomModelData() {
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input custom model data value.");
    awaitMessageInput(MessageEvent.Type.ITEMEDITOR_CUSTOM_MODEL_DATA);
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
   * Opens an {@link AttributeMenu}.
   */
  private void openAttribute() {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    pluginPlayer.setSlot(RpgEquipmentSlot.HAND);
    user.openInventory(new AttributeMenu(user, EquipmentSlot.HAND).getMainMenu());
    pluginPlayer.setMenu(MenuEvent.Menu.ITEMEDITOR_MINECRAFT_ATTRIBUTE);
  }

  /**
   * Opens an {@link AethelAttributeMenu}.
   */
  private void openAethelAttribute() {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    pluginPlayer.setSlot(RpgEquipmentSlot.HAND);
    user.openInventory(new AethelAttributeMenu(user, RpgEquipmentSlot.HAND).getMainMenu());
    pluginPlayer.setMenu(MenuEvent.Menu.ITEMEDITOR_AETHEL_ATTRIBUTE);
  }

  /**
   * Opens an {@link EnchantmentMenu}.
   */
  private void openEnchantment() {
    user.openInventory(new EnchantmentMenu(user).getMainMenu());
    Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).setMenu(MenuEvent.Menu.ITEMEDITOR_ENCHANTMENT);
  }

  /**
   * Opens a {@link PotionMenu}.
   */
  private void openPotion() {
    user.openInventory(new PotionMenu(user).getMainMenu());
    Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).setMenu(MenuEvent.Menu.ITEMEDITOR_POTION);
  }

  /**
   * Opens a {@link PassiveMenu}.
   */
  private void openPassive() {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    pluginPlayer.setSlot(RpgEquipmentSlot.HAND);
    pluginPlayer.setTrigger(PassiveTriggerType.DAMAGE_DEALT);
    user.openInventory(new PassiveMenu(user, RpgEquipmentSlot.HAND, PassiveTriggerType.DAMAGE_DEALT).getMainMenu());
    pluginPlayer.setMenu(MenuEvent.Menu.ITEMEDITOR_PASSIVE);
  }

  /**
   * Opens an {@link ActiveMenu}.
   */
  private void openActive() {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    pluginPlayer.setSlot(RpgEquipmentSlot.HAND);
    user.openInventory(new ActiveMenu(user, RpgEquipmentSlot.HAND).getMainMenu());
    pluginPlayer.setMenu(MenuEvent.Menu.ITEMEDITOR_ACTIVE);
  }

  /**
   * Opens a {@link TagMenu}.
   */
  private void openTag() {
    user.openInventory(new TagMenu(user).getMainMenu());
    Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).setMenu(MenuEvent.Menu.ITEMEDITOR_TAG);
  }

  /**
   * Sets an item's durability.
   */
  private void setDurability() {
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input durability (+) or damage (-) value.");
    awaitMessageInput(MessageEvent.Type.ITEMEDITOR_DURABILITY);
  }

  /**
   * Sets an item's repair cost.
   */
  private void setRepairCost() {
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input repair cost value.");
    awaitMessageInput(MessageEvent.Type.ITEMEDITOR_REPAIR_COST);
  }

  /**
   * Sets an item's lore.
   */
  private void setLore() {
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input lore to set.");
    awaitMessageInput(MessageEvent.Type.ITEMEDITOR_LORE_SET);
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
    awaitMessageInput(MessageEvent.Type.ITEMEDITOR_LORE_ADD);
  }

  /**
   * Edits a line of text from an item's lore.
   */
  private void editLore() {
    if (meta.hasLore()) {
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input line number and new lore.");
      awaitMessageInput(MessageEvent.Type.ITEMEDITOR_LORE_EDIT);
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
      awaitMessageInput(MessageEvent.Type.ITEMEDITOR_LORE_REMOVE);
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

  /**
   * Sets the potion's color.
   */
  private void setPotionColor() {
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input RGB value.");
    awaitMessageInput(MessageEvent.Type.ITEMEDITOR_POTION_COLOR);
  }

  /**
   * Returns to the {@link CosmeticMenu}.
   */
  private void returnToCosmetic() {
    user.openInventory(new CosmeticMenu(user).getMainMenu());
    Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).setMenu(MenuEvent.Menu.ITEMEDITOR_COSMETIC);
  }

  /**
   * Sets the user's interacting equipment slot for attributes.
   *
   * @param eSlot equipment slot
   */
  private void setAttributeSlot(EquipmentSlot eSlot) {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    pluginPlayer.setSlot(RpgEquipmentSlot.valueOf(eSlot.name()));
    user.openInventory(new AttributeMenu(user, eSlot).getMainMenu());
    pluginPlayer.setMenu(MenuEvent.Menu.ITEMEDITOR_MINECRAFT_ATTRIBUTE);
  }

  /**
   * Sets the user's interacting {@link RpgEquipmentSlot} for
   * {@link Key#ATTRIBUTE_LIST Aethel attributes}.
   *
   * @param eSlot {@link RpgEquipmentSlot}
   */
  private void setAethelAttributeSlot(RpgEquipmentSlot eSlot) {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    pluginPlayer.setSlot(eSlot);
    user.openInventory(new AethelAttributeMenu(user, eSlot).getMainMenu());
    pluginPlayer.setMenu(MenuEvent.Menu.ITEMEDITOR_AETHEL_ATTRIBUTE);
  }

  /**
   * Sets the user's interacting {@link RpgEquipmentSlot} for
   * {@link Key#PASSIVE_LIST passive abilities}.
   *
   * @param eSlot {@link RpgEquipmentSlot}
   */
  private void setPassiveSlot(RpgEquipmentSlot eSlot) {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    pluginPlayer.setSlot(eSlot);
    user.openInventory(new PassiveMenu(user, eSlot, pluginPlayer.getTrigger()).getMainMenu());
    pluginPlayer.setMenu(MenuEvent.Menu.ITEMEDITOR_PASSIVE);
  }

  /**
   * Sets the user's interacting {@link PassiveTriggerType} for
   * {@link Key#PASSIVE_LIST passive abilities}.
   *
   * @param passiveTriggerType {@link PassiveTriggerType}
   */
  private void setPassiveTrigger(PassiveTriggerType passiveTriggerType) {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    pluginPlayer.setTrigger(passiveTriggerType);
    user.openInventory(new PassiveMenu(user, pluginPlayer.getSlot(), passiveTriggerType).getMainMenu());
    pluginPlayer.setMenu(MenuEvent.Menu.ITEMEDITOR_PASSIVE);
  }

  /**
   * Sets the user's interacting {@link RpgEquipmentSlot}
   * for {@link Key#ACTIVE_LIST active abilities}.
   *
   * @param eSlot {@link RpgEquipmentSlot}
   */
  private void setActiveMode(RpgEquipmentSlot eSlot) {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    pluginPlayer.setSlot(eSlot);
    user.openInventory(new ActiveMenu(user, eSlot).getMainMenu());
    pluginPlayer.setMenu(MenuEvent.Menu.ITEMEDITOR_ACTIVE);
  }

  /**
   * Determines the Minecraft attribute to be set and prompts the user for an input.
   */
  private void readMinecraftAttribute() {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    EquipmentSlot slot = EquipmentSlot.valueOf(pluginPlayer.getSlot().name());
    String attribute = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + TextFormatter.capitalizePhrase(slot.name()) + " " + attribute + ChatColor.WHITE + " value.");
    user.sendMessage(getAttributeContext(attribute));
    pluginPlayer.setObjectType(attribute);
    awaitMessageInput(MessageEvent.Type.ITEMEDITOR_MINECRAFT_ATTRIBUTE);
  }

  /**
   * Determines the {@link AethelAttribute} to be set and prompts the user for an input.
   */
  private void readAethelAttribute() {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    RpgEquipmentSlot eSlot = pluginPlayer.getSlot();
    String attribute = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + eSlot.getProperName() + " " + attribute + ChatColor.WHITE + " value.");
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Base: " + AethelAttribute.valueOf(TextFormatter.formatEnum(attribute)).getBaseValue());
    pluginPlayer.setObjectType(TextFormatter.formatId(attribute));
    awaitMessageInput(MessageEvent.Type.ITEMEDITOR_AETHEL_ATTRIBUTE);
  }

  /**
   * Determines the enchantment to be set and prompts the user for an input.
   */
  private void readEnchantment() {
    String enchantment = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + TextFormatter.capitalizePhrase(enchantment) + ChatColor.WHITE + " value.");
    Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).setObjectType(TextFormatter.formatId(enchantment));
    awaitMessageInput(MessageEvent.Type.ITEMEDITOR_ENCHANTMENT);
  }

  /**
   * Determines the potion effect to be set and prompts the user for an input.
   */
  private void readPotionEffect() {
    String potionEffect = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + TextFormatter.capitalizePhrase(potionEffect) + ChatColor.WHITE + " duration, amplifier, and ambient.");
    Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).setObjectType(TextFormatter.formatId(potionEffect));
    awaitMessageInput(MessageEvent.Type.ITEMEDITOR_POTION_EFFECT);
  }

  /**
   * Determines the {@link PassiveAbilityType} to be set and prompts the user for an input.
   */
  private void readPassive() {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    RpgEquipmentSlot eSlot = pluginPlayer.getSlot();
    PassiveTriggerType passiveTriggerType = pluginPlayer.getTrigger();
    String passive = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + eSlot.getProperName() + " " + passiveTriggerType.getProperName() + " " + passive + ChatColor.WHITE + " ability values:");
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + passiveTriggerType.getCondition().getData());
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + PassiveAbilityType.valueOf(TextFormatter.formatEnum(passive)).getEffect().getData());
    pluginPlayer.setObjectType(TextFormatter.formatId(passive));
    awaitMessageInput(MessageEvent.Type.ITEMEDITOR_PASSIVE_ABILITY);
  }

  /**
   * Determines the {@link ActiveAbilityType} to be set and prompts the user for an input.
   */
  private void readActive() {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    RpgEquipmentSlot eSlot = pluginPlayer.getSlot();
    String active = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + eSlot.getProperName() + " " + active + ChatColor.WHITE + " ability values:");
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + ActiveAbilityType.valueOf(TextFormatter.formatEnum(active)).getEffect().getData());
    pluginPlayer.setObjectType(TextFormatter.formatId(active));
    awaitMessageInput(MessageEvent.Type.ITEMEDITOR_ACTIVE_ABILITY);
  }

  /**
   * Determines the {@link Key Aethel tag} to be set and prompts the user for an input.
   */
  private void readTag() {
    String tag = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + tag + ChatColor.WHITE + " value.");
    Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).setObjectType(tag);
    awaitMessageInput(MessageEvent.Type.ITEMEDITOR_AETHEL_TAG);
  }

  /**
   * Uses the user's next message as the field's input.
   *
   * @param messageType {@link MessageEvent.Type}
   */
  private void awaitMessageInput(MessageEvent.Type messageType) {
    user.closeInventory();
    Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).setMessageInput(messageType);
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

  /**
   * Represents an item's Minecraft and {@link Key#ATTRIBUTE_LIST Aethel attribute},
   * {@link Key#PASSIVE_LIST passive ability}, and
   * {@link Key#ACTIVE_LIST active ability} lore generation.
   *
   * @author Danny Nguyen
   * @version 1.20.11
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
    private final PersistentDataContainer dataContainer = meta.getPersistentDataContainer();

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
      if (dataContainer.has(Key.RECIPE_FORGE_ID.getNamespacedKey(), PersistentDataType.STRING)) {
        generatedLore = true;
        displayForgeId();
      }
      if (dataContainer.has(Key.ATTRIBUTE_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
        generatedLore = true;
        this.attributeValues = totalAttributeValues();
        addAttributeHeaders();
        menu.setItem(42, ItemCreator.createItem(Material.GREEN_DYE, ChatColor.AQUA + "Hide Attributes", List.of(ChatColor.GREEN + "True")));
      }
      if (dataContainer.has(Key.PASSIVE_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
        generatedLore = true;
        this.passiveAbilities = sortPassiveAbilities();
        addPassiveHeaders();
      }
      if (dataContainer.has(Key.ACTIVE_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
        generatedLore = true;
        this.activeAbilities = sortActiveAbilities();
        addActiveHeaders();
      }
      if (generatedLore) {
        user.sendMessage(ChatColor.GREEN + "[Generated Lore]");
      } else {
        user.sendMessage(ChatColor.RED + "Not modified by plugin.");
      }
    }

    /**
     * Adds the Forge ID to the item's lore.
     */
    private void displayForgeId() {
      lore.add(ChatColor.DARK_GRAY + "Forge ID: " + dataContainer.get(Key.RECIPE_FORGE_ID.getNamespacedKey(), PersistentDataType.STRING));
      meta.setLore(lore);
      item.setItemMeta(meta);
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
              case "critical_chance", "counter_chance", "dodge_chance", "critical_damage", "item_damage", "item_cooldown", "tenacity" -> header.add(ChatColor.DARK_GREEN + "+" + df3.format(attributeValues.get(eSlot).get(attribute)) + "% " + TextFormatter.capitalizePhrase(attribute));
              default -> header.add(ChatColor.DARK_GREEN + "+" + df3.format(attributeValues.get(eSlot).get(attribute)) + " " + TextFormatter.capitalizePhrase(attribute));
            }
          }
          lore.addAll(header);
        }
      }
      meta.setLore(lore);
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      item.setItemMeta(meta);
    }

    /**
     * Sorts {@link Key#PASSIVE_LIST passive abilities}
     * by their {@link RpgEquipmentSlot}.
     *
     * @return {@link RpgEquipmentSlot} : {@link Key#PASSIVE_LIST passive ability}
     */
    private Map<String, List<String>> sortPassiveAbilities() {
      Map<String, List<String>> passiveAbilities = new HashMap<>();
      for (String passive : dataContainer.get(Key.PASSIVE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ")) {
        String[] passiveMeta = passive.split("\\.");
        String slot = passiveMeta[0];
        String condition = passiveMeta[1];
        String type = passiveMeta[2];

        PassiveAbilityType abilityType = PassiveAbilityType.valueOf(TextFormatter.formatEnum(type));
        PassiveTriggerType triggerType = PassiveTriggerType.valueOf(TextFormatter.formatEnum(condition));
        PassiveAbilityType.Effect effect = PassiveAbilityType.valueOf(TextFormatter.formatEnum(type)).getEffect();

        String[] abilityData = dataContainer.get(new NamespacedKey(Plugin.getInstance(), KeyHeader.PASSIVE.getHeader() + slot + "." + condition + "." + type), PersistentDataType.STRING).split(" ");
        StringBuilder abilityLore = new StringBuilder();

        abilityLore.append(ChatColor.DARK_AQUA);
        switch (triggerType.getCondition()) {
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
                abilityLore.append(ChatColor.WHITE).append("Gain ").append(TextFormatter.capitalizePhrase(getPotionEffectTypeAsId(abilityData[3]))).append(" ").append(amplifier).append(ChatColor.AQUA).append("Effect ").append(ChatColor.WHITE).append("(").append(ticksToSeconds(abilityData[5])).append("s) [").append(abilityData[2].equals("true") ? "Self]" : "Target]");
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
                abilityLore.append(ChatColor.WHITE).append("Gain ").append(TextFormatter.capitalizePhrase(getPotionEffectTypeAsId(abilityData[3]))).append(" ").append(amplifier).append(ChatColor.AQUA).append("Effect ").append(ChatColor.WHITE).append("(").append(ticksToSeconds(abilityData[5])).append("s) [").append(abilityData[2].equals("true") ? "Self]" : "Target]");
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
          String tag = ChatColor.BLUE + "Passives";
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
      meta.setLore(lore);
      item.setItemMeta(meta);
    }

    /**
     * Sorts {@link Key#ACTIVE_LIST active abilities}
     * by their {@link RpgEquipmentSlot}.
     *
     * @return {@link RpgEquipmentSlot} : {@link Key#ACTIVE_LIST}
     */
    private Map<String, List<String>> sortActiveAbilities() {
      Map<String, List<String>> activeAbilities = new HashMap<>();
      for (String active : dataContainer.get(Key.ACTIVE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ")) {
        String slot = active.substring(0, active.indexOf("."));
        String type = active.substring(active.indexOf(".") + 1);

        ActiveAbilityType abilityType = ActiveAbilityType.valueOf(TextFormatter.formatEnum(type));
        ActiveAbilityType.Effect abilityEffect = abilityType.getEffect();

        String[] abilityData = dataContainer.get(new NamespacedKey(Plugin.getInstance(), KeyHeader.ACTIVE.getHeader() + slot + "." + type), PersistentDataType.STRING).split(" ");
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
          case DISTANCE_DAMAGE -> activeLore.append("Deal ").append(abilityData[1]).append(" ").append(ChatColor.AQUA).append(abilityType.getProperName()).append(ChatColor.WHITE).append(" Damage").append(" (").append(abilityData[2]).append("m)");
          case MOVEMENT -> activeLore.append(ChatColor.AQUA).append(abilityType.getProperName()).append(ChatColor.WHITE).append(" (").append(abilityData[1]).append("%)");
          case POTION_EFFECT -> {
            int amplifier = Integer.parseInt(abilityData[2] + 1);
            activeLore.append("Gain ").append(TextFormatter.capitalizePhrase(getPotionEffectTypeAsId(abilityData[1]))).append(" ").append(amplifier).append(ChatColor.AQUA).append("Effect ").append(ChatColor.WHITE).append(" (").append(ticksToSeconds(abilityData[3])).append("s)");
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
      meta.setLore(lore);
      item.setItemMeta(meta);
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
            case GENERIC_MAX_HEALTH -> name = "max_hp";
            case GENERIC_ARMOR_TOUGHNESS -> name = "toughness";
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
      PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
      for (String attribute : meta.getPersistentDataContainer().get(Key.ATTRIBUTE_LIST.getNamespacedKey(), PersistentDataType.STRING).split(" ")) {
        String slot = attribute.substring(0, attribute.indexOf("."));
        String name = attribute.substring(attribute.indexOf(".") + 1);
        NamespacedKey key = new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + attribute);
        if (attributeValues.containsKey(slot)) {
          if (attributeValues.get(slot).containsKey(name)) {
            attributeValues.get(slot).put(name, attributeValues.get(slot).get(name) + dataContainer.get(key, PersistentDataType.DOUBLE));
          } else {
            attributeValues.get(slot).put(name, dataContainer.get(key, PersistentDataType.DOUBLE));
          }
        } else {
          attributeValues.put(slot, new HashMap<>(Map.of(name, dataContainer.get(key, PersistentDataType.DOUBLE))));
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
