package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.systems.plugin.PluginConstant;
import me.dannynguyen.aethel.systems.plugin.PluginData;
import me.dannynguyen.aethel.systems.plugin.PluginEnum;
import me.dannynguyen.aethel.systems.plugin.MenuMeta;
import me.dannynguyen.aethel.systems.plugin.PlayerMeta;
import me.dannynguyen.aethel.utility.TextFormatter;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Inventory click event listener for ItemEditor menus.
 *
 * @author Danny Nguyen
 * @version 1.11.7
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
    this.item = PluginData.editedItemCache.getEditedItemMap().get(user);
    this.meta = item.getItemMeta();
    this.slotClicked = e.getSlot();
  }

  /**
   * Sets an item's cosmetic metadata or opens a gameplay metadata editor menu.
   */
  public void interpretCosmeticEditorClick() {
    switch (slotClicked) {
      case 11 -> setDisplayName();
      case 12 -> setCustomModelData();
      case 14 -> openAttributeEditor();
      case 15 -> openEnchantmentEditor();
      case 16 -> openTagEditor();
      case 28 -> { // Lore Context
      }
      case 29 -> setLore();
      case 30 -> clearLore();
      case 37 -> addLore();
      case 38 -> editLore();
      case 39 -> removeLore();
      case 47 -> generateLore();
      case 32 -> toggleHideArmorTrim();
      case 33 -> toggleHideAttributes();
      case 34 -> toggleHideDestroys();
      case 41 -> toggleHideDye();
      case 42 -> toggleHideEnchants();
      case 43 -> toggleHidePlacedOn();
      case 50 -> toggleHidePotionEffects();
      case 51 -> toggleHideUnbreakable();
      case 52 -> toggleUnbreakable();
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
    user.sendMessage(PluginEnum.Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input display name.");
    awaitMessageResponse("display_name");
  }

  /**
   * Sets an item's custom model data.
   */
  private void setCustomModelData() {
    user.sendMessage(PluginEnum.Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input custom model data value.");
    awaitMessageResponse("custom_model_data");
  }

  /**
   * Opens an AttributeEditor menu.
   */
  private void openAttributeEditor() {
    Map<PlayerMeta, String> playerMeta = PluginData.pluginSystem.getPlayerMetadata().get(user);
    if (playerMeta.containsKey(PlayerMeta.MESSAGE)) {
      playerMeta.remove(PlayerMeta.MESSAGE);
    }
    playerMeta.put(PlayerMeta.SLOT, "head");
    user.openInventory(new AttributeEditorMenu(user, AttributeEditorAction.HEAD).openMenu());
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_ATTRIBUTE.getMeta());
  }

  /**
   * Opens an EnchantmentEditor menu.
   */
  private void openEnchantmentEditor() {
    Map<PlayerMeta, String> playerMeta = PluginData.pluginSystem.getPlayerMetadata().get(user);
    if (playerMeta.containsKey(PlayerMeta.MESSAGE)) {
      playerMeta.remove(PlayerMeta.MESSAGE);
    }
    user.openInventory(new EnchantmentEditorMenu(user).openMenu());
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_ENCHANTMENT.getMeta());
  }

  /**
   * Opens a TagEditor menu.
   */
  private void openTagEditor() {
    Map<PlayerMeta, String> playerMeta = PluginData.pluginSystem.getPlayerMetadata().get(user);
    if (playerMeta.containsKey(PlayerMeta.MESSAGE)) {
      playerMeta.remove(PlayerMeta.MESSAGE);
    }
    user.openInventory(new TagEditorMenu(user).openMenu());
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_TAG.getMeta());
  }

  /**
   * Sets an item's lore.
   */
  private void setLore() {
    user.sendMessage(PluginEnum.Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input lore to set.");
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
    user.sendMessage(PluginEnum.Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input lore to add.");
    awaitMessageResponse("lore-add");
  }

  /**
   * Edits a line of text from an item's lore.
   */
  private void editLore() {
    if (meta.hasLore()) {
      user.sendMessage(PluginEnum.Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input line number and lore to edit.");
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
      user.sendMessage(PluginEnum.Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input line number to remove.");
      awaitMessageResponse("lore-remove");
    } else {
      user.sendMessage(ChatColor.RED + "Item has no lore.");
    }
  }

  /**
   * Generates an item's lore based on its plugin-related data.
   */
  private void generateLore() {
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
    NamespacedKey listKey = PluginEnum.Key.ATTRIBUTE_LIST.getNamespacedKey();
    boolean hasAttributes = dataContainer.has(listKey, PersistentDataType.STRING);

    if (hasAttributes) {
      List<String> attributes = new ArrayList<>(List.of(dataContainer.get(listKey, PersistentDataType.STRING).split(" ")));
      List<String> lore;
      if (meta.hasLore()) {
        lore = meta.getLore();
      } else {
        lore = new ArrayList<>();
      }

      for (String attribute : attributes) {
        String attributeType = attribute.substring(0, attribute.indexOf("."));
        String attributeSlot = attribute.substring(attribute.indexOf(".") + 1);
        NamespacedKey attributeKey = new NamespacedKey(Plugin.getInstance(), "aethel.attribute." + attribute);

        switch (attributeSlot) {
          case "head", "chest", "legs", "feet", "necklace", "ring" -> attributeSlot = ChatColor.GRAY + "When on " + TextFormatter.capitalizeWord(attributeSlot) + ": ";
          case "hand" -> attributeSlot = ChatColor.GRAY + "When in Hand: ";
          case "off_hand" -> attributeSlot = ChatColor.GRAY + "When in Off Hand: ";
        }

        lore.add(ChatColor.GRAY + attributeSlot + ChatColor.DARK_GREEN + dataContainer.get(attributeKey, PersistentDataType.DOUBLE) + " " + TextFormatter.capitalizePhrase(attributeType));
      }
      meta.setLore(lore);
      item.setItemMeta(meta);
      user.sendMessage(ChatColor.GREEN + "[Generated Lore]");
    } else {
      user.sendMessage(ChatColor.RED + "Not modified by plugin.");
    }
  }

  /**
   * Toggles an item's hide armor trim flag.
   */
  private void toggleHideArmorTrim() {
    if (!meta.hasItemFlag(ItemFlag.HIDE_ARMOR_TRIM)) {
      meta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
      user.sendMessage(ChatColor.GREEN + "[Hide Armor Trim]");
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
      user.sendMessage(ChatColor.RED + "[Hide Armor Trim]");
    }
    item.setItemMeta(meta);
    CosmeticEditorMenu.addHideArmorTrim(menu, meta);
  }

  /**
   * Toggles an item's hide attributes flag.
   */
  private void toggleHideAttributes() {
    if (!meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)) {
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      user.sendMessage(ChatColor.GREEN + "[Hide Attributes]");
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      user.sendMessage(ChatColor.RED + "[Hide Attributes]");
    }
    item.setItemMeta(meta);
    CosmeticEditorMenu.addHideAttributes(menu, meta);
  }

  /**
   * Toggles an item's hide destroys flag.
   */
  private void toggleHideDestroys() {
    if (!meta.hasItemFlag(ItemFlag.HIDE_DESTROYS)) {
      meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
      user.sendMessage(ChatColor.GREEN + "[Hide Destroys]");
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_DESTROYS);
      user.sendMessage(ChatColor.RED + "[Hide Destroys]");
    }
    item.setItemMeta(meta);
    CosmeticEditorMenu.addHideDestroys(menu, meta);
  }

  /**
   * Toggles an item's hide dye flag.
   */
  private void toggleHideDye() {
    if (!meta.hasItemFlag(ItemFlag.HIDE_DYE)) {
      meta.addItemFlags(ItemFlag.HIDE_DYE);
      user.sendMessage(ChatColor.GREEN + "[Hide Dye]");
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_DYE);
      user.sendMessage(ChatColor.RED + "[Hide Dye]");
    }
    item.setItemMeta(meta);
    CosmeticEditorMenu.addHideDye(menu, meta);
  }

  /**
   * Toggles an item's hide enchants flag.
   */
  private void toggleHideEnchants() {
    if (!meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
      meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
      user.sendMessage(ChatColor.GREEN + "[Hide Enchants]");
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
      user.sendMessage(ChatColor.RED + "[Hide Enchants]");
    }
    item.setItemMeta(meta);
    CosmeticEditorMenu.addHideEnchants(menu, meta);
  }

  /**
   * Toggles an item's hide placed on flag.
   */
  private void toggleHidePlacedOn() {
    if (!meta.hasItemFlag(ItemFlag.HIDE_PLACED_ON)) {
      meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
      user.sendMessage(ChatColor.GREEN + "[Hide Placed On]");
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_PLACED_ON);
      user.sendMessage(ChatColor.RED + "[Hide Placed On]");
    }
    item.setItemMeta(meta);
    CosmeticEditorMenu.addHidePlacedOn(menu, meta);
  }

  /**
   * Toggles an item's hide potion effects flag.
   */
  private void toggleHidePotionEffects() {
    if (!meta.hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS)) {
      meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
      user.sendMessage(ChatColor.GREEN + "[Hide Potion Effects]");
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
      user.sendMessage(ChatColor.RED + "[Hide Potion Effects]");
    }
    item.setItemMeta(meta);
    CosmeticEditorMenu.addHidePotionEffects(menu, meta);
  }

  /**
   * Toggles an item's hide unbreakable flag.
   */
  private void toggleHideUnbreakable() {
    if (!meta.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE)) {
      meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
      user.sendMessage(ChatColor.GREEN + "[Hide Unbreakable]");
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_UNBREAKABLE);
      user.sendMessage(ChatColor.RED + "[Hide Unbreakable]");
    }
    item.setItemMeta(meta);
    CosmeticEditorMenu.addHideUnbreakable(menu, meta);
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
   * Returns to the CosmeticEditor menu.
   */
  private void returnToCosmeticEditor() {
    user.openInventory(new CosmeticEditorMenu(user).openMenu());
    PluginData.pluginSystem.getPlayerMetadata().get(user).put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_COSMETIC.getMeta());
  }

  /**
   * Sets the user's interacting equipment slot.
   *
   * @param action type of interaction
   */
  private void setMode(AttributeEditorAction action) {
    Map<PlayerMeta, String> playerMeta = PluginData.pluginSystem.getPlayerMetadata().get(user);
    String equipmentSlot = AttributeEditorAction.asString(action);
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
      attributeType = "GENERIC_" + TextFormatter.formatEnum(attribute);
    } else {
      attributeType = "aethel.attribute." + TextFormatter.formatId(attribute);
    }
    user.sendMessage(PluginEnum.Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + attribute + ChatColor.WHITE + " value.");
    user.sendMessage(getAttributeValueContext(attribute));
    PluginData.pluginSystem.getPlayerMetadata().get(user).put(PlayerMeta.TYPE, attributeType);
    awaitMessageResponse("attribute");
  }

  /**
   * Determines the enchantment to be set and prompts the user for an input.
   */
  private void readEnchantment() {
    String enchantment = ChatColor.stripColor(TextFormatter.formatId(e.getCurrentItem().getItemMeta().getDisplayName()));
    user.sendMessage(PluginEnum.Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + TextFormatter.capitalizePhrase(enchantment) + ChatColor.WHITE + " value.");
    PluginData.pluginSystem.getPlayerMetadata().get(user).put(PlayerMeta.TYPE, enchantment);
    awaitMessageResponse("enchantment");
  }

  /**
   * Determines the Aethel tag to be set and prompts the user for an input.
   */
  private void readTag() {
    String tag = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
    user.sendMessage(PluginEnum.Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input " + ChatColor.AQUA + tag + ChatColor.WHITE + " value.");
    PluginData.pluginSystem.getPlayerMetadata().get(user).put(PlayerMeta.TYPE, tag);
    awaitMessageResponse("tag");
  }

  /**
   * Uses the user's next message as the field's input.
   *
   * @param metadata metadata field
   */
  private void awaitMessageResponse(String metadata) {
    user.closeInventory();
    PluginData.pluginSystem.getPlayerMetadata().get(user).put(PlayerMeta.MESSAGE, "itemeditor." + metadata);
  }

  /**
   * Sends a contextual base value for the attribute being edited.
   *
   * @param attribute attribute name
   * @return base attribute value
   */
  private String getAttributeValueContext(String attribute) {
    String attributeContext = PluginEnum.Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Base: ";
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
      case "Critical Chance", "Parry Chance", "Deflect", "Dodge Chance", "Status Chance" -> {
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
      case "Block", "Luck" -> {
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
}
