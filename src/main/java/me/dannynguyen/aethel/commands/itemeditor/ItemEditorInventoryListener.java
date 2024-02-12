package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginConstant;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.enums.PluginNamespacedKey;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.listeners.InventoryMenuListener;
import me.dannynguyen.aethel.utility.ItemReader;
import me.dannynguyen.aethel.utility.TextFormatter;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * ItemEditorInventory is an inventory listener for the ItemEditor inventories.
 *
 * @author Danny Nguyen
 * @version 1.9.15
 * @since 1.6.7
 */
public class ItemEditorInventoryListener {
  /**
   * Edits an item's cosmetic metadata fields or opens
   * a gameplay-related metadata field editing menu.
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void interpretMainMenuClick(InventoryClickEvent e, Player user) {
    if (ItemReader.isNotNullOrAir(e.getCurrentItem()) &&
        e.getClickedInventory().getType().equals(InventoryType.CHEST)) {
      switch (e.getSlot()) {
        case 11 -> {
          user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message +
              Success.INPUT_DISPLAY_NAME.message);
          ItemEditorAction.awaitMessageResponse(user, "display_name");
        }
        case 12 -> {
          user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message +
              Success.INPUT_CUSTOMMODELDATA.message);
          ItemEditorAction.awaitMessageResponse(user, "custom_model_data");
        }
        case 14 -> ItemEditorAction.openAttributesMenu(user);
        case 15 -> ItemEditorAction.openEnchantsMenu(user);
        case 16 -> ItemEditorAction.openTagsMenu(user);
        case 28, 29, 30, 37, 38, 39, 47 -> interpretLoreAction(e.getSlot(), user);
        case 32, 33, 34, 41, 42, 43, 50, 51 -> interpretItemFlagToggle(e.getSlot(), e.getClickedInventory(), user);
        case 52 -> toggleUnbreakable(e.getClickedInventory(), user);
      }
    }
    e.setCancelled(true);
  }

  /**
   * Either changes the equipment slot mode or sets an item's attribute.
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void interpretAttributesMenuClick(InventoryClickEvent e, Player user) {
    if (ItemReader.isNotNullOrAir(e.getCurrentItem()) &&
        e.getClickedInventory().getType().equals(InventoryType.CHEST)) {
      switch (e.getSlot()) {
        case 0, 1 -> { // Context, Item
        }
        case 2 -> ItemEditorAction.returnToMainMenu(user);
        case 3 -> setMode(user, "head");
        case 4 -> setMode(user, "chest");
        case 5 -> setMode(user, "legs");
        case 6 -> setMode(user, "feet");
        case 7 -> setMode(user, "hand");
        case 8 -> setMode(user, "off_hand");
        case 16 -> setMode(user, "necklace");
        case 17 -> setMode(user, "ring");
        default -> readAttribute(e, user);
      }
    }
    e.setCancelled(true);
  }

  /**
   * Sets an item's enchant.
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void interpretEnchantsMenuClick(InventoryClickEvent e, Player user) {
    if (ItemReader.isNotNullOrAir(e.getCurrentItem()) &&
        e.getClickedInventory().getType().equals(InventoryType.CHEST)) {
      switch (e.getSlot()) {
        case 2, 4 -> { // Context, Item
        }
        case 6 -> ItemEditorAction.returnToMainMenu(user);
        default -> readEnchant(e, user);
      }
    }
    e.setCancelled(true);
  }

  /**
   * Edits an item's Aethel tag.
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void interpretTagsMenuClick(InventoryClickEvent e, Player user) {
    if (ItemReader.isNotNullOrAir(e.getCurrentItem()) &&
        e.getClickedInventory().getType().equals(InventoryType.CHEST)) {
      switch (e.getSlot()) {
        case 2, 4 -> { // Context, Item
        }
        case 6 -> ItemEditorAction.returnToMainMenu(user);
        default -> readTag(e, user);
      }
    }
    e.setCancelled(true);
  }

  /**
   * Either sets, clears, add, edits, or removes lore.
   *
   * @param slotClicked slot clicked
   * @param user        user
   */
  private static void interpretLoreAction(int slotClicked, Player user) {
    switch (slotClicked) {
      case 28 -> { // Lore Context
      }
      case 29 -> {
        user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message +
            Success.INPUT_SET_LORE.message);
        ItemEditorAction.awaitMessageResponse(user, "lore-set");
      }
      case 30 -> readItemLore(user, "lore-clear");
      case 37 -> {
        user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message +
            Success.INPUT_ADD_LORE.message);
        ItemEditorAction.awaitMessageResponse(user, "lore-add");
      }
      case 38 -> readItemLore(user, "lore-edit");
      case 39 -> readItemLore(user, "lore-remove");
      case 47 -> readItemLore(user, "lore-generate");
    }
  }

  /**
   * Toggles item flags.
   *
   * @param slotClicked slot clicked
   * @param inv         interacting inventory
   * @param user        user
   */
  private static void interpretItemFlagToggle(int slotClicked, Inventory inv, Player user) {
    ItemStack item = PluginData.itemEditorData.getEditedItemMap().get(user);
    ItemMeta meta = item.getItemMeta();

    switch (slotClicked) {
      case 32 -> ItemEditorItemFlags.toggleHideArmorTrim(inv, user, item, meta);
      case 33 -> ItemEditorItemFlags.toggleHideAttributes(inv, user, item, meta);
      case 34 -> ItemEditorItemFlags.toggleHideDestroys(inv, user, item, meta);
      case 41 -> ItemEditorItemFlags.toggleHideDye(inv, user, item, meta);
      case 42 -> ItemEditorItemFlags.toggleHideEnchants(inv, user, item, meta);
      case 43 -> ItemEditorItemFlags.toggleHidePlacedOn(inv, user, item, meta);
      case 50 -> ItemEditorItemFlags.toggleHidePotionEffects(inv, user, item, meta);
      case 51 -> ItemEditorItemFlags.toggleHideUnbreakable(inv, user, item, meta);
    }
  }

  /**
   * Toggles an item's ability to be broken.
   *
   * @param inv  interacting inventory
   * @param user user
   */
  private static void toggleUnbreakable(Inventory inv, Player user) {
    ItemStack item = PluginData.itemEditorData.getEditedItemMap().get(user);
    ItemMeta meta = item.getItemMeta();

    if (!meta.isUnbreakable()) {
      meta.setUnbreakable(true);
      user.sendMessage(Success.ENABLE_UNBREAKABLE.message);
    } else {
      meta.setUnbreakable(false);
      user.sendMessage(Success.DISABLE_UNBREAKABLE.message);
    }
    item.setItemMeta(meta);

    ItemEditorToggles.addUnbreakableMeta(inv, meta);
  }

  /**
   * Checks if the item has lore before making changes.
   *
   * @param user   user
   * @param action interaction type
   */
  private static void readItemLore(Player user, String action) {
    ItemMeta meta = PluginData.itemEditorData.getEditedItemMap().get(user).getItemMeta();
    if (action.equals("lore-generate")) {
      ItemStack item = PluginData.itemEditorData.getEditedItemMap().get(user);
      generateLore(item, meta);
      user.sendMessage(Success.GENERATE_LORE.message);
    } else if (meta.hasLore()) {
      switch (action) {
        case "lore-clear" -> {
          ItemStack item = PluginData.itemEditorData.getEditedItemMap().get(user);
          ItemEditorMessageListener.clearLore(user, item, item.getItemMeta());
          user.sendMessage(Success.CLEAR_LORE.message);
        }
        case "lore-edit" -> {
          user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message +
              Success.INPUT_EDIT_LORE.message);
          ItemEditorAction.awaitMessageResponse(user, action);
        }
        case "lore-remove" -> {
          user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message +
              Success.INPUT_REMOVE_LORE.message);
          ItemEditorAction.awaitMessageResponse(user, action);
        }
      }
    } else {
      user.sendMessage(Failure.NO_ITEM_LORE.message);
    }
  }

  /**
   * Sets the user's interacting equipment slot.
   *
   * @param user          user
   * @param equipmentSlot interacting equipment slot.
   */
  private static void setMode(Player user, String equipmentSlot) {
    user.setMetadata(PluginPlayerMeta.SLOT.getMeta(),
        new FixedMetadataValue(Plugin.getInstance(), equipmentSlot));

    user.openInventory(ItemEditorAttributes.openAttributesMenu(user, equipmentSlot));
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(),
        new FixedMetadataValue(Plugin.getInstance(),
            InventoryMenuListener.Menu.ITEMEDITOR_ATTRIBUTES.menu));
  }

  /**
   * Determines the attribute to be set and prompts the user for an input.
   *
   * @param e    inventory click event
   * @param user user
   */
  private static void readAttribute(InventoryClickEvent e, Player user) {
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
    ItemEditorAction.awaitMessageResponse(user, "attributes");
  }

  /**
   * Determines the enchantment to be set and prompts the user for an input.
   *
   * @param e    inventory click event
   * @param user user
   */
  private static void readEnchant(InventoryClickEvent e, Player user) {
    String enchant = ChatColor.stripColor(TextFormatter.formatId(e.getCurrentItem().getItemMeta().getDisplayName()));

    user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message
        + ChatColor.WHITE + "Input " + ChatColor.AQUA
        + TextFormatter.capitalizePhrase(enchant) + ChatColor.WHITE + " value.");

    user.setMetadata(PluginPlayerMeta.TYPE.getMeta(), new FixedMetadataValue(Plugin.getInstance(), enchant));
    ItemEditorAction.awaitMessageResponse(user, "enchants");
  }

  /**
   * Determines the Aethel tag to be set and prompts the user for an input.
   *
   * @param e    inventory click event
   * @param user user
   */
  private static void readTag(InventoryClickEvent e, Player user) {
    String aethelTag = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());

    user.sendMessage(PluginMessage.Success.NOTIFICATION_INPUT.message +
        ChatColor.WHITE + "Input " + ChatColor.AQUA + aethelTag + ChatColor.WHITE + " value.");

    user.setMetadata(PluginPlayerMeta.TYPE.getMeta(), new FixedMetadataValue(Plugin.getInstance(), aethelTag));
    ItemEditorAction.awaitMessageResponse(user, "tags");
  }

  /**
   * Sends a value context for the attribute being edited.
   *
   * @param attributeName attribute name
   * @return attribute value context
   */
  private static String getAttributeValueContext(String attributeName) {
    String attributeContext = PluginMessage.Success.NOTIFICATION_INPUT.message + ChatColor.WHITE + "Base: ";
    switch (attributeName) {
      case "Attack Damage" -> attributeContext += AttributeContext.ATTACK_DAMAGE.context;
      case "Attack Speed" -> attributeContext += AttributeContext.ATTACK_SPEED.context;
      case "Critical Chance" -> attributeContext += AttributeContext.CRITICAL_CHANCE.context;
      case "Critical Damage" -> attributeContext += AttributeContext.CRITICAL_DAMAGE.context;
      case "Max Health" -> attributeContext += AttributeContext.MAX_HEALTH.context;
      case "Armor" -> attributeContext += AttributeContext.ARMOR.context;
      case "Armor Toughness" -> attributeContext += AttributeContext.ARMOR_TOUGHNESS.context;
      case "Movement Speed" -> attributeContext += AttributeContext.MOVEMENT_SPEED.context;
      case "Block" -> attributeContext += AttributeContext.BLOCK.context;
      case "Parry Chance" -> attributeContext += AttributeContext.PARRY_CHANCE.context;
      case "Parry Deflect" -> attributeContext += AttributeContext.PARRY_DEFLECT.context;
      case "Dodge Chance" -> attributeContext += AttributeContext.DODGE_CHANCE.context;
      case "Ability Damage" -> attributeContext += AttributeContext.ABILITY_DAMAGE.context;
      case "Ability Cooldown" -> attributeContext += AttributeContext.ABILITY_COOLDOWN.context;
      case "Apply Status" -> attributeContext += AttributeContext.APPLY_STATUS.context;
      case "Knockback Resistance" -> attributeContext += AttributeContext.KNOCKBACK_RESISTANCE.context;
      case "Luck" -> attributeContext += AttributeContext.LUCK.context;
    }
    return attributeContext;
  }

  /**
   * Generates an item's lore based on its plugin-related data.
   *
   * @param item interacting item
   * @param meta item meta
   */
  private static void generateLore(ItemStack item, ItemMeta meta) {
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

  private enum Success {
    ENABLE_UNBREAKABLE(ChatColor.GREEN + "[Set Unbreakable]"),
    DISABLE_UNBREAKABLE(ChatColor.RED + "[Set Unbreakable]"),
    CLEAR_LORE(ChatColor.GREEN + "[Cleared Lore]"),
    GENERATE_LORE(ChatColor.GREEN + "[Generated Lore]"),
    INPUT_DISPLAY_NAME(ChatColor.WHITE + "Input display name."),
    INPUT_CUSTOMMODELDATA(ChatColor.WHITE + "Input custom model data value."),
    INPUT_SET_LORE(ChatColor.WHITE + "Input lore to set."),
    INPUT_ADD_LORE(ChatColor.WHITE + "Input lore to add."),
    INPUT_EDIT_LORE(ChatColor.WHITE + "Input line number and lore to edit."),
    INPUT_REMOVE_LORE(ChatColor.WHITE + "Input line number to remove.");

    public final String message;

    Success(String message) {
      this.message = message;
    }
  }

  private enum Failure {
    NO_ITEM_LORE(ChatColor.RED + "Item has no lore.");

    public final String message;

    Failure(String message) {
      this.message = message;
    }
  }

  private enum AttributeContext {
    ATTACK_DAMAGE("1.0"),
    ATTACK_SPEED("4.0"),
    CRITICAL_CHANCE("0.0%"),
    CRITICAL_DAMAGE("1.25x [Input / 100]"),
    MAX_HEALTH("20.0"),
    ARMOR("0.0 [Max: 30.0]"),
    ARMOR_TOUGHNESS("0.0 [Max: 20.0]"),
    MOVEMENT_SPEED("2.0 [Input * 20]"),
    BLOCK("0.0"),
    PARRY_CHANCE("0.0%"),
    PARRY_DEFLECT("0.0%"),
    DODGE_CHANCE("0.0%"),
    ABILITY_DAMAGE("1.0x [Input / 100]"),
    ABILITY_COOLDOWN("-0.0%"),
    APPLY_STATUS("0.0%"),
    KNOCKBACK_RESISTANCE("0.0 [Max: 1.0]"),
    LUCK("0.0");

    public final String context;

    AttributeContext(String context) {
      this.context = context;
    }
  }
}
