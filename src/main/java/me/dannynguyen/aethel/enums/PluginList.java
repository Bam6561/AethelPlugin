package me.dannynguyen.aethel.enums;

import org.bukkit.ChatColor;

import java.util.List;

/**
 * PluginList is an enum containing the plugin's lists.
 *
 * @author Danny Nguyen
 * @version 1.7.13
 * @since 1.7.13
 */
public enum PluginList {
  SPIGOT_FORMAT_CODES(List.of(ChatColor.WHITE + "&k " + ChatColor.MAGIC + "Magic",
      ChatColor.WHITE + "&l " + ChatColor.BOLD + "Bold",
      ChatColor.WHITE + "&m " + ChatColor.STRIKETHROUGH + "Strike",
      ChatColor.WHITE + "&n " + ChatColor.UNDERLINE + "Underline",
      ChatColor.WHITE + "&o " + ChatColor.ITALIC + "Italic",
      ChatColor.WHITE + "&r " + ChatColor.RESET + "Reset")),
  SPIGOT_COLOR_CODES(List.of(ChatColor.WHITE + "&0 " + ChatColor.BLACK + "Black",
      ChatColor.WHITE + "&1 " + ChatColor.DARK_BLUE + "Dark Blue",
      ChatColor.WHITE + "&2 " + ChatColor.DARK_GREEN + "Dark Green",
      ChatColor.WHITE + "&3 " + ChatColor.DARK_RED + "Dark Red",
      ChatColor.WHITE + "&4 " + ChatColor.DARK_AQUA + "Dark Aqua",
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
      ChatColor.WHITE + "&f " + ChatColor.WHITE + "White")),

  MINECRAFT_ATTRIBUTES(List.of(
      "Attack Damage", "Attack Speed", "Max Health", "Armor", "Armor Toughness",
      "Movement Speed", "Knockback Resistance", "Luck")),
  AETHEL_ATTRIBUTE_OFFENSE(List.of(
      "Attack Damage", "Attack Speed",
      "Critical Chance", "Critical Damage")),
  AETHEL_ATTRIBUTE_DEFENSE(List.of(
      "Max Health", "Armor", "Armor Toughness",
      "Movement Speed", "Block", "Parry", "Dodge")),
  AETHEL_ATTRIBUTE_OTHER(List.of(
      "Ability Damage", "Ability Cooldown",
      "Apply Status", "Knockback Resistance", "Luck")),

  PLAYERSTAT_CATEGORY_NAMES(List.of(
      "Activities", "Containers", "Damage", "Entity Types",
      "General", "Interactions", "Materials", "Movement")),
  PLAYERSTAT_CATEGORY_ACTIVITIES(List.of("ANIMALS_BRED", "ARMOR_CLEANED", "BANNER_CLEANED",
      "BELL_RING", "CAKE_SLICES_EATEN", "CAULDRON_FILLED", "CAULDRON_USED",
      "CLEAN_SHULKER_BOX", "DROP_COUNT", "FISH_CAUGHT", "FLOWER_POTTED", "ITEM_ENCHANTED",
      "NOTEBLOCK_PLAYED", "NOTEBLOCK_TUNED", "RAID_TRIGGER", "RAID_WIN", "RECORD_PLAYED",
      "SLEEP_IN_BED", "TALKED_TO_VILLAGER", "TARGET_HIT", "TRADED_WITH_VILLAGER")),
  PLAYERSTAT_CATEGORY_CONTAINERS(List.of("CHEST_OPENED", "DISPENSER_INSPECTED",
      "DROPPER_INSPECTED", "ENDERCHEST_OPENED", "HOPPER_INSPECTED",
      "OPEN_BARREL", "SHULKER_BOX_OPENED", "TRAPPED_CHEST_TRIGGERED")),
  PLAYERSTAT_CATEGORY_DAMAGE(List.of("DAMAGE_ABSORBED", "DAMAGE_BLOCKED_BY_SHIELD", "DAMAGE_DEALT",
      "DAMAGE_DEALT_ABSORBED", "DAMAGE_DEALT_RESISTED", "DAMAGE_RESISTED", "DAMAGE_TAKEN")),
  PLAYERSTAT_CATEGORY_GENERAL(List.of("DEATHS", "LEAVE_GAME", "PLAY_ONE_MINUTE",
      "TIME_SINCE_DEATH", "TIME_SINCE_REST", "TOTAL_WORLD_TIME")),
  PLAYERSTAT_CATEGORY_MOVEMENT(List.of("AVIATE_ONE_CM", "BOAT_ONE_CM", "CLIMB_ONE_CM", "CROUCH_ONE_CM",
      "FALL_ONE_CM", "FLY_ONE_CM", "HORSE_ONE_CM", "JUMP", "MINECART_ONE_CM", "PIG_ONE_CM",
      "SNEAK_TIME", "SPRINT_ONE_CM", "STRIDER_ONE_CM", "SWIM_ONE_CM", "WALK_ON_WATER_ONE_CM",
      "WALK_ONE_CM", "WALK_UNDER_WATER_ONE_CM")),
  PLAYERSTAT_CATEGORY_INTERACTIONS(List.of("BEACON_INTERACTION", "BREWINGSTAND_INTERACTION",
      "CRAFTING_TABLE_INTERACTION", "FURNACE_INTERACTION", "INTERACT_WITH_ANVIL",
      "INTERACT_WITH_BLAST_FURNACE", "INTERACT_WITH_CAMPFIRE", "INTERACT_WITH_CARTOGRAPHY_TABLE",
      "INTERACT_WITH_GRINDSTONE", "INTERACT_WITH_LECTERN", "INTERACT_WITH_LOOM",
      "INTERACT_WITH_SMITHING_TABLE", "INTERACT_WITH_SMOKER", "INTERACT_WITH_STONECUTTER"));

  public final List<String> list;

  PluginList(List<String> list) {
    this.list = list;
  }
}
