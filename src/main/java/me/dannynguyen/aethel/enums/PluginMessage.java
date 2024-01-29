package me.dannynguyen.aethel.enums;

import org.bukkit.ChatColor;

/**
 * PluginMessage is an enum collection containing the plugin's messages.
 *
 * @author Danny Nguyen
 * @version 1.8.1
 * @since 1.7.6
 */
public class PluginMessage {
  /**
   * Sent on success.
   */
  public enum Success {
    PLUGIN_LOAD_AETHELITEMS("[Aethel] Loaded Aethel Items: "),
    PLUGIN_LOAD_FORGE("[Aethel] Loaded Forge Recipes: "),
    PLUGIN_LOAD_PLAYERSTATS("[Aethel] Loaded Player Stats: "),

    NOTIFICATION_GLOBAL(ChatColor.GREEN + "[!] "),
    NOTIFICATION_INPUT(ChatColor.GOLD + "[!] "),

    AETHELITEMS_SAVE(ChatColor.GREEN + "[Saved Aethel Item] "),
    AETHELITEMS_REMOVE(ChatColor.RED + "[Removed Aethel Item] "),
    AETHELITEMS_RELOAD(ChatColor.GREEN + "[Reloaded Aethel Items]"),
    AETHELTAGS_GET(ChatColor.GREEN + "[Get Tags] "),
    AETHELTAGS_SET(ChatColor.GREEN + "[Set Tag] "),
    AETHELTAGS_REMOVE(ChatColor.RED + "[Removed Tag] "),
    DEVELOPERMODE_ON(ChatColor.GREEN + "[Developer Mode On]"),
    DEVELOPERMODE_OFF(ChatColor.RED + "[Developer Mode Off]"),
    FORGE_SAVE(ChatColor.GREEN + "[Saved Recipe] "),
    FORGE_REMOVE(ChatColor.RED + "[Removed Recipe] "),
    FORGE_RELOAD(ChatColor.GREEN + "[Reloaded Forge Recipes]"),
    ITEMEDITOR_NAME_ITEM(ChatColor.GREEN + "[Named Item] "),
    ITEMEDITOR_SET_CUSTOMMODELDATA(ChatColor.GREEN + "[Set Custom Model Data] "),
    ITEMEDITOR_SET_LORE(ChatColor.GREEN + "[Set Lore]"),
    ITEMEDITOR_CLEAR_LORE(ChatColor.GREEN + "[Cleared Lore]"),
    ITEMEDITOR_ADD_LORE(ChatColor.GREEN + "[Added Lore]"),
    ITEMEDITOR_EDIT_LORE(ChatColor.GREEN + "[Edited Lore]"),
    ITEMEDITOR_REMOVE_LORE(ChatColor.GREEN + "[Removed Lore]"),
    ITEMEDITOR_ENABLE_HIDE_ARMOR_TRIM(ChatColor.GREEN + "[Hide Armor Trim]"),
    ITEMEDITOR_ENABLE_HIDE_ATTRIBUTES(ChatColor.GREEN + "[Hide Attributes]"),
    ITEMEDITOR_ENABLE_HIDE_DESTROYS(ChatColor.GREEN + "[Hide Destroys]"),
    ITEMEDITOR_ENABLE_HIDE_DYE(ChatColor.GREEN + "[Hide Dye]"),
    ITEMEDITOR_ENABLE_HIDE_ENCHANTS(ChatColor.GREEN + "[Hide Enchants]"),
    ITEMEDITOR_ENABLE_HIDE_PLACED_ON(ChatColor.GREEN + "[Hide Placed On]"),
    ITEMEDITOR_ENABLE_HIDE_POTION_EFFECTS(ChatColor.GREEN + "[Hide Potion Effects]"),
    ITEMEDITOR_ENABLE_HIDE_UNBREAKABLE(ChatColor.GREEN + "[Hide Unbreakable]"),
    ITEMEDITOR_DISABLE_HIDE_ARMOR_TRIM(ChatColor.RED + "[Hide Armor Trim]"),
    ITEMEDITOR_DISABLE_HIDE_ATTRIBUTES(ChatColor.RED + "[Hide Attributes]"),
    ITEMEDITOR_DISABLE_HIDE_DESTROYS(ChatColor.RED + "[Hide Destroys]"),
    ITEMEDITOR_DISABLE_HIDE_DYE(ChatColor.RED + "[Hide Dye]"),
    ITEMEDITOR_DISABLE_HIDE_ENCHANTS(ChatColor.RED + "[Hide Enchants]"),
    ITEMEDITOR_DISABLE_HIDE_PLACED_ON(ChatColor.RED + "[Hide Placed On]"),
    ITEMEDITOR_DISABLE_HIDE_POTION_EFFECTS(ChatColor.RED + "[Hide Potion Effects]"),
    ITEMEDITOR_DISABLE_HIDE_UNBREAKABLE(ChatColor.RED + "[Hide Unbreakable]"),
    ITEMEDITOR_ENABLE_UNBREAKABLE(ChatColor.GREEN + "[Set Unbreakable]"),
    ITEMEDITOR_DISABLE_UNBREAKABLE(ChatColor.RED + "[Set Unbreakable]"),
    ITEMEDITOR_INPUT_DISPLAY_NAME(ChatColor.WHITE + "Input display name."),
    ITEMEDITOR_INPUT_CUSTOMMODELDATA(ChatColor.WHITE + "Input custom model data value."),
    ITEMEDITOR_INPUT_SET_LORE(ChatColor.WHITE + "Input lore to set."),
    ITEMEDITOR_INPUT_ADD_LORE(ChatColor.WHITE + "Input lore to add."),
    ITEMEDITOR_INPUT_EDIT_LORE(ChatColor.WHITE + "Input line number and lore to edit."),
    ITEMEDITOR_INPUT_REMOVE_LORE(ChatColor.WHITE + "Input line number to remove.");

