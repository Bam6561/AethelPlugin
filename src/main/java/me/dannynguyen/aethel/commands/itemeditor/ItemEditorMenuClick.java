package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.systems.plugin.PluginConstant;
import me.dannynguyen.aethel.systems.plugin.PluginData;
import me.dannynguyen.aethel.systems.plugin.enums.MenuMeta;
import me.dannynguyen.aethel.systems.plugin.enums.PlayerMeta;
import me.dannynguyen.aethel.systems.plugin.enums.PluginMessage;
import me.dannynguyen.aethel.systems.plugin.enums.PluginNamespacedKey;
import me.dannynguyen.aethel.utility.ItemReader;
import me.dannynguyen.aethel.utility.TextFormatter;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
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
 * @version 1.13.7
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
    this.item = PluginData.editedItemCache.getEditedItemMap().get(user.getUniqueId());
    this.meta = item.getItemMeta();
    this.slotClicked = e.getSlot();
  }

  /**
   * Sets an item's cosmetic metadata or opens a gameplay metadata editor menu.
   */
  public void interpretCosmeticEditorClick() {
    switch (slotClicked) {
      case 0, 1 -> { // Color Code Context
      }
      case 9 -> setDisplayName();
      case 10 -> setCustomModelData();
      case 11 -> setDurability();
      case 12 -> setRepairCost();
      case 14 -> openAttributeEditor();
      case 15 -> openEnchantmentEditor();
      case 16 -> openTagEditor();
      case 20 -> toggleUnbreakable();
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
   * Either changes the equipment slot mode or sets an item's attribute.
   */
  public void interpretAttributeEditorClick() {
    switch (e.getSlot()) {
      case 0, 1 -> { // Context, Item
      }
      case 2 -> returnToCosmeticEditor();
      case 5 -> setMode(AttributeEditorAction.HEAD);
      case 6 -> setMode(AttributeEditorAction.CHEST);
      case 7 -> setMode(AttributeEditorAction.LEGS);
      case 8 -> setMode(AttributeEditorAction.FEET);
      case 14 -> setMode(AttributeEditorAction.HAND);
      case 15 -> setMode(AttributeEditorAction.OFF_HAND);
      case 16 -> setMode(AttributeEditorAction.NECKLACE);
      case 17 -> setMode(AttributeEditorAction.RING);
      default -> readAttribute();
    }
  }

  /**
   * Sets an item's enchant.
   */
  public void interpretEnchantmentEditorClick() {
    switch (e.getSlot()) {
      case 2, 4 -> { // Context, Item
      }
      case 6 -> returnToCosmeticEditor();
      default -> readEnchantment();
    }
  }

  /**
   * Sets an item's Aethel tag.
   */
  public void interpretTagEditorClick() {
    switch (e.getSlot()) {
      case 2, 4 -> { // Context, Item
      }
      case 6 -> returnToCosmeticEditor();
      default -> readTag();
    }
  }

  /**
   * Sets an item's display name.
   */
  private void setDisplayName() {
    user.sendMessage(PluginMessage.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input display name.");
    awaitMessageResponse("display_name");
  }

  /**
   * Sets an item's custom model data.
   */
  private void setCustomModelData() {
    user.sendMessage(PluginMessage.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input custom model data value.");
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
    CosmeticEditorMenu.addUnbreakable(menu, meta);
  }

  /**
   * Opens an AttributeEditor menu.
   */
  private void openAttributeEditor() {
    Map<PlayerMeta, String> playerMeta = PluginData.pluginSystem.getPlayerMetadata().get(userUUID);
    playerMeta.remove(PlayerMeta.MESSAGE);
    playerMeta.put(PlayerMeta.SLOT, "head");
    user.openInventory(new AttributeEditorMenu(user, AttributeEditorAction.HEAD).openMenu());
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_ATTRIBUTE.getMeta());
  }

  /**
   * Opens an EnchantmentEditor menu.
   */
  private void openEnchantmentEditor() {
    Map<PlayerMeta, String> playerMeta = PluginData.pluginSystem.getPlayerMetadata().get(userUUID);
    playerMeta.remove(PlayerMeta.MESSAGE);
    user.openInventory(new EnchantmentEditorMenu(user).openMenu());
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_ENCHANTMENT.getMeta());
  }

  /**
   * Opens a TagEditor menu.
   */
  private void openTagEditor() {
    Map<PlayerMeta, String> playerMeta = PluginData.pluginSystem.getPlayerMetadata().get(userUUID);
    playerMeta.remove(PlayerMeta.MESSAGE);
    user.openInventory(new TagEditorMenu(user).openMenu());
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_TAG.getMeta());
  }

  /**
   * Sets an item's durability.
   */
  private void setDurability() {
    user.sendMessage(PluginMessage.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input durability (+) or damage (-) value.");
    awaitMessageResponse("durability");
  }

  /**
   * Sets an item's repair cost.
   */
  private void setRepairCost() {
    user.sendMessage(PluginMessage.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input repair cost value.");
    awaitMessageResponse("repair_cost");
  }

  /**
   * Sets an item's lore.
   */
  private void setLore() {
    user.sendMessage(PluginMessage.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input lore to set.");
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
    user.sendMessage(PluginMessage.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input lore to add.");
    awaitMessageResponse("lore-add");
  }

  /**
   * Edits a line of text from an item's lore.
   */
  private void editLore() {
    if (meta.hasLore()) {
      user.sendMessage(PluginMessage.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input line number and new lore.");
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
      user.sendMessage(PluginMessage.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input line number to remove.");
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

    NamespacedKey recipeId = PluginNamespacedKey.RECIPE_ID.getNamespacedKey();
    if (dataContainer.has(recipeId, PersistentDataType.STRING)) {
      generatedLore = true;
      displayRecipeId(dataContainer, recipeId);
    }

    NamespacedKey listKey = PluginNamespacedKey.ATTRIBUTE_LIST.getNamespacedKey();
    if (dataContainer.has(listKey, PersistentDataType.STRING)) {
      generatedLore = true;
      new ItemAttributeTotals(item, new ArrayList<>(List.of(dataContainer.get(listKey, PersistentDataType.STRING).split(" ")))).addAttributeHeaders();
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
      case HIDE_ARMOR_TRIM -> CosmeticEditorMenu.addHideArmorTrim(menu, meta);
      case HIDE_ATTRIBUTES -> CosmeticEditorMenu.addHideAttributes(menu, meta);
      case HIDE_DESTROYS -> CosmeticEditorMenu.addHideDestroys(menu, meta);
      case HIDE_DYE -> CosmeticEditorMenu.addHideDye(menu, meta);
      case HIDE_ENCHANTS -> CosmeticEditorMenu.addHideEnchants(menu, meta);
      case HIDE_PLACED_ON -> CosmeticEditorMenu.addHidePlacedOn(menu, meta);
      case HIDE_POTION_EFFECTS -> CosmeticEditorMenu.addHidePotionEffects(menu, meta);
      case HIDE_UNBREAKABLE -> CosmeticEditorMenu.addHideUnbreakable(menu, meta);
    }
  }

  /**
   * Returns to the CosmeticEditor menu.
   */
  private void returnToCosmeticEditor() {
    user.openInventory(new CosmeticEditorMenu(user).openMenu());
    PluginData.pluginSystem.getPlayerMetadata().get(userUUID).put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_COSMETIC.getMeta());
  }

  /**
   * Sets the user's interacting equipment slot.
   *
   * @param action type of interaction
   */
  private void setMode(AttributeEditorAction action) {
    Map<PlayerMeta, String> playerMeta = PluginData.pluginSystem.getPlayerMetadata().get(userUUID);
    String equipmentSlot = action.name().toLowerCase();
    playerMeta.put(PlayerMeta.SLOT, equipmentSlot);
    user.openInventory(new AttributeEditorMenu(user, action).openMenu());
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_ATTRIBUTE.getMeta());
  }

  /**
   * Determines the attribute to be set and prompts the user for an input.
   */
  private void readAttribute() {
    String attribute = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
    String attributeType;
    if (PluginConstant.minecraftAttributes.contains(attribute)) {
      String slot = PluginData.pluginSystem.getPlayerMetadata().get(userUUID).get(PlayerMeta.SLOT);
      try {
        EquipmentSlot.valueOf(slot.toUpperCase());
        attributeType = "GENERIC_" + TextFormatter.formatEnum(attribute);
      } catch (IllegalArgumentException e) {
        user.sendMessage(ChatColor.RED + "Minecraft attribute does not exist for that slot.");
        user.openInventory(new AttributeEditorMenu(user, AttributeEditorAction.valueOf(slot.toUpperCase())).openMenu());
        PluginData.pluginSystem.getPlayerMetadata().get(userUUID).put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_ATTRIBUTE.getMeta());
        return;
      }
    } else {
      attributeType = "aethel.attribute." + TextFormatter.formatId(attribute);
    }
    user.sendMessage(PluginMessage.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + attribute + ChatColor.WHITE + " value.");
    user.sendMessage(getAttributeValueContext(attribute));
    PluginData.pluginSystem.getPlayerMetadata().get(userUUID).put(PlayerMeta.TYPE, attributeType);
    awaitMessageResponse("attribute");
  }

  /**
   * Determines the enchantment to be set and prompts the user for an input.
   */
  private void readEnchantment() {
    String enchantment = ChatColor.stripColor(TextFormatter.formatId(e.getCurrentItem().getItemMeta().getDisplayName()));
    user.sendMessage(PluginMessage.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + TextFormatter.capitalizePhrase(enchantment) + ChatColor.WHITE + " value.");
    PluginData.pluginSystem.getPlayerMetadata().get(userUUID).put(PlayerMeta.TYPE, enchantment);
    awaitMessageResponse("enchantment");
  }

  /**
   * Determines the Aethel tag to be set and prompts the user for an input.
   */
  private void readTag() {
    String tag = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
    user.sendMessage(PluginMessage.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + tag + ChatColor.WHITE + " value.");
    PluginData.pluginSystem.getPlayerMetadata().get(userUUID).put(PlayerMeta.TYPE, tag);
    awaitMessageResponse("tag");
  }

  /**
   * Uses the user's next message as the field's input.
   *
   * @param metadata metadata field
   */
  private void awaitMessageResponse(String metadata) {
    user.closeInventory();
    PluginData.pluginSystem.getPlayerMetadata().get(userUUID).put(PlayerMeta.MESSAGE, "itemeditor." + metadata);
  }

  /**
   * Sends a contextual base value for the attribute being edited.
   *
   * @param attribute attribute name
   * @return base attribute value
   */
  private String getAttributeValueContext(String attribute) {
    String attributeContext = PluginMessage.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Base: ";
    switch (attribute) {
      case "Max HP", "Max Health" -> {
        return attributeContext + "20.0";
      }
      case "Attack Damage" -> {
        return attributeContext + "1.0";
      }
      case "Attack Speed" -> {
        return attributeContext + "4.0";
      }
      case "Critical Chance", "Counter Chance", "Dodge Chance" -> {
        return attributeContext + "0.0%";
      }
      case "Critical Damage" -> {
        return attributeContext + "1.25x [Input / 100]";
      }
      case "Armor" -> {
        return attributeContext + "0.0 [Max: 30.0]";
      }
      case "Armor Toughness" -> {
        return attributeContext + "0.0 [Max: 20.0]";
      }
      case "Knockback Resistance" -> {
        return attributeContext + "0.0 [Max: 1.0]";
      }
      case "Movement Speed" -> {
        return attributeContext + "2.0 [Input * 20]";
      }
      case "Luck", "Toughness" -> {
        return attributeContext + "0.0";
      }
      case "Item Damage" -> {
        return attributeContext + "1.0x [Input / 100]";
      }
      case "Item Cooldown" -> {
        return attributeContext + "-0.0%";
      }
      default -> {
        return null;
      }
    }
  }

  /**
   * Adds the recipe ID to the item's lore.
   *
   * @param dataContainer item's persistent tags
   * @param recipeId      recipe id
   */
  private void displayRecipeId(PersistentDataContainer dataContainer, NamespacedKey recipeId) {
    List<String> lore;
    if (meta.hasLore()) {
      lore = meta.getLore();
    } else {
      lore = new ArrayList<>();
    }
    lore.add(ChatColor.GRAY + "Recipe ID: " + ChatColor.DARK_GRAY + dataContainer.get(recipeId, PersistentDataType.STRING));
    meta.setLore(lore);
    item.setItemMeta(meta);
  }
}
