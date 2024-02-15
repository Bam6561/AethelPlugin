package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginConstant;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.enums.PluginNamespacedKey;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.listeners.InventoryMenuListener;
import me.dannynguyen.aethel.utility.TextFormatter;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Inventory click event listener for ItemEditor menus.
 *
 * @author Danny Nguyen
 * @version 1.9.19
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
      case 3 -> setMode(AttributeEditorAction.HEAD);
      case 4 -> setMode(AttributeEditorAction.CHEST);
      case 5 -> setMode(AttributeEditorAction.LEGS);
      case 6 -> setMode(AttributeEditorAction.FEET);
      case 7 -> setMode(AttributeEditorAction.HAND);
      case 8 -> setMode(AttributeEditorAction.OFF_HAND);
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
      default -> readEnchant();
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
    user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message + ChatColor.WHITE + "Input display name.");
    awaitMessageResponse("display_name");
  }

  /**
   * Sets an item's custom model data.
   */
  private void setCustomModelData() {
    user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message + ChatColor.WHITE + "Input custom model data value.");
    awaitMessageResponse("custom_model_data");
  }

  /**
   * Opens an AttributeEditor menu.
   */
  private void openAttributeEditor() {
    if (user.hasMetadata(PluginPlayerMeta.MESSAGE.getMeta())) {
      user.removeMetadata(PluginPlayerMeta.MESSAGE.getMeta(), Plugin.getInstance());
    }
    user.setMetadata(PluginPlayerMeta.SLOT.getMeta(), new FixedMetadataValue(Plugin.getInstance(), "head"));
    user.openInventory(new AttributeEditorMenu(user, AttributeEditorAction.HEAD).openMenu());
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.ITEMEDITOR_ATTRIBUTES.menu));
  }

  /**
   * Opens an EnchantmentEditor menu.
   */
  private void openEnchantmentEditor() {
    if (user.hasMetadata(PluginPlayerMeta.MESSAGE.getMeta())) {
      user.removeMetadata(PluginPlayerMeta.MESSAGE.getMeta(), Plugin.getInstance());
    }
    user.openInventory(new EnchantmentEditorMenu(user).openMenu());
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.ITEMEDITOR_ENCHANTS.menu));
  }

  /**
   * Opens a TagEditor menu.
   */
  private void openTagEditor() {
    if (user.hasMetadata(PluginPlayerMeta.MESSAGE.getMeta())) {
      user.removeMetadata(PluginPlayerMeta.MESSAGE.getMeta(), Plugin.getInstance());
    }
    user.openInventory(new TagEditorMenu(user).openMenu());
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.ITEMEDITOR_TAGS.menu));
  }

  /**
   * Sets an item's lore.
   */
  private void setLore() {
    user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message + ChatColor.WHITE + "Input lore to set.");
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
    user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message + ChatColor.WHITE + "Input lore to add.");
    awaitMessageResponse("lore-add");
  }

  /**
   * Edits a line of text from an item's lore.
   */
  private void editLore() {
    if (meta.hasLore()) {
      user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message + ChatColor.WHITE + "Input line number to remove.");
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
      user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message + ChatColor.WHITE + "Input line number and lore to edit.");
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
    NamespacedKey listKey = PluginNamespacedKey.AETHEL_ATTRIBUTE_LIST.getNamespacedKey();
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
      user.sendMessage(ChatColor.RED + "No plugin modifications to item.");
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
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.ITEMEDITOR_COSMETICS.menu));
  }

  /**
   * Sets the user's interacting equipment slot.
   */
  private void setMode(AttributeEditorAction action) {
    String equipmentSlot = AttributeEditorAction.asString(action);
    user.setMetadata(PluginPlayerMeta.SLOT.getMeta(), new FixedMetadataValue(Plugin.getInstance(), equipmentSlot));
    user.openInventory(new AttributeEditorMenu(user, AttributeEditorAction.asEnum(equipmentSlot)).openMenu());
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.ITEMEDITOR_ATTRIBUTES.menu));
  }

  /**
   * Determines the attribute to be set and prompts the user for an input.
   */
  private void readAttribute() {
    String attributeName = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
    String attribute;
    if (PluginConstant.minecraftAttributes.contains(attributeName)) {
      attribute = "GENERIC_" + TextFormatter.formatEnum(attributeName);
    } else {
      attribute = "aethel.attribute." + TextFormatter.formatId(attributeName);
    }
    user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message + ChatColor.WHITE + "Input " + ChatColor.AQUA + attributeName + ChatColor.WHITE + " value.");
    user.sendMessage(getAttributeValueContext(attributeName));
    user.setMetadata(PluginPlayerMeta.TYPE.getMeta(), new FixedMetadataValue(Plugin.getInstance(), attribute));
    awaitMessageResponse("attributes");
  }

  /**
   * Determines the enchantment to be set and prompts the user for an input.
   */
  private void readEnchant() {
    String enchant = ChatColor.stripColor(TextFormatter.formatId(e.getCurrentItem().getItemMeta().getDisplayName()));
    user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message + ChatColor.WHITE + "Input " + ChatColor.AQUA + TextFormatter.capitalizePhrase(enchant) + ChatColor.WHITE + " value.");
    user.setMetadata(PluginPlayerMeta.TYPE.getMeta(), new FixedMetadataValue(Plugin.getInstance(), enchant));
    awaitMessageResponse("enchantments");
  }

  /**
   * Determines the Aethel tag to be set and prompts the user for an input.
   */
  private void readTag() {
    String tag = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
    user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message + ChatColor.WHITE + "Input " + ChatColor.AQUA + tag + ChatColor.WHITE + " value.");
    user.setMetadata(PluginPlayerMeta.TYPE.getMeta(), new FixedMetadataValue(Plugin.getInstance(), tag));
    awaitMessageResponse("tags");
  }

  /**
   * Uses the user's next message as the field's input.
   *
   * @param metadata metadata field
   */
  private void awaitMessageResponse(String metadata) {
    user.closeInventory();
    user.setMetadata(PluginPlayerMeta.MESSAGE.getMeta(), new FixedMetadataValue(Plugin.getInstance(), "itemeditor." + metadata));
  }

  /**
   * Sends a contextual base value for the attribute being edited.
   *
   * @param attribute attribute name
   * @return base attribute value
   */
  private String getAttributeValueContext(String attribute) {
    String attributeContext = PluginMessage.Success.NOTIFICATION_INPUT.message + ChatColor.WHITE + "Base: ";
    switch (attribute) {
      case "Attack Damage" -> {
        return attributeContext + "1.0";
      }
      case "Attack Speed" -> {
        return attributeContext + "4.0";
      }
      case "Critical Chance", "Parry Chance", "Parry Deflect", "Dodge Chance", "Apply Status" -> {
        return attributeContext + "0.0%";
      }
      case "Critical Damage" -> {
        return attributeContext + "1.25x [Input / 100]";
      }
      case "Max Health" -> {
        return attributeContext + "20.0";
      }
      case "Armor" -> {
        return attributeContext + "0.0 [Max: 30.0]";
      }
      case "Armor Toughness" -> {
        return attributeContext + "0.0 [Max: 20.0]";
      }
      case "Movement Speed" -> {
        return attributeContext + "2.0 [Input * 20]";
      }
      case "Block", "Luck" -> {
        return attributeContext + "0.0";
      }
      case "Ability Damage" -> {
        return attributeContext + "1.0x [Input / 100]";
      }
      case "Ability Cooldown" -> {
        return attributeContext + "-0.0%";
      }
      case "Knockback Resistance" -> {
        return attributeContext + "0.0 [Max: 1.0]";
      }
      default -> {
        return null;
      }
    }
  }
}
