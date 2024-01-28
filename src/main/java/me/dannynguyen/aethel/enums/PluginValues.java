package me.dannynguyen.aethel.enums;

import me.dannynguyen.aethel.commands.playerstats.objects.PlayerStatsCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * PluginValues is a storage containing data value constants.
 *
 * @author Danny Nguyen
 * @version 1.7.13
 * @since 1.7.13
 */
public class PluginValues {
  public static final ArrayList<PlayerStatsCategory> PlayerStatsCategories = new ArrayList<>(List.of(
      new PlayerStatsCategory("Activities",
          new ArrayList<>(PluginList.PLAYERSTATS_STAT_CATEGORY_ACTIVITIES.list)),
      new PlayerStatsCategory("Containers",
          new ArrayList<>(PluginList.PLAYERSTATS_STAT_CATEGORY_CONTAINERS.list)),
      new PlayerStatsCategory("Damage",
          new ArrayList<>(PluginList.PLAYERSTATS_STAT_CATEGORY_DAMAGE.list)),
      new PlayerStatsCategory("General",
          new ArrayList<>(PluginList.PLAYERSTATS_STAT_CATEGORY_GENERAL.list)),
      new PlayerStatsCategory("Movement",
          new ArrayList<>(PluginList.PLAYERSTATS_STAT_CATEGORY_MOVEMENT.list)),
      new PlayerStatsCategory("Interactions",
          new ArrayList<>(PluginList.PLAYERSTATS_STAT_CATEGORY_INTERACTIONS.list))));
}
