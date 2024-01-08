package me.dannynguyen.aethel;

import me.dannynguyen.aethel.data.*;

/**
 * AethelResources represents the plugin's resources loaded in memory as an object.
 *
 * @author Danny Nguyen
 * @version 1.4.12
 * @since 1.1.7
 */
public class AethelResources {
  private final String resourceDirectory = "./plugins/Aethel";
  private final String forgeRecipeDirectory = resourceDirectory + "/forge";
  private final String aethelItemDirectory = resourceDirectory + "/aitem";
  private final ForgeRecipeData forgeRecipeData = new ForgeRecipeData();
  private final AethelItemData aethelItemData = new AethelItemData();
  private final PlayerHeadData playerHeadData = new PlayerHeadData();
  private final ShowItemData showItemData = new ShowItemData();
  private final PlayerStatData playerStatData = new PlayerStatData();

  public String getResourceDirectory() {
    return this.resourceDirectory;
  }

  public String getForgeRecipeDirectory() {
    return this.forgeRecipeDirectory;
  }

  public String getAethelItemDirectory() {
    return this.aethelItemDirectory;
  }

  public ForgeRecipeData getForgeRecipeData() {
    return this.forgeRecipeData;
  }

  public AethelItemData getAethelItemData() {
    return this.aethelItemData;
  }

  public PlayerHeadData getPlayerHeadData() {
    return this.playerHeadData;
  }

  public ShowItemData getShowItemData() {
    return this.showItemData;
  }

  public PlayerStatData getPlayerStatData() {
    return this.playerStatData;
  }
}
