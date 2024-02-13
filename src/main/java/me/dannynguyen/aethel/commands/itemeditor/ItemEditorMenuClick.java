package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginConstant;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.enums.PluginNamespacedKey;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.listeners.InventoryMenuListener;
import me.dannynguyen.aethel.utility.ItemCreator;
import me.dannynguyen.aethel.utility.TextFormatter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
 * @version 1.9.17
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
    this.slotClicked = e.getSlot();
  }

  /**
   * Edits an item's cosmetic metadata or opens a gameplay metadata editor menu.
   */
  public void interpretMainMenuClick() {
    switch (slotClicked) {
      case 11 -> {
        user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message + ChatColor.WHITE + "Input display name.");
        awaitMessageResponse("display_name");
      }
      case 12 -> {
        user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message + ChatColor.WHITE + "Input custom model data value.");
        awaitMessageResponse("custom_model_data");
      }
      case 14 -> openAttributesMenu();
      case 15 -> openEnchantsMenu();
      case 16 -> openTagsMenu();
      case 28 -> { // Lore Context
      }
      case 29 -> {
        user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message + ChatColor.WHITE + "Input lore to set.");
        awaitMessageResponse("lore-set");
      }
      case 30 -> readItemLore("lore-clear");
      case 37 -> {
        user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message + ChatColor.WHITE + "Input lore to add.");
        awaitMessageResponse("lore-add");
      }
      case 38 -> readItemLore("lore-edit");
      case 39 -> readItemLore("lore-remove");
      case 47 -> readItemLore("lore-generate");
      case 32 -> toggleHideArmorTrim();
      case 33 -> toggleHideAttributes();
      case 34 -> toggleHideDestroys();
      case 41 -> toggleHideDye();
      case 42 -> toggleHideEnchants();
      case 43 -> toggleHidePlacedOn();
      case 50 -> toggleHidePotionEffects();
      case 51 -> toggleHideUnbreakable();
      case 52 -> toggleUnbreakable(e.getClickedInventory(), user);
    }
  }

  /**
   * Either changes the equipment slot mode or sets an item's attribute.
   */
  public void interpretAttributesMenuClick() {
    switch (e.getSlot()) {
      case 0, 1 -> { // Context, Item
      }
      case 2 -> returnToMainMenu();
      case 3 -> setMode("head");
      case 4 -> setMode("chest");
      case 5 -> setMode("legs");
      case 6 -> setMode("feet");
      case 7 -> setMode("hand");
      case 8 -> setMode("off_hand");
      case 16 -> setMode("necklace");
      case 17 -> setMode("ring");
      default -> readAttribute(e, user);
    }
  }

  /**
   * Sets an item's enchant.
   */
  public void interpretEnchantsMenuClick() {
    switch (e.getSlot()) {
      case 2, 4 -> { // Context, Item
      }
      case 6 -> returnToMainMenu();
      default -> readEnchant(e, user);
    }
  }

  /**
   * Edits an item's Aethel tag.
   */
  public void interpretTagsMenuClick() {
    switch (e.getSlot()) {
      case 2, 4 -> { // Context, Item
      }
      case 6 -> returnToMainMenu();
      default -> readTag(e, user);
    }
  }

  /**
   * Toggles an item's ability to be broken.
   *
   * @param inv  interacting inventory
   * @param user user
   */
  private void toggleUnbreakable(Inventory inv, Player user) {
    ItemStack item = PluginData.editedItemCache.getEditedItemMap().get(user);
    ItemMeta meta = item.getItemMeta();

    if (!meta.isUnbreakable()) {
      meta.setUnbreakable(true);
      user.sendMessage(ChatColor.GREEN + "[Set Unbreakable]");
    } else {
      meta.setUnbreakable(false);
      user.sendMessage(ChatColor.RED + "[Set Unbreakable]");
    }
    item.setItemMeta(meta);

    addUnbreakableMeta();
  }

  /**
   * Checks if the item has lore before making changes.
   *
   * @param action interaction type
   */
  private void readItemLore(String action) {
    ItemMeta meta = PluginData.editedItemCache.getEditedItemMap().get(user).getItemMeta();
    if (action.equals("lore-generate")) {
      ItemStack item = PluginData.editedItemCache.getEditedItemMap().get(user);
      generateLore(item, meta);
      user.sendMessage(ChatColor.GREEN + "[Generated Lore]");
    } else if (meta.hasLore()) {
      switch (action) {
        case "lore-clear" -> {
          ItemStack item = PluginData.editedItemCache.getEditedItemMap().get(user);
          ItemEditorMessageListener.clearLore(user, item, item.getItemMeta());
          user.sendMessage(ChatColor.GREEN + "[Cleared Lore]");
        }
        case "lore-edit" -> {
          user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message + ChatColor.WHITE + "Input line number and lore to edit.");
          awaitMessageResponse(action);
        }
        case "lore-remove" -> {
          user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message + ChatColor.WHITE + "Input line number to remove.");
          awaitMessageResponse(action);
        }
      }
    } else {
      user.sendMessage(ChatColor.RED + "Item has no lore.");
    }
  }

  /**
   * Sets the user's interacting equipment slot.
   *
   * @param equipmentSlot interacting equipment slot.
   */
  private void setMode(String equipmentSlot) {
    user.setMetadata(PluginPlayerMeta.SLOT.getMeta(), new FixedMetadataValue(Plugin.getInstance(), equipmentSlot));
    user.openInventory(ItemEditorAttributes.openAttributesMenu(user, equipmentSlot));
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.ITEMEDITOR_ATTRIBUTES.menu));
  }

  /**
   * Determines the attribute to be set and prompts the user for an input.
   *
   * @param e    inventory click event
   * @param user user
   */
  private void readAttribute(InventoryClickEvent e, Player user) {
    String attributeName = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
    String attribute;
    if (PluginConstant.minecraftAttributes.contains(attributeName)) {
      attribute = "GENERIC_" + TextFormatter.formatEnum(attributeName);
    } else {
      attribute = "aethel.attribute." + TextFormatter.formatId(attributeName);
    }

    user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message +
        ChatColor.WHITE + "Input " + ChatColor.AQUA + attributeName + ChatColor.WHITE + " value.");
    user.sendMessage(getAttributeValueContext(attributeName));

    user.setMetadata(PluginPlayerMeta.TYPE.getMeta(),
        new FixedMetadataValue(Plugin.getInstance(), attribute));
    awaitMessageResponse("attributes");
  }

  /**
   * Determines the enchantment to be set and prompts the user for an input.
   *
   * @param e    inventory click event
   * @param user user
   */
  private void readEnchant(InventoryClickEvent e, Player user) {
    String enchant = ChatColor.stripColor(TextFormatter.formatId(e.getCurrentItem().getItemMeta().getDisplayName()));

    user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message
        + ChatColor.WHITE + "Input " + ChatColor.AQUA
        + TextFormatter.capitalizePhrase(enchant) + ChatColor.WHITE + " value.");

    user.setMetadata(PluginPlayerMeta.TYPE.getMeta(), new FixedMetadataValue(Plugin.getInstance(), enchant));
    awaitMessageResponse("enchants");
  }

  /**
   * Determines the Aethel tag to be set and prompts the user for an input.
   *
   * @param e    inventory click event
   * @param user user
   */
  private void readTag(InventoryClickEvent e, Player user) {
    String aethelTag = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());

    user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message +
        ChatColor.WHITE + "Input " + ChatColor.AQUA + aethelTag + ChatColor.WHITE + " value.");

    user.setMetadata(PluginPlayerMeta.TYPE.getMeta(), new FixedMetadataValue(Plugin.getInstance(), aethelTag));
    awaitMessageResponse("tags");
  }

  /**
   * Sends a value context for the attribute being edited.
   *
   * @param attributeName attribute name
   * @return attribute value context
   */
  private String getAttributeValueContext(String attributeName) {
    String attributeContext = PluginMessage.Success.NOTIFICATION_INPUT.message + ChatColor.WHITE + "Base: ";
    switch (attributeName) {
      case "Attack Damage" -> attributeContext += "1.0";
      case "Attack Speed" -> attributeContext += "4.0";
      case "Critical Chance", "Parry Chance", "Parry Deflect",
          "Dodge Chance", "Apply Status" -> attributeContext += "0.0%";
      case "Critical Damage" -> attributeContext += "1.25x [Input / 100]";
      case "Max Health" -> attributeContext += "20.0";
      case "Armor" -> attributeContext += "0.0 [Max: 30.0]";
      case "Armor Toughness" -> attributeContext += "0.0 [Max: 20.0]";
      case "Movement Speed" -> attributeContext += "2.0 [Input * 20]";
      case "Block", "Luck" -> attributeContext += "0.0";
      case "Ability Damage" -> attributeContext += "1.0x [Input / 100]";
      case "Ability Cooldown" -> attributeContext += "-0.0%";
      case "Knockback Resistance" -> attributeContext += "0.0 [Max: 1.0]";
    }
    return attributeContext;
  }

  /**
   * Generates an item's lore based on its plugin-related data.
   *
   * @param item interacting item
   * @param meta item meta
   */
  private void generateLore(ItemStack item, ItemMeta meta) {
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
    NamespacedKey listKey = PluginNamespacedKey.AETHEL_ATTRIBUTE_LIST.getNamespacedKey();
    boolean hasAttributes = dataContainer.has(listKey, PersistentDataType.STRING);

    if (hasAttributes) {
      List<String> attributes = new ArrayList<>(List.of(
          dataContainer.get(listKey, PersistentDataType.STRING).split(" ")));

      List<String> lore;
      if (meta.hasLore()) {
        lore = meta.getLore();
      } else {
        lore = new ArrayList<>();
      }

      for (String attribute : attributes) {
        String attributeType = attribute.substring(0, attribute.indexOf("."));
        String attributeSlot = attribute.substring(attribute.indexOf(".") + 1);
        NamespacedKey attributeKey =
            new NamespacedKey(Plugin.getInstance(), "aethel.attribute." + attribute);

        switch (attributeSlot) {
          case "head", "chest", "legs", "feet",
              "necklace", "ring" -> attributeSlot = ChatColor.GRAY
              + "When on " + TextFormatter.capitalizeWord(attributeSlot) + ": ";
          case "hand" -> attributeSlot = ChatColor.GRAY + "When in Hand: ";
          case "off_hand" -> attributeSlot = ChatColor.GRAY + "When in Off Hand: ";
        }

        lore.add(ChatColor.GRAY + attributeSlot + ChatColor.DARK_GREEN +
            dataContainer.get(attributeKey, PersistentDataType.DOUBLE)
            + " " + TextFormatter.capitalizePhrase(attributeType));
      }
      meta.setLore(lore);
      item.setItemMeta(meta);
    }
  }

  /**
   * Toggles an item's hide armor trim flag.
   */
  public void toggleHideArmorTrim() {
    ItemStack item = PluginData.editedItemCache.getEditedItemMap().get(user);
    ItemMeta meta = item.getItemMeta();
    if (!meta.hasItemFlag(ItemFlag.HIDE_ARMOR_TRIM)) {
      meta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
      user.sendMessage(ChatColor.GREEN + "[Hide Armor Trim]");
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
      user.sendMessage(ChatColor.RED + "[Hide Armor Trim]");
    }
    item.setItemMeta(meta);

    addHideArmorTrimMeta();
  }

  /**
   * Toggles an item's hide attributes flag.
   */
  public void toggleHideAttributes() {
    ItemStack item = PluginData.editedItemCache.getEditedItemMap().get(user);
    ItemMeta meta = item.getItemMeta();
    if (!meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)) {
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      user.sendMessage(ChatColor.GREEN + "[Hide Attributes]");
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      user.sendMessage(ChatColor.RED + "[Hide Attributes]");
    }
    item.setItemMeta(meta);

    addHideAttributesMeta();
  }

  /**
   * Toggles an item's hide destroys flag.
   */
  public void toggleHideDestroys() {
    ItemStack item = PluginData.editedItemCache.getEditedItemMap().get(user);
    ItemMeta meta = item.getItemMeta();
    if (!meta.hasItemFlag(ItemFlag.HIDE_DESTROYS)) {
      meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
      user.sendMessage(ChatColor.GREEN + "[Hide Destroys]");
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_DESTROYS);
      user.sendMessage(ChatColor.RED + "[Hide Destroys]");
    }
    item.setItemMeta(meta);

    addHideDestroysMeta();
  }

  /**
   * Toggles an item's hide dye flag.
   */
  public void toggleHideDye() {
    ItemStack item = PluginData.editedItemCache.getEditedItemMap().get(user);
    ItemMeta meta = item.getItemMeta();
    if (!meta.hasItemFlag(ItemFlag.HIDE_DYE)) {
      meta.addItemFlags(ItemFlag.HIDE_DYE);
      user.sendMessage(ChatColor.GREEN + "[Hide Dye]");
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_DYE);
      user.sendMessage(ChatColor.RED + "[Hide Dye]");
    }
    item.setItemMeta(meta);

    addHideDyeMeta();
  }

  /**
   * Toggles an item's hide enchants flag.
   */
  public void toggleHideEnchants() {
    ItemStack item = PluginData.editedItemCache.getEditedItemMap().get(user);
    ItemMeta meta = item.getItemMeta();
    if (!meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
      meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
      user.sendMessage(ChatColor.GREEN + "[Hide Enchants]");
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
      user.sendMessage(ChatColor.RED + "[Hide Enchants]");
    }
    item.setItemMeta(meta);

    addHideEnchantsMeta();
  }

  /**
   * Toggles an item's hide placed on flag.
   */
  public void toggleHidePlacedOn() {
    ItemStack item = PluginData.editedItemCache.getEditedItemMap().get(user);
    ItemMeta meta = item.getItemMeta();
    if (!meta.hasItemFlag(ItemFlag.HIDE_PLACED_ON)) {
      meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
      user.sendMessage(ChatColor.GREEN + "[Hide Placed On]");
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_PLACED_ON);
      user.sendMessage(ChatColor.RED + "[Hide Placed On]");
    }
    item.setItemMeta(meta);

    addHidePlacedOnMeta();
  }

  /**
   * Toggles an item's hide potion effects flag.
   */
  public void toggleHidePotionEffects() {
    ItemStack item = PluginData.editedItemCache.getEditedItemMap().get(user);
    ItemMeta meta = item.getItemMeta();
    if (!meta.hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS)) {
      meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
      user.sendMessage(ChatColor.GREEN + "[Hide Potion Effects]");
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
      user.sendMessage(ChatColor.RED + "[Hide Potion Effects]");
    }
    item.setItemMeta(meta);

    addHidePotionEffectsMeta();
  }

  /**
   * Toggles an item's hide unbreakable flag.
   */
  public void toggleHideUnbreakable() {
    ItemStack item = PluginData.editedItemCache.getEditedItemMap().get(user);
    ItemMeta meta = item.getItemMeta();
    if (!meta.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE)) {
      meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
      user.sendMessage(ChatColor.GREEN + "[Hide Unbreakable]");
    } else {
      meta.removeItemFlags(ItemFlag.HIDE_UNBREAKABLE);
      user.sendMessage(ChatColor.RED + "[Hide Unbreakable]");
    }
    item.setItemMeta(meta);

    addHideUnbreakableMeta();
  }

  /**
   * Adds the unbreakable toggle button.
   */
  public void addUnbreakableMeta() {
    ItemMeta meta = PluginData.editedItemCache.getEditedItemMap().get(user).getItemMeta();
    boolean disabled = !meta.isUnbreakable();
    String unbreakable = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    menu.setItem(52, ItemCreator.createItem(disabled ? Material.CLAY : Material.BEDROCK, ChatColor.AQUA + "Unbreakable", List.of(unbreakable)));
  }

  /**
   * Adds item flag toggle buttons.
   */
  public void addItemFlagsMeta() {
    addHideArmorTrimMeta();
    addHideAttributesMeta();
    addHideDestroysMeta();
    addHideDyeMeta();
    addHideEnchantsMeta();
    addHidePlacedOnMeta();
    addHidePotionEffectsMeta();
    addHideUnbreakableMeta();
  }

  /**
   * Adds hide armor trim toggle button.
   */
  public void addHideArmorTrimMeta() {
    ItemMeta meta = PluginData.editedItemCache.getEditedItemMap().get(user).getItemMeta();
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_ARMOR_TRIM);
    String armorTrim = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    menu.setItem(32, ItemCreator.createItem(disabled ? Material.RED_DYE : Material.GREEN_DYE, ChatColor.AQUA + "Hide Armor Trims", List.of(armorTrim)));
  }

  /**
   * Adds hide attributes toggle button.
   */
  public void addHideAttributesMeta() {
    ItemMeta meta = PluginData.editedItemCache.getEditedItemMap().get(user).getItemMeta();
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES);
    String attributes = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    menu.setItem(33, ItemCreator.createItem(disabled ? Material.RED_DYE : Material.GREEN_DYE, ChatColor.AQUA + "Hide Attributes", List.of(attributes)));
  }

  /**
   * Adds hide destroys toggle button.
   */
  public void addHideDestroysMeta() {
    ItemMeta meta = PluginData.editedItemCache.getEditedItemMap().get(user).getItemMeta();
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_DESTROYS);
    String destroys = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    menu.setItem(34, ItemCreator.createItem(disabled ? Material.RED_DYE : Material.GREEN_DYE, ChatColor.AQUA + "Hide Destroys", List.of(destroys)));
  }

  /**
   * Adds hide dye toggle button.
   */
  public void addHideDyeMeta() {
    ItemMeta meta = PluginData.editedItemCache.getEditedItemMap().get(user).getItemMeta();
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_DYE);
    String dye = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    menu.setItem(41, ItemCreator.createItem(disabled ? Material.RED_DYE : Material.GREEN_DYE, ChatColor.AQUA + "Hide Dyes", List.of(dye)));
  }

  /**
   * Adds hide enchants toggle button.
   */
  public void addHideEnchantsMeta() {
    ItemMeta meta = PluginData.editedItemCache.getEditedItemMap().get(user).getItemMeta();
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS);
    String enchants = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    menu.setItem(42, ItemCreator.createItem(disabled ? Material.RED_DYE : Material.GREEN_DYE, ChatColor.AQUA + "Hide Enchants", List.of(enchants)));
  }

  /**
   * Adds hide placed on toggle button.
   */
  public void addHidePlacedOnMeta() {
    ItemMeta meta = PluginData.editedItemCache.getEditedItemMap().get(user).getItemMeta();
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_PLACED_ON);
    String placedOn = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    menu.setItem(43, ItemCreator.createItem(disabled ? Material.RED_DYE : Material.GREEN_DYE, ChatColor.AQUA + "Hide Placed On", List.of(placedOn)));
  }

  /**
   * Adds hide potion effects toggle button.
   */
  public void addHidePotionEffectsMeta() {
    ItemMeta meta = PluginData.editedItemCache.getEditedItemMap().get(user).getItemMeta();
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS);
    String potionEffects = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    menu.setItem(50, ItemCreator.createItem(disabled ? Material.RED_DYE : Material.GREEN_DYE, ChatColor.AQUA + "Hide Potion Effects", List.of(potionEffects)));
  }

  /**
   * Adds hide unbreakable toggle button.
   */
  public void addHideUnbreakableMeta() {
    ItemMeta meta = PluginData.editedItemCache.getEditedItemMap().get(user).getItemMeta();
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE);
    String unbreakable = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    menu.setItem(51, ItemCreator.createItem(disabled ? Material.RED_DYE : Material.GREEN_DYE, ChatColor.AQUA + "Hide Unbreakable", List.of(unbreakable)));
  }

  /**
   * Opens a ItemEditorAttributes menu.
   */
  public void openAttributesMenu() {
    if (user.hasMetadata(PluginPlayerMeta.MESSAGE.getMeta())) {
      user.removeMetadata(PluginPlayerMeta.MESSAGE.getMeta(), Plugin.getInstance());
    }
    user.setMetadata(PluginPlayerMeta.SLOT.getMeta(), new FixedMetadataValue(Plugin.getInstance(), "head"));
    user.openInventory(ItemEditorAttributes.openAttributesMenu(user, "Head"));
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.ITEMEDITOR_ATTRIBUTES.menu));
  }

  /**
   * Opens a ItemEditorEnchants menu.
   */
  public void openEnchantsMenu() {
    if (user.hasMetadata(PluginPlayerMeta.MESSAGE.getMeta())) {
      user.removeMetadata(PluginPlayerMeta.MESSAGE.getMeta(), Plugin.getInstance());
    }
    user.openInventory(ItemEditorEnchants.openMenu(user));
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.ITEMEDITOR_ENCHANTS.menu));
  }

  /**
   * Opens a ItemEditorTags menu.
   */
  public void openTagsMenu() {
    if (user.hasMetadata(PluginPlayerMeta.MESSAGE.getMeta())) {
      user.removeMetadata(PluginPlayerMeta.MESSAGE.getMeta(), Plugin.getInstance());
    }
    user.openInventory(ItemEditorTags.openTagsMenu(user));
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.ITEMEDITOR_TAGS.menu));
  }

  /**
   * Opens an ItemEditor main menu.
   */
  public void returnToMainMenu() {
    user.openInventory(new ItemEditorCosmetic(user, PluginData.editedItemCache.getEditedItemMap().get(user)).openMenu());
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.ITEMEDITOR_COSMETICS.menu));
  }

  /**
   * Uses the user's next message as the field's input.
   *
   * @param metadata metadata field
   */
  public void awaitMessageResponse(String metadata) {
    user.closeInventory();
    user.setMetadata(PluginPlayerMeta.MESSAGE.getMeta(), new FixedMetadataValue(Plugin.getInstance(), "itemeditor." + metadata));
  }
}
