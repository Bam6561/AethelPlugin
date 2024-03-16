package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.systems.plugin.MenuMeta;
import me.dannynguyen.aethel.systems.plugin.Message;
import me.dannynguyen.aethel.systems.plugin.PlayerMeta;
import me.dannynguyen.aethel.systems.plugin.PluginNamespacedKey;
import me.dannynguyen.aethel.systems.rpg.*;
import me.dannynguyen.aethel.utility.ItemCreator;
import me.dannynguyen.aethel.utility.ItemReader;
import me.dannynguyen.aethel.utility.TextFormatter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

import java.util.*;

/**
 * Inventory click event listener for ItemEditor menus.
 *
 * @author Danny Nguyen
 * @version 1.16.6
 * @since 1.6.7
 */
public class ItemEditorMenuClick {
  /**
   * Inventory click event.
   */
  private final InventoryClickEvent e;

  /**
   * ItemEditor GUI.
   */
  private final Inventory menu;

  /**
   * Player who clicked.
   */
  private final Player user;

  /**
   * User's UUID.
   */
  private final UUID userUUID;

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
  private final int slotClicked;

  /**
   * Associates an inventory click event with its user in the context of an open ItemEditor menu.
   *
   * @param e inventory click event
   */
  public ItemEditorMenuClick(@NotNull InventoryClickEvent e) {
    this.e = Objects.requireNonNull(e, "Null inventory click event");
    this.menu = e.getInventory();
    this.user = (Player) e.getWhoClicked();
    this.userUUID = user.getUniqueId();
    this.item = Plugin.getData().getEditedItemCache().getEditedItemMap().get(user.getUniqueId());
    this.meta = item.getItemMeta();
    this.slotClicked = e.getSlot();
  }

  /**
   * Sets an item's cosmetic metadata or opens a gameplay metadata editor menu.
   */
  public void interpretCosmeticClick() {
    switch (slotClicked) {
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
      case 48 -> generateLore();
      case 41, 42, 43, 44, 50, 51, 52, 53 -> toggleItemFlag();
    }
  }