    public final String message;

    Success(String message) {
      this.message = message;
    }
  }

  /**
   * Sent on failure.
   */
  public enum Failure {
    PLUGIN_INVALID_ITEM_FILE("[Aethel] Invalid item file: "),
    PLUGIN_INVALID_FORGE_RECIPE_FILE("[Aethel] Invalid forge recipe file: "),
    PLUGIN_INVALID_PLAYER_HEAD_TEXTURE("[Aethel] Invalid player head texture: "),

    NOTIFICATION_ERROR(ChatColor.RED + "[!] Error"),
    INVALID_TEXTURE(ChatColor.RED + "Invalid texture."),

    PLAYER_ONLY_COMMAND(ChatColor.RED + "Player-only command."),
    INSUFFICIENT_PERMISSION(ChatColor.RED + "Insufficient permission."),
    NO_PARAMETERS(ChatColor.RED + "No parameters provided."),
    UNRECOGNIZED_PARAMETER(ChatColor.RED + "Unrecognized parameter."),
    UNRECOGNIZED_PARAMETERS(ChatColor.RED + "Unrecognized parameters."),
    NO_MAIN_HAND_ITEM(ChatColor.RED + "No main hand item."),

    AETHELITEMS_NO_ITEM(ChatColor.RED + "No item to save."),
    AETHELITEMS_SAVE_FAILED(ChatColor.RED + "Unable to save item."),
    AETHELTAGS_NO_TAGS(ChatColor.RED + "No tags found."),
    FORGE_CRAFT_INSUFFICIENT_COMPONENTS(ChatColor.RED + "Insufficient components."),
    FORGE_SAVE_NO_COMPONENTS(ChatColor.RED + "No recipe components."),
    FORGE_SAVE_NO_RESULTS(ChatColor.RED + "No recipe results."),
    FORGE_SAVE_FAILED(ChatColor.RED + "Unable to save recipe."),
    ITEMEDITOR_NO_LORE(ChatColor.RED + "Item has no lore."),
    ITEMEDITOR_INVALID_CUSTOMMODELDATA(ChatColor.RED + "Invalid custom model data."),
    ITEMEDITOR_INVALID_LINE(ChatColor.RED + "Invalid line number."),
    ITEMEDITOR_NONEXISTENT_LINE(ChatColor.RED + "Line does not exist."),
    ITEMEDITOR_INVALID_VALUE(ChatColor.RED + "Invalid value."),
    ITEMEDITOR_INVALID_ENCHANT_LEVEL(ChatColor.RED + "Specify a level between 0 - 32767.");

    public final String message;

    Failure(String message) {
      this.message = message;
    }
  }
}
