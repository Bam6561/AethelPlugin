package me.dannynguyen.aethel;

import me.dannynguyen.aethel.commands.aethelItems.AethelItemsData;
import me.dannynguyen.aethel.data.*;

/**
 * PluginData stores the plugin's resources in memory.
 *
 * @author Danny Nguyen
 * @version 1.6.7
 * @since 1.1.7
 */
public class PluginData {
  public static final String resourceDirectory = "./plugins/Aethel";
  public static final String aethelItemsDirectory = resourceDirectory + "/aitem";
  public static final String forgeRecipesDirectory = resourceDirectory + "/forge";

  public static final AethelItemsData aethelItemsData = new AethelItemsData();
  public static final ItemEditorData itemEditorData = new ItemEditorData();
  public static final ForgeRecipeData forgeRecipeData = new ForgeRecipeData();
  public static final LoadedPlayerHeadData loadedPlayerHeadData = new LoadedPlayerHeadData();
  public static final PlayerStatsData playerStatsData = new PlayerStatsData();
  public static final ShowItemData showItemData = new ShowItemData();
}
