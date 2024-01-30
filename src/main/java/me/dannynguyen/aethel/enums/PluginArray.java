package me.dannynguyen.aethel.enums;

/**
 * PluginArray is an enum containing the plugin's arrays.
 *
 * @author Danny Nguyen
 * @version 1.8.3
 * @since 1.8.3
 */
public enum PluginArray {
  MINECRAFT_ATTRIBUTES(new String[]{
      "Attack Damage", "Attack Speed", "Max Health", "Armor", "Armor Toughness",
      "Movement Speed", "Knockback Resistance", "Luck"}),
  AETHEL_ATTRIBUTE_OFFENSE(new String[]{
      "Attack Damage", "Attack Speed",
      "Critical Chance", "Critical Damage"}),
  AETHEL_ATTRIBUTE_DEFENSE(new String[]{
      "Max Health", "Armor", "Armor Toughness",
      "Movement Speed", "Block", "Parry", "Dodge"}),
  AETHEL_ATTRIBUTE_OTHER(new String[]{
      "Ability Damage", "Ability Cooldown",
      "Apply Status", "Knockback Resistance", "Luck"}),

  PLAYERSTAT_CATEGORY_NAMES(new String[]{
      "Activities", "Containers", "Damage", "Entity Types",
      "General", "Interactions", "Materials", "Movement"}),
  PLAYERSTAT_CATEGORY_ACTIVITIES(new String[]{
      "ANIMALS_BRED", "ARMOR_CLEANED", "BANNER_CLEANED", "BELL_RING",
      "CAKE_SLICES_EATEN", "CAULDRON_FILLED", "CAULDRON_USED", "CLEAN_SHULKER_BOX",
      "DROP_COUNT", "FISH_CAUGHT", "FLOWER_POTTED", "ITEM_ENCHANTED", "NOTEBLOCK_PLAYED",
      "NOTEBLOCK_TUNED", "RAID_TRIGGER", "RAID_WIN", "RECORD_PLAYED", "SLEEP_IN_BED",
      "TALKED_TO_VILLAGER", "TARGET_HIT", "TRADED_WITH_VILLAGER"}),
  PLAYERSTAT_CATEGORY_CONTAINERS(new String[]{
      "CHEST_OPENED", "DISPENSER_INSPECTED", "DROPPER_INSPECTED",
      "ENDERCHEST_OPENED", "HOPPER_INSPECTED", "OPEN_BARREL",
      "SHULKER_BOX_OPENED", "TRAPPED_CHEST_TRIGGERED"}),
  PLAYERSTAT_CATEGORY_DAMAGE(new String[]{
      "DAMAGE_ABSORBED", "DAMAGE_BLOCKED_BY_SHIELD", "DAMAGE_DEALT",
      "DAMAGE_DEALT_ABSORBED", "DAMAGE_DEALT_RESISTED", "DAMAGE_RESISTED", "DAMAGE_TAKEN"}),
  PLAYERSTAT_CATEGORY_GENERAL(new String[]{
      "DEATHS", "LEAVE_GAME", "PLAY_ONE_MINUTE",
      "TIME_SINCE_DEATH", "TIME_SINCE_REST", "TOTAL_WORLD_TIME"}),
  PLAYERSTAT_CATEGORY_MOVEMENT(new String[]{
      "AVIATE_ONE_CM", "BOAT_ONE_CM", "CLIMB_ONE_CM", "CROUCH_ONE_CM",
      "FALL_ONE_CM", "FLY_ONE_CM", "HORSE_ONE_CM", "JUMP", "MINECART_ONE_CM",
      "PIG_ONE_CM", "SNEAK_TIME", "SPRINT_ONE_CM", "STRIDER_ONE_CM",
      "SWIM_ONE_CM", "WALK_ON_WATER_ONE_CM", "WALK_ONE_CM", "WALK_UNDER_WATER_ONE_CM"}),
  PLAYERSTAT_CATEGORY_INTERACTIONS(new String[]{
      "BEACON_INTERACTION", "BREWINGSTAND_INTERACTION", "CRAFTING_TABLE_INTERACTION",
      "FURNACE_INTERACTION", "INTERACT_WITH_ANVIL", "INTERACT_WITH_BLAST_FURNACE",
      "INTERACT_WITH_CAMPFIRE", "INTERACT_WITH_CARTOGRAPHY_TABLE", "INTERACT_WITH_GRINDSTONE",
      "INTERACT_WITH_LECTERN", "INTERACT_WITH_LOOM", "INTERACT_WITH_SMITHING_TABLE",
      "INTERACT_WITH_SMOKER", "INTERACT_WITH_STONECUTTER"});

  public final String[] array;

  PluginArray(String[] array) {
    this.array = array;
  }
}
