package me.dannynguyen.aethel.commands.playerstat;

import org.jetbrains.annotations.NotNull;

/**
 * Types of player statistics.
 *
 * @author Danny Nguyen
 * @version 1.17.14
 * @since 1.17.14
 */
enum StatisticType {
  /**
   * Player activities.
   */
  ACTIVITIES("Activities", new org.bukkit.Statistic[]{
      org.bukkit.Statistic.ANIMALS_BRED, org.bukkit.Statistic.ARMOR_CLEANED, org.bukkit.Statistic.BANNER_CLEANED,
      org.bukkit.Statistic.BELL_RING, org.bukkit.Statistic.CAKE_SLICES_EATEN, org.bukkit.Statistic.CAULDRON_FILLED,
      org.bukkit.Statistic.CAULDRON_USED, org.bukkit.Statistic.CLEAN_SHULKER_BOX, org.bukkit.Statistic.DROP_COUNT,
      org.bukkit.Statistic.FISH_CAUGHT, org.bukkit.Statistic.FLOWER_POTTED, org.bukkit.Statistic.ITEM_ENCHANTED,
      org.bukkit.Statistic.NOTEBLOCK_PLAYED, org.bukkit.Statistic.NOTEBLOCK_TUNED, org.bukkit.Statistic.RAID_TRIGGER,
      org.bukkit.Statistic.RAID_WIN, org.bukkit.Statistic.RECORD_PLAYED, org.bukkit.Statistic.SLEEP_IN_BED,
      org.bukkit.Statistic.TALKED_TO_VILLAGER, org.bukkit.Statistic.TARGET_HIT, org.bukkit.Statistic.TRADED_WITH_VILLAGER}),

  /**
   * Container interactions.
   */
  CONTAINERS("Containers", new org.bukkit.Statistic[]{
      org.bukkit.Statistic.CHEST_OPENED, org.bukkit.Statistic.DISPENSER_INSPECTED, org.bukkit.Statistic.DROPPER_INSPECTED,
      org.bukkit.Statistic.ENDERCHEST_OPENED, org.bukkit.Statistic.HOPPER_INSPECTED, org.bukkit.Statistic.OPEN_BARREL,
      org.bukkit.Statistic.SHULKER_BOX_OPENED, org.bukkit.Statistic.TRAPPED_CHEST_TRIGGERED}),

  /**
   * Damage interactions.
   */
  DAMAGE("Damage", new org.bukkit.Statistic[]{
      org.bukkit.Statistic.DAMAGE_ABSORBED, org.bukkit.Statistic.DAMAGE_BLOCKED_BY_SHIELD, org.bukkit.Statistic.DAMAGE_DEALT,
      org.bukkit.Statistic.DAMAGE_DEALT_ABSORBED, org.bukkit.Statistic.DAMAGE_DEALT_RESISTED,
      org.bukkit.Statistic.DAMAGE_RESISTED, org.bukkit.Statistic.DAMAGE_TAKEN}),

  /**
   * General gameplay.
   */
  GENERAL("General", new org.bukkit.Statistic[]{
      org.bukkit.Statistic.DEATHS, org.bukkit.Statistic.LEAVE_GAME, org.bukkit.Statistic.PLAY_ONE_MINUTE,
      org.bukkit.Statistic.TIME_SINCE_DEATH, org.bukkit.Statistic.TIME_SINCE_REST, org.bukkit.Statistic.TOTAL_WORLD_TIME}),

  /**
   * Block interactions.
   */
  INTERACTIONS("Interactions", new org.bukkit.Statistic[]{
      org.bukkit.Statistic.BEACON_INTERACTION, org.bukkit.Statistic.BREWINGSTAND_INTERACTION,
      org.bukkit.Statistic.CRAFTING_TABLE_INTERACTION, org.bukkit.Statistic.FURNACE_INTERACTION,
      org.bukkit.Statistic.INTERACT_WITH_ANVIL, org.bukkit.Statistic.INTERACT_WITH_BLAST_FURNACE,
      org.bukkit.Statistic.INTERACT_WITH_CAMPFIRE, org.bukkit.Statistic.INTERACT_WITH_CARTOGRAPHY_TABLE,
      org.bukkit.Statistic.INTERACT_WITH_GRINDSTONE, org.bukkit.Statistic.INTERACT_WITH_LECTERN,
      org.bukkit.Statistic.INTERACT_WITH_LOOM, org.bukkit.Statistic.INTERACT_WITH_SMITHING_TABLE,
      org.bukkit.Statistic.INTERACT_WITH_SMOKER, org.bukkit.Statistic.INTERACT_WITH_STONECUTTER}),

  /**
   * Player movement.
   */
  MOVEMENT("Movement", new org.bukkit.Statistic[]{
      org.bukkit.Statistic.AVIATE_ONE_CM, org.bukkit.Statistic.BOAT_ONE_CM, org.bukkit.Statistic.CLIMB_ONE_CM,
      org.bukkit.Statistic.CROUCH_ONE_CM, org.bukkit.Statistic.FALL_ONE_CM, org.bukkit.Statistic.FLY_ONE_CM,
      org.bukkit.Statistic.HORSE_ONE_CM, org.bukkit.Statistic.JUMP, org.bukkit.Statistic.MINECART_ONE_CM,
      org.bukkit.Statistic.PIG_ONE_CM, org.bukkit.Statistic.SNEAK_TIME, org.bukkit.Statistic.SPRINT_ONE_CM,
      org.bukkit.Statistic.STRIDER_ONE_CM, org.bukkit.Statistic.SWIM_ONE_CM, org.bukkit.Statistic.WALK_ON_WATER_ONE_CM,
      org.bukkit.Statistic.WALK_ONE_CM, org.bukkit.Statistic.WALK_UNDER_WATER_ONE_CM});

  /**
   * Type's proper name.
   */
  private final String properName;

  /**
   * Type's statistics.
   */
  private final org.bukkit.Statistic[] statistics;

  /***
   * Associates the type with a proper name.
   * @param properName proper name
   * @param statistics statistics
   */
  StatisticType(String properName, org.bukkit.Statistic[] statistics) {
    this.properName = properName;
    this.statistics = statistics;
  }

  /**
   * Gets the type's proper name.
   *
   * @return type's proper name
   */
  @NotNull
  public String getProperName() {
    return this.properName;
  }

  /**
   * Gets the type's statistics.
   *
   * @return type's statistics
   */
  @NotNull
  public org.bukkit.Statistic[] getStatistics() {
    return this.statistics;
  }
}

