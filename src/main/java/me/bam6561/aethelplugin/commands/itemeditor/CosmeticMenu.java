package me.bam6561.aethelplugin.commands.itemeditor;

import me.bam6561.aethelplugin.Plugin;
import me.bam6561.aethelplugin.enums.plugin.Key;
import me.bam6561.aethelplugin.enums.plugin.PlayerHead;
import me.bam6561.aethelplugin.interfaces.Menu;
import me.bam6561.aethelplugin.utils.item.ItemCreator;
import me.bam6561.aethelplugin.utils.item.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a menu that allows the user to edit an item's cosmetic metadata.
 * <p>
 * From this menu, the user can also navigate to gameplay metadata menus.
 *
 * @author Danny Nguyen
 * @version 1.27.0
 * @since 1.6.7
 */
public class CosmeticMenu implements Menu {
  /**
   * Placeable entities.
   */
  private static final Set<Material> placeableEntities = Set.of(
      Material.ACACIA_BOAT, Material.ACACIA_CHEST_BOAT, Material.ARMOR_STAND, Material.BAMBOO_RAFT,
      Material.BAMBOO_CHEST_RAFT, Material.BEETROOT_SEEDS, Material.BIRCH_BOAT, Material.BIRCH_CHEST_BOAT,
      Material.BUCKET, Material.AXOLOTL_BUCKET, Material.COD_BUCKET, Material.PUFFERFISH_BUCKET, Material.SALMON_BUCKET,
      Material.TADPOLE_BUCKET, Material.TROPICAL_FISH_BUCKET, Material.CARROT, Material.CHERRY_BOAT,
      Material.CHERRY_CHEST_BOAT, Material.COCOA_BEANS, Material.DARK_OAK_BOAT, Material.DARK_OAK_CHEST_BOAT,
      Material.END_CRYSTAL, Material.FIRE_CHARGE, Material.FIREWORK_ROCKET, Material.GLOW_BERRIES,
      Material.GLOW_ITEM_FRAME, Material.ITEM_FRAME, Material.JUNGLE_BOAT, Material.JUNGLE_CHEST_BOAT, Material.KELP,
      Material.LAVA_BUCKET, Material.LEAD, Material.MANGROVE_BOAT, Material.MANGROVE_CHEST_BOAT, Material.MELON_SEEDS,
      Material.MINECART, Material.CHEST_MINECART, Material.COMMAND_BLOCK_MINECART, Material.FURNACE_MINECART,
      Material.HOPPER_MINECART, Material.TNT_MINECART, Material.NETHER_WART, Material.OAK_BOAT, Material.OAK_CHEST_BOAT,
      Material.PAINTING, Material.PITCHER_POD, Material.POTATO, Material.POWDER_SNOW_BUCKET, Material.PUMPKIN_SEEDS,
      Material.REDSTONE, Material.SPRUCE_BOAT, Material.SPRUCE_CHEST_BOAT, Material.STRING, Material.SWEET_BERRIES,
      Material.TORCHFLOWER_SEEDS, Material.WATER_BUCKET, Material.WHEAT_SEEDS);

  /**
   * GUI.
   */
  private final Inventory menu;

