package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.rpg.*;
import me.dannynguyen.aethel.enums.rpg.abilities.ActiveType;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveType;
import me.dannynguyen.aethel.enums.rpg.abilities.TriggerType;
import me.dannynguyen.aethel.plugin.interfaces.MenuClick;
import me.dannynguyen.aethel.listeners.MenuEvent;
import me.dannynguyen.aethel.listeners.MessageEvent;
import me.dannynguyen.aethel.plugin.system.PluginPlayer;
import me.dannynguyen.aethel.util.ItemReader;
import me.dannynguyen.aethel.util.TextFormatter;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

/**
 * Inventory click event listener for {@link ItemEditorCommand} menus.
 * <p>
 * Called with {@link MenuEvent}.
 *
 * @author Danny Nguyen
 * @version 1.18.0
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
      case 48 -> new LoreGeneration(user, menu, item).generateLore();
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
      case 5 -> setAttributeMode(EquipmentSlot.HEAD);
      case 6 -> setAttributeMode(EquipmentSlot.CHEST);
      case 7 -> setAttributeMode(EquipmentSlot.LEGS);
      case 8 -> setAttributeMode(EquipmentSlot.FEET);
      case 14 -> setAttributeMode(EquipmentSlot.HAND);
      case 15 -> setAttributeMode(EquipmentSlot.OFF_HAND);
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
      case 5 -> setAethelAttributeMode(RpgEquipmentSlot.HEAD);
      case 6 -> setAethelAttributeMode(RpgEquipmentSlot.CHEST);
      case 7 -> setAethelAttributeMode(RpgEquipmentSlot.LEGS);
      case 8 -> setAethelAttributeMode(RpgEquipmentSlot.FEET);
      case 14 -> setAethelAttributeMode(RpgEquipmentSlot.HAND);
      case 15 -> setAethelAttributeMode(RpgEquipmentSlot.OFF_HAND);
      case 16 -> setAethelAttributeMode(RpgEquipmentSlot.NECKLACE);
      case 17 -> setAethelAttributeMode(RpgEquipmentSlot.RING);
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
      case 0, 1 -> { // Context, Item
      }
      case 2 -> returnToCosmetic();
      case 5 -> setPassiveMode(RpgEquipmentSlot.HEAD);
      case 6 -> setPassiveMode(RpgEquipmentSlot.CHEST);
      case 7 -> setPassiveMode(RpgEquipmentSlot.LEGS);
      case 8 -> setPassiveMode(RpgEquipmentSlot.FEET);
      case 14 -> setPassiveMode(RpgEquipmentSlot.HAND);
      case 15 -> setPassiveMode(RpgEquipmentSlot.OFF_HAND);
      case 16 -> setPassiveMode(RpgEquipmentSlot.NECKLACE);
      case 17 -> setPassiveMode(RpgEquipmentSlot.RING);
      case 9 -> setTriggerMode(TriggerType.BELOW_HEALTH);
      case 10 -> setTriggerMode(TriggerType.DAMAGE_DEALT);
      case 11 -> setTriggerMode(TriggerType.DAMAGE_TAKEN);
      case 12 -> setTriggerMode(TriggerType.ON_KILL);
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
    awaitMessageResponse(MessageEvent.Type.ITEMEDITOR_DISPLAY_NAME);
  }

  /**
   * Sets an item's custom model data.
   */
  private void setCustomModelData() {
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input custom model data value.");
    awaitMessageResponse(MessageEvent.Type.ITEMEDITOR_CUSTOM_MODEL_DATA);
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
    pluginPlayer.setTrigger(TriggerType.DAMAGE_DEALT);
    user.openInventory(new PassiveMenu(user, RpgEquipmentSlot.HAND, TriggerType.DAMAGE_DEALT).getMainMenu());
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
    awaitMessageResponse(MessageEvent.Type.ITEMEDITOR_DURABILITY);
  }

  /**
   * Sets an item's repair cost.
   */
  private void setRepairCost() {
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input repair cost value.");
    awaitMessageResponse(MessageEvent.Type.ITEMEDITOR_REPAIR_COST);
  }

  /**
   * Sets an item's lore.
   */
  private void setLore() {
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input lore to set.");
    awaitMessageResponse(MessageEvent.Type.ITEMEDITOR_LORE_SET);
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
    awaitMessageResponse(MessageEvent.Type.ITEMEDITOR_LORE_ADD);
  }

  /**
   * Edits a line of text from an item's lore.
   */
  private void editLore() {
    if (meta.hasLore()) {
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input line number and new lore.");
      awaitMessageResponse(MessageEvent.Type.ITEMEDITOR_LORE_EDIT);
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
      awaitMessageResponse(MessageEvent.Type.ITEMEDITOR_LORE_REMOVE);
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
    awaitMessageResponse(MessageEvent.Type.ITEMEDITOR_POTION_COLOR);
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
  private void setAttributeMode(EquipmentSlot eSlot) {
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
  private void setAethelAttributeMode(RpgEquipmentSlot eSlot) {
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
  private void setPassiveMode(RpgEquipmentSlot eSlot) {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    pluginPlayer.setSlot(eSlot);
    user.openInventory(new PassiveMenu(user, eSlot, pluginPlayer.getTrigger()).getMainMenu());
    pluginPlayer.setMenu(MenuEvent.Menu.ITEMEDITOR_PASSIVE);
  }

  /**
   * Sets the user's interacting {@link TriggerType} for
   * {@link Key#PASSIVE_LIST passive abilities}.
   *
   * @param triggerType {@link TriggerType}
   */
  private void setTriggerMode(TriggerType triggerType) {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    pluginPlayer.setTrigger(triggerType);
    user.openInventory(new PassiveMenu(user, pluginPlayer.getSlot(), triggerType).getMainMenu());
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
    awaitMessageResponse(MessageEvent.Type.ITEMEDITOR_MINECRAFT_ATTRIBUTE);
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
    awaitMessageResponse(MessageEvent.Type.ITEMEDITOR_AETHEL_ATTRIBUTE);
  }

  /**
   * Determines the enchantment to be set and prompts the user for an input.
   */
  private void readEnchantment() {
    String enchantment = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + TextFormatter.capitalizePhrase(enchantment) + ChatColor.WHITE + " value.");
    Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).setObjectType(TextFormatter.formatId(enchantment));
    awaitMessageResponse(MessageEvent.Type.ITEMEDITOR_ENCHANTMENT);
  }

  /**
   * Determines the potion effect to be set and prompts the user for an input.
   */
  private void readPotionEffect() {
    String potionEffect = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + TextFormatter.capitalizePhrase(potionEffect) + ChatColor.WHITE + " duration, amplifier, and ambient.");
    Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).setObjectType(TextFormatter.formatId(potionEffect));
    awaitMessageResponse(MessageEvent.Type.ITEMEDITOR_POTION_EFFECT);
  }

  /**
   * Determines the {@link PassiveType} to be set and prompts the user for an input.
   */
  private void readPassive() {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    RpgEquipmentSlot eSlot = pluginPlayer.getSlot();
    TriggerType triggerType = pluginPlayer.getTrigger();
    String passive = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + eSlot.getProperName() + " " + triggerType.getProperName() + " " + passive + ChatColor.WHITE + " ability values:");
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + triggerType.getCondition().getData());
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + PassiveType.valueOf(TextFormatter.formatEnum(passive)).getEffect().getData());
    pluginPlayer.setObjectType(TextFormatter.formatId(passive));
    awaitMessageResponse(MessageEvent.Type.ITEMEDITOR_PASSIVE_ABILITY);
  }

  /**
   * Determines the {@link ActiveType} to be set and prompts the user for an input.
   */
  private void readActive() {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    RpgEquipmentSlot eSlot = pluginPlayer.getSlot();
    String active = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + eSlot.getProperName() + " " + active + ChatColor.WHITE + " ability values:");
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + ActiveType.valueOf(TextFormatter.formatEnum(active)).getEffect().getData());
    pluginPlayer.setObjectType(TextFormatter.formatId(active));
    awaitMessageResponse(MessageEvent.Type.ITEMEDITOR_ACTIVE_ABILITY);
  }

  /**
   * Determines the {@link Key Aethel tag} to be set and prompts the user for an input.
   */
  private void readTag() {
    String tag = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + tag + ChatColor.WHITE + " value.");
    Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).setObjectType(tag);
    awaitMessageResponse(MessageEvent.Type.ITEMEDITOR_AETHEL_TAG);
  }

  /**
   * Uses the user's next message as the field's input.
   *
   * @param messageType {@link MessageEvent.Type}
   */
  private void awaitMessageResponse(MessageEvent.Type messageType) {
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
        return context + "2.0 [Input * 20]";
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