  /**
   * Either changes the equipment slot mode or sets an item's Minecraft attribute.
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
   * Either changes the RPG equipment slot mode or sets an item's Aethel attribute.
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
   * Sets an item's passive ability.
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
      case 9 -> setTriggerMode(Trigger.BELOW_HP);
      case 10 -> setTriggerMode(Trigger.DAMAGE_DEALT);
      case 11 -> setTriggerMode(Trigger.DAMAGE_TAKEN);
      case 12 -> setTriggerMode(Trigger.KILL);
      default -> readPassive();
    }
  }

  /**
   * Sets an item's active ability.
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
   * Sets an item's Aethel tag.
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
    awaitMessageResponse("display_name");
  }

  /**
   * Sets an item's custom model data.
   */
  private void setCustomModelData() {
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input custom model data value.");
    awaitMessageResponse("custom_model_data");
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
   * Opens an Attribute menu.
   */
  private void openAttribute() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    playerMeta.remove(PlayerMeta.MESSAGE);
    playerMeta.put(PlayerMeta.SLOT, "head");
    user.openInventory(new AttributeMenu(user, EquipmentSlot.HEAD).openMenu());
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_MINECRAFT_ATTRIBUTE.getMeta());
  }

  /**
   * Opens an AethelAttribute menu.
   */
  private void openAethelAttribute() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    playerMeta.remove(PlayerMeta.MESSAGE);
    playerMeta.put(PlayerMeta.SLOT, "head");
    user.openInventory(new AethelAttributeMenu(user, RpgEquipmentSlot.HEAD).openMenu());
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_AETHEL_ATTRIBUTE.getMeta());
  }

  /**
   * Opens an Enchantment menu.
   */
  private void openEnchantment() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    playerMeta.remove(PlayerMeta.MESSAGE);
    user.openInventory(new EnchantmentMenu(user).openMenu());
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_ENCHANTMENT.getMeta());
  }

  /**
   * Opens a Potion menu.
   */
  private void openPotion() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    playerMeta.remove(PlayerMeta.MESSAGE);
    user.openInventory(new PotionMenu(user).openMenu());
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_POTION.getMeta());
  }

  /**
   * Opens a Passive menu.
   */
  private void openPassive() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    playerMeta.remove(PlayerMeta.MESSAGE);
    playerMeta.put(PlayerMeta.CONDITION, "below_hp");
    playerMeta.put(PlayerMeta.SLOT, "head");
    user.openInventory(new PassiveMenu(user, RpgEquipmentSlot.HEAD, Trigger.BELOW_HP).openMenu());
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_PASSIVE.getMeta());
  }

  /**
   * Opens an Active menu.
   */
  private void openActive() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    playerMeta.remove(PlayerMeta.MESSAGE);
    playerMeta.put(PlayerMeta.SLOT, "head");
    user.openInventory(new ActiveMenu(user, RpgEquipmentSlot.HEAD).openMenu());
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_ACTIVE.getMeta());
  }

  /**
   * Opens a Tag menu.
   */
  private void openTag() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    playerMeta.remove(PlayerMeta.MESSAGE);
    user.openInventory(new TagMenu(user).openMenu());
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_TAG.getMeta());
  }

  /**
   * Sets an item's durability.
   */
  private void setDurability() {
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input durability (+) or damage (-) value.");
    awaitMessageResponse("durability");
  }

  /**
   * Sets an item's repair cost.
   */
  private void setRepairCost() {
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input repair cost value.");
    awaitMessageResponse("repair_cost");
  }

  /**
   * Sets an item's lore.
   */
  private void setLore() {
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input lore to set.");
    awaitMessageResponse("lore-set");
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
      user.sendMessage(ChatColor.RED + "Item has no lore.");
    }
  }

  /**
   * Adds a line of text to an item's lore.
   */
  private void addLore() {
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input lore to add.");
    awaitMessageResponse("lore-add");
  }

  /**
   * Edits a line of text from an item's lore.
   */
  private void editLore() {
    if (meta.hasLore()) {
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input line number and new lore.");
      awaitMessageResponse("lore-edit");
    } else {
      user.sendMessage(ChatColor.RED + "Item has no lore.");
    }
  }

  /**
   * Removes a line of text from an item's lore.
   */
  private void removeLore() {
    if (meta.hasLore()) {
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input line number to remove.");
      awaitMessageResponse("lore-remove");
    } else {
      user.sendMessage(ChatColor.RED + "Item has no lore.");
    }
  }

  /**
   * Generates an item's lore based on its plugin-related data.
   */
  private void generateLore() {
    boolean generatedLore = false;
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();

    if (dataContainer.has(PluginNamespacedKey.RECIPE_FORGE_ID.getNamespacedKey(), PersistentDataType.STRING)) {
      generatedLore = true;
      displayForgeId(dataContainer);
    }
    if (dataContainer.has(PluginNamespacedKey.ATTRIBUTE_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
      generatedLore = true;
      new ItemAttributeLore(item).addAttributeHeaders();
      menu.setItem(42, ItemCreator.createItem(Material.GREEN_DYE, ChatColor.AQUA + "Hide Attributes", List.of(ChatColor.GREEN + "True")));
    }
    if (dataContainer.has(PluginNamespacedKey.PASSIVE_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
      generatedLore = true;
      new ItemPassiveLore(item).addPassiveHeaders();
    }
    if (dataContainer.has(PluginNamespacedKey.ACTIVE_LIST.getNamespacedKey(), PersistentDataType.STRING)) {
      generatedLore = true;
      new ItemActiveLore(item).addActiveHeaders();
    }

    if (generatedLore) {
      user.sendMessage(ChatColor.GREEN + "[Generated Lore]");
    } else {
      user.sendMessage(ChatColor.RED + "Not modified by plugin.");
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
    awaitMessageResponse("potion-color");
  }

  /**
   * Returns to the Cosmetic menu.
   */
  private void returnToCosmetic() {
    user.openInventory(new CosmeticMenu(user).openMenu());
    Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_COSMETIC.getMeta());
  }

  /**
   * Sets the user's interacting equipment slot for attributes.
   *
   * @param slot equipment slot
   */
  private void setAttributeMode(EquipmentSlot slot) {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    playerMeta.put(PlayerMeta.SLOT, slot.name().toLowerCase());
    user.openInventory(new AttributeMenu(user, slot).openMenu());
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_MINECRAFT_ATTRIBUTE.getMeta());
  }

  /**
   * Sets the user's interacting RPG equipment slot for Aethel attributes.
   *
   * @param slot equipment slot
   */
  private void setAethelAttributeMode(RpgEquipmentSlot slot) {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    String rpgEquipmentSlot = slot.name().toLowerCase();
    playerMeta.put(PlayerMeta.SLOT, rpgEquipmentSlot);
    user.openInventory(new AethelAttributeMenu(user, slot).openMenu());
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_AETHEL_ATTRIBUTE.getMeta());
  }

  /**
   * Sets the user's interacting equipment slot for passive abilities.
   *
   * @param slot equipment slot
   */
  private void setPassiveMode(RpgEquipmentSlot slot) {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    playerMeta.put(PlayerMeta.SLOT, slot.name().toLowerCase());
    user.openInventory(new PassiveMenu(user, slot, Trigger.valueOf(playerMeta.get(PlayerMeta.CONDITION).toUpperCase())).openMenu());
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_PASSIVE.getMeta());
  }

  /**
   * Sets the user's interacting trigger type for passive abilities.
   *
   * @param trigger trigger type
   */
  private void setTriggerMode(Trigger trigger) {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    playerMeta.put(PlayerMeta.CONDITION, trigger.name().toLowerCase());
    user.openInventory(new PassiveMenu(user, RpgEquipmentSlot.valueOf(playerMeta.get(PlayerMeta.SLOT).toUpperCase()), trigger).openMenu());
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_PASSIVE.getMeta());
  }

  /**
   * Sets the user's interacting equipment slot for active abilities.
   *
   * @param slot equipment slot
   */
  private void setActiveMode(RpgEquipmentSlot slot) {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    playerMeta.put(PlayerMeta.SLOT, slot.name().toLowerCase());
    user.openInventory(new ActiveMenu(user, slot).openMenu());
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_ACTIVE.getMeta());
  }

  /**
   * Determines the Minecraft attribute to be set and prompts the user for an input.
   */
  private void readMinecraftAttribute() {
    String attribute = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + attribute + ChatColor.WHITE + " value.");
    user.sendMessage(getAttributeContext(attribute));
    Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).put(PlayerMeta.TYPE, attribute);
    awaitMessageResponse("minecraft_attribute");
  }

  /**
   * Determines the Aethel attribute to be set and prompts the user for an input.
   */
  private void readAethelAttribute() {
    String attribute = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + attribute + ChatColor.WHITE + " value.");
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Base: " + AethelAttribute.valueOf(TextFormatter.formatEnum(attribute)).getBaseValue());
    Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).put(PlayerMeta.TYPE, TextFormatter.formatId(attribute));
    awaitMessageResponse("aethel_attribute");
  }

  /**
   * Determines the enchantment to be set and prompts the user for an input.
   */
  private void readEnchantment() {
    String enchantment = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + TextFormatter.capitalizePhrase(enchantment) + ChatColor.WHITE + " value.");
    Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).put(PlayerMeta.TYPE, TextFormatter.formatId(enchantment));
    awaitMessageResponse("enchantment");
  }

  /**
   * Determines the potion effect to be set and prompts the user for an input.
   */
  private void readPotionEffect() {
    String potionEffect = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + TextFormatter.capitalizePhrase(potionEffect) + ChatColor.WHITE + " duration, amplifier, and ambient.");
    Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).put(PlayerMeta.TYPE, TextFormatter.formatId(potionEffect));
    awaitMessageResponse("potion-effect");
  }

  /**
   * Determines the passive ability to be set and prompts the user for an input.
   */
  private void readPassive() {
    Trigger trigger = Trigger.valueOf(TextFormatter.formatEnum(Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).get(PlayerMeta.CONDITION)));
    String passive = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + trigger.getProperName() + " " + passive + ChatColor.WHITE + " ability values:");
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + trigger.getCondition().getData());
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + PassiveAbilityType.valueOf(TextFormatter.formatEnum(passive)).getEffect().getData());
    Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).put(PlayerMeta.TYPE, TextFormatter.formatId(passive));
    awaitMessageResponse("passive_ability");
  }

  /**
   * Determines the active ability to be set and prompts the user for an input.
   */
  private void readActive() {
    String active = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + active + ChatColor.WHITE + " ability values:");
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + ActiveAbilityType.valueOf(TextFormatter.formatEnum(active)).getEffect().getData());
    Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).put(PlayerMeta.TYPE, TextFormatter.formatId(active));
    awaitMessageResponse("active_ability");
  }

  /**
   * Determines the Aethel tag to be set and prompts the user for an input.
   */
  private void readTag() {
    String tag = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
    user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + tag + ChatColor.WHITE + " value.");
    Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).put(PlayerMeta.TYPE, tag);
    awaitMessageResponse("tag");
  }

  /**
   * Uses the user's next message as the field's input.
   *
   * @param metadata metadata field
   */
  private void awaitMessageResponse(String metadata) {
    user.closeInventory();
    Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).put(PlayerMeta.MESSAGE, "itemeditor." + metadata);
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

  /**
   * Adds the forge ID to the item's lore.
   *
   * @param dataContainer item's persistent tags
   */
  private void displayForgeId(PersistentDataContainer dataContainer) {
    List<String> lore;
    if (meta.hasLore()) {
      lore = meta.getLore();
    } else {
      lore = new ArrayList<>();
    }
    lore.add(ChatColor.DARK_GRAY + "Forge ID: " + dataContainer.get(PluginNamespacedKey.RECIPE_FORGE_ID.getNamespacedKey(), PersistentDataType.STRING));
    meta.setLore(lore);
    item.setItemMeta(meta);
  }
}
