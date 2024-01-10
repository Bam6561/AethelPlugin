package me.dannynguyen.aethel;

import me.dannynguyen.aethel.data.*;

/**
 * AethelResources stores the plugin's resources in memory.
 *
 * @author Danny Nguyen
 * @version 1.4.13
 * @since 1.1.7
 */
public class AethelResources {
  public static final String resourceDirectory = "./plugins/Aethel";
  public static final String aethelItemDirectory = resourceDirectory + "/aitem";
  public static final String forgeRecipeDirectory = resourceDirectory + "/forge";

  public static final AethelItemData aethelItemData = new AethelItemData();
  public static final ForgeRecipeData forgeRecipeData = new ForgeRecipeData();
  public static final PlayerHeadData playerHeadData = new PlayerHeadData();
  public static final PlayerStatData playerStatData = new PlayerStatData();
  public static final ShowItemData showItemData = new ShowItemData();
}