  /**
   * GUI user.
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
   * Associates a new Cosmetic menu with its user and item.
   *
   * @param user user
   */
  public CosmeticMenu(@NotNull Player user) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.item = Plugin.getData().getEditedItemCache().getEditedItems().get(user.getUniqueId());
    this.meta = item.getItemMeta();
    this.menu = createMenu();
  }

  /**
   * Creates and names a Cosmetic menu with its item being edited.
   *
   * @return Cosmetic menu
   */
  private Inventory createMenu() {
    Inventory inv = Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "ItemEditor");
    inv.setItem(4, item);
    return inv;
  }

  /**
   * Sets the menu to display interactions with cosmetic metadata.
   *
   * @return Cosmetic menu
   */
  @NotNull
  public Inventory getMainMenu() {
    addDisplayName();
    addCustomModelData();
    addDurability();
    addRepairCost();
    addLore();
    addGameplay();
    addItemFlags();
    addUnusable(menu, item);
    addNonPlaceable(menu, item);
    addNonEdible(menu, item);
    addUnbreakable(menu, meta);
    addContext();
    return menu;
  }

  /**
   * Adds contextual help.
   */
  private void addContext() {
    ItemStack formatCodes = ItemCreator.createPluginPlayerHead(PlayerHead.QUESTION_MARK_WHITE.getHead(),
        ChatColor.GREEN + "Format Codes", List.of(
            ChatColor.WHITE + "&k " + ChatColor.MAGIC + "Magic",
            ChatColor.WHITE + "&l " + ChatColor.BOLD + "Bold",
            ChatColor.WHITE + "&m " + ChatColor.STRIKETHROUGH + "Strike",
            ChatColor.WHITE + "&n " + ChatColor.UNDERLINE + "Underline",
            ChatColor.WHITE + "&o " + ChatColor.ITALIC + "Italic",
            ChatColor.WHITE + "&r " + ChatColor.RESET + "Reset"));
    ItemStack colorCodes = ItemCreator.createPluginPlayerHead(PlayerHead.QUESTION_MARK_WHITE.getHead(),
        ChatColor.GREEN + "Color Codes", List.of(
            ChatColor.WHITE + "&0 " + ChatColor.BLACK + "Black",
            ChatColor.WHITE + "&1 " + ChatColor.DARK_BLUE + "Dark Blue",
            ChatColor.WHITE + "&2 " + ChatColor.DARK_GREEN + "Dark Green",
            ChatColor.WHITE + "&3 " + ChatColor.DARK_AQUA + "Dark Aqua",
            ChatColor.WHITE + "&4 " + ChatColor.DARK_RED + "Dark Red",
            ChatColor.WHITE + "&5 " + ChatColor.DARK_PURPLE + "Dark Purple",
            ChatColor.WHITE + "&6 " + ChatColor.GOLD + "Gold",
            ChatColor.WHITE + "&7 " + ChatColor.GRAY + "Gray",
            ChatColor.WHITE + "&8 " + ChatColor.DARK_GRAY + "Dark Gray",
            ChatColor.WHITE + "&9 " + ChatColor.BLUE + "Blue",
            ChatColor.WHITE + "&a " + ChatColor.GREEN + "Green",
            ChatColor.WHITE + "&b " + ChatColor.AQUA + "Aqua",
            ChatColor.WHITE + "&c " + ChatColor.RED + "Red",
            ChatColor.WHITE + "&d " + ChatColor.LIGHT_PURPLE + "Light Purple",
            ChatColor.WHITE + "&e " + ChatColor.YELLOW + "Yellow",
            ChatColor.WHITE + "&f " + ChatColor.WHITE + "White"));
    menu.setItem(0, formatCodes);
    menu.setItem(1, colorCodes);
  }

  /**
   * Adds the display name button.
   */
  private void addDisplayName() {
    menu.setItem(9, ItemCreator.createItem(Material.NAME_TAG, ChatColor.AQUA + "Display Name", List.of(ChatColor.WHITE + ItemReader.readName(item))));
  }

  /**
   * Adds the custom model data button.
   */
  private void addCustomModelData() {
    menu.setItem(10, !meta.hasCustomModelData() ?
        ItemCreator.createItem(Material.OXEYE_DAISY, ChatColor.AQUA + "Custom Model Data") :
        ItemCreator.createItem(Material.OXEYE_DAISY, ChatColor.AQUA + "Custom Model Data", List.of(ChatColor.WHITE + String.valueOf(meta.getCustomModelData()))));
  }

  /**
   * Adds the durability and reinforcement buttons.
   */
  private void addDurability() {
    if (item.getType().getMaxDurability() != 0) {
      Damageable damageable = (Damageable) meta;
      short maxDurability = item.getType().getMaxDurability();
      int durabilityValue = maxDurability - damageable.getDamage();
      menu.setItem(11, ItemCreator.createItem(Material.CRACKED_STONE_BRICKS, ChatColor.AQUA + "Durability", List.of(ChatColor.WHITE + "" + durabilityValue + " / " + maxDurability)));

      PersistentDataContainer itemTags = meta.getPersistentDataContainer();
      if (itemTags.has(Key.RPG_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER)) {
        menu.setItem(19, ItemCreator.createItem(Material.NETHERITE_SCRAP, ChatColor.AQUA + "Reinforcement", List.of(ChatColor.WHITE + "" + itemTags.get(Key.RPG_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER))));
      }
      menu.setItem(20, ItemCreator.createItem(Material.NETHERITE_INGOT, ChatColor.AQUA + "Max Reinforcement", List.of(ChatColor.WHITE + "" + itemTags.getOrDefault(Key.RPG_MAX_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER, 0))));
    }
  }

  /**
   * Adds the repair cost button.
   */
  private void addRepairCost() {
    Repairable repairable = (Repairable) meta;
    menu.setItem(12, ItemCreator.createItem(Material.ANVIL, ChatColor.AQUA + "Repair Cost", List.of(ChatColor.WHITE + "" + repairable.getRepairCost())));
  }

  /**
   * Adds lore buttons.
   */
  private void addLore() {
    ItemStack lore;
    if (!meta.hasLore()) {
      lore = ItemCreator.createPluginPlayerHead(PlayerHead.QUESTION_MARK_WHITE.getHead(), ChatColor.GREEN + "Lore", List.of(ChatColor.GRAY + "None set."));
    } else {
      List<String> loreLines = meta.getLore();
      for (int i = 0; i < loreLines.size(); i++) {
        loreLines.set(i, ChatColor.WHITE + "" + (i + 1) + " " + ChatColor.RESET + loreLines.get(i));
      }
      lore = ItemCreator.createPluginPlayerHead(PlayerHead.QUESTION_MARK_WHITE.getHead(), ChatColor.GREEN + "Lore", loreLines);
    }
    menu.setItem(36, lore);
    menu.setItem(37, ItemCreator.createItem(Material.PAPER, ChatColor.AQUA + "Set Lore", List.of(ChatColor.WHITE + "Separate lines by \",, \".")));
    menu.setItem(38, ItemCreator.createItem(Material.PAPER, ChatColor.AQUA + "Clear Lore"));
    menu.setItem(45, ItemCreator.createItem(Material.PAPER, ChatColor.AQUA + "Add Lore"));
    menu.setItem(46, ItemCreator.createItem(Material.PAPER, ChatColor.AQUA + "Edit Lore", List.of(ChatColor.WHITE + "Specify line, then new text.")));
    menu.setItem(47, ItemCreator.createItem(Material.PAPER, ChatColor.AQUA + "Remove Lore", List.of(ChatColor.WHITE + "Specify line.")));
    menu.setItem(48, ItemCreator.createItem(Material.PAPER, ChatColor.AQUA + "Generate Lore", List.of(ChatColor.WHITE + "Generates plugin-related lore.")));
  }

  /**
   * Adds Minecraft, {@link AttributeMenu}, {@link EnchantmentMenu},
   * {@link PotionMenu} {@link PassiveMenu}, {@link ActiveMenu}, and {@link TagMenu} buttons.
   */
  private void addGameplay() {
    menu.setItem(14, ItemCreator.createItem(Material.IRON_HELMET, ChatColor.AQUA + "Minecraft Attributes", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(15, ItemCreator.createItem(Material.DIAMOND_HELMET, ChatColor.AQUA + "Aethel Attributes", ItemFlag.HIDE_ATTRIBUTES));
    menu.setItem(16, ItemCreator.createItem(Material.ENCHANTED_BOOK, ChatColor.AQUA + "Enchantments"));
    if (meta instanceof PotionMeta) {
      menu.setItem(17, ItemCreator.createItem(Material.POTION, ChatColor.AQUA + "Potion Effects", ItemFlag.HIDE_ADDITIONAL_TOOLTIP));
    }
    menu.setItem(23, ItemCreator.createItem(Material.REDSTONE, ChatColor.AQUA + "Passive Abilities"));
    menu.setItem(24, ItemCreator.createItem(Material.GLOWSTONE_DUST, ChatColor.AQUA + "Active Abilities"));
    if (item.getType().isEdible()) {
      menu.setItem(26, ItemCreator.createItem(Material.APPLE, ChatColor.AQUA + "Edible Active Abilities"));
    }
    menu.setItem(35, ItemCreator.createItem(Material.NETHER_STAR, ChatColor.AQUA + "Plugin Tags"));
  }

  /**
   * Adds item flag toggle buttons.
   */
  private void addItemFlags() {
    addHideArmorTrim(menu, meta);
    addHideAttributes(menu, meta);
    addHideDestroys(menu, meta);
    addHideDye(menu, meta);
    addHideEnchants(menu, meta);
    addHidePlacedOn(menu, meta);
    addHidePotionEffects(menu, meta);
    addHideUnbreakable(menu, meta);
  }

  /**
   * Adds the unusable toggle button.
   *
   * @param menu Cosmetic menu
   * @param item interacting item
   */
  protected static void addUnusable(@NotNull Inventory menu, @NotNull ItemStack item) {
    Objects.requireNonNull(menu, "Null menu");
    Objects.requireNonNull(item, "Null item");
    PersistentDataContainer itemTags = item.getItemMeta().getPersistentDataContainer();
    if (itemTags.has(Key.UNUSABLE.getNamespacedKey(), PersistentDataType.BOOLEAN)) {
      menu.setItem(6, ItemCreator.createItem(Material.BARRIER, ChatColor.AQUA + "Unusable", List.of(ChatColor.RED + "False")));
    } else {
      menu.setItem(6, ItemCreator.createItem(Material.ENDER_EYE, ChatColor.AQUA + "Unusable", List.of(ChatColor.GREEN + "True")));
    }
  }

  /**
   * Adds the non-placeable toggle button.
   *
   * @param menu Cosmetic menu
   * @param item interacting item
   */
  protected static void addNonPlaceable(@NotNull Inventory menu, @NotNull ItemStack item) {
    Objects.requireNonNull(menu, "Null menu");
    Objects.requireNonNull(item, "Null item");
    Material material = item.getType();
    if (material.isBlock() || placeableEntities.contains(material)) {
      PersistentDataContainer itemTags = item.getItemMeta().getPersistentDataContainer();
      if (itemTags.has(Key.NON_PLACEABLE.getNamespacedKey(), PersistentDataType.BOOLEAN)) {
        menu.setItem(7, ItemCreator.createItem(Material.BARRIER, ChatColor.AQUA + "Non-Placeable", List.of(ChatColor.RED + "False")));
      } else {
        menu.setItem(7, ItemCreator.createItem(Material.GRASS_BLOCK, ChatColor.AQUA + "Placeable", List.of(ChatColor.GREEN + "True")));
      }
    }
  }

  /**
   * Adds the non-edible toggle button.
   *
   * @param menu Cosmetic menu
   * @param item interacting item
   */
  protected static void addNonEdible(@NotNull Inventory menu, @NotNull ItemStack item) {
    Objects.requireNonNull(menu, "Null menu");
    Objects.requireNonNull(item, "Null item");
    if (item.getType().isEdible()) {
      PersistentDataContainer itemTags = item.getItemMeta().getPersistentDataContainer();
      if (itemTags.has(Key.NON_EDIBLE.getNamespacedKey(), PersistentDataType.BOOLEAN)) {
        menu.setItem(8, ItemCreator.createItem(Material.BARRIER, ChatColor.AQUA + "Non-Edible", List.of(ChatColor.RED + "False")));
      } else {
        menu.setItem(8, ItemCreator.createItem(Material.COOKIE, ChatColor.AQUA + "Edible", List.of(ChatColor.GREEN + "True")));
      }
    }
  }

  /**
   * Adds the unbreakable toggle button.
   *
   * @param menu Cosmetic menu
   * @param meta item meta
   */
  protected static void addUnbreakable(@NotNull Inventory menu, @NotNull ItemMeta meta) {
    Objects.requireNonNull(meta, "Null meta");
    boolean disabled = !meta.isUnbreakable();
    String unbreakable = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    Objects.requireNonNull(menu, "Null menu").setItem(21, ItemCreator.createItem(disabled ? Material.CLAY : Material.BEDROCK, ChatColor.AQUA + "Unbreakable", List.of(unbreakable)));
  }

  /**
   * Adds hide armor trim toggle button.
   *
   * @param menu Cosmetic menu
   * @param meta item meta
   */
  protected static void addHideArmorTrim(@NotNull Inventory menu, @NotNull ItemMeta meta) {
    Objects.requireNonNull(meta, "Null meta");
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_ARMOR_TRIM);
    String armorTrim = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    Objects.requireNonNull(menu, "Null menu").setItem(41, ItemCreator.createItem(disabled ? Material.RED_CONCRETE_POWDER : Material.GREEN_CONCRETE_POWDER, ChatColor.AQUA + "Hide Armor Trim", List.of(armorTrim)));
  }

  /**
   * Adds hide attributes toggle button.
   *
   * @param menu Cosmetic menu
   * @param meta item meta
   */
  protected static void addHideAttributes(@NotNull Inventory menu, @NotNull ItemMeta meta) {
    Objects.requireNonNull(meta, "Null meta");
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES);
    String attributes = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    Objects.requireNonNull(menu, "Null menu").setItem(42, ItemCreator.createItem(disabled ? Material.RED_CONCRETE_POWDER : Material.GREEN_CONCRETE_POWDER, ChatColor.AQUA + "Hide Attributes", List.of(attributes)));
  }

  /**
   * Adds hide destroys toggle button.
   *
   * @param menu Cosmetic menu
   * @param meta item meta
   */
  protected static void addHideDestroys(@NotNull Inventory menu, @NotNull ItemMeta meta) {
    Objects.requireNonNull(meta, "Null meta");
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_DESTROYS);
    String destroys = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    Objects.requireNonNull(menu, "Null menu").setItem(43, ItemCreator.createItem(disabled ? Material.RED_CONCRETE_POWDER : Material.GREEN_CONCRETE_POWDER, ChatColor.AQUA + "Hide Destroys", List.of(destroys)));
  }

  /**
   * Adds hide dye toggle button.
   *
   * @param menu Cosmetic menu
   * @param meta item meta
   */
  protected static void addHideDye(@NotNull Inventory menu, @NotNull ItemMeta meta) {
    Objects.requireNonNull(meta, "Null meta");
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_DYE);
    String dye = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    Objects.requireNonNull(menu, "Null menu").setItem(44, ItemCreator.createItem(disabled ? Material.RED_CONCRETE_POWDER : Material.GREEN_CONCRETE_POWDER, ChatColor.AQUA + "Hide Dye", List.of(dye)));
  }

  /**
   * Adds hide enchants toggle button.
   *
   * @param menu Cosmetic menu
   * @param meta item meta
   */
  protected static void addHideEnchants(@NotNull Inventory menu, @NotNull ItemMeta meta) {
    Objects.requireNonNull(meta, "Null meta");
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS);
    String enchants = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    Objects.requireNonNull(menu, "Null menu").setItem(50, ItemCreator.createItem(disabled ? Material.RED_CONCRETE_POWDER : Material.GREEN_CONCRETE_POWDER, ChatColor.AQUA + "Hide Enchants", List.of(enchants)));
  }

  /**
   * Adds hide placed on toggle button.
   *
   * @param menu Cosmetic menu
   * @param meta item meta
   */
  protected static void addHidePlacedOn(@NotNull Inventory menu, @NotNull ItemMeta meta) {
    Objects.requireNonNull(meta, "Null meta");
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_PLACED_ON);
    String placedOn = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    Objects.requireNonNull(menu, "Null menu").setItem(51, ItemCreator.createItem(disabled ? Material.RED_CONCRETE_POWDER : Material.GREEN_CONCRETE_POWDER, ChatColor.AQUA + "Hide Placed On", List.of(placedOn)));
  }

  /**
   * Adds hide potion effects toggle button.
   *
   * @param menu Cosmetic menu
   * @param meta item meta
   */
  protected static void addHidePotionEffects(@NotNull Inventory menu, @NotNull ItemMeta meta) {
    Objects.requireNonNull(meta, "Null meta");
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
    String potionEffects = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    Objects.requireNonNull(menu, "Null menu").setItem(52, ItemCreator.createItem(disabled ? Material.RED_CONCRETE_POWDER : Material.GREEN_CONCRETE_POWDER, ChatColor.AQUA + "Hide Potion Effects", List.of(potionEffects)));
  }

  /**
   * Adds hide unbreakable toggle button.
   *
   * @param menu Cosmetic menu
   * @param meta item meta
   */
  protected static void addHideUnbreakable(@NotNull Inventory menu, @NotNull ItemMeta meta) {
    Objects.requireNonNull(meta, "Null meta");
    boolean disabled = !meta.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE);
    String unbreakable = disabled ? ChatColor.RED + "False" : ChatColor.GREEN + "True";
    Objects.requireNonNull(menu, "Null menu").setItem(53, ItemCreator.createItem(disabled ? Material.RED_CONCRETE_POWDER : Material.GREEN_CONCRETE_POWDER, ChatColor.AQUA + "Hide Unbreakable", List.of(unbreakable)));
  }
}
